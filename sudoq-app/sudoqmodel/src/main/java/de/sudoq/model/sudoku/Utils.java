package de.sudoq.model.sudoku;

/**
 * Created by timo on 28.04.16.
 */
public class Utils {

    public static Position positionToRealWorld(Position p){
        return new Position(p.getX()+1,
                            p.getY()+1);
    }

    public static int symbolToRealWorld(int symbol){
        if(symbol<0)
            throw new IllegalArgumentException("Symbol is below 0, so there is no real world equivalent");
        return symbol+1;
    }

}
