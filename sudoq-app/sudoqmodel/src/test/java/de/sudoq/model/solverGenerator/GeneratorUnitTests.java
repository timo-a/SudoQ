package de.sudoq.model.solverGenerator;

import org.junit.jupiter.api.Test;

import java.util.List;

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GeneratorUnitTests implements GeneratorCallback {

	//this is a dummy so it compiles todo use xmls from resources
	//private IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo();

	/*@Test
	public void testNull() {
		assertFalse(new Generator(sudokuTypeRepo).generate(null, Complexity.arbitrary, this));
		assertFalse(new Generator(sudokuTypeRepo).generate(SudokuTypes.standard9x9, null, this));
		assertFalse(new Generator(sudokuTypeRepo).generate(SudokuTypes.standard9x9, Complexity.arbitrary, null));
	}*/

	@Override
	public void generationFinished(Sudoku sudoku) {
	}

	@Override
	public void generationFinished(Sudoku sudoku, List<Solution> sl) {
	}

}
