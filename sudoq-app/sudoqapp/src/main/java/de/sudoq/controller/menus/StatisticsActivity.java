/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.widget.TextView;

import de.sudoq.R;
import de.sudoq.controller.SudoqCompatActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.Statistics;

/**
 * Diese Klasse stellt eine Activity zur Anzeige der Statisik des aktuellen
 * Spielerprofils dar.
 */
public class StatisticsActivity extends SudoqCompatActivity {
	/** Methods */

	private void setScore(int textViewID, int label, Statistics statLabel){
		TextView current = (TextView) findViewById(textViewID);
		current.setText(getString(label) + ": " + Profile.Companion.getInstance().getStatistic(statLabel));
	}
	
	/**
	 * Wird beim ersten Start der Activity aufgerufen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.launcher);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowTitleEnabled(true);

		setScore(R.id.text_played_sudokus,          R.string.statistics_played_sudokus,           Statistics.playedSudokus);
		setScore(R.id.text_played_easy_sudokus,     R.string.statistics_played_easy_sudokus,      Statistics.playedEasySudokus);
		setScore(R.id.text_played_medium_sudokus,   R.string.statistics_played_medium_sudokus,    Statistics.playedMediumSudokus);
		setScore(R.id.text_played_difficult_sudokus,R.string.statistics_played_difficult_sudokus, Statistics.playedDifficultSudokus);
		setScore(R.id.text_played_infernal_sudokus, R.string.statistics_played_infernal_sudokus,  Statistics.playedInfernalSudokus);
		setScore(R.id.text_score,                   R.string.statistics_score,                    Statistics.maximumPoints);
        
		TextView current = (TextView) findViewById(R.id.text_fastest_solving_time);
		
		int timeRecordInSecs = Profile.Companion.getInstance().getStatistic(Statistics.fastestSolvingTime);
		
		String timeString = "---";
		
		if (timeRecordInSecs != Profile.INITIAL_TIME_RECORD) {
			timeString = SudokuActivity.getTimeString(timeRecordInSecs);
		}
		current.setText(getString(R.string.statistics_fastest_solving_time) + ": " + timeString);
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
