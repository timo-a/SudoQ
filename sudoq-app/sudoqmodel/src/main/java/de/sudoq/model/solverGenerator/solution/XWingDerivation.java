package de.sudoq.model.solverGenerator.solution;

import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;

/**
 * Created by timo on 04.10.16.
 */
public class XWingDerivation extends SolveDerivation {


    private List<Constraint> lockedConstraints;
    private List<Constraint> reducibleConstraints;
    private int note;

    public XWingDerivation() {
        super(HintTypes.XWing);
        lockedConstraints    = new Stack<>();
        reducibleConstraints = new Stack<>();
    }

    public void setLockedConstraints(Constraint c1, Constraint c2){
        lockedConstraints.add(c1);
        lockedConstraints.add(c2);
    }

    public void setReducibleConstraints(Constraint c1, Constraint c2){
        reducibleConstraints.add(c1);
        reducibleConstraints.add(c2);
    }

    public List<Constraint> getReducibleConstraints() {
        return reducibleConstraints;
    }

    public List<Constraint> getLockedConstraints() {
        return lockedConstraints;
    }

    public void setNote(int i){
        note=i;
    }

    public int getNote() {
        return note;
    }




}
