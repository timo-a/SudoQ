/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.actionTree;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import de.sudoq.controller.sudoku.ActionTreeController;
import de.sudoq.model.actionTree.ActionTreeElement;

/**
 * Diese Subklasse des ActionTreeElements definiert die Erscheinung eines
 * gespeicherten Lesezeichens im Aktionsbaum.
 */
public class BranchingElement extends ActionTreeElementView {

	/** Constructors */

	public BranchingElement(Context context, ActionTreeElementView inner, ActionTreeElement ate) {
		super(context, inner, ate);
	}

	/** Methods */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintCanvas(Canvas canvas) {
		Paint elementPaint = new Paint();
		elementPaint.setColor(actionColor);
		elementPaint.setStyle(Style.STROKE);
		elementPaint.setStrokeWidth(8);
		elementPaint.setAntiAlias(true);
		int radius = (int) ((float) ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 3);
		canvas.drawCircle(ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, radius, elementPaint);

		// Paint innerPaint = new Paint();
		// innerPaint.setColor(actionColor);
		// innerPaint.setAntiAlias(true);
		// int innerRadius = (int) ((float)
		// ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 6);
		// canvas.drawCircle(ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2,
		// ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, innerRadius,
		// innerPaint);
	}
}
