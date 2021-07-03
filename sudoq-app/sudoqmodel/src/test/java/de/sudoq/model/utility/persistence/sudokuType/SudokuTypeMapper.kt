package de.sudoq.model.utility.persistence.sudokuType

import de.sudoq.model.sudoku.sudokuTypes.SudokuType

object SudokuTypeMapper {

    fun toBE(sudokuType: SudokuType): SudokuTypeBE {
        return SudokuTypeBE(
            sudokuType.enumType!!,
            sudokuType.numberOfSymbols,
            sudokuType.standardAllocationFactor,
            sudokuType.size!!,
            sudokuType.blockSize,
            sudokuType.constraints,
            sudokuType.permutationProperties,
            sudokuType.helperList,
            sudokuType.ccb
        )
    }

    fun fromBE(sudokuTypeBE: SudokuTypeBE): SudokuType {
        return SudokuType(
            sudokuTypeBE.enumType!!,
            sudokuTypeBE.numberOfSymbols,
            sudokuTypeBE.standardAllocationFactor,
            sudokuTypeBE.size!!,
            sudokuTypeBE.blockSize,
            sudokuTypeBE.constraints,
            sudokuTypeBE.permutationProperties,
            sudokuTypeBE.helperList,
            sudokuTypeBE.ccb
        )
    }
}