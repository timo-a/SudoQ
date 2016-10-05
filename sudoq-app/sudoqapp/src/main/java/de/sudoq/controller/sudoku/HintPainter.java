/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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
	/** Constructors */

	public HintPainter(SudokuLayout sl) {
		this.viewList = new Vector<>();
		this.context  = sl.getContext();
		this.sl = sl;
	}

	public void realizeHint(SolveDerivation sd){
		switch(sd.getType()){
			case LastDigit: List<DerivationBlock> db = sd.getDerivationBlocks();
			                View v = new HighlightedConstraintView(context, sl, db.get(0).getBlock(), Color.BLUE);
			                viewList.add(v);
					        sl.addView(v, sl.getHeight(), sl.getWidth());
			                break;



		}
	}

	/** Methods */

	public void invalidateAll(){
		//Log.d(LOG_TAG, viewList.size()+" hints to be drawn");

		for (View v : viewList)
			v.invalidate();
	}


	/**
	 * Delete all hints from object-internal storage as well as from the SudokuLayout
	 */
	public void deleteAll() {
		for (View v : viewList)
			sl.removeView(v);
		this.viewList.clear();
	}

	public void updateLayout(){
		for (View v : viewList) {
			v.getLayoutParams().height = sl.getHeight();
			v.getLayoutParams().width  = sl.getWidth();
		}
	}

}