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
 * innerhalb der Constraints eines Sudokus nach n elementigen Teilmengen der Kandidaten, die in n Feldern vorkommen. (n
 * entspricht dem level des Helpers). Kommen in n Feldern lediglich dieselben n Kandidaten vor, so können diese
 * Kandidaten aus den anderen Listen des Constraints entfernt werden. Tauchen n Kandidaten lediglich in n Feldern auf,
 * so können in diesen Feldern die restlichen Kandidaten entfernt werden.
 *
 * If there are n fields with only n distinct candidates in them, those candidates can't appear anywhere else.
 *
 *
 */
public class NakedHelper extends SubsetHelper {

	/**
	 * Erzeugt einen neuen NakedHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
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
	public NakedHelper(SolverSudoku sudoku, int level, int complexity) throws IllegalArgumentException {
		super(sudoku, level, complexity);
	}

	/**
	 * collect all candidates appearing in fields with maximum {@code level} candidates.
	 * This is 'naked'-specific code for the template method in superclass
	 * @param sudoku
	 * @param constraint
	 * @return
	 */
	@Override
	protected BitSet collectPossibleCandidates(SolverSudoku sudoku, Constraint constraint) {
		BitSet constraintSet = new BitSet();

		for (Position pos : constraint.getPositions()) {
			BitSet currentCandidates = this.sudoku.getCurrentCandidates(pos);
			if (currentCandidates.cardinality() <= this.level) //we only want up to n candidates per field
				constraintSet.or(currentCandidates);
		}
		//now we have constraintSet of all candidates in the constraint
		return constraintSet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
			for (Position pos : positions) {
				BitSet currentCandidates = this.sudoku.getCurrentCandidates(pos);
				if (0 < currentCandidates.cardinality()
				     && currentCandidates.cardinality() <= this.level) {
					localCopy.clear();
					localCopy.or(currentCandidates);
					localCopy.and(currentSet);
					if (currentCandidates.equals(localCopy)) {
						if (subsetCount < this.level) {
							subsetPositions[subsetCount] = pos;
							subsetCount++;
						} else {
							subsetCount++;
							break;
						}
					}
				}
			}

			// If a subset was found, update the other candidates and look if
			// something changed.
			// If something changed, return this as update, otherwise continue
			// searching
			foundSubset = false;
			boolean foundOne = false;
			if (subsetCount == this.level) {
				for (Position pos : positions) {
					foundOne = false;
					for (int i = 0; i < subsetCount; i++) {
						if (pos == subsetPositions[i])
							foundOne = true;
					}
					if (!foundOne) {
						localCopy.clear();
						localCopy.or(this.sudoku.getCurrentCandidates(pos));
						this.sudoku.getCurrentCandidates(pos).andNot(currentSet);
						if (!this.sudoku.getCurrentCandidates(pos).equals(localCopy)) {
							// If something changed, a field could be updated,
							// so the helper is applied
							// If the derivation shell be returned, add the
							// updated field to the derivation object
							if (buildDerivation) {
								if (!foundSubset) {
									lastDerivation = new SolveDerivation();
									lastDerivation.addDerivationBlock(new DerivationBlock(constraint));
								}
								BitSet relevantCandidates = localCopy;
								relevantCandidates.and(currentSet);
								BitSet irrelevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(pos).clone();
								DerivationField field = new DerivationField(pos, relevantCandidates, irrelevantCandidates);
								lastDerivation.addDerivationField(field);
							}
							foundSubset = true;
						}
					}
				}
			}

			// System.out.println("N: " + constraint + ": " + set + ", " +
			// subset + " (NI: " + (foundSubset) +
			// "), (Count: " + (subsetCount)+ ")");

			if (!foundSubset && constraintSet.cardinality() > this.level) {
				nextSetExists = getNextSubset();
			}
		}

		// If the derivation shell be returned, add the subset fields to the
		// derivation object
		if (foundSubset && buildDerivation) {
			for (int i = 0; i < subsetCount; i++) {
				Position p = subsetPositions[i];
				BitSet relevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(p).clone();
				DerivationField field = new DerivationField(p, relevantCandidates, new BitSet());
				lastDerivation.addDerivationField(field);
			}
		}

		return foundSubset;
	}
}
