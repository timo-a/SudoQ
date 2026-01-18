/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import android.app.AlertDialog
import android.gesture.GestureOverlayView
import android.gesture.GestureStore
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dagger.hilt.android.AndroidEntryPoint
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.menus.Utility
import de.sudoq.controller.sudoku.CellInteractionListener.SelectEvent
import de.sudoq.controller.sudoku.board.CellViewPainter.Companion.instance
import de.sudoq.controller.sudoku.board.CellViewStates
import de.sudoq.model.actionTree.ActionTree
import de.sudoq.model.actionTree.ActionTreeElement
import de.sudoq.model.actionTree.NoteAction
import de.sudoq.model.actionTree.SolveAction
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.Game
import de.sudoq.model.game.GameManager
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.persistence.game.GameRepo
import de.sudoq.view.*
import java.io.*
import javax.inject.Inject
import kotlin.math.abs
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Diese Klasse stellt die Activity des Sudokuspiels dar. Die Klasse hält das
 * Game und mehrere Controller um auf Interaktionen des Benutzers mit dem
 * Spielfeld zu reagieren. Die Klasse wird außerdem benutzt um zu verwalten,
 * welche Navigationselemente dem Nutzer angezeigt werden.
 */
@AndroidEntryPoint
class SudokuActivity : SudoqCompatActivity(), View.OnClickListener, ActionListener,
    ActionTreeNavListener {

    @Inject
    lateinit var profileManager: ProfileManager

    @Inject
    lateinit var gameRepo: GameRepo

    @Inject
    lateinit var gameManager: GameManager

    /**
     * Eine Referenz auf einen ActionTreeController, der die Verwaltung der
     * ActionTree-Anzeige und Benutzerinteraktion übernimmt
     */
    var actionTreeController: ActionTreeController? = null
        private set

    /**
     * Eine Referenz auf einen SudokuController, der Nutzereingaben verwaltet
     * und mit dem Model interagiert
     */
    var sudokuController: SudokuController? = null
        private set
    /**
     * JUST FOR TESTING PURPOSE!
     *
     * @return Das SudokuLayout des aktuellen Spiels
     */
    /**
     * Die View des aktuellen Sudokus
     */
    var sudokuLayout: SudokuLayout? = null
        private set

    /**
     * Die ScrollView, welche die SudokuView beinhaltet
     */
    private var sudokuScrollView: FullScrollLayout? = null
    /**
     * Gibt das aktuelle Game zurück.
     *
     * @return Das Game
     */
    /**
     * Das Game, auf welchem gerade gespielt wird
     */
    var game: Game? = null
        private set

    /**
     * Fängt Gesteneingaben des Benutzers ab
     */
    private var gestureOverlay: GestureInputOverlay? = null

    /**
     * Hält die von der Activity unterstützten Gesten
     */
    private val gestureStore = GestureStore()
    /**
     * Gibt zurück, ob zurzeit der ActionTree angezeigt wird.
     *
     * @return true, falls der ActionTree gerade angezeigt wird, false falls
     * nicht
     */
    /**
     * Ein Flag welches aussagt, ob gerade der ActionTree angezeigt wird
     */
    var isActionTreeShown = false
        private set

    private enum class Mode {
        Regular, HintMode
    }

    private var mode =
        Mode.Regular //TODO see that this gets saved in oninstancesaved and restored so hint persitst orientation change

    /**
     * Der Handler für die Zeit
     */
    private val timeHandler = Handler()
    //TODO this.finished vs game.finished, which is what
    /**
     * Zeigt an, dass dieses Spiel beendet wurde
     */
    var finished = false
        private set

    /**
     * Der Vermittler zwischen Sudoku und Eingabemöglichkeiten
     */
    var mediator: UserInteractionMediator? = null
        private set

    private lateinit var currentSymbolSet: Array<String>

    /** for time. YES IT IS USED! */
    private var mMenu: Menu? = null

    /** Methods  */
    private fun initializeSymbolSet() {
        currentSymbolSet = when (game!!.sudoku!!.sudokuType.numberOfSymbols) {
            4 -> Symbol.MAPPING_NUMBERS_FOUR
            6 -> Symbol.MAPPING_NUMBERS_SIX
            9 -> Symbol.MAPPING_NUMBERS_NINE
            16 -> Symbol.MAPPING_NUMBERS_HEX_LETTERS
            else -> Symbol.MAPPING_NUMBERS_HEX_LETTERS
        }
        Symbol.createSymbol(currentSymbolSet)
    }

    var panel: ControlPanelFragment? = null
        private set

    /**
     * Wird beim ersten Aufruf der Activity aufgerufen. Setzt das Layout der
     * Activity und nimmt Initialisierungen vor.
     *
     * @param savedInstanceState
     * Gespeicherte Daten eines vorigen Aufrufs dieser Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "Created")

        // Load the Game by using current game id
        if (savedInstanceState != null) {
            try {
                game = gameManager.load(savedInstanceState.getInt(SAVE_GAME_ID.toString() + ""))
            } catch (e: Exception) {
                finish()
            }
        } else {
            game = gameManager.load(profileManager.currentGame)
        }

        if (game != null) {

            /* Determine how many numbers are needed. 1-9 or 1-16 ? */
            initializeSymbolSet()
            setContentView(if (game!!.isLefthandedModeActive) R.layout.sudoku_for_lefties else R.layout.sudoku)
            val toolbar =
                findViewById<Toolbar>(R.id.toolbar) //TODO subclass and put time, ... in it
            setSupportActionBar(toolbar)
            //todo the controller only needs the current profile, which is fix for the current game, but profilemanager does not give it out. does profilemanager need to wrap the profile?
            sudokuController = SudokuController(game!!, this, profileManager)
            actionTreeController = ActionTreeController(this)
            Log.d(LOG_TAG, "Initialized")
            inflateViewAndButtons()
            Log.d(LOG_TAG, "Inflated view and control_panel")
            inflateGestures(savedInstanceState == null)
            Log.d(LOG_TAG, "Inflated gestures")
            // Scale SudokuView to LayoutSize, when inflating view is finished
            val vto = sudokuLayout!!.viewTreeObserver
            vto.addOnGlobalLayoutListener(MyGlobalLayoutListener(this, savedInstanceState))
            val keyboardView = findViewById<VirtualKeyboardLayout>(R.id.virtual_keyboard)
            mediator = UserInteractionMediator(
                keyboardView,
                sudokuLayout,
                game,
                gestureOverlay,
                gestureStore,
                profileManager
            )
            mediator!!.registerListener(sudokuController!!)
            mediator!!.registerListener(this)
            if (game!!.isFinished()) {
                setFinished(showWinDialog = false, surrendered = false)
            } else {
                //find the current cell from when the game was saved and mark it selected
                val lastAction = game!!.currentState.action

                fun getCellView(cellId: Int): SudokuCellView {
                    val currentPosition = game!!.sudoku!!.getPosition(cellId)!!
                    return sudokuLayout!!.getSudokuCellView(currentPosition)
                }

                when (lastAction) {
                    // if no action
                    is ActionTree.MockAction -> { /* */}
                    is SolveAction -> {
                        val currentCellView = getCellView(lastAction.cell.id)
                        currentCellView.programmaticallySelectShort()}
                    is NoteAction -> {
                        val currentCellView = getCellView(lastAction.cell.id)
                        currentCellView.programmaticallySelectLong()}
                    else -> Log.e("GAME_RESTORE", "last action of unknown type")
                }

            }
            setTypeText()
            updateButtons()
            panel!!.gestureButton!!.isSelected = profileManager.isGestureActive
        }
    }

    class MyGlobalLayoutListener(
        private val activity: SudokuActivity,
        private val savedInstanceState: Bundle?): OnGlobalLayoutListener {

        override fun onGlobalLayout() {
            Log.d(LOG_TAG, "SudokuView height: $activity.sudokuLayout!!.measuredHeight")
            Log.d(LOG_TAG, "SudokuScrollView height: $activity.sudokuScrollView!!.measuredHeight")
            activity.sudokuLayout!!.optiZoom(
                activity.sudokuScrollView!!.measuredWidth,
                activity.sudokuScrollView!!.measuredHeight
            )
            val obs = activity.sudokuLayout!!.viewTreeObserver
            if (savedInstanceState != null) {
                val zoomFactor =
                    savedInstanceState.getFloat(SAVE_ZOOM_FACTOR.toString() + "")
                if (zoomFactor != 0.0f) {
                    activity.sudokuLayout!!.zoom(zoomFactor)
                    activity.sudokuScrollView!!.zoomFactor = zoomFactor
                }
                val scrollX = savedInstanceState.getFloat(SAVE_SCROLL_X.toString()) +
                        activity.sudokuLayout!!.currentLeftMargin
                val scrollY = savedInstanceState.getFloat(SAVE_SCROLL_Y.toString()) +
                        activity.sudokuLayout!!.currentTopMargin
                activity.sudokuScrollView!!.scrollTo(scrollX.toInt(), scrollY.toInt())

            }
            obs.removeGlobalOnLayoutListener(this)
        }
    }

    /**
     * Speichert das markierte Feld und die Status des Aktionsbaumes, um bei
     * Wiederherstellung der Activity nach einem Orientierungswechsel oder
     * aufgrund einer temporären Verdrängung durch Speicherknappheit den alten
     * Status wiederherzustellen.
     *
     * @param outState
     * Der Status in den gespeichert wird
     */
    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(SAVE_ZOOM_FACTOR.toString(), sudokuScrollView!!.zoomFactor)
        outState.putFloat(
            SAVE_SCROLL_X.toString(),
            sudokuScrollView!!.getScrollValueX() - sudokuLayout!!.currentLeftMargin
        )
        outState.putFloat(
            SAVE_SCROLL_Y.toString(),
            sudokuScrollView!!.getScrollValueY() - sudokuLayout!!.currentTopMargin
        )
        outState.putBoolean(SAVE_ACTION_TREE_SHOWN.toString(), isActionTreeShown)
        outState.putInt(SAVE_GAME_ID.toString(), game!!.id)
        outState.putBoolean(
            SAVE_GESTURE_ACTIVE.toString(),
            gestureOverlay != null && gestureOverlay!!.visibility == View.VISIBLE
        )
        if (sudokuLayout!!.currentCellView != null) {
            val position = game!!.sudoku!!.getPosition(sudokuLayout!!.currentCellView!!.cell.id)!!
            outState.putInt(SAVE_FIELD_X.toString(), position.x)
            outState.putInt(SAVE_FIELD_Y.toString(), position.y)
        } else {
            outState.putInt(SAVE_FIELD_X.toString(), -1)
        }
        Log.d(LOG_TAG, "Saved state")
    }

    /**
     * Stellt den Status der Activity wieder her, also insbesondere das
     * markierte Feld und den Status der Aktionsbaumes.
     *
     * @param state
     * Der wiederherzustellende Status
     */
    public override fun onRestoreInstanceState(state: Bundle) {
        if (state.getBoolean(SAVE_ACTION_TREE_SHOWN.toString())) {
            val vto = sudokuLayout!!.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    toogleActionTree()
                    val obs = sudokuLayout!!.viewTreeObserver
                    obs.removeGlobalOnLayoutListener(this)
                }
            })
        }
        if (state.getInt(SAVE_FIELD_X.toString()) != -1) {
            //save_field_x not being -1 means the last sudoku hat a cell selected
            //this cell shall be selected again
            val currentPosition = Position[
                    state.getInt(SAVE_FIELD_X.toString()),
                    state.getInt(SAVE_FIELD_Y.toString())]

            sudokuLayout!!.getSudokuCellView(currentPosition).programmaticallySelectShort()
            //todo kann man hier auch mit dem letztem Blatt im ActionTree arbeiten, so wie oben in GlobalLayoutListener?

        }
        if (state.getBoolean(SAVE_GESTURE_ACTIVE.toString())) {
            mediator!!.onCellSelected(sudokuLayout!!.currentCellView!!, SelectEvent.Short)
        }
        if (mode == Mode.HintMode) {
            findViewById<View>(R.id.controlPanel).visibility = View.GONE
            findViewById<View>(R.id.hintPanel).visibility = View.VISIBLE
        }
        Log.d(LOG_TAG, "Restored state")
    }

    /**
     * Setzt den Text für Typ und Schwierigkeit des aktuellen Sudokus.
     */
    private fun setTypeText() {
        val type = Utility.type2string(this, game!!.sudoku!!.sudokuType.enumType)
        val comp = Utility.complexity2string(this, game!!.sudoku!!.complexity!!)
        val ab = supportActionBar
        ab!!.title = type
        ab.subtitle = comp
    }

    /**
     * Erzeugt die View für die Gesteneingabe
     *
     * @param firstStart
     * Gibt an, ob dies der erste Start der Activity ist und somit
     * Hinweise angezeigt werden sollen
     */
    private fun inflateGestures(firstStart: Boolean) {
        val gestureFile = profileManager.getCurrentGestureFile()
        try {
            val fis = FileInputStream(gestureFile)
            gestureStore.load(fis)
            fis.close()
        } catch (e: FileNotFoundException) {
            try {
                val os: OutputStream = FileOutputStream(gestureFile)
                gestureStore.save(os)
            } catch (ioe: IOException) {
                Log.w(LOG_TAG, "Gesture file cannot be loaded!")
            }
        } catch (e: IOException) {
            profileManager.isGestureActive = false
            Toast.makeText(this, R.string.error_gestures_no_library, Toast.LENGTH_SHORT).show()
        }
        if (firstStart && profileManager.isGestureActive) {
            val allGesturesSet = checkGesture()
            if (!allGesturesSet) {
                profileManager.isGestureActive = false
                Toast.makeText(
                    this,
                    getString(R.string.error_gestures_not_complete),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        gestureOverlay = GestureInputOverlay(this)
        val frameLayout = findViewById<FrameLayout>(R.id.sudoku_frame_layout)
        frameLayout.addView(gestureOverlay)
    }

    /**
     * Erstellt die Views und Buttons für diese Activity
     */
    private fun inflateViewAndButtons() {
        sudokuScrollView = findViewById(R.id.sudoku_cell)
        sudokuLayout = SudokuLayout(this)
        Log.d(LOG_TAG, "Inflated sudoku layout")
        sudokuLayout!!.gravity = Gravity.CENTER
        sudokuScrollView!!.addView(sudokuLayout!!)
        panel =
            supportFragmentManager.findFragmentById(R.id.controlPanelFragment) as ControlPanelFragment
        panel!!.initialize()
        panel!!.inflateButtons()
        var currentControlsView: LinearLayout? /* = (LinearLayout) findViewById(R.id.sudoku_time_border);
		FieldViewPainter.getInstance().setMarking(currentControlsView, FieldViewStates.CONTROLS);*/
        currentControlsView = findViewById(R.id.sudoku_border)
        instance!!.setMarking(currentControlsView, CellViewStates.SUDOKU)
        currentControlsView = findViewById(R.id.controls)
        instance!!.setMarking(currentControlsView, CellViewStates.KEYBOARD)
        val keyboardView = findViewById<VirtualKeyboardLayout>(R.id.virtual_keyboard)
        instance!!.setMarking(keyboardView, CellViewStates.KEYBOARD)
        keyboardView.refresh(game!!.sudoku!!.sudokuType.numberOfSymbols)
    }

    /**
     * Schaltet den ActionTree an bzw. aus.
     */
    fun toogleActionTree() {
        isActionTreeShown = !isActionTreeShown //toggle value
        actionTreeController!!.setVisibility(isActionTreeShown) //update AT-Controller
        updateButtons()
    }

    /**
     * Behandelt die Klicks auf Buttons dieser Activity
     */
    override fun onClick(v: View) {
        panel!!.onClick(v) //TODO make directly
        updateButtons()
    }

    /**
     * returns whether all Gestures are defined -> Gesture input possible
     */
    fun checkGesture(): Boolean {
        val symbolSet = Symbol.getInstance().symbolSet!!
        val gestures = gestureStore.gestureEntries

        return symbolSet.all { gestures.contains(it) }
    }

    /**
     * Wird aufgerufen, falls die Activity in den Vordergrund der App gelangt.
     */
    public override fun onResume() {
        super.onResume()
        if (!finished) timeHandler.postDelayed(timeUpdate, 1000)
    }

    /**
     * Wird aufgerufen, falls eine andere Activity in den Vordergrund der App
     * gelangt.
     */
    public override fun onPause() {
        timeHandler.removeCallbacks(timeUpdate)
        //gameid = 1
        gameManager.save(game!!)
        //gameid = -1
        val prevZoomFactor = sudokuScrollView!!.zoomFactor
        sudokuLayout!!.isDrawingCacheEnabled = true
        sudokuScrollView!!.resetZoom()

        // Restoring measurements after zomming out.
        sudokuLayout!!.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        sudokuLayout!!.layout(0, 0, sudokuLayout!!.measuredWidth, sudokuLayout!!.measuredHeight)
        sudokuLayout!!.buildDrawingCache(true)
        val sudokuCapture = sudokuLayout!!.drawingCache
        try {
            if (sudokuCapture != null) {
                val thumbnail = gameRepo.getGameThumbnailFile(profileManager.currentGame)
                sudokuCapture.compress(CompressFormat.PNG, 100, FileOutputStream(thumbnail))
            } else {
                Log.d(LOG_TAG, getString(R.string.error_thumbnail_get))
            }
        } catch (e: FileNotFoundException) {
            Log.w(LOG_TAG, getString(R.string.error_thumbnail_saved))
        }
        sudokuScrollView!!.zoomFactor = prevZoomFactor
        if (finished) {
            profileManager.currentGame = ProfileManager.NO_GAME
            profileManager.saveChanges()
        }
        super.onPause()
    }

    /**
     * Wird aufgerufen, falls die "Zurück"-Taste gedrückt wird.
     */
    override fun onBackPressed() {
        if (isActionTreeShown) {
            toogleActionTree()
        } else if (gestureOverlay!!.visibility == GestureOverlayView.VISIBLE) {
            gestureOverlay!!.visibility = GestureOverlayView.INVISIBLE
        } else {
            super.onBackPressed()
        }
    }

    /**
     * Wird aufgerufen, falls die Activity terminiert.
     */
    override fun finish() {
        if (game != null) {
            gameManager.save(game!!)
        }
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    fun setModeHint() {
        panel!!.hide()
        findViewById<View>(R.id.hintPanel).visibility = View.VISIBLE
        mode = Mode.HintMode
    }

    fun setModeRegular() {
        findViewById<View>(R.id.hintPanel).visibility = View.GONE
        panel!!.show()
        mode = Mode.Regular
    }

    private var fm: FragmentManager = supportFragmentManager

    /**
     * Zeigt einen Dialog mit den verfügbaren Hilfestellungen an.
     */
    fun showAssistancesDialog() {
        val ad: DialogFragment = AssistancesDialogFragment()
        ad.show(fm, "assistancesDialog")
    }

    /** Die aktuell ausgewählte FieldView */
    var currentCellView: SudokuCellView?
        get() = sudokuLayout!!.currentCellView
        set(cellView) {
            sudokuLayout!!.currentCellView = cellView
        }

    /**
     * Setzt dieses Spiel auf beendet.
     *
     * @param showWinDialog
     * Spezifiziert, ob ein Gewinn-Dialog angezeigt werden soll
     * @param surrendered
     * Gibt an, ob der Spieler aufgegeben hat
     */
    fun setFinished(showWinDialog: Boolean, surrendered: Boolean) {
        finished = true
        updateButtons()
        sudokuLayout!!.currentCellView?.select(game!!.isAssistanceAvailable(Assistances.markRowColumn))

        val keyView = findViewById<VirtualKeyboardLayout>(R.id.virtual_keyboard)
        for (i in 0 until keyView.childCount) {
            keyView.getChildAt(i).layoutParams = LinearLayout.LayoutParams(1, 1)
        }
        keyView.setPadding(10, 10, 10, 10)
        val text = TextView(this)
        text.text = statisticsString
        text.gravity = Gravity.CENTER
        keyView.addView(text)
        if (showWinDialog) showWinDialog(surrendered)
        timeHandler.removeCallbacks(timeUpdate)
    }

    private val assistancesTimeString: String
        get() = getTimeString(game!!.assistancesTimeCost)

    /**
     * Gibt die vergangene Zeit als formatierten String zurück.
     *
     * @return Den String für die Zeitanzeige
     */
    private val gameTimeString: String
        get() = getTimeString(game!!.time)

    /**
     * Zeigt einen Gewinndialog an, der fragt, ob das Spiel beendet werden soll.
     *
     * @param surrendered
     * Gibt an, ob der Spieler aufgegeben hat
     */
    private fun showWinDialog(surrendered: Boolean) {
        val deleteAlert = AlertDialog.Builder(this).create()
        deleteAlert.setTitle(
            if (surrendered)
                getString(R.string.dialog_surrender_title)
            else
                getString(R.string.dialog_won_title)
        )

        deleteAlert.setMessage(
            """
            ${getString(R.string.dialog_won_text)}
            
            $statisticsString
            """.trimIndent()
        )
        deleteAlert.setButton(getString(R.string.dialog_yes)) { dialog, which -> finish() }
        deleteAlert.setButton2(getString(R.string.dialog_no)) { dialog, which ->
            // Dummy: clicking no means staying in the game
        }
        deleteAlert.show()
    }

    /**
     * Gibt einen String mit der Spielstatistik zurück.
     *
     * @return Die Spielstatistik als String
     */
    private val statisticsString: String
        private get() = """
             ${getString(R.string.dialog_won_statistics)}:
             
             ${getString(R.string.dialog_won_timeneeded)}: $gameTimeString
             ${getString(R.string.dialog_won_score)}: ${game!!.score}
             """.trimIndent()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_sudoku, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Das Update-Runnable für die Zeit
     */
    private val timeUpdate: Runnable = object : Runnable {
        override fun run() {
            game!!.addTime(1)

            //getSupportActionBar().
            val timeView = findViewById<TextView>(R.id.time)
            timeView.setTextColor(resources.getColor(R.color.text1))


            /* for easy formatting, we display both: time and penalty on one element separated by \n
			 * this is not  perfect since we pad with whitespace and the font is not 'mono-style'(=not all letters same width)
			 * solution would be: create custom xml for action bar, but as of now I see no way how to make this easy.
			 * to save computing time we cache the offset*/
            var time = gameTimeString
            var penalty = " (+ $assistancesTimeString)"
            if (time.length > penalty.length) {
                penalty.padStart(time.length - penalty.length, ' ')
            } else if (time.length < penalty.length){
                time.padStart(penalty.length - time.length, ' ')
            }
            timeView.text = """
                $time
                $penalty
                """.trimIndent()
            timeHandler.postDelayed(this, 1000)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onHoverTreeElement(ate: ActionTreeElement) {
        updateButtons()
    }

    /**
     * {@inheritDoc}
     */
    override fun onLoadState(ate: ActionTreeElement) {
        updateButtons()
    }

    /**
     * {@inheritDoc}
     */
    override fun onRedo() {
        updateButtons()
    }

    /**
     * {@inheritDoc}
     */
    override fun onUndo() {
        updateButtons()
    }

    /**
     * {@inheritDoc}
     */
    override fun onNoteAdd(cell: Cell, value: Int) {
        onInputAction()
    }

    /**
     * {@inheritDoc}
     */
    override fun onNoteDelete(cell: Cell, value: Int) {
        onInputAction()
    }

    /**
     * {@inheritDoc}
     */
    override fun onAddEntry(cell: Cell, value: Int) {
        onInputAction()
    }

    /**
     * {@inheritDoc}
     */
    override fun onDeleteEntry(cell: Cell) {
        onInputAction()
    }

    fun onInputAction() {
        updateButtons()
        saveActionTree()
    }

    private fun updateButtons() {
        panel!!.updateButtons()
    }

    /** saves the whole game, purpose: save the action tree so a spontaneous crash doesn't lose us actions record  */
    private fun saveActionTree() {
        gameManager.save(game!!)
    }

    companion object {
        /** Attributes  */
        /**
         * Der Log-TAG
         */
        private val LOG_TAG = SudokuActivity::class.java.simpleName

        /**
         * Konstante für das Speichern der Game ID
         */
        private const val SAVE_GAME_ID = 0 //TODO make enum

        /**
         * Konstante für das Speichern der X-Koordinate der ausgewählten FieldView
         */
        private const val SAVE_FIELD_X = 1

        /**
         * Konstante für das Speichern der Y-Koordinate der ausgewählten FieldView
         */
        private const val SAVE_FIELD_Y = 2

        /**
         * Konstante für das Speichern des Aktionsbaum-Status
         */
        private const val SAVE_ACTION_TREE_SHOWN = 3

        /**
         * Konstante für das Speichern der Gesteneingabe
         */
        private const val SAVE_GESTURE_ACTIVE = 4

        /**
         * Konstante für das Speichern des aktuellen Zoomfaktors
         */
        private const val SAVE_ZOOM_FACTOR = 5

        /**
         * Konstante für das Speichern des Scrollwertes in X-Richtung
         */
        private const val SAVE_SCROLL_X = 6

        /**
         * Konstante für das Speichern des Scrollwertes in Y-Richtung
         */
        private const val SAVE_SCROLL_Y = 7

        /**
         * Returns a string in the format "HH:mm:ss" implied by the specified time in seconds.
         * There is no zero-padding for Hours, instead the string is just shorter if hours is zero.
         * @param time the time to format in seconds
         * @return a string representing the specified time in format "D..D HH:mm:ss"
         */
        @JvmStatic
        fun getTimeString(timeInSeconds: Int): String {
            val duration = timeInSeconds.toLong()
            val days = TimeUnit.SECONDS.toDays(duration)
            val hours = TimeUnit.SECONDS.toHours(duration) % 24
            val minutes = TimeUnit.SECONDS.toMinutes(duration) % 60
            val seconds = duration % 60
            return when {
                days  > 0 -> "%d %02d:%02d:%02d".format(days, hours, minutes, seconds)
                hours > 0 ->      "%d:%02d:%02d".format(      hours, minutes, seconds)
                else      ->         "%02d:%02d".format(             minutes, seconds)
            }
        }
    }
}
