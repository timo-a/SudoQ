package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.actionTree.SolveAction;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

/**
 * Diese Klasse bietet Methoden zum Lösen eines Sudokus. Sowohl einzelne Felder, als auch gesamte Sudokus können gelöst
 * werden. Auch das Validieren eines Sudokus auf Lösbarkeit ist möglich.
 */
public class Solver {
	/** Attributes */

	/**
	 * Das Sudoku, welches von diesem Solver gelöst wird
	 */
	protected SolverSudoku sudoku;

	/**
	 * Eine Liste von SolveHelpern, welche zum Lösen des Sudokus genutzt werden
	 */
	private List<SolveHelper> helper;

	/**
	 * Indikator dafür, ob für das weitere Lösen Kandidatenlisten benötigt werden oder nicht
	 */
	private boolean candidatesNeeded;

	/**
	 * Die Anzahl der verfügbaren Helfer;
	 */
	private int numberOfHelpers;

	/**
	 * Eine Liste der Lösungen des letzten solveAll-Aufrufes
	 */
	private List<Solution> lastSolutions;

	/**
	 * Ein Stack von Branch-Punkten, damit bei einem Backtrack die Einträge aus der Herleitung gelöscht werden.
	 */
	private Stack<Integer> branchPoints;

	/**
	 * Das ComplexityConstraint für die Schwierigkeit des Sudokus.
	 */
	private ComplexityConstraint complConstr;

	/** Constructors */

	/**
	 * Creates a new solver for {@code sudoku}.
	 * If the argument is null, a IllegalArgumentException is thrown.
	 * All methods of this class refer to this sudoku object.
	 * 
	 * @param sudoku
	 *            Sudoku to be solved by this solver
	 * @throws IllegalArgumentException
	 *             if {@code sudoku == null}
	 */
	public Solver(Sudoku sudoku) {
		if (sudoku == null)
			throw new IllegalArgumentException("sudoku was null");
		this.sudoku = new SolverSudoku(sudoku);
		this.complConstr = sudoku.getSudokuType().buildComplexityConstraint(sudoku.getComplexity());

		// Initialize the helpers
		helper = new ArrayList<SolveHelper>();

		helper.add(new HiddenHelper(this.sudoku, 1, 21));
		helper.add(new NakedHelper(this.sudoku, 2, 32));
		helper.add(new NakedHelper(this.sudoku, 3, 40));
		helper.add(new HiddenHelper(this.sudoku, 2, 42));
		helper.add(new NakedHelper(this.sudoku, 4, 48));
		helper.add(new HiddenHelper(this.sudoku, 3, 48));
		helper.add(new NakedHelper(this.sudoku, 5, 49));
		helper.add(new HiddenHelper(this.sudoku, 4, 54));
		// helper.add(new HiddenHelper(this.sudoku, 5, 57));
//TODO add locked, wing
		helper.add(new Backtracking(this.sudoku, 70));
		numberOfHelpers = helper.size();
	}

	/**
	 * Returns the sudoku this solver is working on.
	 * The returned object not identical to the object passed as parameter to the constructor!
	 * A solverSudoku is returned, containing the original parameter-sudoku.
	 * 
	 * @return Das Sudoku-Objekt, auf dem der Solver arbeitet
	 */
	public Sudoku getSudoku() {
		return this.sudoku;
	}

	/** Methods */

