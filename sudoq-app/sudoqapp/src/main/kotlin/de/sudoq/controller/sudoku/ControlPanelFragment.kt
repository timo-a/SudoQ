package de.sudoq.controller.sudoku

import android.content.Context
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
import de.sudoq.model.profile.ProfileSingleton
import de.sudoq.view.SudokuLayout

/**
 * Created by timo on 04.11.16.
 */
class ControlPanelFragment : Fragment() {
    private lateinit var activity: SudokuActivity
    private lateinit var sl: SudokuLayout
    private lateinit var game: Game
    private lateinit var controller: SudokuController

    fun initialize() {
        activity = getActivity() as SudokuActivity
        sl = activity.sudokuLayout!!
        game = activity.game!!
        controller = activity.sudokuController!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val frameLayout = FrameLayout(getActivity()!!)
        populateViewForOrientation(inflater, frameLayout)
        return frameLayout
    }

    /**
     * Container-Klasse für die Buttons dieser Activity
     */
    private object Buttons {
        /**
         * Der "Redo" Button
         */
        var redoButton: ImageButton? = null

        /**
         * Der "Undo" Button
         */
        var undoButton: ImageButton? = null

        /**
         * Der "ActionTree anzeigen" Button
         */
        var actionTreeButton: ImageButton? = null

        /**
         * Der "Gesten umschalten" Button
         */
        var gestureButton: ImageButton? = null

        /**
         * Der "Hilfestellungen anzeigen" Button
         */
        var assistancesButton: ImageButton? = null

        /**
         * Der "Lesezeichen" Button des ActionTrees
         */
        var bookmarkButton: Button? = null

        /**
         * Der "Schließen" Button des ActionTrees
         */
        var closeButton: Button? = null
    }

    fun inflateButtons() {
        Buttons.redoButton = view!!.findViewById<View>(R.id.button_sudoku_redo) as ImageButton
        Buttons.undoButton = view!!.findViewById<View>(R.id.button_sudoku_undo) as ImageButton
        Buttons.actionTreeButton =
            view!!.findViewById<View>(R.id.button_sudoku_actionTree) as ImageButton
        Buttons.gestureButton =
            view!!.findViewById<View>(R.id.button_sudoku_toggle_gesture) as ImageButton
        Buttons.assistancesButton =
            view!!.findViewById<View>(R.id.button_sudoku_help) as ImageButton
        val activity = getActivity() as SudokuActivity
        Buttons.bookmarkButton =
            activity.findViewById<View>(R.id.sudoku_action_tree_button_bookmark) as Button
        Buttons.closeButton =
            activity.findViewById<View>(R.id.sudoku_action_tree_button_close) as Button
    }

    /**
     * Aktualisiert alle Buttons, also den Redo, Undo und ActionTree-Button,
     * sowie die Tastatur
     */
    fun updateButtons() {
        val activity = getActivity() as SudokuActivity
        val actionTreeShown = activity.isActionTreeShown
        val finished = activity.finished
        Buttons.redoButton!!.isEnabled = game.stateHandler!!.canRedo() && !actionTreeShown
        Buttons.undoButton!!.isEnabled = game.stateHandler!!.canUndo() && !actionTreeShown
        Buttons.actionTreeButton!!.isEnabled = !actionTreeShown
        Buttons.assistancesButton!!.isEnabled = !actionTreeShown && !finished
        Buttons.gestureButton!!.isEnabled = !actionTreeShown
        val sudokuView = sl
        activity.mediator!!.setKeyboardState(!finished && sudokuView.currentCellView != null)
    }

    val gestureButton: ImageButton?
        get() = Buttons.gestureButton

    fun onClick(v: View) {
        val activity = getActivity() as SudokuActivity
        val sudokuController = controller
        val mediator = activity.mediator
        if (v === Buttons.undoButton) {
            sudokuController.onUndo()
            mediator!!.updateKeyboard()
        } else if (v === Buttons.redoButton) {
            sudokuController.onRedo()
            mediator!!.updateKeyboard()
        } else if (v === Buttons.actionTreeButton) {
            activity.toogleActionTree()
        } else if (v === Buttons.gestureButton) {
            val profile = ProfileSingleton.getInstance(
                activity.getDir(
                    getString(R.string.path_rel_profiles),
                    Context.MODE_PRIVATE
                )
            )
            if (activity.checkGesture()) {
                /* toggle 'gesture active'
				 * toggle button icon as well */
                profile.isGestureActive = !profile.isGestureActive
                v.setSelected(profile.isGestureActive)
            } else {
                profile.isGestureActive = false
                v.setSelected(false)
                Toast.makeText(
                    activity,
                    getString(R.string.error_gestures_not_complete),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (v === Buttons.assistancesButton) {
            activity.showAssistancesDialog()
        } else if (v === Buttons.bookmarkButton) {
            game.markCurrentState()
            activity.actionTreeController!!.refresh()
        } else if (v === Buttons.closeButton) {
            activity.toogleActionTree()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val inflater = LayoutInflater.from(getActivity())
        populateViewForOrientation(inflater, view as ViewGroup?)
    }

    private fun populateViewForOrientation(inflater: LayoutInflater, viewGroup: ViewGroup?) {
        val activity = getActivity() as SudokuActivity
        game = activity.game!!
        viewGroup!!.removeAllViewsInLayout()
        val conf = resources.configuration
        val portraitLeft =
            conf.orientation == Configuration.ORIENTATION_PORTRAIT && game.isLefthandedModeActive
        val layout = if (portraitLeft) R.layout.bottom_panel_left else R.layout.bottom_panel
        val subview = inflater.inflate(layout, viewGroup)

        // Find your buttons in subview, set up onclicks, set up callbacks to your parent fragment or activity here.
    }

    //sl.find... doesn't seem to work
    private val controlPanel: View
        private get() {
            val conf = resources.configuration
            val portraitLeft =
                conf.orientation == Configuration.ORIENTATION_PORTRAIT && game.isLefthandedModeActive
            return activity.findViewById(
                if (portraitLeft)
                    R.id.controlPanelLeft //sl.find... doesn't seem to work
                else
                    R.id.controlPanel
            )
        }

    fun hide() {
        controlPanel.visibility = View.GONE
    }

    fun show() {
        controlPanel.visibility = View.VISIBLE
    }
}