package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuBuilder
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit

internal class SolverIntegrationTests {
    private var sudoku: Sudoku? = null
    private var sudoku16x16: Sudoku? = null
    private var solver: Solver? = null
    private var solution: PositionMap<Int?>? = null

    //this is a dummy so it compiles todo use xmls from resources
    private val sudokuTypeRepo: ReadRepo<SudokuType> = SudokuTypeRepo4Tests()

    @BeforeEach
    fun before() {
        sudoku = SudokuBuilder(SudokuTypes.standard9x9, sudokuTypeRepo).createSudoku()
        sudoku!!.complexity = Complexity.arbitrary
        solver = Solver(sudoku!!)
        sudoku16x16 = SudokuBuilder(SudokuTypes.standard16x16, sudokuTypeRepo).createSudoku()
        sudoku16x16!!.complexity = Complexity.arbitrary
        solution = PositionMap(sudoku!!.sudokuType.size)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun easySudoku1() {
        sudoku!!.getCell(Position[1, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 1])!!.currentValue = 5
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 7
        sudoku!!.getCell(Position[4, 2])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 8
        sudoku!!.getCell(Position[8, 2])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[0, 4])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 4
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 0
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 7])!!.currentValue = 5
        sudoku!!.getCell(Position[8, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 5
        sudoku!!.getCell(Position[1, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 0

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Easy 1) - Complexity: ")
    }

    private fun skeleton(title: String?) {
        val size = sudoku!!.sudokuType.size

        // copy solution to current value
        for (j in 0..<size.y) {
            for (i in 0..<size.x) {
                sudoku!!.getCell(Position[i, j])!!.currentValue = solution!!.get(Position[i, j])!!
            }
        }

        // check constraints
        for (c in sudoku!!.sudokuType) {
            c.isSaturated(sudoku!!) `should be` true
        }

        // print solution if wanted
        println(title + solver!!.solverSudoku.complexityValue)
        if (PRINT_SOLUTIONS) {
            val sb = StringBuilder()
            for (j in 0..<size.y) {
                for (i in 0..<size.x) {
                    val value = sudoku!!.getCell(Position[i, j])!!.currentValue
                    var op = value.toString() + ""
                    if (value.toString().length < 2) op = " " + value
                    if (value == -1) op = "--"
                    sb.append(op).append(", ")
                }
                sb.append("\n")
            }
            println(sb)
        }
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun easySudoku2() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[0, 1])!!.currentValue = 3
        sudoku!!.getCell(Position[0, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 6])!!.currentValue = 0
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 0])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 3
        sudoku!!.getCell(Position[1, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[2, 4])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 3
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 4])!!.currentValue = 2
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 2
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 4
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 7])!!.currentValue = 6
        sudoku!!.getCell(Position[7, 8])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 0

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.difficult

        //assertEquals(solver.validate(solution), ComplexityRelation.MUCH_TOO_EASY);
        skeleton("Solution (Easy 2) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun easySudoku3() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 7
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[3, 1])!!.currentValue = 3
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 1])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 3
        sudoku!!.getCell(Position[1, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 2])!!.currentValue = 8
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 7
        sudoku!!.getCell(Position[8, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[0, 3])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 5
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[0, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 3
        sudoku!!.getCell(Position[0, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[1, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 5])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 8
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 1
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 1
        sudoku!!.getCell(Position[3, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 0

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Easy 3) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku1() {
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[1, 1])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[3, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 3
        sudoku!!.getCell(Position[5, 2])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[0, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[5, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[2, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 2
        sudoku!!.getCell(Position[6, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 3
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[3, 5])!!.currentValue = 0
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 7])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 7])!!.currentValue = 8
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 7

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Medium 1) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku2() {
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 7
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[8, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[1, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[3, 4])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[1, 5])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[8, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 3
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 0
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[2, 8])!!.currentValue = 3
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 5
        sudoku!!.getCell(Position[8, 8])!!.currentValue = 1

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Medium 2) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku3() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 0])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 1])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[5, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 1
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 4])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 4])!!.currentValue = 7
        sudoku!!.getCell(Position[0, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 3
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 7
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 7])!!.currentValue = 6
        sudoku!!.getCell(Position[1, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 8])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 8])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 8])!!.currentValue = 2

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Medium 3) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku4() {
        sudoku!!.getCell(Position[1, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[5, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[0, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 1])!!.currentValue = 5
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 0
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[4, 3])!!.currentValue = 6
        sudoku!!.getCell(Position[7, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 4])!!.currentValue = 7
        sudoku!!.getCell(Position[0, 5])!!.currentValue = 4
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 8
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[5, 7])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 3

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.easy

        //assertEquals(solver.validate(solution), ComplexityRelation.MUCH_TOO_DIFFICULT);
        skeleton("Solution (Medium 4) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku1() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 1])!!.currentValue = 8
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 2])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[6, 5])!!.currentValue = 4
        sudoku!!.getCell(Position[8, 5])!!.currentValue = 6
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 7
        sudoku!!.getCell(Position[5, 7])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 8])!!.currentValue = 2

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Difficult 1) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku2() {
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 7
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[0, 1])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 3
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[3, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 5])!!.currentValue = 6
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 6])!!.currentValue = 7
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 7

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Difficult 2) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku3() {
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 0])!!.currentValue = 5
        sudoku!!.getCell(Position[0, 1])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 5
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 7
        sudoku!!.getCell(Position[8, 7])!!.currentValue = 1
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 1
        sudoku!!.getCell(Position[1, 8])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 8

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Difficult 3) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku4() {
        sudoku!!.getCell(Position[4, 0])!!.currentValue = 7
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[1, 2])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 2])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 8
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 5])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 7
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 1
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 7])!!.currentValue = 6
        sudoku!!.getCell(Position[1, 8])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 8])!!.currentValue = 6

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.infernal

        // assertEquals(solver.validate(solution), ComplexityRelation.TOO_EASY);
        skeleton("Solution (Difficult 4) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku5() {
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[1, 1])!!.currentValue = 6
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 2
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 3])!!.currentValue = 5
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 8
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[2, 8])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 0
        sudoku!!.getCell(Position[7, 8])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 8])!!.currentValue = 1

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.easy

        //assertEquals(ComplexityRelation.TOO_DIFFICULT, solver.validate(solution));
        skeleton("Solution (Difficult 5) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun infernalSudoku1() {
        sudoku!!.getCell(Position[1, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 1])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 0

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Infernal 1) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun infernalSudoku2() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 0])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 8
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 8
        sudoku!!.getCell(Position[7, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 3
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 6])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 7])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 4
        sudoku!!.getCell(Position[0, 8])!!.currentValue = 0
        sudoku!!.getCell(Position[2, 8])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[5, 8])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 8])!!.currentValue = 5

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Infernal 2) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun infernalSudoku3() {
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 1])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 2])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 1
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[8, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[2, 4])!!.currentValue = 7
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 4])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[3, 7])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 8])!!.currentValue = 6

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Infernal 3) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun worldsHardestSudoku() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[7, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 1])!!.currentValue = 2
        sudoku!!.getCell(Position[4, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[2, 2])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[2, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 7
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[0, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[0, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 6])!!.currentValue = 0
        sudoku!!.getCell(Position[1, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 7])!!.currentValue = 6
        sudoku!!.getCell(Position[2, 8])!!.currentValue = 6
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 2

        solver!!.solverSudoku.complexity = Complexity.easy
        solver!!.validate(solution) `should be equal to` ComplexityRelation.INVALID
        solver!!.solverSudoku.complexity = Complexity.arbitrary
        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (world's hardest) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun worldsHardestSudoku2() {
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[0, 1])!!.currentValue = 7
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 1
        sudoku!!.getCell(Position[1, 2])!!.currentValue = 6
        sudoku!!.getCell(Position[4, 2])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[0, 3])!!.currentValue = 3
        sudoku!!.getCell(Position[5, 3])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 6
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 5])!!.currentValue = 2
        sudoku!!.getCell(Position[3, 5])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 5])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 4
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 8
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[7, 7])!!.currentValue = 2
        sudoku!!.getCell(Position[5, 8])!!.currentValue = 8
        sudoku!!.getCell(Position[6, 8])!!.currentValue = 6

        solver!!.validate(solution) `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (world's hardest 2) - Complexity: ")
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun notSolvableSudoku() {
        sudoku!!.getCell(Position[0, 0])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 0])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 1
        sudoku!!.getCell(Position[5, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[6, 1])!!.currentValue = 8
        sudoku!!.getCell(Position[0, 2])!!.currentValue = 4
        sudoku!!.getCell(Position[1, 2])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[7, 2])!!.currentValue = 7
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[4, 4])!!.currentValue = 8
        sudoku!!.getCell(Position[5, 4])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 4])!!.currentValue = 5
        sudoku!!.getCell(Position[4, 5])!!.currentValue = 5
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 3
        sudoku!!.getCell(Position[6, 5])!!.currentValue = 4
        sudoku!!.getCell(Position[8, 5])!!.currentValue = 6
        sudoku!!.getCell(Position[1, 6])!!.currentValue = 5
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 2
        sudoku!!.getCell(Position[2, 7])!!.currentValue = 7
        sudoku!!.getCell(Position[5, 7])!!.currentValue = 5
        sudoku!!.getCell(Position[6, 7])!!.currentValue = 3
        sudoku!!.getCell(Position[4, 8])!!.currentValue = 0
        sudoku!!.getCell(Position[8, 8])!!.currentValue = 4

        //assertEquals(solver.validate(solution), ComplexityRelation.INVALID);
        while (solver!!.solveOne(true) != null);
        solver!!.solveOne(true) `should be` null
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun ambiguouslySolvable() {
        sudoku!!.getCell(Position[1, 0])!!.currentValue = 6
        sudoku!!.getCell(Position[3, 0])!!.currentValue = 4
        sudoku!!.getCell(Position[6, 0])!!.currentValue = 5
        sudoku!!.getCell(Position[2, 1])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 1])!!.currentValue = 0
        sudoku!!.getCell(Position[4, 2])!!.currentValue = 1
        sudoku!!.getCell(Position[4, 3])!!.currentValue = 0
        sudoku!!.getCell(Position[5, 3])!!.currentValue = 2
        sudoku!!.getCell(Position[7, 3])!!.currentValue = 8
        sudoku!!.getCell(Position[1, 4])!!.currentValue = 4
        sudoku!!.getCell(Position[5, 5])!!.currentValue = 8
        sudoku!!.getCell(Position[3, 6])!!.currentValue = 7
        sudoku!!.getCell(Position[6, 6])!!.currentValue = 3
        sudoku!!.getCell(Position[8, 6])!!.currentValue = 6
        sudoku!!.getCell(Position[0, 7])!!.currentValue = 1
        sudoku!!.getCell(Position[8, 7])!!.currentValue = 4

        solver = Solver(sudoku!!)
        //todo investigate validation, what are we currently using in main? does that work?
        //assertEquals(ComplexityRelation.INVALID, solver.validate(solution));
    }

    companion object {
        private const val PRINT_SOLUTIONS = false
    }
}
