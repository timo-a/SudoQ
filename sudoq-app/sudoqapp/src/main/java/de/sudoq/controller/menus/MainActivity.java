/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import de.sudoq.R;
import de.sudoq.controller.SudoqCompatActivity;
import de.sudoq.controller.menus.preferences.LanguageSetting;
import de.sudoq.controller.menus.preferences.LanguageUtility;
import de.sudoq.controller.menus.preferences.PlayerPreferencesActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.game.GameManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.ProfileManager;

/**
 * Verwaltet das Hauptmenü der App.
 */
public class MainActivity extends SudoqCompatActivity {

	/**
	 * Der Log-Tag für den LogCat
	 */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	/**
	 * stores language at activity start to compare if language changed in advanced preferences
	 */
	private LanguageSetting currentLanguageCode;

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

        /* load language from profile */

		//retrieve language from profile at beginning of activity lifecycle
		currentLanguageCode = LanguageUtility.loadLanguageFromSharedPreferences2(this);

		String a = Locale.getDefault().getLanguage();
		String b = getResources().getConfiguration().locale.getLanguage();
		String c = currentLanguageCode.toStorableString();


		if (currentLanguageCode.isSystemLanguage()) {
			//nothing to do
			//locale is adopted automatically from device
		} else {
			LanguageSetting.LanguageCode currentConf = LanguageUtility.getConfLocale(this);
			if (!currentConf.equals(currentLanguageCode.language)) {
				//if conf != loaded language, set conf and update
				LanguageUtility.setConfLocale(currentLanguageCode.language.name(), this);
				Intent refresh = new Intent(this, this.getClass());
				this.finish();
				this.startActivity(refresh);
			}
		}

		Log.d("lang", "MainActivity.onCreate() set with " + currentLanguageCode);
	}

	/**
	 * Wird aufgerufen, falls die Acitivity wieder den Eingabefokus erhält.
	 */
	@Override
	public void onResume() {
		super.onResume();
		//Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
		File profilesFile = getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE);
		ProfileManager pm = new ProfileManager(profilesFile);
		if (pm.noProfiles()) {
			throw new IllegalStateException("there are no profiles. this is  unexpected. they should be initialized in splashActivity");
		}
		pm.loadCurrentProfile();


		Button continueButton = (Button) findViewById(R.id.button_mainmenu_continue);
		continueButton.setEnabled(pm.getCurrentGame() > Profile.NO_GAME);


		Button loadButton = (Button) findViewById(R.id.button_mainmenu_load_sudoku);
		//loadButton.setEnabled(!gm.getGameList().isEmpty());
		loadButton.setEnabled(true);

		//load language from memory
		LanguageSetting.LanguageCode fromConf = LanguageUtility.getConfLocale(this);

		if (!fromConf.equals( currentLanguageCode.language)){
			Log.d("lang","refresh because " + currentLanguageCode + " -> " + fromConf);

			Intent refresh = new Intent(this, this.getClass());
			this.finish();
			this.startActivity(refresh);
		}

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
			Intent newSudokuIntent = new Intent(this, NewSudokuActivity.class);
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
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// check if configuration has changed
		// per Manifest this method gets called if there are changes in layoutDirection or locale
		// (if we only check for locale, this method doesn't get called, no idea why https://stackoverflow.com/a/27648673/3014199)
		//
		if (!newConfig.locale.getLanguage().equals(currentLanguageCode.language.name())){
			//only adopt external change if language is set to "system language"
			if (currentLanguageCode.isSystemLanguage()){
				//adopt change
				currentLanguageCode.language = LanguageUtility.loadLanguageFromLocale();
				//store changes
				LanguageUtility.storeLanguageToMemory2(this, currentLanguageCode);
			}
		}
	}

}
