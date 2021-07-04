package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.List;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

/**
 * Created by timo on 04.10.16.
 */
public class LastCandidateDerivation extends SolveDerivation {

    private Position   pos;
    private int        remainingNote;

    private List<Action> actionlist;


    public LastCandidateDerivation(Position pos, int remainingNote) {
        super(HintTypes.LastCandidate);
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

        actionlist.add(af.createAction(remainingNote, sudoku.getCell(pos)));
        return actionlist;
    }

}
