/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.gesture.Prediction;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.sudoq.R;
import de.sudoq.controller.sudoku.board.FieldViewStates;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.view.SudokuFieldView;
import de.sudoq.view.SudokuLayout;
import de.sudoq.view.VirtualKeyboardLayout;

/**
 * Ein Vermittler zwischen einem Sudoku und den verschiedenen
 * Eingabemöglichkeiten, also insbesondere Tastatur und Gesten-View.
 */
public class UserInteractionMediator implements OnGesturePerformedListener, InputListener, FieldInteractionListener, ObservableActionCaster {

	/**
	 * Flag für den Notizmodus.
	 */
	private boolean noteMode;

	/**
	 * Die SudokuView, die die Anzeige eines Sudokus mit seinen Feldern
	 * übernimmt.
	 */
	private SudokuLayout sudokuView;

	/**
	 * Virtuelles Keyboard, welches beim Antippen eines Feldes angezeigt wird.
	 */
	private VirtualKeyboardLayout virtualKeyboard;

	/**
	 * Das aktuelle Spiel.
	 */
	private Game game;

	/**
	 * Eine Liste der ActionListener.
	 */
	private List<ActionListener> actionListener;

	/**
	 * Die Gesten-View.
	 */
	private GestureOverlayView gestureOverlay;

	/**
	 * Die Bibliothek für die Gesteneingabe.
	 */
	private GestureStore gestureStore;

