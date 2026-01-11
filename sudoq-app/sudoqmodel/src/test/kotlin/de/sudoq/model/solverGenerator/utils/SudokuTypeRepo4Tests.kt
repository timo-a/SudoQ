package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.UniqueConstraintBehavior
import de.sudoq.model.sudoku.complexity.Complexity.*
import de.sudoq.model.sudoku.complexity.Complexity.easy
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.PermutationProperties
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes.*
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes.Xsudoku

import kotlin.math.sqrt

operator fun Position.plus(other: Position): Position {
    return Position[this.x + other.x, this.y + other.y]
}

class SudokuTypeRepo4Tests : ReadRepo<SudokuType> {

    override fun read(id: Int): SudokuType =
        when(id) {
            standard9x9.ordinal -> SudokuType(standard9x9, 9,
                0.35f, Position[9,9], Position[3,3],
                createSquareConstraints(9),
                mkPermutationProperties(0,1,2,3,4,5,6,7,8,9),
                ArrayList(),
                ComplexityConstraintBuilder(mapOf(
                         easy to ComplexityConstraint(easy,      40,  400,       1200,          2),// todo nur 2?
                       medium to ComplexityConstraint(medium,    32, 1200,       2500,          3),
                    difficult to ComplexityConstraint(difficult, 28, 2500,       4000, 2147483647),
                     infernal to ComplexityConstraint(infernal,  27, 4000,      25000, 2147483647),
                    arbitrary to ComplexityConstraint(arbitrary, 32,    1, 2147483647, 2147483647)
                )))

            standard4x4.ordinal -> SudokuType(standard4x4, 4,
                0.25f, Position[4,4], Position[2,2],
                createSquareConstraints(4),
                mkPermutationProperties(0,1,2,3,4,5,6,7,8,9),
                ArrayList(), ComplexityConstraintBuilder(HashMap()))

            standard6x6.ordinal -> SudokuType(standard6x6, 6,
                0.35f, Position[6,6], Position[3,2],
                create66Constraints(),
                mkPermutationProperties(2,3,4,5,8,9),
                ArrayList(), ComplexityConstraintBuilder(HashMap()))

            standard16x16.ordinal -> SudokuType(standard16x16, 16,
                0.25f, Position[16,16], Position[4,4],
                createSquareConstraints(16),
                mkPermutationProperties(0,1,2,3,4,5,6,7,8,9),
                ArrayList(),
                ComplexityConstraintBuilder(mapOf(
                         easy to ComplexityConstraint(easy,      190,  900,       1350,          3),
                       medium to ComplexityConstraint(medium,    140, 1350,       4000,          4),
                    difficult to ComplexityConstraint(difficult, 120, 4000,       8000, 2147483647),
                     infernal to ComplexityConstraint(infernal,  105, 8000,      25000, 2147483647),
                    arbitrary to ComplexityConstraint(arbitrary,  32,    1, 2147483647, 2147483647)
                )))

            samurai.ordinal -> SudokuType(samurai, 9,
                0.05f, Position[21,21], Position[3,3],
                createSamuraiConstraints(),
                mkPermutationProperties(0,1,6,7,8,9),
                ArrayList(), ComplexityConstraintBuilder(HashMap()))

            Xsudoku.ordinal ->  SudokuType(Xsudoku, 9,
                0.25f, Position[9,9], Position[3,3],
                createXConstraints(),
                mkPermutationProperties(0,1,6,7,8,9),
                ArrayList(), ComplexityConstraintBuilder(HashMap()))

            HyperSudoku.ordinal ->  SudokuType(HyperSudoku, 9,
                0.25f, Position[9,9], Position[3,3],
                createHyperConstraints(),
                mkPermutationProperties(0,1,6,7,8,9),
                ArrayList(),
                ComplexityConstraintBuilder(mapOf(
                         easy to ComplexityConstraint(easy,      40,  500,       1500,          2),//todo grenzen lassen lücken, und so wenig helper?
                       medium to ComplexityConstraint(medium,    32, 1500,       3500,          3),
                    difficult to ComplexityConstraint(difficult, 28, 3500,       6000, 2147483647),
                     infernal to ComplexityConstraint(infernal,  27, 6000,      25000, 2147483647),
                    arbitrary to ComplexityConstraint(arbitrary, 32,    1, 2147483647, 2147483647)
                )))
            squigglya.ordinal ->  SudokuType(squigglya, 9,
                0.25f, Position[9,9], Position[0,0],
                createSquigglyAConstraints(),
                mkPermutationProperties(0,6,7,8,9),
                ArrayList(), ComplexityConstraintBuilder(HashMap()))
            squigglyb.ordinal ->  SudokuType(squigglyb, 9,
                0.2f, Position[9,9], Position[0,0],
                createSquigglyBConstraints(),
                ArrayList(),//todo gibt es echt gar keine?
                ArrayList(), ComplexityConstraintBuilder(HashMap()))
            stairstep.ordinal ->  SudokuType(stairstep, 9,
                0.25f, Position[9,9], Position[4,3],
                createStairstepConstraints(),
                ArrayList(3),
                ArrayList(),
                ComplexityConstraintBuilder(mapOf(
                         easy to ComplexityConstraint(easy,      40,  500,        800,          2),//todo grenzen lassen lücken, und so wenig helper?
                       medium to ComplexityConstraint(medium,    32,  750,       1050,          3),
                    difficult to ComplexityConstraint(difficult, 28, 1000,       2500, 2147483647),
                     infernal to ComplexityConstraint(infernal,  27, 2500,      25000, 2147483647),
                    arbitrary to ComplexityConstraint(arbitrary, 32,    1, 2147483647, 2147483647)
                )))

            else -> throw NotImplementedError("No SudokuType with id $id, I haven't defined ${SudokuTypes.entries[id]} yet")
        }

