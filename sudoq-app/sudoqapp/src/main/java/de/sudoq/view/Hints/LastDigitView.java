/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.Hints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import java.util.List;
import java.util.Stack;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.NakedSetDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.view.SudokuLayout;

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 */
public class LastDigitView extends View {

	/** Attributes */
	private SudokuLayout sl;

	private View constraintV;

	/**
	 * Erstellt einen LastDigitView
	 *
	 * @param context    der Applikationskontext
	 * @throws IllegalArgumentException Wird geworfen, falls eines der Argumente null ist
	 */
	public LastDigitView(Context context, SudokuLayout sl, SolveDerivation d) {
		super(context);
		if (context == null) throw new IllegalArgumentException();

		List<DerivationBlock> db = d.getDerivationBlocks();
		constraintV = new HighlightedConstraintView(context, sl, db.get(0).getBlock(), Color.BLUE);

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

		constraintV.draw(canvas);
	}
}