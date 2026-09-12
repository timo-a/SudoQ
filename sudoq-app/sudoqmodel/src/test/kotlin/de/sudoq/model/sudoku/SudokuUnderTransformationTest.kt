package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.TypeBuilder
import org.amshove.kluent.`should match all with`
import org.junit.jupiter.api.Test

class SudokuUnderTransformationTest {

    val pattern4x4 = intArrayOf(
        1,2,3,4,
        3,4,1,2,
        2,1,4,3,
        4,3,2,1
    )

    @Test
    fun `No empty cells in SudokuUnderTransformation`() {
        //given
        val sudokuType = TypeBuilder.getType(SudokuTypes.standard4x4)
        val solutionMap = PositionMap.Builder<Int>(Position[4, 4]).apply {
            for (y in 0 until 4) {
                for (x in 0 until 4) {
                    put(Position[x, y], pattern4x4[y * 4 + x] - 1)
                }
            }
        }.build()
        val sudoku = Sudoku(sudokuType, solutionMap, setOf(Position[0,0]))

        //when
        val t = sudoku.asSudokuUnderTransformation()

        //then
        t.`should match all with` { it.value in 0..3 }
    }
}