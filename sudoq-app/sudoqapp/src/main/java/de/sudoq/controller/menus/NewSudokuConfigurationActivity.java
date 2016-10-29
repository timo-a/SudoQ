/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.sudoq.R;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.controller.menus.preferences.NewSudokuPreferencesActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.game.Game;
import de.sudoq.model.game.GameManager;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.SudokuTypesList;
import de.sudoq.model.xml.XmlTree;

/**
 * SudokuPreferences ermöglicht das Verwalten von Einstellungen eines zu
 * startenden Sudokus.
 * 
 * Hauptmenü -> "neues Sudoku" führt hierher 
 */
public class NewSudokuConfigurationActivity extends SudoqActivitySherlock {

	/** Attributes */

	private static final String LOG_TAG = NewSudokuConfigurationActivity.class.getSimpleName();

	private SudokuTypes sudokuType;

	private Complexity complexity;
	
	public static GameSettings gameSettings;

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
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.launcher);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowTitleEnabled(true);

		//for initial settings-values from Profile
		XmlTree xt = Profile.getInstance().getAssistances().toXmlTree();
		gameSettings = new GameSettings();
		gameSettings.fillFromXml(xt);
		
		/** type spinner **/
		Spinner typeSpinner = fillTypeSpinner(gameSettings.getWantedTypesList());		
		
		// nested Listener for typeSpinner
		typeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String item = parent.getItemAtPosition(pos).toString();
				if(item.equals(getString(R.string.sf_sudokupreferences_all_disabled_1))
				|| item.equals(getString(R.string.sf_sudokupreferences_all_disabled_2))){
					sudokuType=null;//set to null to prevent: going to advanced -> disabling all -> coming back -> now disabled type still set
					//pass (user disabled all types, so a hint is shown. This hint may not be saved as a sudoku type!)
				}else{
					Log.d(LOG_TAG, "OnItemSel_a "+sudokuType);
					setSudokuType(Utility.string2type(getApplicationContext(), item));
					Log.d(LOG_TAG, "OnItemSel_z "+sudokuType);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
		});

		/** complexity spinner **/
		Spinner complexitySpinner = (Spinner) findViewById(R.id.spinner_sudokucomplexity);

		ArrayAdapter<CharSequence> complexityAdapter = ArrayAdapter.createFromResource(this,
																					R.array.sudokucomplexity_values,
																					android.R.layout.simple_spinner_item);
		complexityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		complexitySpinner.setAdapter(complexityAdapter);

		// nested Listener for complexitySpinner
		complexitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				String item = parent.getItemAtPosition(pos).toString();

				setSudokuDifficulty(Utility.string2complexity(getApplicationContext(), item));
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}
		});		
		
	}

	/**
	 * Wird aufgerufen, wenn die Activity in den Vordergrund gelangt. Die
	 * Preferences werden hier neu geladen.
	 */
	@Override
	public void onResume() {
		super.onResume();
		SudokuTypesList wtl = Profile.getInstance().getAssistances().getWantedTypesList(); 
		fillTypeSpinner(wtl);
		/* this is a hack: for some reason when returning from settings, the typeSpinner selects the first position
		 *                 probably because it gets a new adapter. At the time I'm unable to debug this properly
		 *                 (judging from the LOG.d's it happens after this method) but it seems to work */
		if(wtl.contains(sudokuType))
			((Spinner) findViewById(R.id.spinner_sudokutype)).setSelection(wtl.indexOf(sudokuType));
		Log.d(LOG_TAG, "Resume_ende: "+sudokuType);
	}

	private Spinner fillTypeSpinner(SudokuTypesList stl) {
		
		Spinner typeSpinner = (Spinner) findViewById(R.id.spinner_sudokutype);
		
		List<String> wantedSudokuTypes = new ArrayList<String>();//user can choose to only have selected types offered, so here we filter
		if(stl.size()==0){
			/*special case: user disabled all sudoku types so we give him a hint*/
			wantedSudokuTypes.add(getString(R.string.sf_sudokupreferences_all_disabled_1));
			wantedSudokuTypes.add(getString(R.string.sf_sudokupreferences_all_disabled_2));
		}else{
			Collections.sort(stl);//sortieren 
			/* converse */
			for(SudokuTypes st: stl)
				wantedSudokuTypes.add(Utility.type2string(this, st));
		}
		
		Log.d(LOG_TAG, "Sudokutype_1: " + this.sudokuType);
		ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, wantedSudokuTypes);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeSpinner.setAdapter(typeAdapter);	
		Log.d(LOG_TAG, "Sudokutype_4: " + this.sudokuType);

		return typeSpinner;
	}
	
	
	/**
	 * Die Methode startet per Intent ein Sudokus mit den eingegebenen
	 * Einstellungen.
	 * 
	 * @param view
	 *            von android xml übergebene View
	 */
	public void startGame(View view) {
		if (this.sudokuType != null && this.complexity != null && gameSettings != null) {
			try {
				Game game = GameManager.getInstance().newGame(this.sudokuType, this.complexity, gameSettings);
				Profile.getInstance().setCurrentGame(game.getId());
				startActivity(new Intent(this, SudokuActivity.class));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} catch (IllegalArgumentException e) {
				Toast.makeText(this, getString(R.string.sf_sudokupreferences_copying), Toast.LENGTH_SHORT).show();
				Log.d(LOG_TAG, "no template found- 'wait please'");
			}
		} else {
			Toast.makeText(this, getString(R.string.error_sudoku_preference_incomplete), Toast.LENGTH_SHORT).show();
			
			if(this.sudokuType == null)
				Toast.makeText(this, "sudokuType", Toast.LENGTH_SHORT).show();
			if(this.complexity == null)
				Toast.makeText(this, "complexity", Toast.LENGTH_SHORT).show();
			if(this.gameSettings == null)
				Toast.makeText(this, "gameSetting", Toast.LENGTH_SHORT).show();
			
			
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
	 * Ruft die AssistancesPrefererencesActivity auf. 
	 *
	 * @param view
	 * von android xml übergebene View
	 */
	public void switchToAssistances(View view) {
		Intent assistancesIntent = new Intent(this, NewSudokuPreferencesActivity.class);
		startActivity(assistancesIntent);
	}
	
}