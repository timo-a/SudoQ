package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.Position
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should contain all`
import org.amshove.kluent.`should not be null`
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
        val positions = setOf(
            Position[0, 0], Position[5, 0],
            Position[0, 2], Position[5, 2]
        )
        for (c in ss18) {
            if (c.toString().contains("Block 0")) {
                c `should contain all` positions
            }
        }
    }


    @Test
    fun enumTypeTest() {
        sst.enumType `should be` SudokuTypes.standard9x9
    }
}

class SST18x18 : SudokuType(SudokuTypes.standard4x4, 9, 0f, Position[1,1], Position[1,1],
    ArrayList(), ArrayList(), ArrayList(),
    ComplexityConstraintBuilder(HashMap())) {

    override fun getStandardAllocationFactor(): Float = 0f

}
