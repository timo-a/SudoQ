package de.sudoq.model.solverGenerator.solver.helper;

import java.util.BitSet;

import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.sudoku.Position;

/**
 * Dieser konkrete SolverHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus. Das Backtracking ist ein Try
 * & Error Verfahren, wobei beginnend bei einem Feld systematisch versucht wird ein Symbol einzutragen, sodass die
 * Constraints weiterhin erfüllt sind. Ist dies der Fall, so wird dasselbe mit dem nächsten Feld getan, bis entweder in
 * einem Feld beim Eintragen jedes Symbols die Constraints verletzt sind oder die Kandidatenliste eines anderen Feldes
 * leer wird oder aber das gesamte Sudoku befüllt ist. In den ersten beiden Fällen wird ein Feld zurückgegangen
 * (Backtracking) und dort mit dem nächsten Symbol fortgefahren.
 */
public class Backtracking extends SolveHelper {
	/** Constructors */

	/**
	 * Erzeugt einen neuen Backtracking Helfer für das spezifizierte Sudoku.
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem dieser Helper operieren soll
	 * @param complexity
	 *            Die Schwierigkeit der Anwendbarkeit dieses Lösungshelfers
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das Sudoku null oder die complexity kleiner als 0 ist
	 */
	public Backtracking(SolverSudoku sudoku, int complexity) {
		super(sudoku, complexity);
	}

	/** Methods */

	/**
	 * Wendet das Backtracking-Verfahren an und ermittelt damit die Lösung für alle Felder des Sudokus. Es wird die
	 * Kandidatenliste desjenigen Feldes mit der kleinsten Kandidatenliste upgedated, sodass in dieser nur noch die
	 * korrekte Lösung vorhanden ist. Gibt es keine eindeutige kleinste Kandidatenliste, so wird diese zufällig
	 * ausgewählt.
	 * 
	 * @param buildDerivation
	 *            Bestimmt, ob beim Finden eines Subsets eine Herleitung dafür erstellt werden soll, welche daraufhin
	 *            mit getDerivation abgerufen werden kann.
	 * @return true, falls das Backtracking angewendet werden konnte, false falls nicht
	 */
	public boolean update(boolean buildDerivation) {
		Position leastCandidatesPosition = null;
		lastDerivation = null;

		int leastCandidates = -1;
		for (Position p: this.sudoku.getPositions()) {
			int cardinality =this.sudoku.getCurrentCandidates(p).cardinality(); 
			if ((cardinality < leastCandidates || leastCandidates == -1) && cardinality > 1) {
				leastCandidates = cardinality;
				leastCandidatesPosition = p;
			}
		}

		if (leastCandidatesPosition == null)
			return false;

		int chosenCandidate = this.sudoku.getCurrentCandidates(leastCandidatesPosition).nextSetBit(0);
		this.sudoku.startNewBranch(leastCandidatesPosition, chosenCandidate);

		// UNCOMMENT THE FOLLOWING TO PRINT BACKTRACKING TRACE
		/*
		 * for (int i = 0; i < sudoku.branchings.size(); i++) { System.out.print(" "); }
		 * System.out.println(leastCandidatesPosition + "(#" + leastCandidates + "): " + chosenCandidate);
		 */

		if (buildDerivation) {
			lastDerivation = new SolveDerivation();
			BitSet irrelevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(leastCandidatesPosition).clone();
			BitSet relevantCandidates = new BitSet();
			relevantCandidates.set(chosenCandidate);
			irrelevantCandidates.clear(chosenCandidate);
			DerivationField derivField = new DerivationField(leastCandidatesPosition, relevantCandidates,
					irrelevantCandidates);
			lastDerivation.addDerivationField(derivField);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Als SolveDerivation wird lediglich die Kandidatenliste des gelösten Feldes als irrelevantCandidates, sowie die
	 * richtige Lösung des Feldes als relevantCandidate zurückgegeben.
	 * 
	 * @return Eine SolveDerivation, die die Herleitung für den letzten update-Schritt enthält oder null, falls kein
	 *         Backtracking möglich war
	 */
	@Override
	public SolveDerivation getDerivation() {
		return lastDerivation;
	}
}
