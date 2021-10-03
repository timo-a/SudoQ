package de.sudoq.model.sudoku.complexity

import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should throw`
import org.amshove.kluent.invoking
import org.junit.Assert
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class ComplexityConstraintTests {

    @Test
    fun standardTest() {
        val com = ComplexityConstraint(Complexity.medium, 32, 1000, 2000, 5)
        com.complexity.`should be equal to`(Complexity.medium)
        com.averageCells.`should be equal to`(32)
        com.minComplexityIdentifier.`should be equal to`(1000)
        com.maxComplexityIdentifier.`should be equal to`(2000)
        com.numberOfAllowedHelpers.`should be equal to`(5)
    }

    @Test
    fun testInvalidIdentifierRange() {
        invoking {
            ComplexityConstraint(Complexity.difficult, 5, 2000, 1000, 5)
        }.`should throw`(IllegalArgumentException::class)
    }

    @Test
    fun testNegativeMinIdentifier() {
        invoking {
            ComplexityConstraint(Complexity.easy, 5, -5, 100, 3)
        }.`should throw`(IllegalArgumentException::class)
    }

    @Test
    fun testNegativeAverageCells() {
        invoking {
            ComplexityConstraint(Complexity.easy, -5, 10, 100, 3)
        }.`should throw`(IllegalArgumentException::class)
    }

    @Test
    fun testIllegalNumberOfHelpers() {
        invoking {
            ComplexityConstraint(Complexity.infernal, 5, 1000, 2000, -5)
        }.`should throw`(IllegalArgumentException::class)
    }

    companion object {
        @JvmStatic
		fun returnsValues(
            c: ComplexityConstraint,
            complexity: Complexity?,
            averageFields: Int,
            minComplexityIdentifier: Int,
            maxComplexityIdentifier: Int,
            numberOfAllowedHelpers: Int
        ) {
            c.complexity.`should be equal to`(complexity)
            c.averageCells.`should be equal to`(averageFields.toLong())
            c.minComplexityIdentifier.`should be equal to`(minComplexityIdentifier.toLong())
            c.maxComplexityIdentifier.`should be equal to`(maxComplexityIdentifier.toLong())
            c.numberOfAllowedHelpers.`should be equal to`(numberOfAllowedHelpers.toLong())
        }
    }
}