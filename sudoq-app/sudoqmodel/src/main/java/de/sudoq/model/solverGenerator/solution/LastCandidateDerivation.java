package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;

/**
 * Created by timo on 04.10.16.
 */
public class LastCandidateDerivation extends SolveDerivation {

    private Position   pos;
    private int        remainingNote;

    private List<Action> actionlist;


    public LastCandidateDerivation(HintTypes technique, Position pos, int remainingNote) {
        super(technique);
        this.pos = pos;
        this.remainingNote = remainingNote;
        this.actionlist = new ArrayList<>();
        hasActionListCapability = true;
    }

    public Position getPosition() {
        return pos;
    }

    @Override
    public List<Action> getActionList(Sudoku sudoku){
        SolveActionFactory af = new SolveActionFactory();

        actionlist.add(af.createAction(remainingNote, sudoku.getField(pos)));
        return actionlist;
    }

}
