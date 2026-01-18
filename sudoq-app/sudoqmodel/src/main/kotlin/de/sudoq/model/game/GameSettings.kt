/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

/**
 * This class holds all settings concerning a [Game]:
 * - a set of [Assistances], i.e. their availability.
 * - additional options like lefthandmode, hints...
 */
open class GameSettings(
    /**
     * A BitSet representing available [Assistances]
     * TODO make private again after GameSettingsMapper no longer needs it
     */
    @Deprecated(
        "would be private if not for GameSettingsMapper. " +
                "Not supposed to be used by others."
    ) val assistances: BitSet = BitSet(),
    var isLeftHandModeSet: Boolean = false,
    var isHelpersSet: Boolean = false,
    var isGesturesSet: Boolean = false,
    val wantedTypesList: ArrayList<SudokuTypes> = ArrayList(SudokuTypes.entries)
) {

    /**
     * Sets an assistance to true
     *
     * @param assistance The assistance to set
     */
    fun setAssistance(assistance: Assistances) {
        assistances.set(
            2.0.pow((assistance.ordinal + 1).toDouble()).toInt()
        ) //TODO that looks wrong... we can fix it here, but need to keep it in persistence
    }

    /**
     * Sets an assistance as unavailable.
     *
     * @param assistance The assistance to set
     */
    fun clearAssistance(assistance: Assistances) {
        assistances.clear(2.0.pow((assistance.ordinal + 1).toDouble()).toInt())
    }

    /**
     * Checks if an assistance is set
     *
     * @param assistance [Assistances] to check
     * @return true, if assistance is set, false otherwise
     */
    open fun getAssistance(assistance: Assistances): Boolean {
        return assistances[2.0.pow((assistance.ordinal + 1).toDouble()).toInt()]
    }

    fun copy(): GameSettings = GameSettings(assistances.clone() as BitSet, isLeftHandModeSet, isHelpersSet, isGesturesSet, ArrayList(wantedTypesList))

}