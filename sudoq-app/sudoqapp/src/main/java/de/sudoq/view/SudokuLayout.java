/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import de.sudoq.controller.sudoku.FieldInteractionListener;
import de.sudoq.controller.sudoku.FieldViewPainter;
import de.sudoq.controller.sudoku.HintPainter;
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

	/**
	 *
	 */
	private HintPainter hintPainter;

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
		this.sudokuFieldViews = new SudokuFieldView[sudokuType.getSize().getX() + 1][sudokuType.getSize().getY() + 1];
		for (int x = 0; x <= sudokuType.getSize().getX(); x++) {
			for (int y = 0; y <= sudokuType.getSize().getY(); y++) {
				Field field = sudoku.getField(Position.get(x, y));
				if (field != null) {
					LayoutParams params = new LayoutParams(this.getCurrentFieldViewSize(), this.defaultFieldViewSize);
					params.topMargin  = (y *  this.getCurrentFieldViewSize()) + y;
					params.leftMargin = (x *  this.getCurrentFieldViewSize()) + x;
					this.sudokuFieldViews[x][y] = new SudokuFieldView(context, game, field, this.game.isAssistanceAvailable(Assistances.markWrongSymbol));
					field.registerListener(this.sudokuFieldViews[x][y]);
					this.addView(          this.sudokuFieldViews[x][y], params);
				} else if (sudoku.getSudokuType().getSize().getX() == x //why????
						&& sudoku.getSudokuType().getSize().getY() == y) {
					LayoutParams params = new LayoutParams( this.getCurrentFieldViewSize(), this.defaultFieldViewSize);
					params.topMargin  = ((y - 1) *  this.getCurrentFieldViewSize()) + (y - 1) + getCurrentTopMargin();
					params.leftMargin = ((x - 1) *  this.getCurrentFieldViewSize()) + (x - 1) + getCurrentLeftMargin();
					this.sudokuFieldViews[x][y] = new SudokuFieldView(context, game, this.game.getSudoku().getField(Position.get(x - 1, y - 1)), this.game.isAssistanceAvailable(Assistances.markWrongSymbol));
					this.addView(this.sudokuFieldViews[x][y], params);
					this.sudokuFieldViews[x][y].setVisibility(INVISIBLE);
				}
			}
		}

		ArrayList<Constraint> allConstraints = this.game.getSudoku().getSudokuType().getConstraints();
		ArrayList<Position> positions;
		if (this.game.isAssistanceAvailable(Assistances.markRowColumn)) {
			for (Constraint c : allConstraints) {
				if (c.getType().equals(ConstraintType.LINE)) {
					positions = c.getPositions();
					for (int i = 0; i < positions.size(); i++) {
						for (int k = 0; k < positions.size(); k++) {
							if (i != k){
								Position pos_i = positions.get(i);
								Position pos_k = positions.get(k);
								this.sudokuFieldViews[pos_i.getX()][pos_i.getY()].addConnectedField(
										this.sudokuFieldViews[pos_k.getX()][pos_k.getY()]);
							}
						}
					}
				}
			}
		}
		Log.d(LOG_TAG, "SudokuLayout.inflateSudoku()-end");
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
			Position typeSize = this.game.getSudoku().getSudokuType().getSize();
			//Iterate over all positions within the size 
			//and one more! why do we go 0 to limit, why <= ?
			for (    int x = 0; x <= typeSize.getX(); x++) {
				for (int y = 0; y <= typeSize.getY(); y++) {
					if (game.getSudoku().getField(Position.get(x, y)) != null) {
						//Position is not null. This check is important for samurai sudokus 
						LayoutParams params = (LayoutParams) this.sudokuFieldViews[x][y].getLayoutParams();
						params.width  =  this.getCurrentFieldViewSize();
						params.height =  this.getCurrentFieldViewSize();
						params.topMargin =  (getCurrentTopMargin()  + (y * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						params.leftMargin = (getCurrentLeftMargin() + (x * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						this.sudokuFieldViews[x][y].setLayoutParams(params);
						this.sudokuFieldViews[x][y].invalidate();
					} else if (x == typeSize.getX() && 
							   y == typeSize.getY() ) {
						//both x and y are over the limit. Why do we go there? we could just do it outside the loop, why was it ever put it in there?!
						LayoutParams params = new LayoutParams( this.getCurrentFieldViewSize(), this.defaultFieldViewSize);
						params.width  = this.getCurrentFieldViewSize();
						params.height = this.getCurrentFieldViewSize();
						params.topMargin =  (2 * getCurrentTopMargin()  + ((y - 1) * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						params.leftMargin = (2 * getCurrentLeftMargin() + ((x - 1) * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						this.sudokuFieldViews[x][y].setLayoutParams(params);
						this.sudokuFieldViews[x][y].invalidate();
					}
				}
			}
		}
		invalidate();
		Log.d(LOG_TAG, "SudokuLayout.refresh()-end");
	}

	private int getCol(int i){
		switch (i){
		case 0: return Color.BLACK;
		case 1: return Color.GREEN;
		case 2: return Color.MAGENTA;
		case 3: return Color.YELLOW;
		default: return getCol(i-4);
		}
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
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		for (Constraint c: this.game.getSudoku().getSudokuType()) {
			if (c.getType().equals(ConstraintType.BLOCK)) {
				/* for position p in block c */
				outlineConstraint(c, canvas, edgeRadius, paint, false);
			}
		}

		/*int i=0;
		Paint p = new Paint();
		p.setColor(Color.RED);
		for (Constraint c: this.game.getSudoku().getSudokuType())
			if (c.getType().equals(ConstraintType.LINE)) {
				if (i > 2) {
					outlineConstraint(c, canvas, edgeRadius, p, true);
					break;
				}
				i++;
			}


		//hintPainter.drawConstraints(canvas);
		Log.d(LOG_TAG, "SudokuLayout.onDraw()-end");*/
		hintPainter.invalidateAll();
	}

	private void outlineConstraint(Constraint c, Canvas canvas, float edgeRadius, Paint paint, boolean thick){
		for (Position p: c) {
					/* determine whether the position p is in the (right|left|top|bottom) border of its block constraint.
					 * test for 0 to avoid illegalArgExc for neg. vals
					 * careful when trying to optimize this definition: blocks can be squiggly (every additional compound to row/col but extra as in hypersudoku is s.th. different)
					 * */
			boolean isLeft   = p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY()    ));
			boolean isRight  =                  !c.includes(Position.get(p.getX() + 1, p.getY()    ));
			boolean isTop    = p.getY() == 0 || !c.includes(Position.get(p.getX(),     p.getY() - 1));
			boolean isBottom =                  !c.includes(Position.get(p.getX(),     p.getY() + 1));
					/* apparently:
					 *   00 10 20 30 ...
					 *   01 11
					 *   02    xy
					 *   .
					 *   .
					 * */

			int from,to;
			int scale = 3;
			if(thick){
				from = -getCurrentSpacing()*scale;
				to   =  getCurrentSpacing()*scale;
			}else{
				from = 1;
				to   = getCurrentSpacing();
			}


			for (int i = from; i <= to; i++) {//?
				//deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
				int fieldSizeAndSpacing = this.getCurrentFieldViewSize() + getCurrentSpacing();
						/* these first 4 seem similar. drawing the black line around?*/
						/* fields that touch the edge: Paint your edge but leave space at the corners*/
				//paint.setColor(Color.GREEN);

				if (isLeft) {
					float x = getCurrentLeftMargin() + p.getX() *  fieldSizeAndSpacing - i;

					float startY = getCurrentTopMargin()  +  p.getY()      * fieldSizeAndSpacing + edgeRadius;
					float  stopY = getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing();
					canvas.drawLine(x, startY,     x, stopY, paint);
				}
				if (isRight) {
					float x = getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - 1 + i;

					float startY = getCurrentTopMargin()  +  p.getY()      * fieldSizeAndSpacing + edgeRadius;
					float  stopY = getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing();
					canvas.drawLine(x, startY,     x, stopY, paint);
				}
				if (isTop) {
					float startX = getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing + edgeRadius;
					float stopX  = getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing();

					float y = getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing - i;
					canvas.drawLine(startX, y,    stopX, y, paint);
				}
				if (isBottom) {
					float startX = getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing + edgeRadius;
					float stopX  = getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing();

					float y = getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - 1 + i;
					canvas.drawLine(startX, y,    stopX, y, paint);
				}

						/* Fields at corners of their block draw a circle for a round circumference*/
						/*TopLeft*/
				if (isLeft && isTop) {
					//paint.setColor(Color.MAGENTA);
					canvas.drawCircle(
							getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing + edgeRadius, //center-x
							getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing + edgeRadius, //center-y
							edgeRadius + i,                                                                                          //radius
							paint);
				}

						/* Top Right*/
				if (isRight && isTop) {
					//paint.setColor(Color.BLUE);
					canvas.drawCircle(
							getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - edgeRadius,
							getCurrentTopMargin()  +  p.getY()      * fieldSizeAndSpacing + edgeRadius,
							edgeRadius + i,
							paint);
				}

						/*Bottom Left*/
				if (isLeft && isBottom) {
					//paint.setColor(Color.CYAN);
					canvas.drawCircle(
							getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing + edgeRadius,
							getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing(),
							edgeRadius + i,
							paint);
				}

						/*BottomRight*/
				if (isRight && isBottom) {
					//paint.setColor(Color.RED);
					canvas.drawCircle(
							getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing(),
							getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing(),
							edgeRadius + i,
							paint);
				}


				//paint.setColor(Color.YELLOW);
						/*Now filling the edges (if there's no corner we still leave a gap. that gap is being filled now ) */
				boolean belowRightMember = c.includes(Position.get(p.getX() + 1, p.getY() + 1));
						/*For a field on the right border, initializeWith edge to neighbour below
						 *
						 * !isBottom excludes:      corner to the left -> no neighbour directly below i.e. unwanted filling
						 *  3rd condition excludes: corner to the right-> member below right          i.e. unwanted filling
						 *
						 * */
				if (isRight && !isBottom && !belowRightMember) {
					canvas.drawLine(
							getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - 1 + i,
							getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - edgeRadius,
							getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - 1 + i,
							getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing + edgeRadius,
							paint);
				}
						/*For a field at the bottom, initializeWith edge to right neighbour */
				if (isBottom && !isRight && !belowRightMember) {
					canvas.drawLine(
							getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing(),
							getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - 1 + i,
							getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing + edgeRadius,
							getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - getCurrentSpacing() - 1 + i,
							paint);
				}

						/*For a field on the left border, initializeWith edge to upper neighbour*/
				if (isLeft && !isTop && (p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
					canvas.drawLine(
							getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing - i,
							getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing - getCurrentSpacing() - edgeRadius,
							getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing - i,
							getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing + edgeRadius,
							paint);
				}
						/*For a field at the top initializeWith to the left*/
				if (isTop && !isLeft && (p.getY() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
					canvas.drawLine(
							getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing - edgeRadius - getCurrentSpacing(),
							getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing - i,
							getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing + edgeRadius,
							getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing - i,
							paint);
				}
			}
		}


	}


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

		this.leftMargin = (width - sudokuType.getSize().getX() *
				(this.getCurrentFieldViewSize() + spacing) + spacing)
				/ 2;
		this.topMargin = (height - sudokuType.getSize().getY() *
				(this.getCurrentFieldViewSize() + spacing) + spacing)
				/ 2;
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
	 * Gibt das Array der SudokuFieldViews zurück.
	 * 
	 * @return Das Array der SudokuFieldViews
	 */
	public SudokuFieldView[][] getSudokuFieldViews() {
		return this.sudokuFieldViews;
	}

	/**
	 * Setzt den aktuellen Zoom-Faktor für diese View und refresh sie.
	 * 
	 * @param factor
	 *            Der Zoom-Faktor
	 */
	public boolean zoom(float factor) {
		this.zoomFactor = factor;
		refresh();
		return true;
	}

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
	int getCurrentFieldViewSize() {
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
			this.sudokuFieldViews[p.getX()][p.getY()].registerListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(FieldInteractionListener listener) {
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		for (Position p: sudokuType.getValidPositions())
			this.sudokuFieldViews[p.getX()][p.getY()].removeListener(listener);
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
		return this.game.getSudoku().getSudokuType().getSize().getX() / 2.0f;
	}

}
