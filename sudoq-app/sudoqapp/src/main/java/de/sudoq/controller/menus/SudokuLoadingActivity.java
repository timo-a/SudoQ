/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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
public class SudokuLoadingActivity extends SudoqListActivity implements OnItemClickListener, OnItemLongClickListener {

	/**
	 * Der Log-Tag für das LogCat
	 */
	private static final String LOG_TAG = SudokuLoadingActivity.class.getSimpleName();

	/** Attributes */

	private SudokuLoadingAdapter adapter;

	private List<GameData> games;

/*	protected static MenuItem menuDeleteFinished;
	private static final int MENU_DELETE_FINISHED = 0;

	protected static MenuItem menuDeleteSpecific;
	private static final int MENU_DELETE_SPECIFIC = 1; commented out to make sure it's not needed*/

	private enum FAB_STATES { DELETE, INACTIVE, GO_BACK};

    private FAB_STATES fabstate=FAB_STATES.INACTIVE;

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
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.launcher);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);

        final Context ctx = this;
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            Drawable trash = ContextCompat.getDrawable(ctx, R.drawable.ic_delete_white_24dp);
            Drawable close = ContextCompat.getDrawable(ctx, R.drawable.ic_close_white_24dp);

            @Override
            public void onClick(View view) {
                switch (fabstate){
                    case INACTIVE:
                        fabstate = FAB_STATES.DELETE;
                        fab.setImageDrawable(close);
                        Toast.makeText(ctx, R.string.fab_go_back,Toast.LENGTH_LONG).show();
                        break;
                    case DELETE:
                        fabstate = FAB_STATES.INACTIVE;
                        fab.setImageDrawable(trash);

                        break;
                    case GO_BACK:
                        goBack(view);
                        break;
                }
            }
        });

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
		MenuInflater inflater = getMenuInflater();
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
		case R.id.action_sudokuloading_delete_finished:
			GameManager.getInstance().deleteFinishedGames();
			break;
		case R.id.action_sudokuloading_delete_all:
			for (GameData gd : GameManager.getInstance().getGameList())
				GameManager.getInstance().deleteGame(gd.getId());
			break;
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
		
		menu.findItem(R.id.action_sudokuloading_delete_finished).setVisible(!noGames);
		menu.findItem(R.id.action_sudokuloading_delete_all     ).setVisible(!noGames);
		
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
		Profile.getInstance().setCurrentGame(adapter.isEmpty() ? -1
				                                               : adapter.getItem(0).getId());
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

        if(fabstate==FAB_STATES.INACTIVE) {
		    /* selected in order to play */
            Profile.getInstance().setCurrentGame(adapter.getItem(position).getId());
            startActivity(new Intent(this, SudokuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }else{
            /*selected in order to delete*/
            GameManager.getInstance().deleteGame(adapter.getItem(position).getId());
			onContentChanged();
        }
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		Log.d(LOG_TAG, "LongClick on "+ position + "");
		
		/*gather all options */
		CharSequence[] temp_items = null;
		boolean specialcase = false;
		if (specialcase) { } 
		else {
			temp_items = new CharSequence[2];
			temp_items[0]= getString(R.string.sudokuloading_dialog_play);
			temp_items[1]= getString(R.string.sudokuloading_dialog_delete);
		}
		final CharSequence[] items = temp_items;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0://play
					Profile.getInstance().setCurrentGame(adapter.getItem(position).getId());
					Intent i = new Intent(SudokuLoadingActivity.this, SudokuActivity.class);
					startActivity(i);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case 1://delete
					GameManager.getInstance().deleteGame(adapter.getItem(position).getId());
					onContentChanged();
					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();		
		
		return true;//prevent itemclick from fire-ing as well
	}	

	private void initialiseGames() {
		games = GameManager.getInstance().getGameList();
		// initialize ArrayAdapter for the profile names and set it
		adapter = new SudokuLoadingAdapter(this, games);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().setOnItemLongClickListener(this);

		TextView noGamesTextView =   (TextView) findViewById(R.id.no_games_text_view);
		if(games.isEmpty()) {
            noGamesTextView.setVisibility(View.VISIBLE);
            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp));
            fabstate=FAB_STATES.GO_BACK;
        }else{
            noGamesTextView.setVisibility(View.INVISIBLE);
            //pass
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


    private class FAB extends FloatingActionButton{

        public FAB(Context context) {
            super(context);
        }
        private FAB_STATES fs;

        public void setState(FAB_STATES fs){
            this.fs=fs;
            int id;
            switch (fs){
                case DELETE:
                    id=R.drawable.ic_close_white_24dp;
                    break;
                case INACTIVE:
                    id=R.drawable.ic_delete_white_24dp;
                    break;
                default://case GO_BACK:
                    id=R.drawable.ic_arrow_back_white_24dp;
                    break;
            }
            super.setImageDrawable(ContextCompat.getDrawable(this.getContext(), id));
        }


    }
}