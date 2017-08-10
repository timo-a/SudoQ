/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku.board;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

import java.util.Hashtable;

import de.sudoq.view.SudokuLayout;

/**
 * Stellt eine Klasse zur Verfügung, welche für Animationen bzw. Markierungen
 * von Feldern zuständig ist. TODO does it have to be singleton?
 */
public class FieldViewPainter {
	/** Attributes */

	/**
	 * Mappt ein Field auf einen Animation-Wert, welcher beschreibt, wie das
	 * Feld zu zeichnen ist
	 */
	private Hashtable<View, FieldViewStates> markings;

	/**
	 * Die Singleton-Instanz des Handlers
	 */
	private static FieldViewPainter instance;

	/** Constructors */

	/**
	 * Privater Konstruktor, da diese Klasse statisch ist.
	 */
	private FieldViewPainter() {
		this.markings = new Hashtable<View, FieldViewStates>();
	}

	/**
	 * Gibt die Singleton-Instanz des Handlers zurück.
	 * 
	 * @return Die Instanz dieses Handlers
	 */
	public static FieldViewPainter getInstance() {
		if (instance == null) {
			instance = new FieldViewPainter();
		}

		return instance;
	}

	private SudokuLayout sl;
	public void setSudokuLayout(SudokuLayout sl){
		this.sl=sl;
	}

	/** Methods */

