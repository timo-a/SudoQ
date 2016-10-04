/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStore;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.sudoq.R;
import de.sudoq.controller.SudoqActivitySherlock;
import de.sudoq.controller.menus.Utility;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.Game;
import de.sudoq.model.game.GameManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solvingAssistant.SolvingAssistant;
import de.sudoq.model.sudoku.Field;
import de.sudoq.view.FullScrollLayout;
import de.sudoq.view.SudokuFieldView;
import de.sudoq.view.SudokuLayout;
import de.sudoq.view.VirtualKeyboardLayout;

/**
 * Diese Klasse stellt die Activity des Sudokuspiels dar. Die Klasse hält das
 * Game und mehrere Controller um auf Interaktionen des Benutzers mit dem
 * Spielfeld zu reagieren. Die Klasse wird außerdem benutzt um zu verwalten,
 * welche Navigationselemente dem Nutzer angezeigt werden.
 */
public class SudokuActivity extends SudoqActivitySherlock implements OnClickListener, ActionListener, ActionTreeNavListener {

	/** Attributes */

	/**
	 * Der Log-TAG
	 */
	private static final String LOG_TAG = SudokuActivity.class.getSimpleName();

	/**
	 * Konstante für das Speichern der Game ID
	 */
	private static final int SAVE_GAME_ID = 0;

	/**
	 * Konstante für das Speichern der X-Koordinate der ausgewählten FieldView
	 */
	private static final int SAVE_FIELD_X = 1;

	/**
	 * Konstante für das Speichern der Y-Koordinate der ausgewählten FieldView
	 */
	private static final int SAVE_FIELD_Y = 2;

	/**
	 * Konstante für das Speichern des Aktionsbaum-Status
	 */
	private static final int SAVE_ACTION_TREE_SHOWN = 3;

	/**
	 * Konstante für das Speichern der Gesteneingabe
	 */
	private static final int SAVE_GESTURE_ACTIVE = 4;

	/**
	 * Konstante für das Speichern des aktuellen Zoomfaktors
	 */
	private static final int SAVE_ZOOM_FACTOR = 5;

	/**
	 * Konstante für das Speichern des Scrollwertes in X-Richtung
	 */
	private static final int SAVE_SCROLL_X = 6;

	/**
	 * Konstante für das Speichern des Scrollwertes in Y-Richtung
	 */
	private static final int SAVE_SCROLL_Y = 7;

	/**
	 * Eine Referenz auf einen ActionTreeController, der die Verwaltung der
	 * ActionTree-Anzeige und Benutzerinteraktion übernimmt
	 */
	private ActionTreeController actionTreeController;

	/**
	 * Eine Referenz auf einen SudokuController, der Nutzereingaben verwaltet
	 * und mit dem Model interagiert
	 */
	private SudokuController sudokuController;

	/**
	 * Die View des aktuellen Sudokus
	 */
	private SudokuLayout sudokuView;

	/**
	 * Die ScrollView, welche die SudokuView beinhaltet
	 */
	private FullScrollLayout sudokuScrollView;

	/**
	 * Das Game, auf welchem gerade gespielt wird
	 */
	private Game game;

	/**
	 * Fängt Gesteneingaben des Benutzers ab
	 */
	private GestureOverlayView gestureOverlay;

	/**
	 * Hält die von der Activity unterstützten Gesten
	 */
	private GestureStore gestureStore = new GestureStore();

	/**
	 * Ein Flag welches aussagt, ob gerade der ActionTree angezeigt wird
	 */
	private boolean actionTreeShown;


	private enum Mode {Regular, HintMode};
	private Mode mode=Mode.Regular;//TODO see that this gets saved in oninstancesaved and restored so hint persitst orientation change

	/**
	 * Der Handler für die Zeit
	 */
	private final Handler timeHandler = new Handler();

	/**
	 * Zeigt an, dass dieses Spiel beendet wurde
	 */
	private boolean finished;

	/**
	 * Der Vermittler zwischen Sudoku und Eingabemöglichkeiten
	 */
	private UserInteractionMediator mediator;

