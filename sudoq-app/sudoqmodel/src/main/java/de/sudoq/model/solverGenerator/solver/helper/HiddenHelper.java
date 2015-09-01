package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.BitSet;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;

/**
 * Dieser konkrete SolveHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus. Der SubsetHelper sucht
 * innerhalb der Constraints eines Sudokus nach n Kandidaten, die lediglich noch in denselben n Feldern vorkommen. (n
 * entspricht dem level des Helpers). Ist dies der Fall, müssen diese n Kandidaten in den n Feldern in irgendeiner
 * Kombination eingetragen werden und können somit aus den restlichen Kandidatnelisten entfernt werden.
 */
public class HiddenHelper extends SubsetHelper {

	/**
	 * Erzeugt einen neuen HiddenHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
	 * dabei der Größe der Symbolmenge nach der gesucht werden soll.
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem dieser Helper operieren soll
	 * @param level
	 *            Das Größe der Symbolmenge auf die der Helper hin überprüft
	 * @param complexity
	 *            Die Schwierigkeit der Anwendung dieser Vorgehensweise
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
	 */
	public HiddenHelper(SolverSudoku sudoku, int level, int complexity) {
		super(sudoku, level, complexity);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean updateNext(Constraint constraint, boolean buildDerivation) {
		boolean nextSetExists = true;
		boolean foundSubset = false;
		int subsetCount = 0;

		ArrayList<Position> positions = constraint.getPositions();
		while (nextSetExists) {
			nextSetExists = false;
			foundSubset = false;
			// Count and save all the positions whose candidates are a subset of
			// the one to be checked for,
			// look if one of the other positions would be updated to prevent
			// from finding one subset again
			subsetCount = 0;
			for (int posNum = 0; posNum < positions.size(); posNum++) {
				if (this.sudoku.getCurrentCandidates(positions.get(posNum)).cardinality() > 0
						&& this.sudoku.getCurrentCandidates(positions.get(posNum)).intersects(currentSet)) {
					if (subsetCount < this.level) {
						subsetPositions[subsetCount] = positions.get(posNum);
						subsetCount++;
					} else {
						subsetCount++;
						break;
					}
				}
			}

			// If a subset was found, update the other candidates and look if
			// something changed.
			// If something changed, return this as update, otherwise continue
			// searching
			foundSubset = false;
			if (subsetCount == this.level) {
				for (Position pos: subsetPositions) {
					localCopy.clear();
					localCopy.or(this.sudoku.getCurrentCandidates(pos));
					this.sudoku.getCurrentCandidates(pos).and(currentSet);
					if (!this.sudoku.getCurrentCandidates(pos).equals(localCopy)) {
						// If something changed, a field could be updated, so
						// the helper is applied
						// If the derivation shell be returned, add the updated
						// field to the derivation object
						if (buildDerivation) {
							if (!foundSubset) {
								lastDerivation = new SolveDerivation();
								lastDerivation.addDerivationBlock(new DerivationBlock(constraint));
							}

							BitSet relevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(pos)
									.clone();
							BitSet irrelevantCandidates = localCopy;
							irrelevantCandidates.andNot(currentSet);
							DerivationField field = new DerivationField(pos, relevantCandidates,
									irrelevantCandidates);
							lastDerivation.addDerivationField(field);
						}
						foundSubset = true;
					}
				}
			}

			// System.out.println("H: " + constraint + ": " + set + ", " +
			// subset + " (NI: " + (foundSubset) +
			// "), (Count: " + (subsetCount)+ ")");

			if (!foundSubset && constraintSet.cardinality() > this.level) {
				nextSetExists = getNextSubset();
			}
		}

		// If the derivation shell be returned, add the subset fields to the
		// derivation object
		if (foundSubset && buildDerivation) {
			boolean foundOne = false;
			for (int posNum = 0; posNum < positions.size(); posNum++) {
				foundOne = false;
				for (int i = 0; i < subsetCount; i++) {
					if (positions.get(posNum) == subsetPositions[i])
						foundOne = true;
				}
				if (!foundOne) {
					BitSet irrelevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(positions.get(posNum))
							.clone();
					DerivationField field = new DerivationField(positions.get(posNum), new BitSet(),
							irrelevantCandidates);
					lastDerivation.addDerivationField(field);
				}
			}
		}

		return foundSubset;
	}

}
