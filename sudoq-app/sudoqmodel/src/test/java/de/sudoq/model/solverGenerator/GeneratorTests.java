package de.sudoq.model.solverGenerator;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.transformations.Transformer;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class GeneratorTests implements GeneratorCallback {

	private Generator generator;

	@Before
	public void beforeTest() {
		TypeBuilder.get99();
		generator = new Generator();
	}

	@Override
	public synchronized void generationFinished(Sudoku sudoku) {
		assertEquals(new Solver(sudoku).validate(null, false), ComplexityRelation.CONSTRAINT_SATURATION);
		this.notifyAll();
	}

	// Empirischer Test durch mehrfaches Generieren
	@Test
	public void testGeneration() {
		Random rnd = new Random(0);
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
		//TODO fix this
		/*generator.setRandom(rnd);
		Transformer.setRandom(rnd);
		generator.generate(SudokuTypes.samurai, Complexity.easy, this);
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		
		System.out.println("samurai done");
		
	}
}
