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
import de.sudoq.controller.sudoku.FieldInteractionListener;
import de.sudoq.controller.sudoku.FieldViewPainter;
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
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;

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

		inflateSudoku();
	}

	/**
	 * Erstellt die Anzeige des Sudokus.
	 */
	private void inflateSudoku() {
		FieldViewPainter.getInstance().flushMarkings();
		this.removeAllViews();

		Sudoku sudoku = this.game.getSudoku();
		SudokuType sudokuType = sudoku.getSudokuType();
		this.sudokuFieldViews = new SudokuFieldView[sudokuType.getSize().getX() + 1][sudokuType.getSize().getY() + 1];
		for (int x = 0; x <= sudokuType.getSize().getX(); x++) {
			for (int y = 0; y <= sudokuType.getSize().getY(); y++) {
				Field field = sudoku.getField(Position.get(x, y));
				if (field != null) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) this.getCurrentFieldViewSize(), (int) this.defaultFieldViewSize);
					params.topMargin = (y * (int) this.getCurrentFieldViewSize()) + y;
					params.leftMargin = (x * (int) this.getCurrentFieldViewSize()) + x;
					this.sudokuFieldViews[x][y] = new SudokuFieldView(context, game, field, this.game.isAssistanceAvailable(Assistances.markWrongSymbol));
					field.registerListener(this.sudokuFieldViews[x][y]);
					this.addView(this.sudokuFieldViews[x][y], params);
				} else if (sudoku.getSudokuType().getSize().getX() == x && sudoku.getSudokuType().getSize().getY() == y) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) this.getCurrentFieldViewSize(), (int) this.defaultFieldViewSize);
					params.topMargin = ((y - 1) * (int) this.getCurrentFieldViewSize()) + (y - 1) + getCurrentTopMargin();
					params.leftMargin = ((x - 1) * (int) this.getCurrentFieldViewSize()) + (x - 1) + getCurrentLeftMargin();
					this.sudokuFieldViews[x][y] = new SudokuFieldView(context, game, this.game.getSudoku().getField(Position.get(x - 1, y - 1)), this.game.isAssistanceAvailable(Assistances.markWrongSymbol));
					this.addView(this.sudokuFieldViews[x][y], params);
					this.sudokuFieldViews[x][y].setVisibility(INVISIBLE);
				}
			}
		}

		ArrayList<Constraint> allConstraints = this.game.getSudoku().getSudokuType().getConstraints();
		ArrayList<Position> positions = null;
		if (this.game.isAssistanceAvailable(Assistances.markRowColumn)) {
			for (int constrNum = 0; constrNum < allConstraints.size(); constrNum++) {
				if (allConstraints.get(constrNum).getType().equals(ConstraintType.LINE)) {
					positions = allConstraints.get(constrNum).getPositions();
					for (int i = 0; i < positions.size(); i++) {
						for (int k = 0; k < positions.size(); k++) {
							if (i != k)
								this.sudokuFieldViews[positions.get(i).getX()][positions.get(i).getY()].addConnectedField(
										this.sudokuFieldViews[positions.get(k).getX()][positions.get(k).getY()]);
						}
					}
				}
			}
		}
	}

	/**
	 * Berechnet das aktuelle Spacing (gem. dem aktuellen ZoomFaktor) und gibt
	 * es zurück.
	 * 
	 * @return Das aktuelle Spacing
	 */
	private int getCurrentSpacing() {
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
		if (this.sudokuFieldViews != null) {
			SudokuType sudokuType = this.game.getSudoku().getSudokuType();
			for (int x = 0; x <= sudokuType.getSize().getX(); x++) {
				for (int y = 0; y <= sudokuType.getSize().getY(); y++) {
					if (game.getSudoku().getField(Position.get(x, y)) != null) {
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this.sudokuFieldViews[x][y].getLayoutParams();
						params.width  = (int) this.getCurrentFieldViewSize();
						params.height = (int) this.getCurrentFieldViewSize();
						params.topMargin =  (int) (getCurrentTopMargin()  + (y * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						params.leftMargin = (int) (getCurrentLeftMargin() + (x * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						this.sudokuFieldViews[x][y].setLayoutParams(params);
						this.sudokuFieldViews[x][y].invalidate();
					} else if (game.getSudoku().getSudokuType().getSize().getX() == x && game.getSudoku().getSudokuType().getSize().getY() == y) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) this.getCurrentFieldViewSize(), (int) this.defaultFieldViewSize);
						params.width  = (int) this.getCurrentFieldViewSize();
						params.height = (int) this.getCurrentFieldViewSize();
						params.topMargin =  (int) (2 * getCurrentTopMargin()  + ((y - 1) * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						params.leftMargin = (int) (2 * getCurrentLeftMargin() + ((x - 1) * (this.getCurrentFieldViewSize() + getCurrentSpacing())));
						this.sudokuFieldViews[x][y].setLayoutParams(params);
						this.sudokuFieldViews[x][y].invalidate();
					}
				}
			}
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float edgeRadius = getCurrentFieldViewSize() / 20.0f;
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		ArrayList<Constraint> constraints = this.game.getSudoku().getSudokuType().getConstraints();
		ArrayList<Position> positions = null;
		Position p = null;
		Constraint c = null;
		for (int constrNum = 0; constrNum < constraints.size(); constrNum++) {
			if (constraints.get(constrNum).getType().equals(ConstraintType.BLOCK)) {
				c = constraints.get(constrNum);
				positions = c.getPositions();
				for (int pos = 0; pos < positions.size(); pos++) {
					p = positions.get(pos);
					boolean isLeft = p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY()));
					boolean isRight = !c.includes(Position.get(p.getX() + 1, p.getY()));
					boolean isTop = p.getY() == 0 || !c.includes(Position.get(p.getX(), p.getY() - 1));
					boolean isBottom = !c.includes(Position.get(p.getX(), p.getY() + 1));
					for (int i = 1; i <= getCurrentSpacing(); i++) {
						if (isLeft) {
							canvas.drawLine(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									paint);
						}
						if (isRight) {
							canvas.drawLine(getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									paint);
						}
						if (isTop) {
							canvas.drawLine(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									paint);
						}
						if (isBottom) {
							canvas.drawLine(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									paint);
						}

						if (isLeft && isTop) {
							canvas.drawCircle(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									edgeRadius + i, paint);
						} else if (isLeft && !isTop && (p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
							canvas.drawLine(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - edgeRadius,
									getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									paint);
						} else if (!isLeft && isTop && (p.getY() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
							canvas.drawLine(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - i,
									paint);
						}

						if (isRight && isTop) {
							canvas.drawCircle(getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - edgeRadius,
									getCurrentTopMargin() + p.getY() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									edgeRadius + i, paint);
						}

						if (isLeft && isBottom) {
							canvas.drawCircle(getCurrentLeftMargin() + p.getX() * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									edgeRadius + i, paint);
						}
						if (isRight && isBottom) {
							canvas.drawCircle(getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									edgeRadius + i, paint);
						} else if (isRight && !isBottom && !c.includes(Position.get(p.getX() + 1, p.getY() + 1))) {
							canvas.drawLine(getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - edgeRadius,
									getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									paint);
						} else if (!isRight && isBottom && !c.includes(Position.get(p.getX() + 1, p.getY() + 1))) {
							canvas.drawLine(getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - edgeRadius - getCurrentSpacing(),
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									getCurrentLeftMargin() + (p.getX() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) + edgeRadius,
									getCurrentTopMargin() + (p.getY() + 1) * (int) (this.getCurrentFieldViewSize() + getCurrentSpacing()) - getCurrentSpacing() - 1 + i,
									paint);
						}
					}
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
		int size = width < height ? width : height;
		int numberOfFields = width < height ? sudokuType.getSize().getX() : sudokuType.getSize().getY();
		this.defaultFieldViewSize = (size - (numberOfFields + 1) * spacing) / numberOfFields;
		// this.currentFieldViewSize = this.defaultFieldViewSize;

		this.leftMargin = (int) (width - sudokuType.getSize().getX() *
				(this.getCurrentFieldViewSize() + spacing) + spacing)
				/ 2;
		this.topMargin = (int) (height - sudokuType.getSize().getY() *
				(this.getCurrentFieldViewSize() + spacing) + spacing)
				/ 2;
		Log.d(LOG_TAG, "Sudoku width: " + width);
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
	private int getCurrentFieldViewSize() {
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
		for (int x = 0; x < sudokuType.getSize().getX(); x++) {
			for (int y = 0; y < sudokuType.getSize().getY(); y++) {
				if (this.game.getSudoku().getField(Position.get(x, y)) != null)
					this.sudokuFieldViews[x][y].registerListener(listener);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(FieldInteractionListener listener) {
		SudokuType sudokuType = this.game.getSudoku().getSudokuType();
		for (int x = 0; x < sudokuType.getSize().getX(); x++) {
			for (int y = 0; y < sudokuType.getSize().getY(); y++) {
				if (this.game.getSudoku().getField(Position.get(x, y)) != null)
					this.sudokuFieldViews[x][y].removeListener(listener);
			}
		}
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
