package de.sudoq.test;

import android.widget.LinearLayout;
import de.sudoq.R;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.sudoku.Position;
import de.sudoq.view.SudokuCellView;
import de.sudoq.view.VirtualKeyboardButtonView;
import de.sudoq.view.VirtualKeyboardLayout;

public class SudokuUtilities {

	public static SudokuCellView[][] getViewArray(SudokuActivity a) {
		Position dim = a.getGame().getSudoku().getSudokuType().getSize();
		SudokuCellView[][] oldViews = a.getSudokuLayout().getSudokuFieldViews();
		SudokuCellView[][] views = new SudokuCellView[dim.getX()][dim.getY()];
		for (int i = 0; i < dim.getX(); i++) {
			for (int j = 0; j < dim.getY(); j++) {
				views[i][j] = oldViews[i][j];
			}
		}
		return views;
	}
	
	/**
	 * Gets a Button of the VirtualKeyboard with the given symbol. It expects a real value and the transformation to abstract is done automatically.
	 * 
	 * @param a The current {@link SudokuActivity}
	 * @param value The value, of the Button (Not the abstract symbol!)
	 * @return The View of the {@link VirtualKeyboardButtonView}
	 */
	public static VirtualKeyboardButtonView getKeyboardButton(SudokuActivity a, int value) {
		if (value > 0) {
			VirtualKeyboardLayout view = (VirtualKeyboardLayout) a.findViewById(R.id.virtual_keyboard);
			int columns = (int) Math.ceil(Math.sqrt(a.getGame().getSudoku().getSudokuType().getNumberOfSymbols()));
			LinearLayout linearLayout = (LinearLayout) view.getChildAt((value - 1) / columns);
			VirtualKeyboardButtonView virtualKeyboardButtonView = (VirtualKeyboardButtonView) linearLayout.getChildAt((value - 1) % columns);
			return virtualKeyboardButtonView;
		} else {
			return null;
		}
	}
}
