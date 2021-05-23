package de.sudoq.model.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.sudoq.model.Utility;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.Generator;
import de.sudoq.model.solverGenerator.GeneratorCallback;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder;

public class GameTests {

	private static Sudoku sudoku;

	@BeforeClass
	public static void beforeClass() {
		Utility.copySudokus();
		File profileDir = new File("/tmp/sudoq/GameTests/profile");
		Profile.Companion.getInstance(profileDir);

		TypeBuilder.get99(); //just to force initialization of filemanager
		
		GeneratorCallback gc = new GeneratorCallback() {
			@Override
			public void generationFinished(Sudoku sudoku) {
				GameTests.sudoku = sudoku;
			}

			@Override
			public void generationFinished(Sudoku sudoku, List<Solution> sl) {
				GameTests.sudoku = sudoku;
			}
		};
		
		new Generator().generate(SudokuTypes.standard9x9, Complexity.easy, gc);
	}

	@Test
	public void testInstanciation() {
		Game game = new Game(2, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());
		assertEquals(game.getId(), 2);
		assertNotNull(game.getStateHandler());
		assertEquals(game.getSudoku().getCell(Position.get(8, 8)).getCurrentValue(), Cell.EMPTYVAL);
		assertNull(game.getSudoku().getCell(Position.get(10, 2)));
		assertEquals(0, game.getAssistancesCost());
	}

	@Test(expected = NullPointerException.class)
	public void testNullInstantiation() {
		new Game(2, null);
	}

	@Test
	public void testGameInteraction() {
		Game game = new Game(2, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());

		Position pos = Position.get(1, 1);
		ActionTreeElement start = game.getCurrentState();
        Cell f = game.getSudoku().getCell(pos);
		game.addAndExecute(new SolveActionFactory().createAction(3, f));//setze 3
		game.addAndExecute(new SolveActionFactory().createAction(4, f));//setze 4
		assertFalse(game.isMarked(game.getCurrentState()));
		game.addAndExecute(new SolveActionFactory().createAction(5, f));//setze 5
		assertEquals(5, game.getSudoku().getCell(pos).getCurrentValue());
		game.markCurrentState();
		assertTrue(game.isMarked(game.getCurrentState()));

		game.goToState(start);
		assertEquals(Cell.EMPTYVAL, f.getCurrentValue());
		assertFalse(game.isFinished());

		game.redo();
		game.redo();
		assertEquals(5, f.getCurrentValue());//schlägt fehl
		game.undo();
		assertEquals(Cell.EMPTYVAL, f.getCurrentValue());
		game.redo();
		assertFalse(game.checkSudoku());

		game.addTime(23);
		assertEquals(game.getTime(), 23);
	}

	@Test
	public void testEquals() {
		Game game = new Game(2, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());
		assertEquals(game, game);
		Game game2 = new Game(3, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());
		assertNotEquals(game, game2);

		Position pos = Position.get(1, 1);
		ActionTreeElement start = game.getCurrentState();

		game.addAndExecute(new SolveActionFactory().createAction(3, game.getSudoku().getCell(pos)));
		game.addAndExecute(new SolveActionFactory().createAction(4, game.getSudoku().getCell(pos)));
		game.addAndExecute(new SolveActionFactory().createAction(5, game.getSudoku().getCell(pos)));
		game.markCurrentState();

		game.goToState(start);

		game.redo();
		game.redo();
		game.undo();

		assertNotEquals(game, game2);
		assertEquals(game, game);

		assertNotEquals(game, new Object());
	}

	@Test
	public void testGameXML() {
		Sudoku s = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
		s.setId(5);
		Game game = new Game(2, s);

		Position pos = Position.get(1, 1);
		ActionTreeElement start = game.getCurrentState();

		game.addAndExecute(new SolveActionFactory().createAction(3, game.getSudoku().getCell(pos)));
		game.addAndExecute(new SolveActionFactory().createAction(4, game.getSudoku().getCell(pos)));
		game.addAndExecute(new SolveActionFactory().createAction(5, game.getSudoku().getCell(pos)));
		game.addAndExecute(new NoteActionFactory().createAction(2, game.getSudoku().getCell(pos)));
		assertEquals(game.getSudoku().getCell(pos).getCurrentValue(), 5);
		game.markCurrentState();
		assertTrue(game.isMarked(game.getCurrentState()));

		game.goToState(start);

		game.redo();
		game.redo();
		game.undo();

		Game game2 = new Game();
		game2.fillFromXml(game.toXmlTree());
		assertEquals(game, game2);
	}

