package de.sudoq.persistence.game

import de.sudoq.model.actionTree.Action
import de.sudoq.model.actionTree.ActionTreeElement
import de.sudoq.model.actionTree.SolveAction
import de.sudoq.model.sudoku.Cell
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class ActionTreeElementBETests {

    val action: Action = SolveAction(1, Cell(1, 9))

    @Test
    fun `should leave parent empty when initialized with null`(){
        val ate = ActionTreeElementBE(1, action, null)

        val xmlTreeRoot = ate.toXml()!!

        xmlTreeRoot.getAttributeValue(PARENT) `should be equal to` ""
    }

    @Test
    fun `should retain parent id`() {
        val ateChild = ActionTreeElementBE(44, action, 43)

        var xmlTreeChild = ateChild.toXml()!!

        xmlTreeChild.getAttributeValue(PARENT) `should be equal to` "43"
    }

    @Test
    fun `should retain mark`() {
        val ateChild = ActionTreeElement(44, action, null)
        ateChild.mark()
        var xmlTreeChild = ActionTreeElementMapper.toBE(ateChild).toXml()!!

        xmlTreeChild.getAttributeValue(MARKED) `should be equal to` "true"
    }

    @Test
    fun `should retain mark correct`() {
        val ateChild = ActionTreeElement(44, action, null)
        ateChild.markCorrect()
        var xmlTreeChild = ActionTreeElementMapper.toBE(ateChild).toXml()!!

        xmlTreeChild.getAttributeValue(CORRECT) `should be equal to` "true"
    }

    @Test
    fun `should retain mark Wrong`() {
        val ateChild = ActionTreeElement(44, action, null)
        ateChild.markWrong()
        var xmlTreeChild = ActionTreeElementMapper.toBE(ateChild).toXml()!!

        xmlTreeChild.getAttributeValue(MISTAKE) `should be equal to` "true"
    }

    @Test
    fun `should be converted to null for invalid cell`() {
        val action: Action = SolveAction(1, Cell(-1, 9))
        val ate = ActionTreeElement(1, action, null)
        val ateBE = ActionTreeElementMapper.toBE(ate)
        ateBE.toXml() `should be equal to` null
    }


    companion object {
        const val PARENT = "parent"
        const val MARKED = "marked"
        const val MISTAKE = "mistake"
        const val CORRECT = "correct"
    }
}