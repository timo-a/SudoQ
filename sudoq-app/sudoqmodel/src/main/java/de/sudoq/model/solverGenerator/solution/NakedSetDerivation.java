package de.sudoq.model.solverGenerator.solution;

import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solvingAssistant.HintTypes;

/**
 * Created by timo on 04.10.16.
 */
public class NakedSetDerivation extends SolveDerivation {


    private List<DerivationField> subsetMembers;
    private List<DerivationField> externalFields;
    private BitSet subsetCandidates;

    public NakedSetDerivation(HintTypes technique) {
        super(technique);
        subsetMembers  = new Stack<>();
        externalFields = new Stack<>();
    }

    public void setSubsetCandidates(BitSet bs){
        subsetCandidates = (BitSet) bs.clone();
    }

    public void addExternalField(DerivationField f){
        externalFields.add(f);
    }

    public void addSubsetField(DerivationField f){
        subsetMembers.add(f);
    }

}
