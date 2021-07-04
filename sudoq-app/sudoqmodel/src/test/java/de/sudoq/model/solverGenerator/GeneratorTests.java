package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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

public class GeneratorTests implements GeneratorCallback {

	private Generator generator;
	private static File sudokuDir  = new File(Utility.RES + File.separator + "tmp_suds");

	@BeforeClass
	public static void init() throws IOException {
		Utility.copySudokus();
		//Profile.Companion.getInstance();
	}

	@AfterClass
	public static void clean() throws IOException, SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
        java.lang.reflect.Field f = FileManager.class.getDeclaredField("profiles");
        f.setAccessible(true);
        f.set(null, null);
        java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
        s.setAccessible(true);
        s.set(null, null);
        java.lang.reflect.Field p = Profile.class.getDeclaredField("instance");
        p.setAccessible(true);
        p.set(null, null);
        Utility.deleteDir(Utility.profiles);
        Utility.deleteDir(Utility.sudokus);
    }
	@Before
	public void beforeTest() {
		TypeBuilder.get99();
		generator = new Generator(sudokuDir);
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

	@Test
	public void testGenerationDeb() {
		Random rnd = new Random(0);
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.standard4x4, Complexity.infernal, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("4 done");
	}



	//@Test(timeout = 10 * 60 * 1000)
	public void testGenerationSamurai(){
		//TODO fix this

		//159145199318451
		Random rnd = new Random(159145199318451l);
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.samurai, Complexity.difficult, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("samurai done");

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