	/**
	 * Bemalt das spezifizierte Canvas entsprechend der in der Hashtable für das
	 * spezifizierte Feld eingetragenen Animation. Ist eines der beiden
	 * Argumente null, so wird nichts getan.
	 * 
	 * @param canvas
	 *            Das Canvas, welches bemalt werden soll
	 * @param field
	 *            Das Feld, anhand dessen Animation-Einstellung das Canvas
	 *            bemalt werden soll
	 * @param symbol
	 *            Das Symbol das gezeichnet werden soll
	 * @param justText
	 *            Definiert, dass nur Text geschrieben wird
	 * @param darken
	 *            Verdunkelt das Feld
	 */
	public void markField(Canvas canvas, View field, String symbol, boolean justText, boolean darken) {
		FieldViewStates fieldState = this.markings.get(field);
		/*if(true){}else //to suppress fielddrawing TODO remove again*/
		if (fieldState != null && !justText) {
			switch (fieldState) {
			case SELECTED_INPUT_BORDER:
				drawBackground(canvas, field, Color.DKGRAY, true, darken);
				drawInner(canvas, field, Color.rgb(255, 100, 100), true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case SELECTED_INPUT:
				drawBackground(canvas, field, Color.rgb(255, 100, 100), true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case SELECTED_INPUT_WRONG:
				drawBackground(canvas, field, Color.rgb(255, 100, 100), true, darken);
				drawText(canvas, field, Color.RED, false, symbol);
				break;
			case SELECTED_NOTE_BORDER:
				drawBackground(canvas, field, Color.DKGRAY, true, darken);
				drawInner(canvas, field, Color.YELLOW, true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case SELECTED_NOTE:
				drawBackground(canvas, field, Color.YELLOW, true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case SELECTED_NOTE_WRONG:
				drawBackground(canvas, field, Color.YELLOW, true, darken);
				drawText(canvas, field, Color.RED, false, symbol);
				break;
			case SELECTED_FIXED:
				drawBackground(canvas, field, Color.rgb(220, 220, 255), true, darken);
				drawText(canvas, field, Color.rgb(0, 100, 0), true, symbol);
				break;
			case CONNECTED:
				drawBackground(canvas, field, Color.rgb(220, 220, 255), true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case CONNECTED_WRONG:
				drawBackground(canvas, field, Color.rgb(220, 220, 255), true, darken);
				drawText(canvas, field, Color.RED, false, symbol);
				break;
			case FIXED:
				drawBackground(canvas, field, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, field, Color.rgb(0, 100, 0), true, symbol);
				break;
			case DEFAULT_BORDER:
				drawBackground(canvas, field, Color.DKGRAY, true, darken);
				drawInner(canvas, field, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case DEFAULT_WRONG:
				drawBackground(canvas, field, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, field, Color.RED, false, symbol);
				break;
			case DEFAULT:
				drawBackground(canvas, field, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case CONTROLS:
				drawBackground(canvas, field, Color.rgb(40, 40, 40), false, darken);
				// drawInner(canvas, field, Color.rgb(40, 40, 40), false);
				break;
			case KEYBOARD:
				drawBackground(canvas, field, Color.rgb(230, 230, 230), false, darken);
				drawInner(canvas, field, Color.rgb(40, 40, 40), false, darken);
				break;
			case SUDOKU:
				drawBackground(canvas, field, Color.rgb(200, 200, 200), false, darken);
				// drawInner(canvas, field, Color.LTGRAY, false);
				break;
			}
		} else if (fieldState != null) {
			switch (fieldState) {
			case SELECTED_INPUT_BORDER:
			case SELECTED_INPUT:
			case SELECTED_NOTE_BORDER:
			case SELECTED_NOTE:
			case CONNECTED:
			case DEFAULT_BORDER:
			case DEFAULT:
				drawText(canvas, field, Color.BLACK, false, symbol);
				break;
			case SELECTED_INPUT_WRONG:
			case SELECTED_NOTE_WRONG:
			case DEFAULT_WRONG:
			case CONNECTED_WRONG:
				drawText(canvas, field, Color.RED, false, symbol);
				break;
			case SELECTED_FIXED:
			case FIXED:
				drawText(canvas, field, Color.rgb(0, 100, 0), true, symbol);
				break;
			}
		}
		//Log.d("FieldPainter", "Field drawn");

		try {
			sl.getHintPainter().invalidateAll();//invalidate();
		}catch(NullPointerException e){
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
	 *            Das Canvas
	 * @param field
	 *            Das Field, das gezeichnet wird
	 * @param color
	 *            Die Hintergrundfarbe
	 * @param round
	 *            Gibt an, ob die Ecken rund gezeichnet werden sollen
	 * @param darken
	 *            Gibt an, ob das Feld verdunkelt werden soll
	 */
	private void drawBackground(Canvas canvas, View field, int color, boolean round, boolean darken) {
		Paint mainPaint = new Paint();
		Paint darkenPaint = null;
		if (darken) {
			darkenPaint = new Paint();
			darkenPaint.setARGB(60, 0, 0, 0);
		}
		mainPaint.setColor(color);
		RectF rect = new RectF(0, 0, field.getWidth(), field.getHeight());
		if (round) {
			canvas.drawRoundRect(rect, field.getWidth() / 20.0f, field.getHeight() / 20.0f, mainPaint);
			if (darken) {
				canvas.drawRoundRect(rect, field.getWidth() / 20.0f, field.getHeight() / 20.0f, darkenPaint);
			}
		} else {
			canvas.drawRect(rect, mainPaint);
			if (darken) {
				canvas.drawRect(rect, darkenPaint);
			}
		}
	}

	/**
	 * Malt den inneren Bereich (lässt einen Rahmen).
	 * 
	 * @param canvas
	 *            Das Canvas
	 * @param field
	 *            Das Field, das gezeichnet wird
	 * @param color
	 *            Die Farbe
	 * @param round
	 *            Gibt an, ob die Ecken rund gezeichnet werden sollen
	 * @param darken
	 *            Gibt an, ob das Feld verdunkelt werden soll
	 */
	private void drawInner(Canvas canvas, View field, int color, boolean round, boolean darken) {
		Paint mainPaint = new Paint();
		Paint darkenPaint = null;
		if (darken) {
			darkenPaint = new Paint();
			darkenPaint.setARGB(60, 0, 0, 0);
		}
		mainPaint.setColor(color);
		RectF rect = new RectF(2, 2, field.getWidth() - 2, field.getHeight() - 2);
		if (round) {
			canvas.drawRoundRect(rect, field.getWidth() / 20.0f, field.getHeight() / 20.0f, mainPaint);
			if (darken) {
				canvas.drawRoundRect(rect, field.getWidth() / 20.0f, field.getHeight() / 20.0f, darkenPaint);
			}
		} else {
			canvas.drawRect(rect, mainPaint);
			if (darken) {
				canvas.drawRect(rect, darkenPaint);
			}
		}
	}

	/**
	 * Schreibt den Text
	 * 
	 * @param canvas
	 *            Das Canvas
	 * @param field
	 *            Das Field, das gezeichnet wird
	 * @param color
	 *            Die Farbe des Textes
	 * @param bold
	 *            Definiert, ob der Text fett ist
	 * @param symbol
	 *            Das Symbol, welches geschrieben wird
	 */
	private void drawText(Canvas canvas, View field, int color, boolean bold, String symbol) {
		Paint paint = new Paint();
		paint.setColor(color);
		if (bold) {
			paint.setTypeface(Typeface.DEFAULT_BOLD);
		}
		paint.setAntiAlias(true);
		paint.setTextSize(Math.min(field.getHeight() * 3 / 4, field.getWidth() * 3 / 4));
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(symbol + "", field.getWidth() / 2, field.getHeight() / 2 + Math.min(field.getHeight() / 4, field.getWidth() / 4), paint);
	}

	/**
	 * Setzt die spezifizierte Animation für das spezifizierte Feld, sodass beim
	 * Aufruf der markField Methode für dieses Feld die übergebene Animation auf
	 * diesem gezeichnet wird. Ist eines der beiden Argumente null, so wird
	 * nichts getan.
	 * 
	 * @param field
	 *            Das Field für das Animation eingetragen werden soll
	 * @param marking
	 *            Die Animation die eingetragen werden soll
	 */
	public void setMarking(View field, FieldViewStates marking) {
		this.markings.put(field, marking);
	}

	/**
	 * Löscht alle hinzugefügten Markierungen auf Default.
	 */
	public void flushMarkings() {
		this.markings.clear();
	}

}
