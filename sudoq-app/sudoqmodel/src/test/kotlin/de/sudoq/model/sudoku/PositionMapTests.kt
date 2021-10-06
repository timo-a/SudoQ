package de.sudoq.model.sudoku


import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.*

class PositionMapTests {

    @Test
    fun testStandardUsage() {
        val map = PositionMap<BitSet>(Position[9, 9])
        val b = BitSet()
        map.put(Position[3, 2], b)
        b.set(7)
        (map[Position[3, 2]]!![7]).`should be true`()
        b.clear()
        map[Position[3, 2]]!!.cardinality().`should be`(0)
        val map2 = map.clone()
        map[Position[3, 2]].`should be equal to`(map2[Position[3, 2]])
        map[Position[2, 3]].`should be equal to`(map2[Position[2, 3]])
    }

    @Test
    fun testIllegalArguments() {
        invoking {
            PositionMap<BitSet>(Position[1, 0])
        }.`should throw`(IllegalArgumentException::class)

        invoking {
            PositionMap<BitSet>(Position[0, 1])
        }.`should throw`(IllegalArgumentException::class)

        val map = PositionMap<BitSet>(Position[9, 9])
        invoking {
            map.put(Position[10, 9], BitSet())
        }.`should throw` (IllegalArgumentException::class)

        invoking {
            map[Position[10, 9]]
        }.`should throw`(IllegalArgumentException::class)

        invoking {
            map[Position[9, 10]]
        }.`should throw`(IllegalArgumentException::class)

    }
}