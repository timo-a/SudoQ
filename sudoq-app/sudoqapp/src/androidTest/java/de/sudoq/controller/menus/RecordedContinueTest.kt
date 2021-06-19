package de.sudoq.controller.menus


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import de.sudoq.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RecordedContinueTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun recordedContinueTest() {
        val appCompatButton = onView(
allOf(withId(R.id.button_mainmenu_new_sudoku), withText("New Sudoku"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
2)))
        appCompatButton.perform(scrollTo(), click())
        
        val appCompatButton2 = onView(
allOf(withId(R.id.button_start), withText("Start"),
childAtPosition(
childAtPosition(
withClassName(`is`("android.widget.ScrollView")),
0),
7)))
        appCompatButton2.perform(scrollTo(), click())
        
        pressBack()
        
        pressBack()
        
        val button = onView(
allOf(withId(R.id.button_mainmenu_continue), withText("CONTINUE"),
withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
isDisplayed()))
        button.check(matches(isDisplayed()))
        }
    
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
    }
