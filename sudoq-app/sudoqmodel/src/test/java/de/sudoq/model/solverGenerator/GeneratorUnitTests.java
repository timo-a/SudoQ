package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertFalse;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;

import java.io.File;
import java.util.List;

import de.sudoq.model.Utility;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class GeneratorUnitTests implements GeneratorCallback {

	//this is a dummy so it compiles todo use xmls from resources
	private IRepo<SudokuTypeBE> sudokuTypeRepo = new IRepo<SudokuTypeBE>() {
		@Override
		public void delete(int id) { throw new NotImplementedException(); }

		@Override
		public SudokuTypeBE update(SudokuTypeBE sudokuBE) { throw new NotImplementedException(); }

		@Override
		public SudokuTypeBE read(int id) {
			throw new NotImplementedException();
		}

		@Override
		public SudokuTypeBE create() { throw new NotImplementedException(); }

	};

	@Test
	public void testNull() {
		assertFalse(new Generator(sudokuTypeRepo).generate(null, Complexity.arbitrary, this));
		assertFalse(new Generator(sudokuTypeRepo).generate(SudokuTypes.standard9x9, null, this));
		assertFalse(new Generator(sudokuTypeRepo).generate(SudokuTypes.standard9x9, Complexity.arbitrary, null));
	}

	@Override
	public void generationFinished(Sudoku sudoku) {
	}

	@Override
	public void generationFinished(Sudoku sudoku, List<Solution> sl) {
	}

}
