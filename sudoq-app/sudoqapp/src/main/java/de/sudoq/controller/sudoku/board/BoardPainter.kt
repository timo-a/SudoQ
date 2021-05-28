package de.sudoq.controller.sudoku.board

import android.graphics.Canvas
import android.graphics.Paint
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.view.SudokuLayout

/**
 * Created by timo on 13.10.16.
 */
class BoardPainter(var sl: SudokuLayout, var type: SudokuType) {
    fun paintBoard(paint: Paint, canvas: Canvas, edgeRadius: Float) {
        type.filter { it.type == ConstraintType.BLOCK } //for every constraint which is a Block
            .forEach { outlineConstraint(it, canvas, edgeRadius, paint) } //paint the outline
    }

    /* highlighted constraint has a more intuitive version, update sometime */
    private fun outlineConstraint(c: Constraint, canvas: Canvas, edgeRadius: Float, paint: Paint) {
        val topMargin = sl.currentTopMargin
        val leftMargin = sl.currentLeftMargin
        val spacing = sl.currentSpacing
        for (p in c) {
            /* determine whether the position p is in the (right|left|top|bottom) border of its block constraint.
            * test for 0 to avoid illegalArgExc for neg. vals
            * careful when trying to optimize this definition: blocks can be squiggly (every additional compound to row/col but extra as in hypersudoku is s.th. different)
            */
            val isLeft = p.x == 0 || !c.includes(Position[p.x - 1, p.y])
            val isRight = !c.includes(Position[p.x + 1, p.y])
            val isTop = p.y == 0 || !c.includes(Position[p.x, p.y - 1])
            val isBottom = !c.includes(Position[p.x, p.y + 1])
            // (0,0) is in the top left
            for (i in 1..spacing) { //?
                //deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
                val cellSizeAndSpacing = sl.currentCellViewSize + spacing
                /* these first 4 seem similar. drawing the black line around?*/
                /* cells that touch the edge: Paint your edge but leave space at the corners*/
                //paint.setColor(Color.GREEN);
                if (isLeft) {
                    val x = (leftMargin + p.x * cellSizeAndSpacing - i).toFloat()
                    val startY = topMargin + p.y * cellSizeAndSpacing + edgeRadius
                    val stopY = topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing
                    canvas.drawLine(x, startY, x, stopY, paint)
                }
                if (isRight) {
                    val x = (leftMargin + (p.x + 1) * cellSizeAndSpacing - spacing - 1 + i).toFloat()
                    val startY = topMargin + p.y * cellSizeAndSpacing + edgeRadius
                    val stopY = topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing
                    canvas.drawLine(x, startY, x, stopY, paint)
                }
                if (isTop) {
                    val startX = leftMargin + p.x * cellSizeAndSpacing + edgeRadius
                    val stopX = leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing
                    val y = (topMargin + p.y * cellSizeAndSpacing - i).toFloat()
                    canvas.drawLine(startX, y, stopX, y, paint)
                }
                if (isBottom) {
                    val startX = leftMargin + p.x * cellSizeAndSpacing + edgeRadius
                    val stopX = leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing
                    val y = (topMargin + (p.y + 1) * cellSizeAndSpacing - spacing - 1 + i).toFloat()
                    canvas.drawLine(startX, y, stopX, y, paint)
                }

                /* Cells at corners of their block draw a circle for a round circumference*/
                /*TopLeft*/
                if (isLeft && isTop) {
                    //paint.setColor(Color.MAGENTA);
                    canvas.drawCircle(
                            leftMargin + p.x * cellSizeAndSpacing + edgeRadius,  //center-x
                            topMargin + p.y * cellSizeAndSpacing + edgeRadius,  //center-y
                            edgeRadius + i,  //radius
                            paint)
                }

                /* Top Right*/
                if (isRight && isTop) {
                    //paint.setColor(Color.BLUE);
                    canvas.drawCircle(
                            leftMargin + (p.x + 1) * cellSizeAndSpacing - spacing - edgeRadius,
                            topMargin + p.y * cellSizeAndSpacing + edgeRadius,
                            edgeRadius + i,
                            paint)
                }

                /*Bottom Left*/
                if (isLeft && isBottom) {
                    //paint.setColor(Color.CYAN);
                    canvas.drawCircle(
                            leftMargin + p.x * cellSizeAndSpacing + edgeRadius,
                            topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                            edgeRadius + i,
                            paint)
                }

                /*BottomRight*/
                if (isRight && isBottom) {
                    //paint.setColor(Color.RED);
                    canvas.drawCircle(
                            leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                            topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                            edgeRadius + i,
                            paint)
                }


                //paint.setColor(Color.YELLOW);
                /*Now filling the edges (if there's no corner we still leave a gap. that gap is being filled now ) */
                val belowRightMember = c.includes(Position[p.x + 1, p.y + 1])
                /*For a cell on the right border, initializeWith edge to neighbour below
                 *
                 * !isBottom excludes:      corner to the left -> no neighbour directly below i.e. unwanted filling
                 *  3rd condition excludes: corner to the right-> member below right          i.e. unwanted filling
                 */
                if (isRight && !isBottom && !belowRightMember) {
                    canvas.drawLine((
                            leftMargin + (p.x + 1) * cellSizeAndSpacing - spacing - 1 + i).toFloat(),
                            topMargin + (p.y + 1) * cellSizeAndSpacing - spacing - edgeRadius, (
                            leftMargin + (p.x + 1) * cellSizeAndSpacing - spacing - 1 + i).toFloat(),
                            topMargin + (p.y + 1) * cellSizeAndSpacing + edgeRadius,
                            paint)
                }
                /*For a cell at the bottom, initializeWith edge to right neighbour */
                if (isBottom && !isRight && !belowRightMember) {
                    canvas.drawLine(
                            leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing, (
                            topMargin + (p.y + 1) * cellSizeAndSpacing - spacing - 1 + i).toFloat(),
                            leftMargin + (p.x + 1) * cellSizeAndSpacing + edgeRadius, (
                            topMargin + (p.y + 1) * cellSizeAndSpacing - spacing - 1 + i).toFloat(),
                            paint)
                }

                /*For a cell on the left border, initializeWith edge to upper neighbour*/
                if (isLeft && !isTop && (p.x == 0 || !c.includes(Position[p.x - 1, p.y - 1]))) {
                    canvas.drawLine((
                            leftMargin + p.x * cellSizeAndSpacing - i).toFloat(),
                            topMargin + p.y * cellSizeAndSpacing - spacing - edgeRadius, (
                            leftMargin + p.x * cellSizeAndSpacing - i).toFloat(),
                            topMargin + p.y * cellSizeAndSpacing + edgeRadius,
                            paint)
                }
                /*For a cell at the top initializeWith to the left*/
                if (isTop && !isLeft && (p.y == 0 || !c.includes(Position[p.x - 1, p.y - 1]))) {
                    canvas.drawLine(
                            leftMargin + p.x * cellSizeAndSpacing - edgeRadius - spacing, (
                            topMargin + p.y * cellSizeAndSpacing - i).toFloat(),
                            leftMargin + p.x * cellSizeAndSpacing + edgeRadius, (
                            topMargin + p.y * cellSizeAndSpacing - i).toFloat(),
                            paint)
                }
            }
        }
    }
}