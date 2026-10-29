package de.sudoq.controller.sudoku

import android.graphics.Bitmap
import android.app.UiAutomation
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import de.sudoq.BuildConfig
import de.sudoq.R
import de.sudoq.controller.menus.NewSudokuActivity
import de.sudoq.controller.menus.SplashActivity
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.game.GameSettings
import de.sudoq.model.game.Assistances
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.view.VirtualKeyboardButtonView
import de.sudoq.view.VirtualKeyboardLayout
import org.hamcrest.Matchers.sameInstance
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

/** Exercises the real board listeners, keyboard rendering, controls, and activity restoration. */
@RunWith(Parameterized::class)
class CompletedNumbersUiTest(private val type: SudokuTypes, private val rotation: Int) {
    @Test
    fun completionSurvivesUndoRedoAndRecreation() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(rotation)
        ActivityScenario.launch(NewSudokuActivity::class.java).use { preferences ->
            preferences.onActivity { activity ->
                val game = activity.gameManager.newGame(
                    type, Complexity.easy, GameSettings(), activity.sudokuRepoProvider
                )
                activity.profileManager.currentGame = game.id
                activity.profileManager.saveChanges()
            }
        }

        ActivityScenario.launch(SudokuActivity::class.java).use { scenario ->
            val landscape = rotation == UiAutomation.ROTATION_FREEZE_90
            scenario.onActivity {
                it.requestedOrientation = if (landscape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            val expectedOrientation = if (landscape) Configuration.ORIENTATION_LANDSCAPE
                else Configuration.ORIENTATION_PORTRAIT
            val deadline = SystemClock.uptimeMillis() + 10_000
            var orientationReady = false
            while (!orientationReady && SystemClock.uptimeMillis() < deadline) {
                scenario.onActivity { orientationReady = it.resources.configuration.orientation == expectedOrientation }
                if (!orientationReady) SystemClock.sleep(50)
            }
            assertTrue("Activity did not reach the requested orientation", orientationReady)
            var symbol = 0
            var cellId = 0
            lateinit var button: VirtualKeyboardButtonView
            scenario.onActivity { activity ->
                val sudoku = activity.game.sudoku
                val lastCell = sudoku.first { it.isEditable }
                symbol = lastCell.solution
                cellId = lastCell.id
                val cellView = activity.sudokuLayout!!.getSudokuCellView(sudoku.getPosition(cellId))
                activity.mediator!!.onCellSelected(cellView, CellInteractionListener.SelectEvent.Short)
                sudoku.filter { it.isEditable && it.solution == symbol && it.id != cellId }
                    .forEach {
                        activity.game.addAndExecute(SolveActionFactory().createAction(symbol, it))
                    }
                button = key(activity, symbol)
                assertFalse(button.isCompleted)
                assertEquals(1f, button.alpha, 0f)
            }

            // Use touch input so this also checks that completed keys remain editable.
            onView(sameInstance<View>(button)).perform(click())
            scenario.onActivity { activity ->
                assertEquals(symbol, activity.game.sudoku.getCell(cellId).currentValue)
                assertTrue(button.isCompleted)
                assertTrue(button.isEnabled)
                assertEquals(0.5f, button.alpha, 0f)
            }
            screenshot("$type-completed")

            onView(withId(R.id.button_sudoku_undo)).perform(click())
            scenario.onActivity { activity ->
                assertEquals(Cell.EMPTYVAL, activity.game.sudoku.getCell(cellId).currentValue)
                assertFalse(button.isCompleted)
                assertEquals(1f, button.alpha, 0f)
            }
            screenshot("$type-undo")

            onView(withId(R.id.button_sudoku_redo)).perform(click())
            scenario.onActivity { activity ->
                assertEquals(symbol, activity.game.sudoku.getCell(cellId).currentValue)
                assertTrue(button.isCompleted)
                assertEquals(0.5f, button.alpha, 0f)
            }

            scenario.recreate()
            scenario.onActivity { activity ->
                button = key(activity, symbol)
                assertEquals(symbol, activity.game.sudoku.getCell(cellId).currentValue)
                assertTrue(button.isCompleted)
                assertTrue(button.isEnabled)
                assertEquals(0.5f, button.alpha, 0f)
                val cellView = activity.sudokuLayout!!.getSudokuCellView(
                    activity.game.sudoku.getPosition(cellId)
                )
                if (activity.sudokuLayout!!.currentCellView != cellView) {
                    activity.mediator!!.onCellSelected(cellView, CellInteractionListener.SelectEvent.Short)
                }
            }

            onView(sameInstance<View>(button)).perform(click())
            scenario.onActivity { activity ->
                assertEquals(Cell.EMPTYVAL, activity.game.sudoku.getCell(cellId).currentValue)
                assertFalse(button.isCompleted)
                assertEquals(1f, button.alpha, 0f)

                val cellView = activity.sudokuLayout!!.getSudokuCellView(
                    activity.game.sudoku.getPosition(cellId)
                )
                activity.mediator!!.onCellSelected(cellView, CellInteractionListener.SelectEvent.Long)
            }
            onView(sameInstance<View>(button)).perform(click())
            scenario.onActivity { activity ->
                val cell = activity.game.sudoku.getCell(cellId)
                assertEquals(Cell.EMPTYVAL, cell.currentValue)
                assertTrue(cell.isNoteSet(symbol))
                assertFalse(button.isCompleted)
                assertEquals(1f, button.alpha, 0f)
            }
            scenario.recreate()
            scenario.onActivity { activity ->
                button = key(activity, symbol)
                assertTrue(activity.sudokuLayout!!.currentCellView!!.isNoteMode)
                assertTrue(activity.game.sudoku.getCell(cellId).isNoteSet(symbol))
                assertFalse(button.isCompleted)
            }
            screenshot("$type-note")

            // Re-enter the value, then check completion alongside candidate restriction.
            scenario.onActivity { activity ->
                val cellView = activity.sudokuLayout!!.getSudokuCellView(
                    activity.game.sudoku.getPosition(cellId)
                )
                activity.mediator!!.onCellSelected(cellView, CellInteractionListener.SelectEvent.Short)
            }
            onView(sameInstance<View>(button)).perform(click())
            scenario.onActivity { activity ->
                assertTrue(button.isCompleted)
                activity.game.gameSettings.setAssistance(Assistances.restrictCandidates)
                val otherCell = activity.game.sudoku.first {
                    it.isEditable && it.isNotSolved && it.solution != symbol
                }
                val otherView = activity.sudokuLayout!!.getSudokuCellView(
                    activity.game.sudoku.getPosition(otherCell.id)
                )
                activity.mediator!!.onCellSelected(otherView, CellInteractionListener.SelectEvent.Short)
                assertTrue(button.isCompleted)
                assertFalse(button.isEnabled)
                assertEquals(0.5f, button.alpha, 0f)
            }
            screenshot("$type-restricted")
        }
    }

    private fun key(activity: SudokuActivity, symbol: Int): VirtualKeyboardButtonView {
        val keyboard = activity.findViewById<VirtualKeyboardLayout>(R.id.virtual_keyboard)
        val columns = (keyboard.getChildAt(0) as ViewGroup).childCount
        return (keyboard.getChildAt(symbol / columns) as ViewGroup)
            .getChildAt(symbol % columns) as VirtualKeyboardButtonView
    }

    private fun screenshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        val outputDirectory = InstrumentationRegistry.getArguments().getString("additionalTestOutputDir")
        val directory = File(
            outputDirectory?.let(::File) ?: instrumentation.targetContext.getExternalFilesDir(null),
            "issue-104"
        )
        check(directory.isDirectory || directory.mkdirs())
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        File(directory, "$name-rotation$rotation.png").outputStream().use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        bitmap.recycle()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}-rotation{1}")
        fun variants(): List<Array<Any>> = SudokuTypes.entries.flatMap { type ->
            listOf(UiAutomation.ROTATION_FREEZE_0, UiAutomation.ROTATION_FREEZE_90)
                .map { rotation -> arrayOf<Any>(type, rotation) }
        }

        @JvmStatic
        @AfterClass
        fun restoreRotation() {
            InstrumentationRegistry.getInstrumentation().uiAutomation.setRotation(UiAutomation.ROTATION_UNFREEZE)
        }

        @JvmStatic
        @BeforeClass
        fun initializeTemplates() {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            val preferences = instrumentation.targetContext.getSharedPreferences("Prefs", 0)
            if (preferences.getInt("version_code", 0) == BuildConfig.VERSION_CODE) return

            ActivityScenario.launch(SplashActivity::class.java).use {
                val deadline = SystemClock.uptimeMillis() + 30_000
                while (preferences.getInt("version_code", 0) != BuildConfig.VERSION_CODE) {
                    check(SystemClock.uptimeMillis() < deadline) { "Template initialization timed out" }
                    SystemClock.sleep(100)
                }
            }
        }
    }
}
