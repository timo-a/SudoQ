package de.sudoq.model.solvingAssistant;

import java.util.List;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;

/**
 * Created by timo on 28.04.16.
 */
public class SolveActionLastDigit extends SolveAction {
    public SolveActionLastDigit(Position target, int solution, List<Position> list){
        Position rwPosition = Utils.positionToRealWorld(target);
        int      rwSolution = Utils.symbolToRealWorld(solution);
        /* are all x values the same? */
        int xPo = list.get(0).getX();
        boolean xSame = true;
        for (Position p: list)
            if (p.getX() != xPo) {
                xSame = false;
                break;
            }
        /* are all Y values the same? */
        int yPo = list.get(0).getY();
        boolean ySame = true;
        for (Position p: list)
            if (p.getY() != yPo) {
                ySame = false;
                break;
            }

        String structure = xSame ? "column":
                           ySame ? "row":
                                   "block";

        message = "Field at row "+ rwPosition.getX() +", column "+ rwPosition.getY() + " must be " + rwSolution + ", because all other fields in it's "+structure+" are occupied";
    }
}