	private String[] currentSymbolSet;
	
	/** for time. YES IT IS USED!*/
	private Menu mMenu;
	/** Methods */

	private void initializeSymbolSet(){
		switch (this.game.getSudoku().getSudokuType().getNumberOfSymbols()) {
			case 4:		currentSymbolSet = Symbol.MAPPING_NUMBERS_FOUR;			break;

			case 6:		currentSymbolSet = Symbol.MAPPING_NUMBERS_SIX;			break;

			case 9:		currentSymbolSet = Symbol.MAPPING_NUMBERS_NINE;			break;

			case 16:	currentSymbolSet = Symbol.MAPPING_NUMBERS_HEX_LETTERS;	break;

			default:	currentSymbolSet = Symbol.MAPPING_NUMBERS_HEX_LETTERS;	break;
		}
		Symbol.createSymbol(currentSymbolSet);

	}
	
	/**
	 * Wird beim ersten Aufruf der Activity aufgerufen. Setzt das Layout der
	 * Activity und nimmt Initialisierungen vor.
	 * 
	 * @param savedInstanceState
	 *            Gespeicherte Daten eines vorigen Aufrufs dieser Activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(LOG_TAG, "Created");
		// Load the Game by using current game id
		if (savedInstanceState != null) {
			try {
				this.game = GameManager.getInstance().load(savedInstanceState.getInt(SAVE_GAME_ID + ""));
			} catch (Exception e) {
				this.finish();
			}
		} else {
			this.game = GameManager.getInstance().load(Profile.getInstance().getCurrentGame());
		}

		if (game != null) {
			/* Determine how many numbers are needed. 1-9 or 1-16 ? */
			initializeSymbolSet();
/*			if (game.isLefthandedModeActive()){
				setContentView(R.layout.sudoku_for_lefties);

			}
			else {
				setContentView(R.layout.sudoku);
			}*/
            setContentView(game.isLefthandedModeActive()? R.layout.sudoku_for_lefties : R.layout.sudoku);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            this.sudokuController = new SudokuController(this.game, this);
			this.actionTreeController = new ActionTreeController(this);
			Log.d(LOG_TAG, "Initialized");
			inflateViewAndButtons();
			Log.d(LOG_TAG, "Inflated view and buttons");
			inflateGestures(savedInstanceState == null);
			Log.d(LOG_TAG, "Inflated gestures");
			// Scale SudokuView to LayoutSize, when inflating view is finished
			final Bundle save = savedInstanceState;
			ViewTreeObserver vto = sudokuView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					Log.d(LOG_TAG, "SudokuView height: " + sudokuView.getMeasuredHeight());
					Log.d(LOG_TAG, "SudokuScrollView height: " + sudokuScrollView.getMeasuredHeight());
					sudokuView.optiZoom(sudokuScrollView.getMeasuredWidth(), sudokuScrollView.getMeasuredHeight());
					ViewTreeObserver obs = sudokuView.getViewTreeObserver();
					if (save != null) {
						float zoomFactor = save.getFloat(SAVE_ZOOM_FACTOR + "");
						if (zoomFactor != 0.0f) {
							sudokuView.zoom(zoomFactor);
							sudokuScrollView.setZoomFactor(zoomFactor);
						}
						float scrollX = save.getFloat(SAVE_SCROLL_X + "") + sudokuView.getCurrentLeftMargin();
						float scrollY = save.getFloat(SAVE_SCROLL_Y + "") + sudokuView.getCurrentTopMargin();
						sudokuScrollView.scrollTo((int) scrollX, (int) scrollY);
					}
					obs.removeGlobalOnLayoutListener(this);
				}
			});
			VirtualKeyboardLayout keyboardView = (VirtualKeyboardLayout) findViewById(R.id.virtual_keyboard);
			this.mediator = new UserInteractionMediator(keyboardView, this.sudokuView, this.game, this.gestureOverlay, this.gestureStore);
			this.mediator.registerListener(this.sudokuController);
			this.mediator.registerListener(this);
			if (this.game.isFinished()) {
				setFinished(false, false);
			}

			setTypeText();
			updateButtons();
			Buttons.gestureButton.setSelected(Profile.getInstance().isGestureActive());
		}
	}

	/**
	 * Speichert das markierte Feld und die Status des Aktionsbaumes, um bei
	 * Wiederherstellung der Activity nach einem Orientierungswechsel oder
	 * aufgrund einer temporären Verdrängung durch Speicherknappheit den alten
	 * Status wiederherzustellen.
	 * 
	 * @param outState
	 *            Der Status in den gespeichert wird
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putFloat(SAVE_ZOOM_FACTOR + "", this.sudokuScrollView.getZoomFactor());
		outState.putFloat(SAVE_SCROLL_X + "", this.sudokuScrollView.getScrollValueX() - this.sudokuView.getCurrentLeftMargin());
		outState.putFloat(SAVE_SCROLL_Y + "", this.sudokuScrollView.getScrollValueY() - this.sudokuView.getCurrentTopMargin());
		outState.putBoolean(SAVE_ACTION_TREE_SHOWN + "", this.actionTreeShown);
		outState.putInt(SAVE_GAME_ID + "", game.getId());
		outState.putBoolean(SAVE_GESTURE_ACTIVE + "", this.gestureOverlay != null && this.gestureOverlay.getVisibility() == View.VISIBLE);
		if (this.sudokuView.getCurrentFieldView() != null) {
			outState.putInt(SAVE_FIELD_X + "", game.getSudoku().getPosition(this.sudokuView.getCurrentFieldView().getField().getId()).getX());
			outState.putInt(SAVE_FIELD_Y + "", game.getSudoku().getPosition(this.sudokuView.getCurrentFieldView().getField().getId()).getY());
		} else {
			outState.putInt(SAVE_FIELD_X + "", -1);
		}
		Log.d(LOG_TAG, "Saved state");
	}

	/**
	 * Stellt den Status der Activity wieder her, also insbesondere das
	 * markierte Feld und den Status der Aktionsbaumes.
	 * 
	 * @param state
	 *            Der wiederherzustellende Status
	 */
	@Override
	public void onRestoreInstanceState(Bundle state) {

		if (state.getBoolean(SAVE_ACTION_TREE_SHOWN + "")) {
			ViewTreeObserver vto = sudokuView.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					toogleActionTree();
					ViewTreeObserver obs = sudokuView.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
				}
			});
		}
		if (state.getInt(SAVE_FIELD_X + "") != -1) {
			this.sudokuView.getSudokuFieldViews()[state.getInt(SAVE_FIELD_X + "")][state.getInt(SAVE_FIELD_Y + "")].onTouchEvent(null);
		}

		if (state.getBoolean(SAVE_GESTURE_ACTIVE + "")) {
			this.mediator.onFieldSelected(this.sudokuView.getCurrentFieldView());
		}

		if(mode==Mode.HintMode) {
			findViewById(R.id.controlPanel).setVisibility(View.GONE);
			findViewById(R.id.   hintPanel).setVisibility(View.VISIBLE);
		}

		Log.d(LOG_TAG, "Restored state");
	}

	/**
	 * Setzt den Text für Typ und Schwierigkeit des aktuellen Sudokus.
	 */
	private void setTypeText() {

		String type = Utility.      type2string(this, this.game.getSudoku().getSudokuType().getEnumType());
		String comp = Utility.complexity2string(this, this.game.getSudoku().getComplexity());

		ActionBar ab = getSupportActionBar();
		ab.setTitle(type);
        ab.setSubtitle(comp);

    }

	/**
	 * Erzeugt die View für die Gesteneingabe
	 * 
	 * @param firstStart
	 *            Gibt an, ob dies der erste Start der Activity ist und somit
	 *            Hinweise angezeigt werden sollen
	 */
	private void inflateGestures(boolean firstStart) {
		File gestureFile = FileManager.getCurrentGestureFile();
		try {
			FileInputStream fis = new FileInputStream(gestureFile);
			this.gestureStore.load(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			try {
				OutputStream os = new FileOutputStream(gestureFile);
				this.gestureStore.save(os);
			} catch (IOException ioe) {
				Log.w(LOG_TAG, "Gesture file cannot be loaded!");
			}
		} catch (IOException e) {
			Profile.getInstance().setGestureActive(false);
			Toast.makeText(this, R.string.error_gestures_no_library, Toast.LENGTH_SHORT).show();
		}

		if (firstStart && Profile.getInstance().isGestureActive()) {
			boolean allGesturesSet = checkGesture();
			if (!allGesturesSet) {
				Profile.getInstance().setGestureActive(false);
				Toast.makeText(this, getString(R.string.error_gestures_not_complete), Toast.LENGTH_SHORT).show();
			}
		}

		this.gestureOverlay = new GestureOverlayView(this);
		LayoutParams gestureLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.gestureOverlay.setLayoutParams(gestureLayoutParams);
		this.gestureOverlay.setBackgroundColor(Color.BLACK);
		this.gestureOverlay.getBackground().setAlpha(127);
		this.gestureOverlay.setVisibility(View.INVISIBLE);

		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.sudoku_frame_layout);
		frameLayout.addView(this.gestureOverlay);
	}

	/**
	 * Erstellt die Views und Buttons für diese Activity
	 */
	private void inflateViewAndButtons() {
		
		this.sudokuScrollView = (FullScrollLayout) findViewById(R.id.sudoku_field);
		this.sudokuView = new SudokuLayout(this);
		Log.d(LOG_TAG, "Inflated sudoku layout");
		this.sudokuView.setGravity(Gravity.CENTER);
		this.sudokuScrollView.addView(this.sudokuView);

		Buttons.       redoButton = (ImageButton) findViewById(R.id.button_sudoku_redo);
		Buttons.       undoButton = (ImageButton) findViewById(R.id.button_sudoku_undo);
		Buttons. actionTreeButton = (ImageButton) findViewById(R.id.button_sudoku_actionTree);
		Buttons.    gestureButton = (ImageButton) findViewById(R.id.button_sudoku_toggle_gesture);
		Buttons.assistancesButton = (ImageButton) findViewById(R.id.button_sudoku_help);
		Buttons.   bookmarkButton = (Button) findViewById(R.id.sudoku_action_tree_button_bookmark);
		Buttons.      closeButton = (Button) findViewById(R.id.sudoku_action_tree_button_close);

		LinearLayout currentControlsView;/* = (LinearLayout) findViewById(R.id.sudoku_time_border);
		FieldViewPainter.getInstance().setMarking(currentControlsView, FieldViewStates.CONTROLS);*/
		currentControlsView = (LinearLayout) findViewById(R.id.sudoku_border);
		FieldViewPainter.getInstance().setMarking(currentControlsView, FieldViewStates.SUDOKU);
		currentControlsView = (LinearLayout) findViewById(R.id.controls);
		FieldViewPainter.getInstance().setMarking(currentControlsView, FieldViewStates.KEYBOARD);
		VirtualKeyboardLayout keyboardView = (VirtualKeyboardLayout) findViewById(R.id.virtual_keyboard);
		FieldViewPainter.getInstance().setMarking(keyboardView, FieldViewStates.KEYBOARD);
		keyboardView.refresh(this.game.getSudoku().getSudokuType().getNumberOfSymbols());
	}

	/**
	 * Schaltet den ActionTree an bzw. aus.
	 */
	public void toogleActionTree() {
		actionTreeShown = !actionTreeShown;//toggle value
		this.actionTreeController.setVisibility(actionTreeShown);//update AT-Controller

		updateButtons();
	}

	/**
	 * Behandelt die Klicks auf Buttons dieser Activity
	 */
	public void onClick(View v) {
		if (v == Buttons.undoButton) {
			this.sudokuController.onUndo();
			this.mediator.updateKeyboard();
		} else if (v == Buttons.redoButton) {
			this.sudokuController.onRedo();
			this.mediator.updateKeyboard();
		} else if (v == Buttons.actionTreeButton) {
			toogleActionTree();
		} else if (v == Buttons.gestureButton) {
			Profile profile = Profile.getInstance();
			if (checkGesture()) {
				/* toggle 'gesture active' 
				 * toggle button icon as well */
				profile.setGestureActive( !profile.isGestureActive() );
				v.setSelected(profile.isGestureActive());
			} else {
				profile.setGestureActive(false);
				v.setSelected(           false);
				Toast.makeText(this, getString(R.string.error_gestures_not_complete), Toast.LENGTH_LONG).show();
			}
		} else if (v == Buttons.assistancesButton) {
			showAssistancesDialog();
		} else if (v == Buttons.bookmarkButton) {
			this.game.markCurrentState();
			this.actionTreeController.refresh();
		} else if (v == Buttons.closeButton) {
			toogleActionTree();
		} else if (v == Buttons.switchSymbols) {
			if (currentSymbolSet.length < 9) {
				if (currentSymbolSet[10].equals("10")) {
					currentSymbolSet = Symbol.MAPPING_NUMBERS_HEX_LETTERS;
				} else {
					currentSymbolSet = Symbol.MAPPING_NUMBERS_HEX_DIGGITS;
				}
				Symbol.createSymbol(currentSymbolSet);
			} else {
				Toast.makeText(this, getString(R.string.error_cant_switch_symbols), Toast.LENGTH_SHORT).show();
			}
		}
		updateButtons();
	}

	/*
	 * returns whether all Gestures are defined -> Gesture input possible */
	private boolean checkGesture() {
		Set<String> gestures = this.gestureStore.getGestureEntries();
		boolean allGesturesSet = true;
		for (int i = 0; i < Symbol.getInstance().getNumberOfSymbols(); i++) {
			if (!gestures.contains(Symbol.getInstance().getMapping(i))) {
				allGesturesSet = false;
			}
		}
		return allGesturesSet;
	}

	/**
	 * Wird aufgerufen, falls die Activity in den Vordergrund der App gelangt.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (!this.finished)
			timeHandler.postDelayed(timeUpdate, 1000);
	}

	/**
	 * Wird aufgerufen, falls eine andere Activity in den Vordergrund der App
	 * gelangt.
	 */
	@Override
	public void onPause() {
		this.timeHandler.removeCallbacks(timeUpdate);
		GameManager.getInstance().save(this.game);

		float prevZoomFactor = this.sudokuScrollView.getZoomFactor();
		sudokuView.setDrawingCacheEnabled(true);
		sudokuScrollView.resetZoom();
		
		// Restoring measurements after zomming out.
		this.sudokuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		this.sudokuView.layout(0, 0, this.sudokuView.getMeasuredWidth(), this.sudokuView.getMeasuredHeight());
		this.sudokuView.buildDrawingCache(true);
		Bitmap sudokuCapture = sudokuView.getDrawingCache();
		try {
			if (sudokuCapture != null) {
				sudokuCapture.compress(CompressFormat.PNG, 100, new FileOutputStream(FileManager.getGameThumbnailFile(Profile.getInstance().getCurrentGame())));
			} else {
				Log.d(LOG_TAG, getString(R.string.error_thumbnail_get));
			}
		} catch (FileNotFoundException e) {
			Log.w(LOG_TAG, getString(R.string.error_thumbnail_saved));
		}

		this.sudokuScrollView.setZoomFactor(prevZoomFactor);

		if (finished) {
			Profile.getInstance().setCurrentGame(Profile.NO_GAME);
			Profile.getInstance().saveChanges();
		}
		super.onPause();
	}

	/**
	 * Wird aufgerufen, falls die "Zurück"-Taste gedrückt wird.
	 */
	@Override
	public void onBackPressed() {
		if (this.actionTreeShown) {
			toogleActionTree();
		} else if (this.gestureOverlay.getVisibility() == GestureOverlayView.VISIBLE) {
			this.gestureOverlay.setVisibility(GestureOverlayView.INVISIBLE);
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * Wird aufgerufen, falls die Activity terminiert.
	 */
	@Override
	public void finish() {
		if (this.game != null) {
			GameManager.getInstance().save(this.game);
		}
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	public void setModeHint(){
		findViewById(R.id.controlPanel).setVisibility(View.GONE);
		findViewById(R.id.hintPanel).setVisibility(View.VISIBLE);
		mode=Mode.HintMode;
	}

	public void setModeRegular(){
		findViewById(R.id.   hintPanel).setVisibility(View.GONE);
		findViewById(R.id.controlPanel).setVisibility(View.VISIBLE);
		mode=Mode.Regular;
	}


	/**
	 * Zeigt einen Dialog mit den verfügbaren Hilfestellungen an.
	 */
	private void showAssistancesDialog() {

		Stack<CharSequence> itemStack= new Stack<>();
		itemStack.addAll(Arrays.asList( getString(R.string.sf_sudoku_assistances_solve_surrender)
		                              , getString(R.string.sf_sudoku_assistances_back_to_valid_state)
		                              , getString(R.string.sf_sudoku_assistances_back_to_bookmark)
		                              , getString(R.string.sf_sudoku_assistances_check)
		                              , getString(R.string.sf_sudoku_assistances_solve_random)));

		SudokuFieldView v = this.sudokuView.getCurrentFieldView();
		if (v != null && v.getField().isEmpty())
			itemStack.add(getString(R.string.sf_sudoku_assistances_solve_specific));

		if (Profile.getInstance().getAssistances().isHelperSet())
			itemStack.add(getString(R.string.sf_sudoku_assistances_give_hint));

		// TODO why this no work? final CharSequence[] items = (CharSequence[]) itemStack.toArray();
		CharSequence[] tmp   = new CharSequence[0];
		final CharSequence[] items = itemStack.toArray(tmp);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.sf_sudoku_assistances_title));

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				switch (item) {
					case 0:
						if (!SudokuActivity.this.sudokuController.onSolveAll())
							Toast.makeText(SudokuActivity.this, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
						break;
					case 1:
						SudokuActivity.this.game.goToLastCorrectState();
						break;
					case 2:
						SudokuActivity.this.game.goToLastBookmark();
						break;
					case 3:
						if (SudokuActivity.this.game.checkSudoku())
							Toast.makeText(SudokuActivity.this, R.string.toast_solved_correct, Toast.LENGTH_SHORT).show();
						 else
							Toast.makeText(SudokuActivity.this, R.string.toast_solved_wrong, Toast.LENGTH_LONG).show();
						break;
					case 4:
						if (!SudokuActivity.this.sudokuController.onSolveOne())
							Toast.makeText(SudokuActivity.this, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
						break;
				}

				if (items[item] == getString(R.string.sf_sudoku_assistances_solve_specific)){
					if (!SudokuActivity.this.sudokuController.onSolveCurrent(SudokuActivity.this.sudokuView.getCurrentFieldView().getField())) {
						Toast.makeText(SudokuActivity.this, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
					}

				}else if (items[item] == getString(R.string.sf_sudoku_assistances_give_hint)){
					SolveDerivation sd = SolvingAssistant.giveAHint(game.getSudoku());

					TextView tv = (TextView) findViewById(R.id.hintText);
					tv.setText(HintFormulator.getText(getBaseContext(),  sd));
					SudokuActivity.this.setModeHint();

					getSudokuLayout().getHintPainter().realizeHint(sd);
					getSudokuLayout().getHintPainter().invalidateAll();

					Button b = (Button) findViewById(R.id.hintOkButton);
					b.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {

							SudokuActivity.this.setModeRegular();

							getSudokuLayout().getHintPainter().deleteAll();
							getSudokuLayout().invalidate();
							getSudokuLayout().getHintPainter().invalidateAll();
						}
					});


					//Toast.makeText(SudokuActivity.this, "a hint was requested: "+sd, Toast.LENGTH_LONG).show();
				}
				updateButtons();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Gibt das aktuelle Game zurück.
	 * 
	 * @return Das Game
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * Gibt die aktuell ausgewählte FieldView zurück.
	 * 
	 * @return Die aktuell ausgewählte FieldView
	 */
	public SudokuFieldView getCurrentFieldView() {
		return this.sudokuView.getCurrentFieldView();
	}

	/**
	 * Setzt die aktuelle FieldView auf die spezifizierte.
	 * 
	 * @param fieldView
	 *            Die als aktuell zu setzende FieldView
	 */
	public void setCurrentFieldView(SudokuFieldView fieldView) {
		this.sudokuView.setCurrentFieldView(fieldView);
	}

	/**
	 * Gibt zurück, ob zurzeit der ActionTree angezeigt wird.
	 * 
	 * @return true, falls der ActionTree gerade angezeigt wird, false falls
	 *         nicht
	 */
	public boolean isActionTreeShown() {
		return this.actionTreeShown;
	}

	/**
	 * Setzt dieses Spiel auf beendet.
	 * 
	 * @param showWinDialog
	 *            Spezifiziert, ob ein Gewinn-Dialog angezeigt werden soll
	 * @param surrendered
	 *            Gibt an, ob der Spieler aufgegeben hat
	 */
	protected void setFinished(boolean showWinDialog, boolean surrendered) {
		this.finished = true;

		this.updateButtons();
		if (this.sudokuView.getCurrentFieldView() != null)
			this.sudokuView.getCurrentFieldView().select(this.game.isAssistanceAvailable(Assistances.markRowColumn));

		VirtualKeyboardLayout keyView = (VirtualKeyboardLayout) findViewById(R.id.virtual_keyboard);
		for (int i = 0; i < keyView.getChildCount(); i++) {
			keyView.getChildAt(i).setLayoutParams(new VirtualKeyboardLayout.LayoutParams(1, 1));
		}
		keyView.setPadding(10, 10, 10, 10);
		TextView text = new TextView(this);
		text.setText(getStatisticsString());
		text.setGravity(Gravity.CENTER);
		keyView.addView(text);

		if (showWinDialog)
			showWinDialog(surrendered);
		this.timeHandler.removeCallbacks(timeUpdate);
	}

	protected String getAssistancesTimeString() {
		return getTimeString(game.getAssistancesTimeCost());
	}
	
	/**
	 * Gibt die vergangene Zeit als formatierten String zurück.
	 * 
	 * @return Den String für die Zeitanzeige
	 */
	protected String getGameTimeString() {
		return getTimeString(game.getTime());
	}
	
	/**
	 * Returns a string in the format "HH:mm:ss" implied by the specified time in seconds.
	 * There is no zero-padding for Hours, instead the string is just shorter if hours is zero.
	 * @param time the time to format in seconds
	 * @return a string representing the specified time in format "D..D HH:mm:ss"
	 */
	public static String getTimeString(int time) {
		
		int seconds = time % 60;
		time /= 60;
		
		int minutes = time % 60;
		time /= 60;
		
		int hours   = time % 24;
		time /= 24;

		int days = time;
		
		StringBuilder pattern = new StringBuilder("");
		
		if( days > 0)
			pattern.append(days).append(" ");
		if(hours > 0)
			pattern.append(String.format("%02d:", hours));

		pattern.append(String.format("%02d:%02d", minutes, seconds));
		
		return pattern.toString();
	}

	/**
	 * Zeigt einen Gewinndialog an, der fragt, ob das Spiel beendet werden soll.
	 * 
	 * @param surrendered
	 *            Gibt an, ob der Spieler aufgegeben hat
	 */
	private void showWinDialog(boolean surrendered) {
		AlertDialog deleteAlert = new AlertDialog.Builder(this).create();
		if (surrendered) {
			deleteAlert.setTitle(getString(R.string.dialog_surrender_title));
		} else {
			deleteAlert.setTitle(getString(R.string.dialog_won_title));
		}
		deleteAlert.setMessage(getString(R.string.dialog_won_text) + "\n\n" + getStatisticsString());
		deleteAlert.setButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		deleteAlert.setButton2(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Dummy: clicking no means staying in the game
			}
		});
		deleteAlert.show();
	}

	/**
	 * Gibt einen String mit der Spielstatistik zurück.
	 * 
	 * @return Die Spielstatistik als String
	 */
	private String getStatisticsString() {
		StringBuilder stat= new StringBuilder("");
		stat.append(getString(R.string.dialog_won_statistics) + ":\n");
		stat.append("\n");
		stat.append(getString(R.string.dialog_won_timeneeded) + ": " + getGameTimeString() + "\n");
		stat.append(getString(R.string.dialog_won_score)      + ": " + game.getScore());
		return stat.toString();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_sudoku, menu);    
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mMenu = menu;
		return super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * Das Update-Runnable für die Zeit
	 */
	private Runnable timeUpdate = new Runnable() {
		private String offset = "";
		public void run() {
			game.addTime(1);
			
			//getSupportActionBar().
			final TextView timeView    = (TextView) findViewById (R.id.time);
			timeView.setTextColor(getResources().getColor(R.color.text1));


			/* for easy formatting, we display both: time and penalty on one element separated by \n
			 * this is not  perfect since we padd with whitespace and the font is not 'mono-style'(=not all letters same width)
			 * solution would be: create custom xml for action bar, but as of now I see no way how to make this easy.
			 * to save computing time we cache the offset*/
			String t = getGameTimeString();
			String p = " (+ " + getAssistancesTimeString() + ")";
			int d = t.length() - p.length();
			while(offset.length() > Math.abs(d)){offset = offset.substring(1);}
			while(offset.length() < Math.abs(d)){offset = " " + offset;}
			if(d > 0){
				p = offset + p;
			}
			if(d < 0){
				t = offset + t;
			}
			timeView.setText(t + "\n" + p);

			timeHandler.postDelayed(this, 1000);
		}
	};

	/**
	 * Container-Klasse für die Buttons dieser Activity
	 */
	private static class Buttons {
		/**
		 * Der "Redo" Button
		 */
		protected static ImageButton redoButton;

		/**
		 * Der "Undo" Button
		 */
		protected static ImageButton undoButton;

		/**
		 * Der "ActionTree anzeigen" Button
		 */
		protected static ImageButton actionTreeButton;

		/**
		 * Der "Gesten umschalten" Button
		 */
		protected static ImageButton gestureButton;

		/**
		 * Der "Hilfestellungen anzeigen" Button
		 */
		protected static ImageButton assistancesButton;

		/**
		 * Der "Lesezeichen" Button des ActionTrees
		 */
		protected static Button bookmarkButton;

		/**
		 * Der "Schließen" Button des ActionTrees
		 */
		protected static Button closeButton;

		/**
		 * Der Button zum wechseln der Symbolsätze (HEX)
		 */
		protected static Button switchSymbols;
	}

	/**
	 * {@inheritDoc}
	 */
	public void onHoverTreeElement(ActionTreeElement ate) {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onLoadState(ActionTreeElement ate) {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onRedo() {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onUndo() {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNoteAdd(Field field, int value) {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNoteDelete(Field field, int value) {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onAddEntry(Field field, int value) {
		updateButtons();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDeleteEntry(Field field) {
		updateButtons();
	}

	/**
	 * Aktualisiert alle Buttons, also den Redo, Undo und ActionTree-Button,
	 * sowie die Tastatur
	 */
	private void updateButtons() {
		Buttons.redoButton.setEnabled(game.getStateHandler().canRedo() && !actionTreeShown);
		Buttons.undoButton.setEnabled(game.getStateHandler().canUndo() && !actionTreeShown);
		Buttons.actionTreeButton.setEnabled(!actionTreeShown);
		Buttons.assistancesButton.setEnabled(!actionTreeShown && !finished);
		Buttons.gestureButton.setEnabled(!actionTreeShown);
		this.mediator.setKeyboardState(!finished && this.sudokuView.getCurrentFieldView() != null);
	}

	/**
	 * JUST FOR TESTING PURPOSE!
	 * 
	 * @return Das SudokuLayout des aktuellen Spiels
	 */
	public SudokuLayout getSudokuLayout() {
		return sudokuView;
	}
}
