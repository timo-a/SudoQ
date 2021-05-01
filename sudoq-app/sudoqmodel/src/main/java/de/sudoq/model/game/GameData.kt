/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Eine Klasse um die zu einem Spiel zugehörigen Daten zu sammeln ohne jeweils
 * das ganze Spiel laden zu müssen.
 *
 * @param playedAt time when the game was last played
 * @property id ID of the game
 * @property type The type of the sudoku
 * @property isFinished  Indicates if the game was completed
 * @property complexity The complexit of the sudoku
 */
class GameData(val id: Int, playedAt: String, val isFinished: Boolean, val type: SudokuTypes, val complexity: Complexity) : Comparable<GameData> {

    companion object {
        const val dateFormat = "yyyy:MM:dd HH:mm:ss"
    }

    /**
     * Date when the game was last played
     */
    val playedAt: Date

    /**
     * @throws IllegalArgumentException if playedAt cannot be parsed
     */
    init {
        try {
            this.playedAt = SimpleDateFormat(dateFormat).parse(playedAt)
        } catch (e: ParseException) {
            throw IllegalArgumentException(e)
        }
    }


    /**
     * Sorts by finished < not yet finished and then by last played date
     */
    override fun compareTo(other: GameData): Int {
        return if (isFinished == other.isFinished) {
            playedAt.compareTo(other.playedAt)
        } else {
            if (isFinished) -1 else 1
        }
    }

}