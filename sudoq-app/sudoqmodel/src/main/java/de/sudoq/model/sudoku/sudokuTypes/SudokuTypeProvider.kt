package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeMapper

object SudokuTypeProvider {

    /**
     * Returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type an exception will be thrown.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    @JvmStatic
    fun getSudokuType(type: SudokuTypes, sudokuTypeRepo: IRepo<SudokuTypeBE>): SudokuType {
        val stBE = sudokuTypeRepo.read(type.ordinal)
        return SudokuTypeMapper.fromBE(stBE)
    }
}