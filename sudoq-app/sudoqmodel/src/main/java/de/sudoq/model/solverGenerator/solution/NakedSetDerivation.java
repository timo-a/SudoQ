package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
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
    private List<DerivationCell> subsetMembers;
    private List<DerivationCell> externalCells;
    private CandidateSet subsetCandidates;

    public NakedSetDerivation(HintTypes technique) {
        super(technique);
        subsetMembers  = new Stack<>();
        externalCells = new Stack<>();
        hasActionListCapability = true;
    }

    public void setSubsetCandidates(CandidateSet bs){
        subsetCandidates = (CandidateSet) bs.clone();
    }

    public void setConstraint(Constraint c){ constraint = c; }

    public void addExternalCell(DerivationCell f){
        externalCells.add(f);
    }

    public void addSubsetCell(DerivationCell f){
        subsetMembers.add(f);
    }


    public CandidateSet getSubsetCandidates(){ return subsetCandidates; }

    public Constraint getConstraint(){ return constraint; }

    public List<DerivationCell> getSubsetMembers(){ return subsetMembers; }

    public List<DerivationCell> getExternalCellsMembers(){ return externalCells; }

    /* creates a list of actions in case the user want the app to execute the hints */
    @Override
    public List<Action> getActionList(Sudoku sudoku){
        List<Action> actionlist = new ArrayList<>();
        NoteActionFactory af = new NoteActionFactory();

        for(DerivationCell df : externalCells)
            for(int note: CandidateSet.fromBitSet(df.getRelevantCandidates()).getSetBits())
                actionlist.add(af.createAction(note, sudoku.getCell(df.getPosition())));

        return actionlist;
    }


}
