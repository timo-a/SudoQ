/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import de.sudoq.controller.sudoku.FieldInteractionListener;
import de.sudoq.controller.sudoku.board.FieldViewPainter;
import de.sudoq.controller.sudoku.board.FieldViewStates;
import de.sudoq.controller.sudoku.ObservableFieldInteraction;
import de.sudoq.controller.sudoku.Symbol;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.game.Game;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 */
public class SudokuFieldView extends View implements ModelChangeListener<Field>, ObservableFieldInteraction {

	/** Attributes */

	private static final String LOG_TAG = SudokuFieldView.class.getSimpleName();

	/**
	 * Das Feld, das von diesem View representiert wird
	 * 
	 * @see Field
	 */
	private Field field;

	/**
	 * List der Selektions-Listener
	 */
	private ArrayList<FieldInteractionListener> fieldSelectListener;

	/**
	 * Ein Flag welches definiert, ob der Notizmodus eingeschaltet ist
	 */
	private boolean noteMode;

	/**
	 * Eine Liste von Feldern, die mit diesem in Verbindung stehen und markiert
	 * werden, falls dieses Feld ausgewählt wird
	 */
	private ArrayList<SudokuFieldView> connectedFields;

	/**
	 * Das Symbol was in diesem Feld steht
	 */
	private String symbol;

	/**
	 * Gibt an, ob dieses Feld zurzeit ausgewählt ist
	 */
	private boolean selected;

	/**
	 * Gibt an, ob dieses Feld mit dem zurzeit ausgewählten verbunden ist
	 */
	private boolean connected;

	/**
	 * Gibt an, ob dieses Feld in einem extraConstraint liegt
	 */
	private boolean isInExtraConstraint;

	/**
	 * Definiert, ob falsche Symbole markiert werden sollen
	 */
	private boolean markWrongSymbol;

	/**
	 * Das Game zu dem diese View gehört
	 */
	private Game game;

	/** Constructors */

	/**
	 * Erstellt einen SudokuFieldView und initialisiert die Attribute der
	 * Klasse.
	 * 
	 * @param context
	 *            der Applikationskontext
	 * @param game
	 *            Das Game zu dieser View
	 * @param field
	 *            Das Feld, dass dieser SudokuFieldView representiert
	 * @param markWrongSymbol
	 *            Gibt an, ob ein falsch eingegebenes Symbol markiert werden
	 *            eoll
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eines der Argumente null ist
	 */
	public SudokuFieldView(Context context, Game game, Field field, boolean markWrongSymbol) {
		super(context);
		this.markWrongSymbol = markWrongSymbol;
		this.field = field;
		this.symbol = Symbol.getInstance().getMapping(this.field.getCurrentValue());
		this.game = game;

		this.fieldSelectListener = new ArrayList<FieldInteractionListener>();
		this.connectedFields = new ArrayList<SudokuFieldView>();
		this.selected = false;
		this.connected = false;
		this.noteMode = false;
		this.isInExtraConstraint = false;

		ArrayList<Constraint> constraints = game.getSudoku().getSudokuType().getConstraints();
		for (Constraint c : constraints) {
			if (c.getType().equals(ConstraintType.EXTRA) &&
				c.includes(game.getSudoku().getPosition(field.getId()))) {
					this.isInExtraConstraint = true;
					break;
			}
		}

		updateMarking();
	}

	/** Methods */

