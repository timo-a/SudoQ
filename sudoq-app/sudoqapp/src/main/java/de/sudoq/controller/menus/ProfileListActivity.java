/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import de.sudoq.R;
import de.sudoq.controller.SudoqListActivity;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.ProfileManager;

/**
 * Diese Klasse stellt eine Acitivity zur Anzeige und Auswahl von
 * Spielerprofilen dar.
 */
public class ProfileListActivity extends SudoqListActivity implements OnItemClickListener {
	/** Attributes */

	private static final String LOG_TAG = ProfileListActivity.class.getSimpleName();

	/**
	 * Ein Array der Profil-Dateinamen der Form "Profile_ID", wobei ID der
	 * jeweiligen ID des Profils entspricht.
	 */
	private ArrayList<Integer> profileIds;

	/**
	 * Ein Array der Profilnamen
	 */
	private ArrayList<String> profileNames;

	/** Methods */

	/**
	 * Wird beim ersten Start der Activity aufgerufen.
	 * 
	 * @param savedInstanceState
	 *            Der Zustand eines vorherigen Aufrufs dieser Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profilelist);
		this.setTitle(this.getString(R.string.action_switch_profile));

		//todo make class variable
		ProfileManager pm = new ProfileManager(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		if (pm.noProfiles()) {
			throw new IllegalStateException("there are no profiles. this is  unexpected. they should be initialized in splashActivity");
		}
		pm.loadCurrentProfile();

		profileIds   = pm.getProfilesIdList();
		profileNames = pm.getProfilesNameList();

		Log.d(LOG_TAG, "Array length: " + pm.getProfilesNameList().size());

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				R.layout.profilelist_item, pm.getProfilesNameList());
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	/**
	 * Wird aufgerufen, falls der Benutzer einen Eintrag in der ListView
	 * anklickt.
	 * 
	 * @param parent
	 *            AdapterView auf welcher der Benutzer etwas angeklickt hat
	 * @param view
	 *            Vom Benutzer angeklickte View
	 * @param position
	 *            Position der View im Adapter
	 * @param id
	 *            ID der ausgewählten View
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(LOG_TAG, "Clicked on name " + profileNames.get(position) + " with id:" + profileIds.get(position));
		ProfileManager pm = new ProfileManager(getDir(getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		if (pm.noProfiles()) {
			throw new IllegalStateException("there are no profiles. this is  unexpected. they should be initialized in splashActivity");
		}
		pm.loadCurrentProfile();

		pm.changeProfile(profileIds.get(position));
		this.finish();
	}

}
