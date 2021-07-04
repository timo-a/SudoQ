package de.sudoq.model.solverGenerator;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import de.sudoq.model.Utility;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.transformations.Transformer;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

import static org.junit.Assert.assertEquals;

public class GeneratorTestsSlow implements GeneratorCallback {

	private Generator generator;

	@BeforeClass
	public static void init() throws IOException {
		Utility.copySudokus();
		Profile.getInstance();
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
        Field f = FileManager.class.getDeclaredField("profiles");
        f.setAccessible(true);
        f.set(null, null);
        Field s = FileManager.class.getDeclaredField("sudokus");
        s.setAccessible(true);
        s.set(null, null);
        Field p = Profile.class.getDeclaredField("instance");
        p.setAccessible(true);
        p.set(null, null);
        Utility.deleteDir(Utility.profiles);
        Utility.deleteDir(Utility.sudokus);
    }
	@Before
	public void beforeTest() {
		TypeBuilder.get99();
		generator = new Generator();
	}

	@Override
	public synchronized void generationFinished(Sudoku sudoku) {
		assertEquals(new Solver(sudoku).validate(null), ComplexityRelation.CONSTRAINT_SATURATION);
		this.notifyAll();
	}

	@Override
	public synchronized void generationFinished(Sudoku sudoku, List<Solution> s) {
		assertEquals(new Solver(sudoku).validate(null), ComplexityRelation.CONSTRAINT_SATURATION);
		this.notifyAll();
	}



	// Empirischer Test durch mehrfaches Generieren
	@Test(timeout = 40*60*1000)
	public void testGeneration() {
		/* validate returns INVADILD. why?? - is this solved now? tests run through */
		Random rnd = new Random(0);
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.standard9x9, Complexity.infernal, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("9 done");
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.standard16x16, Complexity.infernal, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("16 done");
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.Xsudoku, Complexity.medium, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("X done");
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.squigglya, Complexity.infernal, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("sqA done");
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
