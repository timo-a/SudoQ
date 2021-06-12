/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.persistence.xml.game.GameBE.Companion.ID
import de.sudoq.model.persistence.xml.game.GameBE.Companion.FINISHED
import de.sudoq.model.persistence.xml.game.GameBE.Companion.PLAYED_AT
import de.sudoq.model.persistence.xml.game.GameBE.Companion.SUDOKU_TYPE
import de.sudoq.model.persistence.xml.game.GameBE.Companion.COMPLEXITY
import de.sudoq.model.files.FileManager
import de.sudoq.model.persistence.xml.game.GameBE
import de.sudoq.model.persistence.xml.game.GameMapper
import de.sudoq.model.persistence.xml.game.GameRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.sudoku.SudokuManager
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Singleton for creating and loading sudoku games.
 */
class GameManager private constructor() {

    private lateinit var xmlHandler: XmlHandler2<Game>

    private lateinit var xmlHandlerBE: XmlHandler2<GameBE>

    private lateinit var profile: Profile

    private lateinit var gameRepo: GameRepo

    /**
     * Creates a new gam and sets up the necessary files.
     *
     * @param type The type of the [Sudoku]
     * @param complexity The complexity of the Sudoku
     * @param assistsances The available assistances for the game
     * @return The new [Game]
     *
     */
    fun newGame(type: SudokuTypes, complexity: Complexity, assistances: GameSettings, sudokuDir: File): Game {
        val sudoku = SudokuManager.getNewSudoku(type, complexity, sudokuDir)
        SudokuManager(sudokuDir).usedSudoku(sudoku) //TODO warum instanziierung, wenn laut doc singleton?

        val repo = GameRepo(
                profilesDir = profile.profilesDir!!,
                profileId = profile.currentProfileID)

        val game = Game(repo.getNextFreeGameId(profile), sudoku)
        game.setAssistances(assistances)


        val gameBE = GameMapper.toBE(game)
        xmlHandlerBE.saveAsXml(gameBE)
        val games = gamesXml
        val gameTree = XmlTree("game")
        gameTree.addAttribute(XmlAttribute(ID, gameBE.id.toString()))
        gameTree.addAttribute(XmlAttribute(SUDOKU_TYPE, gameBE.sudoku!!.sudokuType?.enumType!!.ordinal.toString()))
        gameTree.addAttribute(XmlAttribute(COMPLEXITY, gameBE.sudoku!!.complexity?.ordinal.toString()))
        gameTree.addAttribute(XmlAttribute(PLAYED_AT, SimpleDateFormat(GameData.dateFormat).format(Date())))
        games.addChild(gameTree)
        saveGamesFile(games)
        return game
    }

    /**
     * Loads an existing [Game] of the current player by id.
     *
     * @param id die Id des zu ladenden Spiels
     * @return Das geladene Spiel, null falls kein Spiel zur angegebenen id existiert
     * @throws IllegalArgumentException if there is no game with that id or if id is not positive.
     */
    fun load(id: Int, sudokuDir: File): Game {
        require(id > 0) { "invalid id" }
        val game = Game()
        // throws IllegalArgumentException
        GameXmlHandler(id, profile).createObjectFromXml(game, sudokuDir)
        return game
    }

    /**
     * A list data of all games of a player.
     * Sorted by unfinished first then by most recently played //TODO confirm with test
     *
     * @return the list
     */
    val gameList: List<GameData>
        get() {
            val list: MutableList<GameData> = ArrayList()
            for (game in gamesXml) {
                list.add(GameData(
                        game.getAttributeValue(ID)!!.toInt(),
                        game.getAttributeValue(PLAYED_AT)!!,
                        game.getAttributeValue(FINISHED).toBoolean(),
                        SudokuTypes.values()[game.getAttributeValue(SUDOKU_TYPE)!!.toInt()],
                        Complexity.values()[game.getAttributeValue(COMPLEXITY)!!.toInt()]
                )
                )
            }
            list.sort()
            list.reverse()
            return list
        }

    /**
     * Save a Game to XML.
     *
     * @param game [Game] to save
     */
    fun save(game: Game, profile: Profile) {
        xmlHandler.saveAsXml(game)
        val games = gamesXml

        for (g in games) {
            if (g.getAttributeValue(ID)!!.toInt() == game.id) {
                // TODO anpassen
                g.updateAttribute(XmlAttribute(PLAYED_AT, SimpleDateFormat(GameData.dateFormat).format(Date())))
                g.updateAttribute(XmlAttribute(FINISHED, (game.isFinished().toString())))
                break
            }
        }

        profile.saveChanges()
        saveGamesFile(games)
    }

    /**
     * Deletes no longer existing [Game]s from the list.
     *
     */
    fun updateGamesList() {
        val games = gamesXml
        val newGames = XmlTree(games.name)
        for (g in games) {
            if (gameRepo.getGameFile(g.getAttributeValue(ID)!!.toInt(), profile).exists()) {
                newGames.addChild(g)
            }
        }
        saveGamesFile(newGames)
    }

    /**
     * Deletes a [Game] by id from memory and the list.
     * If no game is found, nothing happens.
     *
     * @param id ID of the game to remove
     */
    fun deleteGame(id: Int, profile: Profile) {
        if (id == profile.currentGame) {
            profile.currentGame = Profile.NO_GAME
            profile.saveChanges() //save 'currentGameID' in xml (otherwise menu will offer 'continue')
        }
        gameRepo.deleteGame(id, profile)
        updateGamesList()
    }

    /**
     * Deletes all finished [Games] from storage and the current list
     */
    fun deleteFinishedGames() {
        val games = gamesXml
        for (g in games) {
            if (g.getAttributeValue(FINISHED).toBoolean()) {
                gameRepo.deleteGame(g.getAttributeValue(ID)!!.toInt(), profile)
            }
        }
        updateGamesList()
    }

    private fun saveGamesFile(games: XmlTree) {
        try {
            XmlHelper().saveXml(games, FileManager.getGamesFile(profile))
        } catch (e: IOException) {
            throw IllegalStateException("Profil broken", e)
        }
    }

    private val gamesXml: XmlTree
        get() = try {
            val gf = FileManager.getGamesFile(profile)
            XmlHelper().loadXml(gf)!!
        } catch (e: IOException) {
            throw IllegalStateException("Profile broken", e)
        }


    companion object {

        private var instance: GameManager? = null

        fun getInstance(f: File): GameManager {
            if (instance == null ) {
                instance = GameManager()
                val profile = Profile.getInstance(f)
                instance!!.profile = profile
                instance!!.xmlHandler = GameXmlHandler(p = profile)
                instance!!.xmlHandlerBE = GameBEXmlHandler(p = profile)
                instance!!.gameRepo = GameRepo(profile.profilesDir!!, profile.currentProfileID)
            }

            return instance!!
        }
    }

}