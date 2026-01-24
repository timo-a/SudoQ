package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.solution.Solution
import de.sudoq.model.solverGenerator.transformations.Transformer.transform
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TransformerTests : GeneratorCallback {
    var map: PositionMap<Int> = PositionMap(Position[9, 9])

    @Test
    fun transformS99Test1() {

        val values = intArrayOf(
            9, 5, 8, 3, 1, 2, 7, 6, 4,
            4, 6, 1, 5, 7, 9, 8, 2, 3,
            3, 7, 2, 4, 6, 8, 9, 5, 1,
            8, 9, 6, 1, 2, 3, 5, 4, 7,
            1, 4, 3, 7, 9, 5, 2, 8, 6,
            5, 2, 7, 6, 8, 4, 3, 1, 9,
            7, 8, 5, 9, 4, 1, 6, 3, 2,
            2, 1, 9, 8, 3, 6, 4, 7, 5,
            6, 3, 4, 2, 5, 7, 1, 9, 8);

		val sudoku1 = Sudoku(TypeBuilder.get99(), initializeMap(9, values), PositionMap(Position[9, 9]));

        repeat(1337) { transform(sudoku1) }

        // printSudoku9x9(sudoku1, 9);
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformS66Test1() {
        val values = intArrayOf(
            5, 2, 6, 3, 1, 4,
            4, 3, 1, 6, 2, 5,
            1, 6, 5, 4, 3, 2,
            2, 4, 3, 5, 6, 1,
            3, 1, 4, 2, 5, 6,
            6, 5, 2, 1, 4, 3)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.standard6x6),
            initializeMap(6, values),
            PositionMap(Position[6, 6])
        )
        repeat(3) { transform(sudoku1) }

        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformS44Test1() {
        val values = intArrayOf(
            3, 2, 4, 1,
            1, 4, 2, 3,
            4, 3, 1, 2,
            2, 1, 3, 4)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.standard4x4),
            initializeMap(4, values),
            PositionMap(Position[4, 4])
        )
        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformS1616Test1() {
        map = PositionMap(Position[16, 16])

        val values = intArrayOf(
            1, 2, 13, 7, 11, 3, 4, 15, 16, 9, 10, 6, 12, 14, 8, 5,
            8, 15, 14, 5, 10, 13, 7, 9, 2, 1, 12, 11, 16, 3, 4, 6,
            6, 12, 3, 11, 8, 1, 14, 16, 4, 7, 15, 5, 2, 9, 10, 13,
            4, 16, 9, 10, 5, 12, 2, 6, 8, 3, 13, 14, 11, 15, 7, 1,
            10, 5, 11, 13, 3, 4, 12, 14, 1, 2, 16, 15, 6, 8, 9, 7,
            3, 6, 12, 8, 15, 11, 9, 1, 10, 4, 5, 7, 13, 2, 14, 16,
            9, 4, 1, 16, 7, 5, 13, 2, 6, 14, 8, 12, 10, 11, 3, 15,
            14, 7, 15, 2, 16, 10, 6, 8, 11, 13, 3, 9, 5, 12, 1, 4,
            16, 9, 4, 3, 13, 15, 8, 12, 7, 6, 2, 1, 14, 5, 11, 10,
            15, 10, 2, 6, 1, 7, 11, 5, 13, 12, 14, 8, 9, 4, 16, 3,
            7, 13, 5, 12, 9, 14, 10, 3, 15, 11, 4, 16, 1, 6, 2, 8,
            11, 14, 8, 1, 2, 6, 16, 4, 3, 5, 9, 10, 7, 13, 15, 12,
            13, 3, 10, 14, 6, 9, 15, 7, 5, 8, 1, 2, 4, 16, 12, 11,
            12, 11, 6, 15, 4, 2, 3, 10, 9, 16, 7, 13, 8, 1, 5, 14,
            5, 8, 7, 9, 14, 16, 1, 11, 12, 15, 6, 4, 3, 10, 13, 2,
            2, 1, 16, 4, 12, 8, 5, 13, 14, 10, 11, 3, 15, 7, 6, 9)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.standard16x16),
            initializeMap(16, values),
            PositionMap(Position[16, 16])
        )

        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformSudokuXTest1() {
        map = PositionMap(Position[9, 9])

        val values = intArrayOf(
            2, 8, 5, 6, 7, 9, 4, 3, 1,
            3, 9, 1, 5, 4, 2, 6, 8, 7,
            4, 7, 6, 8, 1, 3, 9, 5, 2,
            8, 4, 2, 1, 6, 5, 3, 7, 9,
            1, 5, 9, 7, 3, 4, 8, 2, 6,
            6, 3, 7, 2, 9, 8, 5, 1, 4,
            9, 2, 4, 3, 5, 1, 7, 6, 8,
            5, 6, 8, 9, 2, 7, 1, 4, 3,
            7, 1, 3, 4, 8, 6, 2, 9, 5)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.Xsudoku),
            initializeMap(9, values),
            PositionMap(Position[9, 9])
        )

        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformSudokuHyperTest1() {
        map = PositionMap(Position[9, 9])

        val values = intArrayOf(
            8, 4, 5, 2, 1, 3, 6, 7, 9,
            9, 3, 7, 4, 6, 5, 2, 1, 8,
            2, 6, 1, 9, 7, 8, 3, 4, 5,
            1, 2, 8, 5, 4, 7, 9, 6, 3,
            4, 7, 6, 8, 3, 9, 1, 5, 2,
            5, 9, 3, 6, 2, 1, 7, 8, 4,
            6, 8, 4, 1, 9, 2, 5, 3, 7,
            3, 5, 2, 7, 8, 6, 4, 9, 1,
            7, 1, 9, 3, 5, 4, 8, 2, 6)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.HyperSudoku),
            initializeMap(9, values),
            PositionMap(Position[9, 9])
        )
        validSudoku(sudoku1) `should be` true
        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformSudokuStairStepTest1() {
        map = PositionMap(Position[9, 9])

        val values = intArrayOf(
            7, 8, 1, 3, 4, 6, 5, 2, 9,
            2, 9, 6, 1, 8, 3, 4, 5, 7,
            5, 4, 7, 2, 9, 1, 6, 8, 3,
            9, 6, 8, 7, 2, 4, 1, 3, 5,
            3, 2, 4, 8, 5, 9, 7, 1, 6,
            1, 5, 3, 6, 7, 2, 8, 9, 4,
            6, 7, 5, 9, 3, 8, 2, 4, 1,
            4, 3, 2, 5, 1, 7, 9, 6, 8,
            8, 1, 9, 4, 6, 5, 3, 7, 2)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.stairstep),
            initializeMap(9, values),
            PositionMap(Position[9, 9])
        )
        validSudoku(sudoku1) `should be` true
        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformSudokuSquigglyATest1() {
        map = PositionMap(Position[9, 9])

        val values = intArrayOf(
            6, 7, 4, 3, 9, 5, 1, 8, 2,
            5, 4, 1, 8, 7, 2, 6, 3, 9,
            9, 3, 8, 2, 6, 1, 5, 7, 4,
            3, 1, 9, 6, 5, 8, 4, 2, 7,
            4, 5, 6, 7, 2, 3, 9, 1, 8,
            7, 8, 3, 1, 4, 9, 2, 6, 5,
            2, 6, 5, 4, 1, 7, 8, 9, 3,
            1, 2, 7, 9, 8, 4, 3, 5, 6,
            8, 9, 2, 5, 3, 6, 7, 4, 1)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.squigglya),
            initializeMap(9, values),
            PositionMap(Position[9, 9])
        )
        validSudoku(sudoku1) `should be` true
        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformSudokuSquigglyBTest1() {
        map = PositionMap(Position[9, 9])

        val values = intArrayOf(
            8, 9, 2, 5, 1, 4, 6, 3, 7,
            6, 3, 4, 7, 8, 9, 5, 2, 1,
            7, 1, 5, 3, 4, 6, 2, 9, 8,
            4, 8, 7, 6, 9, 2, 3, 1, 5,
            2, 4, 3, 8, 5, 7, 1, 6, 9,
            1, 5, 6, 9, 2, 8, 7, 4, 3,
            5, 2, 9, 1, 6, 3, 8, 7, 4,
            9, 7, 1, 2, 3, 5, 4, 8, 6,
            3, 6, 8, 4, 7, 1, 9, 5, 2)

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.squigglyb),
            initializeMap(9, values),
            PositionMap(Position[9, 9])
        )
        validSudoku(sudoku1) `should be` true
        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    @Test
    fun transformSamuraiTest() {
        val values = "819247365   983472651" +
                "274365198   751863429" +
                "653891472   426951378" +
                "496183257   697514283" +
                "731529846   235689714" +
                "582674931   814237965" +
                "328956714598362148597" +
                "145738629731548796132" +
                "967412583642179325846" +
                "      398416257      " +
                "      452387916      " +
                "      176259834      " +
                "814327965124783251649" +
                "732695841973625947813" +
                "956481237865491836572" +
                "329148576   156792384" +
                "167253489   249183756" +
                "548976123   837564921" +
                "695834712   378419265" +
                "283719654   564328197" +
                "471562398   912675438"

        val sudoku1 = Sudoku(
            TypeBuilder.getType(SudokuTypes.samurai),
            initializeSamuraiMap(values),
            PositionMap(Position[21, 21])
        )
        validSudoku(sudoku1) `should be` true
        repeat(100) { transform(sudoku1) }
        validSudoku(sudoku1) `should be` true
    }

    private fun initializeSamuraiMap(values: String): PositionMap<Int> {
        val map = PositionMap<Int>(Position[21, 21])
        val length = 21
        for (y in 0..<length) for (x in 0..<length)
            if (values[y * 21 + x] != ' ')
                map.put(Position[x, y], (values[y * 21 + x].toString() + "").toInt() - 1)

        return map
    }

    private fun initializeMap(length: Int, values: IntArray): PositionMap<Int> {
        for (i in values.indices) {
            values[i]--
        }

        // assertTrue(ss99[0] == 8);
        for (y in 0..<length) for (x in 0..<length) map.put(Position[x, y], values[y * length + x])

        return map
    }

    private fun validSudoku(sudoku: Sudoku): Boolean {
        var elcount: Int

        for (c in sudoku.sudokuType) {
            for (i in 0..<c.size) {
                elcount = 0
                for (p in c) {
                    if (sudoku.getCell(p)!!.solution == i) elcount++
                }
                if (elcount != 1) {
                    return false
                }
            }
        }
        return true
    }

    override fun generationFinished(sudoku: Sudoku) {
        Assertions.assertTrue(validSudoku(sudoku))
        // printSudoku9x9(sudoku, 9);
        (this as Object).notifyAll()
    }

    override fun generationFinished(sudoku: Sudoku, sl: List<Solution>) {
        Assertions.assertTrue(validSudoku(sudoku))
        // printSudoku9x9(sudoku, 9);
        (this as Object).notifyAll()
    }

    companion object {
        fun printSudoku9x9(sudoku: Sudoku, length: Int) {
            println("tada:")
            for (y in 0..<length) {
                for (x in 0..<length) print(" " + sudoku.getCell(Position[x, y])!!.solution)
                println()
            }
            println()
        }
    }
}