	/**
	 * Instanziiert einen neuen UserInteractionMediator.
	 * 
	 * @param virtualKeyboard
	 *            Das virtuelle Keyboard, auf dem der Benutzer Eingaben
	 *            vornehmen kann
	 * @param sudokuView
	 *            Die View des Sudokus
	 * @param game
	 *            Das aktuelle Spiel
	 * @param gestureOverlay
	 *            Die Gesten-View auf der der Benutzer Gesten eingeben kann
	 * @param gestureStore
	 *            Die Bibliothek der Gesten
	 */
	public UserInteractionMediator(VirtualKeyboardLayout virtualKeyboard, SudokuLayout sudokuView, Game game, GestureOverlayView gestureOverlay,
			GestureStore gestureStore) {
		this.actionListener = new ArrayList<ActionListener>();

		this.game = game;
		this.sudokuView = sudokuView;
		this.virtualKeyboard = virtualKeyboard;
		this.virtualKeyboard.registerListener(this);
		this.gestureOverlay = gestureOverlay;
		this.gestureStore = gestureStore;
		this.gestureOverlay.addOnGesturePerformedListener(this);
		this.gestureOverlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE);
		this.sudokuView.registerListener(this);
	}

	public void onInput(int symbol) {
		SudokuFieldView currentField = this.sudokuView.getCurrentFieldView();
		for (ActionListener listener : actionListener) {
			if (this.noteMode) {
				if (currentField.getField().isNoteSet(symbol)) {
					listener.onNoteDelete(currentField.getField(), symbol);
					restrictCandidates();//because github issue #116 see below					
				} else {
					listener.onNoteAdd(currentField.getField(), symbol);
				}
			} else {
				if (symbol == currentField.getField().getCurrentValue()) {
					listener.onDeleteEntry(currentField.getField());
				} else {
					listener.onAddEntry(currentField.getField(), symbol);
				}
			}
		}

		updateKeyboard();
	}

	public void onFieldSelected(SudokuFieldView view) {
		SudokuFieldView currentField = this.sudokuView.getCurrentFieldView();
		/* select for the first time -> set a solution */
		if (currentField != view) {
			this.noteMode = Profile.getInstance().isGestureActive() && !game.isFinished();

			if (currentField != null)
				currentField.deselect(true);

			this.sudokuView.setCurrentFieldView(view);
			currentField = view;
			if (currentField != null)
				currentField.setNoteState(this.noteMode);
			currentField.select(this.game.isAssistanceAvailable(Assistances.markRowColumn));
			if (currentField.getField().isEditable() && !game.isFinished()) {
				restrictCandidates();
				this.virtualKeyboard.setActivated(true);
			} else {
				this.virtualKeyboard.setActivated(false);
			}
		/* second click */
		} else if (!game.isFinished()) {
			/* gestures are enabled -> set solution via touchy swypy*/
			if (Profile.getInstance().isGestureActive() && this.sudokuView.getCurrentFieldView().getField().isEditable()) {
				this.gestureOverlay.setVisibility(View.VISIBLE);
				restrictCandidates();
				final TextView textView = new TextView(gestureOverlay.getContext());
				textView.setTextColor(Color.YELLOW);
				textView.setText(" " + gestureOverlay.getContext().getString(R.string.sf_sudoku_title_gesture_input) + " ");
				textView.setTextSize(18);
				this.gestureOverlay.addView(textView, new GestureOverlayView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
			/* no gestures -> toogle noteMode*/	
			} else {
				this.noteMode = !this.noteMode;
				restrictCandidates();
			}
			currentField.setNoteState(this.noteMode);
		}

		updateKeyboard();
	}

	/**
	 * Aktualisiert die Anzeige der Tastatur.
	 */
	void updateKeyboard() {
		SudokuFieldView currentField = this.sudokuView.getCurrentFieldView();
		for (int i = 0; i < this.game.getSudoku().getSudokuType().getNumberOfSymbols(); i++) {
			FieldViewStates state;
			if      (currentField != null && i == currentField.getField().getCurrentValue() && !this.noteMode)
				state = FieldViewStates.SELECTED_INPUT_BORDER;

			else if (currentField != null &&      currentField.getField().isNoteSet(i)      && this.noteMode)
				state = FieldViewStates.SELECTED_NOTE_BORDER;

			else
				state = FieldViewStates.DEFAULT_BORDER;

			this.virtualKeyboard.markField(i, state);
		}

		this.virtualKeyboard.invalidate();
	}

	public void notifyListener() {

	}

	public void registerListener(ActionListener listener) {
		this.actionListener.add(listener);
	}

	public void removeListener(ActionListener listener) {
		this.actionListener.remove(listener);
	}

	/**
	 * Setzt den Zustand der Tastatur. Diese wird entsprechend (nicht)
	 * angezeigt.
	 * 
	 * @param activated
	 *            Gibt den zu setzenden Zustand an
	 */
	public void setKeyboardState(boolean activated) {
		this.virtualKeyboard.setActivated(activated);
	}

	public void onFieldChanged(SudokuFieldView view) {
		updateKeyboard();

	}

	/**
	 * Wird aufgerufen, sobald der Benutzer eine Geste eingibt.
	 * 
	 * @param overlay
	 *            GestureOverlay, auf welchem die Geste eingegeben wurde
	 * @param gesture
	 *            Geste, die der Benutzer eingegeben hat
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eines der Argumente null ist
	 */
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		ArrayList<Prediction> predictions = this.gestureStore.recognize(gesture);
		if (predictions.size() > 0) {
			Prediction prediction = predictions.get(0);
			if (prediction.score > 1.5) {
				for (ActionListener listener : this.actionListener) {
					if (prediction.name.equals(String.valueOf(Symbol.getInstance().getMapping(this.sudokuView.getCurrentFieldView().getField().getCurrentValue())))) {
						listener.onDeleteEntry(this.sudokuView.getCurrentFieldView().getField());
					} else {
						int number = Symbol.getInstance().getAbstract(prediction.name);
						int save = this.sudokuView.getCurrentFieldView().getField().getCurrentValue();
						if (number >= this.game.getSudoku().getSudokuType().getNumberOfSymbols())
							number = -1;
						if (number != -1 && this.game.isAssistanceAvailable(Assistances.restrictCandidates)) {
							this.sudokuView.getCurrentFieldView().getField().setCurrentValue(number, false);
							for (Constraint c : this.game.getSudoku().getSudokuType()) {
								if (!c.isSaturated(this.game.getSudoku())) {
									number = -2;
									break;
								}
							}
							this.sudokuView.getCurrentFieldView().getField().setCurrentValue(save, false);
						}
						if (number != -1 && number != -2) {
							listener.onAddEntry(this.sudokuView.getCurrentFieldView().getField(), number);
							this.gestureOverlay.setVisibility(View.INVISIBLE);
						} else if (number == -1) {
							Toast.makeText(this.sudokuView.getContext(),
									this.sudokuView.getContext().getString(R.string.toast_invalid_symbol), Toast.LENGTH_SHORT).show();
						} else if (number == -2) {
							Toast.makeText(this.sudokuView.getContext(),
									this.sudokuView.getContext().getString(R.string.toast_restricted_symbol), Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}
	}

	/**
	 * Schränkt die Kandidaten auf der Tastatur ein.
	 */
	private void restrictCandidates() {
		this.virtualKeyboard.enableAllButtons();
		
		Field currentField = this.sudokuView.getCurrentFieldView().getField();
		SudokuType type = this.game.getSudoku().getSudokuType();
		/* only if assistance 'input assistance' if enabled */
		if (this.game.isAssistanceAvailable(Assistances.restrictCandidates)) {
			
			/* save val of current view */
			int save = currentField.getCurrentValue();
			
			/* iterate over all symbols e.g. 0-8 */
			for (int i = 0; i < type.getNumberOfSymbols(); i++) {
				/* set fieldval to current symbol */
				currentField.setCurrentValue(i, false);
				/* for every constraint */
				for (Constraint c : type) {
					/* if constraint not satisfied -> disable*/
					boolean constraintViolated =! c.isSaturated(this.game.getSudoku());
					
					/* Github Issue #116
					 * it would be stupid if we were in the mode where notes are set 
					 * and would disable a note that has been set.
					 * Because then, it can't be unset by the user*/
					boolean noteNotSet = ! (noteMode && currentField.isNoteSet(i));
					
					if (constraintViolated && noteNotSet) {
						this.virtualKeyboard.disableButton(i);
						break;
					}
				}
				currentField.setCurrentValue(Field.EMPTYVAL, false);
			}
			currentField.setCurrentValue(save, false);
			
		}
	}

}
