package de.sudoq.model.solverGenerator.solver.helper;

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
 * Created by timo on 25.09.16.
 */

public class LastDigitHelper extends SolveHelper {


    public LastDigitHelper(SolverSudoku sudoku, int complexity) throws IllegalArgumentException {
        super(sudoku, complexity);
    }

    /**
     * Finds out if {@code positions} has only one empty field. if so return {@code position} and fill {@code remaining} with all other positions respectively
     * @param positions
     * @param remaining
     * @return
     */
    private Position onlyOneLeft(List<Position> positions, List<Position> remaining){
        Position candidate = null;
        remaining.clear();
        for(Position p : positions)
            if(sudoku.getField(p).isNotSolved()){
                if(candidate==null)//found empty
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
                    List<Integer> possibleSolutions = new ArrayList<>();
                    for(int i = 0; i< sudoku.getSudokuType().getNumberOfSymbols(); i++)
                        possibleSolutions.add(i);

                    /* cut away all other solutions */
                    possibleSolutions.removeAll(otherSolutions);
                    if(possibleSolutions.size()==1) {
                        /* only one solution remains -> there were no doubles */
                        foundOne = true;
                        int solutionValue = possibleSolutions.get(0);


                        if(buildDerivation){

                            lastDerivation = new LastDigitDerivation(HintTypes.LastDigit, c, solutionField, solutionValue);
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