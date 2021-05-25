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
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import de.sudoq.controller.sudoku.InputListener
import de.sudoq.controller.sudoku.ObservableInput
import de.sudoq.controller.sudoku.board.CellViewPainter
import de.sudoq.controller.sudoku.board.CellViewStates

/**
 * Dieses Layout stellt ein virtuelles Keyboard zur Verfügung, in dem sich die
 * Buttons möglichst quadratisch ausrichten.
 */
class VirtualKeyboardLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), ObservableInput, Iterable<View?> {
    /**
     * Die Buttons des VirtualKeyboard
     */
    private var buttons: Array<Array<VirtualKeyboardButtonView>>?
    private val buttonIterator: Iterable<VirtualKeyboardButtonView> = object : Iterable<VirtualKeyboardButtonView?> {
        override fun iterator(): Iterator<VirtualKeyboardButtonView> {
            return object : MutableIterator<VirtualKeyboardButtonView?> {
                var i = 0
                var j = 0
                override fun hasNext(): Boolean {
                    return i < buttons!!.size
                }

                override fun next(): VirtualKeyboardButtonView {
                    val current = buttons!![i][j++]
                    if (j == buttons!![i].length) {
                        j = 0
                        i++
                    }
                    return current
                }

                override fun remove() {}
            }
        }
    }

    /**
     * Beschreibt, ob die Tastatur deaktiviert ist.
     */
    private var deactivated = false

    /**
     * Aktualisiert das Keyboard, sodass für das angegebene Game die korrekten
     * Buttons dargestellt werden.
     *
     * @param numberOfButtons
     * Die Anzahl der Buttons für dieses Keyboard
     */
    fun refresh(numberOfButtons: Int) {
        if (numberOfButtons < 0) return
        deactivated = false
        inflate(numberOfButtons)
    }

    /**
     * Inflatet das Keyboard.
     *
     * @param numberOfButtons
     * Anzahl der Buttons dieser Tastatur
     */
    private fun inflate(numberOfButtons: Int) {
        removeAllViews()
        val buttonsPerColumn = Math.floor(Math.sqrt(numberOfButtons.toDouble())).toInt()
        val buttonsPerRow = Math.ceil(Math.sqrt(numberOfButtons.toDouble())).toInt()
        buttons = Array(buttonsPerRow) { arrayOfNulls(buttonsPerColumn) }
        for (y in 0 until buttonsPerColumn) {
            val la = LinearLayout(context)
            for (x in 0 until buttonsPerRow) {
                buttons!![x][y] = VirtualKeyboardButtonView(context, x + y * buttonsPerRow)
                buttons!![x][y].visibility = INVISIBLE
                val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f)
                params.leftMargin = 2
                params.bottomMargin = 2
                params.topMargin = 2
                params.rightMargin = 2
                la.addView(buttons!![x][y], params)
            }
            addView(la, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f))
        }
    }

    /**
     * {@inheritDoc}
     */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //		FieldViewPainter.getInstance().markField(canvas, this, ' ', false);
    }

    /**
     * Aktiviert bzw. deaktiviert dieses Keyboard.
     *
     * @param activated
     * Spezifiziert, ob das Keyboard aktiviert oder deaktiviert sein
     * soll
     */
    override fun setActivated(activated: Boolean) {
        for (b in buttonIterator) b.visibility = if (activated) VISIBLE else INVISIBLE
    }

    /**
     * Unbenutzt.
     *
     * @throws UnsupportedOperationException
     * Wirft immer eine UnsupportedOperationException
     */
    override fun notifyListeners() {
        throw UnsupportedOperationException()
    }

    /**
     * {@inheritDoc}
     */
    override fun registerListener(listener: InputListener) {
        for (b in buttonIterator) b.registerListener(listener)
    }

    /**
     * {@inheritDoc}
     */
    override fun removeListener(listener: InputListener) {
        for (b in buttonIterator) b.removeListener(listener)
    }

    /**
     * Markiert das spezifizierte Feld mit dem übergebenen Status, um von dem
     * FieldViewPainter entsprechend gezeichnet zu werden.
     *
     * @param symbol
     * Das Symbol des Feldes
     * @param state
     * Der zu setzende Status
     */
    fun markCell(symbol: Int, state: CellViewStates?) {
        val buttonsPerRow = buttons!!.size
        CellViewPainter.getInstance().setMarking(buttons!![symbol % buttonsPerRow][symbol / buttonsPerRow], state)
        buttons!![symbol % buttonsPerRow][symbol / buttonsPerRow].invalidate()
    }

    /**
     * Aktiviert alle Buttons dieses Keyboards.
     */
    fun enableAllButtons() {
        for (b in buttonIterator) b.isEnabled = true
    }

    /**
     * Deaktiviert den spezifizierten Button.
     *
     * @param symbol
     * Das Symbol des zu deaktivierenden Button
     */
    fun disableButton(symbol: Int) {
        val buttonsPerRow = buttons!!.size
        buttons!![symbol % buttonsPerRow][symbol / buttonsPerRow].isEnabled = false
    }

    /**
     * {@inheritDoc}
     */
    override fun invalidate() {
        if (buttons == null) {
            return
        }
        for (b in buttonIterator) b?.invalidate()
    }

    /**
     * Gibt zurueck ob die view angezeigt wird
     *
     * @return true falls aktive andernfalls false
     */
    override fun isActivated(): Boolean {
        return !deactivated
    }

    override fun iterator(): MutableIterator<View> {
        return object : MutableIterator<View?> {
            var i = 0
            override fun hasNext(): Boolean {
                return i < childCount
            }

            override fun next(): View {
                return getChildAt(i++)
            }

            override fun remove() {}
        }
    }

    /**
     * Instanziiert ein neues VirtualKeyboardLayout mit den gegebenen Parametern
     *
     * @param context
     * der Applikationskontext
     * @param attrs
     * das Android AttributeSet
     */
    init {
        setWillNotDraw(false)
    }
}