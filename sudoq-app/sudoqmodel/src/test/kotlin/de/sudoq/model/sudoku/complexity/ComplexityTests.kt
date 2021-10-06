package de.sudoq.model.sudoku.complexity

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class ComplexityTests {

    /**
     * Verifies that toString hasn't been tampered with.
     * Dubious use.
     */
    @ParameterizedTest
    @EnumSource(Complexity::class)
    fun test(c: Complexity) {
        Complexity.valueOf(c.toString()).`should be equal to` (c)
    }
}