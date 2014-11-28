/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.actionTree;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import de.sudoq.controller.sudoku.ActionTreeNavListener;
import de.sudoq.model.actionTree.ActionTreeElement;

/**
 * Diese Klasse ist ein angepasster View, der von der Android API als
 * Interaktionselement genutzt wird. Es wird die Benutzerinteraktion sowie die
 * Erscheinung definiert.
 */
public abstract class ActionTreeElementView extends View {

	/** Attributes */

	/**
	 * Default Action Color
	 */
	public static final int DEFAULT_COLOR = 0xFFFFAD00;

	public static final int WRONG_COLOR = 0xFFCC1F1F;

	public static final int CORRECT_COLOR = 0xFF1FCC1F;

	protected int actionColor = DEFAULT_COLOR;

	protected ActionTreeElementView inner = null;

	/**
	 * Das Element des Aktionsbaumes das von diesem View representiert wird
	 */
	private ActionTreeElement actionTreeElement;

	/**
	 * Die Objekte, die auf Navigations-Änderungen an diesem View reagieren
	 * sollen
	 */
	private ArrayList<ActionTreeNavListener> actionTreeNavListener;

	/** Constructors */

	/**
	 * Erstellt ein neuen ActionTreeElementView und setzt das zugehörige
	 * ActionTreeElement und regestriert einen ActionTreeNavListener.
	 * 
	 * @param context
	 *            Der Kontekt, in dem die View angezeigt wird
	 * @param ate
	 *            Das Element im Aktionsbaum, dass von diesem
	 *            ActionTreeElementView dargestellt wird
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eines der Argumente null ist
	 */
	public ActionTreeElementView(Context context, ActionTreeElementView inner, ActionTreeElement ate) {
		super(context);
		this.actionTreeElement = ate;
		this.actionTreeNavListener = new ArrayList<ActionTreeNavListener>();
		this.inner = inner;
	}

	/** Methods */

	/**
	 * Diese Methode wird von der API aufgerufen, sollte dieser View berührt
	 * werden. An dieser Stelle wird auch die Art der Eingabe ermittelt und es
	 * werden die entsprechenden Aktionen ausgeführt bzw. Listener aufgerufen.
	 * 
	 * @param motionEvent
	 *            Das Event, das von der API generiert wird
	 * @return true, falls das Event bearbeitet wurde, sonst false
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene MotionEvent null ist
	 */
	@Override
	public boolean onTouchEvent(MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			for (ActionTreeNavListener listener : this.actionTreeNavListener) {
				listener.onLoadState(this.actionTreeElement);
			}
		}
		return true;
	}

	/**
	 * Sets the color of this Element to draw
	 * 
	 * @param color
	 *            as an hex color
	 */
	public void changeColor(int color) {
		actionColor = color;
		if (inner != null) {
			inner.changeColor(color);
		}
	}

	/**
	 * Diese Methode definiert die Erscheinung des Views. Auf dem von der API
	 * übergebenen Canvas Objekt wird programmatisch, oder via Bitmap das
	 * Aussehen gezeichnet.
	 * 
	 * @param canvas
	 *            Das Canvas Objekt auf das gezeichnet wird
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene Canvas null ist
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paint(canvas);
	}

	protected void paint(Canvas canvas) {
		if (inner != null) {
			inner.paint(canvas);
		}
		paintCanvas(canvas);
	}

	/**
	 * Template Methode, die das Canvas der View zeichnet.
	 * 
	 * @param canvas
	 *            Das Canvas, das bemalt werden soll.
	 */
	public abstract void paintCanvas(Canvas canvas);

	/**
	 * Erlaubt die Registrierung von Listenern
	 * 
	 * @param actionTreeNavListener
	 *            der zu registrierende Listener
	 */
	public void registerActionTreeNavListener(ActionTreeNavListener actionTreeNavListener) {
		this.actionTreeNavListener.add(actionTreeNavListener);
	}

	/**
	 * Erlaubt die deregistrierung von Listenern
	 * 
	 * @param actionTreeNavListener
	 *            der zu deregistrierende Listener
	 */
	public void removeActionTreeNavListener(ActionTreeNavListener actionTreeNavListener) {
		this.actionTreeNavListener.remove(actionTreeNavListener);
	}

}
