package de.sudoq.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.actionTree.ActionFactory;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.sudoku.Cell;

class GameStateHandlerTests {

    @Test
    void construction() {
		GameStateHandler stateHandler = new GameStateHandler();
		// the following 3 actions shouldnt do anything
		stateHandler.undo();
		stateHandler.undo();
		assertFalse(stateHandler.canRedo());
		stateHandler.redo();
		ActionFactory af = new SolveActionFactory();
		Cell cell = new Cell(-1, 9);

		stateHandler.addAndExecute(af.createAction(5, cell));
        assertEquals(5, cell.getCurrentValue());
		assertNotSame(stateHandler.getActionTree().getSize(),0);
	}

    @Test
    void nullAction() {
		GameStateHandler gsh = new GameStateHandler();
		ActionTreeElement a = gsh.getCurrentState();
		a.undo();
		a.execute();
	}

    @Test
    void undoRedo() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		Cell cell1 = new Cell(-1, 9);
		Cell cell2 = new Cell(-1, 9);
		Cell cell3 = new Cell(-1, 9);

		int value1 = cell1.getCurrentValue();
		int value2 = cell2.getCurrentValue();
		int value3 = cell3.getCurrentValue();
		stateHandler.addAndExecute(af.createAction(5, cell1));
		stateHandler.addAndExecute(af.createAction(6, cell2));
		stateHandler.addAndExecute(af.createAction(7, cell3));
		assertTrue(stateHandler.canUndo());
		stateHandler.undo();
		assertTrue(stateHandler.canUndo());
		stateHandler.undo();
		assertTrue(stateHandler.canUndo());
		stateHandler.undo();
		assertFalse(stateHandler.canUndo());
		assertEquals(value1, cell1.getCurrentValue());
		assertEquals(value2, cell2.getCurrentValue());
		assertEquals(value3, cell3.getCurrentValue());
		assertTrue(stateHandler.canRedo());
		stateHandler.redo();
		assertTrue(stateHandler.canRedo());
		stateHandler.redo();
		assertTrue(stateHandler.canRedo());
		stateHandler.redo();
		assertFalse(stateHandler.canRedo());
		assertEquals(5, cell1.getCurrentValue());
		assertEquals(6, cell2.getCurrentValue());
		assertEquals(7, cell3.getCurrentValue());
	}

    @Test
    void tapSameSymbol4Times() {
		/* regression for: press three times '3' on same field.
		 * first 2 beheave as expected, 3rd does nothing and 4 crashes */
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		Cell cell1 = new Cell(-1, 9);

		stateHandler.addAndExecute(af.createAction(3, cell1));
		assertEquals(3, cell1.getCurrentValue());

		stateHandler.addAndExecute(af.createAction(-1, cell1));
		assertEquals(-1, cell1.getCurrentValue());

		stateHandler.addAndExecute(af.createAction(3, cell1));
		assertEquals(3, cell1.getCurrentValue());

		stateHandler.addAndExecute(af.createAction(-1, cell1));
		assertEquals(-1, cell1.getCurrentValue());

	}


    @Test
    void goTo() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		Cell cell = new Cell(-1, 9);

		//add 5, undo test if undo successful
		int value = cell.getCurrentValue();
		stateHandler.addAndExecute(af.createAction(5, cell));
		stateHandler.goToState(stateHandler.getCurrentState());
		stateHandler.undo();
		assertEquals(value, cell.getCurrentValue());

		//redo the 5,
		stateHandler.redo();
		ActionTreeElement first = stateHandler.getCurrentState();// first <- 5
		stateHandler.addAndExecute(af.createAction(7, cell));
		ActionTreeElement branch = stateHandler.getCurrentState();
		stateHandler.goToState(first);
		assertEquals(5, cell.getCurrentValue());
		stateHandler.addAndExecute(af.createAction(3, cell));
		assertEquals(3, cell.getCurrentValue());

		stateHandler.undo();
		stateHandler.undo();
		assertEquals(value, cell.getCurrentValue());
		stateHandler.undo();
		assertEquals(value, cell.getCurrentValue());

		stateHandler.redo();
		stateHandler.redo();
		assertEquals(3, cell.getCurrentValue());

		stateHandler.goToState(branch);
		assertEquals(7, cell.getCurrentValue());
	}

    @Test
    void emptyUndoStack() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		Cell f_1 = new Cell(-1, 9);
		Cell f_2 = new Cell(-2, 9);
		Cell f_3 = new Cell(-3, 9);
		Cell f_4 = new Cell(-4, 9);

		stateHandler.addAndExecute(af.createAction(1, f_1));
		ActionTreeElement b1 = stateHandler.getCurrentState();
		stateHandler.redo();
		assertEquals(b1, stateHandler.getCurrentState());
		stateHandler.undo();
		ActionTreeElement start = stateHandler.getCurrentState();
		assertNotNull(start);
		stateHandler.addAndExecute(af.createAction(2, f_2));
		ActionTreeElement b2 = stateHandler.getCurrentState();
		stateHandler.goToState(start);
		assertTrue(stateHandler.canRedo());
		stateHandler.redo();
		assertEquals(b2, stateHandler.getCurrentState());
		stateHandler.addAndExecute(af.createAction(3, f_3));
		ActionTreeElement b3 = stateHandler.getCurrentState();
		stateHandler.goToState(b1);
		stateHandler.goToState(b2);
		assertTrue(stateHandler.canRedo());
		stateHandler.redo();
		assertEquals(b3, stateHandler.getCurrentState());
		stateHandler.goToState(b2);
		stateHandler.addAndExecute(af.createAction(4, f_4));
		stateHandler.goToState(b1);
		stateHandler.goToState(b2);
		assertFalse(stateHandler.canRedo());
		stateHandler.redo();
		assertEquals(b2, stateHandler.getCurrentState());
	}

    @Test
    void stateMarking() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		Cell cell = new Cell(-1, 9);

		assertFalse(stateHandler.isMarked(stateHandler.getCurrentState()));
		stateHandler.markCurrentState();
		assertTrue(stateHandler.isMarked(stateHandler.getCurrentState()));

		stateHandler.addAndExecute(af.createAction(5, cell));

		assertFalse(stateHandler.isMarked(stateHandler.getCurrentState()));
		stateHandler.markCurrentState();
		assertTrue(stateHandler.isMarked(stateHandler.getCurrentState()));
		assertFalse(stateHandler.isMarked(null));
	}

    @Test
    void thereAndBackAgain() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		// Vier Felder
		Cell cell1 = new Cell(-1, 9);
		Cell cell2 = new Cell(-1, 9);
		Cell cell3 = new Cell(-1, 9);
		Cell cell4 = new Cell(-1, 9);

		// Vier Werte
		int value1 = 2;
		int value2 = 5;
		int value3 = 7;
		int value4 = 1;

		// Feld 1 wird Wert 1 gesetzt
		stateHandler.addAndExecute(af.createAction(value1, cell1));
		// System.out.println(field1.getCurrentValue() + " " +
		// field2.getCurrentValue() + " " + field3.getCurrentValue() + " " +
		// field4.getCurrentValue());
		stateHandler.addAndExecute(af.createAction(value2, cell2));
		stateHandler.addAndExecute(af.createAction(value3, cell3));
		stateHandler.addAndExecute(af.createAction(value4, cell4));

		// current ist die letzte addAndExecute Operation
		ActionTreeElement element1 = stateHandler.getCurrentState().getParent();
		ActionTreeElement element2 = element1.getParent();
		ActionTreeElement element3 = element2.getParent();

		// Einmal hin und zurück
		stateHandler.goToState(element1);
		stateHandler.goToState(element2);
		stateHandler.goToState(element3);
		stateHandler.goToState(element2);
		stateHandler.goToState(element1);

		// Alles sollte wie vorher sein
		assertEquals(cell1.getCurrentValue(), value1);
		assertEquals(cell2.getCurrentValue(), value2);
		assertEquals(cell3.getCurrentValue(), value3);

	}

    @Test
    void gotoDownwards() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		Cell cell = new Cell(-1, 9);

		stateHandler.addAndExecute(af.createAction(1, cell));
		stateHandler.addAndExecute(af.createAction(2, cell));
		stateHandler.addAndExecute(af.createAction(3, cell));
		ActionTreeElement current = stateHandler.getCurrentState();
		stateHandler.undo();
		stateHandler.undo();
		stateHandler.goToState(current);
		assertEquals(current, stateHandler.getCurrentState());
	}

    @Test
    void locking() {
		final GameStateHandler stateHandler = new GameStateHandler();
		final ActionFactory af = new SolveActionFactory();
		final Cell f = new Cell(-1, 9);

		f.registerListener(new ModelChangeListener<Cell>() {

			@Override
			public void onModelChanged(Cell obj) {
				stateHandler.addAndExecute(af.createAction(8, f));
			}
		});

		stateHandler.addAndExecute(af.createAction(2, f));
		assertEquals(2, f.getCurrentValue());
	}

}