	/**
	 * Zeichnet den Inhalt des Feldes auf das Canvas dieses SudokuFieldViews.
	 * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
	 * Canvas Objekt vorzunehmen.
	 * 
	 * @param canvas
	 *            Das Canvas Objekt auf das gezeichnet wird
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene Canvas null ist
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.d(LOG_TAG, "SudokuFieldView.onDraw()");

		this.symbol = Symbol.getInstance().getMapping(this.field.getCurrentValue());
		FieldViewPainter.getInstance().markField(canvas, this, this.symbol, false, this.isInExtraConstraint && !this.selected);

		// Draw notes if field has no value
		if (this.field.isNotSolved()) {
			drawNotes(canvas);
		}
	}

	/**
	 * Zeichnet die Notizen in dieses Feld
	 * 
	 * @param canvas
	 *            Das Canvas in das gezeichnet werde nsoll
	 */
	private void drawNotes(Canvas canvas) {
		Paint notePaint = new Paint();
		notePaint.setAntiAlias(true);
		int noteTextSize = getHeight() / Symbol.getInstance().getRasterSize();
		notePaint.setTextSize(noteTextSize);
		notePaint.setTextAlign(Paint.Align.CENTER);
		notePaint.setColor(Color.BLACK);
		for (int i = 0; i < Symbol.getInstance().getNumberOfSymbols(); i++) {
			if (this.field.isNoteSet(i)) {
				String note = Symbol.getInstance().getMapping(i);
				canvas.drawText(note + "",
						        (i % Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize / 2,
						        (i / Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize,
						        notePaint);
			}
		}
	}

	/**
	 * notifyListener(); {@inheritDoc}
	 */
	public void onModelChanged(Field obj) {
		for (FieldInteractionListener listener : fieldSelectListener) {
			listener.onFieldChanged(this);
		}

		updateMarking();
	}

	/**
	 * Diese Methode verarbeitet alle Touch Inputs, die der Benutzer macht und
	 * leitet sie an den ShowViewListener weiter.
	 * 
	 * @param touchEvent
	 *            Das TouchEvent das von der API kommt und diese Methode
	 *            aufgerufen hat
	 * @return true falls das TouchEvent behandelt wurde, false falls nicht
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene MotionEvent null ist
	 */
	@Override
	public boolean onTouchEvent(MotionEvent touchEvent) {
		for (FieldInteractionListener listener : fieldSelectListener) {
			listener.onFieldSelected(this);
		}

		return false;
	}

	
	

	/**
	 * Gibt das Feld, welches von dieser View angezeigt wird zurück
	 * 
	 * @return Das Feld dieser View
	 */
	public Field getField() {
		return this.field;
	}

	/**
	 * Setzt den Notizstatus gemäß des Parameters
	 * 
	 * @param state
	 *            true, um den Notizmodus ein-, bzw. false um ihn auszuschalten
	 */
	public void setNoteState(boolean state) {
		this.noteMode = state;
		this.updateMarking();
	}

	/**
	 * Fügt die übergebene SudokuFieldView als mit diesem Feld verbunden hinzu,
	 * sodass bei Markierung dieses Feldes auch das verbundene Feld markiert
	 * wird. Ist sie null, so wird nichts getan
	 * 
	 * @param view
	 *            Die View, die mit dieser verbunden werden soll
	 */
	public void addConnectedField(SudokuFieldView view) {
		if (view != null && !this.connectedFields.contains(view)) {
			this.connectedFields.add(view);
		}
	}

	/**
	 * Setzt diese View als aktuel ausgewählt.
	 * 
	 * @param markConnected
	 *            Gibt an, ob mit diesem Feld verbundene Felder (Zeile / Spalte)
	 *            auch markiert werden sollen
	 */
	public void select(boolean markConnected) {
		if (this.game.isFinished()) {
			this.connected = true;
		} else {
			this.selected = true;
		}
		if (markConnected) {
			for (SudokuFieldView f : this.connectedFields) {
				f.markConnected();
			}
		}

		this.updateMarking();
	}

	/**
	 * Setzt die Markierung für dieses und alle mit diesem Feld verbundenen
	 * FieldViews zurück.
	 * 
	 * @param updateConnected
	 *            Gibt an, ob mit diesem verbundenen Felder aktualisiert, also
	 *            auch abgewählt werden sollen
	 */
	public void deselect(boolean updateConnected) {
		this.selected = false;
		this.connected = false;
		if (updateConnected) {
			for (SudokuFieldView fv : this.connectedFields) {
				fv.deselect(false);
			}
		}

		this.updateMarking();
	}

	/**
	 * Makiert dieses Feld als mit dem aktuell selektierten verbunden.
	 */
	public void markConnected() {
		this.connected = true;
		updateMarking();
	}

	/**
	 * Aktualisiert die Markierungen dieser FieldView
	 */
	private void updateMarking() {
		boolean editable = this.field.isEditable();
		//TODO no idea what 'wrong' is doing, i just etracted it for clarity
		boolean wrong    = this.markWrongSymbol && !this.field.isNotWrong() && checkConstraint();
		FieldViewStates state;
		if (this.connected) 
			state = editable ?                  wrong ? FieldViewStates.CONNECTED_WRONG
			                                          : FieldViewStates.CONNECTED
			    
			                 : FieldViewStates.SELECTED_FIXED;
			
		else if (this.selected) 
			state = editable ? this.noteMode ?  wrong ? FieldViewStates.SELECTED_NOTE_WRONG
				                                      : FieldViewStates.SELECTED_NOTE
				                             :  wrong ? FieldViewStates.SELECTED_INPUT_WRONG
				                                      : FieldViewStates.SELECTED_INPUT
			                 : FieldViewStates.SELECTED_FIXED;
			
		else
			state = editable ?                  wrong ? FieldViewStates.DEFAULT_WRONG
			                                          : FieldViewStates.DEFAULT
			                 : FieldViewStates.FIXED;
		
		FieldViewPainter.getInstance().setMarking(this, state);
		invalidate();
	}

	/**
	 * Gibt true zurück, falls der Wert dieses Feldes die Constraints verletzt.
	 * Überprüft werden nur UniqueConstraints. Befindet sich das Feld in einem
	 * anderen ConstraintTyp wird immer fals ezurückgegeben.
	 * 
	 * @return true, falls der Wert dieses Feldes die UniqueConstraints verletzt
	 *         oder sich in einem anderen ConstraintTyp befindet, false sonst
	 */
	private boolean checkConstraint() {
		ArrayList<Constraint> constraints = this.game.getSudoku().getSudokuType().getConstraints();
		ArrayList<Position> positions;
		Sudoku sudoku = this.game.getSudoku();
		for (Constraint c : constraints) {
			if (c.includes(sudoku.getPosition(this.field.getId()))) {
				if (c.hasUniqueBehavior()) {
					for (Position pos : c.getPositions()) {
						if (pos != sudoku.getPosition(this.field.getId())
							&& sudoku.getField(pos).getCurrentValue() == this.field.getCurrentValue()) {
							return true;
						}
					}
				} else {
					return true;//if no unique-constraint -> automatically satisfied
				}
			}
		}

		return false;
	}

	/**
	 * Gibt zurück, ob sich das Feld gerade im Notizmodus befindet
	 * 
	 * @return true, falls sich das Feld im Notizmodus befindet, false falls
	 *         nicht
	 */
	public boolean isNoteMode() {
		return this.noteMode;
	}

	public void registerListener(FieldInteractionListener listener) {
		this.fieldSelectListener.add(listener);
	}

	public void removeListener(FieldInteractionListener listener) {

	}

	/**
	 * Benachrichtigt alle Registrierten Listener über Interaktion mit diesem
	 * SudokuFieldView.
	 */
	public void notifyListener() {
		for (FieldInteractionListener listener : fieldSelectListener) {
			listener.onFieldSelected(this);
		}
	}

}
