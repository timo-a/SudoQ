/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import java.util.List;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import de.sudoq.R;
import de.sudoq.controller.SudoqListActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.game.GameData;
import de.sudoq.model.game.GameManager;
import de.sudoq.model.profile.Profile;

/**
 * Diese Klasse repräsentiert den Lade-Controller des Sudokuspiels. Mithilfe von
 * SudokuLoading können Sudokus geladen werden und daraufhin zur SudokuActivity
 * gewechselt werden.
 */
public class SudokuLoadingActivity extends SudoqListActivity implements OnItemClickListener {

	/**
	 * Der Log-Tag für das LogCat
	 */
	private static final String LOG_TAG = SudokuLoadingActivity.class.getSimpleName();

	/** Attributes */

	private SudokuLoadingAdapter adapter;

	private List<GameData> games;

	protected static MenuItem menuDeleteFinished;
	private static final int MENU_DELETE_FINISHED = 0;

	protected static MenuItem menuDeleteSpecific;
	private static final int MENU_DELETE_SPECIFIC = 1;

	private boolean deleteMode = false;
	
    /** Constructors */

	/** Methods */

	/**
	 * Wird aufgerufen, wenn SudokuLoading nach Programmstart zum ersten Mal
	 * geladen aufgerufen wird. Hier wird das Layout inflated und es werden
	 * nötige Initialisierungen vorgenommen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sudokuloading);
		initialiseGames();
	}
	
	
	
	/**
	 * Wird beim ersten Anzeigen des Options-Menü von SudokuLoading aufgerufen
	 * und initialisiert das Optionsmenü indem das Layout inflated wird.
	 * 
	 * @return true falls das Options-Menü angezeigt werden kann, sonst false
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_sudoku_loading, menu);    
		return true;
	}

	/**
	 * Wird beim Auswählen eines Menü-Items im Options-Menü aufgerufen. Ist das
	 * spezifizierte MenuItem null oder ungültig, so wird nichts getan.
	 * 
	 * @param item
	 *            Das ausgewählte Menü-Item
	 * @return true, falls die Selection hier bearbeitet wird, false falls nicht
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sudokuloading_delete_specific:
			this.deleteMode = true;
		case R.id.action_sudokuloading_delete_finished:
			GameManager.getInstance().deleteFinishedGames();
		default:
			super.onOptionsItemSelected(item);
		}
		onContentChanged();
		return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		List<GameData> gamesList = GameManager.getInstance().getGameList();
		boolean noGames = gamesList.isEmpty();
		
		menu.findItem(R.id.action_sudokuloading_delete_specific).setVisible(!noGames);
		menu.findItem(R.id.action_sudokuloading_delete_finished).setVisible(!noGames);
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		initialiseGames();
	}

	
	/**
	 * Wird aufgerufen, falls ein Element (eine View) in der AdapterView
	 * angeklickt wird.
	 * 
	 * @param parent
	 *            AdapterView in welcher die View etwas angeklickt wurde
	 * @param view
	 *            View, welche angeklickt wurde
	 * @param position
	 *            Position der angeklickten View im Adapter
	 * @param id
	 *            ID der angeklickten View
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d(LOG_TAG, position + "");

		if (!this.deleteMode) {
			/* selected in order to play */
			Profile.getInstance().setCurrentGame(adapter.getItem(position).getId());
			startActivity(new Intent(this, SudokuActivity.class));
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		} else {
			/* user wants it deleted */
			this.deleteMode = false;
			GameManager.getInstance().deleteGame(adapter.getItem(position).getId());
			onContentChanged();
		}
	}

	private void initialiseGames() {
		games = GameManager.getInstance().getGameList();
		// initialize ArrayAdapter for the profile names and set it
		adapter = new SudokuLoadingAdapter(this, games);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);

		TextView noGamesTextView = (TextView) findViewById(R.id.no_games_text_view);
		TextView noGamesBackButton = (TextView) findViewById(R.id.no_games_back_button);
		if (games.isEmpty()) {
			noGamesTextView.setVisibility(View.VISIBLE);
			noGamesBackButton.setVisibility(View.VISIBLE);
		} else {
			noGamesTextView.setVisibility(View.INVISIBLE);
			noGamesBackButton.setVisibility(View.INVISIBLE);
		}

		TextView deleteSpecific = (TextView) findViewById(R.id.delete_specific_game_text_view);
		if (this.deleteMode) {
			deleteSpecific.setVisibility(View.VISIBLE);
		} else {
			deleteSpecific.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Führt die onBackPressed-Methode aus.
	 * 
	 * @param view
	 *            unbenutzt
	 */
	public void goBack(View view) {
		super.onBackPressed();
	}

	/**
	 * Just for testing!
	 * @return
	 * 		number of saved games 
	 */
	public int getSize() {
		return games.size();
	}	
	
}