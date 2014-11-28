/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller;

import de.sudoq.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import de.sudoq.controller.tutorial.TutorialActivity;
import de.sudoq.model.files.FileManager;

/**
 * Eine Activity, welche die für einwandfreie Funktionalität der SudoQ-App
 * notwendigen Initialisierungsarbeiten ausführt.
 */
public class SudoqActivity extends Activity {

	/**
	 * Konstante für den Tutorial Menü-Eintrag
	 */
	private static final int MENU_TUTORIAL = -1;

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
		menu.clear();
		prepareOptionsMenu(menu);
		return true;
	}

	/**
	 * Fügt dem übergebenen Menü die globalen Einträge (Tutorial) hinzu.
	 * 
	 * @param menu
	 *            Das Menü
	 */
	protected void prepareOptionsMenu(Menu menu) {
		menu.add(0, MENU_TUTORIAL, 0, getString(R.string.action_show_tutorial));
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
		if (item.getItemId() == MENU_TUTORIAL) {
			startActivity(new Intent(this, TutorialActivity.class));
		}
		return true;
	}

}
