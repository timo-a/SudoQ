package de.sudoq.model.sudoku.sudokuTypes;

import java.io.File;

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo2;

public class TypeBuilder {

	//this is a dummy so it compiles todo use xmls from resources
	private static IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo2(new File("persistence", "SudokuTypes"));//todo use mocks = new SudokuTypeRepo();

	public static SudokuType getType(SudokuTypes st){
		return SudokuTypeProvider.getSudokuType(st, sudokuTypeRepo);
	}
	
	public static SudokuType get99(){
		return SudokuTypeProvider.getSudokuType(SudokuTypes.standard9x9, sudokuTypeRepo);
	}

}
