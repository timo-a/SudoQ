package de.sudoq.model.sudoku

import de.sudoq.model.ModelChangeListener
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class SudokuTests {

    val sudokuType99: SudokuType = TypeBuilder.getType(SudokuTypes.standard9x9)

    @Test
    fun initializeStandardSudoku() {
        val sudoku = Sudoku(sudokuType99)

        sudoku.sudokuType `should be` sudokuType99
        sudoku.isFinished.`should be false`()

        for (pos in sudokuType99.validPositions)
            sudoku.getCell(pos).`should not be null`()

        for (f in sudoku)
            f.`should not be null`()

        sudoku.transformCount.`should be`(0)
        sudoku.id.`should be`(0)

        sudoku.increaseTransformCount()

        sudoku.transformCount.`should be`(1)
    }

    @Test
    fun initializeWithoutSolutions() {
        val sudoku = Sudoku(sudokuType99, null, null)

        sudoku.sudokuType.`should be`(sudokuType99)
        sudoku.isFinished.`should be false`()

        for (pos in sudokuType99.validPositions)
            sudoku.getCell(pos).`should not be null`()

        for (f in sudoku)
            f.`should not be null`()

        sudoku.transformCount.`should be`(0)
        sudoku.id.`should be`(0)

        sudoku.increaseTransformCount()

        sudoku.transformCount.`should be`(1)

    }

    @Test
    fun initializeWithoutSetValues() {
        val solutions = PositionMap<Int>(Position[9, 9], sudokuType99.validPositions) {
            _ -> 0 }
        val sudoku = Sudoku(sudokuType99, solutions, null)
        sudoku.sudokuType.`should be`(sudokuType99)
        sudoku.isFinished.`should be false`()

        for (pos in sudokuType99.validPositions) {
            sudoku.getCell(pos).`should not be null`()
        }

        sudoku.transformCount.`should be`(0)
        sudoku.id.`should be`(0)

        sudoku.increaseTransformCount()

        sudoku.transformCount.`should be`(1)

    }

    @Test
    fun getCell() {
        val sudoku = Sudoku(sudokuType99)
        val p12 = Position[1, 2]
        sudoku.getCell(Position[9, 10]).`should be null`() //because out of board

        val f = sudoku.getCell(p12)
        f!!.currentValue = 6
        sudoku.getCell(p12)!!.currentValue.`should be`(6)
    }

    @Test
    fun complexity() {
        val sudoku = Sudoku(sudokuType99)
        sudoku.complexity.`should be null`()
        sudoku.complexity = Complexity.easy
        sudoku.complexity.`should not be null`()
        sudoku.complexity.`should be`(Complexity.easy)
    }

    @Test//TODO no chance to fail...
    fun iterator() {
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
    fun initializeSudokuWithValues() {
        val map = PositionMap<Int>(Position[9, 9], sudokuType99.validPositions) {
            pos -> pos.x + 1}
        val setValues = PositionMap<Boolean>(Position[9, 9],
            sudokuType99.validPositions.filter { pos -> pos.x != pos.y }) {
            _ -> true }
        val sudoku = Sudoku(sudokuType99, map, setValues)
        var cell: Cell?
        for (pos in sudokuType99.validPositions) {
            cell = sudoku.getCell(pos)
            if (pos.x == pos.y) {
                cell!!.isEditable.`should be true`()
            } else {
                cell!!.isEditable.`should be false`()
            }
        }
    }

    @Test //todo use mockk
    fun cellChangeNotification() {
        val sudokuTypeRepo = SudokuTypeRepo4Tests()
        val sudoku = SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku()
        val listener = Listener();

        sudoku.getCell(Position[0, 0])!!.currentValue = 2
        listener.callCount `should be` 0

        sudoku.registerListener(listener);
        sudoku.getCell(Position[3, 2])!!.currentValue = 5
        listener.callCount `should be` 1
   }

    class Listener : ModelChangeListener<Cell> {
        var callCount : Int = 0;

        override fun onModelChanged(obj: Cell) {
            callCount++;
        }
    }

    @Test
    fun notEquals() {
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
    fun hasErrors() {
        val sudokuType = sudokuType99
        val solutions = PositionMap<Int>(Position[9, 9])
        for (pos in sudokuType.validPositions) {
            solutions.put(pos, 0)
        }
        val sudoku = Sudoku(sudokuType, solutions, null)
        sudoku.getCell(Position[0, 0])!!.currentValue = 1
        sudoku.hasErrors().`should be true`()
    }

    @Test
    fun cellModification() {
        val s = Sudoku(TypeBuilder.get99())
        val f = Cell(1000, 9)
        s.setCell(f, Position[4, 4])
        s.getCell(Position[4, 4]).`should be`(f)
        s.getPosition(f.id).`should be equal to`(Position[4, 4])
    }


    @Test
    fun toString44() {
        val sudokuType = TypeBuilder.getType(SudokuTypes.standard4x4)
        val sudoku = Sudoku(sudokuType)
        sudoku.getCell(Position[1, 1])!!.currentValue = 3
        sudoku.cells!!.remove(Position[1, 2])
        sudoku.toString().`should be equal to`(
            """
            x x x x
            x 3 x x
            x   x x
            x x x x
            """.trimIndent(),
        )
    }

    @Test
    fun toString99() {
        val sudokuType = TypeBuilder.getType(SudokuTypes.standard16x16)
        val sudoku = Sudoku(sudokuType)
        sudoku.getCell(Position[1, 1])!!.currentValue = 12
        sudoku.toString().`should be equal to`(
            """
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx 12 xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx
            """.trimIndent(),
        )
    }
}
