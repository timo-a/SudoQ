package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.sudokuType.SudokuTypeRepo
import java.io.File

class SudokuTypeRepo2(private val sudokuTypesDir: File): SudokuTypeRepo(sudokuTypesDir) {

    override fun getSudokuTypeFile(type: SudokuTypes): File {
        val path = "${sudokuTypesDir.absolutePath}${File.separator}$type.xml"
        val classLoader = javaClass.classLoader
        return File(classLoader!!.getResource(path).file)
    }
}