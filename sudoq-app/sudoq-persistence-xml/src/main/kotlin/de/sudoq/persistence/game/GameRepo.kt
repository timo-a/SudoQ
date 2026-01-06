package de.sudoq.persistence.game

import de.sudoq.model.game.Game
import de.sudoq.model.game.GameSettings
import de.sudoq.model.game.GameStateHandler
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import java.io.File
import java.io.IOException

/**
 * repo for the games of one specific profile
 */
class GameRepo(
    profilesDir: File,
    profileId: Int,
    private val sudokuTypeRepo: IRepo<SudokuType>
) : IRepo<Game> {


    private val gamesDir: File

    val gamesFile: File


    /**
     * do not use the generated game for anything other than obtaining its id!
     */
    override fun create(): Game {
        val id = getNextFreeGameId()
        val dummySudokuType = SudokuType(SudokuTypes.standard4x4, 9, 0f, Position[1,1],
            Position[1,1], ArrayList(), ArrayList(), ArrayList(), ComplexityConstraintBuilder()
        )
        val dummySudoku = Sudoku(-1, 0, dummySudokuType, Complexity.arbitrary, HashMap())
        return Game(id, dummySudoku)
    }

    private fun createBE(): GameBE {
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

    override fun read(id: Int): Game {
        val game = readBE(id)
        return GameMapper.fromBE(game)
    }

    private fun readBE(id: Int): GameBE {
        val obj = GameBE()
        val helper = XmlHelper()
        val gameFile: File = getGameFile(id)
        //todo is exception catching necessary? profilerepo doesn't catch them
        try {
            obj.fillFromXml(helper.loadXml(gameFile)!!, sudokuTypeRepo)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when reading xml $gameFile", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Something went wrong when filling obj from xml ", e)
        }
        return obj
    }

    override fun update(g: Game): Game {
        val gameBE = GameMapper.toBE(g)
        val gameLoaded = updateBE(gameBE)
        return GameMapper.fromBE(gameLoaded)
    }

    private fun updateBE(g: GameBE): GameBE {

        val file = File(gamesDir, "game_${g.id}.xml")
        try {
            XmlHelper().saveXml(g.toXmlTree(), file)
        } catch (e: IOException) {
            throw IllegalStateException("Error saving game xml tree", e)
        }
        return readBE(g.id)
    }

    override fun delete(id: Int) {
        getGameFile(id).delete()
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
        return File(
            gamesDir.toString() + File.separator + "game_" +
                    gameID + ".png"
        )
    }

    init {
        val profile = File(profilesDir, "profile_$profileId")
        this.gamesDir = File(profile, "games")
        this.gamesFile = File(profile, "games.xml")
    }

    override fun ids(): List<Int> {
        TODO("Not yet implemented")
    }

}