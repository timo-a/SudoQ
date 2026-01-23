package de.sudoq.model.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.sudoq.model.ports.persistence.ReadRepo;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

class SudokuBuilderTests {

	private final ReadRepo<SudokuType> str = new SudokuTypeRepo4Tests();

	Cell cell;

    @Test
    void initialisation() {
		for (SudokuTypes t : SudokuTypes.values()) {
            testBuildergeneric(t);
		}
	}

	private void testBuildergeneric(SudokuTypes t) {
		Sudoku sudoku = new SudokuBuilder(t, str).createSudoku();
		for (Position pos : sudoku.getSudokuType().getValidPositions()) {
            cell = sudoku.getCell(pos);
            if (cell != null)
                assertEquals(Cell.EMPTYVAL, cell.getCurrentValue());
		}
	}

    @Test
    void builderWithSolutions() {
		SudokuBuilder sb = new SudokuBuilder(SudokuTypes.standard9x9, str);
		sb.addSolution(Position.get(0, 0), 5);
		sb.setFixed(Position.get(0, 0));
		sb.addSolution(Position.get(0, 1), 3);
		Sudoku s = sb.createSudoku();

		assertEquals(5, s.getCell(Position.get(0, 0)).getSolution());
		assertEquals(5, s.getCell(Position.get(0, 0)).getCurrentValue());
		assertEquals(3, s.getCell(Position.get(0, 1)).getSolution());
		assertEquals(Cell.EMPTYVAL, s.getCell(Position.get(0, 1)).getCurrentValue());

		try {
			sb.addSolution(Position.get(1, 3), -5);
			fail("no exception");
		} catch (IllegalArgumentException e) {
			// great
		}
		try {
			sb.addSolution(Position.get(1, 3), 9);
			fail("no exception");
		} catch (IllegalArgumentException e) {
			// great
		}
	}

}