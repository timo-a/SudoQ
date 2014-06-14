/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivity;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.profile.Profile;

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 */
public class AssistancesPreferencesActivity extends SudoqActivitySherlock implements ModelChangeListener<Profile> {
	/** Attributes */
	private static final String LOG_TAG = AssistancesPreferencesActivity.class.getSimpleName();

	CheckBox gesture;
	CheckBox autoAdjustNotes;
	CheckBox markRowColumn;
	CheckBox markWrongSymbol;
	CheckBox restrictCandidates;

	/**
	 * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.playerpreferences);

		gesture = (CheckBox) findViewById(R.id.checkbox_gesture);
		autoAdjustNotes = (CheckBox) findViewById(R.id.checkbox_autoAdjustNotes);
		markRowColumn = (CheckBox) findViewById(R.id.checkbox_markRowColumn);
		markWrongSymbol = (CheckBox) findViewById(R.id.checkbox_markWrongSymbol);
		restrictCandidates = (CheckBox) findViewById(R.id.checkbox_restrictCandidates);

		LinearLayout layout = (LinearLayout) findViewById(R.id.playerpreferences_layout_everything);

		/* Aufruf aus SudokuPreferenceActivity */
		Log.d(LOG_TAG, "Short assistances");
			
		layout = (LinearLayout) findViewById(R.id.playerpreferences_layout_everything);
		layout.removeView(findViewById(R.id.button_showStatistics));
		layout.removeView(findViewById(R.id.playerpreferences_layout_profilename));
	
		Profile.getInstance().registerListener(this);
	}

	
	/**
	 * Wird aufgerufen, falls die Activity erneut den Eingabefokus erhält. Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.
	 */
	@Override
	public void onResume() {
		super.onResume();
		refreshValues();
	}

	/**
	 * Aktualisiert die Werte in den Views
	 */
	private void refreshValues() {
		gesture.setChecked(Profile.getInstance().isGestureActive());
		autoAdjustNotes.setChecked(SudokuPreferencesActivity.assistances.getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.setChecked(SudokuPreferencesActivity.assistances.getAssistance(Assistances.markRowColumn));
		markWrongSymbol.setChecked(SudokuPreferencesActivity.assistances.getAssistance(Assistances.markWrongSymbol));
		restrictCandidates.setChecked(SudokuPreferencesActivity.assistances.getAssistance(Assistances.restrictCandidates));
	}


	/**
	 * Speichert die Profiländerungen.
	 * 
	 * @param view
	 *            unbenutzt
	 */
	public void saveChanges(View view) {
		onBackPressed();
	}

	/**
	 * Wird aufgerufen, falls eine andere Activity den Eingabefokus erhält.
	 * Speichert die Einstellungen.
	 */
	@Override
	public void onPause() {
		super.onPause();
		adjustValuesAndSave();
	}

	/**
	 * Uebernimmt die Werte der Views im Profil und speichert die aenderungen
	 */
	private void adjustValuesAndSave() {
		Profile.getInstance().setGestureActive(gesture.isChecked());
		saveCheckbox(autoAdjustNotes, Assistances.autoAdjustNotes);
		saveCheckbox(markRowColumn, Assistances.markRowColumn);
		saveCheckbox(markWrongSymbol, Assistances.markWrongSymbol);
		saveCheckbox(restrictCandidates, Assistances.restrictCandidates);
		
		Profile.getInstance().saveChanges();
	}
	
	private void saveCheckbox(CheckBox cb, Assistances a){
		if(cb.isChecked())
			SudokuPreferencesActivity.assistances.setAssistance(a);
		else
			SudokuPreferencesActivity.assistances.clearAssistance(a);
	}

	public void onModelChanged(Profile obj) {
		this.refreshValues();
	}

	/**
	 * Öffnet den GestureBuilder.
	 * 
	 * @param view
	 *            unbenutzt
	 */
	public void openGestureBuilder(View view) {
		Intent gestureBuilderIntent = new Intent(this, GestureBuilder.class);
		startActivity(gestureBuilderIntent);
	}

}