    fun createSquareConstraints(len: Int): List<Constraint> {
        val blockLen : Int = sqrt(len.toFloat()).toInt()
        val rows = (0 until len).map {
            val positions = (0 until len).map { x -> Position(x, it) }.toTypedArray()
            Constraint(UniqueConstraintBehavior(), ConstraintType.LINE, "Row $it", *positions)
        }
        val columns = (0 until len).map {
            val positions = (0 until len).map { y -> Position(it, y) }.toTypedArray()
            Constraint(UniqueConstraintBehavior(), ConstraintType.LINE, "Column $it", *positions)
        }

        val tlBlock = (0 until len).map { Position[it % blockLen, it / blockLen] }
        val blocks = (0 until len)
            .map { Position[(it % blockLen) * blockLen, (it / blockLen) * blockLen] }
            .map { offset -> tlBlock.map { p -> p + offset } }
            .mapIndexed { it, ps ->
                Constraint(UniqueConstraintBehavior(), ConstraintType.BLOCK, "Block $it", *ps.toTypedArray()) }

        return rows + columns + blocks
    }

    fun create66Constraints(): List<Constraint> {
        val len = 6
        val rows = (0 until len).map {
            val positions = (0 until len).map { x -> Position(x, it) }.toTypedArray()
            Constraint(UniqueConstraintBehavior(), ConstraintType.LINE, "Row $it", *positions)
        }
        val columns = (0 until len).map {
            val positions = (0 until len).map { y -> Position(it, y) }.toTypedArray()
            Constraint(UniqueConstraintBehavior(), ConstraintType.LINE, "Column $it", *positions)
        }

        val tlBlock = (0 until len).map { Position[it % 3, it / 3] }
        val blocks = (0 until len)
            .map { Position[(it % 2) * 3, (it / 2) * 2] }
            .map { offset -> tlBlock.map { p -> p + offset } }
            .mapIndexed { it, ps ->
                Constraint(UniqueConstraintBehavior(), ConstraintType.BLOCK, "Block $it", *ps.toTypedArray()) }

        return rows + columns + blocks
    }

    fun createXConstraints(): List<Constraint> {
        val downRight = Constraint(UniqueConstraintBehavior(), ConstraintType.EXTRA,
            "Extra block diagonal down right",
            *(0 until 9).map { Position[it, it] }.toTypedArray()
        )
        val upRight = Constraint(UniqueConstraintBehavior(), ConstraintType.EXTRA,
            "Extra block diagonal up right",
            *(0 until 9).map { Position[it, 8 - it] }.toTypedArray()
        )

        return createSquareConstraints(9) + listOf(downRight, upRight)
    }

    fun createHyperConstraints(): List<Constraint> {
        val len = 9
        val tlBlock = (0 until len).map { Position[it % 3, it / 3] }
        val extraBlocks = arrayOf(Position[1,1], Position[5,1], Position[1,5], Position[5,5], )
            .map { offset -> tlBlock.map { p -> p + offset } }
            .mapIndexed { it, ps ->
                Constraint(UniqueConstraintBehavior(), ConstraintType.BLOCK, "Extra block $it", *ps.toTypedArray()) }

        return createSquareConstraints(9) + extraBlocks
    }