	// Regression Test for Issue-89
	@Test
	public void testFinishedAttributeConsistency() {
		SudokuBuilder sb = new SudokuBuilder(SudokuTypes.standard9x9);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sb.addSolution(Position.get(i, j), 1);
			}
		}
		Game game = new Game(1, sb.createSudoku());
		assertTrue(game.solveAll());
		assertTrue(game.isFinished());

		Game game2 = new Game();
		game2.fillFromXml(game.toXmlTree());
		assertTrue(game2.isFinished());

		game2.undo();
		assertTrue(game2.isFinished());

		game = new Game();
		game.fillFromXml(game2.toXmlTree());
		assertTrue(game.isFinished());
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullAssistances() {
		Game game = new Game(2, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());
		game.setAssistances(null);
	}

	@Test
	public void testAssistanceSetting() {
		Game game = new Game(2, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());

		game.setAssistances(new GameSettings() {
			@Override
			public boolean getAssistance(Assistances assistance) {
				return true;
			}
		});

		for (Assistances a : Assistances.values()) {
			assertTrue(game.isAssistanceAvailable(a));
		}
	}

	@Test
	public void testHelp() {
		class SudokuMock extends Sudoku {
			private boolean finished = false;
			private boolean errors = false;

			public SudokuMock() {
				super(SudokuBuilder.createType(SudokuTypes.standard9x9), new PositionMap<Integer>(Position.get(9, 9)),
						new PositionMap<Boolean>(Position.get(9, 9)));
			}

			@Override
			public boolean isFinished() {
				return finished;
			}

			@Override
			public boolean hasErrors() {
				return errors;
			}

			public void toogleErrors() {
				errors = !errors;
			}

			public void toogleFinished() {
				finished = !finished;
			}

		}
		SudokuMock sudoku = new SudokuMock();
		Game game = new Game(2, sudoku);

		assertFalse(game.solveCell(null));
		assertFalse(game.solveCell(new Cell(true, -1, 3, 9)));

		game.addAndExecute(new SolveActionFactory().createAction(2, game.getSudoku().getCell(Position.get(0, 0))));
		sudoku.toogleErrors();
		assertFalse(game.checkSudoku());
		assertTrue(game.getCurrentState().isMistake());
		assertFalse(game.solveCell());
		assertFalse(game.solveCell(game.getSudoku().getCell(Position.get(0, 0))));
		assertFalse(game.solveAll());

		sudoku.toogleErrors();
		sudoku.toogleFinished();
		game.addAndExecute(new SolveActionFactory().createAction(Cell.EMPTYVAL,
				game.getSudoku().getCell(Position.get(0, 0))));
		game.addAndExecute(new SolveActionFactory().createAction(1, game.getSudoku().getCell(Position.get(0, 0))));
		assertEquals(Cell.EMPTYVAL, game.getSudoku().getCell(Position.get(0, 0)).getCurrentValue());
	}

	@Test
	public void testNoteAdjustment() {
		Game game = new Game(2, new SudokuBuilder(SudokuTypes.standard9x9).createSudoku());
		GameSettings as = new GameSettings();
		as.setAssistance(Assistances.autoAdjustNotes);
		game.setAssistances(as);

		game.addAndExecute(new NoteActionFactory().createAction(2, game.getSudoku().getCell(Position.get(1, 0))));
		game.addAndExecute(new NoteActionFactory().createAction(3, game.getSudoku().getCell(Position.get(1, 0))));
		game.addAndExecute(new NoteActionFactory().createAction(2, game.getSudoku().getCell(Position.get(0, 1))));
		game.addAndExecute(new NoteActionFactory().createAction(3, game.getSudoku().getCell(Position.get(0, 1))));

		assertTrue(game.getSudoku().getCell(Position.get(0, 1)).isNoteSet(2));
		assertTrue(game.getSudoku().getCell(Position.get(1, 0)).isNoteSet(2));
		assertTrue(game.getSudoku().getCell(Position.get(0, 1)).isNoteSet(3));
		assertTrue(game.getSudoku().getCell(Position.get(1, 0)).isNoteSet(3));

		game.addAndExecute(new SolveActionFactory().createAction(2, game.getSudoku().getCell(Position.get(0, 0))));

		assertTrue(game.getSudoku().getCell(Position.get(0, 1)).isNoteSet(3));
		assertTrue(game.getSudoku().getCell(Position.get(1, 0)).isNoteSet(3));
		assertFalse(game.getSudoku().getCell(Position.get(0, 1)).isNoteSet(2));
		assertFalse(game.getSudoku().getCell(Position.get(1, 0)).isNoteSet(2));
	}

	@Test
	public void testScore() {
		for (Complexity c : Complexity.values()) {
			Sudoku sudoku = new Sudoku(TypeBuilder.get99());
			sudoku.setComplexity(c);
			Game game = new Game(0, sudoku);
			game.addTime(60);
			switch (c) {
			case easy:
				assertEquals(game.getScore(), 2430);
				break;
			case medium:
				assertEquals(game.getScore(), 7290);
				break;
			case difficult:
				assertEquals(game.getScore(), 21870);
				break;
			case infernal:
				assertEquals(game.getScore(), 65610);
				break;
			}
		}
	}

	@Test
	public synchronized void testSolve() {
		System.out.println("before while");
		
		int counter = 0;
		while (GameTests.sudoku == null && counter<120) {
			try {
				wait(1000);
				counter++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(GameTests.sudoku == null)
			throw new IllegalStateException("infinite loop!");
		
        System.out.println("we passed the while loop!");
		Game game = new Game(1, sudoku);
		ArrayList<Cell> unsolvedCells = new ArrayList<Cell>();
		for (Cell f : sudoku) {
			if (f.isEditable()) {
				f.clearCurrentValue();
				unsolvedCells.add(f);
			}
		}
		assertTrue(game.checkSudoku());
		assertTrue(game.solveCell());
		boolean hasNewSolved = false;
		for (Cell f : sudoku) {
			if (f.isEditable() && f.isSolvedCorrect()) {
				if (hasNewSolved) {
					fail("Solve field solved more than one field");
				} else {
					hasNewSolved = true;
					unsolvedCells.remove(f);
				}
			}
		}
		assertTrue(hasNewSolved);
		assertTrue(game.checkSudoku());
		assertFalse(game.isFinished());
		assertTrue(game.solveCell(unsolvedCells.get(0)));
		assertTrue(unsolvedCells.get(0).isSolvedCorrect());
		unsolvedCells.remove(0);
		assertFalse(game.isFinished());
		assertTrue(game.solveAll());
		for (Cell f : unsolvedCells) {
			assertTrue(f.isSolvedCorrect());
		}
		assertTrue(game.isFinished());
		assertTrue(game.isFinished());
	}

	@Test
	public synchronized void testGoToLastCorrectState() {
		while (sudoku == null) {
			try {
				wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Game game = new Game(1, sudoku);

		Cell unsolvedCell = null;
		for (Cell f : sudoku) {
			if (f.isEditable()) {
				f.clearCurrentValue();
				if (unsolvedCell == null)
					unsolvedCell = f;
			}
		}

		int oldAssistanceCost = game.getAssistancesCost();
		game.goToLastCorrectState();
		assertTrue(game.getAssistancesCost() - 3 == oldAssistanceCost);
		oldAssistanceCost = game.getAssistancesCost();

		if (unsolvedCell.getSolution() < 8) {
			game.addAndExecute(new SolveActionFactory().createAction(8, unsolvedCell));
		} else {
			game.addAndExecute(new SolveActionFactory().createAction(7, unsolvedCell));
		}

		game.goToLastCorrectState();
		assertTrue(game.getAssistancesCost() - 3 == oldAssistanceCost);
		oldAssistanceCost = game.getAssistancesCost();
		assertTrue(unsolvedCell.getCurrentValue() == Cell.EMPTYVAL);
		assertTrue(game.getCurrentState().isCorrect());
	}

	// Regression Test for Issue-90
	@Test
	public void testAutoAdjustNotesForAutomaticSolving() {
		SudokuBuilder sb = new SudokuBuilder(SudokuTypes.standard9x9);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sb.addSolution(Position.get(i, j), 1);
			}
		}
		Game game = new Game(2, sb.createSudoku());
		GameSettings as = new GameSettings();
		as.setAssistance(Assistances.autoAdjustNotes);
		game.setAssistances(as);

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				game.addAndExecute(new NoteActionFactory().createAction(1, game.getSudoku().getCell(Position.get(i, j))));
				assertTrue(game.getSudoku().getCell(Position.get(i, j)).isNoteSet(1));
			}
		}

		assertTrue(game.solveCell());
		boolean done = false;
		int x = -1;
		int y = -1;
		for (int i = 0; i < 9 && !done; i++) {
			for (int j = 0; j < 9 && !done; j++) {
				if (game.getSudoku().getCell(Position.get(i, j)).getCurrentValue() == 1) {
					done = true;
					x = i;
					y = j;
				}
			}
		}
		assertTrue(done);

		for (int i = 0; i < 9; i++) {
			assertFalse(game.getSudoku().getCell(Position.get(x, i)).isNoteSet(1));
			assertFalse(game.getSudoku().getCell(Position.get(i, y)).isNoteSet(1));
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				assertFalse(game.getSudoku().getCell(Position.get((x - (x % 3) + i), (y - (y % 3) + j))).isNoteSet(1));
			}
		}

		x = (x + 3) % 9;
		y = (y + 3) % 9;
		game.solveCell(game.getSudoku().getCell(Position.get(x, y)));

		for (int i = 0; i < 9; i++) {
			assertFalse(game.getSudoku().getCell(Position.get(x, i)).isNoteSet(1));
			assertFalse(game.getSudoku().getCell(Position.get(i, y)).isNoteSet(1));
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				assertFalse(game.getSudoku().getCell(Position.get((x - (x % 3) + i), (y - (y % 3) + j))).isNoteSet(1));
			}
		}
	}
}
