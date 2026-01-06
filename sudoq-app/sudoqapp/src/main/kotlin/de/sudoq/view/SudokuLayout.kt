/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import de.sudoq.controller.sudoku.CellInteractionListener
import de.sudoq.controller.sudoku.ObservableCellInteraction
import de.sudoq.controller.sudoku.SudokuActivity
import de.sudoq.controller.sudoku.board.BoardPainter
import de.sudoq.controller.sudoku.board.CellViewPainter
import de.sudoq.controller.sudoku.hints.HintPainter
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.Game
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * Eine View als RealativeLayout, die eine Sudoku-Anzeige verwaltet.
 *
 * @param context Der Kontext, in dem diese View angezeigt wird

 */
class SudokuLayout(context: Context) : RelativeLayout(context), ObservableCellInteraction,
    ZoomableView {

    /**
     * Das Game, welches diese Anzeige verwaltet
     */
    private val game: Game = (context as SudokuActivity).game!!

    /**
     * Die Standardgröße eines Feldes
     */
    private var defaultCellViewSize: Int
    /**
     * Die aktuelle Größe eines Feldes
     */
    // private int currentCellViewSize;

    /**
     * Die aktuell ausgewählte CellView
     */
    var currentCellView: SudokuCellView? = null
    private var zoomFactor: Float
        private set

    /**
     * Ein Array aller CellViews
     */
    private var sudokuCellViews: Array<Array<SudokuCellView?>>? = null

    /**
     * Der linke Rand, verursacht durch ein zu niedriges Layout
     */
    private var leftMargin = 0

    /**
     * Der linke Rand, verursacht durch ein zu schmales Layout
     */
    private var topMargin = 0
    private val boardPainter: BoardPainter
    val hintPainter: HintPainter
    private val paint: Paint

    /**
     * Erstellt die Anzeige des Sudokus.
     * doesn't draw anything
     */
    private fun inflateSudoku() {
        Log.d(LOG_TAG, "SudokuLayout.inflateSudoku()")
        CellViewPainter.instance!!.flushMarkings()
        removeAllViews()
        val sudoku = game.sudoku
        val sudokuType = sudoku!!.sudokuType
        val isMarkWrongSymbolAvailable = game.isAssistanceAvailable(Assistances.markWrongSymbol)
        sudokuCellViews = Array(sudokuType.size.x + 1) { arrayOfNulls(sudokuType.size.y + 1) }
        for (p in sudokuType.validPositions) {
            val cell = sudoku.getCell(p)
            if (cell != null) {
                val x = p.x
                val y = p.y
                val params = LayoutParams(currentCellViewSize, defaultCellViewSize)
                params.topMargin = y * currentCellViewSize + y
                params.leftMargin = x * currentCellViewSize + x
                sudokuCellViews!![x][y] =
                    SudokuCellView(context, game, cell, isMarkWrongSymbolAvailable)
                cell.registerListener(sudokuCellViews!![x][y]!!)
                this.addView(sudokuCellViews!![x][y], params)
            }
        }
        val x = sudoku.sudokuType.size.x //why all this????
        val y = sudoku.sudokuType.size.y
        val params = LayoutParams(currentCellViewSize, defaultCellViewSize)
        params.topMargin = (y - 1) * currentCellViewSize + (y - 1) + currentTopMargin
        params.leftMargin = (x - 1) * currentCellViewSize + (x - 1) + currentLeftMargin
        sudokuCellViews!![x][y] = SudokuCellView(
            context,
            game,
            game.sudoku!!.getCell(Position[x - 1, y - 1])!!,
            isMarkWrongSymbolAvailable
        )
        this.addView(sudokuCellViews!![x][y], params)
        sudokuCellViews!![x][y]!!.visibility = INVISIBLE


        /* In case highlighting of current row and col is activated,
		   pass each pos its constraint-mates */if (game.isAssistanceAvailable(Assistances.markRowColumn)) {
            var positions: ArrayList<Position>
            val allConstraints: Iterable<Constraint> = game.sudoku!!.sudokuType
            for (c in allConstraints) if (c.type == ConstraintType.LINE) {
                positions = c.getPositions()
                for (i in positions.indices) for (k in i + 1 until positions.size) {
                    val fvI = getSudokuCellView(positions[i])
                    val fvK = getSudokuCellView(positions[k])
                    fvI.addConnectedCell(fvK)
                    fvK.addConnectedCell(fvI)
                }
            }
        }
        hintPainter.updateLayout()
        //Log.d(LOG_TAG, "SudokuLayout.inflateSudoku()-end");
    }

    /**
     * Berechnet das aktuelle Spacing (gem. dem aktuellen ZoomFaktor) und gibt
     * es zurück.
     *
     * @return Das aktuelle Spacing
     */
    val currentSpacing: Int
        get() = (spacing * zoomFactor).toInt()

    /**
     * Berechnet das aktuelle obere Margin (gem. dem aktuellen ZoomFaktor) und
     * gibt es zurück.
     *
     * @return Das aktuelle obere Margin
     */
    val currentTopMargin: Int
        get() = (topMargin * zoomFactor).toInt()

    /**
     * Berechnet das aktuelle linke Margin (gem. dem aktuellen ZoomFaktor) und
     * gibt es zurück.
     *
     * @return Das aktuelle linke Margin
     */
    val currentLeftMargin: Int
        get() = (leftMargin * zoomFactor).toInt()

    /**
     * Aktualisiert die Sudoku-Anzeige bzw. der enthaltenen Felder.
     */
    private fun refresh() {
        Log.d(LOG_TAG, "SudokuLayout.refresh()")
        if (sudokuCellViews != null) {
            val type = game.sudoku!!.sudokuType
            val typeSize = type!!.size
            val cellPlusSpacing = currentCellViewSize + currentSpacing
            //Iterate over all positions within the size 
            for (p in type.validPositions) {
                val params = getSudokuCellView(p).layoutParams as LayoutParams
                params.width = currentCellViewSize
                params.height = currentCellViewSize
                params.topMargin = currentTopMargin + p.y * cellPlusSpacing
                params.leftMargin = currentLeftMargin + p.x * cellPlusSpacing
                getSudokuCellView(p).layoutParams = params
                getSudokuCellView(p).invalidate()
            }
            //still not sure why we are doing this...
            val x = typeSize!!.x
            val y = typeSize.y
            //both x and y are over the limit. Why do we go there? we could just do it outside the loop, why was it ever put it in there?!
            val params = LayoutParams(currentCellViewSize, defaultCellViewSize)
            params.width = currentCellViewSize
            params.height = currentCellViewSize
            params.topMargin = 2 * currentTopMargin + (y - 1) * cellPlusSpacing
            params.leftMargin = 2 * currentLeftMargin + (x - 1) * cellPlusSpacing
            sudokuCellViews!![x][y]!!.layoutParams = params
            sudokuCellViews!![x][y]!!.invalidate()
            //end strange thing
        }
        hintPainter.updateLayout()
        invalidate()
        //Log.d(LOG_TAG, "SudokuLayout.refresh()-end");
    }

    /**
     * Draws all black borders for the sudoku, nothing else
     * Cells have to be drawn after this method
     * No insight on the coordinate-wise workings, unsure about the 'i's.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d(LOG_TAG, "SudokuLayout.onDraw()")
        val edgeRadius = currentCellViewSize / 20.0f
        paint.reset()
        paint.color = Color.BLACK
        boardPainter.paintBoard(paint, canvas, edgeRadius)
        hintPainter.invalidateAll()
    }

    var focusX = 0f
    var focusY = 0f

    /**
     * Zoom so heraus, dass ein diese View optimal in ein Layout der
     * spezifizierte Größe passt
     *
     * @param width
     * Die Breite auf die optimiert werden soll
     * @param height
     * Die Höhe auf die optimiert werden soll
     */
    fun optiZoom(width: Int, height: Int) {
        Log.d(LOG_TAG, "SudokuView height intern: " + this.measuredHeight)
        val sudokuType = game.sudoku!!.sudokuType
        val size = if (width < height) width else height
        val numberOfCells = if (width < height) sudokuType.size.x else sudokuType.size.y
        defaultCellViewSize = (size - (numberOfCells + 1) * spacing) / numberOfCells
        // this.currentCellViewSize = this.defaultCellViewSize;
        val cellSizeX =
            sudokuType.size.x * currentCellViewSize + (sudokuType.size.x - 1) * spacing
        val cellSizeY =
            sudokuType.size.y * currentCellViewSize + (sudokuType.size.y - 1) * spacing
        leftMargin = (width - cellSizeX) / 2
        topMargin = (height - cellSizeY) / 2
        Log.d(LOG_TAG, "Sudoku width: $width")
        Log.d(LOG_TAG, "Sudoku height: $height")
        refresh()
    }

    /**
     * Touch-Events werden nicht verarbeitet.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    /**
     * returns the CellView at Position p.
     */
    fun getSudokuCellView(p: Position): SudokuCellView {
        return sudokuCellViews!![p.x][p.y]!!
    }

    /**
     * Setzt den aktuellen Zoom-Faktor für diese View und refresh sie.
     *
     * @param factor
     * Der Zoom-Faktor
     */
    override fun zoom(factor: Float): Boolean {
        zoomFactor = factor
        //		//this.canvas.scale(factor,factor);
        refresh()
        //invalidate();
        return true
    }

    /**
     * Gibt die aktuelle Größe einer CellView zurück.
     *
     * @return die aktuelle Größe einer CellView
     */
    val currentCellViewSize: Int
        get() = (defaultCellViewSize * zoomFactor).toInt()

    /**
     * Unbenutzt.
     *
     * @throws UnsupportedOperationException
     * Wirft immer eine UnsupportedOperationException
     */
    fun notifyListener() {
        throw UnsupportedOperationException()
    }

    /**
     * {@inheritDoc}
     */
    override fun registerListener(listener: CellInteractionListener) {
        val sudokuType = game.sudoku!!.sudokuType
        for (p in sudokuType.validPositions) getSudokuCellView(p).registerListener(listener)
    }

    /**
     * {@inheritDoc}
     */
    override fun removeListener(listener: CellInteractionListener) {
        val sudokuType = game.sudoku!!.sudokuType
        for (p in sudokuType.validPositions) getSudokuCellView(p).removeListener(listener)
    }

    /**
     * {@inheritDoc}
     */
    override fun getMinZoomFactor(): Float {
        return 1.0f
    }

    /**
     * {@inheritDoc}
     */
    override fun getMaxZoomFactor(): Float {
        return 10f //this.game.getSudoku().getSudokuType().getSize().getX() / 2.0f;
    }

    companion object {
        /**
         * Das Log-Tag für den LogCat
         */
        private val LOG_TAG = SudokuLayout::class.java.simpleName

        /**
         * Der Platz zwischen 2 Blöcken
         */
        private const val spacing = 2
    }

    /**
     * Instanziiert eine neue SudokuView in dem spezifizierten Kontext.
     *
     */
    init {
        defaultCellViewSize = 40
        zoomFactor = 1.0f
        // this.currentCellViewSize = this.defaultCellViewSize;
        setWillNotDraw(false)
        paint = Paint()
        boardPainter = BoardPainter(this, game.sudoku!!.sudokuType)
        CellViewPainter.instance!!.setSudokuLayout(this)
        hintPainter = HintPainter(this)
        inflateSudoku()
        Log.d(LOG_TAG, "End of Constructor.")
    }
}