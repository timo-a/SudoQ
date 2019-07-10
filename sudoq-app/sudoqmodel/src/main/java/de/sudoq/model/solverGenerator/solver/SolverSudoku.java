package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.solver.BranchingPool.Branching;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

/**
 * Eine für den Lösungsalgorithmus optimierte und erweiterte Sudoku Klasse
 */
public class SolverSudoku extends Sudoku {
	/** Attributes */

	/**
	 * Eine Liste aller Positionen dieses Sudokus
	 */
	List<Position> positions;

	/**
	 * Mappt die Positionen auf eine Liste von Constraints, zu welchen diese Position gehört
	 */
	PositionMap<ArrayList<Constraint>> constraints;

	/**
	 * Mappt die Positionen auf ein BitSet, welches die Kandidaten für dieses Feld repräsentiert nach jedem
	 * Branching-Schritt repräsentiert
	 */
	private PositionMap<CandidateSet> currentCandidates;

	/**
	 * Speichert die Positionen an denen gebrancht wurde. (implizit in den einzelnen branchings )
	 * TODO warum nehmen wir nicht branchPool.usedBranchings?
	 */
	Stack<Branching> branchings;

	/**
	 * Der BranchingPool zum Verwalten der Branchings.
	 */
	private BranchingPool branchPool;

	/**
	 * Der PositionMapPool zum Verwalten der für das Branching benötigten PositionMaps.
	 */
	private PositionMapPool positionPool;

	/**
	 * Die Summe der Schwierigkeit aller auf diesem Sudoku ausgeführten Operationen zum Lösen
	 */
	private int complexityValue;

	private Map<Constraint, List<Constraint>> neighborDirectory; //better to have that static in the type

	public  enum Initialization {NEW_CANDIDATES, USE_EXISTING};

	/**
	 * Instanziiert ein neues SolverSudoku, welches sich auf das spezifizierte Sudoku bezieht.
	 * 
	 * @param sudoku
	 *            Das Sudoku das zu dem dieses SolverSudoku gehört
	 *            Parameter and created object will be different objects with indepentent values,
	 *            can be modified independently
	 */
	public SolverSudoku(Sudoku sudoku) {
		super(sudoku.getSudokuType());
		initializeSolverSudoku(sudoku, Initialization.NEW_CANDIDATES);
	}

	/**
	 * Instanziiert ein neues SolverSudoku, welches sich auf das spezifizierte Sudoku bezieht.
	 *
	 * @param sudoku
	 *            Das Sudoku das zu dem dieses SolverSudoku gehört
	 *            Parameter and created object will be different objects with indepentent values,
	 *            can be modified independently
	 */
	public SolverSudoku(Sudoku sudoku, Initialization mode) {
		super(sudoku.getSudokuType());
		initializeSolverSudoku(sudoku, mode);
	}

	/* Initializes this object according to the passed sudoku
	 * the passed sudoku is neither modified nor stored
	 *
	 *
	 */
	public void initializeSolverSudoku(Sudoku sudoku, Initialization mode) {
		this.setComplexity(sudoku.getComplexity());//transfer complexity as well

		// initialize the list of positions
		//this.positions = new ArrayList<>(fields.keySet());
		this.positions = new ArrayList<Position>();//for debugging we need the same as once
		for (Position p : fields.keySet())
			this.positions.add(p);


		/** For debugging, we need predictable order */
		this.positions = Generator.getPositions(sudoku);//TODO remove again


		// initialize new SolverSudoku with the fields of the specified one
		for (Position p : this.positions)
			fields.put(p, (Field) sudoku.getField(p).clone());

		// initialize the constraints lists for each position and the initial
		// candidates for each field
		this.constraints = new PositionMap<>(this.getSudokuType().getSize());
		for (Position p: positions)
			this.constraints.put(p, new ArrayList<Constraint>());


		//if we were functional
		//this.constraints = new PositionMap<>(this.getSudokuType().getSize());
		//this.positions.stream().forEach(p -> this.constraints.put(p, new ArrayList<>()));

		// add the constraints each position belongs to to the list
		Iterable<Constraint> allConstraints = sudoku.getSudokuType();
		for (Constraint constr : allConstraints)
			for (Position pos : constr.getPositions())
				this.constraints.get(pos).add(constr);

		// initialize the candidates map
		this.positionPool = new PositionMapPool(getSudokuType().getSize(), positions);
		this.branchPool = new BranchingPool();
		this.currentCandidates = this.positionPool.getPositionMap();

		// initialize the candidate lists and branchings
		this.branchings = new Stack<>();

		switch(mode) {
			case NEW_CANDIDATES:
				resetCandidates();
				break;
			case USE_EXISTING:
				//solverSudoku's fields take the candidates/notes from sudoku
				for (Position p : positions)
					if(sudoku.getField(p).isNotSolved()) {
						for (int i : getSudokuType().getSymbolIterator())
							if (sudoku.getField(p).isNoteSet(i) != currentCandidates.get(p).get(i))
								currentCandidates.get(p).flip(i);
					}
		}


	}

