package de.sudoq.model.solverGenerator.solver.helper;

import de.sudoq.model.solverGenerator.solution.LeftoverNoteDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;

/**
 * Created by timo on 25.09.16.
 */

public class LeftoverNoteHelper extends SolveHelper {


    public LeftoverNoteHelper(SolverSudoku sudoku, int complexity) throws IllegalArgumentException {
        super(sudoku, complexity);
        hintType = HintTypes.LeftoverNote;
    }



    @Override
    public boolean update(boolean buildDerivation) {
        boolean foundOne = false;

        for (Constraint c : sudoku.getSudokuType())
            if(c.hasUniqueBehavior() && hasLeftoverNotes(c)) {

                foundOne=true;

                int leftover = getLeftoverNote(c);

                deleteNote(c, leftover);

                if(buildDerivation){
                   lastDerivation = new LeftoverNoteDerivation(c, leftover);
                }
                break;
            }

        return foundOne;
    }

    protected boolean hasLeftoverNotes(Constraint c){
        CandidateSet filled = new CandidateSet();
        CandidateSet notes  = new CandidateSet();
        for(Position p: c){
            if(sudoku.getCell(p).isNotSolved())
                notes.or(sudoku.getCurrentCandidates(p)); //collect all notes
            else
                filled.set(sudoku.getCell(p).getCurrentValue()); //collect all entered solution
        }

        return filled.hasCommonElement(notes);
    }

    private int getLeftoverNote(Constraint c){
        CandidateSet filled = new CandidateSet();
        CandidateSet notes  = new CandidateSet();
        for(Position p: c){
            if(sudoku.getCell(p).isNotSolved())
                notes.or(sudoku.getCurrentCandidates(p));
            else
                filled.set(sudoku.getCell(p).getCurrentValue());
        }
        filled.and(notes);
        return filled.nextSetBit(0);
    }

    private void deleteNote(Constraint c, int note){
        for(Position p: c)
            if(sudoku.getCell(p).isNotSolved() && sudoku.getCell(p).isNoteSet(note))
                sudoku.getCurrentCandidates(p).clear(note);
    }

}