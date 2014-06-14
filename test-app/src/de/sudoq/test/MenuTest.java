package de.sudoq.test;

import android.app.Activity;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

import de.sudoq.R;
import de.sudoq.controller.menus.MainActivity;
import de.sudoq.controller.menus.PlayerPreferencesActivity;
import de.sudoq.controller.menus.StatisticsActivity;
import de.sudoq.controller.menus.SudokuLoadingActivity;
import de.sudoq.controller.menus.SudokuPreferencesActivity;
import de.sudoq.controller.sudoku.SudokuActivity;

public class MenuTest extends SudoqTestCase {

	public void testMenus() {
		Log.d("debug", "mark0");
		Activity a = getActivity();

		String continueSudoku = a.getString(R.string.sf_mainmenu_continue);
		String newSudoku      = a.getString(R.string.sf_mainmenu_new_sudoku);
		String loadSudoku     = a.getString(R.string.sf_mainmenu_load_sudoku);
		String profile        = a.getString(R.string.sf_mainmenu_profile);
		String statistics     = a.getString(R.string.profile_preference_button_statistics);

		solo.sleep(2000);
		assertTrue(solo.searchText(continueSudoku));
		assertTrue(solo.searchText(newSudoku));
		assertTrue(solo.searchText(loadSudoku));
		assertTrue(solo.searchText(profile));
		
		solo.clickOnText(newSudoku);
		solo.assertCurrentActivity("should be sudokupreferences", SudokuPreferencesActivity.class);
		assertTrue(solo.searchText(a.getString(R.string.complexity_easy)));
		assertTrue(solo.searchText(a.getString(R.string.sudoku_type_standard_9x9)));
		assertTrue(solo.searchText(a.getString(R.string.sf_sudokupreferences_start)));
		solo.goBack();
		solo.assertCurrentActivity("should be mainactivity", MainActivity.class);

		solo.clickOnText(loadSudoku);
		solo.assertCurrentActivity("should be sudokuloading", SudokuLoadingActivity.class);
		solo.goBack();

		Log.d("debug", "mark1");
		solo.clickOnText(profile);
		solo.assertCurrentActivity("should be preferences", PlayerPreferencesActivity.class);

		solo.clickOnText(statistics);
		solo.assertCurrentActivity("should be statistics", StatisticsActivity.class);
		solo.goBack();
		Log.d("debug", "mark2");
		
		/* AT 140 */
		solo.sendKey(Solo.MENU);
		solo.clickOnText(a.getString(R.string.action_switch_profile));
		Log.d("debug", "mark3");
		assertTrue(solo.searchText(a.getString(R.string.action_switch_profile)));
		solo.goBack();
	}

	public void testAssistanceBeforeSudoku() {
		solo.sleep(2000);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_mainmenu_new_sudoku));
		solo.assertCurrentActivity("should be sudokupreferences", SudokuPreferencesActivity.class);

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_assistances));
		// solo.assertCurrentActivity("should be playerpreferences",
		// PlayerPreferencesActivity.class);

		// CheckBox checkBox = (CheckBox)
		// solo.getCurrentActivity().findViewById(R.id.checkbox_autoAdjustNotes);
		// solo.clickOnCheckBox(checkBox.getId());
		// solo.clickOnCheckBox(checkBox.getId());
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.profile_preference_button_save));
		solo.assertCurrentActivity("should be sudokupreferences", SudokuPreferencesActivity.class);

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);
	}

	/* AT 150 and AT 160 */
	public void testCreateProfile() {
		Activity a = getActivity();
		solo.sleep(2000);
		/* go to Profile */
		solo.clickOnText(a.getString(R.string.sf_mainmenu_profile));
		solo.assertCurrentActivity("should be preferences", PlayerPreferencesActivity.class);
		
		// create new profile
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.action_new_profile));
		
		solo.clickOnText(a.getString(R.string.profile_preference_title_gesture));
		solo.goBack();
		solo.clickOnText(a.getString(R.string.sf_mainmenu_profile));
		assertTrue(solo.isCheckBoxChecked(a.getString(R.string.profile_preference_title_gesture)));
		solo.sendKey(Solo.MENU);
		solo.clickOnText(a.getString(R.string.action_delete_profile));
		solo.goBack();
	}

	public void testSudokuLoading() {
		Activity a = solo.getCurrentActivity();

		solo.clickOnText(a.getString(R.string.sf_mainmenu_new_sudoku));
		solo.assertCurrentActivity("should be sudokupreferences", SudokuPreferencesActivity.class);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);
		solo.goBack();
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_surrender));
		solo.goBackToActivity("MainActivity");

		solo.clickOnText(a.getString(R.string.sf_mainmenu_load_sudoku));
		solo.assertCurrentActivity("should be sudokuloading", SudokuLoadingActivity.class);

		assertTrue(((SudokuLoadingActivity) solo.getCurrentActivity()).getSize() == 2);
		solo.sendKey(Solo.MENU);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sudokuloading_delete_finished));
		solo.sleep(13000);
		assertTrue(((SudokuLoadingActivity) solo.getCurrentActivity()).getSize() == 1);
	}

	public void testTutorial() {
		Activity a = solo.getCurrentActivity();
		solo.sendKey(Solo.MENU);
		solo.clickOnText(a.getString(R.string.action_show_tutorial));
		solo.clickOnText(a.getString(R.string.sf_tutorial_sudoku_title));
		solo.clickOnText(a.getString(R.string.sf_tutorial_assistances_title));
		solo.clickOnText(a.getString(R.string.sf_tutorial_action_title));
	}
}