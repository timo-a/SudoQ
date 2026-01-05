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
import android.view.MotionEvent
import android.view.View
import android.widget.TableLayout
import de.sudoq.controller.sudoku.InputListener
import de.sudoq.controller.sudoku.ObservableInput
import de.sudoq.controller.sudoku.Symbol
import de.sudoq.controller.sudoku.board.CellViewPainter
import de.sudoq.controller.sudoku.board.CellViewStates
import java.util.*

/**
 * Diese Subklasse des Android internen Views stellt einen Button in der
 * Eingabeansicht des Sudokus dar.
 *
 * @property symbol The symbol associated with this VirtualKeyboardButtonView.
 */
class VirtualKeyboardButtonView(context: Context?, private val symbol: Int) : View(context),
    ObservableInput {

    /**
     * Das Symbol, welches in diesem Button steht so, wie es gemalt wird
     */
    private val drawnSymbol: String

    /**
     * Diese Listener werden benachrichtigt, wenn der Benutzer mit diesem
     * VirtualKeyboardButtonView interagiert, bspw. durch Anlicken.
     */
    private val inputListener: ArrayList<InputListener>
    /** Methods  */
    /**
     * Diese Methode verarbeitet alle Touch Inputs, die der Benutzer macht und
     * leitet sie an den InputListener weiter.
     *
     * @param motionEvent
     * Das Event, das von der API generiert wird
     * @return true, falls das Event bearbeitet wurde, false falls nicht
     * @throws IllegalArgumentException
     * Wird geworfen, falls das übergebene MotionEvent null ist
     */
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (this.isEnabled) notifyListeners()
        return false
    }

    /**
     * Zeichnet den VirtualKeyboardButtonView inklusive dessen Symbol abhängig
     * vom Status (an/aus).
     *
     * @param canvas
     * Das Canvas Objekt auf das gezeichnet wird
     * @throws IllegalArgumentException
     * Wird geworfen, falls canvas null ist
     */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        CellViewPainter.instance!!.markCell(canvas, this, drawnSymbol, justText = false, darken = false)
        if (!this.isEnabled) {
            canvas.drawARGB(100, 10, 10, 10)
        }
    }

    /**
     * Fügt einen InputListener diesem VirtualKeyboardButtonView hinzu um ihn
     * benachrichtigen zu können.
     *
     * @param listener
     * Ein InputListener der bei interaktion mit diesem
     * VirtualKeyboardButtonView benachricht wird
     * @throws IllegalArgumentException
     * Wird geworfen, falls listener null ist
     */
    override fun registerListener(listener: InputListener) {
        inputListener.add(listener)
    }

    /**
     * Löscht einen InputListener aus diesem VirtualKeyboardButtonView der
     * daraufhin nicht weiter benachricht wird.
     *
     * @param listener
     * Ein InputListener der vom VirtualKeyboardButtonView gelöst
     * werden soll
     */
    override fun removeListener(listener: InputListener) {
        inputListener.remove(listener)
    }

    /**
     * {@inheritDoc}
     */
    override fun notifyListeners() {
        for (listener in inputListener) {
            listener.onInput(symbol)
        }
    }
    /** Constructors  */
    /**
     * Instanziiert eine neue VirtualKeyboardButtonView
     *
     * @param context
     * Der Kontext, in dem diese View angezeigt wird
     * @param symbol
     * die interne Id des Symbols
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene Kontext null ist
     */
    init {
        this.layoutParams = TableLayout.LayoutParams(
            TableLayout.LayoutParams.WRAP_CONTENT,
            TableLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        drawnSymbol = Symbol.getInstance().getMapping(symbol)
        inputListener = ArrayList()
        CellViewPainter.instance!!.setMarking(this, CellViewStates.DEFAULT_BORDER)
    }
}