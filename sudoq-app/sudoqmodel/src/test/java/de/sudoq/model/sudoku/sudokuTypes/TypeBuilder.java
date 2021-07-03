package de.sudoq.model.sudoku.sudokuTypes;

import org.apache.commons.lang3.NotImplementedException;

import java.io.File;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.persistence.xml.sudokuType.SudokuTypeBE;

public class TypeBuilder {

	//this is a dummy so it compiles todo use xmls from resources
	private static IRepo<SudokuTypeBE> sudokuTypeRepo = new IRepo<SudokuTypeBE>() {
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

	public static SudokuType getType(SudokuTypes st){
		return SudokuTypeProvider.getSudokuType(st, sudokuTypeRepo);
	}
	
	public static SudokuType get99(){
		return SudokuTypeProvider.getSudokuType(SudokuTypes.standard9x9, sudokuTypeRepo);
	}


}
