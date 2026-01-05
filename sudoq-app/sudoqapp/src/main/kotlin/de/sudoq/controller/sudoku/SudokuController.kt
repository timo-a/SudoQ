/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import android.content.Context
import de.sudoq.R
import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.game.Game
import de.sudoq.model.profile.ProfileSingleton
import de.sudoq.model.profile.Statistics
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.persistence.profile.ProfileRepo
import de.sudoq.persistence.profile.ProfilesListRepo

/**
 * Der SudokuController ist dafür zuständig auf Aktionen des Benutzers mit dem
 * Spielfeld zu reagieren.
 */
class SudokuController(
    /** Hält eine Referenz auf das Game, welches Daten über das aktuelle Spiel enthält */
    private val game: Game,
    /** Die SudokuActivity. */
    private val context: SudokuActivity
) : AssistanceRequestListener, ActionListener {

    /**
     * Debugging
     *
     * @throws IllegalArgumentException
     * Wird geworfen, falls null übergeben wird
     */
    private fun getsucc(illegal: Boolean) {
        require(!illegal) { "tu" }
    }
    /** Methods  */
    /**
     * {@inheritDoc}
     */
    override fun onRedo() {
        game.redo()
    }

    /**
     * {@inheritDoc}
     */
    override fun onUndo() {
        game.undo()
    }

    /**
     * {@inheritDoc}
     */
    override fun onNoteAdd(cell: Cell, value: Int) {
        game.addAndExecute(NoteActionFactory().createAction(value, cell))
    }

    /**
     * {@inheritDoc}
     */
    override fun onNoteDelete(cell: Cell, value: Int) {
        game.addAndExecute(
            NoteActionFactory().createAction(
                value,
                cell
            )
        ) //TODO same code as onNoteAdd why?
    }

    /**
     * {@inheritDoc}
     */
    override fun onAddEntry(cell: Cell, value: Int) {
        game.addAndExecute(SolveActionFactory().createAction(value, cell))
        if (game.isFinished()) {
            updateStatistics()
            handleFinish(false)
        }
    }

    fun onHintAction(a: Action) {
        game.addAndExecute(a)
        if (game.isFinished()) {
            updateStatistics()
            handleFinish(false)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onDeleteEntry(cell: Cell) {
        game.addAndExecute(SolveActionFactory().createAction(Cell.EMPTYVAL, cell))
    }

    /**
     * {@inheritDoc}
     */
    override fun onSolveOne(): Boolean {
        val res = game.solveCell()
        if (game.isFinished()) {
            updateStatistics()
            handleFinish(false)
        }
        return res
    }

    /**
     * {@inheritDoc}
     */
    override fun onSolveCurrent(cell: Cell): Boolean {
        val res = game.solveCell(cell)
        if (game.isFinished()) {
            updateStatistics()
            handleFinish(false)
        }
        return res
    }

    /**
     * {@inheritDoc}
     */
    override fun onSolveAll(): Boolean {
        for (f in game.sudoku!!) {
            if (!f.isNotWrong) {
                game.addAndExecute(SolveActionFactory().createAction(Cell.EMPTYVAL, f))
            }
        }
        val res = game.solveAll()
        if (res) handleFinish(true)
        return res
    }

    /**
     * Zeigt einen Gewinndialog an, der fragt, ob das Spiel beendet werden soll.
     *
     * @param surrendered
     * TODO
     */
    private fun handleFinish(surrendered: Boolean) {
        context.setFinished(true, surrendered)
    }

    /**
     * Updatet die Spielerstatistik des aktuellen Profils in der App.
     */
    private fun updateStatistics() {
        when (game.sudoku!!.complexity!!) {
            Complexity.infernal -> incrementStatistic(Statistics.playedInfernalSudokus)
            Complexity.difficult -> incrementStatistic(Statistics.playedDifficultSudokus)
            Complexity.medium -> incrementStatistic(Statistics.playedMediumSudokus)
            Complexity.easy -> incrementStatistic(Statistics.playedEasySudokus)
            Complexity.arbitrary -> throw IllegalStateException("unexpected complexity value: 'arbitrary'")
        }
        incrementStatistic(Statistics.playedSudokus)
        val profilesDir = context.getDir(
            context.getString(R.string.path_rel_profiles),
            Context.MODE_PRIVATE
        )
        val p = ProfileSingleton.getInstance(profilesDir, ProfileRepo(profilesDir),
                                             ProfilesListRepo(profilesDir))
        if (p.getStatistic(Statistics.fastestSolvingTime) > game.time) {
            p.setStatistic(Statistics.fastestSolvingTime, game.time)
        }
        if (p.getStatistic(Statistics.maximumPoints) < game.score) {
            p.setStatistic(Statistics.maximumPoints, game.score)
        }
    }

    private fun incrementStatistic(s: Statistics) { //TODO this should probably be in model...
        val profilesDir = context.getDir(
            context.getString(R.string.path_rel_profiles),
            Context.MODE_PRIVATE
        )
        val p = ProfileSingleton.getInstance(profilesDir, ProfileRepo(profilesDir),
                                             ProfilesListRepo(profilesDir))
        p.setStatistic(s, p.getStatistic(s) + 1)
    }
}