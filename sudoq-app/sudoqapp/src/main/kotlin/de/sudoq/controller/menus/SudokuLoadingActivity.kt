/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.TextView
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import de.sudoq.R
import de.sudoq.controller.SudoqListActivity
import de.sudoq.controller.sudoku.SudokuActivity
import de.sudoq.model.game.GameData
import de.sudoq.model.game.GameManager
import de.sudoq.model.profile.ProfileManager
import de.sudoq.persistence.game.GameRepo
import javax.inject.Inject

/**
 * Diese Klasse repräsentiert den Lade-Controller des Sudokuspiels. Mithilfe von
 * SudokuLoading können Sudokus geladen werden und daraufhin zur SudokuActivity
 * gewechselt werden.
 */
@AndroidEntryPoint
class SudokuLoadingActivity : SudoqListActivity(), OnItemClickListener, OnItemLongClickListener {
    /** Attributes  */
    @Inject
    lateinit var profileManager: ProfileManager

    @Inject
    lateinit var gameRepo: GameRepo

    @Inject
    lateinit var gameManager: GameManager

    private lateinit var adapter: SudokuLoadingAdapter
    private lateinit var games: List<GameData>
    private var actionMode: ActionMode? = null

    /**
     * Wird aufgerufen, wenn SudokuLoading nach Programmstart zum ersten Mal
     * geladen aufgerufen wird. Hier wird das Layout inflated und es werden
     * nötige Initialisierungen vorgenommen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.sudokuloading)

        //toolbar
        initToolBar()

        initialiseGames()
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)
    }

    /// Action Bar

    /**
     * Wird beim ersten Anzeigen des Options-Menü von SudokuLoading aufgerufen
     * und initialisiert das Optionsmenü indem das Layout inflated wird.
     *
     * @return true falls das Options-Menü angezeigt werden kann, sonst false
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_sudoku_loading, menu)
        return true
    }

    /**
     * Wird beim Auswählen eines Menü-Items im Options-Menü aufgerufen. Ist das
     * spezifizierte MenuItem null oder ungültig, so wird nichts getan.
     *
     * @param item
     * Das ausgewählte Menü-Item
     * @return true, falls die Selection hier bearbeitet wird, false falls nicht
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sudokuloading_delete_finished -> {
                gameManager.deleteFinishedGames()
            }
            R.id.action_sudokuloading_delete_all -> {
                gameManager.gameList.forEach { gameManager.deleteGame(it.id) }
            }
            else -> super.onOptionsItemSelected(item)
        }
        onContentChanged()
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val noGames = gameManager.gameList.isEmpty()
        menu.findItem(R.id.action_sudokuloading_delete_finished).isVisible = !noGames
        menu.findItem(R.id.action_sudokuloading_delete_all).isVisible = !noGames
        return true
    }


    /**
     * {@inheritDoc}
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * {@inheritDoc}
     */
    override fun onContentChanged() {
        super.onContentChanged()
        initialiseGames()
        profileManager.currentGame = if (adapter.isEmpty) -1 else adapter.getItem(0)!!.id
    }

    /**
     * Wird aufgerufen, falls ein Element (eine View) in der AdapterView
     * angeklickt wird.
     *
     * @param parent
     * AdapterView in welcher die View etwas angeklickt wurde
     * @param view
     * View, welche angeklickt wurde
     * @param position
     * Position der angeklickten View im Adapter
     * @param id
     * ID der angeklickten View
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        if (actionMode == null) {
            val game = adapter.getItem(position)
            if (game != null) {
                profileManager.currentGame = game.id
                startActivity(Intent(this, SudokuActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        } else {
            adapter.toggleSelection(position)
            actionMode!!.title =
                getString(R.string.number_of_sudokus_selected, adapter.getSelectedPositions().size)
            if (adapter.getSelectedPositions().isEmpty()) {
                actionMode!!.finish()
            }
        }
    }

    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        if (actionMode != null) return false

        actionMode = startSupportActionMode(actionModeCallback)
        adapter.toggleSelection(position)
        actionMode?.title = getString(R.string.number_of_sudokus_selected, 1)
        return true
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.action_bar_sudoku_loading_contextual, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = when (item.itemId) {
            R.id.action_delete_selected -> {
                AlertDialog.Builder(this@SudokuLoadingActivity)
                    .setMessage(R.string.sudokuloading_delete_selected_confirmation)
                    .setPositiveButton(R.string.dialog_yes) { _, _ ->
                        val selectedPositions = adapter.getSelectedPositions()
                        val itemsToRemove = selectedPositions.mapNotNull { pos -> adapter.getItem(pos) }
                        itemsToRemove.forEach { gameData ->
                            gameManager.deleteGame(gameData.id)
                            adapter.remove(gameData)
                        }
                        mode.finish()
                        onContentChanged()
                    }
                    .setNegativeButton(R.string.dialog_no, null)
                    .show()
                true
            }
            else -> false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            adapter.clearSelection()
            actionMode = null
        }
    }

    private fun initialiseGames() {
        games = gameManager.gameList
        adapter = SudokuLoadingAdapter(this, games.toMutableList(), gameRepo)
        listAdapter = adapter
        listView!!.onItemClickListener = this
        listView!!.onItemLongClickListener = this
        val noGamesTextView = findViewById<TextView>(R.id.no_games_text_view)
        noGamesTextView.visibility = if (games.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Just for testing!
     * @return
     * number of saved games
     */
    val size: Int
        get() = games.size

    companion object {
        private val LOG_TAG = SudokuLoadingActivity::class.java.simpleName
    }
}
