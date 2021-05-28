/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.Toolbar
import de.sudoq.R
import de.sudoq.controller.menus.NewSudokuActivity
import de.sudoq.controller.menus.preferences.AdvancedPreferencesActivity.ParentActivity
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.Profile.Companion.getInstance

/**
 * Wird aufgerufen in Hauptmenü-> neues Sudoku -> einstellungen
 */
class NewSudokuPreferencesActivity : PreferencesActivity() {
    /* shortcut for NewSudokuActivity.gameSettings */
    var confSettings: GameSettings? = null

    /**
     * stores language at activity start to compare if language changed in advanced preferences
     */
    private var currentLanguageCode: LanguageSetting? = null

    /**
     * Wird aufgerufen, falls die Activity zum ersten Mal gestartet wird. ?Läd
     * die Preferences anhand der zur Zeit aktiven Profil-ID.?
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.preferences_newsudoku)
        Log.i("gameSettings", "NewSudokuPreferencesActivity onCreate beginning")
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
        gesture = findViewById<View>(R.id.checkbox_gesture) as CheckBox
        autoAdjustNotes = findViewById<View>(R.id.checkbox_autoAdjustNotes) as CheckBox
        markRowColumn = findViewById<View>(R.id.checkbox_markRowColumn) as CheckBox
        markWrongSymbol = findViewById<View>(R.id.checkbox_markWrongSymbol) as CheckBox
        restrictCandidates = findViewById<View>(R.id.checkbox_restrictCandidates) as CheckBox
        Log.i("gameSettings", "NewSudokuPreferencesActivity onCreate end is gameSettings null?" + (NewSudokuActivity.gameSettings == null))
        Log.d("gameSettings", "NewSudokuPreferencesActivity onCreate end is gameSettings null?" + (NewSudokuActivity.gameSettings == null))
        confSettings = NewSudokuActivity.gameSettings
        gesture!!.isChecked = confSettings.isGesturesSet
        autoAdjustNotes!!.isChecked = confSettings.getAssistance(Assistances.autoAdjustNotes)
        markRowColumn!!.isChecked = confSettings.getAssistance(Assistances.markRowColumn)
        markWrongSymbol!!.isChecked = confSettings.getAssistance(Assistances.markWrongSymbol)
        restrictCandidates!!.isChecked = confSettings.getAssistance(Assistances.restrictCandidates)
        val p = getInstance(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        p.registerListener(this)

        //set and store language at beginning of activity lifecycle
        currentLanguageCode = LanguageUtility.loadLanguageFromSharedPreferences(this)
    }

    override fun onResume() {
        super.onResume()

        //load language from memory
        val fromMemory = LanguageUtility.loadLanguageFromSharedPreferences(this)
        if (fromMemory!!.language != currentLanguageCode!!.language) {
            val refresh = Intent(this, this.javaClass)
            finish()
            this.startActivity(refresh)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Aktualisiert die Werte in den Views
     */
    override fun refreshValues() {
        /*
		gesture.           setChecked(confSettings.isGesturesSet());
		autoAdjustNotes.   setChecked(confSettings.getAssistance(Assistances.autoAdjustNotes));
		markRowColumn.     setChecked(confSettings.getAssistance(Assistances.markRowColumn));
		markWrongSymbol.   setChecked(confSettings.getAssistance(Assistances.markWrongSymbol));
		restrictCandidates.setChecked(confSettings.getAssistance(Assistances.restrictCandidates));
		*/
    }

    /**
     * Saves currend state of buttons/checkboxes to gameSettings
     */
    override fun adjustValuesAndSave() {
        confSettings!!.setGestures(gesture!!.isChecked)
        saveCheckbox(autoAdjustNotes!!, Assistances.autoAdjustNotes, confSettings!!)
        saveCheckbox(markRowColumn!!, Assistances.markRowColumn, confSettings!!)
        saveCheckbox(markWrongSymbol!!, Assistances.markWrongSymbol, confSettings!!)
        saveCheckbox(restrictCandidates!!, Assistances.restrictCandidates, confSettings!!)
        //confSettings.setHelper();
        //confSettings.setCrash();
        //todo singleton not necessary
        val p = getInstance(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        p.saveChanges()
    }

    /**
     * Speichert die Profiländerungen
     * @param view
     * unbenutzt
     */
    fun saveChanges(view: View?) {
        saveToProfile()
        onBackPressed() //go back to parent activity
    }

    override fun saveToProfile() {
        val p = getInstance(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        p.isGestureActive = gesture!!.isChecked
        saveAssistance(Assistances.autoAdjustNotes, autoAdjustNotes!!)
        saveAssistance(Assistances.markRowColumn, markRowColumn!!)
        saveAssistance(Assistances.markWrongSymbol, markWrongSymbol!!)
        saveAssistance(Assistances.restrictCandidates, restrictCandidates!!)
        p.setHelperActive(confSettings!!.isHelperSet)
        p.setLefthandActive(confSettings!!.isLefthandModeSet)

        //restrict types is automatically saved to profile...
        p.saveChanges()
    }

    /* parameter View only needed to be foud by xml who clicks this*/
    fun switchToAdvancedPreferences(view: View?) {
        val advIntent = Intent(this, AdvancedPreferencesActivity::class.java)
        AdvancedPreferencesActivity.Companion.caller = ParentActivity.NEW_SUDOKU
        startActivity(advIntent)
    }
}