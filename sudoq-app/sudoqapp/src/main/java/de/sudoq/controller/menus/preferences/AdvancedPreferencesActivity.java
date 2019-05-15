/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import de.sudoq.R;
import de.sudoq.controller.menus.NewSudokuActivity;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.profile.Profile;

import static de.sudoq.controller.menus.preferences.AdvancedPreferencesActivity.ParentActivity.NOT_SPECIFIED;

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 * 
 */
public class AdvancedPreferencesActivity extends PreferencesActivity {
	/** Attributes */
	private static final String LOG_TAG = AdvancedPreferencesActivity.class.getSimpleName();

	public enum ParentActivity{PROFILE,NEW_SUDOKU,NOT_SPECIFIED};

    /*this is still a hack! this activity can be called in newSudoku-pref and in player(profile)Pref, but has different behaviours*/
    public static ParentActivity caller= NOT_SPECIFIED;

	public static GameSettings    gameSettings;

	CheckBox crasher;
	CheckBox helper;
	CheckBox lefthand;
	Button   restricttypes;

	/**
	 * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. Läd
	 * die Preferences anhand der zur Zeit aktiven Profil-ID.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		this.setContentView(R.layout.preferences_advanced);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.launcher);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowTitleEnabled(true);


		crasher       = (CheckBox) findViewById(R.id.checkbox_crash_trigger);
		helper        = (CheckBox) findViewById(R.id.checkbox_hints_provider);
		lefthand      = (CheckBox) findViewById(R.id.checkbox_lefthand_mode);
		restricttypes = (Button)   findViewById(R.id.button_provide_restricted_set_of_types);
		gameSettings = NewSudokuActivity.gameSettings;
		GameSettings profileGameSettings = Profile.getInstance().getAssistances();

        switch (caller){
            case NEW_SUDOKU:
				crasher. setChecked(gameSettings.isCrashSet());
				helper.  setChecked(gameSettings.isHelperSet());
		        lefthand.setChecked(gameSettings.isLefthandModeSet());
                break;
            case PROFILE:
            case NOT_SPECIFIED://not specified souldn't happen, but you never know
				crasher. setChecked(profileGameSettings.isCrashSet());
                helper.  setChecked(profileGameSettings.isHelperSet());
                lefthand.setChecked(profileGameSettings.isLefthandModeSet());
        }
		//myCaller.restricttypes.setChecked(a.isreHelperSet());
		
		Profile.getInstance().registerListener(this);
	}


	/**
	 * Aktualisiert die Werte in den Views
	 * 
	 */
	@Override
	protected void refreshValues() {
		//myCaller.lefthand.setChecked(gameSettings.isLefthandModeSet());
		//myCaller.helper.setChecked(  gameSettings.isHelperSet());
	}

	/**
	 * Selected by click on button (specified in layout file)
	 * Starts new activity that lets user choose which types are offered in 'new sudoku' menu
	 *
	 * @param view
	 *            von android xml übergebene View
	 */
	public void selectTypesToRestrict(View view) {
		startActivity(new Intent(this, RestrictTypesActivity.class));
	}

	public void helperSelected(final View view){
		final CheckBox cb = (CheckBox)view;

		if(cb.isChecked()){//if it is now, after click selected
			askConfirmation(cb);
		}
	}

	private void askConfirmation(final CheckBox cb) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// pass
			}
		});

		builder.setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				cb.setChecked(false);
			}
		});

		builder.setMessage("This feature is still in development. Are you sure you want to activate it?");
		AlertDialog alertDialog = builder.create();

		alertDialog.show();
	}

	@Override
	protected void adjustValuesAndSave() {
        switch(caller){
            case NEW_SUDOKU://TODO just have 2 subclasses, one to be called from playerpref, one from newSudokuPref
	            saveToGameSettings();
                break;
            case PROFILE:
                saveToProfile();
                break;
        }
	}

	private void saveToGameSettings(){
        if(lefthand != null && helper != null && crasher != null){//todo warum und???
            gameSettings.setLefthandMode(lefthand.isChecked());
            gameSettings.setHelper(helper.isChecked());
            gameSettings.setCrash(crasher.isChecked());
        }
    }

    protected void saveToProfile() {
        Profile p = Profile.getInstance();
		if(crasher != null)
			p.setCrasherActive(crasher.isChecked());
		if(helper != null)
			p.setHelperActive(helper.isChecked());
        if(lefthand != null)
            p.setLefthandActive(lefthand.isChecked());
        //restrict types is automatically saved to profile...
        Profile.getInstance().saveChanges();
    }


    // ///////////////////////////////////////optionsMenue

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_standard, menu);    
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);		
		return true;
	}


}