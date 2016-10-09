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
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import de.sudoq.controller.sudoku.Symbol;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 */
public class HighlightedConstraintView extends View {

	/** Attributes */

	/**
	 * The Constraint, represented by this View
	 *
	 * @see Constraint
	 */
	private Constraint constraint;

	/**
	 * Color of the margin
	 */
	private int marginColor;

	private SudokuLayout sl;

	private Paint paint = new Paint();
	private RectF oval = new RectF();
	/** Constructors */

	/**
	 * Erstellt einen SudokuFieldView und initialisiert die Attribute der
	 * Klasse.
	 *
	 * @param context    der Applikationskontext
	 * @param constraint constraint represented
	 * @param color      Color of the margin
	 * @throws IllegalArgumentException Wird geworfen, falls eines der Argumente null ist
	 */
	public HighlightedConstraintView(Context context, SudokuLayout sl, Constraint constraint, int color) {
		super(context);
		if (context == null || constraint == null) throw new IllegalArgumentException();

		this.constraint = constraint;
		this.marginColor = color;
		this.sl = sl;
	}

	/** Methods */

	/**
	 * Zeichnet den Inhalt des Feldes auf das Canvas dieses SudokuFieldViews.
	 * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
	 * Canvas Objekt vorzunehmen.
	 *
	 * @param canvas Das Canvas Objekt auf das gezeichnet wird
	 * @throws IllegalArgumentException Wird geworfen, falls das übergebene Canvas null ist
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float edgeRadius = sl.getCurrentFieldViewSize() / 20.0f;
		paint.reset();
		paint.setColor(marginColor);
		int thickness = 20;
		paint.setStrokeWidth(thickness*sl.getCurrentSpacing());
		Constraint c = this.constraint;
		//Log.d("HighlightCV", "This is happening!");

		//canvas.drawLine(0, 0, 600, 600, paint);

		for (Position p : c) {
					/* determine whether the position p is in the (right|left|top|bottom) border of its block constraint.
					 * test for 0 to avoid illegalArgExc for neg. vals
					 * careful when trying to optimize this definition: blocks can be squiggly (every additional compound to row/col but extra as in hypersudoku is s.th. different)
					 * */
			boolean isLeft   = p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY()));
			boolean isRight  =                  !c.includes(Position.get(p.getX() + 1, p.getY()));
			boolean isTop    = p.getY() == 0 || !c.includes(Position.get(p.getX(), p.getY() - 1));
			boolean isBottom =                  !c.includes(Position.get(p.getX(), p.getY() + 1));
					/* apparently:
					 *   00 10 20 30 ...
					 *   01 11
					 *   02    xy
					 *   .
					 *   .
					 * */


			//deklariert hier, weil wir es nicht früher brauchen, effizienter wäre weiter oben
			int fieldSizeAndSpacing = sl.getCurrentFieldViewSize() + sl.getCurrentSpacing();
					/* these first 4 seem similar. drawing the black line around?*/
					/* fields that touch the edge: Paint your edge but leave space at the corners*/
				//paint.setColor(Color.GREEN);

			float  leftX = sl.getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing - sl.getCurrentSpacing()/2 ;
			float rightX = sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - sl.getCurrentSpacing()/2;

			float    topY = sl.getCurrentTopMargin() +  p.getY()      * fieldSizeAndSpacing - sl.getCurrentSpacing()/2;
			float bottomY = sl.getCurrentTopMargin() + (p.getY() + 1) * fieldSizeAndSpacing - sl.getCurrentSpacing()/2;

			if (isLeft) {
				float startY = sl.getCurrentTopMargin() +  p.getY()      * fieldSizeAndSpacing + edgeRadius;
				float  stopY = sl.getCurrentTopMargin() + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
				canvas.drawLine(leftX, startY, leftX, stopY, paint);
			}
			if (isRight) {
				float startY = sl.getCurrentTopMargin() +  p.getY()      * fieldSizeAndSpacing + edgeRadius;
				float  stopY = sl.getCurrentTopMargin() + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
				canvas.drawLine(rightX, startY, rightX, stopY, paint);
			}
			if (isTop) {
				float startX = sl.getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing + edgeRadius;
				float  stopX = sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();

				canvas.drawLine(startX, topY, stopX, topY, paint);
			}
			if (isBottom) {
				float startX = sl.getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing + edgeRadius;
				float  stopX = sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();

				canvas.drawLine(startX, bottomY, stopX, bottomY, paint);
			}

					/* Fields at corners of their block draw a circle for a round circumference*/

			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			float radius  = edgeRadius +sl.getCurrentSpacing()/2;
			short angle = 90+10;
			/*TopLeft*/
			if (isLeft && isTop) {
				float centerX = sl.getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing + edgeRadius;
				float centerY = sl.getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing + edgeRadius;

				oval.set( centerX - radius, centerY - radius, centerX + radius, centerY + radius);
				canvas.drawArc(oval, 180 -5, angle, false, paint);
			}

			/* Top Right*/
			if (isRight && isTop) {
				float centerX = sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - sl.getCurrentSpacing() - edgeRadius;
				float centerY = sl.getCurrentTopMargin()  +  p.getY()      * fieldSizeAndSpacing + edgeRadius;

				oval.set( centerX - radius, centerY - radius, centerX + radius, centerY + radius);
				canvas.drawArc(oval, 270 -5, angle, false, paint);
			}

					/*Bottom Left*/
			if (isLeft && isBottom) {
				float centerX = sl.getCurrentLeftMargin() +  p.getX()      * fieldSizeAndSpacing + edgeRadius;
				float centerY = sl.getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();

				oval.set( centerX - radius, centerY - radius, centerX + radius, centerY + radius);
				canvas.drawArc(oval, 90 -5, angle, false, paint);

			}

					/*BottomRight*/
			if (isRight && isBottom) {
				float centerX = sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();
				float centerY = sl.getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing();

				oval.set( centerX - radius, centerY - radius, centerX + radius, centerY + radius);
				canvas.drawArc(oval, 0 -5, angle, false, paint);
			}

			paint.setColor(Color.BLUE);

					/*Now filling the edges (if there's no corner we still leave a gap. that gap is being filled now ) */
			boolean belowRightMember = c.includes(Position.get(p.getX() + 1, p.getY() + 1));
					/*For a field on the right border, initializeWith edge to neighbour below
					 *
					 * !isBottom excludes:      corner to the left -> no neighbour directly below i.e. unwanted filling
					 *  3rd condition excludes: corner to the right-> member below right          i.e. unwanted filling
					 *
					 * */
			/*  */
			if (isRight && !isBottom && !belowRightMember) {
				canvas.drawLine(
						rightX,
						sl.getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing - sl.getCurrentSpacing() - edgeRadius,
						rightX,
						sl.getCurrentTopMargin()  + (p.getY() + 1) * fieldSizeAndSpacing + edgeRadius,
						paint);
			}
					/*For a field at the bottom, initializeWith edge to right neighbour */
			if (isBottom && !isRight && !belowRightMember) {
				canvas.drawLine(
						sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing(),
						bottomY,
						sl.getCurrentLeftMargin() + (p.getX() + 1) * fieldSizeAndSpacing + edgeRadius,
						bottomY,
						paint);
			}
						/*For a field on the left border, initializeWith edge to upper neighbour*/
			if (isLeft && !isTop && (p.getX() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
				canvas.drawLine(
				                leftX
				               ,sl.getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing - sl.getCurrentSpacing() - edgeRadius
				               ,leftX
				               ,sl.getCurrentTopMargin()  + p.getY() * fieldSizeAndSpacing + edgeRadius
				               ,paint);
			}

					/*For a field at the top initializeWith to the left*/
			if (isTop && !isLeft && (p.getY() == 0 || !c.includes(Position.get(p.getX() - 1, p.getY() - 1)))) {
				canvas.drawLine(sl.getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing - edgeRadius - sl.getCurrentSpacing()
				               ,topY
				               ,sl.getCurrentLeftMargin() + p.getX() * fieldSizeAndSpacing + edgeRadius
				               ,topY
				               ,paint
				               );
			}
		}

		/* uncomment to paint focuspoint of zooming
		paint.setStyle(Paint.Style.FILL);
		float z = sl.getZoomFactor();
		canvas.drawCircle(sl.focusX * z
		                 ,sl.focusY * z , z * 20, paint);
		paint.reset();*/


	}







	/** TODO may come in handy later for highlighting notes. or do that seperately
	 * Zeichnet die Notizen in dieses Feld
	 *
	 * @param canvas
	 *            Das Canvas in das gezeichnet werde nsoll
	 *
	 * @param field
	 *            Das Canvas in das gezeichnet werde nsoll
	 */
	private void drawNotes(Canvas canvas, Field field) {
		Paint notePaint = new Paint();
		notePaint.setAntiAlias(true);
		int noteTextSize = getHeight() / Symbol.getInstance().getRasterSize();
		notePaint.setTextSize(noteTextSize);
		notePaint.setTextAlign(Paint.Align.CENTER);
		notePaint.setColor(Color.BLACK);
		for (int i = 0; i < Symbol.getInstance().getNumberOfSymbols(); i++) {
			if (field.isNoteSet(i)) {
				String note = Symbol.getInstance().getMapping(i);
				canvas.drawText(note + "", (i % Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize / 2, (i / Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize, notePaint);
			}
		}
	}


}
