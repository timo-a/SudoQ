package de.sudoq.model.persistence.xml.game

import de.sudoq.model.game.GameSettings
import de.sudoq.model.game.GameStateHandler
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.profile.ProfileRepo
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.xml.XmlHelper
import java.io.File
import java.io.IOException

/**
 * repo for the games of one specific profile
 */
class GameRepo(profilesDir: File,
               profileId: Int,
               private val sudokuDir: File) : IRepo<GameBE> {


    private val gamesDir: File

    val gamesFile: File


    override fun create(): GameBE {
        val newGame = GameBE()
        newGame.id = getNextFreeGameId()
        newGame.gameSettings = GameSettings()
        newGame.time = 0
        newGame.stateHandler = GameStateHandler()

        //serialization cannot handle null value for sudoku yet
        // and I don't want to change it at this time
        /*val file = File(gamesDir, "game_${newGame.id}.xml")
        try {
            XmlHelper().saveXml(newGame.toXmlTree(), file)
        } catch (e: IOException) {
            throw IllegalStateException("Error saving game xml tree", e)
        }*/
        return newGame
    }

    /**
     * Gibt die naechste verfuegbare ID fuer ein Game zurueck
     *
     * @return naechste verfuegbare ID
     */
    fun getNextFreeGameId(): Int {
        return gamesDir.list().size + 1
    }


    /**
     * Gibt die XML eines Games des aktuellen Profils anhand seiner ID zurueck
     *
     * @param id
     * ID des Games
     * @return File, welcher auf die XML Datei des Games zeigt
     */
    fun getGameFile(id: Int): File {
        return File(gamesDir, "game_$id.xml")
    }

    override fun read(id: Int): GameBE {
        val obj = GameBE()
        val helper: XmlHelper = XmlHelper()
        val gameFile: File =getGameFile(id)
        //todo is exception catching necessary? profilerepo doesn't catch them
        try {
            obj.fillFromXml(helper.loadXml(gameFile)!!, sudokuDir)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when reading xml $gameFile", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Something went wrong when filling obj from xml ", e)
        }
        return obj
    }

    override fun update(g: GameBE): GameBE {
        val file = File(gamesDir, "game_${g.id}.xml")
        try {
            XmlHelper().saveXml(g.toXmlTree(), file)
        } catch (e: IOException) {
            throw IllegalStateException("Error saving game xml tree", e)
        }
        return read(g.id)
    }

    override fun delete(id: Int) {
        val game = getGameFile(id).delete()
        getGameThumbnailFile(id).delete()
    }
    // Thumbnails
    /**
     * Returns the .png File for thumbnail of the game with id gameID
     *
     * @param gameID
     * The ID of the game whos thumbnail is requested.
     *
     * @return The thumbnail File.
     */
    fun getGameThumbnailFile(gameID: Int): File {
        return File(gamesDir.toString() + File.separator + "game_" +
                gameID + ".png")
    }

    init {
        val profile = File(profilesDir, "profile_$profileId")
        this.gamesDir = File(profile, "games")
        this.gamesFile = File(profile, "games.xml")
    }

}