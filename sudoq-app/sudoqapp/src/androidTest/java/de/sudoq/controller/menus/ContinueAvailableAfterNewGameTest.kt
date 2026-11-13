package de.sudoq.controller.menus

import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.sudoq.BuildConfig
import de.sudoq.R
import de.sudoq.model.profile.ProfileManager
import org.hamcrest.core.IsNot.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContinueAvailableAfterNewGameTest {

    @Test
    fun testNewGameEnablesContinue(){
        initializeTemplates()

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            // Establish the initial state independently of other tests or saved games.
            scenario.onActivity { it.profile.currentGame = ProfileManager.NO_GAME }
            scenario.recreate()

            onView(withId(R.id.button_mainmenu_continue)).check(matches(not(isEnabled())))
            onView(withId(R.id.button_mainmenu_new_sudoku)).perform(scrollTo(), click())
            onView(withId(R.id.button_start)).perform(scrollTo(), click())
            Espresso.pressBack() // Sudoku -> new-game preferences
            Espresso.pressBack() // Preferences -> main menu
            onView(withId(R.id.button_mainmenu_continue)).check(matches(isEnabled()))
        }
    }

    private fun initializeTemplates() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences("Prefs", 0)
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
