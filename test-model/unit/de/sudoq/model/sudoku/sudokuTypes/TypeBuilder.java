package de.sudoq.model.sudoku.sudokuTypes;

import java.io.File;

import de.sudoq.model.files.FileManager;

public class TypeBuilder {

	public static SudokuType getType(SudokuTypes st){

		File profiles = new File("res" + File.separator + "tmp_profiles");
		profiles.mkdirs();

		File sudokus = new File("res" + File.separator + "sudokus");

		FileManager.initialize(profiles, sudokus);

		return SudokuType.getSudokuType(st);
	}
	
	public static SudokuType get99(){

		File profiles = new File("res" + File.separator + "tmp_profiles");
		profiles.mkdirs();

		File sudokus = new File("res" + File.separator + "sudokus");

		FileManager.initialize(profiles, sudokus);

		return SudokuType.getSudokuType(SudokuTypes.standard9x9);
	}


}
