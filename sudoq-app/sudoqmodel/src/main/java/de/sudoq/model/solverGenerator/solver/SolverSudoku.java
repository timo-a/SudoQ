package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solverGenerator.solver.BranchingPool.Branching;
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
	private PositionMap<BitSet> currentCandidates;

	/**
	 * Speichert die Positionen an denen gebrancht wurde.
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

	/**
	 * Instanziiert ein neues SolverSudoku, welches sich auf das spezifizierte Sudoku bezieht.
	 * 
	 * @param sudoku
	 *            Das Sudoku zu dem dieses SolverSudoku gehört
	 * @throws NullpointerException
	 *             Wird geworfen, falls das spezifizierte Sudoku null ist
	 */
	public SolverSudoku(Sudoku sudoku) {
		super(sudoku.getSudokuType());
		this.setComplexity(sudoku.getComplexity());

		// initialize the list of positions
		this.positions = new ArrayList<Position>();
		for (Position p : fields.keySet()) {
			this.positions.add(p);
		}

		// initialize new SolverSudoku with the fields of the specified one
		for (int i = 0; i < this.positions.size(); i++) {
			if (fields.remove(positions.get(i)) != null)
				fields.put(positions.get(i), sudoku.getField(positions.get(i)));
		}

		// initialize the constraints lists for each position and the initial
		// candidates for each field
		this.constraints = new PositionMap<ArrayList<Constraint>>(this.getSudokuType().getSize());
		for (int i = 0; i < this.positions.size(); i++) {
			this.constraints.put(positions.get(i), new ArrayList<Constraint>());
		}

		// add the constraints each position belongs to to the list
		ArrayList<Constraint> allConstraints = sudoku.getSudokuType().getConstraints();
		ArrayList<Position> currentPositions;
		for (int constrNum = 0; constrNum < allConstraints.size(); constrNum++) {
			currentPositions = allConstraints.get(constrNum).getPositions();
			for (int posNum = 0; posNum < currentPositions.size(); posNum++) {
				this.constraints.get(currentPositions.get(posNum)).add(allConstraints.get(constrNum));
			}
		}

		// initialize the candidates map
		this.positionPool = new PositionMapPool(getSudokuType().getSize(), positions);
		this.branchPool = new BranchingPool();
		this.currentCandidates = this.positionPool.getPositionMap();

		// initialize the candidate lists and branchings
		this.branchings = new Stack<Branching>();
		this.resetCandidates();
	}

	/**
	 * Setzt die Kandidatenlisten aller Feldern zurück, sodass alle Kandidatenlisten komplett befüllt sind. "Komplett"
	 * wird anhand des größten Constraints in dem sich dieses Feld befindet bemessen. Anschließend werden die
	 * Kandidatenlisten bzgl. ConstraintSaturation upgedatet.
	 */
	public void resetCandidates() {
		this.complexityValue = 0;

		// delete the branchings
		this.branchPool.returnAll();
		this.positionPool.returnAll();
		this.branchings.clear();

		this.currentCandidates = this.positionPool.getPositionMap();
		// set the candidate lists of all fields to maximum
		for (int i = 0; i < this.positions.size(); i++) {
			if (fields.get(this.positions.get(i)).isEmpty()) {
				this.currentCandidates.get(this.positions.get(i)).set(0, getSudokuType().getNumberOfSymbols());
			}
		}

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
		Branching branch = this.branchPool.getBranching(pos, candidate);
		branch.candidates = currentCandidates;
		this.currentCandidates = this.positionPool.getPositionMap();
		for (int i = 0; i < this.positions.size(); i++) {
			this.currentCandidates.get(this.positions.get(i)).or(branch.candidates.get(this.positions.get(i)));
		}

		this.branchings.push(branch);

		this.currentCandidates.get(pos).clear();
		this.currentCandidates.get(pos).set(candidate);
	}

	/**
	 * Entfernt den aktuellen Zweig und löscht den gesetzten Wert aus der Kandidatenliste des Feldes, welches für das
	 * Branching genutzt wurde. Alles Änderungen in dem Zweig werden zurückgesetzt. Ist kein aktueller Zweig vorhanden,
	 * so wird nichts getan.
	 */
	Position killCurrentBranch() {
		// if there is no branch, return
		if (this.branchings.isEmpty())
			return null;

		// delete old branch and remove the candidate used for branching from
		// candidates list
		Branching lastBranching = this.branchings.pop();
		currentCandidates = lastBranching.candidates;
		for (int i = 0; i < lastBranching.solutionsSet.size(); i++) {
			fields.get(lastBranching.solutionsSet.get(i)).setCurrentValue(Field.EMPTYVAL, false);
		}
		this.complexityValue -= lastBranching.complexityValue;

		BitSet branchCandidates = this.currentCandidates.get(lastBranching.position);
		branchCandidates.clear(lastBranching.candidate);
		this.branchPool.returnBranching();
		this.positionPool.returnPositionMap();
		if (branchCandidates.cardinality() == 0) {
			return killCurrentBranch();
		} else {
			return lastBranching.position;
		}
	}

	/**
	 * Updatet die Kandidatenlisten aller Felder dahingehend, dass alle Kandidaten, die die Constraints bei deren
	 * Eintragung in ein Feld nicht erfüllen würden aus der jeweiligen Kandidatenliste entfernt werden.
	 */
	void updateCandidates() {
		ArrayList<Constraint> updatedConstraints;
		ArrayList<Position> updatedPositions;
		boolean isInvalid = false;

		for (int posNum = 0; posNum < this.positions.size() && !isInvalid; posNum++) {
			if (!getField(this.positions.get(posNum)).isEmpty()) {
				// Update fields in unique constraints
				updatedConstraints = this.constraints.get(this.positions.get(posNum));
				for (int uc = 0; uc < updatedConstraints.size() && !isInvalid; uc++) {
					if (updatedConstraints.get(uc).hasUniqueBehavior()) {
						updatedPositions = updatedConstraints.get(uc).getPositions();
						for (int up = 0; up < updatedPositions.size() && !isInvalid; up++) {
							this.currentCandidates.get(updatedPositions.get(up)).clear(
									getField(this.positions.get(posNum)).getCurrentValue());
							if (this.currentCandidates.get(updatedPositions.get(up)).cardinality() == 0
									&& getField(updatedPositions.get(up)).isEmpty())
								isInvalid = true;
						}
					}
				}
			} else {
				// Update candidates in non-unique constraints
				boolean hasNonUnique = false;
				updatedConstraints = this.constraints.get(this.positions.get(posNum));
				for (int i = 0; i < updatedConstraints.size(); i++) {
					if (!updatedConstraints.get(i).hasUniqueBehavior()) {
						hasNonUnique = true;
						break;
					}
				}
				if (hasNonUnique) {
					Field currentField = null;
					BitSet currentCandidatesSet = null;
					currentField = this.fields.get(this.positions.get(posNum));
					currentCandidatesSet = this.currentCandidates.get(this.positions.get(posNum));
					int currentCandidate = -1;
					int numberOfCandidates = currentCandidatesSet.cardinality();
					for (int i = 0; i < numberOfCandidates; i++) {
						currentCandidate = currentCandidatesSet.nextSetBit(currentCandidate + 1);
						currentField.setCurrentValue(currentCandidate, false);
						for (int constrNum = 0; constrNum < updatedConstraints.size(); constrNum++) {
							if (!updatedConstraints.get(constrNum).isSaturated(this))
								currentCandidatesSet.clear(currentCandidate);
						}
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
		ArrayList<Position> updatedPositions;
		ArrayList<Constraint> checkedConstraints;
		for (int constrNum = 0; constrNum < updatedConstraints.size(); constrNum++) {
			updatedPositions = updatedConstraints.get(constrNum).getPositions();
			for (int posNum = 0; posNum < updatedPositions.size(); posNum++) {
				if (this.fields.get(updatedPositions.get(posNum)).isEmpty()) {
					if (updatedConstraints.get(constrNum).hasUniqueBehavior()) {
						this.currentCandidates.get(updatedPositions.get(posNum)).clear(candidate);
					} else {
						int currentCandidate = -1;
						int numberOfCandidates = this.currentCandidates.get(updatedPositions.get(posNum)).cardinality();
						for (int i = 0; i < numberOfCandidates; i++) {
							currentCandidate = this.currentCandidates.get(updatedPositions.get(posNum)).nextSetBit(
									currentCandidate + 1);
							this.fields.get(updatedPositions.get(posNum)).setCurrentValue(currentCandidate, false);
							checkedConstraints = constraints.get(updatedPositions.get(posNum));
							for (int upCon = 0; upCon < checkedConstraints.size(); upCon++) {
								if (!checkedConstraints.get(upCon).isSaturated(this))
									this.currentCandidates.get(updatedPositions.get(posNum)).clear(currentCandidate);
							}
							this.fields.get(updatedPositions.get(posNum)).setCurrentValue(Field.EMPTYVAL, false);
						}
					}
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
		if (!branchings.isEmpty())
			branchings.peek().solutionsSet.add(pos);
		updateCandidates(pos, candidate);
	}

	/**
	 * Gibt zurück, ob auf diesem Sudoku ein Branch erzeugt wurde oder nicht.
	 * 
	 * @return true, falls auf diesem Sudoku ein Branch erzeugt wurde, false falls nicht
	 */
	boolean hasBranch() {
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
	public BitSet getCurrentCandidates(Position pos) {
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
	int getComplexityValue() {
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
		private Stack<PositionMap<BitSet>> unusedMaps;

		/**
		 * Ein Stack der erstellten und bereits vergebenen Maps
		 */
		private Stack<PositionMap<BitSet>> usedMaps;

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

			usedMaps = new Stack<PositionMap<BitSet>>();
			unusedMaps = new Stack<PositionMap<BitSet>>();
			unusedMaps.push(initialisePositionMap());
			unusedMaps.push(initialisePositionMap());
		}

		/**
		 * Gibt eine PositionMap entsprechend der aktuell gesetzten Größe zurück. Ist der Pool leer, so wird seine Größe
		 * verdoppelt.
		 * 
		 * @return Eine PositionMap entsprechend der aktuell gesetzten Größe
		 */
		public PositionMap<BitSet> getPositionMap() {
			if (unusedMaps.size() == 0) {
				unusedMaps.add(this.initialisePositionMap());
			}

			PositionMap<BitSet> ret = unusedMaps.pop();
			usedMaps.push(ret);
			return ret;
		}

		/**
		 * Gibt die zuletzt geholte PositionMap an den Pool zurück.
		 */
		public void returnPositionMap() {
			if (!usedMaps.isEmpty()) {
				PositionMap<BitSet> returnedMap = usedMaps.pop();
				for (int i = 0; i < this.positions.size(); i++) {
					returnedMap.get(this.positions.get(i)).clear();
				}
				unusedMaps.push(returnedMap);
			}
		}

		/**
		 * Initialisiert eine neue PositionMap der im Konstruktor definierten Größe und gibt diese zurück.
		 * 
		 * @return Eine neue PositionMap der im Konstruktor definierten Größe
		 */
		private PositionMap<BitSet> initialisePositionMap() {
			PositionMap<BitSet> newMap = new PositionMap<BitSet>(currentDimension);
			for (int i = 0; i < this.positions.size(); i++) {
				newMap.put(this.positions.get(i), new BitSet());
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

}
