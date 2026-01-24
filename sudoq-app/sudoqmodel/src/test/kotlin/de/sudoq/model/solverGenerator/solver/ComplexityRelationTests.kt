package de.sudoq.model.solverGenerator.solver

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test


internal class ComplexityRelationTests {
    @Test
    fun test() {
        val types = ComplexityRelation.entries.toTypedArray()
        types.forEach { it `should be equal to` ComplexityRelation.valueOf(it.toString()) }
    }
}
