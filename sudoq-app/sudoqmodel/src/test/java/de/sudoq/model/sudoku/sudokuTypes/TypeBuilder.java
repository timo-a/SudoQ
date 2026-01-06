package de.sudoq.model.sudoku.sudokuTypes;

import de.sudoq.model.ports.persistence.ReadRepo;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;

public class TypeBuilder {

	//todo can we use xmls from main/resources?
    private static final ReadRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo4Tests();

	public static SudokuType getType(SudokuTypes st){
		return SudokuTypeProvider.getSudokuType(st, sudokuTypeRepo);
	}
	
	public static SudokuType get99(){
		return SudokuTypeProvider.getSudokuType(SudokuTypes.standard9x9, sudokuTypeRepo);
	}

}
