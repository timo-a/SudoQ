package de.sudoq.model.solverGenerator.solution;

import java.util.Collections;
import java.util.List;

import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;
import de.sudoq.model.sudoku.UtilsKt;

/**
 * Created by timo on 04.10.16.
 */
public class LastDigitDerivation extends SolveDerivation {

    private Constraint constraint;
    private Position   emptyPosition;
    private int        solution;

    public LastDigitDerivation(Constraint constraint, Position emptyPosition, int solution) {
        super(HintTypes.LastDigit);
        this.constraint = constraint;
        this.emptyPosition = emptyPosition;
        this.solution = solution;
        hasActionListCapability = true;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public ConstraintShape getConstraintShape(){
        return UtilsKt.getGroupShape(constraint);
    }

    @Override
    public List<Action> getActionList(Sudoku sudoku){
        SolveActionFactory af = new SolveActionFactory();
        return Collections.singletonList(af.createAction(solution, sudoku.getCell(emptyPosition)));
    }


}
