/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.menus.preferences.LanguageSetting
import de.sudoq.controller.menus.preferences.LanguageUtility.getConfLocale
import de.sudoq.controller.menus.preferences.LanguageUtility.loadLanguageFromLocale
import de.sudoq.controller.menus.preferences.LanguageUtility.loadLanguageFromSharedPreferences2
import de.sudoq.controller.menus.preferences.LanguageUtility.setConfLocale
import de.sudoq.controller.menus.preferences.LanguageUtility.storeLanguageToMemory2
import de.sudoq.controller.menus.preferences.PlayerPreferencesActivity
import de.sudoq.controller.sudoku.SudokuActivity
import de.sudoq.model.game.GameManager
import de.sudoq.model.profile.Profile
import de.sudoq.model.profile.ProfileManager
import java.io.File
import java.util.*

/**
 * Verwaltet das Hauptmenü der App.
 */
class MainActivity : SudoqCompatActivity() {
    /**
     * stores language at activity start to compare if language changed in advanced preferences
     */
    private var currentLanguageCode: LanguageSetting? = null

    private lateinit var profilesFile: File
    private lateinit var sudokuFile: File

    /**
     * Wird beim ersten Anzeigen des Hauptmenüs aufgerufen. Inflated das Layout.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profilesFile = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        sudokuFile = getDir(getString(de.sudoq.R.string.path_rel_sudokus), MODE_PRIVATE)
        //Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();
        setContentView(R.layout.mainmenu)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(false)

        /* load language from profile */

        //retrieve language from profile at beginning of activity lifecycle
        currentLanguageCode = loadLanguageFromSharedPreferences2(this)
        val a = Locale.getDefault().language
        val b = resources.configuration.locale.language
        val c = currentLanguageCode!!.toStorableString()
        if (currentLanguageCode!!.isSystemLanguage) {
            //nothing to do
            //locale is adopted automatically from device
        } else {
            val currentConf = getConfLocale(this)
            if (currentConf != currentLanguageCode!!.language) {
                //if conf != loaded language, set conf and update
                setConfLocale(currentLanguageCode!!.language.name, this)
                val refresh = Intent(this, this.javaClass)
                finish()
                this.startActivity(refresh)
            }
        }
        Log.d("lang", "MainActivity.onCreate() set with $currentLanguageCode")
    }

    /**
     * Wird aufgerufen, falls die Acitivity wieder den Eingabefokus erhält.
     */
    public override fun onResume() {
        super.onResume()
        //Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
        val pm = ProfileManager(profilesFile)
        check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        pm.loadCurrentProfile()
        val continueButton = findViewById<View>(R.id.button_mainmenu_continue) as Button
        continueButton.isEnabled = pm.currentGame > Profile.NO_GAME
        val gm = GameManager.getInstance(profilesFile, sudokuFile)
        val loadButton = findViewById<View>(R.id.button_mainmenu_load_sudoku) as Button
        //loadButton.setEnabled(!gm.getGameList().isEmpty());
        loadButton.isEnabled = true

        //load language from memory
        val fromConf = getConfLocale(this)
        if (fromConf != currentLanguageCode!!.language) {
            Log.d("lang", "refresh because $currentLanguageCode -> $fromConf")
            val refresh = Intent(this, this.javaClass)
            finish()
            this.startActivity(refresh)
        }
    }

    /**
     * Wechselt zu einer Activity, entsprechend der Auswahl eines Menübuttons.
     * Ist der übergebene Button null oder unbekannt, so wird nichts getan.
     *
     * @param button
     * Vom Benutzer ausgewählter Menübutton
     */
    fun switchActivity(button: View) {
        when (button.id) {
            R.id.button_mainmenu_new_sudoku -> {
                val newSudokuIntent = Intent(this, NewSudokuActivity::class.java)
                startActivity(newSudokuIntent)
            }
            R.id.button_mainmenu_continue -> {
                val continueSudokuIntent = Intent(this, SudokuActivity::class.java)
                startActivity(continueSudokuIntent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            R.id.button_mainmenu_load_sudoku -> {
                val loadSudokuIntent = Intent(this, SudokuLoadingActivity::class.java)
                startActivity(loadSudokuIntent)
            }
            R.id.button_mainmenu_profile -> {
                val preferencesIntent = Intent(this, PlayerPreferencesActivity::class.java)
                startActivity(preferencesIntent)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // check if configuration has changed
        // per Manifest this method gets called if there are changes in layoutDirection or locale
        // (if we only check for locale, this method doesn't get called, no idea why https://stackoverflow.com/a/27648673/3014199)
        //
        if (newConfig.locale.language != currentLanguageCode!!.language.name) {
            //only adopt external change if language is set to "system language"
            if (currentLanguageCode!!.isSystemLanguage) {
                //adopt change
                currentLanguageCode!!.language = loadLanguageFromLocale()
                //store changes
                storeLanguageToMemory2(this, currentLanguageCode!!)
            }
        }
    }

    companion object {
        /**
         * Der Log-Tag für den LogCat
         */
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}