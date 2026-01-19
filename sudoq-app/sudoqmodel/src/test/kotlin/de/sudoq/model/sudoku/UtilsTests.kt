package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.Utils.classifyGroup
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.Test
import java.util.ArrayList

class UtilsTests {
    /**
     *
     */
    @Test
    fun classifyTest() {
        val positionList: MutableList<Position> = ArrayList()
        positionList.add(Position[2, 4])
        positionList.add(Position[2, 5])
        positionList.add(Position[2, 6])
        positionList.add(Position[2, 7])
        positionList.add(Position[2, 9])

        getGroupShape(positionList).`should be`(Utils.ConstraintShape.Column)
        classifyGroup(positionList).`should be equal to`("col 3")


        //Row
        positionList.clear()
        positionList.add(Position[2, 6])
        positionList.add(Position[3, 6])
        positionList.add(Position[4, 6])
        positionList.add(Position[5, 6])

        getGroupShape(positionList).`should be`(Utils.ConstraintShape.Row)
        classifyGroup(positionList).`should be equal to`("row 7")


        //diagonal
        positionList.clear()
        positionList.add(Position[2, 4])
        positionList.add(Position[3, 5])
        positionList.add(Position[4, 6])
        positionList.add(Position[5, 7])
        getGroupShape(positionList).`should be`(Utils.ConstraintShape.Diagonal)
        classifyGroup(positionList).`should be equal to`("a diagonal")


        //diagonal
        positionList.clear()
        positionList.add(Position[2, 4])
        positionList.add(Position[2, 5])
        positionList.add(Position[3, 4])
        positionList.add(Position[3, 5])
        getGroupShape(positionList).`should be`(Utils.ConstraintShape.Block)
        classifyGroup(positionList).`should be equal to`("a block containing (3, 5)")
    }
}