package de.sudoq.playaround;

import java.util.List;
import java.util.Random;

import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.sudoku.Field;

public class MyClass {

    	public static void main(String[] args) {

		// TODO Auto-generated method stub
		System.out.println(7);
		Field f = new Field(2,3);
		System.out.println(f);
		GenerateSomeSudokus gss = new GenerateSomeSudokus();
		System.out.println("gss.gSu before:"+gss.getSudoku());
		gss.generate2();
		System.out.println("gss.gSu after: "+gss.getSudoku());
		System.out.println("gss.gSu cmplx: "+gss.getSudoku().getComplexity());
		List<Solution> solutions = gss.getSolutions();
		System.out.println("gss.gSu == null?: "+(gss.getSudoku()==null));
		System.out.println("gss.getSolutions  "+solutions);
		for(Solution s : solutions) {
			System.out.println(s);
			for(SolveDerivation sd : s.getDerivations()) {
				System.out.println(sd + " " + sd.getType());
			}}
		//gss.printDebugMsg();
		//gss.getSudoku().





	}

}
