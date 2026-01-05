/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
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
import de.sudoq.model.sudoku.Position
import de.sudoq.view.SudokuLayout

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 *
 * @property position Position of the cell represented by this View
 */
class HighlightedCellView(
    context: Context, sl: SudokuLayout,
    private val position: Position, color: Int
) : View(context) {
    /* Attributes */

    /**
     * Color of the margin
     */
    private val marginColor: Int = color
    private val sl: SudokuLayout = sl
    private val paint = Paint()
    private val oval = RectF()
    var style: Paint.Style
    /* Methods */
    /**
     * Draws the content of the cell on the canvas of this SudokuCellView.
     * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
     * Canvas Objekt vorzunehmen.
     *
     * @param canvas Das Canvas Objekt auf das gezeichnet wird
     * @throws IllegalArgumentException Wird geworfen, falls das übergebene Canvas null ist
     */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Todo use canvas.drawRoundRect();
        drawNewMethod(position, canvas, marginColor) //red
    }

    private fun drawOldMethod(p: Position, canvas: Canvas) {
        val edgeRadius = sl.currentCellViewSize / 20.0f
        paint.reset()
        val thickness = 10
        paint.strokeWidth = (thickness * sl.currentSpacing).toFloat()


        //deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
        val cellSizeAndSpacing = sl.currentCellViewSize + sl.currentSpacing
        /* these first 4 seem similar. drawing the black line around?*/
        /* cells that touch the edge: Paint your edge but leave space at the corners*/paint.reset()
        paint.strokeWidth = (thickness * sl.currentSpacing).toFloat()
        paint.color = marginColor
        val leftX: Float =
            (sl.currentLeftMargin + p.x * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        val rightX: Float =
            (sl.currentLeftMargin + (p.x + 1) * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        val topY: Float =
            (sl.currentTopMargin + p.y * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        val bottomY: Float =
            (sl.currentTopMargin + (p.y + 1) * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()

        /* left edge */
        val startY: Float = sl.currentTopMargin + p.y * cellSizeAndSpacing + edgeRadius
        val stopY: Float =
            sl.currentTopMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - sl.currentSpacing
        canvas.drawLine(leftX, startY, leftX, stopY, paint)

        /* right edge */canvas.drawLine(rightX, startY, rightX, stopY, paint)

        /* top edge */
        val startX: Float = sl.currentLeftMargin + p.x * cellSizeAndSpacing + edgeRadius
        val stopX: Float =
            sl.currentLeftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - sl.currentSpacing
        canvas.drawLine(startX, topY, stopX, topY, paint)

        /* bottom edge */canvas.drawLine(startX, bottomY, stopX, bottomY, paint)


        /* Cells at corners of their block draw a circle for a round circumference*/paint.style =
            Paint.Style.FILL_AND_STROKE
        val radius = edgeRadius + sl.currentSpacing / 2
        val angle = (90 + 10).toShort()
        /*TopLeft*/
        var centerX: Float = sl.currentLeftMargin + p.x * cellSizeAndSpacing + edgeRadius
        var centerY: Float = sl.currentTopMargin + p.y * cellSizeAndSpacing + edgeRadius
        oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
        canvas.drawArc(oval, (180 - 5).toFloat(), angle.toFloat(), false, paint)

        /* Top Right*/centerX =
            sl.currentLeftMargin + (p.x + 1) * cellSizeAndSpacing - sl.currentSpacing - edgeRadius
        centerY = sl.currentTopMargin + p.y * cellSizeAndSpacing + edgeRadius
        oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
        canvas.drawArc(oval, (270 - 5).toFloat(), angle.toFloat(), false, paint)

        /*Bottom Left*/centerX = sl.currentLeftMargin + p.x * cellSizeAndSpacing + edgeRadius
        centerY =
            sl.currentTopMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - sl.currentSpacing
        oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
        canvas.drawArc(oval, (90 - 5).toFloat(), angle.toFloat(), false, paint)

        /*BottomRight*/centerX =
            sl.currentLeftMargin + (p.x + 1) * cellSizeAndSpacing - edgeRadius - sl.currentSpacing
        centerY =
            sl.currentTopMargin + (p.y + 1) * cellSizeAndSpacing - edgeRadius - sl.currentSpacing
        oval[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
        canvas.drawArc(oval, (0 - 5).toFloat(), angle.toFloat(), false, paint)
    }

    private fun drawNewMethod(p: Position, canvas: Canvas, color: Int) {
        val edgeRadius = sl.currentCellViewSize / 20.0f
        paint.reset()
        paint.color = color
        paint.style = Paint.Style.STROKE
        val thickness = 10
        paint.strokeWidth = (thickness * sl.currentSpacing).toFloat()
        val cellSizeAndSpacing = sl.currentCellViewSize + sl.currentSpacing
        val left =
            (sl.currentLeftMargin + p.x * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        val top = (sl.currentTopMargin + p.y * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        val right =
            (sl.currentLeftMargin + (p.x + 1) * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        val bottom =
            (sl.currentTopMargin + (p.y + 1) * cellSizeAndSpacing - sl.currentSpacing / 2).toFloat()
        canvas.drawRoundRect(
            RectF(left, top, right, bottom),
            edgeRadius + sl.currentSpacing / 2,
            edgeRadius + sl.currentSpacing / 2,
            paint
        )
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
                canvas.drawText(
                    note + "",
                    (i % Symbol.getInstance()
                        .getRasterSize() * noteTextSize + noteTextSize / 2).toFloat(),
                    (i / Symbol.getInstance()
                        .getRasterSize() * noteTextSize + noteTextSize).toFloat(), notePaint
                )
            }
        }
    }
    /* Constructors */ /**
     * Creates a SudokuCellView
     *
     * @param context    the application context
     * @param sl         a sudokuLayout
     * @param position   cell represented
     * @param color      Color of the margin
     */
    init {
        paint.color = marginColor
        val thickness = 10
        paint.strokeWidth = (thickness * sl.currentSpacing).toFloat()
        style = paint.style
    }
}