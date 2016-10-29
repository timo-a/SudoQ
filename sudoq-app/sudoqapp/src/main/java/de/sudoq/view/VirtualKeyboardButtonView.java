/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TableLayout.LayoutParams;

import java.util.ArrayList;

import de.sudoq.controller.sudoku.FieldViewPainter;
import de.sudoq.controller.sudoku.FieldViewStates;
import de.sudoq.controller.sudoku.InputListener;
import de.sudoq.controller.sudoku.ObservableInput;
import de.sudoq.controller.sudoku.Symbol;

/**
 * Diese Subklasse des Android internen Views stellt einen Button in der
 * Eingabeansicht des Sudokus dar.
 */
public class VirtualKeyboardButtonView extends View implements ObservableInput {
	/** Attributes */

	/**
	 * Das mit diesem VirtualKeyboardButtonView assoziierte Symbol. Es handlet
	 * sich um einen int, da ein generische Zeichensatz unterstützt werden soll,
	 * in dem die Symbole durch int repräsentiert werden.
	 */
	private int symbol;

	/**
	 * Das Symbol, welches in diesem Button steht so, wie es gemalt wird
	 */
	private String drawnSymbol;

	/**
	 * Diese Listener werden benachrichtigt, wenn der Benutzer mit diesem
	 * VirtualKeyboardButtonView interagiert, bspw. durch Anlicken.
	 */
	private ArrayList<InputListener> inputListener;

	/** Constructors */

	/**
	 * Instanziiert eine neue VirtualKeyboardButtonView
	 * 
	 * @param context
	 *            Der Kontext, in dem diese View angezeigt wird
	 * @param symbol
	 *            die interne Id des Symbols
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene Kontext null ist
	 */
	public VirtualKeyboardButtonView(Context context, int symbol) {
		super(context);
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
		this.symbol = symbol;
		this.drawnSymbol = Symbol.getInstance().getMapping(symbol);
		this.inputListener = new ArrayList<InputListener>();

		FieldViewPainter.getInstance().setMarking(this, FieldViewStates.DEFAULT_BORDER);
	}

	/** Methods */

	/**
	 * Diese Methode verarbeitet alle Touch Inputs, die der Benutzer macht und
	 * leitet sie an den InputListener weiter.
	 * 
	 * @param motionEvent
	 *            Das Event, das von der API generiert wird
	 * @return true, falls das Event bearbeitet wurde, false falls nicht
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene MotionEvent null ist
	 */
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		if (this.isEnabled())
			notifyListeners();

		return false;
	}

	/**
	 * Zeichnet den VirtualKeyboardButtonView inklusive dessen Symbol abhängig
	 * vom Status (an/aus).
	 * 
	 * @param canvas
	 *            Das Canvas Objekt auf das gezeichnet wird
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls canvas null ist
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		FieldViewPainter.getInstance().markField(canvas, this, this.drawnSymbol, false, false);
		if (!this.isEnabled()) {
			canvas.drawARGB(100, 10, 10, 10);
		}
	}

	/**
	 * Fügt einen InputListener diesem VirtualKeyboardButtonView hinzu um ihn
	 * benachrichtigen zu können.
	 * 
	 * @param listener
	 *            Ein InputListener der bei interaktion mit diesem
	 *            VirtualKeyboardButtonView benachricht wird
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls listener null ist
	 */
	public void registerListener(InputListener listener) {
		this.inputListener.add(listener);
	}

	/**
	 * Löscht einen InputListener aus diesem VirtualKeyboardButtonView der
	 * daraufhin nicht weiter benachricht wird.
	 * 
	 * @param listener
	 *            Ein InputListener der vom VirtualKeyboardButtonView gelöst
	 *            werden soll
	 */
	public void removeListener(InputListener listener) {
		this.inputListener.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void notifyListeners() {
		for (InputListener listener : this.inputListener) {
			listener.onInput(this.symbol);
		}
	}
}
