/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.game.GameMapper
import de.sudoq.model.persistence.xml.game.GameRepo
import de.sudoq.model.persistence.xml.game.GamesListRepo
import de.sudoq.model.persistence.xml.game.IGamesListRepo
import de.sudoq.model.profile.ProfileSingleton
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.SudokuManager
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Singleton for creating and loading sudoku games.
 */
class GameManager(private var profile: ProfileManager,
                  private var gameRepo: IRepo<Game>,
                  private var gamesListRepo: IGamesListRepo,
                  val sudokuTypeRepo: IRepo<SudokuType>) {


    private var games: MutableList<GameData>

    /**
     * Creates a new gam and sets up the necessary files.
     *
     * @param type The type of the [Sudoku]
     * @param complexity The complexity of the Sudoku
     * @param assistsances The available assistances for the game
     * @return The new [Game]
     *
     */
    fun newGame(
        type: SudokuTypes,
        complexity: Complexity,
        assistances: GameSettings,
        sudokuDir: File
    ): Game {
        val sm = SudokuManager(sudokuDir, sudokuTypeRepo)
        val sudoku = sm.getNewSudoku(type, complexity)
        sm.usedSudoku(sudoku) //TODO warum instanziierung, wenn laut doc singleton?

        val newGameID = gameRepo.create().id //due to interface we cannot pass sudoku to the new game
        val game = Game(newGameID, sudoku)
        game.setAssistances(assistances)
        gameRepo.update(game)
        val gameData = GameData(
            game.id,
            SimpleDateFormat(GameData.dateFormat).format(Date()),
            game.isFinished(),
            game.sudoku!!.sudokuType?.enumType!!,
            game.sudoku!!.complexity!!
        )

        games.add(gameData)
        gamesListRepo.saveGamesFile(games)

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
        return gameRepo.read(id)
    }


    /**
     * Save a Game to XML.
     *
     * @param game [Game] to save
     */
    fun save(game: Game) {
        gameRepo.update(game)

        updateGameInList(game)

        profile.saveChanges()
        gamesListRepo.saveGamesFile(games)
    }

    private fun updateGameInList(game: Game) {
        val oldGameData = games.find { it.id == game.id }!!
        val newGameData = GameData(
            oldGameData.id,
            SimpleDateFormat(GameData.dateFormat).format(Date()), game.isFinished(),
            oldGameData.type, oldGameData.complexity
        )

        games.remove(oldGameData)
        games.add(newGameData)
        games = games.sortedDescending().toMutableList()
    }

    /**
     * Deletes a [Game] by id from memory and the list.
     * If no game is found, nothing happens.
     *
     * @param id ID of the game to remove
     */
    fun deleteGame(id: Int) {
        if (id == profile.currentGame) {
            profile.currentGame = ProfileManager.NO_GAME
            profile.saveChanges() //save 'currentGameID' in xml (otherwise menu will offer 'continue')
        }
        gameRepo.delete(id)
        updateGamesList()
    }

    /**
     * A list data of all games of a player.
     * Sorted by unfinished first then by most recently played //TODO confirm with test
     *
     * @return the list
     */
    val gameList: List<GameData>
        get() = games

    /**
     * Deletes no longer existing [Game]s from the list.
     *
     */
    fun updateGamesList() {
        gamesListRepo.saveGamesFile(games.filter { gamesListRepo.fileExists(it.id) })
    }


    /**
     * Deletes all finished [Games] from storage and the current list
     */
    fun deleteFinishedGames() {
        games.filter { it.isFinished }
            .forEach { gameRepo.delete(it.id) }

        updateGamesList()
    }



    init{
        this.games = gamesListRepo.load()
    }

}