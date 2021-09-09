package de.sudoq.playaround;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class MyClass {

	public static void main(String[] args) {

		evaluate(); System.exit(0);

		//generateNewSudokus(); System.exit(0);

		GenerateSomeSudokus gss = new GenerateSomeSudokus();
		gss.setup();

		System.out.println(EvaluateAssets.displayHelperScores());


//		System.out.println("end\n"+gss.getSudoku().getComplexityValue());
		System.out.println("I'm done");
		//gss.printDebugMsg();
		//gss.getSudoku().


	}


	public static void evaluate(){
		//contains sudokuType files
		String oldSudokus = "/home/t/Code/SudoQ/sudoq-app/sudoqapp/src/main/assets/sudokus/";
		//contains sudokus to evaluate
		String newSudokus = "/home/t/Code/SudoQ/utilities/sudoku_generation/new_generated/";
		EvaluateAssets.setup(oldSudokus,
				             "/home/t/Code/SudoQ/DebugOnPC/profilefiles",
				             newSudokus);
		//eval old ones
		/*EvaluateAssets.setup(oldSudokus,
				             "/home/t/Code/SudoQ/DebugOnPC/profilefiles",
				             oldSudokus);*/
		//EvaluateAssets.evaluateAssets();


	}

	public static void generateNewSudokus(){

		GenerateSomeSudokus gss = new GenerateSomeSudokus();
		gss.setup();

		//samurai
		//gss.setSeed(180631319407507l); //abgebrochen
		gss.setSeed(180631319407540l);
		gss.setSeed(79061170708369l);
		//gss.setSeed(93461930395355l); dlx1 zu lang
		//gss.setSeed(211352950792173l);//braucht seehr lange

		//gss.setSeed(112060279460122l); //mit dlx 2h kein ergebnis


		//gss.setSeed(203930232449646l); nullpointerException im Eval

		SudokuTypes st;

		//st = SudokuTypes.standard4x4;
		//st = SudokuTypes.standard6x6;
		//st = SudokuTypes.standard9x9;

		st = SudokuTypes.Xsudoku;
		//st = SudokuTypes.squigglya;  //TODO `difficult` needs to be more difficult
		//st = SudokuTypes.squigglyb;  //TODO `difficult` needs to be more difficult

		/*
		*  172267580913542 ist seed nachdem sudokus 1 bis 8 mit dlx2 erzeugt wurden
		*
		*
		*
		* */
		//st = SudokuTypes.standard16x16; // geht nicht
		// 180631319407500 ist seed bei dlx1 nach generating sudoku7
		// scheint zu klappen mit fast (nochmal prüfen!!!)
        //
		// nächste endlosschleife:
		//
		//for (SudokuTypes st: SudokuTypes.values()) {
			System.out.println(st);
		for (Complexity c : Complexity.playableValues()){
		//for (Complexity c : new Complexity[]{/*Complexity.difficult, */Complexity.infernal}){
		//for (Complexity c : new Complexity[]{Complexity.difficult}){
				System.out.println(" " + c + " ------------------");
				for (int i = 0; i < 30; i++){
					System.out.println("Generating Sudoku "+i+ " - " + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
					gss.generate(c, st, 1);
					//System.out.println(EvaluateAssets.evaluateSudoku(gss.getSudoku()));
					gss.saveSudoku("/home/t/Code/SudoQ/utilities/sudoku_generation/new_generated/");
					System.out.println(getSeed(gss.getRandom()));

				}
			}
		//}
	}








	private static long getSeed(Random r){

		try
		{
		    Field field = Random.class.getDeclaredField("seed");
		    field.setAccessible(true);
		    AtomicLong scrambledSeed = (AtomicLong) field.get(r);   //this needs to be XOR'd with 0x5DEECE66DL
    		return scrambledSeed.get() ^ 0x5DEECE66DL;
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("smth went wrong");
		    //handle exception
		}
	}


}
