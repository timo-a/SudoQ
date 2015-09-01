/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import de.sudoq.R;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Field;
import de.sudoq.view.actionTree.ActionElement;
import de.sudoq.view.actionTree.ActionTreeElementView;
import de.sudoq.view.actionTree.ActiveElement;
import de.sudoq.view.actionTree.BookmarkedElement;
import de.sudoq.view.actionTree.BranchingElement;

public class TutorialActionTreeActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_actiontree);

		RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(68, 68);
		viewLayoutParams.topMargin = 0;
		viewLayoutParams.leftMargin = 0;

		ActionTreeElementView normal = new ActionElement(this, null, new ActionTreeElement(0, new SolveActionFactory().createAction(0, new Field(1, 9)), null));
		ActionTreeElement dummyATE = new ActionTreeElement(0, new SolveActionFactory().createAction(0, new Field(1, 9)), null);
		                             
		ActionTreeElementView view = new BookmarkedElement(this, normal, dummyATE);
		view.setLayoutParams(viewLayoutParams);
		//((RelativeLayout) findViewById(R.id.tutorial_bookmark)).addView(view);

		view = normal;
		view.setLayoutParams(viewLayoutParams);
		//((RelativeLayout) findViewById(R.id.tutorial_state)).addView(view);

		view = new ActiveElement(this, normal, dummyATE);
		view.setLayoutParams(viewLayoutParams);
		//((RelativeLayout) findViewById(R.id.tutorial_current_state)).addView(view);

		view = new BranchingElement(this, normal, dummyATE);
		view.setLayoutParams(viewLayoutParams);
		//((RelativeLayout) findViewById(R.id.tutorial_branching)).addView(view);
	}
}
