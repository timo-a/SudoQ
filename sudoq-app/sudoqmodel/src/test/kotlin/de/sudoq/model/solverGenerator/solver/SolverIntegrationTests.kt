package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuBuilderLegacy
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
    private lateinit var sudoku: Sudoku
    private var sudoku16x16: Sudoku? = null
    private var solver: Solver? = null

    //this is a dummy so it compiles todo use xmls from resources
    private val sudokuTypeRepo: ReadRepo<SudokuType> = SudokuTypeRepo4Tests()

    @BeforeEach
    fun before() {
        sudoku = SudokuBuilderLegacy(SudokuTypes.standard9x9, sudokuTypeRepo)
            .complexity(Complexity.arbitrary)
            .build()
        solver = Solver(sudoku)
        sudoku16x16 = SudokuBuilderLegacy(SudokuTypes.standard16x16, sudokuTypeRepo)
            .complexity(Complexity.arbitrary)
            .build()
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun easySudoku1() {
        val assignment = """
              4  |5    |     
                 |7 6  |     
            8 _ _|_ 1 _|9 _ 7
            1    |9    |    8
            9    |     |     
            _ 3 6|2 5 _|_ 1 _
                 |3    |6 7  
                3|  2 6|    4
            6 9  |  7  |1    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)

        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Easy 1) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun easySudoku2() {
        val assignment = """
            2 8 1|  5  |  9 4
            4    |2   1|  5 7
            _ _ 7|4 _ 6|_ 3 _
                5|6 4  |7   8
            7 4 8|3 2 5|  6 1
            _ 9 _|_ _ _|3 _ _
            1    |9   7|5    
            5 3 6|  1  |4 7  
                9|5 3  |2 1  
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.difficult

        //assertEquals(solver.validate(solution), ComplexityRelation.MUCH_TOO_EASY);
        skeleton("Solution (Easy 2) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun easySudoku3() {
        val assignment = """
            1   7   8     4  
                  4   5 1   3
            4 3 2 1   9   8 6
            7   3   4   6   5
            5     2   6 7   4
            6 2     5 7 8    
              8 5 7     9 1 2
            2     5 9   3    
            9 7   3 1        
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Easy 3) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku1() {
        val assignment = """
                3 4 7   2   1
              6 8 2          
            4         1 6 5  
            9   6 8   4     5
                2   3   7   4
                4 1       2  
                      3     6
              3 5   1 8 4 9  
            7   9   5   8    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Medium 1) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku2() {
        val assignment = """
                1   5 2 8 3 7
            7 2       1      
                5 3       2  
              4 9 2   8 1    
                  9     2   6
              8 2   3       9
                      6     4
            1 3     2     5  
            9   4 7 8   6   2
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Medium 2) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku3() {
        val assignment = """
            5 6 7 9 2     8  
                3   1   7    
                      6   2 5
              3 9 2     5   1
                1   6   8    
            6   5   9 1 2 3  
              4   6       5  
                8   4     7  
              9     5 2   1 3
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Medium 3) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun mediumSudoku4() {
        val assignment = """
              2 3     4   7 9
            7       6        
            1     5     6 3  
              4 8   7     5  
              6       3   8  
            5             4  
              3 6           7
                9   4 1     5
                        4    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.easy

        //assertEquals(solver.validate(solution), ComplexityRelation.MUCH_TOO_DIFFICULT);
        skeleton("Solution (Medium 4) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku1() {
        val assignment = """
            3   9 5     2    
                      1 9    
            5 7   2       8  
                             
              5     9 2     6
                    6 4 5   7
              6   3          
                8     6 4    
                    1       3
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Difficult 1) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku2() {
        val assignment = """
                6 8 3   2 1  
            9         6      
                3         4  
              5 7           1
                  7         8
              2 4   1       7
              9       8     5
              1     4     3  
            7     9     8    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Difficult 2) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku3() {
        val assignment = """
                1     7   4 6
            3   7   2 8      
                        2    
              3   9     6    
              6           5  
                9 2   6   3  
                6            
                  3 5   8   2
            2 5   8     9    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Difficult 3) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku4() {
        val assignment = """
                    8     3  
                      7     1
            3 1 6     4   8  
                1           7
            7     6 1 9     3
            9           8    
              8   5     4 2  
            6     7          
              5     2     7  
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.infernal

        // assertEquals(solver.validate(solution), ComplexityRelation.TOO_EASY);
        skeleton("Solution (Difficult 4) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun difficultSudoku5() {
        val assignment = """
                          1  
              7       8   2  
                  3     7    
            6 8 9            
                      5      
                  6 2       3
              1 3   5     9 7
                    4        
                6 9 1     4 2
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION
        solver!!.solverSudoku.complexity = Complexity.easy

        //assertEquals(ComplexityRelation.TOO_DIFFICULT, solver.validate(solution));
        skeleton("Solution (Difficult 5) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun infernalSudoku1() {
        val assignment = """
              7   5     6    
                3         1  
                    2        
                    1 3   9  
              5              
                      9      
                  8     4   7
            2               5
            1                
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Infernal 1) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun infernalSudoku2() {
        val assignment = """
            9     5   6 4   1
                2     9   8  
                        9    
              6         3    
            5 3     9     6 4
                4         7  
                8            
              7   9     5    
            1   5 7   4     6
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Infernal 2) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun infernalSudoku3() {
        val assignment = """
                  7          
                      8 2 1 9
              2 6     9 8    
              3   2     9   4
                8   7   3    
            2   9     3   6  
                5 6     7 3  
            9 7 3 1          
                      7      
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (Infernal 3) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun worldsHardestSudoku() {
        val assignment = """
            1         7   9  
              3     2       8
                9 6     5    
                5 3     9    
              1     8       2
            6         4      
            3             1  
              4             7
                7       3    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        solver!!.solverSudoku.complexity = Complexity.easy
        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.INVALID
        solver!!.solverSudoku.complexity = Complexity.arbitrary
        val pair2 = solver!!.validateDeprecated()
        pair2.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (world's hardest) - Complexity: ", pair2.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun worldsHardestSudoku2() {
        val assignment = """
                5 3          
            8             2  
              7     1   5    
            4         5 3    
              1     7       6
                3 2       8  
              6   5         9
                4         3  
                      9 7    
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)

        val pair = solver!!.validateDeprecated()
        pair.first `should be equal to` ComplexityRelation.CONSTRAINT_SATURATION

        skeleton("Solution (world's hardest 2) - Complexity: ", pair.second!!)
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun notSolvableSudoku() {
        val assignment = """
            3   9|5    |2    
                 |    1|9    
            5 7 _|2 _ _|_ 8  
                 |     |     
              5  |  9 2|    6
            _ _ _|_ 6 4|5 _ 7
              6  |3    |     
                8|    6|4    
            _ _ _|_ 1 _|_ _ 5
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)
        //assertEquals(solver.validate(solution), ComplexityRelation.INVALID);
        while (solver!!.solveOne(true) != null);
        solver!!.solveOne(true) `should be` null
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    fun ambiguouslySolvable() {
        val assignment = """
            . 7  |5    |6    
                3|     |  1  
            _ _ _|_ 2 _|_ _ _
                 |  1 3|  9  
              5  |     |     
            _ _ _|_ _ 9|_ _ _
                 |8    |4   7
            2    |     |    5
            _ _ _|_ _ _|_ _ _
        """.trimIndent()
        val lsudoku = parse9x9FromBlock(assignment)
        solver = Solver(lsudoku)
        //todo investigate validation, what are we currently using in main? does that work?
        //assertEquals(ComplexityRelation.INVALID, solver.validate(solution));
    }

    fun parse9x9FromBlock(block: String): Sudoku {
        val sudoku = SudokuBuilderLegacy(SudokuTypes.standard9x9, sudokuTypeRepo)
            .complexity(Complexity.arbitrary)
            .build()

        block.lines().forEachIndexed { y, line ->
            line.chunked(2).forEachIndexed { x, value ->
                when (value[0]) {
                    ' ', '_', '.' -> {}
                    in '1'..'9' -> sudoku.getCell(Position[x, y]).currentValue =
                        value[0].digitToInt() - 1

                    else -> throw IllegalArgumentException("Invalid character: $value")
                }
            }
        }
        return sudoku
    }

    private fun skeleton(title: String?, solution: PositionMap<Int>) {
        val size = sudoku.sudokuType.size

        // copy solution to current value
        for (j in 0..<size.y) {
            for (i in 0..<size.x) {
                sudoku.getCell(Position[i, j]).currentValue = solution[Position[i, j]]
            }
        }

        // check constraints
        for (c in sudoku.sudokuType) {
            c.isSaturated(sudoku) `should be` true
        }

        // print solution if wanted
        println(title + solver!!.solverSudoku.complexityValue)
        if (PRINT_SOLUTIONS) {
            val sb = StringBuilder()
            for (j in 0..<size.y) {
                for (i in 0..<size.x) {
                    val value = sudoku.getCell(Position[i, j]).currentValue
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

    companion object {
        private const val PRINT_SOLUTIONS = false
    }
}
