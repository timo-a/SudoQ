/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

import java.util.BitSet;

import de.sudoq.model.ObservableModelImpl;

/**
 * Ein Field-Objekt beschreibt ein Feld eines Sudoku-Spielfeldes. Dabei enthält es Informationen über aktuellen Wert,
 * Editierbarkeit, Notizen, sowie die korrekte Lösung für das Feld und stellt diese zur Verfügung. Außerdem ist es ein
 * OberservableModel und kann damit auf Veränderungen hinsichtlich aktuellem Wert und Notizen beobachtet werden.
 */
public class Field extends ObservableModelImpl<Field> {
	/** Attributes */

	/**
	 * Eine Sudokuweit eindeutige Nummer für dieses Feld
	 */
	private final int id;

	/**
	 * Die korrekte Lösung für dieses Feld
	 */
	private final int solution;

	/**
	 * Der aktuell in diesem Feld eingetragene Wert
	 */
	// package scope to increase performance and bypass the notifications
	int currentVal;

	/**
	 * Der Wert der ein leeres Feld repräsentiert
	 */
	public static final int EMPTYVAL = -1;

	/**
	 * Die Editierbarkeit dieses Feldes; vorgegebene Felder haben den Wert false
	 */
	private final boolean editable;

	/**
	 * Die gesetzten Notizen in diesem Feld; ist das n-te Bit 1, so ist das n-te Symbol als Notiz gesetzt
	 */
	private BitSet noticeFlags;

	/**
	 * Der höchste Wert, den dieses Feld annehmen kann
	 */
	private int maxValue;

	/** Constructors */

	/**
	 * Instanziiert ein neues Field-Objekt. Mit den Parametern wird spezifiziert, ob dieses Feld vom Spieler änderbar
	 * sein soll oder nicht und was die korrekte Lösung für dieses Feld ist.
	 * 
	 * @param editable
	 *            Gibt an, ob das Feld vom Spieler editierbar sein soll
	 * @param solution
	 *            Die Richtige Belegung für dieses Feld
	 * @param id
	 *            die id dieses Feldes
	 * @param numberOfValues
	 *            Die Anzahl von Werten, die dieses Feld annehmen kann
	 */
	public Field(boolean editable, int solution, int id, int numberOfValues) {
		if (solution < 0 && solution != EMPTYVAL)
			throw new IllegalArgumentException("Solution has to be positive.");

		noticeFlags = new BitSet();

		this.maxValue = numberOfValues - 1;
		this.id = id;
		this.editable = editable;
		this.solution = solution;

		currentVal = editable ? EMPTYVAL : solution;

	}

	/**
	 * Instanziiert ein neues editierbares Field-Objekt.
	 * 
	 * @param id
	 *            die id dieses Feldes
	 * @param numberOfValues
	 *            Die Anzahl an Werten, die dieses Feld annehmen kann
	 */
	public Field(int id, int numberOfValues) {
		this(true, EMPTYVAL, id, numberOfValues);
	}

	/** Methods */

	/**
	 * Gibt die id dieses Feldes zurück
	 * 
	 * @return die id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gibt die korrekte Lösung für dieses Feld zurück.
	 * 
	 * @return Die Korrekte Lösung für dieses Feld
	 */
	public int getSolution() {
		return solution;
	}

	/**
	 * Gibt den aktuell in diesem Feld eingetragenen Wert zurück.
	 * 
	 * @return Der aktuell in diesem Feld eingetragene Wert
	 */
	public int getCurrentValue() {
		return currentVal;
	}

	/**
	 * Setzt den aktuellen Wert dieses Feldes auf den Spezifizierten. Falls editable false ist passiert nichts.
	 * 
	 * @param value
	 *            Der in dieses Feld als aktuell einzutragende Wert
	 * @throws IllegalArgumentException
	 *             Falls value < 0
	 */
	public void setCurrentValue(int value) {
		if (isEditable()) {
			if ((value < 0 && value != EMPTYVAL) || value > maxValue) {
				throw new IllegalArgumentException("value is "+value);
			}

			this.currentVal = value;
			notifyListeners(this);
		}
	}

