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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.sudoq.R;
import de.sudoq.controller.sudoku.board.CellViewStates;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.view.SudokuCellView;
import de.sudoq.view.SudokuLayout;
import de.sudoq.view.VirtualKeyboardLayout;

/**
 * Ein Vermittler zwischen einem Sudoku und den verschiedenen
 * Eingabemöglichkeiten, also insbesondere Tastatur und Gesten-View.
 */
public class UserInteractionMediator implements OnGesturePerformedListener, InputListener, CellInteractionListener, ObservableActionCaster {

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
		SudokuCellView currentField = this.sudokuView.getCurrentCellView();
		for (ActionListener listener : actionListener) {
			if (this.noteMode) {
				if (currentField.getCell().isNoteSet(symbol)) {
					listener.onNoteDelete(currentField.getCell(), symbol);
					restrictCandidates();//because github issue #116 see below
					                     //in case we deleted a now impossible,
					                     // we immediately restrict so it cant be selected again
				} else {
					listener.onNoteAdd(currentField.getCell(), symbol);
				}
			} else {
				if (symbol == currentField.getCell().getCurrentValue()) {
					listener.onDeleteEntry(currentField.getCell());
				} else {
					listener.onAddEntry(currentField.getCell(), symbol);
				}
			}
		}

		updateKeyboard();
	}

	public void onCellSelected(SudokuCellView view) {
		SudokuCellView currentField = this.sudokuView.getCurrentCellView();
		/* select for the first time -> set a solution */
		if (currentField != view) {
			this.noteMode = Profile.getInstance().isGestureActive() && !game.isFinished();

			if (currentField != null)
				currentField.deselect(true);

			this.sudokuView.setCurrentCellView(view);
			currentField = view;
			if (currentField != null)
				currentField.setNoteState(this.noteMode);
			currentField.select(this.game.isAssistanceAvailable(Assistances.markRowColumn));
			if (currentField.getCell().isEditable() && !game.isFinished()) {
				restrictCandidates();
				this.virtualKeyboard.setActivated(true);
			} else {
				this.virtualKeyboard.setActivated(false);
			}
		/* second click */
		} else if (!game.isFinished()) {
			/* gestures are enabled -> set solution via touchy swypy*/
			if (Profile.getInstance().isGestureActive() && this.sudokuView.getCurrentCellView().getCell().isEditable()) {
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
		SudokuCellView currentField = this.sudokuView.getCurrentCellView();
		for (int i : this.game.getSudoku().getSudokuType().getSymbolIterator()) {
			CellViewStates state;
			if      (currentField != null && i == currentField.getCell().getCurrentValue() && !this.noteMode)
				state = CellViewStates.SELECTED_INPUT_BORDER;

			else if (currentField != null &&      currentField.getCell().isNoteSet(i)      && this.noteMode)
				state = CellViewStates.SELECTED_NOTE_BORDER;

			else
				state = CellViewStates.DEFAULT_BORDER;

			this.virtualKeyboard.markCell(i, state);
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

	public void onCellChanged(SudokuCellView view) {
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
					if (prediction.name.equals(String.valueOf(Symbol.getInstance().getMapping(this.sudokuView.getCurrentCellView().getCell().getCurrentValue())))) {
						listener.onDeleteEntry(this.sudokuView.getCurrentCellView().getCell());
					} else {
						int number = Symbol.getInstance().getAbstract(prediction.name);
						int save = this.sudokuView.getCurrentCellView().getCell().getCurrentValue();
						if (number >= this.game.getSudoku().getSudokuType().getNumberOfSymbols())
							number = -1;
						if (number != -1 && this.game.isAssistanceAvailable(Assistances.restrictCandidates)) {
							this.sudokuView.getCurrentCellView().getCell().setCurrentValue(number, false);
							for (Constraint c : this.game.getSudoku().getSudokuType()) {
								if (!c.isSaturated(this.game.getSudoku())) {
									number = -2;
									break;
								}
							}
							this.sudokuView.getCurrentCellView().getCell().setCurrentValue(save, false);
						}
						if (number != -1 && number != -2) {
							listener.onAddEntry(this.sudokuView.getCurrentCellView().getCell(), number);
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
	void restrictCandidates() {
		this.virtualKeyboard.enableAllButtons();
		SudokuCellView currectFieldView = this.sudokuView.getCurrentCellView();
		if (currectFieldView == null)
			return;//maybe there is no focus, then pass

		Cell currentCell = currectFieldView.getCell();
		SudokuType type = this.game.getSudoku().getSudokuType();
		/* only if assistance 'input assistance' if enabled */
		if (this.game.isAssistanceAvailable(Assistances.restrictCandidates)) {

			Set<Integer> allPossible = getRestrictedSymbolSet(this.game.getSudoku(), currentCell, noteMode);

			for (int i : type.getSymbolIterator())
				if (!allPossible.contains(i))
					this.virtualKeyboard.disableButton(i);

		}
	}


    /* compute the symbols that the keyboard offers if `input assistance`
          i.e. "grey out values that apprear in the same constraint" is selected.
       caution
          */
	private synchronized Set<Integer> getRestrictedSymbolSet(Sudoku s, Cell currentCell,
																      boolean noteMode){
		Set<Integer> restrictedSet = new HashSet<>();
        SudokuType type = s.getSudokuType();
		List<Constraint> relevantConstraints = new ArrayList<>();
		for (Constraint c : type)
			if(c.getPositions().contains(s.getPosition(currentCell.getId())))
				relevantConstraints.add(c);

		/* save val of current view */
		int save = currentCell.getCurrentValue();

		/* iterate over all symbols e.g. 0-8 */
		for (int i : type.getSymbolIterator()) {

			/* set fieldval to current symbol */
			currentCell.setCurrentValue(i, false);

			boolean possible = true;
			/* for every constraint */
			for (Constraint c : relevantConstraints) {

				/* if constraint not satisfied -> disable */
				if (!c.isSaturated(s)) {
					possible = false;
					break;
				}
			}

			if (possible)
				restrictedSet.add(i);

			currentCell.setCurrentValue(Cell.EMPTYVAL, false); // unneccessary
		}
		currentCell.setCurrentValue(save, false);

		/* Github Issue #116
		 * it would be stupid if we were in the mode where notes are set
		 * and would disable a now impossible note that had been set by user.
		 * Because then, it can't be unset by the user */
		Set<Integer> setNotes =	new HashSet<>();
		if (noteMode)
			for (int i : type.getSymbolIterator())
				if (currentCell.isNoteSet(i))
					setNotes.add(i);


		restrictedSet.addAll(setNotes);
		return restrictedSet;
	}

}
