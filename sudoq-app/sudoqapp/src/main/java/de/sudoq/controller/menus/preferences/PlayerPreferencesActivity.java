/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences;

import java.util.List;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.CheckBox;
import android.widget.EditText;
import de.sudoq.R;
import de.sudoq.controller.menus.ProfileListActivity;
import de.sudoq.controller.menus.StatisticsActivity;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.profile.Profile;

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 * aufgerufen im Hauptmenü 4. Button
 */
public class PlayerPreferencesActivity extends PreferencesActivity {
	/** Attributes */
	private static final String LOG_TAG = PlayerPreferencesActivity.class.getSimpleName();

	/**
	 * Konstante um anzuzeigen, dass nur die Assistences konfiguriert werden
	 * sollen
	 */
	public static final String INTENT_ONLYASSISTANCES = "only_assistances";
	/**
	 * Konstante um anzuzeigen, dass nur ein neues Profil erzeugt werden soll
	 */
	public static final String INTENT_CREATEPROFILE = "create_profile";

	private static boolean createProfile;

	EditText name;

	boolean firstStartup;
	
	GameSettings gameSettings;

	/**
	 * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		this.setContentView(R.layout.preferences_player);

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
		
		name = (EditText) findViewById(R.id.edittext_profilename);
		name.clearFocus();
		name.setSingleLine(true);// no multiline names

		firstStartup = false;		
		createProfile = true;

		Profile.getInstance().registerListener(this);
	}


	/**
	 * Aktualisiert die Werte in den Views
	 * 
	 */
	@Override
	protected void refreshValues() {
		name.              setText(Profile.getInstance().getName());
		gesture.           setChecked(Profile.getInstance().isGestureActive());
		autoAdjustNotes.   setChecked(Profile.getInstance().getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.     setChecked(Profile.getInstance().getAssistance(Assistances.markRowColumn));
		markWrongSymbol.   setChecked(Profile.getInstance().getAssistance(Assistances.markWrongSymbol));
		restrictCandidates.setChecked(Profile.getInstance().getAssistance(Assistances.restrictCandidates));
	}

	/**
	 * Wird beim Buttonklick aufgerufen und erstellt ein neues Profil
	 * 
	 * @param view
	 *            von android xml übergebene View
	 */
	public void createProfile(View view) {
		if (!firstStartup) {
			adjustValuesAndSave();

			String newProfileName = getString(R.string.profile_preference_new_profile);

			int newIndex = 0;
			/* increment newIndex to be bigger than the others */
			List<String> l = Profile.getInstance().getProfilesNameList();
			for (String s : l)
				if (s.startsWith(newProfileName)) {
					String currentIndex = s.substring(newProfileName.length());
					try {
						int otherIndex = currentIndex.equals("") ? 0 : Integer.parseInt(currentIndex);
						newIndex = newIndex <= otherIndex ? otherIndex + 1 : newIndex;
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

			if (newIndex != 0)
				newProfileName += newIndex;

			Profile.getInstance().createProfile();
			name.setText(newProfileName);
		} else {
			adjustValuesAndSave();
			this.finish();
		}
	}

	/**
	 * Zeigt die Statistik des aktuellen Profils.
	 * 
	 * @param view
	 *            unbenutzt
	 */
	public void viewStatistics(View view) {
		Intent statisticsIntent = new Intent(this, StatisticsActivity.class);
		startActivity(statisticsIntent);
	}

	/**
	 * Uebernimmt die Werte der Views im Profil und speichert die aenderungen
	 */
	protected void adjustValuesAndSave() {
		Profile.getInstance().setName(name.getText().toString());
		saveToProfile();
	}

	/* parameter View only needed to be found by xml who clicks this */
	public void switchToAdvancedPreferences(View view){
		
		Intent advIntent = new Intent(this, AdvancedPreferencesActivity.class);
		AdvancedPreferencesActivity.myCaller=this;
		//AdvancedPreferencesActivity.gameSettings = this.gameSettings;
		startActivity(advIntent);

	}
	
	/**
	 * wechselt zur Profil Liste
	 * 
	 * @param view
	 *            von der android xml übergebene view
	 */
	public void switchToProfileList(View view) {
		Intent profileListIntent = new Intent(this, ProfileListActivity.class);
		startActivity(profileListIntent);
	}

	/**
	 * Löscht das ausgewählte Profil
	 * 
	 * @param view
	 *            von der android xml übergebene view
	 */
	public void deleteProfile(View view) {
		Profile.getInstance().deleteProfile();
	}


	// ///////////////////////////////////////optionsMenue

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_player_preferences, menu);    
		return true;
	}

	/**
	 * Stellt das OptionsMenu bereit
	 * 
	 * @param item
	 *            Das ausgewählte Menü-Item
	 * @return true
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new_profile:
			createProfile(null);
			return true;
		case R.id.action_delete_profile:
			deleteProfile(null);
			return true;
		case R.id.action_switch_profile:
			switchToProfileList(null);
			return true;	
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		boolean multipleProfiles=Profile.getInstance().getNumberOfAvailableProfiles() > 1;
		
		menu.findItem(R.id.action_delete_profile).setVisible(multipleProfiles);
		menu.findItem(R.id.action_switch_profile).setVisible(multipleProfiles);
		
		return true;
	}
}