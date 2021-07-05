package de.sudoq.model.sudoku

import kotlin.math.abs

//fun oneplusone (): Int = 2

/**
 * Determines the group shape from a constraint holding the positions
 * @param c the constraint
 * @return shape of constraint as enum
 */
fun getGroupShape(c: Constraint): Utils.ConstraintShape {
    return getGroupShape(c.getPositions())
}


fun getGroupShape(pList: List<Position>): Utils.ConstraintShape {
    return when {
        Utils.isRow(pList) -> Utils.ConstraintShape.Row
        Utils.isColumn(pList) -> Utils.ConstraintShape.Column
        Utils.isDiagonal(pList) -> Utils.ConstraintShape.Diagonal
        else -> Utils.ConstraintShape.Block
    }
}


/**
 * Utils class todo kotlin doesn't need functions to be in a class -> refactor
 */
object Utils {
    @JvmStatic
    fun positionToRealWorld(p: Position): Position {
        return Position(
            p.x + 1,
            p.y + 1
        )
    }

    @JvmStatic
    fun symbolToRealWorld(symbol: Int): Int {
        require(symbol >= 0) { "Symbol is below 0, so there is no real world equivalent" }
        return symbol + 1
    }

    //Todo return enum probably prettier
    //This is bad for localization
    /*
    * analyses whether the list of positions constitutes a roe/col/diag/block
    * TODO currently only tests for alignment but not continuity!
    * */
    @JvmStatic
    fun classifyGroup(pl: List<Position>): String {
        assert(pl.size >= 2)
        return when (getGroupShape(pl)) {
            ConstraintShape.Row -> "row " + (pl[0].y + 1)
            ConstraintShape.Column -> "col " + (pl[0].x + 1)
            ConstraintShape.Diagonal -> "a diagonal"
            else -> "a block containing (" + positionToRealWorld(pl[0]) + ")"
        }
    }


    fun isRow(list: List<Position>): Boolean {
        assert(list.size >= 2)
        val ycoord = list[0].y
        for (pos in list) if (pos.y != ycoord) return false
        return true
    }

    fun isColumn(list: List<Position>): Boolean {
        assert(list.size >= 2)
        val xcoord = list[0].x
        for (pos in list) if (pos.x != xcoord) return false
        return true
    }

    fun isDiagonal(list: List<Position>): Boolean {
        assert(list.size >= 2)
        var diag = true
        val diff = list[0].distance(list[1]) //gradient = diff-vector
        val reference = list[1]
        for (i in 2 until list.size) {
            val d = reference.distance(list[i])
            if (abs(d.x * diff.y) != abs(diff.x * d.y)) //ratio comparison trick: a/b==c/d <=> a*d == b*c, abs for 180° difference
                diag = false
        }
        return diag
    }

    /** Shapes of the constraints as the user would classify them  */
    enum class ConstraintShape {
        Row, Column, Diagonal, Block, Other //Never change the order!!! string-arrays in the xml-values depend on it!
    }
}