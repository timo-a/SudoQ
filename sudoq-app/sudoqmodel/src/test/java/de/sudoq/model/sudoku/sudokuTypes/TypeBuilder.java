package de.sudoq.model.sudoku.sudokuTypes;

import java.io.File;

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;

public class TypeBuilder {

	//todo can we use xmls from main/resources?
    private static final IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo4Tests();

	public static SudokuType getType(SudokuTypes st){
		return SudokuTypeProvider.getSudokuType(st, sudokuTypeRepo);
	}
	
	public static SudokuType get99(){
		return SudokuTypeProvider.getSudokuType(SudokuTypes.standard9x9, sudokuTypeRepo);
	}

}
