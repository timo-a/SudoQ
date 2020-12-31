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
 * This class is responsible for Animationens and highlighting of cells.
 * TODO does it have to be singleton?
 */
public class CellViewPainter {
	/* Attributes */

	/**
	 * Maps a cell onto an Animation value that describes how to draw the cell
	 */
	private Hashtable<View, CellViewStates> markings;

	/**
	 * Die Singleton-Instanz des Handlers
	 */
	private static CellViewPainter instance;

	/** Constructors */

	/**
	 * Privater Konstruktor, da diese Klasse statisch ist.
	 */
	private CellViewPainter() {
		this.markings = new Hashtable<View, CellViewStates>();
	}

	/**
	 * Gibt die Singleton-Instanz des Handlers zurück.
	 * 
	 * @return Die Instanz dieses Handlers
	 */
	public static CellViewPainter getInstance() {
		if (instance == null) {
			instance = new CellViewPainter();
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
	 * @param cell
	 *            Das Feld, anhand dessen Animation-Einstellung das Canvas
	 *            bemalt werden soll
	 * @param symbol
	 *            Das Symbol das gezeichnet werden soll
	 * @param justText
	 *            Definiert, dass nur Text geschrieben wird
	 * @param darken
	 *            Verdunkelt das Feld
	 */
	public void markCell(Canvas canvas, View cell, String symbol, boolean justText, boolean darken) {
		CellViewStates cellState = this.markings.get(cell);
		/*if(true){}else //to suppress fielddrawing TODO remove again*/
		if (cellState != null && !justText) {
			switch (cellState) {
			case SELECTED_INPUT_BORDER:
				drawBackground(canvas, cell, Color.DKGRAY, true, darken);
				drawInner(canvas, cell, Color.rgb(255, 100, 100), true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case SELECTED_INPUT:
				drawBackground(canvas, cell, Color.rgb(255, 100, 100), true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case SELECTED_INPUT_WRONG:
				drawBackground(canvas, cell, Color.rgb(255, 100, 100), true, darken);
				drawText(canvas, cell, Color.RED, false, symbol);
				break;
			case SELECTED_NOTE_BORDER:
				drawBackground(canvas, cell, Color.DKGRAY, true, darken);
				drawInner(canvas, cell, Color.YELLOW, true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case SELECTED_NOTE:
				drawBackground(canvas, cell, Color.YELLOW, true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case SELECTED_NOTE_WRONG:
				drawBackground(canvas, cell, Color.YELLOW, true, darken);
				drawText(canvas, cell, Color.RED, false, symbol);
				break;
			case SELECTED_FIXED:
				drawBackground(canvas, cell, Color.rgb(220, 220, 255), true, darken);
				drawText(canvas, cell, Color.rgb(0, 100, 0), true, symbol);
				break;
			case CONNECTED:
				drawBackground(canvas, cell, Color.rgb(220, 220, 255), true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case CONNECTED_WRONG:
				drawBackground(canvas, cell, Color.rgb(220, 220, 255), true, darken);
				drawText(canvas, cell, Color.RED, false, symbol);
				break;
			case FIXED:
				drawBackground(canvas, cell, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, cell, Color.rgb(0, 100, 0), true, symbol);
				break;
			case DEFAULT_BORDER:
				drawBackground(canvas, cell, Color.DKGRAY, true, darken);
				drawInner(canvas, cell, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case DEFAULT_WRONG:
				drawBackground(canvas, cell, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, cell, Color.RED, false, symbol);
				break;
			case DEFAULT:
				drawBackground(canvas, cell, Color.rgb(250, 250, 250), true, darken);
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case CONTROLS:
				drawBackground(canvas, cell, Color.rgb(40, 40, 40), false, darken);
				// drawInner(canvas, field, Color.rgb(40, 40, 40), false);
				break;
			case KEYBOARD:
				drawBackground(canvas, cell, Color.rgb(230, 230, 230), false, darken);
				drawInner(canvas, cell, Color.rgb(40, 40, 40), false, darken);
				break;
			case SUDOKU:
				drawBackground(canvas, cell, Color.rgb(200, 200, 200), false, darken);
				// drawInner(canvas, field, Color.LTGRAY, false);
				break;
			}
		} else if (cellState != null) {
			switch (cellState) {
			case SELECTED_INPUT_BORDER:
			case SELECTED_INPUT:
			case SELECTED_NOTE_BORDER:
			case SELECTED_NOTE:
			case CONNECTED:
			case DEFAULT_BORDER:
			case DEFAULT:
				drawText(canvas, cell, Color.BLACK, false, symbol);
				break;
			case SELECTED_INPUT_WRONG:
			case SELECTED_NOTE_WRONG:
			case DEFAULT_WRONG:
			case CONNECTED_WRONG:
				drawText(canvas, cell, Color.RED, false, symbol);
				break;
			case SELECTED_FIXED:
			case FIXED:
				drawText(canvas, cell, Color.rgb(0, 100, 0), true, symbol);
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
	 * @param cell
	 *            Das Field, das gezeichnet wird
	 * @param color
	 *            Die Hintergrundfarbe
	 * @param round
	 *            Gibt an, ob die Ecken rund gezeichnet werden sollen
	 * @param darken
	 *            Gibt an, ob das Feld verdunkelt werden soll
	 */
	private void drawBackground(Canvas canvas, View cell, int color, boolean round, boolean darken) {
		Paint mainPaint = new Paint();
		Paint darkenPaint = null;
		if (darken) {
			darkenPaint = new Paint();
			darkenPaint.setARGB(60, 0, 0, 0);
		}
		mainPaint.setColor(color);
		RectF rect = new RectF(0, 0, cell.getWidth(), cell.getHeight());
		if (round) {
			canvas.drawRoundRect(rect, cell.getWidth() / 20.0f, cell.getHeight() / 20.0f, mainPaint);
			if (darken) {
				canvas.drawRoundRect(rect, cell.getWidth() / 20.0f, cell.getHeight() / 20.0f, darkenPaint);
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
	 * @param cell
	 *            The cell to draw
	 * @param color
	 *            Die Farbe
	 * @param round
	 *            Gibt an, ob die Ecken rund gezeichnet werden sollen
	 * @param darken
	 *            determines whether the cell should be darkened
	 */
	private void drawInner(Canvas canvas, View cell, int color, boolean round, boolean darken) {
		Paint mainPaint = new Paint();
		Paint darkenPaint = null;
		if (darken) {
			darkenPaint = new Paint();
			darkenPaint.setARGB(60, 0, 0, 0);
		}
		mainPaint.setColor(color);
		RectF rect = new RectF(2, 2, cell.getWidth() - 2, cell.getHeight() - 2);
		if (round) {
			canvas.drawRoundRect(rect, cell.getWidth() / 20.0f, cell.getHeight() / 20.0f, mainPaint);
			if (darken) {
				canvas.drawRoundRect(rect, cell.getWidth() / 20.0f, cell.getHeight() / 20.0f, darkenPaint);
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
	 * @param cell
	 *            The cell on which to draw
	 * @param color
	 *            Die Farbe des Textes
	 * @param bold
	 *            Definiert, ob der Text fett ist
	 * @param symbol
	 *            Das Symbol, welches geschrieben wird
	 */
	private void drawText(Canvas canvas, View cell, int color, boolean bold, String symbol) {
		Paint paint = new Paint();
		paint.setColor(color);
		if (bold) {
			paint.setTypeface(Typeface.DEFAULT_BOLD);
		}
		paint.setAntiAlias(true);
		paint.setTextSize(Math.min(cell.getHeight() * 3 / 4, cell.getWidth() * 3 / 4));
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(symbol + "", cell.getWidth() / 2, cell.getHeight() / 2 + Math.min(cell.getHeight() / 4, cell.getWidth() / 4), paint);
	}

	/**
	 * Sets the specified animation for the passed cell, so that it is drawn when markCell is
	 * called. If either parameter is null, nothing happens.
	 *
	 * 
	 * @param cell
	 *            The cell for which the animation is to be stored
	 * @param marking
	 *            Die Animation die eingetragen werden soll
	 */
	public void setMarking(View cell, CellViewStates marking) {
		this.markings.put(cell, marking);
	}

	/**
	 * Löscht alle hinzugefügten Markierungen auf Default.
	 */
	public void flushMarkings() {
		this.markings.clear();
	}

}