    fun createSamuraiConstraints(): List<Constraint> {
        val topLeft = createSquareConstraints(9)
        val topRight = topLeft
            .map { c -> Constraint(UniqueConstraintBehavior(), ConstraintType.LINE,
                c.name.split(" ").let { it[0] + " " + (it[1].toInt() + 9) },
                *(c.getPositions().map { p -> p + Position[12,0] }.toTypedArray()))
            }
        val bottomLeft = topLeft
            .map { c -> Constraint(UniqueConstraintBehavior(), ConstraintType.LINE,
                c.name.split(" ").let { it[0] + " " + (it[1].toInt() + 18) },
                *(c.getPositions().map { p -> p + Position[0,12] }.toTypedArray()))
            }

        val bottomRight = topLeft
            .map { c -> Constraint(UniqueConstraintBehavior(), ConstraintType.LINE,
                c.name.split(" ").let { it[0] + " " + (it[1].toInt() + 27) },
                *(c.getPositions().map { p -> p + Position[12,12] }.toTypedArray()))
            }

        val middleLines = topLeft
            .filter { c -> c.type == ConstraintType.LINE }
            .map { c -> Constraint(UniqueConstraintBehavior(), ConstraintType.LINE,
                c.name.split(" ").let { it[0] + " " + (it[1].toInt() + 36) },
                *(c.getPositions().map { p -> p + Position[6,6] }.toTypedArray()))
            }


        val middleBlocks = topLeft
            .filter { c -> c.type == ConstraintType.BLOCK }
            .map { c -> c.name.split(" ")[1].toInt() to c.getPositions() }
            .filter { it.first in setOf(1,3,4,5,7) }
            .sortedBy { it.first }
            .mapIndexed { i, (_, ps) -> Constraint(UniqueConstraintBehavior(), ConstraintType.BLOCK,
                "Block ${i+36}", *ps.map { p -> p + Position[6,6] }.toTypedArray())
            }

        return topLeft + topRight + bottomLeft + bottomRight + middleLines + middleBlocks
    }

    fun createSquigglyAConstraints(): List<Constraint> {
        val pattern = """
        A  A  B  B  B  B  B  C  C
        A  A  A  B  B  B  C  C  C
        D  A  A  A  B  C  C  C  E
        D  D  A  F  F  F  C  E  E
        D  D  D  F  F  F  E  E  E
        D  D  G  F  F  F  H  E  E
        D  G  G  G  I  H  H  H  E
        G  G  G  I  I  I  H  H  H
        G  G  I  I  I  I  I  H  H
        """.trimIndent()

        val blocks: List<Constraint> = parseBlockConstraints(pattern)

        return createSquareConstraints(9).filter { c -> c.type != ConstraintType.BLOCK } + blocks
    }


    fun createSquigglyBConstraints(): List<Constraint> {
        val pattern = """
        A  A  A  A  A  B  B  B  B
        A  A  C  C  C  C  D  B  B
        A  C  C  E  F  C  D  D  B
        A  E  E  E  F  C  C  D  B
        G  E  F  F  F  F  F  D  B
        G  E  H  H  F  D  D  D  I
        G  E  E  H  F  D  H  H  I
        G  G  E  H  H  H  H  I  I
        G  G  G  G  I  I  I  I  I
        """.trimIndent()

        val blocks: List<Constraint> = parseBlockConstraints(pattern)

        return createSquareConstraints(9).filter { c -> c.type != ConstraintType.BLOCK } + blocks
    }

    fun createStairstepConstraints(): List<Constraint> {
        val pattern = """
        A  A  A  A  B  B  B  C  C
        A  A  A  B  B  B  C  C  C
        A  A  B  B  B  C  C  C  C
        D  D  D  D  E  E  E  F  F
        D  D  D  E  E  E  F  F  F
        D  D  E  E  E  F  F  F  F
        G  G  G  G  H  H  H  I  I
        G  G  G  H  H  H  I  I  I
        G  G  H  H  H  I  I  I  I
        """.trimIndent()

        val blocks: List<Constraint> = parseBlockConstraints(pattern)

        return createSquareConstraints(9).filter { c -> c.type != ConstraintType.BLOCK } + blocks
    }

    private fun parseBlockConstraints(pattern: String): List<Constraint> {
        val blocks: List<Constraint> = pattern.lines().mapIndexed { y, line ->
            line.split("  ").mapIndexed { x, c -> c to Position[x, y] }
        }.flatten()
            .groupBy({ it.first }, { it.second })
            .entries.map { (key, posList) ->
                Constraint(
                    UniqueConstraintBehavior(),
                    ConstraintType.LINE,
                    "Block $key",
                    *posList.sortedByYX().toTypedArray()
                )
            }
        return blocks
    }

    //todo Positions soll Comparable implementieren, dann kann Constraints im Constructor selber sortieren
    val Position.Companion.readingOrder: Comparator<Position>
        get() = compareBy<Position> { it.y }.thenBy { it.x }

    /**
     * Extension to sort any Iterable of Positions using the Y-X reading order.
     */
    fun Iterable<Position>.sortedByYX(): List<Position> {
        return this.sortedWith(Position.readingOrder)
    }

    fun mkPermutationProperties(vararg i: Int): List<PermutationProperties> =
        i.map { PermutationProperties.entries[it] }

}

