/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import de.sudoq.R
import de.sudoq.controller.menus.ProfileListActivity
import de.sudoq.controller.menus.StatisticsActivity
import de.sudoq.controller.menus.preferences.AdvancedPreferencesActivity.ParentActivity
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.ProfileSingleton.Companion.getInstance
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 * aufgerufen im Hauptmenü 4. Button
 */
class PlayerPreferencesActivity : PreferencesActivity() {
    var name: EditText? = null
    private var firstStartup = false
    var gameSettings: GameSettings? = null

    /**
     * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. Läd
     * die Preferences anhand der zur Zeit aktiven Profil-ID.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.preferences_player)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
        //set title explicitly so localization kicks in when language is changed
        ab.setTitle(R.string.profile_preference_title)
        gesture = findViewById<View>(R.id.checkbox_gesture) as CheckBox
        autoAdjustNotes = findViewById<View>(R.id.checkbox_autoAdjustNotes) as CheckBox
        markRowColumn = findViewById<View>(R.id.checkbox_markRowColumn) as CheckBox
        markWrongSymbol = findViewById<View>(R.id.checkbox_markWrongSymbol) as CheckBox
        restrictCandidates = findViewById<View>(R.id.checkbox_restrictCandidates) as CheckBox
        name = findViewById<View>(R.id.edittext_profilename) as EditText
        name!!.clearFocus()
        name!!.isSingleLine = true // no multiline names
        firstStartup = false
        createProfile = true
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val p = getInstance(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        p.registerListener(this)
    }

    /**
     * Aktualisiert die Werte in den Views
     */
    override fun refreshValues() {
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val profile = getInstance(profilesDir, ProfileRepo(profilesDir),
                                  ProfilesListRepo(profilesDir))
        name!!.setText(profile.name)
        gesture!!.isChecked = profile.isGestureActive
        autoAdjustNotes!!.isChecked = profile.getAssistance(Assistances.autoAdjustNotes)
        markRowColumn!!.isChecked = profile.getAssistance(Assistances.markRowColumn)
        markWrongSymbol!!.isChecked = profile.getAssistance(Assistances.markWrongSymbol)
        restrictCandidates!!.isChecked = profile.getAssistance(Assistances.restrictCandidates)
    }

    /**
     * Wird beim Buttonklick aufgerufen und erstellt ein neues Profil
     *
     * @param view
     * von android xml übergebene View
     */
    private fun createProfile() {
        if (firstStartup) {
            adjustValuesAndSave()
            finish()
        } else {
            adjustValuesAndSave()
            var newProfileName = getString(R.string.profile_preference_new_profile)
            var newIndex = 0
            /* increment newIndex to be bigger than the others */
            val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
            val p = getInstance(profilesDir, ProfileRepo(profilesDir),
                                ProfilesListRepo(profilesDir))
            val l: List<String> = p.profilesNameList
            for (s in l) if (s.startsWith(newProfileName)) {
                val currentIndex = s.substring(newProfileName.length)
                try {
                    val otherIndex = if (currentIndex == "") 0 else currentIndex.toInt()
                    newIndex = if (newIndex <= otherIndex) otherIndex + 1 else newIndex
                } catch (e: Exception) {
                    // TODO: handle exception
                }
            }
            if (newIndex != 0) newProfileName += newIndex
            p.createAnotherProfile(newProfileName)
            name!!.setText(newProfileName)
        }
    }

    /**
     * Zeigt die Statistik des aktuellen Profils.
     *
     * @param view
     * unbenutzt
     */
    fun viewStatistics(view: View?) {
        val statisticsIntent = Intent(this, StatisticsActivity::class.java)
        startActivity(statisticsIntent)
    }

    /**
     * Uebernimmt die Werte der Views im Profil und speichert die aenderungen
     */
    override fun adjustValuesAndSave() {
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val p = getInstance(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        p.name = name!!.text.toString()
        saveToProfile()
    }

    override fun saveToProfile() {
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val p = getInstance(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        p.isGestureActive = gesture!!.isChecked
        saveAssistance(Assistances.autoAdjustNotes, autoAdjustNotes!!)
        saveAssistance(Assistances.markRowColumn, markRowColumn!!)
        saveAssistance(Assistances.markWrongSymbol, markWrongSymbol!!)
        saveAssistance(Assistances.restrictCandidates, restrictCandidates!!)
        p.saveChanges()
    }

    /* parameter View only needed to be found by xml who clicks this */
    fun switchToAdvancedPreferences(view: View?) {
        val advIntent = Intent(this, AdvancedPreferencesActivity::class.java)
        AdvancedPreferencesActivity.caller = ParentActivity.PROFILE
        //AdvancedPreferencesActivity.gameSettings = this.gameSettings;
        startActivity(advIntent)
    }

    /**
     * wechselt zur Profil Liste
     *
     * @param view
     * von der android xml übergebene view
     */
    private fun switchToProfileList() {
        val profileListIntent = Intent(this, ProfileListActivity::class.java)
        startActivity(profileListIntent)
    }

    /**
     * Löscht das ausgewählte Profil
     *
     * @param view
     * von der android xml übergebene view
     */
    private fun deleteProfile() {
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val p = getInstance(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        p.deleteProfile()
    }

    // ///////////////////////////////////////optionsMenue
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_player_preferences, menu)
        return true
    }

    /**
     * Stellt das OptionsMenu bereit
     *
     * @param item
     * Das ausgewählte Menü-Item
     * @return true
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_new_profile -> {
                createProfile()
                true
            }
            R.id.action_delete_profile -> {
                deleteProfile()
                true
            }
            R.id.action_switch_profile -> {
                switchToProfileList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val p = getInstance(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        val multipleProfiles = p.numberOfAvailableProfiles > 1
        menu.findItem(R.id.action_delete_profile).isVisible = multipleProfiles
        menu.findItem(R.id.action_switch_profile).isVisible = multipleProfiles
        return true
    }

    companion object {

        /**
         * Konstante um anzuzeigen, dass nur die Assistences konfiguriert werden
         * sollen
         */
        const val INTENT_ONLYASSISTANCES = "only_assistances"

        /**
         * Konstante um anzuzeigen, dass nur ein neues Profil erzeugt werden soll
         */
        const val INTENT_CREATEPROFILE = "create_profile"
        private var createProfile = false
    }
}