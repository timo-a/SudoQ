/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.content.Intent
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.multidex.BuildConfig
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.menus.preferences.LanguageCode
import de.sudoq.controller.menus.preferences.LanguageUtility
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.complexity.Complexity.Companion.playableValues
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo
import java.io.*
import java.util.regex.Pattern
import androidx.core.content.edit

/**
 * Eine Splash Activity für die SudoQ-App, welche einen Splash-Screen zeigt,
 * sowie den FileManager initialisiert und die Daten für den ersten Start
 * vorbereitet.
 */
class SplashActivity : SudoqCompatActivity() {
    /**
     * Besagt, ob die Initialisierung abgeschlossen ist.
     */
    private var isReady = false

    /**
     * Besagt, ob der Kopiervorgang der Templates bereits gestartet wurde
     */
    private var startedCopying = false

    /**
     * {@inheritDoc}
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !isReady }

        // Load the language setting from preferences:
        val languageCode = LanguageUtility.loadLanguageCodeFromPreferences(this)
        Log.i("SudoQLanguage", "Using language setting: $languageCode")
        // If the desired language is not 'system' and not the current system language, update the resources (globally):
        if (languageCode != LanguageCode.system && languageCode != LanguageUtility.resolveSystemLanguage()) {
            LanguageUtility.setResourceLocale(this, languageCode)
        }

        // If there is no profile initialize one
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir), ProfilesListRepo(profilesDir))
        if (pm.noProfiles()) {
            pm.initialize()
            pm.name = getString(R.string.default_user_name)
            pm.saveChanges()
        } else {
            pm.loadCurrentProfile()
        }
        //confirm that there is a profile
        val profileDir = pm.profilesDir
        val filenames = profileDir!!.list()
        Log.d("ProfileD", "onCreate: after init: ${filenames?.joinToString(", ")}")
        check(filenames != null && filenames.size >= 2) { "Too few files. initialization was not successfull" }

        if (savedInstanceState != null) {
            startedCopying = savedInstanceState.getBoolean(SAVE_STARTED_COPYING.toString())
        }

        /* is this a new version? */
        val lastVersion = getLastVersion()
        if (updateSituation(lastVersion) && !startedCopying) {
            /*hint*/
            alertIfNoAssetFolder()
            Log.v(LOG_TAG, "we will do an initialization")
            val sudokus : File = getDir(getString(R.string.path_rel_sudokus), MODE_PRIVATE)
            Initialization(sudokus).execute()
            startedCopying = true
        } else {
            Log.v(LOG_TAG, "we will not do an initialization")
            // If no initialization is needed, we still want to wait for the minimum splash time
            Handler(Looper.getMainLooper()).postDelayed({
                isReady = true
                goToMainMenu()
            }, splashTime.toLong())
        }
    }

    private fun getLastVersion(): Int {
        val settings = getSharedPreferences("Prefs", 0)

        //as of version code:3.0.0, name:44, the current "version code" is stored in shared preferences.
        //up until then only the "version name" was stored. Version name are much harder to compare,
        // I don't know why they weren't used from the beginning.
        // If no version code is stored the version was at most 43, so that is the default value
        val lastVersion = settings.getInt(VERSION_CODE_TAG, 43)

        //1.1.0b = 25 needed an upgrade too, but we only stored the name then, so if the last
        // version we are upgrading from is < 1.1.0b (=25), we return 24
        // this way versions 25 to 43 don't get an asset update that they don't need and
        // versions 0-24 do get the update that they need
        if (lastVersion == 43 && NEWEST_ASSET_VERSION_CODE < 43) {
            val oldVersionName = settings.getString(VERSION_NAME_TAG, NO_VERSION_YET) ?: NO_VERSION_YET

            val olderThan110b =  try {
                older(oldVersionName, NEWEST_ASSET_VERSION_NAME)
            } catch (_: Exception) {
                true //when in doubt DO an update!
            }
            if (olderThan110b)
                return NEWEST_ASSET_VERSION_CODE - 1
        }
        return lastVersion
    }
    /* Specifies whether this is a regular start or an assets-update,
	 * i.e. version has changed and assets have to be copied
	 */
    private fun updateSituation(lastVersion: Int): Boolean {
        return lastVersion < NEWEST_ASSET_VERSION_CODE
    }

    /** is version a older than b?
     * a,b = "12.68.87(abc..)"   */
    @Throws(Exception::class)
    fun older(a: String, b: String): Boolean {
        val aTokenized = versionToNumbers(a)
        val bTokenized = versionToNumbers(b)
        assert(aTokenized.size == bTokenized.size)
        for (i in aTokenized.indices) {
            val aTok = aTokenized[i]
            val bTok = bTokenized[i]
            if (aTok < bTok) return true else if (aTok > bTok) return false
        }
        return false
    }

    @Throws(Exception::class)
    private fun versionToNumbers(version: String): IntArray {
        val pattern = "(\\d+)[.](\\d+)[.](\\d+)([a-z]?)"
        val r = Pattern.compile(pattern)
        val m = r.matcher(version)
        m.find()
        val result = IntArray(4)
        if (m.groupCount() == 4) {
            val letter = m.group(4)!!
            if (letter.length == 1) result[3] = letter[0] - 'a' + 1
        }
        for (i in intArrayOf(1, 2, 3)) result[i - 1] = m.group(i).toInt()
        return result
    }

    private fun alertIfNoAssetFolder() {
        try {
            val l = assets.list("")
            val foundSudokusInAssetfolder = l!!.contains(HEAD_DIRECTORY)
            if (!foundSudokusInAssetfolder) {
                val msg = "This app will probably crash once you try to start a new sudoku. " +
                        "This is because the person who compiled this app forgot about the 'assets' folder. " +
                        "Please tell him that!"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    /**
     * Speichert die bereits im Splash gewartete Zeit.
     */
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_STARTED_COPYING.toString(), startedCopying)
    }

    /**
     * Im Splash wird kein Optionsmenü angezeigt.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     * Wechselt in die MainMenu-Activity
     */
    private fun goToMainMenu() {
        if (!isFinishing) {
            val startMainMenuIntent = Intent(this, MainActivity::class.java)
            startActivity(startMainMenuIntent)
            finish()
        }
    }

    /**
     * Ein AsyncTask zur Initialisierung des Benutzers und der Vorlagen für den
     * ersten Start.
     */
    private inner class Initialization(val sudokuDir: File) : AsyncTask<Void?, Void?, Void?>() {

        override fun onPostExecute(v: Void?) {
            val settings = getSharedPreferences("Prefs", 0)
            settings.edit {
                putBoolean(INITIALIZED_TAG, true)
                //we still store the version name just in case
                putString(VERSION_NAME_TAG, CURRENT_VERSION_NAME)
                putInt(VERSION_CODE_TAG, CURRENT_VERSION_CODE)
            }
            Log.d(LOG_TAG, "Assets completely copied")
            
            Handler(Looper.getMainLooper()).postDelayed({
                isReady = true
                goToMainMenu()
            }, splashTime.toLong())
        }

        /**
         * Kopiert alle Sudoku Vorlagen.
         */
        private fun copyAssets() {
            var types = SudokuTypes.entries.toTypedArray()
            types = swap99tothefront(types)

            for (t in types) {
                val sourceType = HEAD_DIRECTORY + File.separator + t.toString() + File.separator
                val targetType = sudokuDir.absolutePath + File.separator + t.toString() + File.separator
                copyFile("$sourceType$t.xml", "$targetType$t.xml")
                for (c in playableValues()) {
                    val sourceComplexity = sourceType + c.toString() + File.separator
                    val targetComplexity = targetType + c.toString() + File.separator
                    val fnames = getSubfiles(sourceType + c.toString())
                    fnames?.forEach { filename ->
                        copyFile(sourceComplexity + filename, targetComplexity + filename)
                    }
                }
            }
        }

        private fun swap99tothefront(types: Array<SudokuTypes>): Array<SudokuTypes> {
            if (types[0] !== SudokuTypes.standard9x9) {
                var pos9x9 = 0
                while (pos9x9 < types.size) {
                    if (types[pos9x9] === SudokuTypes.standard9x9) break
                    pos9x9++
                }
                if (pos9x9 < types.size) {
                    types[pos9x9] = types[0]
                    types[0] = SudokuTypes.standard9x9
                }
            }
            return types
        }

        private fun getSubfiles(relPath: String): Array<String>? {
            return try {
                assets.list(relPath)
            } catch (e: IOException) {
                e.message?.let { Log.e(LOG_TAG, it) }
                null
            }
        }

        private fun copyFile(sourcePath: String, destinationPath: String) {
            File(destinationPath).parentFile?.mkdirs()
            val destination = File(destinationPath)
            try {
                assets.open(sourcePath).use { input ->
                    FileOutputStream(destination).use { output ->
                        Utility.copyFileOnStreamLevel(input, output)
                    }
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error copying file: $sourcePath", e)
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            Log.d(LOG_TAG, "Starting to copy templates")
            copyAssets()
            return null
        }
    }

    companion object {
        private val LOG_TAG = SplashActivity::class.java.simpleName
        private const val SAVE_STARTED_COPYING = 1
        @JvmField
        var splashTime = 2500
        private const val HEAD_DIRECTORY = "sudokus"
        private const val INITIALIZED_TAG = "Initialized"
        private const val VERSION_NAME_TAG = "version"
        private const val VERSION_CODE_TAG = "version_code"
        private const val NO_VERSION_YET = "0.0.0"
        private const val NEWEST_ASSET_VERSION_NAME = "1.1.0b"
        private const val NEWEST_ASSET_VERSION_CODE = 25
        private const val CURRENT_VERSION_NAME = BuildConfig.VERSION_NAME
        private const val CURRENT_VERSION_CODE = BuildConfig.VERSION_CODE
    }
}