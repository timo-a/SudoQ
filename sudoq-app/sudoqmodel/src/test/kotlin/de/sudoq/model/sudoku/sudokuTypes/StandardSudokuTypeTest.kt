package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Position
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class StandardSudokuTypeTest {
    var sst = TypeBuilder.getType(SudokuTypes.standard9x9)

    @Test
    fun test() {
        val p = sst.size
        p.`should not be null`()
        p.x.`should be`(9)
        p.y.`should be`(9)
    }

    @Test
    fun nonQuadraticBlocksTest() {
        val ss18: SudokuType = SST18x18()
        val p0 = Position[0, 0]
        val p1 = Position[5, 0]
        val p2 = Position[0, 2]
        val p3 = Position[5, 2]
        for (c in ss18) {
            if (c.toString().contains("Block 0")) {

                c.`should contain all`(listOf(p0, p1, p2,p3))
            }
        }
    }


    @Test
    fun enumTypeTest() {
        sst.enumType.`should be`(SudokuTypes.standard9x9)
    }
}

class SST18x18 : SudokuType(SudokuTypes.standard4x4, 9, 0f, Position[1,1], Position[1,1],
    ArrayList(), ArrayList(), ArrayList(),
    ComplexityConstraintBuilder(HashMap())) {

    override fun getStandardAllocationFactor(): Float = 0f

}
