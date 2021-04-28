/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.files.FileManager
import de.sudoq.model.solverGenerator.Generator
import de.sudoq.model.solverGenerator.GeneratorCallback
import de.sudoq.model.solverGenerator.solution.Solution
import de.sudoq.model.solverGenerator.transformations.Transformer
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.SudokuXmlHandler

/**
 * Ist fuer die Verwaltung der vorhandenen Sudokus zuständig. Setzt das
 * Singleton Pattern um.
 */
open class SudokuManager : GeneratorCallback {
    private val generator = Generator()

    /**
     * speicher für das alte Sudoku während ein neues generiert wird
     */
    private var used: Sudoku? = null

    /**
     * Das Callback fuer den Generator
     */
    override fun generationFinished(sudoku: Sudoku) {
        SudokuXmlHandler().saveAsXml(sudoku)
        FileManager.deleteSudoku(used)
    }

    override fun generationFinished(sudoku: Sudoku, sl: List<Solution>) {
        SudokuXmlHandler().saveAsXml(sudoku)
        FileManager.deleteSudoku(used)
    }

    /**
     * Markiert ein Sudoku als benutzt. Falls möglich wird es transformiert,
     * andernfalls gelöscht und ein neues generiert.
     *
     * @param sudoku
     * das genutzte Sudoku
     */
    fun usedSudoku(sudoku: Sudoku) {
        if (sudoku.transformCount >= 10) {
            used = sudoku
            generator.generate(sudoku.sudokuType!!.enumType, sudoku.complexity, this)
        } else {
            Transformer.transform(sudoku)
            SudokuXmlHandler().saveAsXml(sudoku)
        }
    }

    companion object {
        /**
         * Gibt ein neues Sudoku des gewünschten Typs und der gewünschten
         * Schwierigkeit zurück
         *
         * @param t Typ des Sudokus
         * @param c Schwierigkeit des Sudokus
         * @return das neue Sudoku
         */
        fun getNewSudoku(t: SudokuTypes?, c: Complexity?): Sudoku {
            val sudoku = emptySudokuToFillWithXml
            SudokuXmlHandler(t, c).createObjectFromXml(sudoku)
            return sudoku
        }

        /**
         * Erzeugt ein vollständig leeres Sudoku, welches noch gefüllt werden muss.
         * DO NOT USE THIS METHOD (if you are not from us)
         *
         * @return das neue Sudoku
         */
        val emptySudokuToFillWithXml: Sudoku
            get() = Sudoku()
    }
}