package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.List;

import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GeneratorUnitTests implements GeneratorCallback {

	@Test
	public void testNull() {
		assertFalse(new Generator().generate(null, Complexity.arbitrary, this));
		assertFalse(new Generator().generate(SudokuTypes.standard9x9, null, this));
		assertFalse(new Generator().generate(SudokuTypes.standard9x9, Complexity.arbitrary, null));
	}

	@Override
	public void generationFinished(Sudoku sudoku) {
	}

	@Override
	public void generationFinished(Sudoku sudoku, List<Solution> sl) {
	}

}
