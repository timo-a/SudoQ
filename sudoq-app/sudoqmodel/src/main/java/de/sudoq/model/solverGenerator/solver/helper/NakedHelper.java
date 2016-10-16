package de.sudoq.model.solverGenerator.solver.helper;

import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.NakedSetDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;

/**
 * Dieser konkrete SolveHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus. Der SubsetHelper sucht
 * innerhalb der Constraints eines Sudokus nach n elementigen Teilmengen der Kandidaten, die in n Feldern vorkommen. (n
 * entspricht dem level des Helpers). Kommen in n Feldern lediglich dieselben n Kandidaten vor, so können diese
 * Kandidaten aus den anderen Listen des Constraints entfernt werden. Tauchen n Kandidaten lediglich in n Feldern auf,
 * so können in diesen Feldern die restlichen Kandidaten entfernt werden.
 * <p/>
 * If there are n fields with only n distinct candidates in them, those candidates can't appear anywhere else.
 */
public class NakedHelper extends SubsetHelper {

    protected HintTypes hintType;

    private NakedSetDerivation derivation;

    /**
     * Erzeugt einen neuen NakedHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
     * dabei der Größe der Symbolmenge nach der gesucht werden soll.
     *
     * @param sudoku     Das Sudoku auf dem dieser Helper operieren soll
     * @param level      Das Größe der Symbolmenge auf die der Helper hin überprüft
     * @param complexity Die Schwierigkeit der Anwendung dieser Vorgehensweise
     * @throws IllegalArgumentException Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
     */
    public NakedHelper(SolverSudoku sudoku, int level, int complexity) throws IllegalArgumentException {
        super(sudoku, level, complexity);
        HintTypes types[] = {HintTypes.NakedSingle
                            ,HintTypes.NakedPair
                            ,HintTypes.NakedTriple
                            ,HintTypes.NakedQuadruple
                            ,HintTypes.NakedQuintuple
                            };
        if(level<= 5)
            hintType = types[level-1];
        else
            throw new IllegalArgumentException("we can't handle a level > 3 yet.");

    }

    /**
     * collect all candidates appearing in fields with maximum {@code level} candidates.
     * This is 'naked'-specific code for the template method in superclass
     *
     * @param constraint Constraint whose candidates are to be filtered
     * @return
     */
    @Override
    protected BitSet collectPossibleCandidates(Constraint constraint) {
        BitSet possibleCandidates = new BitSet();

        for (Position pos : constraint.getPositions()) {
            BitSet currentCandidates = this.sudoku.getCurrentCandidates(pos);
            byte nrCandidates = (byte)currentCandidates.cardinality();
            if (0 < nrCandidates && nrCandidates <= this.level) //we only want up to n candidates per field
                possibleCandidates.or(currentCandidates);
        }
        //now we have constraintSet of all candidates in the constraint
        return possibleCandidates;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean updateNext(Constraint constraint, boolean buildDerivation) {
        boolean foundSubset = false;

        Stack<Position> positions = new Stack<>();
        for(Position p: constraint.getPositions())
            if(sudoku.getField(p).isEmpty())
                positions.add(p);

        do {

            int subsetCount = filterForSubsets(positions); //subsetPositions = {p | p ∈ positions, p.candidates ⊆ currentSet}

            if (subsetCount == this.level) {
                List<Position> externalPositions = (List<Position>) positions.clone();
                externalPositions.removeAll(subsetPositions);

                for (Position pos : externalPositions) {

                    BitSet currentPosCandidates = this.sudoku.getCurrentCandidates(pos);
                    if (currentPosCandidates.intersects(currentSet)) {
                        //save original candidates
                        localCopy.clear();
                        localCopy.or(currentPosCandidates);

                        //delete all candidates that appear in currentSet
                        currentPosCandidates.andNot(currentSet);

						/* We found a subset that does delete candidates,
                           initialize derivation obj. and fill it during remaining cycles of pos  */
                        if (buildDerivation) {
                            if (!foundSubset) {
                                derivation = new NakedSetDerivation(hintType);
                                derivation.setConstraint(constraint);
                                derivation.setSubsetCandidates(currentSet);
                                for (Position p : subsetPositions) {
                                    BitSet relevantCandidates = (BitSet) this.sudoku.getCurrentCandidates(p).clone();
                                    DerivationField field = new DerivationField(p, relevantCandidates, new BitSet());
                                    derivation.addSubsetField(field);
                                }
                            }
                            //what was deleted?
                            BitSet relevant = (BitSet) localCopy.clone();
                            BitSet irrelevant = (BitSet) localCopy.clone();
                            relevant.and(currentSet);
                            irrelevant.andNot(currentSet);
                            DerivationField field = new DerivationField(pos, relevant, irrelevant);
                            derivation.addExternalField(field);
                        }
                        foundSubset = true;
                    }
                }

            }

        }while(!foundSubset && constraintSet.cardinality() > this.level && getNextSubset());

        if (foundSubset && buildDerivation)
            lastDerivation = derivation;

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
            int nrCandidates = currentCandidates.cardinality();
            if (0 < nrCandidates && nrCandidates <= this.level)
                if (isSubsetOfCurrentSet(currentCandidates))
                    subsetPositions.add(pos);

        }
        //assert subsetCount == subsetPositions.size();
        return subsetPositions.size();
    }

    /*
    * determines whether bs is a subset of CurrentSet, i.e. ∀ i: bs_i == 1  =>  CurrentSet_i == 1
    * */
    private synchronized boolean isSubsetOfCurrentSet(BitSet bs) {//TODO make decorator of BitSet so we can just bs.isSubsetOf(Current)
        /* localCopy := currentCandidates & currentSet */
        localCopy.clear();
        localCopy.or(bs); //copy bs into localCopy
        localCopy.and(currentSet);
        return bs.equals(localCopy); // => bs == bs & currentSet => bs ⊆ currentSetf
    }

}