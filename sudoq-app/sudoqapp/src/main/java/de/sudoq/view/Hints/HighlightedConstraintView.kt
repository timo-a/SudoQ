/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.Hints

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import de.sudoq.controller.sudoku.Symbol
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import de.sudoq.view.SudokuLayout

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 */
class HighlightedConstraintView(context: Context, sl: SudokuLayout,
                                /** The Constraint represented by this View */
                                private val constraint: Constraint, color: Int) : View(context) {

    /**
     * Color of the margin
     */
    private val marginColor: Int = color
    private val sl: SudokuLayout = sl
    private val paint = Paint()
    private val oval = RectF()

    /**
     * Zeichnet den Inhalt des Feldes auf das Canvas dieses SudokuCellViews.
     * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
     * Canvas Objekt vorzunehmen.
     *
     * @param canvas Das Canvas Objekt auf das gezeichnet wird
     * @throws IllegalArgumentException Wird geworfen, falls das übergebene Canvas null ist
     */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val edgeRadius = sl.currentCellViewSize / 20.0f
        paint.reset()
        paint.color = marginColor
        val thickness = 10
        paint.strokeWidth = (thickness * sl.currentSpacing).toFloat()
        val c = constraint
        //Log.d("HighlightCV", "This is happening!");

        //canvas.drawLine(0, 0, 600, 600, paint);
        val topMargin = sl.currentTopMargin
        val leftMargin = sl.currentLeftMargin
        val spacing = sl.currentSpacing
        for (p in c) {
            /* determine whether the position p is in the (right|left|top|bottom) border of its block constraint.
					 * test for 0 to avoid illegalArgExc for neg. vals
					 * careful when trying to optimize this definition: blocks can be squiggly (every additional compound to row/col but extra as in hypersudoku is s.th. different)
					 * */
            val isLeft = p.x == 0 || !c.includes(Position[p.x - 1, p.y])
            val isRight = !c.includes(Position[p.x + 1, p.y])
            val isTop = p.y == 0 || !c.includes(Position[p.x, p.y - 1])
            val isBottom = !c.includes(Position[p.x, p.y + 1])
            // (0,0) is in the top right


            //deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
            val cellSizeAndSpacing = sl.currentCellViewSize + spacing
            /* these first 4 seem similar. drawing the black line around?*/
            /* cells that touch the edge: Paint your edge but leave space at the corners*/
            //paint.setColor(Color.GREEN);
            val leftX = (leftMargin + p.x * cellSizeAndSpacing - spacing / 2).toFloat()
            val rightX = (leftMargin + (p.x + 1) * cellSizeAndSpacing - spacing / 2).toFloat()
            val topY = (topMargin + p.y * cellSizeAndSpacing - spacing / 2).toFloat()
            val bottomY = (topMargin + (p.y + 1) * cellSizeAndSpacing - spacing / 2).toFloat()
            if (isLeft) {
                val startY = topMargin + p.y * cellSizeAndSpacing + edgeRadius
                val stopY = topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing
                canvas.drawLine(leftX, startY, leftX, stopY, paint)
            }
            if (isRight) {
                val startY = topMargin + p.y * cellSizeAndSpacing + edgeRadius
                val stopY = topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing
                canvas.drawLine(rightX, startY, rightX, stopY, paint)
            }
            if (isTop) {
                val startX = leftMargin + p.x * cellSizeAndSpacing + edgeRadius
                val stopX = leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing
                canvas.drawLine(startX, topY, stopX, topY, paint)
            }
            if (isBottom) {
                val startX = leftMargin + p.x * cellSizeAndSpacing + edgeRadius
                val stopX = leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing
                canvas.drawLine(startX, bottomY, stopX, bottomY, paint)
            }

            /* Cells at corners of their block draw a circle for a round circumference*/paint.style = Paint.Style.FILL_AND_STROKE
            val radius = edgeRadius + spacing / 2
            val angle = (90 + 10).toShort()
            /*TopLeft*/if (isLeft && isTop) {
                val centerX = leftMargin + p.x * cellSizeAndSpacing + edgeRadius
                val centerY = topMargin + p.y * cellSizeAndSpacing + edgeRadius
                oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
                canvas.drawArc(oval, (180 - 5).toFloat(), angle.toFloat(), false, paint)
            }

            /* Top Right*/if (isRight && isTop) {
                val centerX = leftMargin + (p.x + 1) * cellSizeAndSpacing - spacing - edgeRadius
                val centerY = topMargin + p.y * cellSizeAndSpacing + edgeRadius
                oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
                canvas.drawArc(oval, (270 - 5).toFloat(), angle.toFloat(), false, paint)
            }

            /*Bottom Left*/if (isLeft && isBottom) {
                val centerX = leftMargin + p.x * cellSizeAndSpacing + edgeRadius
                val centerY = topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing
                oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
                canvas.drawArc(oval, (90 - 5).toFloat(), angle.toFloat(), false, paint)
            }

            /*BottomRight*/if (isRight && isBottom) {
                val centerX = leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing
                val centerY = topMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - spacing
                oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
                canvas.drawArc(oval, (0 - 5).toFloat(), angle.toFloat(), false, paint)
            }
            paint.color = marginColor

            /*Now filling the edges (if there's no corner we still leave a gap. that gap is being filled now ) */
            val belowRightMember = c.includes(Position[p.x + 1, p.y + 1])
            /*For a cell on the right border, initializeWith edge to neighbour below
					 *
					 * !isBottom excludes:      corner to the left -> no neighbour directly below i.e. unwanted filling
					 *  3rd condition excludes: corner to the right-> member below right          i.e. unwanted filling
					 *
					 * */
            /*  */if (isRight && !isBottom && !belowRightMember) {
                canvas.drawLine(
                        rightX,
                        topMargin + (p.y + 1) * cellSizeAndSpacing - spacing - edgeRadius,
                        rightX,
                        topMargin + (p.y + 1) * cellSizeAndSpacing + edgeRadius,
                        paint)
            }
            /*For a cell at the bottom, initializeWith edge to right neighbour */if (isBottom && !isRight && !belowRightMember) {
                canvas.drawLine(
                        leftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - spacing,
                        bottomY,
                        leftMargin + (p.x + 1) * cellSizeAndSpacing + edgeRadius,
                        bottomY,
                        paint)
            }
            /*For a cell on the left border, initializeWith edge to upper neighbour*/if (isLeft && !isTop && (p.x == 0 || !c.includes(Position[p.x - 1, p.y - 1]))) {
                canvas.drawLine(
                        leftX, topMargin + p.y * cellSizeAndSpacing - spacing - edgeRadius, leftX, topMargin + p.y * cellSizeAndSpacing + edgeRadius, paint)
            }

            /*For a cell at the top initializeWith to the left*/if (isTop && !isLeft && (p.y == 0 || !c.includes(Position[p.x - 1, p.y - 1]))) {
                canvas.drawLine(leftMargin + p.x * cellSizeAndSpacing - edgeRadius - spacing, topY, leftMargin + p.x * cellSizeAndSpacing + edgeRadius, topY, paint
                )
            }
        }

        /* uncomment to paint focuspoint of zooming
		paint.setStyle(Paint.Style.FILL);
		float z = sl.getZoomFactor();
		canvas.drawCircle(sl.focusX * z
		                 ,sl.focusY * z , z * 20, paint);
		paint.reset();*/
    }

    /** TODO may come in handy later for highlighting notes. or do that seperately
     * Zeichnet die Notizen in dieses Feld
     *
     * @param canvas
     * Das Canvas in das gezeichnet werde nsoll
     *
     * @param cell
     * Das Canvas in das gezeichnet werde nsoll
     */
    private fun drawNotes(canvas: Canvas, cell: Cell) {
        val notePaint = Paint()
        notePaint.isAntiAlias = true
        val noteTextSize = height / Symbol.getInstance().getRasterSize()
        notePaint.textSize = noteTextSize.toFloat()
        notePaint.textAlign = Paint.Align.CENTER
        notePaint.color = Color.BLACK
        for (i in 0 until Symbol.getInstance().getNumberOfSymbols()) {
            if (cell.isNoteSet(i)) {
                val note = Symbol.getInstance().getMapping(i)
                canvas.drawText(note + "",
                        (i % Symbol.getInstance().getRasterSize() * noteTextSize + noteTextSize / 2).toFloat(),
                        (i / Symbol.getInstance().getRasterSize() * noteTextSize + noteTextSize).toFloat(), notePaint)
            }
        }
    }
}