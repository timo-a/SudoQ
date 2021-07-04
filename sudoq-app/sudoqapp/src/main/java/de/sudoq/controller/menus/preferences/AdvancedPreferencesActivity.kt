/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.widget.Toolbar
import de.sudoq.R
import de.sudoq.controller.menus.NewSudokuActivity
import de.sudoq.controller.menus.preferences.AdvancedPreferencesActivity
import de.sudoq.controller.menus.preferences.LanguageSetting.LanguageCode
import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.ProfileManager

/**
 * Activity um Profile zu bearbeiten und zu verwalten
 *
 */
class AdvancedPreferencesActivity : PreferencesActivity() {
    enum class ParentActivity {
        PROFILE, NEW_SUDOKU, NOT_SPECIFIED
    }

    var lefthand: CheckBox? = null
    override var restricttypes: Button? = null
    var helper: CheckBox? = null
    var debug: CheckBox? = null
    var debugCounter: Byte = 0
    private val lastSelectedLanguageItem = 0
    private var langSpinnerInit = true

    /**
     * stores language at activity start to compare if language changed in advanced preferences
     */
    private var currentLanguageCode: LanguageSetting? = null

    /**
     * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. Läd
     * die Preferences anhand der zur Zeit aktiven Profil-ID.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("lang", "AdvancedPreferencesActivity.onCreate() called.")
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.preferences_advanced)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
        //set title explicitly so localization kicks in
        ab.setTitle(R.string.sf_advancedpreferences_title)
        lefthand = findViewById<View>(R.id.checkbox_lefthand_mode) as CheckBox
        restricttypes = findViewById<View>(R.id.button_provide_restricted_set_of_types) as Button
        helper = findViewById<View>(R.id.checkbox_hints_provider) as CheckBox
        debug = findViewById<View>(R.id.checkbox_debug) as CheckBox
        //exporter      = (CheckBox) findViewById(R.id.checkbox_exportcrash_trigger);
        gameSettings = NewSudokuActivity.gameSettings
        val pm = ProfileManager(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        pm.loadCurrentProfile()
        val profileGameSettings = pm.assistances
        when (caller) {
            ParentActivity.NEW_SUDOKU -> {
                debug!!.isChecked = pm.appSettings.isDebugSet
                if (debug!!.isChecked) {
                    debug!!.visibility = View.VISIBLE
                }
                helper!!.isChecked = gameSettings.isHelperSet
                lefthand!!.isChecked = gameSettings.isLefthandModeSet
            }
            ParentActivity.PROFILE, ParentActivity.NOT_SPECIFIED -> {
                if (debug!!.isChecked) {
                    debug!!.visibility = View.VISIBLE
                }
                debug!!.isChecked = pm.appSettings.isDebugSet
                helper!!.isChecked = profileGameSettings.isHelperSet
                lefthand!!.isChecked = profileGameSettings.isLefthandModeSet
            }
        }
        //myCaller.restricttypes.setChecked(a.isreHelperSet());

        // nothing happens onModelChangesd
        // Profile.Companion.getInstance().registerListener(this);
        /** language spinner  */
        val languageSpinner = findViewById<Spinner>(R.id.spinner_language)
        val languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_choice_values,
                android.R.layout.simple_spinner_item)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter
        val thishere: Activity = this

        //set language
        currentLanguageCode = LanguageUtility.loadLanguageFromSharedPreferences2(this)
        Log.d("lang", "set language to AdvancedPreferencesActivity.onCreate() after setLocaleFromMemory.")
        languageSpinner.setSelection(if (currentLanguageCode!!.isSystemLanguage) 0 else currentLanguageCode!!.language.ordinal)
        // nested Listener for languageSpinner
        languageSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                var pos = pos
                if (langSpinnerInit) {
                    //onitemselected is called after initialization
                    //this flag prevents it
                    langSpinnerInit = false
                    return
                }

                //if position is out of array bounds set to 0 (= system language).
                if (pos >= LanguageCode.values().size) {
                    pos = 0
                }

                //translate pos to enum
                val enumCode = LanguageCode.values()[pos]
                //enum to string(resolving system language) and set
                val newCode = LanguageUtility.getLanguageFromItem(enumCode)
                LanguageUtility.setConfLocale(newCode!!.language.name, thishere)
                LanguageUtility.storeLanguageToMemory2(this@AdvancedPreferencesActivity, newCode)
                //int previous = LanguageUtility.loadLanguageFromConf(AdvancedPreferencesActivity.this).name();
                if (currentLanguageCode!!.language != newCode.language) {
                    //if we change e.g. from system(english) to english we need to store a different value but we don't need to refresh.

                    //restart activity so changes can take placem
                    val refresh = Intent(thishere, thishere.javaClass)
                    thishere.finish()
                    thishere.startActivity(refresh)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    /**
     * Aktualisiert die Werte in den Views
     *
     */
    override fun refreshValues() {
        //myCaller.lefthand.setChecked(gameSettings.isLefthandModeSet());
        //myCaller.helper.setChecked(  gameSettings.isHelperSet());
    }

    /**
     * Selected by click on button (specified in layout file)
     * Starts new activity that lets user choose which types are offered in 'new sudoku' menu
     *
     * @param view
     * von android xml übergebene View
     */
    fun selectTypesToRestrict(view: View?) {
        Log.d("gameSettings", "AdvancedPreferencesActivity.selectTypesToRestrict")
        startActivity(Intent(this, RestrictTypesActivity::class.java))
    }

    fun helperSelected(view: View) {
        val cb = view as CheckBox
        if (cb.isChecked) { //if it is now, after click selected
            askConfirmation(cb)
        }
    }

    fun count(view: View?) {
        debugCounter++
        if (debugCounter >= 10) debug!!.visibility = View.VISIBLE
    }

    private fun askConfirmation(cb: CheckBox) {
        val builder = AlertDialog.Builder(this)
        builder.setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->
            // pass
        }
        builder.setNegativeButton(getString(R.string.dialog_no)) { dialog, which -> cb.isChecked = false }
        builder.setMessage("This feature is still in development. Are you sure you want to activate it?")
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun adjustValuesAndSave() {
        when (caller) {
            ParentActivity.NEW_SUDOKU -> {
                saveToGameSettings()
                if (debug != null) {
                    val pm = ProfileManager(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
                    check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
                    pm.loadCurrentProfile()
                    pm.setDebugActive(debug!!.isChecked)
                }
            }
            ParentActivity.PROFILE -> saveToProfile()
        }
    }

    private fun saveToGameSettings() {
        if (lefthand != null && helper != null) {
            gameSettings!!.setLefthandMode(lefthand!!.isChecked)
            gameSettings!!.setHelper(helper!!.isChecked)
        }
    }

    override fun saveToProfile() {
        val pm = ProfileManager(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        pm.loadCurrentProfile()
        if (debug != null) pm.setDebugActive(debug!!.isChecked)
        if (helper != null) pm.setHelperActive(helper!!.isChecked)
        if (lefthand != null) pm.setLefthandActive(lefthand!!.isChecked)
        //restrict types is automatically saved to profile...
        pm.saveChanges()
    }

    // ///////////////////////////////////////optionsMenue
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_standard, menu)
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
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        return true
    }

    companion object {
        /** Attributes  */
        private val LOG_TAG = AdvancedPreferencesActivity::class.java.simpleName

        /*this is still a hack! this activity can be called in newSudoku-pref and in player(profile)Pref, but has different behaviours*/
        var caller = ParentActivity.NOT_SPECIFIED
        var gameSettings: GameSettings? = null
    }
}