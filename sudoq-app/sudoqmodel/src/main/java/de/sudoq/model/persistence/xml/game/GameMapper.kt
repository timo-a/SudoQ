package de.sudoq.model.persistence.xml.game

import de.sudoq.model.game.Game

object GameMapper {

    fun toBE(game: Game) : GameBE {
        return GameBE(game.id,
                game.time,
                game.assistancesCost,
                game.sudoku!!,
                game.stateHandler!!,
                game.gameSettings!!,
                game.isFinished())
    }

    fun fromBE(gameBE: GameBE) : Game {
        return Game(gameBE.id,
                gameBE.time,
                gameBE.assistancesCost,
                gameBE.sudoku!!,
                gameBE.stateHandler!!,
                gameBE.gameSettings!!,
                gameBE.finished
        )
    }
}