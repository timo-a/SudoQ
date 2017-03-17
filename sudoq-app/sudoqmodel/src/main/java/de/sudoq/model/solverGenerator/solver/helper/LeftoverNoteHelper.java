package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solution.LeftoverNoteDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;

/**
 * Created by timo on 25.09.16.
 */

public class LeftoverNoteHelper extends SolveHelper {


    public LeftoverNoteHelper(SolverSudoku sudoku, int complexity) throws IllegalArgumentException {
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
            if(sudoku.getField(p).isEmpty()){
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

        for (Constraint c : sudoku.getSudokuType().getConstraints())
            if(c.hasUniqueBehavior() && hasLeftoverNotes(c)) {

                foundOne=true;

                int leftover = getLeftoverNote(c);

                deleteNote(c, leftover);

                if(buildDerivation){
                   lastDerivation = new LeftoverNoteDerivation(c, leftover);
                }
            }

        return foundOne;
    }

    protected boolean hasLeftoverNotes(Constraint c){
        CandidateSet filled = new CandidateSet();
        CandidateSet notes  = new CandidateSet();
        for(Position p: c){
            if(sudoku.getField(p).isEmpty())
                notes.or(sudoku.getCurrentCandidates(p));
            else
                filled.set(sudoku.getField(p).getCurrentValue());
        }

        return filled.hasCommonElement(notes);
    }

    private int getLeftoverNote(Constraint c){
        CandidateSet filled = new CandidateSet();
        CandidateSet notes  = new CandidateSet();
        for(Position p: c){
            if(sudoku.getField(p).isEmpty())
                notes.or(sudoku.getCurrentCandidates(p));
            else
                filled.set(sudoku.getField(p).getCurrentValue());
        }
        filled.and(notes);
        return filled.nextSetBit(0);
    }

    private void deleteNote(Constraint c, int note){
        for(Position p: c)
            if(sudoku.getField(p).isEmpty() && sudoku.getField(p).isNoteSet(note))
                sudoku.getCurrentCandidates(p).clear(note);
    }

}