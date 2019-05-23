package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.BitSet;
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
public class NakedSetDerivation extends SolveDerivation {


    private Constraint constraint;
    private List<DerivationField> subsetMembers;
    private List<DerivationField> externalFields;
    private CandidateSet subsetCandidates;

    public NakedSetDerivation(HintTypes technique) {
        super(technique);
        subsetMembers  = new Stack<>();
        externalFields = new Stack<>();
        hasActionListCapability = true;
    }

    public void setSubsetCandidates(CandidateSet bs){
        subsetCandidates = (CandidateSet) bs.clone();
    }

    public void setConstraint(Constraint c){ constraint = c; }

    public void addExternalField(DerivationField f){
        externalFields.add(f);
    }

    public void addSubsetField(DerivationField f){
        subsetMembers.add(f);
    }


    public CandidateSet getSubsetCandidates(){ return subsetCandidates; }

    public Constraint getConstraint(){ return constraint; }

    public List<DerivationField> getSubsetMembers(){ return subsetMembers; }

    public List<DerivationField> getExternalFieldsMembers(){ return externalFields; }

    /* creates a list of actions in case the user want the app to execute the hints */
    @Override
    public List<Action> getActionList(Sudoku sudoku){
        List<Action> actionlist = new ArrayList<>();
        NoteActionFactory af = new NoteActionFactory();

        for(DerivationField df : externalFields)
            for(int note: CandidateSet.fromBitSet(df.getRelevantCandidates()).getSetBits())
                actionlist.add(af.createAction(note, sudoku.getField(df.getPosition())));

        return actionlist;
    }


}
