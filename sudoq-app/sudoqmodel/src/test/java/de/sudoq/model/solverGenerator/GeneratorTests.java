package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.sudoq.model.Utility;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.utility.FileManager;
import de.sudoq.model.persistence.IRepo;
import de.sudoq.model.profile.ProfileSingleton;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.transformations.Transformer;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
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
        java.lang.reflect.Field s = FileManager.class.getDeclaredField("sudokus");
        s.setAccessible(true);
        s.set(null, null);
        java.lang.reflect.Field p = ProfileSingleton.class.getDeclaredField("instance");
        p.setAccessible(true);
        p.set(null, null);
        //Utility.deleteDir(Utility.profiles);
        Utility.deleteDir(Utility.sudokus);
    }
	@Before
	public void beforeTest() {
		TypeBuilder.get99();
		IRepo<SudokuType> dummySoItCompiles = new IRepo<SudokuType>() {
			@NotNull
			@Override
			public List<Integer> ids() {
				throw new NotImplementedException();
			}

			@Override
			public SudokuType create() {
				return null;
			}

			@Override
			public SudokuType read(int id) {
				return null;
			}

			@Override
			public SudokuType update(SudokuType SudokuType) {
				return null;
			}

			@Override
			public void delete(int id) {

			}
		};
		generator = new Generator(dummySoItCompiles/*sudokuDir*/);
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

	//@Test todo fix this
	public void testGenerationDebug() throws ExecutionException, InterruptedException {
        Generator generator = new Generator(new SudokuTypeRepo4Tests());
		Random rnd = new Random(0);
		generator.setRandom(rnd);
		Transformer.setRandom(rnd);
        CompletableFuture<Sudoku> future = new CompletableFuture<>();
        GeneratorCallback gc = new GeneratorCallback() {
            @Override
            public void generationFinished(Sudoku sudoku) {
                future.complete(sudoku);
            }

            @Override
            public void generationFinished(Sudoku sudoku, List<Solution> sl) {
                future.complete(sudoku);
            }
        };

        generator.generate(SudokuTypes.standard4x4, Complexity.infernal, gc);
        assertTimeoutPreemptively(Duration.ofSeconds(60), () -> {
            future.get();
        });
        Sudoku sudoku = future.get();
        assertNotNull(sudoku);
        //assertEquals(new Solver(sudoku).validate(null), ComplexityRelation.CONSTRAINT_SATURATION);
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
