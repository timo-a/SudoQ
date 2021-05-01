/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.solver.Solver
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuBuilder
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.util.*

/**
 * Diese Klasse stellt verschiedene Methoden zum Erstellen eines validen, neuen
 * Sudokus zur Verfügung. Dazu gibt es sowohl die Möglichkeit ein gänzlich neues
 * Sudoku mit einer spezifizierten Schwierigkeit erzeugen zu lassen, als auch
 * ein vorhandenes Sudoku durch Transformationen in ein Äquivalentes überführen
 * zu lassen.
 *
 * @see Sudoku
 *
 * @see Solver
 */
class Generator {
    /** Attributes  */
    private var random: Random
    /* Methods */
    /**
     * creates a sudoku of type @param{type} and difficulty @param{complexity} and appends it
     * (together with the callback object) to the queue of sudokus being generated.
     * If the queue is empty and there is no sudoku being generated at the moment,
     * generation of the new sudoku beginns immediately.
     * Otherwise it begins after all sudokus in the queue are generated.
     *
     * Ist das spezifizierte SudokuType-Objekt oder das GeneratorCallback-Objekt
     * null, oder hat das Complexity-Argument einen ungültigen Wert, so wird
     * false zurückgegeben. Ansonsten ist der Rückgabewert true.
     *
     * @param type
     * Der SudokuTypes-Enum Wert, aus welchem ein Sudoku erstellt und
     * generiert werden soll
     * @param complexity
     * Die Komplexität des zu erstellenden Sudokus
     * @param callbackObject
     * Das Objekt, dessen Callback-Methode aufgerufen werden soll,
     * sobald der Generator fertig ist
     * @return true, falls ein leeres Sudoku erzeugt und der Warteschlange
     * hinzugefügt werden konnte, false andernfalls
     */
    fun generate(type: SudokuTypes?, complexity: Complexity?, callbackObject: GeneratorCallback?): Boolean {
        if (type == null || complexity == null || callbackObject == null) return false

        // Create sudoku
        val sudoku = SudokuBuilder(type).createSudoku()
        sudoku.complexity = complexity
        val t = Thread(GenerationAlgo(sudoku, callbackObject, random))
        t.start()

        // Initiate new random object
        random = Random()
        return true
    }

    /**
     * NUR ZU DEBUG-ZWECKEN: Setzt das Random-Objekt dieses Sudokus, um einen
     * reproduzierbaren, deterministischen Ablauf des Generator zu provozieren.
     * Das Random-Objekt muss vor jedem Aufruf der generate-Methode neu gesetzt
     * werden.
     *
     * @param rnd
     * Das zu setzende random Objekt.
     */
    fun setRandom(rnd: Random) {
        random = rnd
    }

    companion object {
        /**
         * returns all positions of non-null Fields of sudoku
         * @param sudoku a sudoku object
         *
         * @return list of positions whose corresponding `Field` objects are not null
         */
        fun getPositions(sudoku: Sudoku): List<Position> {
            val p: MutableList<Position> = ArrayList()
            for (x in 0 until sudoku.sudokuType!!.size!!.x) for (y in 0 until sudoku.sudokuType!!.size!!.y) if (sudoku.getCell(Position[x, y]) != null) p.add(Position[x, y])
            return p
        }
    }
    /* Constructors */ /**
     * Initiiert ein neues Generator-Objekt.
     */
    init {
        random = Random(0)
    } //Todo remove 0 again
}