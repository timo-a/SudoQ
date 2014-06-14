/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/*import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;*/

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivity;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.profile.Profile;

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 * aufgerufen im Hauptmenü 4. Button
 */
public class PlayerPreferencesActivity extends SudoqActivitySherlock implements ModelChangeListener<Profile> {
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
	CheckBox gesture;
	CheckBox autoAdjustNotes;
	CheckBox markRowColumn;
	CheckBox markWrongSymbol;
	CheckBox restrictCandidates;

	boolean firstStartup;

	/**
	 * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setTitle(R.string.profile_preference_title);
		
		this.setContentView(R.layout.playerpreferences);

		firstStartup = false;

		name = (EditText) findViewById(R.id.edittext_profilename);
		name.clearFocus();
		name.setSingleLine(true);// no multiline names

		gesture = (CheckBox) findViewById(R.id.checkbox_gesture);
		autoAdjustNotes = (CheckBox) findViewById(R.id.checkbox_autoAdjustNotes);
		markRowColumn = (CheckBox) findViewById(R.id.checkbox_markRowColumn);
		markWrongSymbol = (CheckBox) findViewById(R.id.checkbox_markWrongSymbol);
		restrictCandidates = (CheckBox) findViewById(R.id.checkbox_restrictCandidates);

		LinearLayout layout = (LinearLayout) findViewById(R.id.playerpreferences_layout_everything);

		createProfile = true;

		/* Aufruf aus SudokuPreferenceActivity */
		/* not happening: assistences preference activity would be called*/
		if (getIntent().hasExtra(INTENT_ONLYASSISTANCES) && getIntent().getExtras().getBoolean(INTENT_ONLYASSISTANCES)) {
			Log.d(LOG_TAG, "Short assistances");

			createProfile = false;

			layout = (LinearLayout) findViewById(R.id.playerpreferences_layout_everything);
			layout.removeView(findViewById(R.id.button_showStatistics));
			layout.removeView(findViewById(R.id.playerpreferences_layout_profilename));
		} else {
			layout.removeView(findViewById(R.id.button_saveChanges));
		}

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
		name.setText(Profile.getInstance().getName());
		gesture.setChecked(Profile.getInstance().isGestureActive());
		autoAdjustNotes.setChecked(Profile.getInstance().getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.setChecked(Profile.getInstance().getAssistance(Assistances.markRowColumn));
		markWrongSymbol.setChecked(Profile.getInstance().getAssistance(Assistances.markWrongSymbol));
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
	 * Öffnet den GestureBuilder.
	 * 
	 * @param view
	 *            unbenutzt
	 */
	public void openGestureBuilder(View view) {
		Intent gestureBuilderIntent = new Intent(this, GestureBuilder.class);
		startActivity(gestureBuilderIntent);
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
	 * Wird aufgerufen, falls eine andere Activity den Eingabfokus erhält.
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
		Profile.getInstance().setName(name.getText().toString());
		Profile.getInstance().setGestureActive(gesture.isChecked());
		Profile.getInstance().setAssistance(Assistances.autoAdjustNotes, autoAdjustNotes.isChecked());
		Profile.getInstance().setAssistance(Assistances.markRowColumn, markRowColumn.isChecked());
		Profile.getInstance().setAssistance(Assistances.markWrongSymbol, markWrongSymbol.isChecked());
		Profile.getInstance().setAssistance(Assistances.restrictCandidates, restrictCandidates.isChecked());

		Profile.getInstance().saveChanges();
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


	public void onModelChanged(Profile obj) {
		this.refreshValues();
	}

	// ///////////////////////////////////////optionsMenue

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getSupportMenuInflater();
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