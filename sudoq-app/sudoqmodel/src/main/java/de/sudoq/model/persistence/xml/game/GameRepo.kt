package de.sudoq.model.persistence.xml.game

import de.sudoq.model.game.GameSettings
import de.sudoq.model.game.GameStateHandler
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.profile.ProfileRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import java.io.File
import java.io.IOException

/**
 * repo for the games of one specific profile
 */
class GameRepo(private val profilesDir: File, private val profileId: Int) : IRepo<GameBE> {


    private val gamesDir: File

    val gamesFile: File


    override fun create(): GameBE {
        val newGame = GameBE()
        newGame.id = getNextFreeGameId()
        newGame.gameSettings = GameSettings()
        newGame.time = 0
        newGame.stateHandler = GameStateHandler()

        val file = File(gamesDir, "game_${newGame.id}.xml")
        try {
            XmlHelper().saveXml(newGame.toXmlTree(), file)
        } catch (e: IOException) {
            throw IllegalStateException("Error saving game xml tree", e)
        }
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

    /**
     * Loescht falls existierend das Spiel mit der gegebenen id des aktuellen
     * Profils
     *
     * @param id
     * die id des zu loeschenden Spiels
     * @return ob es geloescht wurde.
     */
    fun deleteGame(id: Int, p: Profile?): Boolean {
        val game = getGameFile(id).delete()
        return game && getGameThumbnailFile(id, p).delete()
    }


    // Thumbnails

    // Thumbnails
    /**
     * Returns the .png File for thumbnail of the game with id gameID
     *
     * @param gameID
     * The ID of the game whos thumbnail is requested.
     *
     * @return The thumbnail File.
     */
    fun getGameThumbnailFile(gameID: Int, p: ProfileManager?): File {
        return File(gamesDir.toString() + File.separator + "game_" +
                gameID + ".png")
    }


    override fun read(id: Int): GameBE {
        TODO("Not yet implemented")
    }

    override fun update(t: GameBE): GameBE {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    init {
        val profileRepo = ProfileRepo(profilesDir)
        var pm : ProfileManager = ProfileManager()

        val profile = File(profilesDir, "profile_$profileId")
        this.gamesDir = File(profile, "games")
        this.gamesFile = File(profile, "games.xml")
    }

}