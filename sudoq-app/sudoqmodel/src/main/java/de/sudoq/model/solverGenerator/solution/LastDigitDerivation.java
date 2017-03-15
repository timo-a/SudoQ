package de.sudoq.model.solverGenerator.solution;

import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;

/**
 * Created by timo on 04.10.16.
 */
public class LastDigitDerivation extends SolveDerivation {

    private Constraint constraint;
    private Position   emptyPosition;
    private int        remainingNote;


    public LastDigitDerivation(HintTypes technique, Constraint constraint, Position emptyPosition, int remainingNote) {
        super(technique);
        this.constraint = constraint;
        this.emptyPosition = emptyPosition;
        this.remainingNote = remainingNote;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public Position getEmptyPosition() {
        return emptyPosition;
    }

    public ConstraintShape getConstraintShape(){
        return Utils.getGroupShape(constraint);
    }


}