	/**
	 * Setzt die Kandidatenlisten aller Felder zurück, sodass alle Kandidatenlisten komplett befüllt sind. "Komplett"
	 * wird anhand des größten Constraints in dem sich dieses Feld befindet bemessen. Anschließend werden die
	 * Kandidatenlisten bzgl. ConstraintSaturation upgedatet.
	 */
	public void resetCandidates() {
		this.complexityValue = 0;

		// delete the branchings
		this.branchPool.recycleAllBranchings();
		this.positionPool.returnAll();
		this.branchings.clear();

		this.currentCandidates = this.positionPool.getPositionMap();
		// set the candidate lists of all fields to maximum
		for (Position p: this.positions)
			if (fields.get(p).isNotSolved())
				this.currentCandidates.get(p).set(0, getSudokuType().getNumberOfSymbols());
			
		

		/*this.positions.stream()
						.filter(p -> fields.get(p).isNotSolved())
						.forEach(p -> this.currentCandidates.get(p).set(0, getSudokuType().getNumberOfSymbols()));
		functional*/
		updateCandidates();
	}

	/**
	 * Initialisiert einen neuen Zweig, indem der aktuelle Stand der Kandidatenlisten kopiert und auf den
	 * Branching-Stack gepusht wird. Der Branch wird an der spezifizierten Position vorgenommen. Dabei wird der
	 * spezifizierte Kandidat als temporäre Lösung für das übergebene Feld gesetzt.
	 * 
	 * @param pos
	 *            Die Position an der gebrancht werden soll
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die spezifizierte Position null oder nicht in dem Sudoku vorhanden ist
	 */
	public void startNewBranch(Position pos, int candidate) {
		if (pos == null || this.fields.get(pos) == null)
			throw new IllegalArgumentException("Position was null or does not exist in this sudoku.");

		// initialize a new branch and copy candidate lists of current branch
		Branching branch = this.branchPool.getBranching(pos, candidate);//create new branch
		branch.candidates = currentCandidates;                          //store current candidates there
		this.currentCandidates = this.positionPool.getPositionMap();    //current candidates in a new (empty) PositionMap

		for (Position p : this.positions)
			this.currentCandidates.get(p).or(branch.candidates.get(p)); //fill currentCandidates with candidates from before branching

		this.branchings.push(branch);//put branch (i.e. a backup of what we had before this method was called) on branchings (which seems to be identical to branchpool.branchesinactiveuse)

        //the candidate given as parameter is entered as a (user solution)
		this.currentCandidates.get(pos).clear();
		this.currentCandidates.get(pos).set(candidate);
	}

