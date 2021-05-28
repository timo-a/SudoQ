/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.model.files.FileManager
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.complexity.Complexity.Companion.playableValues
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import org.apache.commons.lang3.StringUtils
import java.io.*
import java.util.*
import java.util.regex.Pattern

/**
 * Eine Splash Activity für die SudoQ-App, welche einen Splash-Screen zeigt,
 * sowie den FileManager initialisiert und die Daten für den ersten Start
 * vorbereitet.
 */
class SplashActivity : SudoqCompatActivity() {
    /**
     * Die Zeit, die schon vergangen ist, seit der SplashScreen gestartet wurde
     */
    private var waited = 0

    /**
     * Besagt, ob der Kopiervorgang der Templates bereits gestartet wurde
     */
    private var startedCopying = false

    /**
     * Der SplashThread
     */
    private var splashThread: Thread? = null

    /**
     * {@inheritDoc}
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash)

        // If there is no profile initialize one
        val pm = ProfileManager(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
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
        Log.d("ProfileD", "onCreate: after init: " + StringUtils.join(filenames, ", "))
        check(filenames.size >= 2) { "Too few files. initialization was not successfull" }

        // Restore waited time after interruption or set it to 0
        if (savedInstanceState == null) {
            waited = 0
        } else {
            waited = savedInstanceState.getInt(SAVE_WAITED.toString() + "")
            startedCopying = savedInstanceState.getBoolean(SAVE_STARTED_COPYING.toString() + "")
        }

        // Get the preferences and look if assets where completely copied before
        val settings = getSharedPreferences("Prefs", 0)

        /* get version value */try {
            currentVersionName = this.packageManager.getPackageInfo(this.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.v(LOG_TAG, e.message)
        }

        /* is this a new version? */
        val oldVersionName = settings.getString(VERSION_TAG, NO_VERSION_YET)
        if (updateSituation(oldVersionName) && !startedCopying) {

            /*hint*/
            alertIfNoAssetFolder()
            Log.v(LOG_TAG, "we will do an initialization")
            Initialization().execute(null, null, null)
            startedCopying = true
        } else Log.v(LOG_TAG, "we will not do an initialization")

        /* splash thread*/splashThread = object : Thread() {
            override fun run() {
                try {
                    while (waited < splashTime) {
                        sleep(100)
                        if (waited < splashTime) {
                            waited += 100
                        }
                    }
                    goToMainMenu()
                } catch (e: InterruptedException) {
                    // do nothing
                }
            }
        }
        splashThread.start()
    }

    /* Specifies whether this is a regular start or an assets-update,
	 * i.e. version has changed and assets have to be copied
	 *
	 * 'protected' for unit test
	 */
    protected fun updateSituation(oldVersionName: String?): Boolean {
        val updateSituation: Boolean
        updateSituation = try {
            older(oldVersionName, NEWEST_ASSET_VERSION)
        } catch (e: Exception) {
            true //when in doubt DO an update!
        }
        return updateSituation
    }

    /** is version a older than b?
     * a,b = "12.68.87(abc..)"   */
    @Throws(Exception::class)
    fun older(a: String?, b: String?): Boolean {
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
    private fun versionToNumbers(version: String?): IntArray {
        val pattern = "(\\d+)[.](\\d+)[.](\\d+)([a-z]?)"
        val r = Pattern.compile(pattern)
        val m = r.matcher(version)
        m.find()
        val result = IntArray(4)
        if (m.groupCount() == 4) {
            val letter = m.group(4)
            if (letter.length == 1) result[3] = letter[0] - 'a' + 1
        }
        for (i in intArrayOf(1, 2, 3)) result[i - 1] = m.group(i).toInt()
        return result
    }

    private fun alertIfNoAssetFolder() {
        try {
            val l = assets.list("")
            val foundSudokusInAssetfolder = Arrays.asList(*l).contains(HEAD_DIRECTORY)
            //TODO make this work:
            //boolean fsaf = Stream.of(l).anyMatch(s -> s.equals(HEAD_DIRECTORY));
            if (!foundSudokusInAssetfolder) {
                val msg = "This app will probably crash once you try to start a new sudoku. " +
                        "This is because the person who compiled this app forgot about the 'assets' folder. " +
                        "Please tell him that!"
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
            //Toast.makeText(this, l[0].equals(HEAD_DIRECTORY)+"", Toast.LENGTH_SHORT).show();
        } catch (e1: IOException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }
    }

    /**
     * Speichert die bereits im Splash gewartete Zeit.
     */
    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SAVE_WAITED.toString() + "", waited)
        outState.putBoolean(SAVE_STARTED_COPYING.toString() + "", startedCopying)
    }

    /**
     * Im Splash wird kein Optionsmenü angezeigt.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return true
    }

    /**
     * {@inheritdoc}
     */
    public override fun onPause() {
        super.onPause()
    }

