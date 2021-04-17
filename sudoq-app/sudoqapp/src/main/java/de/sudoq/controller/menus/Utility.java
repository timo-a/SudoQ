package de.sudoq.controller.menus;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.sudoq.R;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class Utility {

	public static <T extends Enum> String enum2String(String[] typeStrings, T e){
		int index = e.ordinal();
		return index >= typeStrings.length ? null : typeStrings[index];
	}



	/* SudokuTypes */
	private static String[] getSudokuTypeValues(Context context){//we need a method because of the context...
		String[] typeStrings = new String[SudokuTypes.values().length];
		typeStrings[SudokuTypes.standard4x4.  ordinal()] = context.getString(R.string.advanced_settings_restrict_item_standard_4x4);
		typeStrings[SudokuTypes.standard6x6.  ordinal()] = context.getString(R.string.advanced_settings_restrict_item_standard_6x6);
		typeStrings[SudokuTypes.standard9x9.  ordinal()] = context.getString(R.string.advanced_settings_restrict_item_standard_9x9);
		typeStrings[SudokuTypes.standard16x16.ordinal()] = context.getString(R.string.advanced_settings_restrict_item_standard_16x16);
		typeStrings[SudokuTypes.Xsudoku.      ordinal()] = context.getString(R.string.advanced_settings_restrict_item_xsudoku);
		typeStrings[SudokuTypes.HyperSudoku.  ordinal()] = context.getString(R.string.advanced_settings_restrict_item_hyper);
		typeStrings[SudokuTypes.stairstep.    ordinal()] = context.getString(R.string.advanced_settings_restrict_item_stairstep_9x9);
		typeStrings[SudokuTypes.squigglya.    ordinal()] = context.getString(R.string.advanced_settings_restrict_item_squiggly_a_9x9);
		typeStrings[SudokuTypes.squigglyb.    ordinal()] = context.getString(R.string.advanced_settings_restrict_item_squiggly_b_9x9);
		typeStrings[SudokuTypes.samurai.      ordinal()] = context.getString(R.string.advanced_settings_restrict_item_samurai);
		return typeStrings;
	}

	public static String type2string(Context context, SudokuTypes st){
		String[] typeStrings = getSudokuTypeValues(context);
		int index = st.ordinal(); 
		return index >= typeStrings.length ? null : typeStrings[index];
	}


	
	/* Complexities */
	private static String[] getComplexityValues(Context context){
		String[] typeStrings = new String[Complexity.values().length];
		typeStrings[Complexity.easy.     ordinal()] = context.getString(R.string.complexity_easy);
		typeStrings[Complexity.medium.   ordinal()] = context.getString(R.string.complexity_medium);
		typeStrings[Complexity.difficult.ordinal()] = context.getString(R.string.complexity_difficult);
		typeStrings[Complexity.infernal. ordinal()] = context.getString(R.string.complexity_infernal);
		typeStrings[Complexity.arbitrary.ordinal()] = "error";//only for compatibility. There's no reason to convert 'arbitrary' to a string 
		return typeStrings;
	}

	public static Complexity string2complexity(Context context, String string){
		String[] complexityStrings = getComplexityValues(context);
		for(int i=0; i<complexityStrings.length; i++)
			if(string.equals(complexityStrings[i])) 
				return Complexity.values()[i];
		return null;
	}

	public static String complexity2string(Context context, Complexity st){
		String[] complexityStrings = getComplexityValues(context);
		int index = st.ordinal(); 
		return index >= complexityStrings.length ? null : complexityStrings[index];
	}


	/* Shapes */
	private static String[] getConstraintShapeValuesAccusativeDetermined(Context context){
		String[] typeStrings = new String[Utils.ConstraintShape.values().length];
		typeStrings[ConstraintShape.Row     .ordinal()] = context.getString(R.string.constraintshape_row_accusative_determined);
		typeStrings[ConstraintShape.Column  .ordinal()] = context.getString(R.string.constraintshape_column_accusative_determined);
		typeStrings[ConstraintShape.Diagonal.ordinal()] = context.getString(R.string.constraintshape_diagonal_accusative_determined);
		typeStrings[ConstraintShape.Block   .ordinal()] = context.getString(R.string.constraintshape_block_accusative_determined);
		return typeStrings;
	}

	private static String[] getConstraintShapeValuesGenitiveDetermined(Context context){
		String[] typeStrings = new String[Utils.ConstraintShape.values().length];
		typeStrings[ConstraintShape.Row     .ordinal()] = context.getString(R.string.constraintshape_row_genitive_determined);
		typeStrings[ConstraintShape.Column  .ordinal()] = context.getString(R.string.constraintshape_column_genitive_determined);
		typeStrings[ConstraintShape.Diagonal.ordinal()] = context.getString(R.string.constraintshape_diagonal_genitive_determined);
		typeStrings[ConstraintShape.Block   .ordinal()] = context.getString(R.string.constraintshape_block_genitive_determined);
		return typeStrings;
	}


	/** returns shape in accusative e.g. look at the row
	 * @param context Context(to access localized strings)
	 * @return a string representation of the constraint shape passed as enum.
	 * */
	public static String constraintShapeAccDet2string(Context context, ConstraintShape cs){
		String[] shapeStrings = getConstraintShapeValuesAccusativeDetermined(context);
		return enum2String(shapeStrings, cs);
	}

	public static String constraintShapeGenDet2string(Context context, ConstraintShape cs){
		String[] shapeStrings = getConstraintShapeValuesGenitiveDetermined(context);
		return enum2String(shapeStrings, cs);
	}

	/*  */
	private static String[] getConstraintShapeGender(Context context){
		return context.getResources().getStringArray(R.array.shape_gender_values);
	}

	public static String getGender(Context context, ConstraintShape cs){ return enum2String(getConstraintShapeGender(context), cs); }

	public static String gender2AccDeterminer(Context context, String gender){
		switch (gender){
			case "m":	return context.getString(R.string.maskuline_accusative_determiner);
			case "f":	return context.getString(R.string. feminine_accusative_determiner);
			case "n":	return context.getString(R.string.  neutral_accusative_determiner);
			default:    return null;
		}
	}

	public static String gender2AccSufix(Context context, String gender){
		switch (gender){
			case "m":	return context.getString(R.string.maskuline_accusative_suffix);
			case "f":	return context.getString(R.string. feminine_accusative_suffix);
			case "n":	return context.getString(R.string.  neutral_accusative_suffix);
			default:    return null;
		}
	}

	public static String gender2inThe(Context context, String gender){
		return parseStringArray(context,gender.charAt(0), R.array.gender2in_the);
	}

//http://stackoverflow.com/questions/3013655/creating-hashmap-map-from-xml-resources
public static String parseStringArray(Context context, char gender, int stringArrayResourceId) {
    String[] stringArray = context.getResources().getStringArray(stringArrayResourceId);
    for (String entry : stringArray)
        if(entry.charAt(0)==gender)
			return entry.substring(2);

    return null;
}


	/**
	 * Kopiert die Dateien zwischen den angegeben Streams
	 *
	 * @param in
	 *            Der Eingabestream
	 * @param out
	 *            Der Ausgabestream
	 * @throws IOException
	 *             Wird geworfen, falls beim Lesen/Schreiben der Streams ein
	 *             Fehler auftritt
	 */
	public static void copyFileOnStreamLevel(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}


}
