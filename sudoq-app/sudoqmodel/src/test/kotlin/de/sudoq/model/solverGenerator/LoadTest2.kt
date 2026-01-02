package de.sudoq.model.solverGenerator

import de.sudoq.model.solverGenerator.utils.PrettySudokuRepo2
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import org.amshove.kluent.*
import org.junit.jupiter.api.*
import java.io.File

class LoadTest2 {


    @Test
    fun test_loading() {
        val sudokuRepo = PrettySudokuRepo2(SudokuTypeRepo4Tests())
        sudokuRepo.`should not be null`()

    }

}