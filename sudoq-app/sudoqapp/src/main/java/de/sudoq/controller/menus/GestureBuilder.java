/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import de.sudoq.R;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.controller.sudoku.FieldViewPainter;
import de.sudoq.controller.sudoku.FieldViewStates;
import de.sudoq.controller.sudoku.InputListener;
import de.sudoq.controller.sudoku.Symbol;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.view.VirtualKeyboardLayout;

/**
 * Der GestureBuilder gestattet es dem Benutzer selber Gesten für die Benutzung im Spiel zu definieren, das ermöglicht eine wesentlich höhere Erkennungsrate als mitgelieferte Gesten.
 * @author Anrion
 *
 */
public class GestureBuilder extends SudoqActivitySherlock implements OnGesturePerformedListener, InputListener {

	/**
	 * Fängt Gesteneingaben des Benutzers ab
	 */
	private GestureOverlayView gestureOverlay;

	/**
	 * Hält die von der Activity unterstützten Gesten
	 */
	private GestureStore gestureStore = new GestureStore();
	

	private static final String LOG_TAG = GestureBuilder.class.getSimpleName();

	/**
	 * Flag für das Löschen von einer Gesten.
	 */
	private boolean deleteSpecific = false;
	
	/**
	 * Der aktuelle Symbolsatz, für den Gesten eingetragen werden können.
	 */
	private String[] currentSymbolSet = Symbol.MAPPING_NUMBERS_HEX_LETTERS;
	
	/**
	 * Das aktuell ausgewählte Symbol, dass der GestureLibrary hinzugefügt werden soll.
	 */
	private String currentSelectedSymbol;

	/**
	 * Virtuelles Keyboard, zum Auswählen des Symbols, für das eine Geste angelegt werden soll.
	 */
	private VirtualKeyboardLayout virtualKeyboard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturebuilder);

		Symbol.createSymbol(currentSymbolSet);
		this.virtualKeyboard = (VirtualKeyboardLayout) findViewById(R.id.gesture_builder_virtual_keyboard);
		inflateGestures();
		refreshKeyboard();
	}
	
	@Override
	protected void onResume() {
		refreshKeyboard();
		super.onResume();
	}

	/**
	 * Erzeugt die View für die Gesteneingabe
	 */
	private void inflateGestures() {
		File gestureFile = FileManager.getCurrentGestureFile();
		try {
			this.gestureStore.load(new FileInputStream(gestureFile));
		} catch (FileNotFoundException e) {
			try {
				gestureFile.createNewFile();
			} catch (IOException ioe) {
				Log.w(LOG_TAG, "Gesture file cannot be loaded!");
			}
		} catch (IOException e) {
			Profile.getInstance().setGestureActive(false);
			Toast.makeText(this, R.string.error_gestures_no_library, Toast.LENGTH_SHORT).show();
		}

		this.gestureOverlay = new GestureOverlayView(this);
		this.gestureOverlay.addOnGesturePerformedListener(this);
		LayoutParams gestureLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.gestureOverlay.setLayoutParams(gestureLayoutParams);
		this.gestureOverlay.setBackgroundColor(Color.BLACK);
		this.gestureOverlay.getBackground().setAlpha(127);
		this.gestureOverlay.setVisibility(View.INVISIBLE);
		this.gestureOverlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.gesture_builder_layout);
		frameLayout.addView(this.gestureOverlay);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_gesture_builder, menu);    
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete_all_gestures:
			Set<String> gestures = this.gestureStore.getGestureEntries();
			for (Iterator<String> gestureIterator = gestures.iterator(); gestureIterator.hasNext();) {
				String gestureName = (String) gestureIterator.next();
				gestureIterator.remove();
			}
			saveGestures();
			refreshKeyboard();
			break;
		case R.id.action_delete_single_gesture:
			this.deleteSpecific = true;
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Speichert den aktuellen Satz an Gesten om Profile Ordner des aktuellen Benutzers
	 */
	private void saveGestures() {
		File gestureFile = FileManager.getCurrentGestureFile();
		try {
			this.gestureStore.save(new FileOutputStream(gestureFile));
		} catch (IOException e) {
			Profile.getInstance().setGestureActive(false);
			Toast.makeText(this, R.string.error_gestures_no_library, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Wurde eine Geste akzeptiert, wird diese im GestureStore gespeichert. (Das ist noch nicht persistent)
	 */
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		this.gestureStore.addGesture(this.currentSelectedSymbol, gesture);
		saveGestures();
		this.gestureOverlay.setVisibility(View.INVISIBLE);
		refreshKeyboard();
	}
	
	/**
	 * Markiert die Tasten der Tastatur so, dass alle Tasten mit Symbolen, für die bereits Gesten existieren Gelb markiert sind.
	 */
	private void markAlreadyCapturedSymbols() {
		Set<String> gestures = this.gestureStore.getGestureEntries();
		for (String sym : this.currentSymbolSet)
			if (gestures.contains(sym))
				this.virtualKeyboard.markField(Symbol.getInstance().getAbstract(sym), FieldViewStates.SELECTED_NOTE);
	}
	
	/**
	 * Aktuallisiert die Tastatur.
	 */
	private void refreshKeyboard() {
		FieldViewPainter.getInstance().setMarking(this.virtualKeyboard, FieldViewStates.KEYBOARD);
		Symbol.createSymbol(currentSymbolSet);
		this.virtualKeyboard.refresh(currentSymbolSet.length);
		this.virtualKeyboard.setActivated(true);
		this.virtualKeyboard.enableAllButtons();
		markAlreadyCapturedSymbols();
		this.virtualKeyboard.registerListener(this);
	}

	@Override
	public void onBackPressed() {
		if (this.gestureOverlay.isShown()) {
			this.gestureOverlay.setVisibility(View.INVISIBLE);
		} else {
			saveGestures();
			super.onBackPressed();
		}
	}

	/**
	 * Wird auf eine Taste gedrückt, wird die Prozedur zum eingeben von Gesten gestartet.
	 * 
	 * @param symbol Das ausgewählte Symbol
	 */
	public void onInput(int symbol) {
		Set<String> gestures = this.gestureStore.getGestureEntries();
		if (this.deleteSpecific) {
			if (gestures.contains(Symbol.getInstance().getMapping(symbol))) {
				this.gestureStore.removeEntry(Symbol.getInstance().getMapping(symbol));
				refreshKeyboard();
			}
			saveGestures();
			this.deleteSpecific = false;
		} else {
			this.currentSelectedSymbol = Symbol.getInstance().getMapping(symbol);
			final TextView textView = new TextView(gestureOverlay.getContext());
			textView.setTextColor(Color.YELLOW);
			textView.setText(" " + gestureOverlay.getContext().getString(R.string.gesture_builder_define_gesture) + " ");
			textView.setTextSize(18);
			this.gestureOverlay.addView(textView, new GestureOverlayView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
			this.gestureOverlay.setVisibility(View.VISIBLE);
		}
	}
}
