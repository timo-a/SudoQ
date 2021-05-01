/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.gesture.GestureOverlayView;
import android.gesture.GestureStore;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import de.sudoq.R;
import de.sudoq.controller.SudoqCompatActivity;
import de.sudoq.controller.menus.Utility;
import de.sudoq.controller.sudoku.board.CellViewPainter;
import de.sudoq.controller.sudoku.board.CellViewStates;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.Game;
import de.sudoq.model.game.GameManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.view.FullScrollLayout;
import de.sudoq.view.GestureInputOverlay;
import de.sudoq.view.SudokuCellView;
import de.sudoq.view.SudokuLayout;
import de.sudoq.view.VirtualKeyboardLayout;

/**
 * Diese Klasse stellt die Activity des Sudokuspiels dar. Die Klasse hält das
 * Game und mehrere Controller um auf Interaktionen des Benutzers mit dem
 * Spielfeld zu reagieren. Die Klasse wird außerdem benutzt um zu verwalten,
 * welche Navigationselemente dem Nutzer angezeigt werden.
 */
public class SudokuActivity extends SudoqCompatActivity implements OnClickListener, ActionListener, ActionTreeNavListener {

	/** Attributes */

	/**
	 * Der Log-TAG
	 */
	private static final String LOG_TAG = SudokuActivity.class.getSimpleName();

	/**
	 * Konstante für das Speichern der Game ID
	 */
	private static final int SAVE_GAME_ID = 0;//TODO make enum

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
	private GestureInputOverlay gestureOverlay;

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

