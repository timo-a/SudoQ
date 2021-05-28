/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
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
import android.view.View
import de.sudoq.controller.sudoku.CellInteractionListener
import de.sudoq.controller.sudoku.ObservableCellInteraction
import de.sudoq.controller.sudoku.Symbol
import de.sudoq.controller.sudoku.board.CellViewPainter
import de.sudoq.controller.sudoku.board.CellViewStates
import de.sudoq.model.ModelChangeListener
import de.sudoq.model.game.Game
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import java.util.*

/**
 * This subclass of a View represents a cell in a sudoku. It extends the functionality of the
 * Android View by user interaction and coloring in.
 *
 * @property cell The cell associated with this view
 * @property markWrongSymbol should wrong symbols be highlighted
 */
class SudokuCellView(context: Context?, game: Game, val cell: Cell, private val markWrongSymbol: Boolean)
    : View(context), ModelChangeListener<Cell>, ObservableCellInteraction {

    /**
     * List of the  selektion listeners
     */
    private val cellSelectListener: ArrayList<CellInteractionListener>

    /**
     * A flag defining whether note mode is active
     */
    var isNoteMode: Boolean
        private set

    /**
     * A list of cells, that are semantically connected to this one and should be highlighted if
     * this cell is selected
     */
    private val connectedCells: ArrayList<SudokuCellView>

    /**
     * The symbol this cell is filled with
     */
    private var symbol: String

    /**
     * Indicates if this cell is currently selected
     */
    private var cellSelected: Boolean
    //parent class already defines property 'selected' so we need to call our custom one 'cellSelected'

    /**
     * Indicates if this cell is connected to the one currently selected
     */
    private var connected: Boolean

    /**
     * Indicates if this cell is part of an extraConstraint
     */
    private var isInExtraConstraint: Boolean

    /**
     * The game associated with this view
     */
    private val game: Game
    /* Methods */
    /**
     * Draws the content of the associated cell on the canvas of this SudokuCellView.
     * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
     * Canvas Objekt vorzunehmen.
     *
     * @param canvas
     * canvas object on which to draw
     * @throws IllegalArgumentException
     * if canvas is null TODO really??
     */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Log.d(LOG_TAG, "SudokuFieldView.onDraw()");
        symbol = Symbol.getInstance().getMapping(cell.currentValue)
        CellViewPainter.instance.markCell(canvas, this, symbol, false, isInExtraConstraint && !cellSelected)

        // Draw notes if cell has no value
        if (cell.isNotSolved) {
            drawNotes(canvas)
        }
    }

    /**
     * draws notes into the cell
     *
     * @param canvas
     * the canvas on which to draw
     */
    private fun drawNotes(canvas: Canvas) {
        val notePaint = Paint()
        notePaint.isAntiAlias = true
        val noteTextSize = height / Symbol.getInstance().rasterSize
        notePaint.textSize = noteTextSize.toFloat()
        notePaint.textAlign = Paint.Align.CENTER
        notePaint.color = Color.BLACK
        for (i in 0 until Symbol.getInstance().numberOfSymbols) {
            if (cell.isNoteSet(i)) {
                val note = Symbol.getInstance().getMapping(i)
                canvas.drawText(note + "", (
                        i % Symbol.getInstance().rasterSize * noteTextSize + noteTextSize / 2).toFloat(), (
                        i / Symbol.getInstance().rasterSize * noteTextSize + noteTextSize).toFloat(),
                        notePaint)
            }
        }
    }

    /**
     * notifyListener(); {@inheritDoc}
     */
    override fun onModelChanged(obj: Cell) {
        for (listener in cellSelectListener) {
            listener.onCellChanged(this)
        }
        updateMarking()
    }
    /**
     * Diese Methode verarbeitet alle Touch Inputs, die der Benutzer macht und
     * leitet sie an den ShowViewListener weiter.
     *
     * @param touchEvent
     * Das TouchEvent das von der API kommt und diese Methode
     * aufgerufen hat
     * @return true falls das TouchEvent behandelt wurde, false falls nicht
     * @throws IllegalArgumentException
     * Wird geworfen, falls das übergebene MotionEvent null ist
     */
    /*@Override
	public boolean onTouchEvent(MotionEvent touchEvent) {
		for (CellInteractionListener listener : cellSelectListener) {
			listener.onCellSelected(this);
		}

		return false;
	}*/
    /**
     * Setzt den Notizstatus gemäß des Parameters
     *
     * @param state
     * true, um den Notizmodus ein-, bzw. false um ihn auszuschalten
     */
    fun setNoteState(state: Boolean) {
        isNoteMode = state
        updateMarking()
    }

    /**
     * Adds the passed SudokuCellView as connected to this cell so that selecting this cell will
     * highlight it as well. If it is null, nothing will happen.
     *
     * @param view
     * the View to connect with this one
     */
    fun addConnectedCell(view: SudokuCellView?) {
        if (view != null && !connectedCells.contains(view)) {
            connectedCells.add(view)
        }
    }

    /**
     * Sets this View as selected.
     *
     * @param markConnected
     * Determines if cells connected to this cell (row / column) should be highlighted
     */
    fun select(markConnected: Boolean) {
        if (game.isFinished()) {
            connected = true
        } else {
            cellSelected = true
        }
        if (markConnected) {
            for (f in connectedCells) {
                f.markConnected()
            }
        }
        updateMarking()
    }

    /**
     * Resets highlighting for this and connected cellViews.
     *
     * @param updateConnected
     * determines if connected cells should also be reset.
     */
    fun deselect(updateConnected: Boolean) {
        cellSelected = false
        connected = false
        if (updateConnected) {
            for (fv in connectedCells) {
                fv.deselect(false)
            }
        }
        updateMarking()
    }

    /**
     * Highlights this cell as connected with the currently selected.
     */
    fun markConnected() {
        connected = true
        updateMarking()
    }

    /**
     * Updates the highlighting of this CellView
     */
    private fun updateMarking() {
        val editable = cell.isEditable
        //TODO no idea what 'wrong' is doing, i just etracted it for clarity
        val wrong = markWrongSymbol && !cell.isNotWrong && checkConstraint()
        val state: CellViewStates =
                if (connected)
                    if (editable)
                        if (wrong) CellViewStates.CONNECTED_WRONG
                        else CellViewStates.CONNECTED
                    else CellViewStates.SELECTED_FIXED
                else if (cellSelected)
                    if (editable)
                        if (isNoteMode)
                            if (wrong)
                                CellViewStates.SELECTED_NOTE_WRONG
                            else CellViewStates.SELECTED_NOTE
                        else if (wrong) CellViewStates.SELECTED_INPUT_WRONG
                        else CellViewStates.SELECTED_INPUT
                    else CellViewStates.SELECTED_FIXED
                else if (editable)
                    if (wrong) CellViewStates.DEFAULT_WRONG
                    else CellViewStates.DEFAULT
                else CellViewStates.FIXED
        CellViewPainter.instance.setMarking(this, state)
        invalidate()
    }

    /**
     * Returns true if the value of this cell violates the Constraints.
     * Only UniqueConstraints are checked. If the cell is part of another constraint type false is
     * returned.
     *
     * @return true, if the value of this cell violates the UniqueConstraints or is part of another
     * ConstraintType, false otherwise
     */
    private fun checkConstraint(): Boolean {
        val constraints: Iterable<Constraint>? = game.sudoku!!.sudokuType
        val sudoku = game.sudoku
        for (c in constraints!!) {
            if (c.includes(sudoku!!.getPosition(cell.id)!!)) {
                if (c.hasUniqueBehavior()) {
                    for (pos in c.getPositions()) {
                        //if a different position has the same value
                        if (pos !== sudoku.getPosition(cell.id)
                                && sudoku.getCell(pos)!!.currentValue == cell.currentValue) {
                            return true
                        }
                    }
                } else {
                    return true //if no unique-constraint -> automatically satisfied
                }
            }
        }
        return false
    }

    override fun registerListener(listener: CellInteractionListener) {
        cellSelectListener.add(listener)
    }

    override fun removeListener(listener: CellInteractionListener) {}

    /**
     * Notifies all registered listeners about interaction with this SudokuCellView.
     */
    /*public void notifyListener() {
		for (CellInteractionListener listener : cellSelectListener) {
			listener.onCellSelected(this);
		}
	}*/
    companion object {
        /* Attributes */
        private val LOG_TAG = SudokuCellView::class.java.simpleName
    }
    /* Constructors */ /**
     * Instantiates a SudokuCellView.
     *
     * @param context
     * the application context
     * @param game
     * the game this view is a part of
     * @param cell
     * The cell represented by this SudokuCellView
     * @param markWrongSymbol
     * indicates if an incorrect value should be highlighted
     * eoll
     * @throws IllegalArgumentException
     * if one of the arguments is null
     */
    init {
        symbol = Symbol.getInstance().getMapping(cell.currentValue)
        this.game = game
        cellSelectListener = ArrayList()
        connectedCells = ArrayList()
        cellSelected = false
        connected = false
        isNoteMode = false
        isInExtraConstraint = false
        val constraints: Iterable<Constraint>? = game.sudoku!!.sudokuType
        for (c in constraints!!) {
            if (c.type == ConstraintType.EXTRA &&
                    c.includes(game.sudoku!!.getPosition(cell.id)!!)) {
                isInExtraConstraint = true
                break
            }
        }
        updateMarking()
        setOnClickListener(object : OnClickListener {
            override fun onClick(v: View) {
                for (listener in cellSelectListener) listener.onCellSelected(scv, CellInteractionListener.SelectEvent.Short)
            }

            //this is just to pass `this`
            var scv: SudokuCellView? = null
            fun init(scv: SudokuCellView?): OnClickListener {
                this.scv = scv
                return this
            }
        }.init(this))
        setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(v: View): Boolean {
                for (listener in cellSelectListener)
                    listener.onCellSelected(scv, CellInteractionListener.SelectEvent.Long)
                return true
            }

            //this is just to pass `this`
            var scv: SudokuCellView? = null
            fun init(scv: SudokuCellView?): OnLongClickListener {
                this.scv = scv
                return this
            }
        }.init(this))
    }
}