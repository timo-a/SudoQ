package de.sudoq.model.solverGenerator.solution;

import java.util.BitSet;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;

/**
 * Created by timo on 04.10.16.
 */
public class LockedCandidatesDerivation extends SolveDerivation {


    private Constraint lockedConstraint;
    private Constraint reducibleConstraint;
    private BitSet removableNotes;
    private int note;

    public LockedCandidatesDerivation() {
        super(HintTypes.LockedCandidatesExternal);
    }

    public void setLockedConstraint(Constraint c1){ lockedConstraint = c1; }

    public void setReducibleConstraint(Constraint c1){ reducibleConstraint = c1; }

    public Constraint getReducibleConstraint() {
        return reducibleConstraint;
    }

    public Constraint getLockedConstraint() {
        return lockedConstraint;
    }



    public void setRemovableNotes(BitSet i){
        removableNotes = i;
        note = removableNotes.nextSetBit(0);
    }

    public int getNote() {
        return note + 1;
    }

}
