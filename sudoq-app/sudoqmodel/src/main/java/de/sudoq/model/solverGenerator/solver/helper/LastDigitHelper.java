package de.sudoq.model.solverGenerator.solver.helper;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Vector;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;

/**
 * Helper that searches for an `open Single`, a constraint in which exactly one field is not solved {@literal ->} can be be solved by principle of exclusion.
 * (update does not modify the sudoku passed in the constructor)
 * Difference to Naked Single: Naked single looks at candidates, LastDigitHelper does not.
 *                             if candidates are constantly updated, naked single catches everything LastDigitHelper catches and more:
 *                             e.g. if constraint A has 2 empty fields but intersects with constraint B which can exclude some candidates in A, naked single will catch that
 */
public class LastDigitHelper extends SolveHelper {


    public LastDigitHelper(SolverSudoku sudoku, int complexity) throws IllegalArgumentException {
        super(sudoku, complexity);
        hintType = HintTypes.LastDigit;
    }

    /**
     * Finds out if {@code positions} has only one empty field. if so return {@code position} and fill {@code remaining} with all other positions respectively
     * @param positions
     * @param remaining list that is filled with all solved positions if there is only one empty field left
     *                  if there are 2+ empty fields, remaining contains not neccessarily all solved positions
     * @return the only position in {@code positions} not solved by the user, if there is one
     *         null otherwise
     */
    private Position onlyOneLeft(List<Position> positions, List<Position> remaining){
        assert remaining.isEmpty();
        Position candidate = null;//no empty fields found
        for(Position p : positions)
            if(sudoku.getField(p).isNotSolved()){
                if(candidate==null)//found our first empty field
                    candidate = p;
                else{
                    candidate = null;//found 2nd empty -> break
                    break;
                }
            }
            else//found
                remaining.add(p);

        return candidate;
    }


    @Override
    public boolean update(boolean buildDerivation) {
        boolean foundOne = false;
        Position candidate;
        Vector<Position> remaining = new Vector<>();
        for (Constraint c : sudoku.getSudokuType().getConstraints())
            if(c.hasUniqueBehavior()) {
                remaining.clear();
                candidate = onlyOneLeft(c.getPositions(), remaining);

                if(candidate != null){
                    /* We found an instance where only one field is empty */
                    //
                    Position solutionField = candidate; //position that needs to be filled

                    //make List with all values entered in this constraint
                    List<Integer> otherSolutions = new ArrayList<>();
                    for(Position p : remaining)
                        otherSolutions.add(sudoku.getField(p).getCurrentValue());

                    //make list with all possible values
                    List<Integer> possibleSolutions = new ArrayList<>((AbstractList)sudoku.getSudokuType().getSymbolIterator());

                    /* cut away all other solutions */
                    possibleSolutions.removeAll(otherSolutions);
                    if(possibleSolutions.size()==1) {
                        /* only one solution remains -> there were no doubles */
                        foundOne = true;
                        int solutionValue = possibleSolutions.get(0);


                        if(buildDerivation){

                            lastDerivation = new LastDigitDerivation(c, solutionField, solutionValue);
                            BitSet relevant = new BitSet();
                            relevant.set(solutionValue); //set solution to 1
                            BitSet irrelevant = new BitSet();
                            irrelevant.xor(relevant); // create complement to relevant
                            lastDerivation.addDerivationField(new DerivationField(candidate, relevant, irrelevant));

                            lastDerivation.addDerivationBlock(new DerivationBlock(c));
                            lastDerivation.setDescription("Look at "+ Utils.classifyGroup(c.getPositions())+"! Only field "+Utils.positionToRealWorld(candidate) + "is empty.");
                        }
                    }
                }
            }
        return foundOne;
    }
}