/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import de.sudoq.R;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.controller.menus.preferences.PlayerPreferencesActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.profile.Profile;

/**
 * Verwaltet das Hauptmenü der App.
 */
public class MainActivity extends SudoqActivitySherlock {

	/**
	 * Der Log-Tag für den LogCat
	 */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	/** Methods */

	/**
	 * Wird beim ersten Anzeigen des Hauptmenüs aufgerufen. Inflated das Layout.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
		setContentView(R.layout.mainmenu);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.launcher);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        onResume();
	}

	/**
	 * Wird aufgerufen, falls die Acitivity wieder den Eingabefokus erhält.
	 */
	@Override
	public void onResume() {
		super.onResume();
		//Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
		Profile p = Profile.getInstance();

		Button continueButton = (Button) findViewById(R.id.button_mainmenu_continue);
		continueButton.setEnabled(p.getCurrentGame() > Profile.NO_GAME);

		Button loadButton = (Button) findViewById(R.id.button_mainmenu_load_sudoku);
		loadButton.setEnabled(p.getCurrentGame() > Profile.NO_GAME);

	}

	/**
	 * Wechselt zu einer Activity, entsprechend der Auswahl eines Menübuttons.
	 * Ist der übergebene Button null oder unbekannt, so wird nichts getan.
	 * 
	 * @param button
	 *            Vom Benutzer ausgewählter Menübutton
	 */
	public void switchActivity(View button) {
		switch (button.getId()) {
		case R.id.button_mainmenu_new_sudoku:
			Intent newSudokuIntent = new Intent(this, NewSudokuConfigurationActivity.class);
			startActivity(newSudokuIntent);
			break;

		case R.id.button_mainmenu_continue:
			Intent continueSudokuIntent = new Intent(this, SudokuActivity.class);
			startActivity(continueSudokuIntent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;

		case R.id.button_mainmenu_load_sudoku:
			Intent loadSudokuIntent = new Intent(this, SudokuLoadingActivity.class);
			startActivity(loadSudokuIntent);
			break;

		case R.id.button_mainmenu_profile:
			Intent preferencesIntent = new Intent(this, PlayerPreferencesActivity.class);
			startActivity(preferencesIntent);
			break;
		}
	}

	/*
	 * {@inheritDoc}
	 */
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	// }
}
