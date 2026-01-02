package de.sudoq.model.game

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.sudoku.complexity.Complexity
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.util.*

class GameDataTests {

    @Test
    fun initTest() {
        val g: GameData
        val d = Date()
        g = GameData(0, d, true, SudokuTypes.squigglya, Complexity.difficult)
        g.complexity.`should be`(Complexity.difficult)
        g.type.`should be`(SudokuTypes.squigglya)
        g.id.`should be`(0)

        g.playedAt.toString().`should be equal to`(d.toString())
        g.isFinished.`should be true`()
    }

    @Test
    fun compareTest() {
        val d = Date()
        d.time = 2
        var gd1 = GameData(0, d, false, SudokuTypes.squigglya, Complexity.difficult)
        val d2 = Date()
        d2.time = 400000
        var gd2 = GameData(0, d2, false, SudokuTypes.squigglya, Complexity.difficult)
        gd2.shouldBeGreaterThan(gd1)
        gd2 = GameData(0, d2, true, SudokuTypes.squigglya, Complexity.difficult)
        gd1.compareTo(gd2).shouldBe(1)
        gd1 = GameData(0, d, true, SudokuTypes.squigglya, Complexity.difficult)
        gd2 = GameData(0, d2, false, SudokuTypes.squigglya, Complexity.difficult)
        gd2.shouldBeGreaterThan(gd1)
    }

    companion object {
        protected const val dateFormat = "yyyy:MM:dd HH:mm:ss"
    }
}