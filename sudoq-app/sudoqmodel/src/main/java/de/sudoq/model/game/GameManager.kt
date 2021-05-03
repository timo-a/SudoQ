/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.files.FileManager
import de.sudoq.model.profile.Profile
import de.sudoq.model.sudoku.SudokuManager
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.*
import java.io.IOException
import java.lang.Boolean
import java.text.SimpleDateFormat
import java.util.*

/**
 * Singleton for creating and loading sudoku games.
 */
class GameManager private constructor() {

    private val xmlHandler: XmlHandler<Game>

    /**
     * Creates a new gam and sets up the necessary files.
     *
     * @param type The type of the [Sudoku]
     * @param complexity The complexity of the Sudoku
     * @param assistsances The available assistances for the game
     * @return The new [Game]
     *
     */
    fun newGame(type: SudokuTypes, complexity: Complexity, assistances: GameSettings): Game {
        val sudoku = SudokuManager.getNewSudoku(type, complexity)
        SudokuManager().usedSudoku(sudoku) //TODO warum instanziierung, wenn laut doc singleton?
        val game = Game(FileManager.getNextFreeGameId(), sudoku)
        game.setAssistances(assistances)
        xmlHandler.saveAsXml(game)
        val games = gamesXml
        val gameTree = XmlTree("game")
        gameTree.addAttribute(XmlAttribute(ID, game.id.toString()))
        gameTree.addAttribute(XmlAttribute(SUDOKU_TYPE, game.sudoku!!.sudokuType.enumType.ordinal.toString()))
        gameTree.addAttribute(XmlAttribute(COMPLEXITY, game.sudoku!!.complexity.ordinal.toString()))
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
    fun load(id: Int): Game {
        require(id > 0) { "invalid id" }
        val game = Game()
        // throws IllegalArgumentException
        GameXmlHandler(id).createObjectFromXml(game)
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
                        game.getAttributeValue(ID).toInt(),
                        game.getAttributeValue(PLAYED_AT),
                        Boolean.parseBoolean(game.getAttributeValue(FINISHED)),
                        SudokuTypes.values()[game.getAttributeValue(SUDOKU_TYPE).toInt()],
                        Complexity.values()[game.getAttributeValue(COMPLEXITY).toInt()]
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
    fun save(game: Game) {
        xmlHandler.saveAsXml(game)
        val games = gamesXml

        for (g in games) {
            if (g.getAttributeValue(ID).toInt() == game.id) {
                // TODO anpassen
                g.updateAttribute(XmlAttribute(PLAYED_AT, SimpleDateFormat(GameData.dateFormat).format(Date())))
                g.updateAttribute(XmlAttribute(FINISHED, Boolean.toString(game.isFinished())))
                break
            }
        }

        Profile.Companion.instance!!.saveChanges()
        saveGamesFile(games)
    }

    /**
     * Deletes no longer existing [Games] from the list.
     *
     */
    fun updateGamesList() {
        val games = gamesXml
        val newGames = XmlTree(games.name)
        for (g in games) {
            if (FileManager.getGameFile(g.getAttributeValue(ID).toInt()).exists()) {
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
    fun deleteGame(id: Int) {
        if (id == Profile.instance!!.currentGame) {
            Profile.instance!!.currentGame = Profile.NO_GAME
            Profile.instance!!.saveChanges() //save 'currentGameID' in xml (otherwise menu will offer 'continue')
        }
        FileManager.deleteGame(id)
        updateGamesList()
    }

    /**
     * Deletes all finished [Games] from storage and the current list
     */
    fun deleteFinishedGames() {
        val games = gamesXml
        for (g in games) {
            if (Boolean.parseBoolean(g.getAttributeValue(FINISHED))) {
                FileManager.deleteGame(g.getAttributeValue(ID).toInt())
            }
        }
        updateGamesList()
    }

    private fun saveGamesFile(games: XmlTree) {
        try {
            XmlHelper().saveXml(games, FileManager.getGamesFile())
        } catch (e: IOException) {
            throw IllegalStateException("Profil broken", e)
        }
    }

    private val gamesXml: XmlTree
        private get() = try {
            val gf = FileManager.getGamesFile()
            XmlHelper().loadXml(gf)
        } catch (e: IOException) {
            throw IllegalStateException("Profil broken", e)
        }

    companion object {

        private const val ID = "id"
        private const val FINISHED = "finished"
        private const val PLAYED_AT = "played_at"
        private const val SUDOKU_TYPE = "sudoku_type"
        private const val COMPLEXITY = "complexity"

        val instance = GameManager()
    }

    init {
        xmlHandler = GameXmlHandler()
    }
}