package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.io.File;
import java.util.List;

import de.sudoq.model.Utility;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GeneratorUnitTests implements GeneratorCallback {
	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	@Test
	public void testNull() {
		assertFalse(new Generator(sudokuDir).generate(null, Complexity.arbitrary, this));
		assertFalse(new Generator(sudokuDir).generate(SudokuTypes.standard9x9, null, this));
		assertFalse(new Generator(sudokuDir).generate(SudokuTypes.standard9x9, Complexity.arbitrary, null));
	}

	@Override
	public void generationFinished(Sudoku sudoku) {
	}

	@Override
	public void generationFinished(Sudoku sudoku, List<Solution> sl) {
	}

}
