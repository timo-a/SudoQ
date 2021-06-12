package de.sudoq.model.persistence.xml.sudokuType

import de.sudoq.model.game.Game
import de.sudoq.model.persistence.xml.game.GameBE
import de.sudoq.model.sudoku.sudokuTypes.SudokuType

object SudokuTypeMapper {

    fun toBE(game: Game) : GameBE {
        return GameBE(game.id,
                game.time,
                game.assistancesCost,
                game.sudoku!!,
                game.stateHandler!!,
                game.gameSettings!!,
                game.isFinished())
    }
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
        TODO("Not yet implemented")
    }
    fun fromBE(gameBE: GameBE) : Game {
        TODO("Not yet implemented")
    }
}