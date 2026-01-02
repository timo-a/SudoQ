package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.sudokuType.SudokuTypeRepo
import java.io.File

class SudokuTypeRepo4Tests: SudokuTypeRepo {

    constructor(): super(constructFile())

    companion object {
        private fun getFromResourceDirectory(relativePath: String): File {
            val classLoader = SudokuTypeRepo4Tests::class.java.classLoader
            return File(classLoader.getResource(relativePath)!!.file)
        }

        private fun constructFile(): File {
            return getFromResourceDirectory("persistence/SudokuTypes")
        }

    }
}