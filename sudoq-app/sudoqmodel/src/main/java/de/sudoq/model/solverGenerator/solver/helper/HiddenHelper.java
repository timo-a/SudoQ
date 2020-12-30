package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.HiddenSetDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;

/**
 * Dieser konkrete SolveHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus.
 * Der HiddenHelper sucht innerhalb der Constraints eines Sudokus nach n Kandidaten,
 * die lediglich noch in denselben n Feldern vorkommen.
 * (n entspricht dem level des Helpers).
 * Ist dies der Fall, müssen diese n Kandidaten in den n Feldern in irgendeiner
 * Kombination eingetragen werden und daher können alle übrigen symbole aus diesen n feldern entfert werden.
 *
 * {@literal [¹²³⁴][¹²³⁴][³..][³..] -> [¹²][¹²][³..][³..]}
 * Looks for a constraint and a set of n candidates(i.e. distinct symbols that are not a solution).
 * For these candidates must hold that they exclusively appear in n unsolved fields.
 * In that case, they are all the solutions to these n fields (in some order) and all other candidates within these n fields can be removed
 * 
 */
public class HiddenHelper extends SubsetHelper {

    private final HintTypes[] labels = new HintTypes[] { HintTypes.HiddenSingle,
                                                         HintTypes.HiddenPair,
                                                         HintTypes.HiddenTriple,
                                                         HintTypes.HiddenQuadruple,
                                                         HintTypes.HiddenQuintuple,
                                                         HintTypes.Hidden__6_tuple,
                                                         HintTypes.Hidden__7_tuple,
                                                         HintTypes.Hidden__8_tuple,
                                                         HintTypes.Hidden__9_tuple,
                                                         HintTypes.Hidden_10_tuple,
                                                         HintTypes.Hidden_11_tuple,
                                                         HintTypes.Hidden_12_tuple,
                                                         HintTypes.Hidden_13_tuple};

	private HiddenSetDerivation derivation;

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
        if (level <= 0 || level > labels.length)
            throw new IllegalArgumentException("level must be ∈ [1,"+labels.length+"] but is "+level);

        hintType = labels[level-1];

	}

	/**
	 * Collect all candidates appearing in this constraint.
 	 * This is 'hidden'-specific code for the template method in superclass
	 *
	 * @param constraint Constraint object
     * @return BitSet of all candidates in the constraint
     */
	@Override
	protected BitSet collectPossibleCandidates(Constraint constraint) {
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
		boolean foundSubset;
		derivation=null;
		ArrayList<Position> positions = constraint.getPositions();

		do {

			// Count and save all the positions whose candidates are a subset of
			// currentSet i.e. the one to be checked for,
			int subsetCount = filterForSubsets(positions);
			//subsetPositions = {p | p ∈ positions, p.candidates ⊆ currentSet && p.candidates ∩ currentSet ≠ ø }

			// If a subset was found, look through all fields in the subset whether there is one that has more candidates than currentSet -> something can be removed
			foundSubset = false;
			if (subsetCount == this.level) {
				for (Position pos: subsetPositions) {
					BitSet currentCandidates = this.sudoku.getCurrentCandidates(pos);
					localCopy.assignWith(currentCandidates);// localCopy <- currentCand...
					currentCandidates.and(currentSet);
					if (!currentCandidates.equals(localCopy)) {
						// If something changed, a field could be updated, so
						// the helper is applied
						// If the derivation shall be returned, add the updated
						// field to the derivation object
						if (buildDerivation) {
							if (derivation==null) {
								derivation = initializeDerivation(constraint);
								lastDerivation = derivation;
							}

							BitSet relevantCandidates = (BitSet) currentCandidates.clone();
							BitSet irrelevantCandidates = localCopy;
							irrelevantCandidates.andNot(currentSet);
							DerivationField field = new DerivationField(pos, relevantCandidates, irrelevantCandidates);
							lastDerivation.addDerivationField(field);
						}
						foundSubset = true;
					}
				}
			}

		}while(!foundSubset && constraintSet.cardinality() > this.level && getNextSubset());

		return foundSubset;
	}

	/*
     * counts the number of Positions whose candidates are a subset of currentSet -> eligible
     * stores the first 'level' of those in subsetPositions
     */
	private int filterForSubsets(List<Position> positions) {

		subsetPositions.clear();
		for (Position pos : positions) {
			BitSet currentCandidates = this.sudoku.getCurrentCandidates(pos);
			if (!currentCandidates.isEmpty() && currentCandidates.intersects(currentSet)) //TODO why check for empty??
					subsetPositions.add(pos);

		}
		return subsetPositions.size();
	}

	private HiddenSetDerivation initializeDerivation(Constraint constraint){
		HiddenSetDerivation derivation = new HiddenSetDerivation(hintType);
		derivation.setDescription("hidden helper (" + hintType + ")");
		derivation.setConstraint(constraint);
		derivation.setSubsetCandidates(currentSet);
		for (Position p : subsetPositions) {
			BitSet relevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(p).clone();
			relevantCandidates.and(currentSet);
			DerivationField field = new DerivationField(p, relevantCandidates, new BitSet());
			derivation.addSubsetField(field);
		}
		return derivation;
	}
}