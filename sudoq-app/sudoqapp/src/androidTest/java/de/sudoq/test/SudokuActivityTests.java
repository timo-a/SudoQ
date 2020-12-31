package de.sudoq.test;

import android.view.View;
import de.sudoq.R;
import de.sudoq.controller.menus.NewSudokuActivity;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.view.SudokuCellView;

public class SudokuActivityTests extends SudoqTestCase {

	// AT20
	public void testSudokuSolveHelp() {
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_mainmenu_new_sudoku));
		solo.assertCurrentActivity("should be sudokupreferences", NewSudokuActivity.class);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.sleep(500);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_surrender));
		solo.sleep(13000);
	//	assertEquals(true, ((SudokuActivity) solo.getCurrentActivity()).getGame().isFinished());
	}

	// AT 40
	public void testSudokuTime() {
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_mainmenu_new_sudoku));
		solo.assertCurrentActivity("should be sudokupreferences", NewSudokuActivity.class);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.sleep(500);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.sleep(500);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));

		solo.goBack();
		solo.sleep(500);
		solo.goBack();

		solo.sleep(120000);

		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_mainmenu_continue));

		View helpButton =solo.getView(R.id.button_sudoku_help); 
		assertNotNull(helpButton);
		solo.clickOnView(helpButton);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_random));
		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_solve_surrender));

/*		int gameTime = ((SudokuActivity) solo.getCurrentActivity()).getGame().getTime();

		Log.v("Game Time", String.valueOf(gameTime));
		assertEquals(true, gameTime < 20000);*/
	}

	// AT80
	public void testIsValidToast() {
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_mainmenu_new_sudoku));
		solo.assertCurrentActivity("should be sudokupreferences", NewSudokuActivity.class);
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudokupreferences_start));
		solo.assertCurrentActivity("should be in sudoku", SudokuActivity.class);

		SudokuActivity sudokuActivity = (SudokuActivity) solo.getCurrentActivity();
		SudokuCellView[][] views = SudokuUtilities.getViewArray(sudokuActivity);

		boolean found = false;
		for (int row = 0; row < views.length; row++) {
			for (int colum = 0; colum < views[row].length - 1; colum++) {
				if (views[row][colum].getCell().isNotSolved() && views[row][colum + 1].getCell().isNotSolved()) {
					solo.clickOnView(views[row][colum]);
					solo.clickOnView(SudokuUtilities.getKeyboardButton(sudokuActivity, 1));
					solo.clickOnView(views[row][colum + 1]);
					solo.clickOnView(SudokuUtilities.getKeyboardButton(sudokuActivity, 1));
					found = true;
					break;
				}
			}

			if (found) {
				break;
			}
		}

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.button_sudoku_help));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.sf_sudoku_assistances_check));
		assertTrue(solo.waitForText(solo.getCurrentActivity().getString(R.string.toast_solved_wrong)));
	}
}
