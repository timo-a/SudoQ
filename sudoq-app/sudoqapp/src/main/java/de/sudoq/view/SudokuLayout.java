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
import de.sudoq.controller.sudoku.CellInteractionListener;
import de.sudoq.controller.sudoku.board.CellViewPainter;
import de.sudoq.controller.sudoku.hints.HintPainter;
import de.sudoq.controller.sudoku.ObservableCellInteraction;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.Game;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;

/**
 * Eine View als RealativeLayout, die eine Sudoku-Anzeige verwaltet.
 */
public class SudokuLayout extends RelativeLayout implements ObservableCellInteraction, ZoomableView {
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
	private int defaultCellViewSize;

	/**
	 * Die aktuelle Größe eines Feldes
	 */
	// private int currentFieldViewSize;

	/**
	 * Die aktuell ausgewählte FieldView
	 */
	private SudokuCellView currentCellView;

	private float zoomFactor;

	/**
	 * Ein Array aller FieldViews
	 */
	private SudokuCellView[][] sudokuCellViews;

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

		this.defaultCellViewSize = 40;
		this.zoomFactor = 1.0f;
		// this.currentFieldViewSize = this.defaultFieldViewSize;
		this.setWillNotDraw(false);
		paint = new Paint();
		this.boardPainter = new BoardPainter(this, game.getSudoku().getSudokuType());
		CellViewPainter.getInstance().setSudokuLayout(this);
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
		CellViewPainter.getInstance().flushMarkings();
		this.removeAllViews();

		Sudoku sudoku = this.game.getSudoku();
		SudokuType sudokuType = sudoku.getSudokuType();
		boolean isMarkWrongSymbolAvailable = this.game.isAssistanceAvailable(Assistances.markWrongSymbol);
		this.sudokuCellViews = new SudokuCellView[sudokuType.getSize().getX() + 1][sudokuType.getSize().getY() + 1];
		for (Position p : sudokuType.getValidPositions()) {
				Cell cell = sudoku.getCell(p);
				if (cell != null) {
					int x = p.getX();
					int y = p.getY();
					LayoutParams params = new LayoutParams(this.getCurrentCellViewSize(), this.defaultCellViewSize);
					params.topMargin  = (y *  this.getCurrentCellViewSize()) + y;
					params.leftMargin = (x *  this.getCurrentCellViewSize()) + x;
					this.sudokuCellViews[x][y] = new SudokuCellView(context, game, cell, isMarkWrongSymbolAvailable);
					cell.registerListener(this.sudokuCellViews[x][y]);
					this.addView(          this.sudokuCellViews[x][y], params);
				}
		}
		int x = sudoku.getSudokuType().getSize().getX();//why all this????
		int y = sudoku.getSudokuType().getSize().getY();

		LayoutParams params = new LayoutParams( this.getCurrentCellViewSize(), this.defaultCellViewSize);
		params.topMargin  = ((y - 1) *  this.getCurrentCellViewSize()) + (y - 1) + getCurrentTopMargin();
		params.leftMargin = ((x - 1) *  this.getCurrentCellViewSize()) + (x - 1) + getCurrentLeftMargin();
		this.sudokuCellViews[x][y] = new SudokuCellView(context, game, this.game.getSudoku().getCell(Position.get(x - 1, y - 1)), isMarkWrongSymbolAvailable);
		this.addView(this.sudokuCellViews[x][y], params);
		this.sudokuCellViews[x][y].setVisibility(INVISIBLE);


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
							SudokuCellView fvI = getSudokuCellView(positions.get(i));
							SudokuCellView fvK = getSudokuCellView(positions.get(k));
							fvI.addConnectedCell(fvK);
							fvK.addConnectedCell(fvI);
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

		if (this.sudokuCellViews != null) {
			SudokuType type = this.game.getSudoku().getSudokuType();
			Position typeSize = type.getSize();
			int fieldPlusSpacing = (this.getCurrentCellViewSize() + getCurrentSpacing());
			//Iterate over all positions within the size 
			for (Position p : type.getValidPositions()) {
				LayoutParams params = (LayoutParams) this.getSudokuCellView(p).getLayoutParams();
				params.width  = this.getCurrentCellViewSize();
				params.height = this.getCurrentCellViewSize();
				params.topMargin  = (getCurrentTopMargin()  + (p.getY() * fieldPlusSpacing));
				params.leftMargin = (getCurrentLeftMargin() + (p.getX() * fieldPlusSpacing));
				this.getSudokuCellView(p).setLayoutParams(params);
				this.getSudokuCellView(p).invalidate();
			}
			//still not sure why we are doing this...
			int x = typeSize.getX();
			int y = typeSize.getY();
			//both x and y are over the limit. Why do we go there? we could just do it outside the loop, why was it ever put it in there?!
			LayoutParams params = new LayoutParams( this.getCurrentCellViewSize(), this.defaultCellViewSize);
			params.width  = this.getCurrentCellViewSize();
			params.height = this.getCurrentCellViewSize();
			params.topMargin =  (2 * getCurrentTopMargin()  + ((y - 1) * fieldPlusSpacing));
			params.leftMargin = (2 * getCurrentLeftMargin() + ((x - 1) * fieldPlusSpacing));
			this.sudokuCellViews[x][y].setLayoutParams(params);
			this.sudokuCellViews[x][y].invalidate();
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
		float edgeRadius = getCurrentCellViewSize() / 20.0f;
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
		this.defaultCellViewSize = (size - (numberOfFields + 1) * spacing) / numberOfFields;
		// this.currentFieldViewSize = this.defaultFieldViewSize;

		int fieldSizeX = sudokuType.getSize().getX() * this.getCurrentCellViewSize() + (sudokuType.getSize().getX() -1) * spacing;
		int fieldSizeY = sudokuType.getSize().getY() * this.getCurrentCellViewSize() + (sudokuType.getSize().getY() -1) * spacing;

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
	public SudokuCellView getSudokuCellView(Position p){
		return sudokuCellViews[p.getX()][p.getY()];
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
	public SudokuCellView getCurrentCellView() {
		return this.currentCellView;
	}

	/**
	 * Setzt die aktuelle SudokuFieldView
	 * 
	 * @param currentCellView
	 *            die zu setzende SudokuFieldView
	 */
	public void setCurrentCellView(SudokuCellView currentCellView) {
		this.currentCellView = currentCellView;
	}

	/**
	 * Gibt die aktuelle Größe einer FieldView zurück.
	 * 
	 * @return die aktuelle Größe einer FieldView
	 */
	public int getCurrentCellViewSize() {
		return (int) (this.defaultCellViewSize * zoomFactor);
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
	public void registerListener(CellInteractionListener listener) {
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		for (Position p: sudokuType.getValidPositions())
			this.getSudokuCellView(p).registerListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(CellInteractionListener listener) {
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		for (Position p: sudokuType.getValidPositions())
			this.getSudokuCellView(p).removeListener(listener);
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
