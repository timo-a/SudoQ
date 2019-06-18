package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.sudoq.model.actionTree.SolveAction;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.LastDigitHelper;
import de.sudoq.model.solverGenerator.solver.helper.LeftoverNoteHelper;
import de.sudoq.model.solverGenerator.solver.helper.LockedCandandidatesHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper;
import de.sudoq.model.solverGenerator.solver.helper.XWingHelper;
import de.sudoq.model.solvingAssistant.HintTypes;
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
        helper = makeHelperList();
		numberOfHelpers = helper.size();
	}

	private List<SolveHelper> makeHelperList(){
		// Initialize the helpers
		List<SolveHelper> helpers = new ArrayList<SolveHelper>();

		helpers.add(new LastDigitHelper(this.sudoku, 1)); //only one field in
		helpers.add(new LeftoverNoteHelper(this.sudoku, 1));

		//add subset helpers
		//naked and hidden sets complement eaxch other https://www.sadmansoftware.com/sudoku/hiddensubset.php
		//if a naked set $n$ exists with size $|n|$ then there exists a hidden set $h$ with size $|h| = |empty fields in constraint| - |n|$
		//the maximum number of empty fields is #Symbols -> if we look at naked sets up to $a$ we only need to look for hidden sets up to #Symbols - a -1
		// => we don't need to add all possible helpers:
		int numberOfNakedHelpers = ( sudoku.getSudokuType().getNumberOfSymbols()) / 2;     //half if #symbols is even, less than half otherwise
		int numberOfHiddenHelpers= sudoku.getSudokuType().getNumberOfSymbols() - numberOfNakedHelpers - 1 ; //we don't need the complement -> one less

		for (int i = 2; i <= numberOfNakedHelpers; i++) {//no naked single at this point, they're hardcoded later in the program
			helpers.add(new NakedHelper(this.sudoku, i, i*10));
		}
		for (int i = 1; i <= numberOfHiddenHelpers; i++) {
			helpers.add(new HiddenHelper(this.sudoku, i, i*10));
		}

		helpers.add(new LockedCandandidatesHelper(this.sudoku, 70));
		helpers.add(new XWingHelper(this.sudoku, 80));

		helpers.add(new Backtracking(this.sudoku, 100));
        return helpers;
	}

	/**
	 * Returns the sudoku this solver is working on.
	 * The returned object not identical to the object passed as parameter to the constructor!
	 * A solverSudoku is returned, containing the original parameter-sudoku.
	 * 
	 * @return Das Sudoku-Objekt, auf dem der Solver arbeitet
	 */
	public SolverSudoku getSolverSudoku() {
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

			if (isFilledCompletely())	return null; // if every field is already filled, no solution can be found, because that already happened

			if (isInvalid()) {
				if (!this.sudoku.hasBranch()) {	// if sudoku is invalid && has no branches we're in a dead end
					isIncorrect = true;
				} else {
					this.sudoku.killCurrentBranch(); //if there is a branch, make a backstep
					didUpdate = true;
				}
			}

			/* try to solve fields where only one note is remaining TODO why not just make a naked single?!(efficiency?)*/
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
		//System.out.println("start of solveAll2");
		//print9x9(sudoku);

		PositionMap<Integer> copy = new PositionMap<>(this.sudoku.getSudokuType().getSize());
		for (Position p : this.sudoku.positions) {
			copy.put(p, this.sudoku.getField(p).getCurrentValue());
		}

		boolean solved = solveAll(buildDerivation, false, false);

		System.out.println("solved: "+solved);
		//print9x9(sudoku);

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

	public Map<HintTypes, Integer> getHintCounts(){
		if (this.lastSolutions == null)
			throw new IllegalStateException("lastsolutions is null -> no counts can be generated");

		HintTypes[] values = HintTypes.values();
		int[] hist = new int[values.length];

		for (Solution s: this.lastSolutions)
			for (SolveDerivation sd :s.getDerivations())
				hist[sd.getType().ordinal()]++;

		EnumMap<HintTypes, Integer> hm = new EnumMap<HintTypes, Integer>(HintTypes.class);

		for (int i = 0; i < hist.length; i++)
			if (hist[i] > 0)
				hm.put(values[i], hist[i]);


		return hm;

	}

	public String getHintCountString(){
		Map<HintTypes, Integer> em = getHintCounts();
		String counts = "";
		for (HintTypes h : em.keySet())
			counts += "  " + em.get(h) + ' ' + h;

		///calc  score based on hints  alone
		counts += " " + getHintScore();
		///would

		return counts.length() > 2 ? counts.substring(2)
				                   : counts;


	}

	private static Map<HintTypes, Integer> hintscores = new HashMap<>();

	private int getHintScore(){
		Map<HintTypes, Integer> em = getHintCounts();
		int checkscore = 0;
		for (HintTypes h : em.keySet())
			checkscore += em.get(h) * getHintScore(h);
		return checkscore;
	}

	private int getHintScore(HintTypes h){
		if(h == HintTypes.NakedSingle)
			return 10;
		else {
			if(hintscores.isEmpty())
				for (SolveHelper sh : helper)
					hintscores.put(sh.getHintType(), sh.getComplexityScore());
			return hintscores.get(h);
		}
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
     *
	 * @return Ein ComplexityRelation-Objekt, welches die Constraint-gemäße Lösbarkeit beschreibt
	 */
	public ComplexityRelation validate(PositionMap<Integer> solution) {
		ComplexityRelation result = ComplexityRelation.INVALID;

		boolean solved  = false;
		boolean ambiguous = false;

		//map position -> value
		PositionMap<Integer> copy = new PositionMap<Integer>(this.sudoku.getSudokuType().getSize());
		for (Position p : this.sudoku.positions) {
			copy.put(p, this.sudoku.getField(p).getCurrentValue());
		}

		//if a solution is found according to the complexity constraints
		if (solveAll(true, true, false)) {
			solved = true;
			// store the correct solution
			if (solution != null) {
				for (Position p: this.sudoku.positions) {
					int curVal = this.sudoku.getField(p).getCurrentValue();
					solution.put(p, curVal);

				}
			}
		}

		if (solved && severalSolutionsExist()) //TODO maybe try to fix by adding fields and only then return invalid?
			ambiguous = true;

		// restore initial state
		for(Position p : this.sudoku.positions)
			this.sudoku.getField(p).setCurrentValue(copy.get(p),false);


		// depending on the result, return an int
		int complexity = this.sudoku.getComplexityValue();
		int minComplextiy = complConstr.getMinComplexityIdentifier();
		int maxComplextiy = complConstr.getMaxComplexityIdentifier();

		if (ambiguous)
			result = ComplexityRelation.AMBIGUOUS;
		else if (solved) {

			if      (maxComplextiy * 1.2 < complexity                                      ) result = ComplexityRelation.MUCH_TOO_DIFFICULT;
			else if (maxComplextiy       < complexity && complexity <= maxComplextiy * 1.2 ) result = ComplexityRelation.TOO_DIFFICULT;
			else if (minComplextiy       < complexity && complexity <= maxComplextiy       ) result = ComplexityRelation.CONSTRAINT_SATURATION;
			else if (minComplextiy * 0.8 < complexity && complexity <= minComplextiy       ) result = ComplexityRelation.TOO_EASY;
			else if (                                    complexity <= minComplextiy * 0.8 ) result = ComplexityRelation.MUCH_TOO_EASY;
			/*   0.8 minC      minC               maxC            1.2 maxC
		    much too easy| too easy|   saturation     |too difficult      | Much too difficult         */
		}

		// System.out.println(sudoku.getComplexityValue() + "(" + sudoku.getComplexityScore() + ") " + result);

		return result;
	}

	/**
	 * Indicates whether further solutions exist for a sudoku where we've already found one.
	 * (potentially) modifies sudoku.
	 *
	 * @return
	 */
	private boolean severalSolutionsExist(){
        //lastsolutions might be set to null, e.g. if we kill branch but dont find another solution
		//if we want to call getSolutions later on we'll be interested in the first solution anyway
		List<Solution> ls = lastSolutions;
		while (this.sudoku.hasBranch()) {
			this.sudoku.killCurrentBranch();//NB killing a branch is like a clockwork, we can't go back the same branches, bec we eliminate the candidate we chose last time
			if (solveAll(false, false, true))//why is it invalid if solved and another solve?
				return true;
		}
		lastSolutions = ls;
		return false;


	}

	/**
	 * Solves the Löst das gesamte spezifizierte Sudoku. Die Lösung wird als Liste von Solution-Objekten zurückgeliefert, deren
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
	 * @return true if sudoku can be solved
	 *         false otherwise
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls followComplexityConstraints true ist, jedoch keine Constraint-Definition für den
	 *             Sudokutyp und die Schwierigkeit vorhanden ist
	 */
	public boolean solveAll(boolean buildDerivation, boolean followComplexityConstraints, boolean validation) {
		if (!validation)
			this.sudoku.resetCandidates();

		try {
			if (followComplexityConstraints) {
				//if complexity is relevant restrict helpers
				complConstr = this.sudoku.getSudokuType().buildComplexityConstraint(this.sudoku.getComplexity());
				numberOfHelpers = complConstr.getNumberOfAllowedHelpers(); //TODO specifying a max helper would be clearer
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
		boolean isUnsolvable = false;

		if (buildDerivation) {
			lastSolutions = new ArrayList<Solution>();
			lastSolutions.add(new Solution());
			branchPoints = new Stack<Integer>();
		}
        int solver_counter = 0;
		while (!solved           //if `solved` we're done
				&& didUpdate     //if we didn't do an update in the last iteration, we won't do one the next iteration either
				&& !isUnsolvable) { //if we found out there is no solution, no need to try further
			didUpdate = false;

			////////////////////////
			if (solver_counter == 25) {
				System.out.println("Breakpoint");
			}


			// try to solve the sudoku
			solved = isFilledCompletely();

			if (!solved && isInvalid()) {
				/*
				 * if sudoku is invalid, has no branches and no solution was found,
				 * it is invalid if there was already a solution
				 * there is no further one, so it is solved correct if there is a branch, make a backstep
				 */
				if (!this.sudoku.hasBranch()) {
					isUnsolvable = true;
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
			if (!solved && !didUpdate && !isUnsolvable) {
				if (updateNakedSingles(buildDerivation, !validation)) {
					didUpdate = true;
				}
			}

			if(useHelper(solved, didUpdate, isUnsolvable, buildDerivation, validation)) {
				didUpdate = true;
			}

			////////////////////////
			if (this.lastSolutions != null){
				if (this.lastSolutions.size() >= 13 ||
					sudoku.getComplexityValue() != getHintScore())
					System.out.println(getHintCountString() +
							" " + sudoku.getComplexityValue() +
							" sc: " + solver_counter);
			}

			solver_counter++;
			////////////////////////

			// UNCOMMENT THE FOLLOWING TO PRINT THE WHOLE SUDOKU AFTER EACH LOOP
			//if(sudoku.getSudokuType().getEnumType() == SudokuTypes.samurai){
				//print9x9(sudoku);
			//}
		}

		if (!solved) {
			lastSolutions = null;
		} else if (buildDerivation) {
			lastSolutions.remove(lastSolutions.size() - 1); //TODO why remove last element???
		}

		// depending on the result, return an int
		return solved;
	}

	/**
	*  According to their priority use the helpers until one of them can
	*  be applied.
	*
	*  @returns true if any helper could be applied, false if no helper could be applied
	*/
	private boolean useHelper(boolean solved, boolean didUpdate, boolean isUnsolvable, boolean buildDerivation, boolean validation){
		if (!solved && !didUpdate && !isUnsolvable) {
			for (int i = 0; i < numberOfHelpers; i++) {
				SolveHelper hel = helper.get(i);

				//if a helper can be applied
				if (hel.update(buildDerivation)) {

					if (!validation)
						this.sudoku.addComplexityValue(hel.getComplexityScore(), !(hel instanceof Backtracking));
					if (buildDerivation) {
						lastSolutions.get(lastSolutions.size() - 1).addDerivation(hel.getDerivation());
						if (hel instanceof Backtracking) {
							branchPoints.push(lastSolutions.size());
							lastSolutions.add(new Solution());
							//System.out.println("Backtracking!");
						}

					}
					return true;
				}
			}
		}
		return false;

	}

	public static void print9x9(Sudoku sudoku){

		System.out.println(sudoku);
	}

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
		boolean hasNakedSingle;   //indicates that one was found in the last iteration -> continue to iterate
		boolean foundNakedSingle = false; //indicates that at least one was found
		// Iterate trough the fields to look if each field has only one
		// candidate left = solved
		do {
			hasNakedSingle = false;
			for (Position p : this.sudoku.positions) {
				BitSet b = this.sudoku.getCurrentCandidates(p);
				if (b.cardinality() == 1) {
					if (addDerivations) {
						Solution sol = lastSolutions.get(lastSolutions.size() - 1);
						SolveDerivation deriv = new SolveDerivation(HintTypes.NakedSingle);
						deriv.addDerivationField(new DerivationField(p, (BitSet) b.clone(),
						                                             new BitSet()));
						deriv.setDescription("debug: naked single via Solver.updateNakedSingles");
						SolveAction action = (SolveAction) new SolveActionFactory().createAction(b.nextSetBit(0),
								this.sudoku.getField(p));
						sol.setAction(action);
						sol.addDerivation(deriv);
						//lastSolutions.add(new Solution());
					}
					sudoku.setSolution(p, b.nextSetBit(0));
					if (addComplexity) {
						this.sudoku.addComplexityValue(10, true);
					}
					hasNakedSingle = true;
					foundNakedSingle = true;
				}
			}
		} while(hasNakedSingle);

		if(foundNakedSingle && addDerivations)
			lastSolutions.add(new Solution());

		return foundNakedSingle;
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
	 * Überprüft, ob im Sudoku in jedem Feld eine Lösung eingetragen ist.
	 * Keine Überprüfung auf Richtigkeit.
	 *
	 * @return true, falls das Sudoku gelöst ist, false andernfalls
	 */
	private boolean isFilledCompletely() {

		//return sudoku.positions.forall( p => !sudoku.getField(p).isNotSolved())
		//return sudoku.positions.map(sudoku.getField).forall(f=>!f.isNotSolved())
		//return !sudoku.positions.map(sudoku.getField).exists(f=>f.isNotSolved())

		for (Position p : this.sudoku.positions)
			if (this.sudoku.getField(p).isNotSolved())
				return false;

		return true;
	}
}