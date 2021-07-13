/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.sudoku.SudokuActivity.Companion.getTimeString
import de.sudoq.model.profile.ProfileSingleton
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.profile.Statistics
import de.sudoq.persistence.profile.ProfileRepo

/**
 * Diese Klasse stellt eine Activity zur Anzeige der Statisik des aktuellen
 * Spielerprofils dar.
 */
class StatisticsActivity : SudoqCompatActivity() {
    /** Methods  */
    private fun setScore(textViewID: Int, label: Int, statLabel: Statistics) {
        val current = findViewById<View>(textViewID) as TextView
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir))
        check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        pm.loadCurrentProfile()
        current.text = "${getString(label)}: ${pm.getStatistic(statLabel)}"
    }

    /**
     * Wird beim ersten Start der Activity aufgerufen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
        setScore(
            R.id.text_played_sudokus,
            R.string.statistics_played_sudokus,
            Statistics.playedSudokus
        )
        setScore(
            R.id.text_played_easy_sudokus,
            R.string.statistics_played_easy_sudokus,
            Statistics.playedEasySudokus
        )
        setScore(
            R.id.text_played_medium_sudokus,
            R.string.statistics_played_medium_sudokus,
            Statistics.playedMediumSudokus
        )
        setScore(
            R.id.text_played_difficult_sudokus,
            R.string.statistics_played_difficult_sudokus,
            Statistics.playedDifficultSudokus
        )
        setScore(
            R.id.text_played_infernal_sudokus,
            R.string.statistics_played_infernal_sudokus,
            Statistics.playedInfernalSudokus
        )
        setScore(R.id.text_score, R.string.statistics_score, Statistics.maximumPoints)
        val current = findViewById<View>(R.id.text_fastest_solving_time) as TextView
        val profilesDir = getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE)
        val pm = ProfileManager(profilesDir, ProfileRepo(profilesDir))
        check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
        pm.loadCurrentProfile()
        val timeRecordInSecs = pm.getStatistic(Statistics.fastestSolvingTime)
        var timeString = "---"
        if (timeRecordInSecs != ProfileManager.INITIAL_TIME_RECORD) {
            timeString = getTimeString(timeRecordInSecs)
        }
        current.text = getString(R.string.statistics_fastest_solving_time) + ": " + timeString
    }
    /**
     * {@inheritDoc}
     */
    //@Override
    /*public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}*/
}