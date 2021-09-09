package de.sudoq.model.actionTree

import de.sudoq.model.game.GameStateHandler
import de.sudoq.model.sudoku.Cell
import org.junit.Assert
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AddElementsTest {

    @Test
    fun testAddingRedundantElement() {
        val stateHandler = GameStateHandler()
        //ActionTree at = new ActionTree();
        val factory: ActionFactory = SolveActionFactory()
        val cell = Cell(1, 9)
        val root = stateHandler.actionTree.root
        stateHandler.addAndExecute(factory.createAction(1, cell))
        stateHandler.undo()
        stateHandler.addAndExecute(factory.createAction(1, cell)) //should be ignored by at

        root.childrenList.size `should be equal to` 1

        root.childrenList[0].childrenList.`should be empty`()

    }

    @Test
    fun testAddingRedundantElementBelow() {
        /*        r      intended values, not diffs
		 *       / \
		 *      1    2
		 *     |
		 *     2
		 */
        val sh = GameStateHandler()
        val af: ActionFactory = SolveActionFactory()
        val cell = Cell(1, 9)
        val root = sh.actionTree.root
        sh.addAndExecute(af.createAction(2, cell))
        sh.addAndExecute(af.createAction(1, cell))
        sh.addAndExecute(af.createAction(2, cell))

        root.childrenList.size `should be equal to` 2
        for (child in root.childrenList)
            child.childrenList.`should be empty`()
    }


}