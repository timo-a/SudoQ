package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeMapper
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeRepo
import java.io.File

object SudokuTypeProvider {

    /**
     * Returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type an exception will be thrown.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    @JvmStatic
    fun getSudokuType(type: SudokuTypes, sudokuDir: File): SudokuType {
        val repo = SudokuTypeRepo(sudokuDir)
        val stBE = repo.read(type.ordinal)
        return SudokuTypeMapper.fromBE(stBE)
    }
}