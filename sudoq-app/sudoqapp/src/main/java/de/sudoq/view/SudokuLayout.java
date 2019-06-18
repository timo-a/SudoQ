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
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import de.sudoq.controller.sudoku.board.BoardPainter;
import de.sudoq.controller.sudoku.FieldInteractionListener;
import de.sudoq.controller.sudoku.board.FieldViewPainter;
import de.sudoq.controller.sudoku.hints.HintPainter;
import de.sudoq.controller.sudoku.ObservableFieldInteraction;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.Game;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;

/**
 * Eine View als RealativeLayout, die eine Sudoku-Anzeige verwaltet.
 */
public class SudokuLayout extends RelativeLayout implements ObservableFieldInteraction, ZoomableView {
	/**
	 * Das Log-Tag für den LogCat
	 */
	private static final String LOG_TAG = SudokuLayout.class.getSimpleName();

	/**
	 * Das Game, welches diese Anzeige verwaltet
	 */
	private Game game;

	/**
	 * Der Kontext dieser View
	 */
	private Context context;

	/**
	 * Die Standardgröße eines Feldes
	 */
	private int defaultFieldViewSize;

	/**
	 * Die aktuelle Größe eines Feldes
	 */
	// private int currentFieldViewSize;

	/**
	 * Die aktuell ausgewählte FieldView
	 */
	private SudokuFieldView currentFieldView;

	private float zoomFactor;

	/**
	 * Ein Array aller FieldViews
	 */
	private SudokuFieldView[][] sudokuFieldViews;

	/**
	 * Der linke Rand, verursacht durch ein zu niedriges Layout
	 */
	private int leftMargin;

	/**
	 * Der linke Rand, verursacht durch ein zu schmales Layout
	 */
	private int topMargin;

	/**
	 * Der Platz zwischen 2 Blöcken
	 */
	private static int spacing = 2;

	private BoardPainter boardPainter;
	private HintPainter hintPainter;
	private Paint paint;
	/**
	 * Instanziiert eine neue SudokuView in dem spezifizierten Kontext.
	 * 
	 * @param context
	 *            Der Kontext, in dem diese View angezeigt wird
	 */
	public SudokuLayout(Context context) {
		super(context);
		this.context = context;
		this.game = ((SudokuActivity) context).getGame();

		this.defaultFieldViewSize = 40;
		this.zoomFactor = 1.0f;
		// this.currentFieldViewSize = this.defaultFieldViewSize;
		this.setWillNotDraw(false);
		paint = new Paint();
		this.boardPainter = new BoardPainter(this, game.getSudoku().getSudokuType());
		FieldViewPainter.getInstance().setSudokuLayout(this);
		this.hintPainter = new HintPainter(this);
		inflateSudoku();

		Log.d(LOG_TAG, "End of Constructor.");
	}

