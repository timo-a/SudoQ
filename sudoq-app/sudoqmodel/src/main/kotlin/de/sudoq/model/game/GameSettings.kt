/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.util.EnumMap

/**
 * This class holds all settings concerning a [Game]:
 * - a set of [Assistances], i.e. their availability.
 * - additional options like lefthandmode, hints...
 */
open class GameSettings(
    assistances: EnumMap<Assistances, Boolean> = EnumMap(Assistances::class.java),
    var isLeftHandModeSet: Boolean = false,
    var isHelpersSet: Boolean = false,
    var isGesturesSet: Boolean = false,
    val wantedTypesList: ArrayList<SudokuTypes> = ArrayList(SudokuTypes.entries)
) {

    /**
     * An EnumMap representing available [Assistances]
     */
    private val assistances = assistances

    /**
     * Sets an assistance to true
     *
     * @param assistance The assistance to set
     */
    fun setAssistance(assistance: Assistances) {
        assistances[assistance] = true
    }

    /**
     * Sets an assistance as unavailable.
     *
     * @param assistance The assistance to set
     */
    fun clearAssistance(assistance: Assistances) {
        assistances[assistance] = false
    }

    /**
     * Checks if an assistance is set
     *
     * @param assistance [Assistances] to check
     * @return true, if assistance is set, false otherwise
     */
    open fun getAssistance(assistance: Assistances): Boolean {
        return assistances[assistance] ?: false
    }

    fun copy(): GameSettings = GameSettings(EnumMap<Assistances, Boolean>(assistances),
        isLeftHandModeSet, isHelpersSet, isGesturesSet, ArrayList(wantedTypesList))
}