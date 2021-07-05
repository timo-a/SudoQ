/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.sudoku.SudokuBE
import de.sudoq.model.persistence.xml.sudoku.SudokuMapper
import de.sudoq.model.persistence.xml.sudoku.SudokuRepo
import de.sudoq.model.solverGenerator.Generator
import de.sudoq.model.solverGenerator.GeneratorCallback
import de.sudoq.model.solverGenerator.solution.Solution
import de.sudoq.model.solverGenerator.transformations.Transformer
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlHelper
import java.io.File

/** Responsible for maintaining existing Sudokus.
 * Implemented as Singleton. */
open class SudokuManager(val sudokuDir: File, val sudokuTypeRepo: IRepo<SudokuType>) : GeneratorCallback {

    private val generator = Generator(sudokuTypeRepo)

    /** holds the old sudoku while the new sudoku is being generated. */
    private var used: Sudoku? = null

    /**
     * Callback for the Generator
     */
    override fun generationFinished(sudoku: Sudoku) {
        val sudokuRepo = SudokuRepo(sudokuDir, sudoku, sudokuTypeRepo)
        val i = sudokuRepo.create().id
        val sudokuBE = SudokuMapper.toBE(sudoku)
        sudokuBE.id = i
        sudokuRepo.update(sudokuBE)
        used?.also { sudokuRepo.delete(SudokuMapper.toBE(it)) }
    }

    override fun generationFinished(sudoku: Sudoku, sl: List<Solution>) {
        val sudokuRepo = SudokuRepo(sudokuDir, sudoku, sudokuTypeRepo)
        val i = sudokuRepo.create().id
        val sudokuBE = SudokuMapper.toBE(sudoku)
        sudokuBE.id = i
        sudokuRepo.update(sudokuBE)
        used?.also { sudokuRepo.delete(SudokuMapper.toBE(it)) }
    }

    /**
     * Marks a Sudoku as used.
     * If possible it will be transformed, otherwise a new one is generated.
     *
     * @param sudoku the used Sudoku
     */
    fun usedSudoku(sudoku: Sudoku) {
        if (sudoku.transformCount >= 10) {
            used = sudoku
            generator.generate(sudoku.sudokuType!!.enumType, sudoku.complexity, this)
        } else {
            Transformer.transform(sudoku)
            val sudokuRepo = SudokuRepo(sudokuDir, sudoku, sudokuTypeRepo)
            sudokuRepo.update(SudokuMapper.toBE(sudoku))
        }
    }

    /**
     * Retrune a new [Sudoku] of the specified [type][SudokuTypes] and [Complexity]
     *
     * @param t [type][SudokuTypes] of the [Sudoku]
     * @param c [Complexity] of the [Sudoku]
     * @return the new [Sudoku]
     */
    fun getNewSudoku(t: SudokuTypes?, c: Complexity?): Sudoku {
        val sudokuRepo = SudokuRepo(sudokuDir, t!!, c!!, sudokuTypeRepo)
        val f = sudokuRepo.getRandomSudoku()!!

        val sudokuBE = SudokuBE()
        sudokuBE.fillFromXml(XmlHelper().loadXml(f)!!, sudokuTypeRepo)
        return SudokuMapper.fromBE(sudokuBE)
    }


    companion object {

        /**
         * Creates an empty sudoku that has to be filled.
         * @return empty Sudoku
         */
        @Deprecated("DO NOT USE THIS METHOD (if you are not from us)")
        internal val emptySudokuToFillWithXml: Sudoku
            get() = Sudoku()
    }
}