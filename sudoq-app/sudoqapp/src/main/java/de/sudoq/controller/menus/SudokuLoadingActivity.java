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
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import de.sudoq.R;
import de.sudoq.controller.SudoqListActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.files.FileManager;
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

	private enum FAB_STATES { DELETE, INACTIVE, GO_BACK} //Floating Action Button

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
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.launcher);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);

        final Context ctx = this;
        final FloatingActionButton fab = findViewById(R.id.fab);


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
			GameManager.Companion.getInstance().deleteFinishedGames();
			break;
		case R.id.action_sudokuloading_delete_all:
			for (GameData gd : GameManager.Companion.getInstance().getGameList())
				GameManager.Companion.getInstance().deleteGame(gd.getId());
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
		List<GameData> gamesList = GameManager.Companion.getInstance().getGameList();
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
		Profile.Companion.getInstance().setCurrentGame(adapter.isEmpty() ? -1
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
            Profile.Companion.getInstance().setCurrentGame(adapter.getItem(position).getId());
            startActivity(new Intent(this, SudokuActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }else{
            /*selected in order to delete*/
            GameManager.Companion.getInstance().deleteGame(adapter.getItem(position).getId());
			onContentChanged();
        }
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		Log.d(LOG_TAG, "LongClick on "+ position + "");
		
		/*gather all options */
		List<CharSequence> temp_items = new ArrayList<CharSequence>();
		boolean specialcase = false;
		if (specialcase) { } 
		else {
			temp_items.add(getString(R.string.sudokuloading_dialog_play));
			temp_items.add(getString(R.string.sudokuloading_dialog_delete));

			if(Profile.Companion.getInstance().getAppSettings().isDebugSet()){
				temp_items.add("export as text");
				temp_items.add("export as file");
			}
		}
		final CharSequence[] items = (CharSequence[]) temp_items.toArray(new CharSequence[0]);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch (item) {
				case 0://play
					Profile.Companion.getInstance().setCurrentGame(adapter.getItem(position).getId());
					Intent i = new Intent(SudokuLoadingActivity.this, SudokuActivity.class);
					startActivity(i);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					break;
				case 1://delete
					GameManager.Companion.getInstance().deleteGame(adapter.getItem(position).getId());
					onContentChanged();
					break;
				case 2://export as text
					int gameID = adapter.getItem(position).getId();
					File gameFile = FileManager.getGameFile(gameID);

					String str = "there was an error reading the file, sorry";
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(gameFile);
						byte[] data = new byte[(int) gameFile.length()];
						fis.read(data);
						fis.close();
						str = new String(data, "UTF-8");

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);//
					//sendIntent.putExtra(Intent.EXTRA_FROM_STORAGE, gameFile);
					sendIntent.putExtra(Intent.EXTRA_TEXT, str);
					sendIntent.setType("text/plain");
					startActivity(sendIntent);
					break;

				case 3://export as file
					//already defined under 2
					/*int */ gameID = adapter.getItem(position).getId();
					/*File*/ gameFile = FileManager.getGameFile(gameID);
					/* we can only copy from 'files' subdir, so we have to move the file there first */
					File tmpFile = new File(getFilesDir(),gameFile.getName());

					InputStream in;
					OutputStream out;
					try {
						in = new FileInputStream(gameFile);
						out = new FileOutputStream(tmpFile);
						Utility.copyFileOnStreamLevel(in, out);
						in.close();
						out.flush();
						out.close();
					} catch (Exception e) {
						Log.e(LOG_TAG, e.getMessage());
						Log.e(LOG_TAG, "there seems to be an exception");
					}





					Log.v("file-share", "tmpfile: "+tmpFile.getAbsolutePath());

					Log.v("file-share", "gamefile is null? "+(gameFile==null));
					Log.v("file-share", "gamefile getPath "+gameFile.getPath());
					Log.v("file-share", "gamefile getAbsolutePath "+gameFile.getAbsolutePath());
					Log.v("file-share", "gamefile getName "+gameFile.getName());
					Log.v("file-share", "gamefile getParent "+gameFile.getParent());

					Uri fileUri = FileProvider.getUriForFile(SudokuLoadingActivity.this,
                            "de.sudoq.fileprovider", tmpFile);
					Log.v("file-share", "uri is null? "+(fileUri==null));
					/*Intent*/ sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);//
					//sendIntent.putExtra(Intent.EXTRA_FROM_STORAGE, gameFile);
					sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
					sendIntent.setType("text/plain");
					sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					//startActivity(Intent.createChooser(sendIntent, "Share to"));
					startActivity(sendIntent);

					break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();		
		
		return true;//prevent itemclick from fire-ing as well
	}




	private void initialiseGames() {
		games = GameManager.Companion.getInstance().getGameList();
		// initialize ArrayAdapter for the profile names and set it
		adapter = new SudokuLoadingAdapter(this, games);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().setOnItemLongClickListener(this);

		TextView noGamesTextView = findViewById(R.id.no_games_text_view);
		if(games.isEmpty()) {
            noGamesTextView.setVisibility(View.VISIBLE);
            final FloatingActionButton fab = findViewById(R.id.fab);
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