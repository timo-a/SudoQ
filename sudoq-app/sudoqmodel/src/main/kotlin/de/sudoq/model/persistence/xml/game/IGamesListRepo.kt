package de.sudoq.model.persistence.xml.game

import de.sudoq.model.game.GameData

interface IGamesListRepo {

    fun load() : MutableList<GameData>

    fun fileExists(id: Int) : Boolean

    fun saveGamesFile(games: List<GameData>)
}