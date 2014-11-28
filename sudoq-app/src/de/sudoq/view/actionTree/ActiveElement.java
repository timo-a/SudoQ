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
 * Diese Subklasse des ActionTreeElements definiert das Aussehen des aktuellen
 * Elements innerhalb des Aktionsbaumes.
 */
public class ActiveElement extends ActionTreeElementView {

	/**
	 * Die Farbe für das aktive Element
	 */
	public static final int ACTIVE_COLOR = 0xFFFF4B00;

	/** Constructors */

	public ActiveElement(Context context, ActionTreeElementView inner, ActionTreeElement ate) {
		super(context, inner, ate);
	}

	/** Methods */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintCanvas(Canvas canvas) {
		Paint activePaint = new Paint();
		activePaint.setColor(ACTIVE_COLOR);
		int stroke = (int) ((float) ActionTreeController.MAX_ELEMENT_VIEW_SIZE * 0.1);
		activePaint.setStrokeWidth(stroke);
		activePaint.setStyle(Style.STROKE);
		activePaint.setAntiAlias(true);
		int radius = (int) ((float) ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2.1);

		canvas.drawCircle(ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, radius, activePaint);
	}
}
