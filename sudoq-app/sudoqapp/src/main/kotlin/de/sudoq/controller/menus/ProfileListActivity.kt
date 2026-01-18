/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import dagger.hilt.android.AndroidEntryPoint
import de.sudoq.R
import de.sudoq.controller.SudoqListActivity
import de.sudoq.model.profile.ProfileManager
import java.util.*
import javax.inject.Inject

/**
 * Diese Klasse stellt eine Acitivity zur Anzeige und Auswahl von
 * Spielerprofilen dar.
 */
@AndroidEntryPoint
class ProfileListActivity : SudoqListActivity(), OnItemClickListener {

    @Inject
    lateinit var profileManager: ProfileManager

    /** Ein Array der Profil-Dateinamen der Form "Profile_ID", wobei ID der
     *  jeweiligen ID des Profils entspricht.
     */
    private lateinit var profileIds: List<Int>

    /** Ein Array der Profilnamen */
    private lateinit var profileNames: List<String>

    /**
     * Wird beim ersten Start der Activity aufgerufen.
     *
     * @param savedInstanceState
     * Der Zustand eines vorherigen Aufrufs dieser Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profilelist)
        this.title = this.getString(R.string.action_switch_profile)

        profileIds = profileManager.profilesIdList
        profileNames = profileManager.profilesNameList
        val adapter = ArrayAdapter(this, R.layout.profilelist_item, profileManager.profilesNameList)
        listAdapter = adapter
        listView!!.onItemClickListener = this
    }

    /**
     * Wird aufgerufen, falls der Benutzer einen Eintrag in der ListView anklickt.
     *
     * @param parent
     * AdapterView auf welcher der Benutzer etwas angeklickt hat
     * @param view
     * Vom Benutzer angeklickte View
     * @param position
     * Position der View im Adapter
     * @param id
     * ID der ausgewählten View
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        val profileName = profileNames[position]
        val profileId = profileIds[position]
        Log.d(LOG_TAG, "Clicked on name $profileName with id:$profileId")
        profileManager.changeProfile(profileId)
        finish()
    }

    companion object {
        /** Attributes  */
        private val LOG_TAG = ProfileListActivity::class.java.simpleName
    }
}
