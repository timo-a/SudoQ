 package de.sudoq.model.persistence.xml.sudokuType

import de.sudoq.model.game.Game
import de.sudoq.model.persistence.xml.game.GameBE
import de.sudoq.model.sudoku.sudokuTypes.SudokuType

object SudokuTypeMapper {

    fun toBE(sudokuType: SudokuType) : SudokuTypeBE {
        return SudokuTypeBE(sudokuType.enumType!!,
                sudokuType.numberOfSymbols,
                sudokuType.size!!,
                sudokuType.blockSize,
                sudokuType.constraints,
                sudokuType.permutationProperties,
                sudokuType.helperList,
                sudokuType.ccb)
    }

    fun fromBE(sudokuTypeBE: SudokuTypeBE) : SudokuType {
        return SudokuType(sudokuTypeBE.enumType!!,
                sudokuTypeBE.numberOfSymbols,
                sudokuTypeBE.size!!,
                sudokuTypeBE.blockSize,
                sudokuTypeBE.constraints,
                sudokuTypeBE.permutationProperties,
                sudokuTypeBE.helperList,
                sudokuTypeBE.ccb)
    }
}