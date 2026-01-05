/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.sudoku.ISudokuRepoProvider
import de.sudoq.model.solverGenerator.Generator
import de.sudoq.model.solverGenerator.GeneratorCallback
import de.sudoq.model.solverGenerator.solution.Solution
import de.sudoq.model.solverGenerator.transformations.Transformer
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

/** Responsible for maintaining existing Sudokus.
 * Implemented as Singleton. */
open class SudokuManager(val sudokuTypeRepo: IRepo<SudokuType>,
                         private val sudokuRepoProvider: ISudokuRepoProvider) : GeneratorCallback {

    private val generator = Generator(sudokuTypeRepo)

    /** holds the old sudoku while the new sudoku is being generated. */
    private var used: Sudoku? = null

    /**
     * Callback for the Generator
     */
    override fun generationFinished(sudoku: Sudoku) {
        val sudokuRepo = sudokuRepoProvider.getRepo(sudoku)
        val i = sudokuRepo.create().id
        val sudokuWithId = Sudoku(i,
                                  sudoku.transformCount,
                                  sudoku.sudokuType,
                                  sudoku.complexity!!,
                                  sudoku.cells!!)
        sudokuRepo.update(sudokuWithId)
        used?.also { sudokuRepo.delete(it.id) }
    }

    override fun generationFinished(sudoku: Sudoku, sl: List<Solution>) {
        //todo is it ever used, if not safely remove/throw not implemented
        generationFinished(sudoku)
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
            generator.generate(sudoku.sudokuType.enumType, sudoku.complexity, this)
        } else {
            Transformer.transform(sudoku)
            val sudokuRepo = sudokuRepoProvider.getRepo(sudoku)
            sudokuRepo.update(sudoku)
        }
    }

    /**
     * Return a new [Sudoku] of the specified [type][SudokuTypes] and [Complexity]
     *
     * @param t [type][SudokuTypes] of the [Sudoku]
     * @param c [Complexity] of the [Sudoku]
     * @return the new [Sudoku]
     */
    fun getNewSudoku(t: SudokuTypes?, c: Complexity?): Sudoku {
        val sudokuRepo = sudokuRepoProvider.getRepo(t!!, c!!)
        val randomId = sudokuRepo.ids().random()
        return sudokuRepo.read(randomId)
    }
}