	/**
	 * Ermittelt die Lösung für ein Feld, sowie dessen Herleitung. Die Herleitung wird als Solution-Objekt
	 * zurückgegeben. Mit applySolution kann spezifiziert werden, dass die Lösung direkt in das Feld eingetragen werden
	 * soll. Kann keine Lösung ermittelt werden, z.B. weil ein Feld falsch gelöst ist, so wird null zurückgegeben.
	 * Hinweis: Ist ein Feld bereits inkorrekt gelöst, so kann der Aufruf dieser Methode dazu führen, dass ein weiteres
	 * Feld falsch gelöst wird.
	 * 
	 * @param applySolution
	 *            Gibt an, ob die Lösung direkt in das Feld eingetragen werden soll oder nicht
	 * @return Ein Solution-Objekt, welches Schritte zur Herleitung der Lösung eines Feldes enthält bzw null, falls
	 *         keine Lösung gefunden wurde
	 */
	public Solution solveOne(boolean applySolution) {
		this.sudoku.resetCandidates();

		// Look for constraint saturation at the beginning * if(this.sudoku.getSudokuType().exists((x => !x.isSaturated(this.sudoku)))
		for (Constraint con : this.sudoku.getSudokuType()) {     // return null
			if (!con.isSaturated(this.sudoku))
				return null;
		}

		Solution solution = new Solution();

		boolean solvedField = false;
		boolean didUpdate = true;
		boolean isIncorrect = false;

		//loop until we get the solution for a field TODO what if a helper solves a field directly? solvedField would never be true right? I DONT THINK WE ARE SUPPOSED TO 'SOLVE' IN HELPERS, JUST FIND
		while (!solvedField && didUpdate && !isIncorrect) {
			didUpdate = false;

			if (isSolved())	return null; // if every field is already filled, no solution can be found, because that already happened

			if (isInvalid()) {
				if (!this.sudoku.hasBranch()) {	// if sudoku is invalid && has no branches we're in a dead end
					isIncorrect = true;
				} else {
					this.sudoku.killCurrentBranch(); //if there is a branch, make a backstep
					didUpdate = true;
				}
			}

			/* try to solve fields where only one note is remaining TODO why not just make a naked single?!*/
			for (Position p : this.sudoku.positions) {
				BitSet b = this.sudoku.getCurrentCandidates(p);
				if (b.cardinality() == 1) { //we found a field where only one note remains
					if (!this.sudoku.hasBranch()) {
						//if there are no branches create solution-object
						solution.setAction(new SolveActionFactory().createAction(b.nextSetBit(0), this.sudoku.getField(p)));

						SolveDerivation deriv = new SolveDerivation();
						deriv.addDerivationField(new DerivationField(p, (BitSet) b.clone(), new BitSet())); //since only one bit set, complement is an empty set
						solution.addDerivation(deriv);
						solvedField = true;
					} else {
						this.sudoku.setSolution(p, b.nextSetBit(0)); //set solution that can be removed again (in case it's the wrong branch)
						didUpdate = true;
					}
				}
			}

			// According to their priority use the helpers until one of them can
			// be applied
			if (!solvedField && !didUpdate && !isIncorrect)
				for (SolveHelper hel : helper)
					if (hel.update(true)) {
						solution.addDerivation(hel.getDerivation());//we don't check whether branches exist here?!
						didUpdate = true;
						break;
					}
		}

		// Apply solution if wanted
		if (!isIncorrect && solvedField && applySolution)
			solution.getAction().execute();

		return (!isIncorrect && solvedField) ? solution : null;
	}

	/**
	 * Löst das gesamte Sudoku, sofern keine Felder fehlerhaft gelöst sind. Ist buildDerivation true, so wird die
	 * Herleitung der Lösung erstellt und kann durch die getDerivation Methode abgerufen werden. Ist applySolutions
	 * true, so werden die Lösungen direkt in das Sudoku eingetragen, ist es false, so müssen die Lösungen aus der
	 * Herleitung selbst ausgeführt werden.
	 * 
	 * @param buildDerivation
	 *            Gibt an, ob eine Herleitung der Lösung erstellt werden soll
	 * @param applySolutions
	 *            Gibt an, ob die Lösungen direkt in das Sudoku eingetragen werden sollen
	 * @return true, falls das Sudoku gelöst werden konnte, false falls nicht
	 */
	public boolean solveAll(boolean buildDerivation, boolean applySolutions) {
		System.out.println("start of solveAll2");
		print9x9(sudoku);

		PositionMap<Integer> copy = new PositionMap<>(this.sudoku.getSudokuType().getSize());
		for (Position p : this.sudoku.positions) {
			copy.put(p, this.sudoku.getField(p).getCurrentValue());
		}

		boolean solved = solveAll(buildDerivation, false, false);

		System.out.println("solved: "+solved);
		print9x9(sudoku);

		// Restore old state if solutions shall not be applied or if sudoku could not be solved
		if (!applySolutions || !solved) {
			for (Position p : this.sudoku.positions) {
				this.sudoku.getField(p).setCurrentValue(copy.get(p), false);
			}
		}


		return solved;
	}

