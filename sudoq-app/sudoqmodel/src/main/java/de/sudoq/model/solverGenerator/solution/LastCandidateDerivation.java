package de.sudoq.model.solverGenerator.solution;

import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;

/**
 * Created by timo on 04.10.16.
 */
public class LastCandidateDerivation extends SolveDerivation {

    private Position   pos;
    private int        remainingNote;


    public LastCandidateDerivation(HintTypes technique, Position pos, int remainingNote) {
        super(technique);
        this.pos = pos;
        this.remainingNote = remainingNote;
    }

    public Position getPosition() {
        return pos;
    }

}
