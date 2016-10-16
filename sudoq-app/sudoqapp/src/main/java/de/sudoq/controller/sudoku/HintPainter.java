/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.content.Context;
import android.view.View;

import java.util.Vector;

import de.sudoq.model.solverGenerator.solution.NakedSetDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.view.Hints.LastDigitView;
import de.sudoq.view.Hints.NakedSetView;
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
		View v=null;
		switch(sd.getType()){
			case LastDigit:   v = new LastDigitView(context, sl, sd);
			                  break;

			case NakedSingle:
			case NakedPair:
			case NakedTriple:
			case NakedQuadruple:
			case NakedQuintuple:   v = new NakedSetView(context, sl, (NakedSetDerivation)sd);
			                       break;


		}
		if(v!=null) {
			viewList.add(v);
			sl.addView(v, sl.getHeight(), sl.getWidth());
		}

	}

	/** Methods */

	public void invalidateAll(){
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

	/**
	 * Have this Object update all its views layouts from the changed sudokulayout object
	 * */
	public void updateLayout(){
		int newHeight = sl.getHeight();
		int newWidth  = sl.getWidth();
		for (View v : viewList) {
			v.getLayoutParams().height = newHeight;
			v.getLayoutParams().width  = newWidth;
		}
	}

}