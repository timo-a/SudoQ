package de.sudoq.test;

import android.app.Activity;
import de.sudoq.R;
import de.sudoq.controller.menus.MainActivity;
import de.sudoq.controller.menus.NewSudokuConfigurationActivity;
import de.sudoq.controller.sudoku.SudokuActivity;

public class CrossActivityTests extends SudoqTestCase {

	// AT10
	public void testSudokuStarting() {
		Activity a = getActivity();

		String continueSudoku = a.getString(R.string.sf_mainmenu_continue);
		String newSudoku = a.getString(R.string.sf_mainmenu_new_sudoku);

		/*
		 * assert: Button "new Sudoku" exists
		 *         Button "continue" exists, but not clickable
		 */
		assertTrue(solo.searchText(continueSudoku));
		assertTrue(solo.searchText(newSudoku));
		assertFalse(solo.getButton(continueSudoku).isEnabled());

		/*
		 * start a new Sudoku(easy 9x9):
		 * assert we're in the right activity                
		 */
		solo.clickOnText(newSudoku);
		solo.assertCurrentActivity("should be sudokupreferences", NewSudokuConfigurationActivity.class);
		assertTrue(solo.searchText(a.getString(R.string.complexity_easy)));
		assertTrue(solo.searchText(a.getString(R.string.sudoku_type_standard_9x9)));
		assertTrue(solo.searchText(a.getString(R.string.sf_sudokupreferences_start)));

		solo.clickOnText(a.getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);

		/*
		 * go out to main again
		 */
		solo.goBack();
		solo.sleep(1000);//wait 1s
		solo.goBack();
		
		boolean arrive = solo.waitForActivity("MainActivity",60000);//wait 1min for emulator to load MainActivity
		
		assertTrue("waiting for activity takes too long...", arrive);
		solo.sleep(500);
		
		solo.assertCurrentActivity("should be mainactivity", MainActivity.class);

		/*
		 * assert continue is enabled and leads to right activity
		 */
		assertTrue(solo.getButton(continueSudoku).isEnabled());
		solo.clickOnText(continueSudoku);
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);
	}
}
