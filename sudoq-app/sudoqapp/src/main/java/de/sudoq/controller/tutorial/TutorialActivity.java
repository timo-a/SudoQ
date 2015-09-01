/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.tutorial;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;

import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import de.sudoq.R;

public class TutorialActivity extends ActionBarActivity {

	private ActionBar.Tab createTab(int text, int icon){
		ActionBar.Tab tab = getSupportActionBar().newTab();
		if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE )
			tab.setText(text);
		else
			tab.setIcon(icon);
		
		tab.setTabListener(new MyTabListener());
		return tab;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onCreate(Bundle savedInstanceState) {
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tutorial);

		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		/* sudoku tab */
		bar.addTab(createTab(R.string.sf_tutorial_sudoku_title, R.drawable.sudoku_tutorial));

		/* assistances tab */
		bar.addTab(createTab(R.string.sf_tutorial_assistances_title, R.drawable.help_tutorial));

		/* action tree tab */
		bar.addTab(createTab(R.string.sf_tutorial_action_title, R.drawable.action_tree_tutorial));
		
		getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

	}

	private class MyTabListener implements ActionBar.TabListener
	{
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Fragment frag;
			switch (tab.getPosition()){
			case 0:
				frag = new FragmentSudoku();
				break;
			
			case 1:
				frag = new FragmentAssistances();
				break;
			
			default:
				frag = new FragmentActionTree();
			}
			ft.replace(android.R.id.content, frag);
			
		}
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
		}
	}
}