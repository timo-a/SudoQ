package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class BranchingPoolTests {
    private val dummyPositionMap = PositionMap<CandidateSet>(Position[9, 9])

    @Test
    fun complete() {
        val pool = BranchingPool()
        pool.getBranching(Position[1, 5], 1, dummyPositionMap).candidate `should be equal to` 1
        pool.getBranching(Position[1, 5], 2, dummyPositionMap).position `should be equal to` Position[1, 5]

        // new branchings to be initialized
        pool.getBranching(Position[1, 5], 4, dummyPositionMap)

        pool.recycleAllBranchings()
        // return another branching
        pool.recycleLastBranching()
    }

}
