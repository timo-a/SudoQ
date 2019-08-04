package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.List;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;

/**
 * Created by timo on 04.10.16.
 */
public class LeftoverNoteDerivation extends SolveDerivation {


    private Constraint constraint;
    private int note;

    private List<Action> actionlist;


    public LeftoverNoteDerivation(Constraint c, int note) {
        super(HintTypes.LeftoverNote);
        constraint = c;
        this.note = note;
        this.actionlist = new ArrayList<>();
        hasActionListCapability = true;
    }

    public Constraint getConstraint(){ return constraint; }
    public int getNote(){ return note; }

    public ConstraintShape getConstraintShape(){
        return Utils.getGroupShape(constraint);
    }


    @Override
    public List<Action> getActionList(Sudoku sudoku){
        NoteActionFactory af = new NoteActionFactory();

        for (Position p : constraint ) {
            Field f = sudoku.getField(p);
            if (f.isNoteSet(note) && f.isNotSolved())
                actionlist.add(af.createAction(note, f));
        }
        return actionlist;
    }
}
