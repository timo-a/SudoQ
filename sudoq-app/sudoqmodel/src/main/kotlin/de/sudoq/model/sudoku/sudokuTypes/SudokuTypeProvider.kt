package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.ports.persistence.ReadRepo

object SudokuTypeProvider {

    /**
     * Returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type an exception will be thrown.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    @JvmStatic
    fun getSudokuType(type: SudokuTypes, sudokuTypeRepo: ReadRepo<SudokuType>): SudokuType {
        return sudokuTypeRepo.read(type.ordinal)
    }
}