	/**
	 * Entfernt den aktuellen Zweig und löscht den gesetzten Wert aus der Kandidatenliste des Feldes, welches für das
	 * Branching genutzt wurde. Alles Änderungen in dem Zweig werden zurückgesetzt. Ist kein aktueller Zweig vorhanden,
	 * so wird nichts getan.
	 */
	public void killCurrentBranch() {
		// there is a slight chance that we work with an unsolvable sudoku
        // then we might backtrack to a point where there are no branches left
        // and in that case returning null is better than failing
		//if(this.branchings.isEmpty())
		//    return null;

		/* We're talking about two branches here:
		   B the current branch at the start of the method
		   A that which was before B (possibly another branch) */

		// delete old branch and remove the candidate used for branching from
		// candidates list

		Branching lastBranching = this.branchings.pop();
		currentCandidates = lastBranching.candidates;//override current branch B with A
		for (Position p : lastBranching.solutionsSet)
			fields.get(p).setCurrentValue(Field.EMPTYVAL, false);//remove solutions added in B

		this.complexityValue -= lastBranching.complexityValue;//substract cmplx scores of techniques that are not used after all

		//BitSet branchCandidates = this.currentCandidates.get(lastBranching.position);//candidates of A at critical pos of A
		//branchCandidates.clear(lastBranching.candidate);//since we're deleting B, guessing this candidate led to failure -> it is not part of solution, we need to delete it
		this.branchPool.recycleLastBranching();
		this.positionPool.returnPositionMap();
		//if (branchCandidates.isEmpty()) {
		//    //no candidate was applicable -> backtrack even further
		//	/*return*/ killCurrentBranch();
		//} else {
		//	//return lastBranching.position; //return
		//}
	}

	public Branching getLastBranch(){
		return this.branchings.peek();
	}

	public Position getFirstBranchPosition(){
		return this.branchings.peek().position;
	}

	/**
	 * Updatet die Kandidatenlisten aller Felder dahingehend, dass alle Kandidaten, die die Constraints bei deren
	 * Eintragung in ein Feld nicht erfüllen würden aus der jeweiligen Kandidatenliste entfernt werden.
	 */
	void updateCandidates() {
		ArrayList<Constraint> updatedConstraints;
		ArrayList<Position> updatedPositions;
		boolean isInvalid = false;

		for (Position position : positions) {
			if (!isInvalid && !getField(position).isNotSolved()) {
				// Update fields in unique constraints
				updatedConstraints = this.constraints.get(position);
				for (Constraint uConstraint : updatedConstraints) {
					if (!isInvalid && uConstraint.hasUniqueBehavior()) {
						updatedPositions = uConstraint.getPositions();
						for (int up = 0; up < updatedPositions.size() && !isInvalid; up++) {
							Position updatedPosition = updatedPositions.get(up);
							this.currentCandidates.get(updatedPosition).clear(getField(position).getCurrentValue());
							if (this.currentCandidates.get(updatedPosition).isEmpty()
							 && getField(updatedPosition).isNotSolved())
								isInvalid = true;
						}
					}
				}
			} else {
				/* Update candidates in non-unique constraints */
				boolean hasNonUnique = false;
				updatedConstraints = this.constraints.get(position);
				for (Constraint updatedConstraint : updatedConstraints) {
					if (!updatedConstraint.hasUniqueBehavior()) {
						hasNonUnique = true;
						break;
					}
				}
				//boolean hasNonUnique = updatedConstraints.stream().anyMatch(c -> !c.hasUniqueBehavior());
				if (hasNonUnique) {
					Field currentField = null;
					BitSet currentCandidatesSet = null;
					currentField = this.fields.get(position);
					currentCandidatesSet = this.currentCandidates.get(position);
					int currentCandidate = -1;
					int numberOfCandidates = currentCandidatesSet.cardinality();
					for (int i = 0; i < numberOfCandidates; i++) {
						currentCandidate = currentCandidatesSet.nextSetBit(currentCandidate + 1);
						currentField.setCurrentValue(currentCandidate, false);
						for (Constraint updatedConstraint : updatedConstraints)
							if (!updatedConstraint.isSaturated(this))
								currentCandidatesSet.clear(currentCandidate);

						currentField.setCurrentValue(Field.EMPTYVAL, false);
					}
				}
			}
		}

	}

