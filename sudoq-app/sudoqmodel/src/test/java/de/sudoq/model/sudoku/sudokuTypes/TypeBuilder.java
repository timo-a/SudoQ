package de.sudoq.model.sudoku.sudokuTypes;

import java.io.File;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;

public class TypeBuilder {

	private static File sudokus  = new File(Utility.RES + File.separator + "tmp_suds");

	public static SudokuType getType(SudokuTypes st){

		FileManager.initialize(sudokus);

		return SudokuTypeProvider.getSudokuType(st, sudokus);
	}
	
	public static SudokuType get99(){

		FileManager.initialize(sudokus);

		return SudokuTypeProvider.getSudokuType(SudokuTypes.standard9x9, sudokus);
	}


}
