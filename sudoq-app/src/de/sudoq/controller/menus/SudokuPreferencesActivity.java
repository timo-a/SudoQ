/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.game.AssistanceSet;
import de.sudoq.model.game.Game;
import de.sudoq.model.game.GameManager;
import de.sudoq.model.game.GameType;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * SudokuPreferences ermöglicht das Verwalten von Einstellungen eines zu
 * startenden Sudokus.
 * 
 * Hauptmenü -> "neues Sudoku" führt hierher 
 */
public class SudokuPreferencesActivity extends SudoqActivitySherlock {
	/** Attributes */
	private Intent startGameIntent;

	/**
	 * Konstante für den Key des GameTypes
	 */
	public static final String DESIRED_GAME_TYPE = "desired_game_type";

	/**
	 * Konstante für den Wert des Game Typs
	 */
	public static final int NO_GAME_TYPE = 0;
	/**
	 * Konstante für den Wert des Game Typs
	 */
	public static final int LOCAL_GAME = 1;
	/**
	 * Konstante für den Wert des Game Typs
	 */
	public static final int MULTIPLAYER_GAME = 2;

	private static final String LOG_TAG = SudokuPreferencesActivity.class.getSimpleName();

	private SudokuTypes sudokuType;

	private GameType gameType;

	private Complexity complexity;
	
	public static AssistanceSet assistances;

	/** Constructors */

	/**
	 * Instanziiert ein neues SudokuPreferences-Objekt.
	 */

	/** Methods */

	/**
	 * Wird beim ersten Aufruf der SudokuPreferences aufgerufen. Die Methode
	 * inflated das Layout der Preferences.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sudokupreferences);

		Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_sudokutype);
		Spinner complexitySpinner = (Spinner) findViewById(R.id.spinner_sudokucomplexity);

		ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
				this, R.array.sudokutype_values, android.R.layout.simple_spinner_item);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(typeAdapter);

		ArrayAdapter<CharSequence> complexityAdapter = ArrayAdapter.createFromResource(
				this, R.array.sudokucomplexity_values, android.R.layout.simple_spinner_item);
		complexityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		complexitySpinner.setAdapter(complexityAdapter);

		// nested Listener for typeSpinner
		complexitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
				String item = parent.getItemAtPosition(pos).toString();

				if (item.equals(getResources().getString(R.string.complexity_easy))) {
					setSudokuDifficulty(Complexity.easy);
				} else if (item.equals(getResources().getString(R.string.complexity_medium))) {
					setSudokuDifficulty(Complexity.medium);
				} else if (item.equals(getResources().getString(R.string.complexity_difficult))) {
					setSudokuDifficulty(Complexity.difficult);
				} else if (item.equals(getResources().getString(R.string.complexity_infernal))) {
					setSudokuDifficulty(Complexity.infernal);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
		});

		// nested Listener for complexitySpinner
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,
					View view, int pos, long id) {
				String item = parent.getItemAtPosition(pos).toString();

				if (item.equals(getResources().getString(R.string.sudoku_type_standard_9x9))) {
					setSudokuType(SudokuTypes.standard9x9);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_standard_16x16))) {
					setSudokuType(SudokuTypes.standard16x16);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_standard_6x6))) {
					setSudokuType(SudokuTypes.standard6x6);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_standard_4x4))) {
					setSudokuType(SudokuTypes.standard4x4);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_squiggly_a_9x9))) {
					setSudokuType(SudokuTypes.squigglya);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_squiggly_b_9x9))) {
					setSudokuType(SudokuTypes.squigglyb);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_stairstep_9x9))) {
					setSudokuType(SudokuTypes.stairstep);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_hyper))) {
					setSudokuType(SudokuTypes.HyperSudoku);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_xsudoku))) {
					setSudokuType(SudokuTypes.Xsudoku);
				} else if (item.equals(getResources().getString(R.string.sudoku_type_samurai))) {
					setSudokuType(SudokuTypes.samurai);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
		});
		
		assistances = AssistanceSet.fromString(Profile.getInstance().getAssistances().convertToString());
	}

	/**
	 * Wird aufgerufen, wenn die Activity in den Vordergrund gelangt. Die
	 * Preferences werden hier neu geladen.
	 */
	@Override
	public void onResume() {
		super.onResume();
		int desiredGameType = getIntent().getIntExtra(DESIRED_GAME_TYPE, NO_GAME_TYPE);
		switch (desiredGameType) {
		case LOCAL_GAME:
			this.startGameIntent = new Intent(this, SudokuActivity.class);
			this.gameType = GameType.LOCAL;
			break;
		// we assumed there would be multiplayer...
		}
	}

	/*
	 * {@inheritDoc}
	 */
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	// }

	/**
	 * Die Methode startet per Intent ein Sudokus mit den eingegebenen
	 * Einstellungen.
	 * 
	 * @param view
	 *            von android xml übergebene View
	 */
	public void startGame(View view) {
		if (this.sudokuType != null && this.complexity != null && this.gameType != null && assistances != null) {
			try {
				Game game = GameManager.getInstance().newGame(this.sudokuType, this.complexity, this.gameType, assistances);
				Profile.getInstance().setCurrentGame(game.getId());
				startActivity(this.startGameIntent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} catch (IllegalArgumentException e) {
				Toast.makeText(this, getString(R.string.sf_sudokupreferences_copying), Toast.LENGTH_SHORT).show();
				Log.d(LOG_TAG, "no template found- 'wait please'");
			}
		} else {
			Toast.makeText(this, getString(R.string.error_sudoku_preference_incomplete), Toast.LENGTH_SHORT).show();
			Log.d(LOG_TAG, "else- 'wait please'");			
		}
	}

	/**
	 * Setzt den Sudokutyp des zu startenden Sudokus. Ist dieser null oder
	 * ungültig, so wird nichts getan
	 * 
	 * @param type
	 *            Typ des zu startenden Sudokus
	 */
	public void setSudokuType(SudokuTypes type) {
		this.sudokuType = type;
		Log.d(LOG_TAG, "type changed to:" + type.toString());
	}

	/**
	 * Setzt die Schwierigkeit des zu startenden Sudokus. Ist diese null, so
	 * wird nichts getan.
	 * 
	 * @param difficulty
	 *            Schwierigkeit des zu startenden Sudokus
	 */
	public void setSudokuDifficulty(Complexity difficulty) {
		this.complexity = difficulty;
		Log.d(LOG_TAG, "complexity changed to:" + difficulty.toString());
	}

	/**
	 * Ruft die PrefererencesActivityNew auf mit dem Intent, dort nur
	 * Assistances einzustellen.
	 * 
	 * @param view
	 *            von android xml übergebene View
	 */
	public void switchToAssistances(View view) {
		Intent assistancesIntent = new Intent(this, AssistancesPreferencesActivity.class);
//		assistancesIntent.putExtra(PlayerPreferencesActivity.INTENT_ONLYASSISTANCES, true);
		startActivity(assistancesIntent);
	}
}
