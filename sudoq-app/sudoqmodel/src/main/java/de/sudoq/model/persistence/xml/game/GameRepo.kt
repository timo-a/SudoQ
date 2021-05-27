package de.sudoq.model.persistence.xml.game

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.profile.ProfileRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.profile.ProfileManager
import java.io.File

class GameRepo(private val profilesDir: File, private val profileId: Int) : IRepo<GameBE> {

    override fun create(): GameBE {

        TODO("Not yet implemented")
    }

    /**
     * Gibt die naechste verfuegbare ID fuer ein Game zurueck
     *
     * @return naechste verfuegbare ID
     */
    fun getNextFreeGameId(p: Profile): Int {
        val gamesDir = getGamesDir(p)
        return gamesDir.list().size + 1
    }

    /**
     * Gibt das Game-Verzeichnis des aktuellen Profils zurueck
     *
     * @return File, welcher auf das Game-Verzeichnis des aktuellen Profils
     * zeigt
     */
    fun getGamesDir(p: ProfileManager): File {
        val currentProfile = p.currentProfileDir
        return File(currentProfile, "games")
    }

    /**
     * Gibt die XML eines Games des aktuellen Profils anhand seiner ID zurueck
     *
     * @param id
     * ID des Games
     * @return File, welcher auf die XML Datei des Games zeigt
     */
    fun getGameFile(id: Int, p: Profile?): File {
        return File(getGamesDir(p!!), "game_$id.xml")
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
        val game = getGameFile(id, p)!!.delete()
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
        return File(getGamesDir(p!!).toString() + File.separator + "game_" +
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
    }

}