package de.sudoq.model.solverGenerator.solution;

import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;

/**
 * Created by timo on 04.10.16.
 */
public class HiddenSetDerivation extends SolveDerivation {


    private Constraint constraint;
    private List<DerivationField> subsetMembers;
    private BitSet subsetCandidates;

    public HiddenSetDerivation(HintTypes technique) {
        super(technique);
        subsetMembers  = new Stack<>();
    }

    public void setSubsetCandidates(BitSet bs){
        subsetCandidates = (BitSet) bs.clone();
    }

    public void setConstraint(Constraint c){ constraint = c; }

    public void addSubsetField(DerivationField f){
        subsetMembers.add(f);
    }


    public BitSet getSubsetCandidates(){ return subsetCandidates; }

    public Constraint getConstraint(){ return constraint; }

    public List<DerivationField> getSubsetMembers(){ return subsetMembers; }

}
