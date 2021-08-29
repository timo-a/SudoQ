package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.utils.PrettySamuraiRepo
import de.sudoq.model.solverGenerator.utils.PrettyStandard9x9Repo
import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo
import de.sudoq.model.sudoku.complexity.Complexity
import org.amshove.kluent.*
import org.junit.jupiter.api.*
import java.io.File
import java.nio.file.Path

class LoadTest {

    @Test
    fun loadStandard9x9() {
        val sudokuRepo = PrettyStandard9x9Repo()
        val sudoku = sudokuRepo.read(
            Path.of("sudokus", "standard9x9", "lockedCandidates1.pretty"),
            Complexity.arbitrary,
            0)

        sudoku.`should not be null`()
    }

    @Test
    fun loadSamurai() {
        val sudokuRepo = PrettySamuraiRepo()
        val sudoku = sudokuRepo.read(
            Path.of("sudokus", "samurai", "samurai1.pretty"),
            Complexity.arbitrary,
            0)

        sudoku.`should not be null`()
    }
}