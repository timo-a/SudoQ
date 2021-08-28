package de.sudoq.persistence.game

import de.sudoq.model.game.GameData
import de.sudoq.persistence.XmlHelper
import de.sudoq.persistence.XmlTree
import de.sudoq.model.persistence.xml.game.IGamesListRepo
import de.sudoq.persistence.game.GameDataBE
import de.sudoq.persistence.game.GameDataMapper
import java.io.File
import java.io.IOException

class GamesListRepo(private val gamesDir: File,
                    private val gamesFile: File) : IGamesListRepo {

    override fun load(): MutableList<GameData> {
        try {
            return XmlHelper()
                .loadXml(this.gamesFile)!!
                .map { GameDataBE.fromXml(it) }
                .map { GameDataMapper.fromBE(it) }
                .sortedDescending().toMutableList()
        } catch (e: IOException) {
            throw IllegalStateException("Profile broken", e)
        }
    }

    override fun fileExists(id: Int): Boolean = getGameFile(id).exists()

    //todo this should be in gameRepo, but then it would need its own interface...
    fun getGameFile(id: Int): File {
        return File(gamesDir, "game_$id.xml")
    }

    override fun saveGamesFile(games: List<GameData>) {
        val xmlTree = XmlTree("games")
        games.map { GameDataMapper.toBE(it) }
            .map { it.toXmlTree() }
            .forEach { xmlTree.addChild(it) }
        try {
            XmlHelper().saveXml(xmlTree, gamesFile)
        } catch (e: IOException) {
            throw IllegalStateException("Profile broken", e)
        }
    }

}