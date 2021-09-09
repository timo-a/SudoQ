package de.sudoq.controller.menus

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.sudoq.R
import org.hamcrest.core.IsNot.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContinueAvailableAfterNewGameTest {

    @get:Rule var activityScenarioRule = activityScenarioRule<MainActivity>()

    @Test
    fun testNewGameEnablesContinue(){
        //doesn't work yet...

        //ensure 'continue' is not enabled yet. (because we assume no games in the beginning)
        onView(withId(R.id.button_mainmenu_continue)).check(matches(not(isEnabled())))
        onView(withId(R.id.button_mainmenu_new_sudoku)).perform(click())
        onView(withId(R.id.button_start)).perform(click())
        Espresso.pressBack()
        onView(withId(R.id.button_mainmenu_continue)).check(matches(isEnabled()))

    }

}