	/**
	 * Setzt den aktuellen Wert dieses Feldes auf den Spezifizierten. Falls editable false ist passiert nichts. Es
	 * werden hierbei keine Listener benachrichtigt.
	 * 
	 * @param value
	 *            Der in dieses Feld als aktuell einzutragende Wert
	 * @param notify
	 *            gibt an ob die Listener benachrichtigt werden sollen
	 * @throws IllegalArgumentException
	 *             Falls value < 0
	 */

	public void setCurrentValue(int value, boolean notify) {
		if (isEditable()) {
			if ((value < 0 && value != EMPTYVAL) || value > maxValue) {
				throw new IllegalArgumentException();
			}

			this.currentVal = value;
			if (notify)
				notifyListeners(this);
		}
	}

	/**
	 * Löscht den aktuellen Wert dieses Feldes. Falls das Feld nicht editierbar ist, so wird nichts getan.
	 */
	public void clearCurrentValue() {
		if (this.editable) {
			currentVal = EMPTYVAL;
			notifyListeners(this);
		}
	}

	/**
	 * Gibt die Anzahl an Werten zurück, die dieses Feld annehmen kann.
	 * 
	 * @return Die Anzahl an Werten, die dieses Feld annehmen kann
	 */
	public int getNumberOfValues() {
		return this.maxValue + 1;
	}

	/**
	 * Checks whether the field is occupied with any solution
	 *
	 * @return true, if the current solution is not 'empty'
	 */
	public boolean isSolved() {
		return this.currentVal != EMPTYVAL;
	}

	/**
	 * Checks whether the field is occupied with any solution
	 *
	 * @return true, if the current solution is 'empty'
	 */
	public boolean isNotSolved() {
		return this.currentVal == EMPTYVAL;
	}

	/**
	 * Checks wherther no solution nor notice flags are set.
	 * @return
	 */
	public boolean isCompletelyEmpty(){
		return isNotSolved() && noticeFlags.isEmpty();
	}

	/**
	 * Gibt zurück, ob die Notiz mit dem gegebenen Wert gesetzt ist.
	 * 
	 * @param value
	 *            der Wert der Notiz
	 * @return true falls sie gesetzt ist, andernfalls false
	 */
	public boolean isNoteSet(int value) {
		return value >= 0 && noticeFlags.get(value);
	}

	/**
	 * Schaltet das spezifizierte Symbol als Notiz um. Ist der Parameter value kleiner als 0, so wird nichts getan.
	 * 
	 * @param value
	 *            Die Nummer des Symbols, welches als Notiz umgeschaltet werden soll
	 */
	public void toggleNote(int value) {
		if (value >= 0) {
			noticeFlags.flip(value);
			notifyListeners(this);
		}
	}

	/**
	 * Überprüft, ob das Feld zur Belegung freigegeben ist.
	 * 
	 * @return true wenn das Feld belegt werden darf, anderenfalls false
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Überprüft, ob das Feld korrekt gelöst ist, also ob der aktuell eingetragene Wert der richtigen Lösung entspricht.
	 * 
	 * @return true, falls das Feld korrekt gelöst ist, sonst false
	 */
	public boolean isSolvedCorrect() {
		return currentVal == solution && currentVal != EMPTYVAL;
	}

	/**
	 * Überprüft ob das Feld nicht falsch belegt ist d.h. korrekt gelöst oder unbelegt ist
	 * 
	 * @return true, falls das Feld unbelegt oder korrekt gelöst ist
	 */
	public boolean isNotWrong() {
		return currentVal == solution || currentVal == EMPTYVAL;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Field) {

			Field other = (Field) obj;

			return this.id         == other.id         &&
                   this.solution   == other.solution   &&
                   this.currentVal == other.currentVal &&
                   this.editable   == other.editable   &&
                   this.noticeFlags.equals(
                                      other.noticeFlags);
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return String.valueOf(this.currentVal);
	}

	/**  creates another object with the same values.
	 *   the created object is a perfect clone: even the id attribute is cloned
	 *
	 */
	@Override
	public Object clone(){
		Field clone = new Field(this.isEditable(),
			                    this.getSolution(),
			                    this.getId(),
			                    this.getNumberOfValues());
		clone.setCurrentValue(this.currentVal);
		clone.noticeFlags = (BitSet) this.noticeFlags.clone();
		return clone;
	}
}
