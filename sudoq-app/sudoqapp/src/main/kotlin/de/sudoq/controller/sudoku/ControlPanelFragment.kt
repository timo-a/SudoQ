package de.sudoq.controller.sudoku

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import de.sudoq.R
import de.sudoq.model.game.Game
import de.sudoq.model.game.GameSettings

class ControlPanelFragment : Fragment() {
    private lateinit var game: Game
    private lateinit var controller: SudokuController
    private lateinit var gameSettings: GameSettings

    // Buttons
    private var redoButton: ImageButton? = null
    private var undoButton: ImageButton? = null
    private var actionTreeButton: ImageButton? = null
    var gestureButton: ImageButton? = null
        private set

        /**
         * Der "Hilfestellungen anzeigen" Button
         */
    private var assistancesButton: ImageButton? = null
    private var bookmarkButton: Button? = null

        /**
         * Der "Schließen" Button des ActionTrees
         */
    private var closeButton: Button? = null

    fun initialize() {
        val activity = requireActivity() as SudokuActivity
        game = activity.game!!
        controller = activity.sudokuController!!
        gameSettings = game.gameSettings
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val frameLayout = FrameLayout(requireActivity())
        populateViewForOrientation(inflater, frameLayout)
        return frameLayout
    }


    fun inflateButtons(root: ViewGroup) {
        redoButton = root.findViewById<ImageButton>(R.id.button_sudoku_redo)
        undoButton = root.findViewById<ImageButton>(R.id.button_sudoku_undo)
        actionTreeButton = root.findViewById<ImageButton>(R.id.button_sudoku_actionTree)
        gestureButton = root.findViewById<ImageButton>(R.id.button_sudoku_toggle_gesture)
        assistancesButton = root.findViewById<ImageButton>(R.id.button_sudoku_help)
    }

    /**
     * The bookmark/close buttons live in the activity's action-tree overlay, not in this
     * fragment's own layout, so they can't be bound in [inflateButtons]/[populateViewForOrientation]:
     * those run during onCreateView(), which happens while the activity's root layout is still
     * being inflated - before the action-tree overlay (declared further down in the same XML)
     * exists. findViewById() would silently return null there.
     *
     * Call this only after the activity is done inflating its full layout (i.e. after the
     * action-tree overlay is guaranteed to exist), e.g. from SudokuActivity once its own
     * setContentView()/action-tree setup has completed.
     */
    fun bindActionTreeButtons() {
        val sudokuActivity = requireActivity() as SudokuActivity
        bookmarkButton = sudokuActivity.findViewById<Button>(R.id.sudoku_action_tree_button_bookmark)
        closeButton = sudokuActivity.findViewById<Button>(R.id.sudoku_action_tree_button_close)

        val clickListener = View.OnClickListener { v -> onClick(v) }
        bookmarkButton?.setOnClickListener(clickListener)
        closeButton?.setOnClickListener(clickListener)
    }

    /**
     * Aktualisiert alle Buttons, also den Redo, Undo und ActionTree-Button,
     * sowie die Tastatur
     */
    fun updateButtons() {
        val sudokuActivity = activity as? SudokuActivity ?: return
        val actionTreeShown = sudokuActivity.isActionTreeShown
        val finished = sudokuActivity.finished
        redoButton?.isEnabled = game.stateHandler.canRedo() && !actionTreeShown
        undoButton?.isEnabled = game.stateHandler.canUndo() && !actionTreeShown
        actionTreeButton?.isEnabled = !actionTreeShown
        assistancesButton?.isEnabled = !actionTreeShown && !finished
        gestureButton?.isEnabled = !actionTreeShown
        sudokuActivity.mediator?.setKeyboardState(!finished && sudokuActivity.currentCellView != null)
    }

    fun onClick(v: View) {
        val sudokuActivity = requireActivity() as SudokuActivity
        val mediator = sudokuActivity.mediator
        when (v) {
            undoButton -> {
                controller.onUndo()
                mediator!!.updateKeyboard()
            }
            redoButton -> {
                controller.onRedo()
                mediator!!.updateKeyboard()
            }
            actionTreeButton, closeButton -> {
                sudokuActivity.toogleActionTree()
            }
            gestureButton -> {
                if (sudokuActivity.checkGesture()) {
                    /* toggle 'gesture active'
                     * toggle button icon as well */
                    gameSettings.isGesturesSet = !gameSettings.isGesturesSet
                    v.isSelected = gameSettings.isGesturesSet
                    sudokuActivity.updateBackPressState()
                } else {
                    gameSettings.isGesturesSet = false
                    v.isSelected = false
                    Toast.makeText(
                        activity,
                        getString(R.string.error_gestures_not_complete),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            assistancesButton -> {
                sudokuActivity.showAssistancesDialog()
            }
            bookmarkButton -> {
                game.markCurrentState()
                sudokuActivity.actionTreeController!!.refresh()
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val inflater = LayoutInflater.from(activity)
        populateViewForOrientation(inflater, view as ViewGroup?)
    }

    private fun populateViewForOrientation(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        val sudokuActivity = requireActivity() as SudokuActivity
        game = sudokuActivity.game!!
        viewGroup!!.removeAllViewsInLayout()
        val conf = resources.configuration
        // there is only a left handed version for Portrait
        val portraitLeft = conf.orientation == Configuration.ORIENTATION_PORTRAIT
                && game.isLefthandedModeActive
        val layout = if (portraitLeft)
            R.layout.bottom_panel_left
        else
            R.layout.bottom_panel
        val subview = inflater.inflate(layout, viewGroup)

        // Find your buttons in subview, set up onclicks, set up callbacks to your parent fragment or activity here.

        inflateButtons(viewGroup)

        val clickListener = View.OnClickListener { v -> onClick(v) }

        undoButton?.setOnClickListener(clickListener)
        redoButton?.setOnClickListener(clickListener)
        actionTreeButton?.setOnClickListener(clickListener)
        gestureButton?.setOnClickListener(clickListener)
        assistancesButton?.setOnClickListener(clickListener)

        updateButtons()
    }

    fun hide() {
        view?.visibility = View.GONE
    }

    fun show() {
        view?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up references to views when the fragment view is destroyed
        redoButton = null
        undoButton = null
        actionTreeButton = null
        gestureButton = null
        assistancesButton = null
        bookmarkButton = null
        closeButton = null
    }
}