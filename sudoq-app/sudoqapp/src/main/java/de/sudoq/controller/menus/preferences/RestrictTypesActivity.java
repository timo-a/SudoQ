/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus.preferences;

import android.content.res.Configuration;
import android.os.Bundle;
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

import de.sudoq.R;
import de.sudoq.controller.SudoqListActivity;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.SudokuTypesList;

/**
 * Diese Klasse repräsentiert den Lade-Controller des Sudokuspiels. Mithilfe von
 * SudokuLoading können Sudokus geladen werden und daraufhin zur SudokuActivity
 * gewechselt werden.
 */
public class RestrictTypesActivity extends SudoqListActivity implements OnItemClickListener, OnItemLongClickListener {

	/**
	 * Der Log-Tag für das LogCat
	 */
	private static final String LOG_TAG = RestrictTypesActivity.class.getSimpleName();

	/** Attributes */

	private RestrictTypesAdapter adapter;

	private SudokuTypesList types;
	
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
		setContentView(R.layout.restricttypes);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.launcher);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowTitleEnabled(true);

		initialiseTypes();
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
		inflater.inflate(R.menu.action_bar_restrict_types, menu);    
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
		case R.id.action_restore_all:
			/* add (only!) types that are not currently selected */
			for(SudokuTypes s: types.getAllTypes())
				if(!types.contains(s))
					types.add(s);
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
		
		//Toast.makeText(getApplicationContext(), "prepOpt called. s_1: "+types.size()+" s_2: "+types.getAllTypes().size(), Toast.LENGTH_LONG).show();
		menu.findItem(R.id.action_restore_all).setVisible(types.size() < types.getAllTypes().size());//offer option to restore all only when some are disabled...
		
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
		initialiseTypes();
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

		/* toggle item */
		SudokuTypes st = adapter.getItem(position);
		if (types.contains(st))
			types.remove(st);
		else
			types.add(st);
		Profile.getInstance().saveChanges();
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		Log.d(LOG_TAG, "LongClick on "+ position + "");
		
		/* nothing */
			
		return true;//prevent itemclick from fire-ing as well
	}	

	private void initialiseTypes() {
		types = Profile.getInstance().getAssistances().getWantedTypesList();
		// initialize ArrayAdapter for the type names and set it
		adapter = new RestrictTypesAdapter(this, types);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		getListView().setOnItemLongClickListener(this);
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
		
}