	/**
	 * Erstellt die Anzeige des Sudokus.
	 * doesn't draw anything
	 */
	private void inflateSudoku() {
		Log.d(LOG_TAG, "SudokuLayout.inflateSudoku()");
		FieldViewPainter.getInstance().flushMarkings();
		this.removeAllViews();

		Sudoku sudoku = this.game.getSudoku();
		SudokuType sudokuType = sudoku.getSudokuType();
		boolean isMarkWrongSymbolAvailable = this.game.isAssistanceAvailable(Assistances.markWrongSymbol);
		this.sudokuFieldViews = new SudokuFieldView[sudokuType.getSize().getX() + 1][sudokuType.getSize().getY() + 1];
		for (Position p : sudokuType.getValidPositions()) {
				Field field = sudoku.getField(p);
				if (field != null) {
					int x = p.getX();
					int y = p.getY();
					LayoutParams params = new LayoutParams(this.getCurrentFieldViewSize(), this.defaultFieldViewSize);
					params.topMargin  = (y *  this.getCurrentFieldViewSize()) + y;
					params.leftMargin = (x *  this.getCurrentFieldViewSize()) + x;
					this.sudokuFieldViews[x][y] = new SudokuFieldView(context, game, field, isMarkWrongSymbolAvailable);
					field.registerListener(this.sudokuFieldViews[x][y]);
					this.addView(          this.sudokuFieldViews[x][y], params);
				}
		}
		int x = sudoku.getSudokuType().getSize().getX();//why all this????
		int y = sudoku.getSudokuType().getSize().getY();

		LayoutParams params = new LayoutParams( this.getCurrentFieldViewSize(), this.defaultFieldViewSize);
		params.topMargin  = ((y - 1) *  this.getCurrentFieldViewSize()) + (y - 1) + getCurrentTopMargin();
		params.leftMargin = ((x - 1) *  this.getCurrentFieldViewSize()) + (x - 1) + getCurrentLeftMargin();
		this.sudokuFieldViews[x][y] = new SudokuFieldView(context, game, this.game.getSudoku().getField(Position.get(x - 1, y - 1)), isMarkWrongSymbolAvailable);
		this.addView(this.sudokuFieldViews[x][y], params);
		this.sudokuFieldViews[x][y].setVisibility(INVISIBLE);


		/* In case highlighting of current row and col is activated,
		   pass each pos its constraint-mates */
		if (this.game.isAssistanceAvailable(Assistances.markRowColumn)) {
			ArrayList<Position> positions;
			Iterable<Constraint> allConstraints = this.game.getSudoku().getSudokuType();
			for (Constraint c : allConstraints)
				if (c.getType().equals(ConstraintType.LINE)) {
					positions = c.getPositions();
					for (int i = 0; i < positions.size(); i++)
						for (int k = i+1; k < positions.size(); k++) {
							SudokuFieldView fvI = getSudokuFieldView(positions.get(i));
							SudokuFieldView fvK = getSudokuFieldView(positions.get(k));
							fvI.addConnectedField(fvK);
							fvK.addConnectedField(fvI);
						}

				}

		}

		this.hintPainter.updateLayout();
		//Log.d(LOG_TAG, "SudokuLayout.inflateSudoku()-end");
	}

	/**
	 * Berechnet das aktuelle Spacing (gem. dem aktuellen ZoomFaktor) und gibt
	 * es zurück.
	 * 
	 * @return Das aktuelle Spacing
	 */
	public int getCurrentSpacing() {
		return (int) (spacing * this.zoomFactor);
	}

	/**
	 * Berechnet das aktuelle obere Margin (gem. dem aktuellen ZoomFaktor) und
	 * gibt es zurück.
	 * 
	 * @return Das aktuelle obere Margin
	 */
	public int getCurrentTopMargin() {
		return (int) (this.topMargin * this.zoomFactor);
	}

	/**
	 * Berechnet das aktuelle linke Margin (gem. dem aktuellen ZoomFaktor) und
	 * gibt es zurück.
	 * 
	 * @return Das aktuelle linke Margin
	 */
	public int getCurrentLeftMargin() {
		return (int) (this.leftMargin * this.zoomFactor);
	}


	/**
	 * Aktualisiert die Sudoku-Anzeige bzw. der enthaltenen Felder.
	 */
	private void refresh() {
		Log.d(LOG_TAG, "SudokuLayout.refresh()");

		if (this.sudokuFieldViews != null) {
			SudokuType type = this.game.getSudoku().getSudokuType();
			Position typeSize = type.getSize();
			int fieldPlusSpacing = (this.getCurrentFieldViewSize() + getCurrentSpacing());
			//Iterate over all positions within the size 
			for (Position p : type.getValidPositions()) {
				LayoutParams params = (LayoutParams) this.getSudokuFieldView(p).getLayoutParams();
				params.width  = this.getCurrentFieldViewSize();
				params.height = this.getCurrentFieldViewSize();
				params.topMargin  = (getCurrentTopMargin()  + (p.getY() * fieldPlusSpacing));
				params.leftMargin = (getCurrentLeftMargin() + (p.getX() * fieldPlusSpacing));
				this.getSudokuFieldView(p).setLayoutParams(params);
				this.getSudokuFieldView(p).invalidate();
			}
			//still not sure why we are doing this...
			int x = typeSize.getX();
			int y = typeSize.getY();
			//both x and y are over the limit. Why do we go there? we could just do it outside the loop, why was it ever put it in there?!
			LayoutParams params = new LayoutParams( this.getCurrentFieldViewSize(), this.defaultFieldViewSize);
			params.width  = this.getCurrentFieldViewSize();
			params.height = this.getCurrentFieldViewSize();
			params.topMargin =  (2 * getCurrentTopMargin()  + ((y - 1) * fieldPlusSpacing));
			params.leftMargin = (2 * getCurrentLeftMargin() + ((x - 1) * fieldPlusSpacing));
			this.sudokuFieldViews[x][y].setLayoutParams(params);
			this.sudokuFieldViews[x][y].invalidate();
			//end strange thing

		}
		hintPainter.updateLayout();
		invalidate();
		//Log.d(LOG_TAG, "SudokuLayout.refresh()-end");
	}

