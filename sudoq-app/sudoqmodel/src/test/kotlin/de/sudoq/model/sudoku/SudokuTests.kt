package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.Position.Companion.get
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.*
import org.junit.Assert
import org.junit.jupiter.api.Test

class SudokuTests {

    val sudokuType99: SudokuType = TypeBuilder.getType(SudokuTypes.standard9x9)

    @Test
    fun testInitializeStandardSudoku() {
        val sudoku = Sudoku(sudokuType99)

        sudoku.sudokuType.`should be`(sudokuType99)
        sudoku.isFinished.`should be false`()

        for (x in 0..8) {
            for (y in 0..8) {
                sudoku.getCell(Position[x, y]).`should not be null`()
            }
        }

        sudoku.transformCount.`should be`(0)
        sudoku.id.`should be`(0)

        sudoku.increaseTransformCount()

        sudoku.transformCount.`should be`(1)
    }

    @Test
    fun testInitializeWithoutSolutions() {
        val sudoku = Sudoku(sudokuType99, null, null)

        sudoku.sudokuType.`should be`(sudokuType99)
        sudoku.isFinished.`should be false`()

        for (x in 0..8) {
            for (y in 0..8) {
                sudoku.getCell(Position[x, y]).`should not be null`()
            }
        }

        sudoku.transformCount.`should be`(0)
        sudoku.id.`should be`(0)

        sudoku.increaseTransformCount()

        sudoku.transformCount.`should be`(1)

    }

    @Test
    fun testInitializeWithoutSetValues() {
        val solutions = PositionMap<Int>(Position[9, 9])
        for (x in 0..8) {
            for (y in 0..8) {
                solutions.put(Position[x, y], 0)
            }
        }
        val sudoku = Sudoku(sudokuType99, solutions, null)
        sudoku.sudokuType.`should be`(sudokuType99)
        sudoku.isFinished.`should be false`()

        for (x in 0..8) {
            for (y in 0..8) {
                sudoku.getCell(Position[x, y]).`should not be null`()
            }
        }

        sudoku.transformCount.`should be`(0)
        sudoku.id.`should be`(0)

        sudoku.increaseTransformCount()

        sudoku.transformCount.`should be`(1)

    }

    @Test
    fun testGetCell() {
        val sudoku = Sudoku(sudokuType99)
        val p12 = Position[1, 2]
        sudoku.getCell(Position[9, 10]).`should be null`() //because out of board

        val f = sudoku.getCell(p12)
        f!!.currentValue = 6
        sudoku.getCell(p12)!!.currentValue.`should be`(6)
    }

    @Test
    fun testComplexity() {
        val sudoku = Sudoku(sudokuType99)
        sudoku.complexity.`should be null`()
        sudoku.complexity = Complexity.easy
        sudoku.complexity.`should not be null`()
        sudoku.complexity.`should be`(Complexity.easy)
    }

    @Test//TODO no chance to fail...
    fun testIterator() {
        val su = Sudoku(sudokuType99)
        su.getCell(Position[0, 0])!!.currentValue = 5
        su.getCell(Position[1, 4])!!.currentValue = 4
        val i = su.iterator()
        var aThere = false
        var bThere = false
        var f: Cell
        while (i.hasNext()) {
            f = i.next()
            if (f.currentValue == 5 && !aThere) aThere = true
            if (f.currentValue == 4 && !bThere) bThere = true
        }
    }

    @Test
    fun testInitializeSudokuWithValues() {
        val map = PositionMap<Int>(Position[9, 9])
        val setValues = PositionMap<Boolean>(Position[9, 9])
        for (x in 0..8) {
            for (y in 0..8) {
                map.put(Position[x, y], x + 1)
                if (x != y) {
                    setValues.put(Position[x, y], true)
                }
            }
        }
        val sudoku = Sudoku(sudokuType99, map, setValues)
        var cell: Cell?
        for (x in 0..8) {
            for (y in 0..8) {
                if (x == y) {
                    cell = sudoku.getCell(Position[x, y])
                    cell!!.isEditable.`should be true`()
                } else {
                    cell = sudoku.getCell(Position[x, y])
                    cell!!.isEditable.`should be false`()
                }
            }
        }
    }

    @Test
    fun testNotEquals() {
        val s1 = Sudoku(sudokuType99)
        var s2 = Sudoku(TypeBuilder.getType(SudokuTypes.standard16x16))
        s1.`should not be equal to`(s2)
        s1.`should not be null`()

        s2 = Sudoku(sudokuType99)
        s1.complexity = Complexity.easy
        s2.complexity = Complexity.medium
        s1.`should not be equal to`(s2)
        s2 = Sudoku(TypeBuilder.getType(SudokuTypes.samurai))
        s2.complexity = Complexity.easy
        s2.`should not be equal to`(s1)
    }

    @Test
    fun testHasErrors() {
        val sudokuType = sudokuType99
        val solutions = PositionMap<Int>(Position[9, 9])
        for (x in 0..8) {
            for (y in 0..8) {
                solutions.put(Position[x, y], 0)
            }
        }
        val sudoku = Sudoku(sudokuType, solutions, null)
        sudoku.getCell(Position[0, 0])!!.currentValue = 1
        sudoku.hasErrors().`should be true`()
    }

}