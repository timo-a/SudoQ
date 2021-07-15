package de.sudoq.model.persistence.xml.game

import de.sudoq.model.game.GameData
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import java.io.File
import java.io.IOException

class GamesListRepo(private val gamesDir: File,
                    private val gamesFile: File) : IGamesListRepo {

    override fun load(): MutableList<GameData> {
        try {
            return XmlHelper()
                .loadXml(this.gamesFile)!!
                .map { GameData.fromXml(it) }
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
        games.map { it.toXmlTree() }.forEach { xmlTree.addChild(it) }
        try {
            XmlHelper().saveXml(xmlTree, gamesFile)
        } catch (e: IOException) {
            throw IllegalStateException("Profile broken", e)
        }
    }

}