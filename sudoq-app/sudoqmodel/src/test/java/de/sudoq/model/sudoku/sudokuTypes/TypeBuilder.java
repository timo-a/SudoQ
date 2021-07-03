package de.sudoq.model.sudoku.sudokuTypes;

import org.apache.commons.lang3.NotImplementedException;

import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.utility.persistence.sudokuType.SudokuTypeRepo;

public class TypeBuilder {

	//this is a dummy so it compiles todo use xmls from resources
	private static IRepo<SudokuType> sudokuTypeRepo = new SudokuTypeRepo();

	public static SudokuType getType(SudokuTypes st){
		return SudokuTypeProvider.getSudokuType(st, sudokuTypeRepo);
	}
	
	public static SudokuType get99(){
		return SudokuTypeProvider.getSudokuType(SudokuTypes.standard9x9, sudokuTypeRepo);
	}

}