	/**
	 * Updatet die Kandidatenlisten aller Felder, die in einem Constraint liegen in dem sich auch die spezifizierte
	 * Position befindet dahingehend, dass alle Kandidaten, die die Constraints bei deren Eintragung in ein Feld nicht
	 * erfüllen würden aus der jeweiligen Kandidatenliste entfernt werden. Ist die übergebene Position null, so wird
	 * nichts getan
	 * 
	 * @param pos
	 *            Die Position des Feldes, wessen Veränderung ein Update der Kandidatenlisten erfordert
	 * @param candidate
	 *            Der Kandidat, der in dem angegebenen Feld entfernt wurde
	 */
	void updateCandidates(Position pos, int candidate) {
		if (pos == null)
			return;

		ArrayList<Constraint> updatedConstraints = this.constraints.get(pos);
		ArrayList<Position>   updatedPositions;
		ArrayList<Constraint> checkedConstraints;
		for (Constraint constr: updatedConstraints) {
			updatedPositions = constr.getPositions();
			for (Position uPos : updatedPositions)
				if (this.fields.get(uPos).isNotSolved())
					if (constr.hasUniqueBehavior())
						this.currentCandidates.get(uPos).clear(candidate);
					else {
						int currentCandidate = -1;
						int numberOfCandidates = this.currentCandidates.get(uPos).cardinality();
						for (int i = 0; i < numberOfCandidates; i++) {
							currentCandidate = this.currentCandidates.get(uPos).nextSetBit(currentCandidate + 1);
							this.fields.get(uPos).setCurrentValue(currentCandidate, false);
							checkedConstraints = constraints.get(uPos);
							for (Constraint checkedConstraint : checkedConstraints) {
								if (!checkedConstraint.isSaturated(this))
									this.currentCandidates.get(uPos).clear(currentCandidate);
							}

							this.fields.get(uPos).setCurrentValue(Field.EMPTYVAL, false);
						}
					}


		}
	}

	/**
	 * Setzt die temporären Lösung für das Feld an der spezifizierten Position auf den angegebenen Kandidaten. Es werden
	 * alle abhängigen Kandidatenlisten upgedatet. Beim Entfernene des aktuellen Zweiges wird die eingetragene Lösung
	 * wieder gelöscht.
	 * 
	 * @param pos
	 *            Die Position, an der die Lösung eingetragen werden soll
	 * @param candidate
	 *            Die temporäre Lösung, die eingetragen werden soll
	 */
	void setSolution(Position pos, int candidate) {
		if (pos == null || candidate < 0)
			return;

		fields.get(pos).setCurrentValue(candidate, false);

		currentCandidates.get(pos).clear();
		if (hasBranch())
			branchings.peek().solutionsSet.add(pos);
		updateCandidates(pos, candidate);
	}

	/**
	 * Gibt zurück, ob auf diesem Sudoku ein Branch erzeugt wurde oder nicht.
	 * 
	 * @return true, falls auf diesem Sudoku ein Branch erzeugt wurde, false falls nicht
	 */
	public boolean hasBranch() {
		return !branchings.isEmpty();
	}

	/**
	 * Gibt die Kandidatenliste der spezifizierten Position zurück.
	 * 
	 * @param pos
	 *            Die Position, dessen Kandidatenliste abgerufen werden soll
	 * @return Die Kandidatenliste der übergebenen Position
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die spezifizierte Position ungültig ist
	 */
	public CandidateSet getCurrentCandidates(Position pos) {
		return this.currentCandidates.get(pos);
	}

	/**
	 * Erhöht den Schwierigkeitswert dieses Sudokus um den spezifizierten Wert. Ist dieser kleiner als 0, so wird nichts
	 * getan.
	 * 
	 * @param value
	 *            Der Wert, um den die Schwierigkeit dieses Sudokus erhöht werden soll
	 * @param applyToBranch
	 *            Gibt an, ob der Wert auf den aktuellen Branch oder das gesamte Sudoku angewendet werden soll
	 */
	void addComplexityValue(int value, boolean applyToBranch) {
		if (value > 0) {
			if (branchings.size() > 0)
				branchings.peek().complexityValue += value;
			this.complexityValue += value;
		}
	}

	/**
	 * Gibt die Schwierigkeit dieses Sudokus zurück.
	 * 
	 * @return Die Schwierigkeit dieses Sudokus
	 */
	public int getComplexityValue() {
		return this.complexityValue;
	}

	public List<Position> getPositions(){
		return positions;
	}
	
