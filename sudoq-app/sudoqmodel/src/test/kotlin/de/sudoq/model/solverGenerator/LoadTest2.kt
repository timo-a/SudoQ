package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo2
import org.amshove.kluent.*
import org.junit.jupiter.api.*
import java.io.File

class LoadTest2 {


    @Test
    fun test_loading() {
        val sudokuRepo = PrettySudokuRepo2(File("sudokus"))
        sudokuRepo.`should not be null`()

    }

}