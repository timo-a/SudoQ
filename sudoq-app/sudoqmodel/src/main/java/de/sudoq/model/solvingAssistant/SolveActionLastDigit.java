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
        boolean xSame = true;
        int xPo = list.get(0).getX();
        for (Position p: list)
            if(p.getX()!=xPo)
                xSame = false;
        /* are all Y values the same? */
        boolean ySame = true;
        int yPo = list.get(0).getY();
        for (Position p: list)
            if(p.getY()!=yPo)
                ySame = false;

        String structure = xSame ? "column":
                           ySame ? "row":
                                   "block";

        message = "Field at row "+ rwPosition.getX() +", column "+ rwPosition.getY() + " must be " + rwSolution + ", because all other fields in it's "+structure+" are occupied";
    }
}