	/**
	 * Gibt eine Liste von Lösungherleitungen zurück, die durch den letzten Aufruf der solveAll-Methode erzeigt wurde.
	 * Wurde die solveAll-Methode noch nicht bzw. ohne den Parameter buildSolution aufgerufen, so wird null
	 * zurückgegeben.
	 * 
	 * @return Eine Liste der Herleitungen für den letzten Aufruf der solveAll-Methode, oder null, falls dieser Methode
	 *         noch nicht oder ohne den Parameter buildSolution aufgerufen wurde
	 */
	public List<Solution> getSolutions() {
		return this.lastSolutions;
	}

	/**
	 * Überprüft das gegebene Sudoku auf Validität entpsrechend dem spezifizierten ComplexityConstraint. Es wird
	 * versucht das Sudoku mithilfe der im ComplexityConstraint für die im Sudoku definierte Schwierigkeit definierten
	 * SolveHelper und Anzahl an Schritten versucht zu lösen. Das Ergbnis wird durch ein ComplexityRelation Objekt
	 * zurückgegeben. Wird eine PositionsMap übergeben, kann über den Parameter {@code in} spezifiziert werden, ob die
	 * eine Eingabe ist und dazu genutzt werden soll, die Lösung anzugeben und zu validieren oder ob das Objekt mit der
	 * Korrekten Lösung befüllt werden soll.
	 * 
	 * @param solution
	 *            In diese Map wird die ermittelte Lösung geschrieben
	 * @param in
	 *            if false, {@code solution} is filled otherwise not TODO further investigation
	 *            Spezifiziert, ob die solution Ein- oder Ausgabe ist
	 * @return Ein ComplexityRelation-Objekt, welches die Constraint-gemäße Lösbarkeit beschreibt
	 */
	public ComplexityRelation validate(PositionMap<Integer> solution, boolean in) {
		ComplexityRelation result = ComplexityRelation.INVALID;

		boolean solved = false;
		boolean invalid = false;
		PositionMap<Integer> copy = new PositionMap<Integer>(this.sudoku.getSudokuType().getSize());
		for (Position p : this.sudoku.positions) {
			copy.put(p, this.sudoku.getField(p).getCurrentValue());
		}

		if (solveAll(false, true, false)) {
			solved = true;
			// store the correct solution
			if (solution != null) {
				for (Position p: this.sudoku.positions) {
					int curVal = this.sudoku.getField(p).getCurrentValue();
					if (!in) {
						solution.put(p, curVal);
					} else if (in && solution.get(p) != curVal) {
						solved = false;
					}
				}
			}
		}

		if (this.sudoku.hasBranch()) {
			this.sudoku.killCurrentBranch();
			if (solved && solveAll(false, false, true))//why is it invalid if solved and another solve?
				invalid = true;
		}

		// restore initial state
		for(Position p : this.sudoku.positions)
			this.sudoku.getField(p).setCurrentValue(copy.get(p),false);


		// depending on the result, return an int
		int complexity = this.sudoku.getComplexityValue();
		int minComplextiy = complConstr.getMinComplexityIdentifier();
		int maxComplextiy = complConstr.getMaxComplexityIdentifier();

		if (!invalid && solved) {

			if      (maxComplextiy * 1.2 < complexity                                      ) result = ComplexityRelation.MUCH_TO_DIFFICULT;
			else if (maxComplextiy       < complexity && complexity <= maxComplextiy * 1.2 ) result = ComplexityRelation.TO_DIFFICULT;
			else if (minComplextiy       < complexity && complexity <= maxComplextiy       ) result = ComplexityRelation.CONSTRAINT_SATURATION;
			else if (minComplextiy * 0.8 < complexity && complexity <= minComplextiy       ) result = ComplexityRelation.TO_EASY;
			else if (                                    complexity <= minComplextiy * 0.8 ) result = ComplexityRelation.MUCH_TO_EASY;
			
				
		}

		// System.out.println(sudoku.getComplexityValue() + "(" + sudoku.getComplexity() + ") " + result);

		return result;
	}

