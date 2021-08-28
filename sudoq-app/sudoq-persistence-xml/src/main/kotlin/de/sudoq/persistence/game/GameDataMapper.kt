package de.sudoq.persistence.game

import de.sudoq.model.game.GameData
import de.sudoq.persistence.game.GameDataBE
import java.text.ParseException
import java.text.SimpleDateFormat

object GameDataMapper {

    fun toBE(gameData: GameData): GameDataBE {
        return GameDataBE(
            gameData.id,
            SimpleDateFormat(GameDataBE.dateFormat).format(gameData.playedAt),
            gameData.isFinished,
            gameData.type,
            gameData.complexity
        )
    }
/*    try {
        this.playedAt = SimpleDateFormat(GameDataBE.dateFormat).parse(playedAt)
    } catch (e: ParseException) {
        throw IllegalArgumentException(e)
    }*/

    fun fromBE(gameDataBE: GameDataBE): GameData {
        return GameData(
            gameDataBE.id,
            try {
                SimpleDateFormat(GameData.dateFormat).parse(gameDataBE.playedAt)
            } catch (e: ParseException) {
                throw IllegalArgumentException(e)
            },
            gameDataBE.isFinished,
            gameDataBE.type,
            gameDataBE.complexity
        )
    }
}