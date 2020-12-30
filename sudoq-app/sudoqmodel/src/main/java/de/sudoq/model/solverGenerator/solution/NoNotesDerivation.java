package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Sudoku;

/**
 * In case the user doesn't specify any notes, we find fields that have none
 */
public class NoNotesDerivation extends SolveDerivation {

    private List<Action> actionlist;

    public NoNotesDerivation() {
        super(HintTypes.NoNotes);
        this.actionlist = new ArrayList<>();
        hasActionListCapability = true;
    }



    /*public ConstraintShape getConstraintShape(){
        return Utils.getGroupShape(constraint);
    }*/

    /* creates a list of actions in case the user want the app to execute the hints */
    @Override
    public List<Action> getActionList(Sudoku sudoku){
        NoteActionFactory af = new NoteActionFactory();

        for(Iterator<DerivationField> dfi = getFieldIterator(); dfi.hasNext();){
            DerivationField df = dfi.next();
            CandidateSet cs = new CandidateSet();
            cs.assignWith(df.getRelevantCandidates());
            for (int i : cs.getSetBits()){
                actionlist.add(af.createAction(i, sudoku.getField(df.getPosition())));
            }
        }
        return actionlist;
    }

}