	@Override
	/**
	 * Draws all black borders for the sudoku, nothing else
	 * Fields have to be drawn after this method
	 * No insight on the coordinate-wise workings, unsure about the 'i's.
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(LOG_TAG, "SudokuLayout.onDraw()");
		float edgeRadius = getCurrentFieldViewSize() / 20.0f;
		paint.reset();
		paint.setColor(Color.BLACK);
		boardPainter.paintBoard(paint, canvas, edgeRadius);
		hintPainter.invalidateAll();
	}

	public float focusX, focusY;



	/**
	 * Zoom so heraus, dass ein diese View optimal in ein Layout der
	 * spezifizierte Größe passt
	 * 
	 * @param width
	 *            Die Breite auf die optimiert werden soll
	 * @param height
	 *            Die Höhe auf die optimiert werden soll
	 */
	public void optiZoom(int width, int height) {
		Log.d(LOG_TAG, "SudokuView height intern: " + this.getMeasuredHeight());
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		int size           = width < height ? width
		                                    : height;
		int numberOfFields = width < height ? sudokuType.getSize().getX()
		                                    : sudokuType.getSize().getY();
		this.defaultFieldViewSize = (size - (numberOfFields + 1) * spacing) / numberOfFields;
		// this.currentFieldViewSize = this.defaultFieldViewSize;

		int fieldSizeX = sudokuType.getSize().getX() * this.getCurrentFieldViewSize() + (sudokuType.getSize().getX() -1) * spacing;
		int fieldSizeY = sudokuType.getSize().getY() * this.getCurrentFieldViewSize() + (sudokuType.getSize().getY() -1) * spacing;

		this.leftMargin = ( width - fieldSizeX) / 2;
		this. topMargin = (height - fieldSizeY)	/ 2;
		Log.d(LOG_TAG, "Sudoku width: "  + width);
		Log.d(LOG_TAG, "Sudoku height: " + height);
		this.refresh();
	}

	/**
	 * Touch-Events werden nicht verarbeitet.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;

	}

	public HintPainter getHintPainter(){
		return hintPainter;
	}

	/**
	 *  returns the FieldView at Position p.
	 */
	public SudokuFieldView getSudokuFieldView(Position p){
		return sudokuFieldViews[p.getX()][p.getY()];
	}

	/**
	 * Setzt den aktuellen Zoom-Faktor für diese View und refresh sie.
	 * 
	 * @param factor
	 *            Der Zoom-Faktor
	 */
	public boolean zoom(float factor) {
		this.zoomFactor = factor;
//		//this.canvas.scale(factor,factor);
		refresh();
		//invalidate();

		return true;
	}

	public float getZoomFactor(){ return zoomFactor; }

	/**
	 * Gibt die aktuell aktive SudokuFieldView dieser View zurück.
	 * 
	 * @return Die aktive SudokuFieldView
	 */
	public SudokuFieldView getCurrentFieldView() {
		return this.currentFieldView;
	}

	/**
	 * Setzt die aktuelle SudokuFieldView
	 * 
	 * @param currentFieldView
	 *            die zu setzende SudokuFieldView
	 */
	public void setCurrentFieldView(SudokuFieldView currentFieldView) {
		this.currentFieldView = currentFieldView;
	}

	/**
	 * Gibt die aktuelle Größe einer FieldView zurück.
	 * 
	 * @return die aktuelle Größe einer FieldView
	 */
	public int getCurrentFieldViewSize() {
		return (int) (this.defaultFieldViewSize * zoomFactor);
	}

	/**
	 * Unbenutzt.
	 * 
	 * @throws UnsupportedOperationException
	 *             Wirft immer eine UnsupportedOperationException
	 */
	public void notifyListener() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerListener(FieldInteractionListener listener) {
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		for (Position p: sudokuType.getValidPositions())
			this.getSudokuFieldView(p).registerListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(FieldInteractionListener listener) {
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		for (Position p: sudokuType.getValidPositions())
			this.getSudokuFieldView(p).removeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMinZoomFactor() {
		return 1.0f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMaxZoomFactor() {
		return 10;//this.game.getSudoku().getSudokuType().getSize().getX() / 2.0f;
	}

}
