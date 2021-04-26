/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.ObservableModelImpl
import java.util.*

/**
 * A Cell describes an atomic unit in a sudoku board. It holds information about the current value,
 * editierbility, notes and the correct solution. It extends OberservableModel so changes in value
 * und notes can be observed.
 */
class Cell(editable: Boolean, solution: Int, id: Int, numberOfValues: Int) : ObservableModelImpl<Cell?>(), Cloneable {

    /** A unique number identifying the cell in the scope of the sudoku */
    val id: Int

    /** The correct solution for this cell */
    val solution: Int

    /** The current value in this cell */
    // package scope to increase performance and bypass the notifications
    var currentVal: Int

    /** The editability of this cell; false for prefilled cell */
    val isEditable: Boolean

    /** The set notes in this cell; Symbol $n$ is represented by bit number n-1 being set */
    private var noticeFlags: BitSet

    /** The highest value this cell can take */
    private val maxValue: Int

    /**
     * Intantiates a new editable cell object.
     *
     * @param id id of this cell
     * @param numberOfValues the number of values this cell can take (e.g. 9 for regular sudoku)
     */
    constructor(id: Int, numberOfValues: Int) : this(true, EMPTYVAL, id, numberOfValues) {}

    /** The current value in this cell */
    var currentValue: Int
        get() = currentVal
        /**
         * Sets the current value of the cell to the specified and notifies listeners.
         * If the cell is not editable, the parameter will be ignored and nothing will change.
         *
         * @param value
         * The new value for this cell
         * @throws IllegalArgumentException
         * if `value < 0`
         */
        set(value) {
            setCurrentValueP(value, true)
        }

    /**
     * Sets the current value of the cell to the specified and notifies listeners if requested.
     * If the cell is not editable, the parameter will be ignored and listeners will not be
     * informed.
     *
     * @param value The new value for this cell
     * @param notify if true listeners will be notified of change
     * @throws IllegalArgumentException
     * if `value < 0`
     */
    fun setCurrentValue(value: Int, notify: Boolean) { //Todo refactor flag into name
        setCurrentValueP(value, notify)
    }

    private fun setCurrentValueP(value: Int, notify: Boolean) {
        if (isEditable) {
            require(!(value < 0 && value != EMPTYVAL || value > maxValue)) {
                ("maxValue is " + maxValue
                        + " parameter value is " + value)
            }
            currentVal = value
            if (notify) notifyListeners(this)
        }
    }

    /**
     * Clears the current value in this cell and notifies listeners.
     * If the cell is not editable nothing happens.
     */
    fun clearCurrentValue() {
        if (isEditable) {
            currentVal = EMPTYVAL
            notifyListeners(this)
        }
    }

    /**
     * Returns the number of symbols this cell can take.
     *
     * @return the number of symbols this cell can take
     */
    val numberOfValues: Int
        get() = maxValue + 1

    /**
     * Checks whether the cell is occupied with any solution
     *
     * @return true, if the current solution is not 'empty'
     */
    val isSolved: Boolean
        get() = currentVal != EMPTYVAL

    /**
     * Checks whether the cell is occupied with any solution
     *
     * @return true, if the current solution is 'empty'
     */
    val isNotSolved: Boolean
        get() = currentVal == EMPTYVAL

    /**
     * Checks whether no solution nor notice flags are set.
     * @return true iff no solution is filled in and no notes are set
     */
    val isCompletelyEmpty: Boolean
        get() = isNotSolved && noticeFlags.isEmpty

    /**
     * Returns whether the passed note is set.
     *
     * @param value
     * note value
     * @return true if the note is set, false otherwise
     */
    fun isNoteSet(value: Int): Boolean {
        return value >= 0 && noticeFlags[value]
    }

    /**
     * toggles the specified symbol as note. If the parameter is below 0, nothing happens.
     *
     * @param value
     * the note to toggle
     */
    fun toggleNote(value: Int) {
        if (value >= 0) {
            noticeFlags.flip(value)
            notifyListeners(this)
        }
    }

    /**
     * Checks if the cell is solved correctly, i.e. if the filled in value is correct.
     *
     * @return true, iff the cell is solved correctly
     */
    val isSolvedCorrect: Boolean
        get() = currentVal == solution && currentVal != EMPTYVAL

    /**
     * Checks that the cell is not solved wrong i.e. either solved correct or empty
     *
     * @return true, iff the cell is solved correctly or empty
     */
    val isNotWrong: Boolean
        get() = currentVal == solution || currentVal == EMPTYVAL

    /**
     * {@inheritDoc}
     */
    override fun equals(obj: Any?): Boolean {
        if (obj != null && obj is Cell) {
            return id == obj.id
                    && solution == obj.solution
                    && currentVal == obj.currentVal
                    && isEditable == obj.isEditable
                    && noticeFlags == obj.noticeFlags
        }
        return false
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        return currentVal.toString()
    }

    /**
     * creates another object with the same values.
     * the created object is a perfect clone: even the id attribute is cloned
     */
    public override fun clone(): Any {
        val clone = Cell(isEditable,
                solution,
                id,
                numberOfValues)
        clone.currentValue = currentVal
        clone.noticeFlags = noticeFlags.clone() as BitSet
        return clone
    }

    companion object {
        /** The value representing an empty cell */
        const val EMPTYVAL = -1
    }

    init {
        require(!(solution < 0 && solution != EMPTYVAL)) { "Solution has to be positive." }
        noticeFlags = BitSet()
        maxValue = numberOfValues - 1
        this.id = id
        isEditable = editable
        this.solution = solution
        currentVal = if (editable) EMPTYVAL else solution
    }
}