	/**
	 * Diese Klasse stellt einen Pool von PositionMaps auf BitSets zur Verfügung, sodass benutzte PositionMaps nicht
	 * verfallen, sondern im Pool behalten und für eine weitere Nutzung vorgehalten werden.
	 */
	private static class PositionMapPool {
		/**
		 * Eine Liste der erstellten, noch nicht vergebenen Maps
		 */
		private Stack<PositionMap<CandidateSet>> unusedMaps;

		/**
		 * Ein Stack der erstellten und bereits vergebenen Maps
		 */
		private Stack<PositionMap<CandidateSet>> usedMaps;

		/**
		 * Die Größe der Verwalteten PositionMaps
		 */
		private Position currentDimension;

		/**
		 * Die Positionen
		 */
		private List<Position> positions;

		/**
		 * Initialisiert einen neues PositionMapPool mit PositionMaps der spezifizierten Größe. Die dimension sollte
		 * nicht null oder gleich 0 sein, die positions sollten ebenfalls nicht null sein und denen des Sudokus
		 * entsprechen.
		 * 
		 * @param dimension
		 *            Die Größe der verwalteten PositionMaps
		 */
		public PositionMapPool(Position dimension, List<Position> positions) {
			// Keine Überprüfung der Eingabesituation, da nur lokal genutzt
			this.positions = positions;
			this.currentDimension = dimension;

			usedMaps = new Stack<PositionMap<CandidateSet>>();
			unusedMaps = new Stack<PositionMap<CandidateSet>>();
			unusedMaps.push(initialisePositionMap());
			unusedMaps.push(initialisePositionMap());
		}

		/**
		 * Gibt eine PositionMap entsprechend der aktuell gesetzten Größe zurück. Ist der Pool leer, so wird seine Größe
		 * verdoppelt.
		 * 
		 * @return Eine PositionMap entsprechend der aktuell gesetzten Größe
		 */
		public PositionMap<CandidateSet> getPositionMap() {
			if (unusedMaps.size() == 0) {
				unusedMaps.add(this.initialisePositionMap());
			}

			PositionMap<CandidateSet> ret = unusedMaps.pop();
			usedMaps.push(ret);
			return ret;
		}

		/**
		 * Gibt die zuletzt geholte PositionMap an den Pool zurück.
		 */
		public void returnPositionMap() {
			if (!usedMaps.isEmpty()) {
				PositionMap<CandidateSet> returnedMap = usedMaps.pop();
				for (Position pos : this.positions) {
					returnedMap.get(pos).clear();
				}
				unusedMaps.push(returnedMap);
			}
		}

		/**
		 * Initialisiert eine neue PositionMap der im Konstruktor definierten Größe und gibt diese zurück.
		 * 
		 * @return Eine neue PositionMap der im Konstruktor definierten Größe
		 */
		private PositionMap<CandidateSet> initialisePositionMap() {
			PositionMap<CandidateSet> newMap = new PositionMap<CandidateSet>(currentDimension);
			for (Position pos : this.positions) {
				newMap.put(pos, new CandidateSet());
			}

			return newMap;
		}

		/**
		 * Gibt alle PositionMaps an den Pool zurück.
		 */
		public void returnAll() {
			while (!this.usedMaps.empty()) {
				returnPositionMap();
			}
		}

	}


	/**
	 * Determines whether Lists a,b have a common(by equals) element
	 * @param a
	 * @param b
	 * @param <T> any element in the list needs to have equals defined
	 * @return true iff i.equals(j) == true for at least one i € a, j € b
	 */
	public static <T> boolean intersect(Iterable<T> a, Iterable<T> b){

		for (T t1: a)
			for (T t2: b)
				if(t1.equals(t2))
					return true;

		return false;
	}

	/**
	 * creates a perfect clone,

	@Override
	public Object clone(){
		SolverSudoku clone = new SolverSudoku(this.type);
		clone.id             = this.id;
		clone.transformCount = this.transformCount;
		clone.fields = new HashMap<>();

		for(Map.Entry<Position, Field> e : this.fields.entrySet())
			clone.fields.put(e.getKey(), (Field) e.getValue().clone());

		clone.fieldIdCounter = this.fieldIdCounter;

		clone.fieldPositions = new HashMap<>(this.fieldPositions);

		clone.complexity = this.complexity;

		return clone;
	}*/

}
