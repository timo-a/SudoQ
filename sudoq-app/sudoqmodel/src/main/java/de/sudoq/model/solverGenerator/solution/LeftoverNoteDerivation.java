package de.sudoq.model.solverGenerator.solution;

import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;

/**
 * Created by timo on 04.10.16.
 */
public class LeftoverNoteDerivation extends SolveDerivation {


    private Constraint constraint;
    private int note;

    public LeftoverNoteDerivation(Constraint c, int note) {
        super(HintTypes.LeftoverNote);
        constraint = c;
        this.note = note;
    }

    public Constraint getConstraint(){ return constraint; }
    public int getNote(){ return note; }

}
