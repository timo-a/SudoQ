/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import de.sudoq.R;
import de.sudoq.controller.menus.NewSudokuActivity;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.profile.Profile;

/**
 * Wird aufgerufen in Hauptmenü-> neues Sudoku -> einstellungen
 */
public class NewSudokuPreferencesActivity extends PreferencesActivity {

	/* shortcut for NewSudokuActivity.gameSettings */
	GameSettings confSettings;

	/**
	 * stores language at activity start to compare if language changed in advanced preferences
	 */
	private LanguageSetting currentLanguageCode;

	/**
	 * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. ?Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.?
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.preferences_newsudoku);
		Log.i("gameSettings", "NewSudokuPreferencesActivity onCreate beginning");

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.launcher);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);


		gesture =            (CheckBox) findViewById(R.id.checkbox_gesture);
		autoAdjustNotes =    (CheckBox) findViewById(R.id.checkbox_autoAdjustNotes);
		markRowColumn =      (CheckBox) findViewById(R.id.checkbox_markRowColumn);
		markWrongSymbol =    (CheckBox) findViewById(R.id.checkbox_markWrongSymbol);
		restrictCandidates = (CheckBox) findViewById(R.id.checkbox_restrictCandidates);
		Log.i("gameSettings", "NewSudokuPreferencesActivity onCreate end is gameSettings null?" +(NewSudokuActivity.gameSettings == null));
		Log.d("gameSettings", "NewSudokuPreferencesActivity onCreate end is gameSettings null?" +(NewSudokuActivity.gameSettings == null));

		confSettings = NewSudokuActivity.gameSettings;
        gesture.           setChecked(confSettings.isGesturesSet());
		autoAdjustNotes.   setChecked(confSettings.getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.     setChecked(confSettings.getAssistance(Assistances.markRowColumn));
		markWrongSymbol.   setChecked(confSettings.getAssistance(Assistances.markWrongSymbol));
		restrictCandidates.setChecked(confSettings.getAssistance(Assistances.restrictCandidates));

		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		assert p != null;
		p.registerListener(this);

		//set and store language at beginning of activity lifecycle
		currentLanguageCode = LanguageUtility.loadLanguageFromSharedPreferences(this);

	}

	@Override
	public void onResume() {
		super.onResume();

		//load language from memory
		LanguageSetting fromMemory = LanguageUtility.loadLanguageFromSharedPreferences(this);
		if (!fromMemory.language.equals(currentLanguageCode.language)){
			Intent refresh = new Intent(this, this.getClass());
			this.finish();
			this.startActivity(refresh);
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
         * Aktualisiert die Werte in den Views
         */
	protected void refreshValues() {
		/*
		gesture.           setChecked(confSettings.isGesturesSet());
		autoAdjustNotes.   setChecked(confSettings.getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.     setChecked(confSettings.getAssistance(Assistances.markRowColumn));
		markWrongSymbol.   setChecked(confSettings.getAssistance(Assistances.markWrongSymbol));
		restrictCandidates.setChecked(confSettings.getAssistance(Assistances.restrictCandidates));
		*/}



	/**
	 * Saves currend state of buttons/checkboxes to gameSettings
	 */
	protected void adjustValuesAndSave() {
		confSettings.setGestures(gesture.isChecked());
		saveCheckbox(autoAdjustNotes,    Assistances.autoAdjustNotes, confSettings);
		saveCheckbox(markRowColumn,      Assistances.markRowColumn,   confSettings);
		saveCheckbox(markWrongSymbol,    Assistances.markWrongSymbol, confSettings);
		saveCheckbox(restrictCandidates, Assistances.restrictCandidates, confSettings);
		//confSettings.setHelper();
		//confSettings.setCrash();
		//todo singleton not necessary
		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		p.saveChanges();
	}
	


	/**
	 * Speichert die Profiländerungen
	 * @param view
	 *            unbenutzt
	 */
	public void saveChanges(View view) {
		saveToProfile();
		onBackPressed();//go back to parent activity
	}

    protected void saveToProfile() {
		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));

        p.setGestureActive(gesture.isChecked());
		saveAssistance(Assistances.autoAdjustNotes,    autoAdjustNotes);
		saveAssistance(Assistances.markRowColumn,      markRowColumn  );
		saveAssistance(Assistances.markWrongSymbol,    markWrongSymbol);
		saveAssistance(Assistances.restrictCandidates, restrictCandidates);

		p.setHelperActive(confSettings.isHelperSet());
		p.setLefthandActive(confSettings.isLefthandModeSet());

        //restrict types is automatically saved to profile...
        p.saveChanges();
    }

	/* parameter View only needed to be foud by xml who clicks this*/
	public void switchToAdvancedPreferences(View view){
		
		Intent advIntent = new Intent(this, AdvancedPreferencesActivity.class);
        AdvancedPreferencesActivity.caller= AdvancedPreferencesActivity.ParentActivity.NEW_SUDOKU;
		startActivity(advIntent);

	}
}