	/**
	 * Löst das gesamte spezifizierte Sudoku. Die Lösung wird als Liste von Solution-Objekten zurückgeliefert, deren
	 * Reihenfolge die Reihenfolge der Lösungsschritte des Algorithmus, realisiert durch die SolveHelper, repräsentiert.
	 * Ist das Sudoku invalid und kann somit nicht eindeutig gelöst werden, so wird null zurückgegeben.
	 * 
	 * @param buildDerivation
	 *            Bestimmt, ob die Herleitung der Lösung oder lediglich eine leere Liste zurückgegeben werden soll
	 * @param followComplexityConstraints
	 *            Bestimmt, ob zum Lösen die Constraints der Komplexität des Sudokus befolgt werden müssen
	 * @param validation
	 *            Besagt, dass dieser Lösungsversuch zum Validieren gedacht ist und daher die Kandidatenlisten nicht
	 *            zurückgesetzt werden sollen und ob die Schwierigkeit jedes Lösungsschrittes zum Sudoku hinzugefügt
	 *            werden soll
	 * @return Eine Liste von Solution-Objekten, welche die Lösung und Herleitung für jedes Feld enthält oder null,
	 *         falls kein Feld eindeutig gelöst werden kann
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls followComplexityConstraints true ist, jedoch keine Constraint-Definition für den
	 *             Sudokutyp und die Schwierigkeit vorhanden ist
	 */
	private boolean solveAll(boolean buildDerivation, boolean followComplexityConstraints, boolean validation) {
		if (!validation)
			this.sudoku.resetCandidates();
		candidatesNeeded = false;

		try {
			if (followComplexityConstraints) {
				complConstr = this.sudoku.getSudokuType().buildComplexityConstraint(this.sudoku.getComplexity());
				numberOfHelpers = complConstr.getNumberOfAllowedHelpers();
			} else {
				numberOfHelpers = this.helper.size();
			}
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Invalid sudoku complexity to follow constraints");
		}

		// Look for constraint saturation at the beginning
		if (!this.sudoku.getSudokuType().checkSudoku(this.sudoku)) {
			return false;
		}

		boolean solved = false;
		boolean didUpdate = true;
		boolean isIncorrect = false;

		if (buildDerivation) {
			lastSolutions = new ArrayList<Solution>();
			lastSolutions.add(new Solution());
			branchPoints = new Stack<Integer>();
		}

		while (!solved && didUpdate && !isIncorrect) {
			didUpdate = false;

			// try to solve the sudoku
			solved = isSolved();

			if (!solved && isInvalid()) {
				/*
				 * if sudoku is invalid, has no branches and no solution was found,
				 * it is invalid if there was already a solution
				 * there is no further one, so it is solved correct if there is a branch, make a backstep
				 */
				if (!this.sudoku.hasBranch()) {
					isIncorrect = true;
				} else {
					if (buildDerivation) {
						while (lastSolutions.size() > branchPoints.peek()) {
							lastSolutions.remove(lastSolutions.size() - 1);
						}
						branchPoints.pop();
					}
					this.sudoku.killCurrentBranch();
					didUpdate = true;
				}
			}

			// try to update naked singles
			if (!solved && !didUpdate && !isIncorrect) {
				if (updateNakedSingles(buildDerivation, !validation)) {
					didUpdate = true;
				}
			}

			// According to their priority use the helpers until one of them can
			// be applied
			if (!solved && !didUpdate && !isIncorrect) {
				for (int i = 0; i < numberOfHelpers; i++) {
					if (i == 2)
						candidatesNeeded = true;
					SolveHelper hel = helper.get(i);
					if (hel.update(buildDerivation)) {
						if (!validation)
							this.sudoku.addComplexityValue(hel.getComplexity(), !(hel instanceof Backtracking));
						if (this.candidatesNeeded)
							this.sudoku.addComplexityValue(30, !(hel instanceof Backtracking));
						if (buildDerivation) {
							if (hel instanceof Backtracking) {
								branchPoints.push(lastSolutions.size());
							}
							lastSolutions.get(lastSolutions.size() - 1).addDerivation(hel.getDerivation());
						}
						didUpdate = true;
						break;
					}
				}
			}

			// UNCOMMENT THE FOLLOWING TO PRINT THE WHOLE SUDOKU AFTER EACH LOOP
			//if(sudoku.getSudokuType().getEnumType() == SudokuTypes.samurai){
				//print9x9(sudoku);
			//}
		}

		if (!solved) {
			lastSolutions = null;
		} else if (buildDerivation) {
			lastSolutions.remove(lastSolutions.size() - 1);
		}

		// depending on the result, return an int
		return solved;
	}

