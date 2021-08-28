package de.sudoq.persistence.sudoku

import java.io.IOException
import java.util.*

import org.amshove.kluent.*
import org.junit.jupiter.api.Test

import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import testutils.SudokuTypeProvider
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import de.sudoq.persistence.XmlTree

class SudokuBETests {

    //todo define 9x9 type once, load from xml
    val sudokuType9x9 = SudokuTypeProvider.getType(SudokuTypes.standard9x9)

    @Test
    fun testToXml() {
        val sudoku = SudokuBE(4357, 0, sudokuType9x9, Complexity.easy, HashMap())
        val tree = sudoku.toXmlTree()
        tree.numberOfChildren `should be greater than` 0
        tree.numberOfChildren.`should be positive`()

        0.shouldBePositive()

        tree.getAttributeValue("id") `should be equal to` "4357"

        tree.getAttributeValue("type") `should be equal to` SudokuTypes.standard9x9.ordinal.toString()

        tree.getAttributeValue("complexity") `should be equal to` Complexity.easy.ordinal.toString()

        tree.numberOfChildren.toLong() `should be equal to` 81

    }

    @Test
    @Throws(IllegalArgumentException::class, IOException::class)
    fun testFillFromXml() {
        val sudoku = SudokuBE(
            6374, 0,
            SudokuType(9, 9, 9),
            Complexity.easy, HashMap()
        )
        val tree = sudoku.toXmlTree()
        println(XmlHelper().buildXmlStructure(tree))
        val rebuilt = SudokuBE()
        //rebuilt.fillFromXml(tree, mockSudokuTypeRepo) todo mock so that 9x9 is returned
        rebuilt `should be equal to` sudoku
    }


    @Test
        //(expected = java.lang.IllegalArgumentException::class)
    fun testFromXmlError() {
        val sudoku = Sudoku(sudokuType9x9)
        val sudokuBE = SudokuBE(
            sudoku.id, sudoku.transformCount,
            sudoku.sudokuType!!, sudoku.complexity!!, sudoku.cells!!
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
            sudoku.sudokuType!!, sudoku.complexity!!, sudoku.cells!!
        )
        val tree = sudokuBE.toXmlTree()
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
        //todo use mock
    // sudokuBE.fillFromXml(tree, SudokuTests.sudokuTypeRepo)
    }

    @Test
    fun testFromXmlAdditionalChild() {
        val sudoku = Sudoku(sudokuType9x9)
        val sudokuBE = SudokuBE(sudoku.id, sudoku.transformCount, sudoku.sudokuType!!,
            sudoku.complexity!!, sudoku.cells!!
        )
        val tree = sudokuBE.toXmlTree()
        tree.addChild(XmlTree("Test"))
        val s2 = SudokuBE()
        //todo use mock s2.fillFromXml(tree, SudokuTests.sudokuTypeRepo)
        s2 `should be equal to` sudoku
    }

}