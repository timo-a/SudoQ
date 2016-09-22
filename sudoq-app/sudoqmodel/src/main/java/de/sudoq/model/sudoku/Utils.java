package de.sudoq.model.sudoku;

import java.util.List;

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

    //Todo return enum probably prettier
    //This is bad for localization
    /*
    * analyses whether the list of positions constitutes a roe/col/diag/block
    * TODO currently only tests for alignment but not continuity!
    * */
    public static String classifyGroup(List<Position> pl){
        assert pl.size() >=2;

        switch (getGroupShape(pl)) {
            case Row:
                return "row " + (pl.get(0).getY() + 1);
            case Column:
                return "col " + (pl.get(0).getX() + 1);
            case Diagonal:
                return "a diagonal";
            default:
                return "a block containing (" + positionToRealWorld(pl.get(0)) + ")";
        }
    }

    /**
     *
     * */
    public static ConstraintShape getGroupShape(List<Position> pList) {
        if     (isRow(pList))
            return ConstraintShape.Row;
        else if(isColumn(pList))
            return ConstraintShape.Column;
        else if(isDiagonal(pList))
            return ConstraintShape.Diagonal;
        else
            return ConstraintShape.Block;
    }

    public enum ConstraintShape {
        Row,Column,Diagonal,Block,Other


    }

    public static Boolean isRow(List<Position> list){
        assert list.size() >=2;

        boolean row = true;

        int ycoord = list.get(0).getY();
        for (Position pos : list)
            if (pos.getY() != ycoord)
                row=false;

        return row;
    }

    public static Boolean isColumn(List<Position> list){
        assert list.size() >=2;

        boolean col = true;

        int xcoord = list.get(0).getX();
        for (Position pos : list)
            if (pos.getX() != xcoord)
                col=false;

        return col;
    }

    public static Boolean isDiagonal(List<Position> list){
        assert list.size() >=2;

        boolean diag = true;

        Position diff = list.get(0).distance(list.get(1)); //gradient = diff-vector
        Position reference = list.get(1);
        for (int i = 2; i < list.size(); i++){
            Position d = reference.distance(list.get(i));
            if(Math.abs(d.getX() * diff.getY()) != Math.abs(diff.getX() * d.getY())) //ratio comparison trick: a/b==c/d <=> a*d == b*c, abs for 180° difference
                diag=false;
        }

        return diag;
    }
}
