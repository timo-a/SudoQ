package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Sudoku;

/**
 * Created by timo on 04.10.16.
 */
public class HiddenSetDerivation extends SolveDerivation {


    private Constraint constraint;
    private List<DerivationField> subsetMembers;
    private CandidateSet subsetCandidates;

    public HiddenSetDerivation(HintTypes technique) {
        super(technique);
        subsetMembers  = new Stack<>();
        hasActionListCapability = true;
    }

    public void setSubsetCandidates(BitSet bs){
        subsetCandidates = (CandidateSet) bs.clone();
    }

    public void setConstraint(Constraint c){ constraint = c; }

    public void addSubsetField(DerivationField f){
        subsetMembers.add(f);
    }


    public CandidateSet getSubsetCandidates(){ return subsetCandidates; }

    public Constraint getConstraint(){ return constraint; }

    public List<DerivationField> getSubsetMembers(){ return subsetMembers; }

    /* creates a list of actions in case the user want the app to execute the hints */
    @Override
    public List<Action> getActionList(Sudoku sudoku){
        List<Action> actionlist = new ArrayList<>();
        NoteActionFactory af = new NoteActionFactory();

        for (Iterator<DerivationField> it = getFieldIterator(); it.hasNext(); ) {
            DerivationField df = it.next();
            for(int note: CandidateSet.fromBitSet(df.getIrrelevantCandidates()).getSetBits())
                actionlist.add(af.createAction(note, sudoku.getField(df.getPosition())));
        }

        return actionlist;
    }

}
