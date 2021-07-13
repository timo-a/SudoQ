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
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.menus.preferences.NewSudokuPreferencesActivity
import de.sudoq.controller.sudoku.SudokuActivity
import de.sudoq.model.game.GameManager
import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.SudokuTypesList
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo
import de.sudoq.persistence.sudokuType.SudokuTypeRepo
import java.util.*

/**
 * SudokuPreferences ermöglicht das Verwalten von Einstellungen eines zu
 * startenden Sudokus.
 *
 * Hauptmenü -> "neues Sudoku" führt hierher
 */
class NewSudokuActivity : SudoqCompatActivity() {

    private var sudokuType: SudokuTypes? = null
    private var complexity: Complexity? = null

    /**
     * Wird beim ersten Aufruf der SudokuPreferences aufgerufen. Die Methode
     * inflated das Layout der Preferences.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.sudokupreferences)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
        //set title explicitly so localization kicks in when language is changed
        ab.setTitle(R.string.sf_sudokupreferences_title)

        //for initial settings-values from Profile
        val profileDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val pm = ProfileManager(profileDir, ProfileRepo(profileDir), ProfilesListRepo(profileDir))
        check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        pm.loadCurrentProfile()
        val xt = pm.assistances.toXmlTree()
        gameSettings = GameSettings()
        gameSettings!!.fillFromXml(xt)
        /** complexity spinner  */
        val complexitySpinner = findViewById<View>(R.id.spinner_sudokucomplexity) as Spinner
        val complexityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.sudokucomplexity_values,
            android.R.layout.simple_spinner_item
        )
        complexityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        complexitySpinner.adapter = complexityAdapter

        // nested Listener for complexitySpinner
        complexitySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                setSudokuDifficulty(Complexity.values()[pos])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }
        Log.d(
            "gameSettings",
            "NewSudokuActivity onCreate end is gameSettings null?" + (gameSettings == null)
        )
    }

    /**
     * Wird aufgerufen, wenn die Activity in den Vordergrund gelangt. Die
     * Preferences werden hier neu geladen.
     */
    public override fun onResume() {
        super.onResume()
        /** type spinner  */
        val possibleTypes = gameSettings!!.wantedTypesList
        check(!possibleTypes.isEmpty()) {  //TODO shouldn't happen in the first place!
            "list shouldn't be empty"
        }
        initTypeSpinner(possibleTypes)


//		SudokuTypesList wtl = Profile.Companion.getInstance().getAssistances().getWantedTypesList();
//		fillTypeSpinner(wtl);
//		/* this is a hack: for some reason when returning from settings, the typeSpinner selects the first position
//		 *                 probably because it gets a new adapter. At the time I'm unable to debug this properly
//		 *                 (judging from the LOG.d's it happens after this method) but it seems to work */
//		if(wtl.contains(sudokuType))
//			((Spinner) findViewById(R.id.spinner_sudokutype)).setSelection(wtl.indexOf(sudokuType));
        Log.d(LOG_TAG, "Resume_ende: $sudokuType")

        //set language
        //LanguageUtility.setLocaleFromMemory(this);
    }

    private fun initTypeSpinner(stl: SudokuTypesList) {
        val typeSpinner = findViewById<Spinner>(R.id.spinner_sudokutype)
        //List<String> translatedSudokuTypes = Arrays.asList(getResources().getStringArray(R.array.sudokutype_values));
        val wantedSudokuTypes: MutableList<StringAndEnum<SudokuTypes>> =
            ArrayList() //user can choose to only have selected types offered, so here we filter
        check(stl.size != 0) { "list shouldn't be empty" }

        /* convert */
        for (st in stl) {
            val sae = StringAndEnum(Utility.type2string(this, st)!!, st)
            wantedSudokuTypes.add(sae)
        }
        wantedSudokuTypes.sortWith { o1, o2 ->
            SudokuTypeOrder.getKey(o1.enum) - SudokuTypeOrder.getKey(
                o2.enum
            )
        }
        Log.d(LOG_TAG, "Sudokutype_1: $sudokuType")
        val typeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, wantedSudokuTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter
        Log.d(LOG_TAG, "Sudokutype_4: $sudokuType")

        /* add onItemSelectListener */typeSpinner.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    pos: Int,
                    id: Long
                ) {
                    val item = parent.getItemAtPosition(pos) as StringAndEnum<SudokuTypes>
                    setSudokuType(item.enum)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // do nothing
                }
            }
    }

    /**
     * Die Methode startet per Intent ein Sudokus mit den eingegebenen
     * Einstellungen.
     *
     * @param view
     * von android xml übergebene View
     */
    fun startGame(view: View?) {
        if (sudokuType != null && complexity != null && gameSettings != null) {
            try {
                val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
                val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir),
                                        ProfilesListRepo(profilesDir))
                val sudokuDir = getDir(getString(R.string.path_rel_sudokus), MODE_PRIVATE)
                val game = GameManager(pm, SudokuTypeRepo(sudokuDir))
                    .newGame(sudokuType!!, complexity!!, gameSettings!!, sudokuDir)
                check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
                pm.loadCurrentProfile()
                pm.currentGame = game.id
                pm.saveChanges()
                startActivity(Intent(this, SudokuActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } catch (e: IllegalArgumentException) {
                Log.e(LOG_TAG, "exception: $e")
                Toast.makeText(
                    this,
                    getString(R.string.sf_sudokupreferences_copying),
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(LOG_TAG, "no template found- 'wait please'")
            }
        } else {
            Toast.makeText(
                this,
                getString(R.string.error_sudoku_preference_incomplete),
                Toast.LENGTH_SHORT
            ).show()
            if (sudokuType == null) Toast.makeText(this, "sudokuType", Toast.LENGTH_SHORT).show()
            if (complexity == null) Toast.makeText(this, "complexity", Toast.LENGTH_SHORT).show()
            if (gameSettings == null) Toast.makeText(this, "gameSetting", Toast.LENGTH_SHORT).show()
            Log.d(LOG_TAG, "else- 'wait please'")
        }
    }

    /**
     * Setzt den Sudokutyp des zu startenden Sudokus. Ist dieser null oder
     * ungültig, so wird nichts getan
     *
     * @param type
     * Typ des zu startenden Sudokus
     */
    fun setSudokuType(type: SudokuTypes?) {
        sudokuType = type
        Log.d(LOG_TAG, "type changed to:" + (type?.toString() ?: "null"))
    }

    /**
     * Setzt die Schwierigkeit des zu startenden Sudokus. Ist diese null, so
     * wird nichts getan.
     *
     * @param difficulty
     * Schwierigkeit des zu startenden Sudokus
     */
    fun setSudokuDifficulty(difficulty: Complexity) {
        complexity = difficulty
        Log.d(LOG_TAG, "complexity changed to:$difficulty")
    }

    /**
     * Ruft die AssistancesPrefererencesActivity auf.
     *
     * @param view
     * von android xml übergebene View
     */
    fun switchToAssistances(view: View?) {
        val assistancesIntent = Intent(this, NewSudokuPreferencesActivity::class.java)
        startActivity(assistancesIntent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    companion object {
        /** Attributes  */
        private val LOG_TAG = NewSudokuActivity::class.java.simpleName
        var gameSettings: GameSettings? = null
    }
}