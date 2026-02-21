package de.sudoq.model.sudoku


import org.amshove.kluent.invoking
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be true`
import org.amshove.kluent.`should throw`
import org.junit.jupiter.api.Test
import java.util.BitSet

class PositionMapTests {

    @Test
    fun standardUsage() {
        val map = PositionMap.Builder<BitSet>(Position[9, 9])
        val b = BitSet()
        map.put(Position[3, 2], b)
        b.set(7)
        (map.build()[Position[3, 2]][7]).`should be true`()
        b.clear()
        map.build()[Position[3, 2]].cardinality().`should be`(0)
    }

    @Test
    fun illegalArguments() {
        invoking {
            PositionMap.Builder<BitSet>(Position[1, 0])
        }.`should throw`(IllegalArgumentException::class)

        invoking {
            PositionMap.Builder<BitSet>(Position[0, 1])
        }.`should throw`(IllegalArgumentException::class)

        val map = PositionMap.Builder<BitSet>(Position[9, 9])
        invoking {
            map.put(Position[10, 9], BitSet())
        }.`should throw` (IllegalArgumentException::class)

        invoking {
            map.build()[Position[10, 9]]
        }.`should throw`(IllegalArgumentException::class)

        invoking {
            map.build()[Position[9, 10]]
        }.`should throw`(IllegalArgumentException::class)

    }
}