	private ControlPanelFragment controlPanel;

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
				this.game = GameManager.Companion.getInstance().load(savedInstanceState.getInt(SAVE_GAME_ID + ""));
			} catch (Exception e) {
				this.finish();
			}
		} else {
			this.game = GameManager.Companion.getInstance().load(Profile.getInstance().getCurrentGame());
		}

		if (game != null) {


			/* Determine how many numbers are needed. 1-9 or 1-16 ? */
			initializeSymbolSet();

            setContentView(game.isLefthandedModeActive() ? R.layout.sudoku_for_lefties
					                                     : R.layout.sudoku);


            Toolbar toolbar = findViewById(R.id.toolbar);//TODO subclass and put time, ... in it
            setSupportActionBar(toolbar);


            this.sudokuController = new SudokuController(this.game, this);
			this.actionTreeController = new ActionTreeController(this);
			Log.d(LOG_TAG, "Initialized");

			inflateViewAndButtons();

			Log.d(LOG_TAG, "Inflated view and control_panel");
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
			VirtualKeyboardLayout keyboardView = findViewById(R.id.virtual_keyboard);
			this.mediator = new UserInteractionMediator(keyboardView, this.sudokuView, this.game, this.gestureOverlay, this.gestureStore);
			this.mediator.registerListener(this.sudokuController);
			this.mediator.registerListener(this);
			if (this.game.isFinished()) {
				setFinished(false, false);
			}

			setTypeText();
			updateButtons();
			controlPanel.getGestureButton().setSelected(Profile.getInstance().isGestureActive());
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
		super.onSaveInstanceState(outState);
		outState.putFloat(SAVE_ZOOM_FACTOR + "", this.sudokuScrollView.getZoomFactor());
		outState.putFloat(SAVE_SCROLL_X + "", this.sudokuScrollView.getScrollValueX() - this.sudokuView.getCurrentLeftMargin());
		outState.putFloat(SAVE_SCROLL_Y + "", this.sudokuScrollView.getScrollValueY() - this.sudokuView.getCurrentTopMargin());
		outState.putBoolean(SAVE_ACTION_TREE_SHOWN + "", this.actionTreeShown);
		outState.putInt(SAVE_GAME_ID + "", game.getId());
		outState.putBoolean(SAVE_GESTURE_ACTIVE + "", this.gestureOverlay != null && this.gestureOverlay.getVisibility() == View.VISIBLE);
		if (this.sudokuView.getCurrentCellView() != null) {
			outState.putInt(SAVE_FIELD_X + "", game.getSudoku().getPosition(this.sudokuView.getCurrentCellView().getCell().getId()).getX());
			outState.putInt(SAVE_FIELD_Y + "", game.getSudoku().getPosition(this.sudokuView.getCurrentCellView().getCell().getId()).getY());
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
			this.sudokuView.getSudokuCellView(Position.get(state.getInt(SAVE_FIELD_X + "")
			                                               ,state.getInt(SAVE_FIELD_Y + ""))).onTouchEvent(null);
		}

		if (state.getBoolean(SAVE_GESTURE_ACTIVE + "")) {
			this.mediator.onCellSelected(this.sudokuView.getCurrentCellView(), CellInteractionListener.SelectEvent.Short);
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

		this.gestureOverlay = new GestureInputOverlay(this);

		FrameLayout frameLayout = findViewById(R.id.sudoku_frame_layout);
		frameLayout.addView(this.gestureOverlay);
	}

	/**
	 * Erstellt die Views und Buttons für diese Activity
	 */
	private void inflateViewAndButtons() {

		this.sudokuScrollView = findViewById(R.id.sudoku_cell);
		this.sudokuView = new SudokuLayout(this);
		Log.d(LOG_TAG, "Inflated sudoku layout");
		this.sudokuView.setGravity(Gravity.CENTER);
		this.sudokuScrollView.addView(this.sudokuView);

		controlPanel = (ControlPanelFragment) getSupportFragmentManager().findFragmentById(R.id.controlPanelFragment);
		controlPanel.initialize();
		controlPanel.inflateButtons();


		LinearLayout currentControlsView;/* = (LinearLayout) findViewById(R.id.sudoku_time_border);
		FieldViewPainter.getInstance().setMarking(currentControlsView, FieldViewStates.CONTROLS);*/
		currentControlsView = findViewById(R.id.sudoku_border);
		CellViewPainter.getInstance().setMarking(currentControlsView, CellViewStates.SUDOKU);
		currentControlsView = findViewById(R.id.controls);
		CellViewPainter.getInstance().setMarking(currentControlsView, CellViewStates.KEYBOARD);
		VirtualKeyboardLayout keyboardView = findViewById(R.id.virtual_keyboard);
		CellViewPainter.getInstance().setMarking(keyboardView, CellViewStates.KEYBOARD);
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
		controlPanel.onClick(v);//TODO make directly
		updateButtons();
	}

	/**
	 * returns whether all Gestures are defined -> Gesture input possible
	 * */
	boolean checkGesture() {
		Set<String> gestures = this.gestureStore.getGestureEntries();
		boolean allGesturesSet = true;
		for (String s : Symbol.getInstance().getSymbolSet())
			if (!gestures.contains(s))
				allGesturesSet = false;

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
		GameManager.Companion.getInstance().save(this.game);

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
			GameManager.Companion.getInstance().save(this.game);
		}
		super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}



	public void setModeHint(){
		controlPanel.hide();
		findViewById(R.id.hintPanel).setVisibility(View.VISIBLE);
		mode=Mode.HintMode;
	}

	public void setModeRegular(){
		findViewById(R.id.hintPanel).setVisibility(View.GONE);
		controlPanel.show();
		mode=Mode.Regular;
	}

	public SudokuController getSudokuController(){
		return sudokuController;
	}

	FragmentManager fm = getSupportFragmentManager();

	/**
	 * Zeigt einen Dialog mit den verfügbaren Hilfestellungen an.
	 */
	void showAssistancesDialog() {

		DialogFragment ad = new AssistancesDialogFragment();
		ad.show(fm, "assistancesDialog");
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
	public SudokuCellView getCurrentCellView() {
		return this.sudokuView.getCurrentCellView();
	}

	/**
	 * Setzt die aktuelle FieldView auf die spezifizierte.
	 *
	 * @param cellView
	 *            Die als aktuell zu setzende FieldView
	 */
	public void setCurrentCellView(SudokuCellView cellView) {
		this.sudokuView.setCurrentCellView(cellView);
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
		if (this.sudokuView.getCurrentCellView() != null)
			this.sudokuView.getCurrentCellView().select(this.game.isAssistanceAvailable(Assistances.markRowColumn));

		VirtualKeyboardLayout keyView = findViewById(R.id.virtual_keyboard);
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

	protected boolean getFinished() {//TODO this.finished vs game.finished, which is what
		return finished;
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

		StringBuilder pattern = new StringBuilder();

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
		return  getString(R.string.dialog_won_statistics) + ":\n"
		      + "\n"
		      + getString(R.string.dialog_won_timeneeded) + ": " + getGameTimeString() + "\n"
		      + getString(R.string.dialog_won_score)       + ": " + game.getScore();
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
		private StringBuilder offset = new StringBuilder();
		public void run() {
			game.addTime(1);

			//getSupportActionBar().
			final TextView timeView    = findViewById (R.id.time);
			timeView.setTextColor(getResources().getColor(R.color.text1));


			/* for easy formatting, we display both: time and penalty on one element separated by \n
			 * this is not  perfect since we padd with whitespace and the font is not 'mono-style'(=not all letters same width)
			 * solution would be: create custom xml for action bar, but as of now I see no way how to make this easy.
			 * to save computing time we cache the offset*/
			String time     = getGameTimeString();
			String penealty = " (+ " + getAssistancesTimeString() + ")";
			int d = time.length() - penealty.length();
			while(offset.length() > Math.abs(d)){offset.setLength(offset.length() - 1);}
			while(offset.length() < Math.abs(d)){offset.append(' ');}
			if(d > 0)
				penealty = offset + penealty;

			if(d < 0)
				time = offset + time;

			timeView.setText(time + "\n" + penealty);

			timeHandler.postDelayed(this, 1000);
		}
	};


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
	public void onNoteAdd(Cell cell, int value) {
		onInputAction();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNoteDelete(Cell cell, int value) {
		onInputAction();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onAddEntry(Cell cell, int value) {
		onInputAction();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDeleteEntry(Cell cell) {
		onInputAction();
	}

	public void onInputAction(){
		updateButtons();
		saveActionTree();
	}


	private void updateButtons(){
		controlPanel.updateButtons();
	}

    /** saves the whole game, purpose: save the action tree so a spontaneous crash doesn't lose us actions record */
	private void saveActionTree() {
		GameManager.Companion.getInstance().save(this.game);
	}

	ActionTreeController getActionTreeController(){
		return actionTreeController;
	}

	public UserInteractionMediator getMediator(){
		return mediator;
	}

	ControlPanelFragment getPanel(){return controlPanel;}

	/**
	 * JUST FOR TESTING PURPOSE!
	 * 
	 * @return Das SudokuLayout des aktuellen Spiels
	 */
	public SudokuLayout getSudokuLayout() {
		return sudokuView;
	}
}
