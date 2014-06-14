/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import de.sudoq.R;
import de.sudoq.controller.tutorial.TutorialActivity;
import de.sudoq.model.files.FileManager;

/**
 * Eine ListActivity, welche die für einwandfreie Funktionalität der SudoQ-App
 * notwendigen Initialisierungsarbeiten ausführt.
 */
public class SudoqListActivity extends SherlockListActivity {


	/**
	 * Initialisiert eine neue Activity, setzt dabei die für die App notwendigen
	 * System-Properties und initialisiert den FileManager.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
		FileManager.initialize(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE),
				getDir(getString(R.string.path_rel_sudokus), Context.MODE_PRIVATE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	/**
	 * Erstellt das Optionsmenü mit einem Tutorial-Eintrag.
	 * 
	 * @param menu
	 *            Das Menü
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_standard, menu);    
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Verarbeitet das Auswählen des Tutorial-Menüeintrags.
	 * 
	 * @param item
	 *            Das ausgewählte Menü-Item
	 * @return true
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_show_tutorial:
			startActivity(new Intent(this, TutorialActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}