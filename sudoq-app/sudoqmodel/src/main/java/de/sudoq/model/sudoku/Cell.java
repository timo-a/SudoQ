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
 * A Cell describes an atomic unit in a sudoku board. It holds information about the current value,
 * editierbility, notes and the correct solution. It extends OberservableModel so changes in value
 * und notes can be observed.
 */
public class Cell extends ObservableModelImpl<Cell> {
	/* Attributes */

	/**
	 * A unique number identifying the cell in the scope of the sudoku
	 */
	private final int id;

	/**
	 * The correct solution for this cell
	 * */
	private final int solution;

	/**
	 * The current value in this cell
	 */
	// package scope to increase performance and bypass the notifications
	int currentVal;

	/**
	 * The value representing an empty cell
	 */
	public static final int EMPTYVAL = -1;

	/**
	 * The editability of this cell; false for prefilled cell
	 */
	private final boolean editable;

	/**
	 * The set notes in this cell; Symbol $n$ is represented by bit number n-1 being set
	 */
	private BitSet noticeFlags;

	/**
	 * The highest value this cell can take
	 */
	private int maxValue;

	/* Constructors */

	/**
	 * Instantiates a new cell object.
	 * 
	 * @param editable
	 *            specifies whether the value is mutable (true) of immutable, i.e. prefilled
	 * @param solution
	 *            the correct value for this cell
	 * @param id
	 *            the id of this cell
	 * @param numberOfValues
	 *            the number of values this cell can take (e.g. 9 for regular sudoku)
	 */
	public Cell(boolean editable, int solution, int id, int numberOfValues) {
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
	 * Intantiates a new editable cell object.
	 * 
	 * @param id
	 *            id of this cell
	 * @param numberOfValues
	 *            the number of values this cell can take (e.g. 9 for regular sudoku)
	 */
	public Cell(int id, int numberOfValues) {
		this(true, EMPTYVAL, id, numberOfValues);
	}

	/* Methods */

	/**
	 * Returns the id of this cell
	 *
	 * @return this cells id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the correct solution for this cell.
	 * 
	 * @return the correct solution for this cell.
	 */
	public int getSolution() {
		return solution;
	}

	/**
	 * Returns the current value in this cell.
	 * 
	 * @return the current value in this cell
	 */
	public int getCurrentValue() {
		return currentVal;
	}

	/**
	 * Sets the current value of the cell to the specified and notifies listeners.
	 * If the cell is not editable, the parameter will be ignored and nothing will change.
	 * 
	 * @param value
	 *            The new value for this cell
	 * @throws IllegalArgumentException
	 *             if {@code value < 0}
	 */
	public void setCurrentValue(int value) {
		setCurrentValueP(value, true);
	}

	/**
	 * Sets the current value of the cell to the specified and notifies listeners if requested.
	 * If the cell is not editable, the parameter will be ignored and listeners will not be
	 * informed.
	 *
	 * @param value
	 *            The new value for this cell
	 * @param notify
	 *            if true listeners will be notified of change
	 * @throws IllegalArgumentException
	 *             if {@code value < 0}
	 */
    public void setCurrentValue(int value, boolean notify) {//Todo refactor flag into name
		setCurrentValueP(value, notify);
	}


	private void setCurrentValueP(int value, boolean notify) {
		if (isEditable()) {
			if ((value < 0 && value != EMPTYVAL) || value > maxValue) {
				throw new IllegalArgumentException("maxValue is " + maxValue
				                                  + " parameter value is " + value);
			}

			this.currentVal = value;
			if (notify)
				notifyListeners(this);
		}
	}

	/**
	 * Clears the current value in this cell and notifies listeners.
	 * If the cell is not editable nothing happens.
	 */
	public void clearCurrentValue() {
		if (this.editable) {
			currentVal = EMPTYVAL;
			notifyListeners(this);
		}
	}

	/**
	 * Returns the number of symbols this cell can take.
	 * 
	 * @return the number of symbols this cell can take
	 */
	public int getNumberOfValues() {
		return this.maxValue + 1;
	}

	/**
	 * Checks whether the cell is occupied with any solution
	 *
	 * @return true, if the current solution is not 'empty'
	 */
	public boolean isSolved() {
		return this.currentVal != EMPTYVAL;
	}

	/**
	 * Checks whether the cell is occupied with any solution
	 *
	 * @return true, if the current solution is 'empty'
	 */
	public boolean isNotSolved() {
		return this.currentVal == EMPTYVAL;
	}

	/**
	 * Checks whether no solution nor notice flags are set.
	 * @return true iff no solution is filled in and no notes are set
	 */
	public boolean isCompletelyEmpty(){
		return isNotSolved() && noticeFlags.isEmpty();
	}

	/**
	 * Returns whether the passed note is set.
	 * 
	 * @param value
	 *            note value
	 * @return true if the note is set, false otherwise
	 */
	public boolean isNoteSet(int value) {
		return value >= 0 && noticeFlags.get(value);
	}

	/**
	 * toggles the specified symbol as note. If the parameter is below 0, nothing happens.
	 * 
	 * @param value
	 *            the note to toggle
	 */
	public void toggleNote(int value) {
		if (value >= 0) {
			noticeFlags.flip(value);
			notifyListeners(this);
		}
	}

	/**
	 * Checks if the cell is editable, i.e. its value is mutable.
	 * 
	 * @return true if cell is editable. false otherwise
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Checks if the cell is solved correctly, i.e. if the filled in value is correct.
	 * 
	 * @return true, iff the cell is solved correctly
	 */
	public boolean isSolvedCorrect() {
		return currentVal == solution && currentVal != EMPTYVAL;
	}

	/**
	 * Checks that the cell is not solved wrong i.e. either solved correct or empty
	 * 
	 * @return true, iff the cell is solved correctly or empty
	 */
	public boolean isNotWrong() {
		return currentVal == solution || currentVal == EMPTYVAL;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Cell) {

			Cell other = (Cell) obj;

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
		Cell clone = new Cell(this.isEditable(),
			                    this.getSolution(),
			                    this.getId(),
			                    this.getNumberOfValues());
		clone.setCurrentValue(this.currentVal);
		clone.noticeFlags = (BitSet) this.noticeFlags.clone();
		return clone;
	}
}
