package de.sudoq.test;

import android.app.Activity;
import de.sudoq.R;
import de.sudoq.controller.menus.NewSudokuActivity;
import de.sudoq.controller.sudoku.SudokuActivity;

/**
 * this class represents the Scenario 7.1.1 (normaler Ablauf)
 */
public class NormalFlowTest extends SudoqTestCase {

	// szenario 7.1.1 (normaler ablauf)
	// test just runs on 2.1, at least in 2.3 solo.scrollUp() does the opposite.
	public void testStartTheGame() {
		Activity a = getActivity();

		solo.clickOnText(a.getString(R.string.sf_mainmenu_new_sudoku));
		solo.assertCurrentActivity("should be sudokupreferences", NewSudokuActivity.class);
		assertTrue(solo.searchText(a.getString(R.string.complexity_easy)));
		assertTrue(solo.searchText(a.getString(R.string.sudoku_type_standard_9x9)));
		assertTrue(solo.searchText(a.getString(R.string.sf_sudokupreferences_start)));

		// change the typ to 16x16 and back
		solo.clickOnText(a.getString(R.string.sudoku_type_standard_9x9));
		solo.clickOnText(a.getString(R.string.sudoku_type_standard_16x16));
		// Robotium cant scroll up - WTF?
		// solo.clickOnText(a.getString(R.string.sudoku_type_standard_16x16));
		// while (solo.scrollUp()) {
		// }
		// solo.clickOnText(a.getString(R.string.sudoku_type_standard_9x9));

		// start the game
		solo.clickOnText(a.getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudokuactivity", SudokuActivity.class);

		// actually the user should solve some cells by himself right here, but
		// thats not possible i guess

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_surrender));

		// statistik wird angezeigt
		assertTrue(solo.searchText(a.getString(R.string.dialog_surrender_title)));
		assertTrue(solo.searchText(a.getString(R.string.dialog_won_timeneeded)));
		assertTrue(solo.searchText(a.getString(R.string.dialog_won_score)));

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.dialog_yes));
	}
}
