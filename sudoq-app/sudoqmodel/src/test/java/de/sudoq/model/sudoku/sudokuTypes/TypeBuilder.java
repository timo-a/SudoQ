package de.sudoq.model.sudoku.sudokuTypes;

import java.io.File;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;

public class TypeBuilder {

	private static File profiles = new File(Utility.RES + File.separator + "tmp_profiles");
	private static File sudokus  = new File(Utility.RES + File.separator + "tmp_suds");

	public static SudokuType getType(SudokuTypes st){

		profiles.mkdirs();

		FileManager.initialize(profiles, sudokus);

		return SudokuType.getSudokuType(st);
	}
	
	public static SudokuType get99(){

		profiles.mkdirs();

		FileManager.initialize(profiles, sudokus);

		return SudokuType.getSudokuType(SudokuTypes.standard9x9);
	}


}
