package de.sudoq.model.solverGenerator.solver;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

public final class SudokuTestUtilities {

	private SudokuTestUtilities() {

	}

	public static void printSudoku(Sudoku sudoku) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < sudoku.getSudokuType().getSize().getY(); j++) {
			for (int i = 0; i < sudoku.getSudokuType().getSize().getX(); i++) {
				int value = sudoku.getCell(Position.get(i, j)).getCurrentValue();
				String op = value + "";
				if (String.valueOf(value).length() < 2)
					op = " " + value;
				if (value == -1)
					op = "--";
				sb.append(op + ", ");
			}
			sb.append("\n");
		}
		System.out.println(sb);
	}
}
