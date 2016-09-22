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
 *
 * [¹²³⁴][¹²³⁴][³..][³..] -> [¹²][¹²][³..][³..]
 * Looks for a constraint and a set of n candidates(i.e. distinct symbols that are not a solution).
 * For these candidates must hold that they exclusively appear in n unsolved fields.
 * In that case, they are all the solutions to these n fields (in some order) and all other candidates within these n fields can be removed
 * 
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
	 * Collect all candidates appearing in this constraint.
 	 * This is 'hidden'-specific code for the template method in superclass
	 * @param sudoku
	 * @param constraint
     * @return
     */
	@Override
	protected BitSet collectPossibleCandidates(SolverSudoku sudoku, Constraint constraint) {
		BitSet constraintSet = new BitSet();
		for (Position pos : constraint.getPositions()) {
				constraintSet.or(this.sudoku.getCurrentCandidates(pos));
		}
		//now we have constraintSet of all candidates in the constraint
		return constraintSet;

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
			// look if one of the other positions would be updated to prevent <- where does this happen?
			// from finding one subset again
			subsetCount = 0;
			for (Position pos : positions) {
				BitSet currentCandidates = this.sudoku.getCurrentCandidates(pos);
				if (!currentCandidates.isEmpty() && currentCandidates.intersects(currentSet)) {//TODO why check for empty??
					if (subsetCount < this.level) {
						subsetPositions[subsetCount] = pos;
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
					BitSet currerntCandidates = this.sudoku.getCurrentCandidates(pos);
					localCopy.clear();
					localCopy.or(currerntCandidates);// localCopy <- currentCand...
					currerntCandidates.and(currentSet);
					if (!currerntCandidates.equals(localCopy)) {
						// If something changed, a field could be updated, so
						// the helper is applied
						// If the derivation shall be returned, add the updated
						// field to the derivation object
						if (buildDerivation) {
							if (!foundSubset) {
								lastDerivation = new SolveDerivation();
								lastDerivation.addDerivationBlock(new DerivationBlock(constraint));
							}

							BitSet relevantCandidates = (BitSet) currerntCandidates.clone();
							BitSet irrelevantCandidates = localCopy;
							irrelevantCandidates.andNot(currentSet);
							DerivationField field = new DerivationField(pos, relevantCandidates, irrelevantCandidates);
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

		// If the derivation shall be returned, add the subset fields to the
		// derivation object
		if (foundSubset && buildDerivation) {
			boolean foundOne;
			for (Position pos : positions) {
				foundOne = false;
				for (Position pSub: subsetPositions)
					if(pos == pSub)
						foundOne = true;

				if (!foundOne) {
					BitSet irrelevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(pos).clone();
					DerivationField field = new DerivationField(pos, new BitSet(), irrelevantCandidates);
					lastDerivation.addDerivationField(field);
				}
			}
		}

		return foundSubset;
	}

}
