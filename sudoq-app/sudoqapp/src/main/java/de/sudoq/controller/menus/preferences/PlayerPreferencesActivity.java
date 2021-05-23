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

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import de.sudoq.R;
import de.sudoq.controller.menus.ProfileListActivity;
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
	 * stores language at activity start to compare if language changed in advanced preferences
	 */
	private LanguageSetting currentLanguageCode;

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
		//set title explicitly so localization kicks in when language is changed
		ab.setTitle(R.string.profile_preference_title);

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

		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		p.registerListener(this);

		//store language at beginning of activity lifecycle
		currentLanguageCode = LanguageUtility.loadLanguageFromSharedPreferences2(this);

	}

	@Override
	public void onResume() {
		super.onResume();

		//load language from memory
		//LanguageSetting fromMemory = LanguageUtility.loadLanguageFromSharedPreferences2(this);
		LanguageSetting.LanguageCode fromConf = LanguageUtility.getConfLocale(this);

		if (!fromConf.equals(currentLanguageCode.language)){
			Intent refresh = new Intent(this, this.getClass());
			this.finish();
			this.startActivity(refresh);
		} else {
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

	/**
	 * Aktualisiert die Werte in den Views
	 * 
	 */
	@Override
	protected void refreshValues() {
		Profile profile = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));

		name.              setText(profile.getName());
		gesture.           setChecked(profile.isGestureActive());
		autoAdjustNotes.   setChecked(profile.getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.     setChecked(profile.getAssistance(Assistances.markRowColumn));
		markWrongSymbol.   setChecked(profile.getAssistance(Assistances.markWrongSymbol));
		restrictCandidates.setChecked(profile.getAssistance(Assistances.restrictCandidates));
	}

	/**
	 * Wird beim Buttonklick aufgerufen und erstellt ein neues Profil
	 * 
	 * @param view
	 *            von android xml übergebene View
	 */
	public void createProfile(View view) {
		if (firstStartup) {
			adjustValuesAndSave();
			this.finish();
		} else {
			adjustValuesAndSave();

			String newProfileName = getString(R.string.profile_preference_new_profile);

			int newIndex = 0;
			/* increment newIndex to be bigger than the others */
			Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
			List<String> l = p.getProfilesNameList();
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

			p.createAnotherProfile();
			name.setText(newProfileName);
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
		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		p.setName(name.getText().toString());
		saveToProfile();
	}

	protected void saveToProfile() {
		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		p.setGestureActive(gesture.isChecked());
		saveAssistance(Assistances.autoAdjustNotes,    autoAdjustNotes);
		saveAssistance(Assistances.markRowColumn,      markRowColumn  );
		saveAssistance(Assistances.markWrongSymbol,    markWrongSymbol);
		saveAssistance(Assistances.restrictCandidates, restrictCandidates);
		p.saveChanges();
	}

	/* parameter View only needed to be found by xml who clicks this */
	public void switchToAdvancedPreferences(View view){
		
		Intent advIntent = new Intent(this, AdvancedPreferencesActivity.class);
		AdvancedPreferencesActivity.caller= AdvancedPreferencesActivity.ParentActivity.PROFILE;
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
		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		p.deleteProfile();
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

		Profile p = Profile.Companion.getInstance(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		boolean multipleProfiles=p.getNumberOfAvailableProfiles() > 1;
		
		menu.findItem(R.id.action_delete_profile).setVisible(multipleProfiles);
		menu.findItem(R.id.action_switch_profile).setVisible(multipleProfiles);
		
		return true;
	}
}