	public static void print9x9(Sudoku sudoku){

		System.out.println(sudoku);
	}

	boolean failed = false; //TODO that cant be right...

	/**
	 * Sucht nach NakedSingles und trägt diese daraufhin als Lösung für das jeweilige Feld ein. Gibt zurück, ob ein
	 * NakedSingle gefunden wurde.
	 * 
	 * @param addDerivations
	 *            Bestimmt, ob die Herleitung der Lösungen zurückgegeben oder lediglich eine leere Liste zurückgegeben
	 *            werden soll
	 * @param addComplexity
	 *            Bestimmt, ob der Schwierigkeitswert beim Finden eines NakedSingles dem Sudoku hinzugefügt werden soll
	 * @return Eine Liste der Herleitungen der Lösungen oder null, falls keine gefunden wurde
	 */
	private boolean updateNakedSingles(boolean addDerivations, boolean addComplexity) {
		boolean hasNakedSingle = false;
		failed = false;
		// Iterate trough the fields to look if each field has only one
		// candidate left = solved
		for (Position p : this.sudoku.positions) {
			BitSet b = this.sudoku.getCurrentCandidates(p);
			if (b.cardinality() == 1) {
				if (addDerivations) {
					Solution sol = lastSolutions.get(lastSolutions.size() - 1);
					SolveDerivation deriv = new SolveDerivation();
					deriv.addDerivationField(new DerivationField(p, (BitSet) b.clone(),
							new BitSet()));
					SolveAction action = (SolveAction) new SolveActionFactory().createAction(b.nextSetBit(0),
							this.sudoku.getField(p));
					sol.setAction(action);
					sol.addDerivation(deriv);
					lastSolutions.add(new Solution());
				}
				sudoku.setSolution(p, b.nextSetBit(0));
				if (addComplexity) {
					this.sudoku.addComplexityValue(18, true);
					if (this.candidatesNeeded)
						this.sudoku.addComplexityValue(30, true);
				}
				hasNakedSingle = true;
			}
		}

		return hasNakedSingle;
	}

	/**
	 * Überprüft, ob das Sudoku invalide ist, also ob es ein ungelöstes Feld gibt, für das keine Kandidaten mehr
	 * vorhanden sind.
	 * 
	 * @return true, falls das Sudoku aktuell invalide ist, false falls nicht
	 */
	private boolean isInvalid() {
		for (Position p : this.sudoku.positions)
		    /* look for no solution entered && no candidates left */
			if (this.sudoku.getCurrentCandidates(p).isEmpty() && this.sudoku.getField(p).isNotSolved() )
				return true;

		return false;
	}

	/**
	 * Überprüft, ob das Sudoku gelöst ist. Dies ist der Fall, wenn für jedes Feld eine Lösung eingetragen worden ist
	 * und alle Constraints des Sudokus erfüllt sind. TODO zzt wird nur überprüft ob voll...
	 * 
	 * @return true, falls das Sudoku gelöst ist, false andernfalls
	 */
	private boolean isSolved() {

		//return sudoku.positions.forall( p => !sudoku.getField(p).isNotSolved())
		//return sudoku.positions.map(sudoku.getField).forall(f=>!f.isNotSolved())
		//return !sudoku.positions.map(sudoku.getField).exists(f=>f.isNotSolved())

		for (Position p : this.sudoku.positions)
			if (this.sudoku.getField(p).isNotSolved())
				return false;

		return true;
	}
}