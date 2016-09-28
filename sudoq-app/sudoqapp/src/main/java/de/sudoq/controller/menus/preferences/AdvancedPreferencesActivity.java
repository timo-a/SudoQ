/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import de.sudoq.R;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.profile.Profile;

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 * 
 */
public class AdvancedPreferencesActivity extends PreferencesActivity {
	/** Attributes */
	private static final String LOG_TAG = AdvancedPreferencesActivity.class.getSimpleName();
	
	/*this is a hack! activity can be called in sudoku-pref and in profile, but has different behaviours*/
	public static PreferencesActivity myCaller;
	//public static GameSettings    gameSettings;
	
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


		myCaller.helper        = (CheckBox) findViewById(R.id.checkbox_hints_provider);
		myCaller.lefthand      = (CheckBox) findViewById(R.id.checkbox_lefthand_mode);
		myCaller.restricttypes = (Button)   findViewById(R.id.button_provide_restricted_set_of_types);
		
		GameSettings a = Profile.getInstance().getAssistances();
		myCaller.helper.       setChecked(a.isHelperSet());
		myCaller.lefthand.     setChecked(a.isLefthandModeSet());
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
	 * Wird beim Buttonklick aufgerufen und erstellt ein neues Profil
	 * 
	 * @param view
	 *            von android xml übergebene View
	 */
	public void selectTypesToRestrict(View view) {
		startActivity(new Intent(this, RestrictTypesActivity.class));
	}

	public void helperSelected(final View view){
		AlertDialog deleteAlert = new AlertDialog.Builder(this).create();
		deleteAlert.setTitle("This feature is still in development. Are you sure you want to activate it?");

		deleteAlert.setButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				CheckBox cb = (CheckBox)view;
				cb.setChecked(true);
			}
		});
		deleteAlert.setButton2(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Dummy: clicking no means staying in the game
			}
		});
		deleteAlert.show();
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

	@Override
	protected void adjustValuesAndSave() {}
}