    /**
     * {@inheritdoc}
     */
    override fun onBackPressed() {
        super.onBackPressed()
        splashThread!!.interrupt()
        finish()
    }

    /**
     * {@inheritDoc}
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Wechselt in die MainMenu-Activity
     */
    private fun goToMainMenu() {
        finish()
        // overridePendingTransition(android.R.anim.fade_in,
        // android.R.anim.fade_out);
        val startMainMenuIntent = Intent(this, MainActivity::class.java)
        // startMainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(startMainMenuIntent)
        // overridePendingTransition(android.R.anim.fade_in,
        // android.R.anim.fade_out);
    }

    /**
     * Ein AsyncTask zur Initialisierung des Benutzers und der Vorlagen für den
     * ersten Start.
     */
    private inner class Initialization : AsyncTask<Void?, Void?, Void?>() {
        public override fun onPostExecute(v: Void?) {
            val settings = getSharedPreferences("Prefs", 0)
            settings.edit().putBoolean(INITIALIZED_TAG, true).commit()
            settings.edit().putString(VERSION_TAG, currentVersionName).commit()
            Log.d(LOG_TAG, "Assets completely copied")
        }

        /**
         * Kopiert alle Sudoku Vorlagen.
         */
        private fun copyAssets() {
            /* sudoku types*/
            var types = SudokuTypes.values()
            /* ensure sudoku9x9 is first element ->  will be finished first.
			 * Reason: people will probably want to play 9x9 first
			 * kind of unnecessary because 9x9 is declared first in SudokuTypes, but maybe that will change*/types = swap99tothefront(types)

            /* actual copying*/for (t in types) {
                val sourceType = HEAD_DIRECTORY + File.separator + t.toString() + File.separator // e.g. .../standard9x9/
                val targetType = FileManager.getSudokuDir().absolutePath + File.separator + t.toString() + File.separator
                copyFile("$sourceType$t.xml",
                        "$targetType$t.xml")
                for (c in playableValues()) {
                    val sourceComplexity = sourceType + c.toString() + File.separator
                    val targetComplexity = targetType + c.toString() + File.separator
                    val fnames = getSubfiles(sourceType + c.toString())
                    for (filename in fnames!!) {
                        copyFile(sourceComplexity + filename,
                                targetComplexity + filename)
                    }
                }
            }
        }

        private fun swap99tothefront(types: Array<SudokuTypes>): Array<SudokuTypes> {
            if (types[0] !== SudokuTypes.standard9x9) {
                /* find index */
                var pos9x9: Int
                pos9x9 = 0
                while (pos9x9 < types.size) {
                    if (types[pos9x9] === SudokuTypes.standard9x9) break
                    pos9x9++
                }

                /* swap */types[pos9x9] = types[0]
                types[0] = SudokuTypes.standard9x9
            }
            return types
        }

        /** apache FileUtils.copyFile does not work!!!  */ /* get all files/directories in relPath */
        private fun getSubfiles(relPath: String): Array<String>? {
            var files: Array<String>? = null
            try {
                files = assets.list(relPath)
            } catch (e: IOException) {
                Log.e(LOG_TAG, e.message)
            }
            return files
        }

        /**TODO  do shorter with library
         * Copies content from sourcePath to destination
         *
         * @param sourcePath
         * @param destinationPath
         */
        private fun copyFile(sourcePath: String, destinationPath: String) {
            File(destinationPath).parentFile.mkdirs()
            val destination = File(destinationPath)
            val `in`: InputStream
            val out: OutputStream
            try {
                `in` = assets.open(sourcePath)
                val abs = destination.absolutePath
                out = FileOutputStream(abs)
                Utility.copyFileOnStreamLevel(`in`, out)
                `in`.close()
                out.flush()
                out.close()
            } catch (e: Exception) {
                Log.e(LOG_TAG, e.message)
                Log.e(LOG_TAG, "there seems to be an exception")
            }
        }

        protected override fun doInBackground(vararg params: Void): Void? {
            Log.d(LOG_TAG, "Starting to copy templates")
            copyAssets()
            return null
        }
    }

    companion object {
        /**
         * Das Log-Tag für das LogCat.
         */
        private val LOG_TAG = SplashActivity::class.java.simpleName

        /**
         * Konstante für das Speichern der bereits gewarteten Zeit.
         */
        private const val SAVE_WAITED = 0

        /**
         * Konstante für das Speichern des bereits begonnenen Kopiervorgangs.
         */
        private const val SAVE_STARTED_COPYING = 1

        /**
         * Die minimale Anzeigedauer des SplashScreens
         */
        var splashTime = 2500
        private const val HEAD_DIRECTORY = "sudokus"
        private const val INITIALIZED_TAG = "Initialized"
        private const val VERSION_TAG = "version"
        private const val NO_VERSION_YET = "0.0.0"
        protected const val NEWEST_ASSET_VERSION = "1.1.0b"
        private var currentVersionName = ""
    }
}