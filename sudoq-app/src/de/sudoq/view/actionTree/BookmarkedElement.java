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
import de.sudoq.controller.sudoku.ActionTreeController;
import de.sudoq.model.actionTree.ActionTreeElement;

/**
 * Diese Subklasse des ActionTreeElements definiert das Aussehen eines als
 * Lesezeichen markierten Elements.
 */
public class BookmarkedElement extends ActionTreeElementView {

	/**
	 * Die Farbe der Bookmark-Elemente
	 */
	private static final int BOOKMARK_COLOR = 0xFFFF4B00;

	public BookmarkedElement(Context context, ActionTreeElementView inner, ActionTreeElement ate) {
		super(context, inner, ate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintCanvas(Canvas canvas) {
		// Paint elementPaint = new Paint();
		// elementPaint.setColor(BOOKMARK_COLOR);
		// elementPaint.setAntiAlias(true);
		// int radius = (int) ((float)
		// ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 3.1);
		// canvas.drawCircle(ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2,
		// ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, radius,
		// elementPaint);

		Paint innerPaint = new Paint();
		innerPaint.setColor(actionColor);
		innerPaint.setAntiAlias(true);
		int innerRadius = (int) (((float) ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 6) * 1.42);
		canvas.drawCircle(ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, innerRadius, innerPaint);
	}
}
