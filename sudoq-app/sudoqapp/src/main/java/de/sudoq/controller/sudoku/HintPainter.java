/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.view.HighlightedConstraintView;
import de.sudoq.view.SudokuLayout;

/**
 * Manages all hintrelated views
 */
public class HintPainter {
	/** Attributes */

	private static final String LOG_TAG = HintPainter.class.getSimpleName();

	Vector<View> viewList;
	private Context context;
	private SudokuLayout sl;
	private Canvas canvas;
	/** Constructors */

	/**
	 * Privater Konstruktor, da diese Klasse statisch ist.
	 */
	public HintPainter(Context context, SudokuLayout sl/*, Canvas canvas*/) {
		this.viewList = new Vector<>();
		this.context  = context;
		this.sl = sl;
//		this.canvas = canvas;
	}

	public void realizeHint(SolveDerivation sd){
		switch(sd.getType()){
			case LastDigit: List<DerivationBlock> db = sd.getDerivationBlocks();
			                View v = new HighlightedConstraintView(context, sl, db.get(0).getBlock(), Color.BLUE);
			                viewList.add(v);
							sl.addView(v);
			                break;


		}//		hintPainter.drawConstraints(canvas);
		sl.invalidate();
	}

	/** Methods */

	public void addView(View v){
		viewList.add(v);
	}

	public void debug(Canvas canvas){
		Paint p = new Paint();
		p.setColor(Color.GREEN);
		p.setStrokeWidth(20);
		canvas.drawLine(0, 100, 100, 0, p);
	}

	/**
	 * Draws all constraints
	 * 
	 * @param canvas
	 *            Das Canvas
	 */
	public void drawConstraints(Canvas canvas) {
		Log.d(LOG_TAG, "HintPainter.drawConstraints()");

		View vi = new View(context){
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);
				debug(canvas);
			}
		};
		vi.draw(canvas);

		for (View v : viewList) {
			v.draw(canvas);
			v.bringToFront();
			//v.getParent().requestLayout();
			//v.getParent().invalidate();
		}
	}


	/**
	 * Löscht alle hinzugefügten Markierungen auf Default.
	 */
	public void deleteAll() {
		this.viewList.clear();
	}

}