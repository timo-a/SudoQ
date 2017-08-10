package de.sudoq.model.actionTree;

import org.junit.Test;

import java.util.List;

import de.sudoq.model.game.GameStateHandler;
import de.sudoq.model.sudoku.Field;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ActionTreeAddElementsTests {

	@Test
	public void testAddingRedundantElement() {
		GameStateHandler stateHandler = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();
		//ActionTree at = new ActionTree();
		ActionFactory factory = new SolveActionFactory();

		Field field = new Field(1, 9);

		ActionTreeElement root = stateHandler.getActionTree().getRoot();

		stateHandler.addAndExecute(factory.createAction(1, field));
		stateHandler.undo();
		stateHandler.addAndExecute(factory.createAction(1, field));//should be ignored by at

		assertEquals(1, root.getChildrenList().size());
		assertTrue(root.getChildrenList().get(0).getChildrenList().isEmpty());
	}

	@Test
	public void testAddingRedundantElementBelow() {
		/*        r      intended values, not diffs
		 *       / \
		 *      1    2
		 *     |
		 *     2
		 */
		GameStateHandler sh = new GameStateHandler();
		ActionFactory af = new SolveActionFactory();

		Field field = new Field(1, 9);

		ActionTreeElement root = sh.getActionTree().getRoot();

		sh.addAndExecute(af.createAction(2, field));
		sh.addAndExecute(af.createAction(1, field));

		sh.addAndExecute(af.createAction(2, field));

		assertEquals(2, root.getChildrenList().size());
		for(ActionTreeElement child: root.getChildrenList())
			assertTrue(child.getChildrenList().isEmpty());
	}


}
