/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku.board

import android.graphics.*
import android.view.View
import de.sudoq.view.SudokuLayout
import java.util.*
import kotlin.collections.set

/**
 * This class is responsible for Animationens and highlighting of cells.
 * TODO does it have to be singleton?
 */
class CellViewPainter private constructor() {
    /* Attributes */
    /**
     * Maps a cell onto an Animation value that describes how to draw the cell
     */
    private val markings: Hashtable<View, CellViewStates>
    private var sl: SudokuLayout? = null
    fun setSudokuLayout(sl: SudokuLayout?) {
        this.sl = sl
    }
    /* Methods */
    /**
     * Bemalt das spezifizierte Canvas entsprechend der in der Hashtable für das
     * spezifizierte Feld eingetragenen Animation. Ist eines der beiden
     * Argumente null, so wird nichts getan.
     *
     * @param canvas
     * Das Canvas, welches bemalt werden soll
     * @param cell
     * Das Feld, anhand dessen Animation-Einstellung das Canvas
     * bemalt werden soll
     * @param symbol
     * Das Symbol das gezeichnet werden soll
     * @param justText
     * Definiert, dass nur Text geschrieben wird
     * @param darken
     * Verdunkelt das Feld
     */
    fun markCell(canvas: Canvas, cell: View, symbol: String, justText: Boolean, darken: Boolean) {
        val cellState = markings[cell]
        /*if(true){}else //to suppress celldrawing TODO remove again*/
        if (cellState != null && !justText) {
            when (cellState) {
                CellViewStates.SELECTED_INPUT_BORDER -> {
                    drawBackground(canvas, cell, Color.DKGRAY, true, darken)
                    drawInner(canvas, cell, Color.rgb(255, 100, 100), true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.SELECTED_INPUT -> {
                    drawBackground(canvas, cell, Color.rgb(255, 100, 100), true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.SELECTED_INPUT_WRONG -> {
                    drawBackground(canvas, cell, Color.rgb(255, 100, 100), true, darken)
                    drawText(canvas, cell, Color.RED, false, symbol)
                }
                CellViewStates.SELECTED_NOTE_BORDER -> {
                    drawBackground(canvas, cell, Color.DKGRAY, true, darken)
                    drawInner(canvas, cell, Color.YELLOW, true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.SELECTED_NOTE -> {
                    drawBackground(canvas, cell, Color.YELLOW, true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.SELECTED_NOTE_WRONG -> {
                    drawBackground(canvas, cell, Color.YELLOW, true, darken)
                    drawText(canvas, cell, Color.RED, false, symbol)
                }
                CellViewStates.SELECTED_FIXED -> {
                    drawBackground(canvas, cell, Color.rgb(220, 220, 255), true, darken)
                    drawText(canvas, cell, Color.rgb(0, 100, 0), true, symbol)
                }
                CellViewStates.CONNECTED -> {
                    drawBackground(canvas, cell, Color.rgb(220, 220, 255), true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.CONNECTED_WRONG -> {
                    drawBackground(canvas, cell, Color.rgb(220, 220, 255), true, darken)
                    drawText(canvas, cell, Color.RED, false, symbol)
                }
                CellViewStates.FIXED -> {
                    drawBackground(canvas, cell, Color.rgb(250, 250, 250), true, darken)
                    drawText(canvas, cell, Color.rgb(0, 100, 0), true, symbol)
                }
                CellViewStates.DEFAULT_BORDER -> {
                    drawBackground(canvas, cell, Color.DKGRAY, true, darken)
                    drawInner(canvas, cell, Color.rgb(250, 250, 250), true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.DEFAULT_WRONG -> {
                    drawBackground(canvas, cell, Color.rgb(250, 250, 250), true, darken)
                    drawText(canvas, cell, Color.RED, false, symbol)
                }
                CellViewStates.DEFAULT -> {
                    drawBackground(canvas, cell, Color.rgb(250, 250, 250), true, darken)
                    drawText(canvas, cell, Color.BLACK, false, symbol)
                }
                CellViewStates.CONTROLS -> drawBackground(canvas, cell, Color.rgb(40, 40, 40), false, darken)
                CellViewStates.KEYBOARD -> {
                    drawBackground(canvas, cell, Color.rgb(230, 230, 230), false, darken)
                    drawInner(canvas, cell, Color.rgb(40, 40, 40), false, darken)
                }
                CellViewStates.SUDOKU -> drawBackground(canvas, cell, Color.rgb(200, 200, 200), false, darken)
            }
        } else if (cellState != null) {
            when (cellState) {
                CellViewStates.SELECTED_INPUT_BORDER,
                CellViewStates.SELECTED_INPUT,
                CellViewStates.SELECTED_NOTE_BORDER,
                CellViewStates.SELECTED_NOTE,
                CellViewStates.CONNECTED,
                CellViewStates.DEFAULT_BORDER,
                CellViewStates.DEFAULT -> drawText(canvas, cell, Color.BLACK, false, symbol)
                CellViewStates.SELECTED_INPUT_WRONG,
                CellViewStates.SELECTED_NOTE_WRONG,
                CellViewStates.DEFAULT_WRONG,
                CellViewStates.CONNECTED_WRONG -> drawText(canvas, cell, Color.RED, false, symbol)
                CellViewStates.SELECTED_FIXED,
                CellViewStates.FIXED -> drawText(canvas, cell, Color.rgb(0, 100, 0), true, symbol)
            }
        }
        //Log.d("FieldPainter", "Field drawn");
        try {
            sl!!.hintPainter.invalidateAll() //invalidate();
        } catch (e: NullPointerException) {
            /*
			I don't see how this happens but a nullpointer exception was reported, so I made a try-catch-block here:
			reported at version 20
			This happens when 'gesture' is clicked in profile without playing a game first. sl is then null
			java.lang.NullPointerException:
			at de.sudoq.controller.sudoku.board.FieldViewPainter.markField (FieldViewPainter.java:182)
			at de.sudoq.view.VirtualKeyboardButtonView.onDraw (VirtualKeyboardButtonView.java:104)
		        at android.view.View.draw (View.java:17469)
			at android.view.View.updateDisplayListIfDirty (View.java:16464)
			at android.view.View.draw (View.java:17238)
			at android.view.ViewGroup.drawChild (ViewGroup.java:3921)
			at android.view.ViewGroup.dispatchDraw (ViewGroup.java:3711)
			at android.view.View.updateDisplayListIfDirty (View.java:16459)
			at android.view.View.draw (View.java:17238)
			at android.view.ViewGroup.drawChild (ViewGroup.java:3921)
			at android.view.ViewGroup.dispatchDraw (ViewGroup.java:3711)
            ...
            */
        }
    }

    /**
     * Zeichnet den Hintergrund.
     *
     * @param canvas
     * Das Canvas
     * @param cell
     * Das Field, das gezeichnet wird
     * @param color
     * Die Hintergrundfarbe
     * @param round
     * Gibt an, ob die Ecken rund gezeichnet werden sollen
     * @param darken
     * Gibt an, ob das Feld verdunkelt werden soll
     */
    private fun drawBackground(canvas: Canvas, cell: View, color: Int, round: Boolean, darken: Boolean) {
        val mainPaint = Paint()
        var darkenPaint: Paint? = null
        if (darken) {
            darkenPaint = Paint()
            darkenPaint.setARGB(60, 0, 0, 0)
        }
        mainPaint.color = color
        val rect = RectF(0f, 0f, cell.width.toFloat(), cell.height.toFloat())
        if (round) {
            canvas.drawRoundRect(rect, cell.width / 20.0f, cell.height / 20.0f, mainPaint)
            if (darken) {
                canvas.drawRoundRect(rect, cell.width / 20.0f, cell.height / 20.0f, darkenPaint!!)
            }
        } else {
            canvas.drawRect(rect, mainPaint)
            if (darken) {
                canvas.drawRect(rect, darkenPaint!!)
            }
        }
    }

    /**
     * Malt den inneren Bereich (lässt einen Rahmen).
     *
     * @param canvas
     * Das Canvas
     * @param cell
     * The cell to draw
     * @param color
     * Die Farbe
     * @param round
     * Gibt an, ob die Ecken rund gezeichnet werden sollen
     * @param darken
     * determines whether the cell should be darkened
     */
    private fun drawInner(canvas: Canvas, cell: View, color: Int, round: Boolean, darken: Boolean) {
        val mainPaint = Paint()
        var darkenPaint: Paint? = null
        if (darken) {
            darkenPaint = Paint()
            darkenPaint.setARGB(60, 0, 0, 0)
        }
        mainPaint.color = color
        val rect = RectF(2f, 2f, (cell.width - 2).toFloat(), (cell.height - 2).toFloat())
        if (round) {
            canvas.drawRoundRect(rect, cell.width / 20.0f, cell.height / 20.0f, mainPaint)
            if (darken) {
                canvas.drawRoundRect(rect, cell.width / 20.0f, cell.height / 20.0f, darkenPaint!!)
            }
        } else {
            canvas.drawRect(rect, mainPaint)
            if (darken) {
                canvas.drawRect(rect, darkenPaint!!)
            }
        }
    }

    /**
     * Schreibt den Text
     *
     * @param canvas
     * Das Canvas
     * @param cell
     * The cell on which to draw
     * @param color
     * Die Farbe des Textes
     * @param bold
     * Definiert, ob der Text fett ist
     * @param symbol
     * Das Symbol, welches geschrieben wird
     */
    private fun drawText(canvas: Canvas, cell: View, color: Int, bold: Boolean, symbol: String) {
        val paint = Paint()
        paint.color = color
        if (bold) {
            paint.typeface = Typeface.DEFAULT_BOLD
        }
        paint.isAntiAlias = true
        paint.textSize = Math.min(cell.height * 3 / 4, cell.width * 3 / 4).toFloat()
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(symbol + "", (cell.width / 2).toFloat(), (cell.height / 2 + Math.min(cell.height / 4, cell.width / 4)).toFloat(), paint)
    }

    /**
     * Sets the specified animation for the passed cell, so that it is drawn when markCell is
     * called. If either parameter is null, nothing happens.
     *
     *
     * @param cell
     * The cell for which the animation is to be stored
     * @param marking
     * Die Animation die eingetragen werden soll
     */
    fun setMarking(cell: View, marking: CellViewStates) {
        markings[cell] = marking
    }

    /**
     * Löscht alle hinzugefügten Markierungen auf Default.
     */
    fun flushMarkings() {
        markings.clear()
    }

    companion object {
        /**
         * Gibt die Singleton-Instanz des Handlers zurück.
         *
         * @return Die Instanz dieses Handlers
         */
        /**
         * Die Singleton-Instanz des Handlers
         */
        @JvmStatic
        var instance: CellViewPainter? = null
            get() {
                if (field == null) {
                    field = CellViewPainter()
                }
                return field
            }
            private set
    }
    /* Constructors */ /**
     * Privater Konstruktor, da diese Klasse statisch ist.
     */
    init {
        markings = Hashtable()
    }
}