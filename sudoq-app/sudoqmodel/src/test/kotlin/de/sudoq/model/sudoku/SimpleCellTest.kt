package de.sudoq.model.sudoku

import org.amshove.kluent.invoking
import org.amshove.kluent.`should not throw`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SimpleCellTest {

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6])
    fun `solutions in range should succeed in initialization`(solution: Int) {
        invoking { SimpleCell(true, solution, 0, 7) } `should not throw` (Exception::class)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 7, 8, 9, 15])
    fun `solutions out of range should fail in initialization`(solution: Int) {
        invoking { SimpleCell(true, solution, 0, 7) } `should throw` (IllegalArgumentException::class)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6])
    fun `solutions in range should succeed in setter`(solution: Int) {
        //given
        val cell = SimpleCell(true, 0, 0, 7)
        //when
        val action = { cell.value = solution }
        //then
        invoking(action) `should not throw` (Exception::class)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 7, 8, 9, 15])
    fun `solutions out of range should fail in setter`(solution: Int) {
        //given
        val cell = SimpleCell(true, 0, 0, 7)
        //when
        val action = { cell.value = solution }
        //then
        invoking(action) `should throw` (IllegalArgumentException::class)
    }

}