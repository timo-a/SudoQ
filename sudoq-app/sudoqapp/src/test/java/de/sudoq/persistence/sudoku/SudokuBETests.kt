package de.sudoq.persistence.sudoku

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.sudoku.Cell
import java.io.IOException
import java.util.*

import org.amshove.kluent.*
import org.junit.jupiter.api.Test

import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import testutils.SudokuTypeProvider
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import de.sudoq.persistence.XmlTree

class SudokuBETests {

    //todo define 9x9 type once, load from xml
    val sudokuType9x9 = SudokuTypeProvider.getType(SudokuTypes.standard9x9)

    @Test
    fun testToXml() {
        val sudoku = SudokuBE(4357, 0, sudokuType9x9, Complexity.easy, hashMapOf(
            Position[0,0] to Cell(true, 0, 0, 9),
        ))
        val tree = sudoku.toXmlTree()
        tree.numberOfChildren `should be greater than` 0
        tree.numberOfChildren.`should be positive`()

        tree.getAttributeValue("id") `should be equal to` "4357"

        tree.getAttributeValue("type") `should be equal to` SudokuTypes.standard9x9.ordinal.toString()

        tree.getAttributeValue("complexity") `should be equal to` Complexity.easy.ordinal.toString()

        tree.numberOfChildren.toLong() `should be equal to` 1

    }

    @Test
    @Throws(IllegalArgumentException::class, IOException::class)
    fun testFillFromXml() {

        val dummySudokuType = SudokuType(SudokuTypes.standard4x4, 9, 0f, Position[1,1],
            Position[1,1], ArrayList(), ArrayList(), ArrayList(), ComplexityConstraintBuilder(HashMap())
        )
        val mockSudokuTypeRepo = ReadRepo<SudokuType> { dummySudokuType }

        val sudoku = SudokuBE(
            6374, 0,
            dummySudokuType,
            Complexity.easy, HashMap()
        )
        val tree = sudoku.toXmlTree()
        println(XmlHelper().buildXmlStructure(tree))

        val rebuilt = SudokuBE()
        rebuilt.fillFromXml(tree, mockSudokuTypeRepo)
        equals(rebuilt, sudoku)
    }

    private fun equals(
        first: SudokuBE,
        second: SudokuBE
    ) {
        first.id `should be equal to` second.id
        first.transformCount `should be equal to` second.transformCount
        first.sudokuType `should be equal to` second.sudokuType
        first.complexity `should be equal to` second.complexity
        if (first.cells == null) {
            second.cells `should be` null
        } else {
            second.cells `should not be` null
            second.cells!!.size `should be equal to` first.cells!!.size
        }
    }


    @Test
    fun testFromXmlError() {
        val sudoku = Sudoku(sudokuType9x9)
        val sudokuBE = SudokuBE(
            sudoku.id, sudoku.transformCount,
            sudoku.sudokuType, Complexity.difficult, sudoku.cells!!
        )
        val tree = sudokuBE.toXmlTree()
        val iterator: Iterator<XmlTree> = tree.getChildren()
        while (iterator.hasNext()) {
            val sub = iterator.next()
            if (sub.name == "fieldmap") {
                sub.addChild(XmlTree("Hallo"))
            }
            sub.numberOfChildren `should be equal to` 2
        }
    }

    @Test//(expected = java.lang.IllegalArgumentException::class)
    fun testFromXmlError2() {
        val sudoku = Sudoku(sudokuType9x9)
        val sudokuBE = SudokuBE(
            sudoku.id, sudoku.transformCount,
            sudoku.sudokuType, Complexity.difficult, sudoku.cells!!
        )
        val tree = sudokuBE.toXmlTree()
        // make it so every fieldmap has only one child: "Test"
        val iterator: Iterator<XmlTree> = tree.getChildren()
        while (iterator.hasNext()) {
            val sub = iterator.next()
            val it = sub.getChildren()
            if (it.hasNext()) {
                it.next()
                it.remove()
            }
            sub.addChild(XmlTree("Test"))
        }
        val mockSudokuTypeRepo = ReadRepo { sudokuType9x9 }
        val s2 = SudokuBE()
        invoking { s2.fillFromXml(tree, mockSudokuTypeRepo) } `should throw` IllegalArgumentException::class
    }

    @Test
    fun testFromXmlAdditionalChild() {
        val sudoku = Sudoku(sudokuType9x9)
        val sudokuBE = SudokuBE(2, sudoku.transformCount, sudoku.sudokuType,
            Complexity.difficult, sudoku.cells!!
        )
        val mockSudokuTypeRepo = ReadRepo { sudokuType9x9 }
        val tree = sudokuBE.toXmlTree()

        tree.addChild(XmlTree("Test"))//this addition is expected not to influence the result
        val s2 = SudokuBE()
        s2.fillFromXml(tree, mockSudokuTypeRepo)
        equals(s2, sudokuBE)
    }

}