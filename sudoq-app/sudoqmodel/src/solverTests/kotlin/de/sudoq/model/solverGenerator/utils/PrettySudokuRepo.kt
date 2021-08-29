package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.solverGenerator.utils.parser.StandardParser
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.io.File
import java.nio.file.Path

open class PrettySudokuRepo(typeEnum: SudokuTypes) {

    private val typeRepo = SudokuTypeRepo(File("SudokuTypes"))
    val type = typeRepo.read(typeEnum.ordinal)

    fun read(relPath: Path, complexity: Complexity, id: Int = 0): Sudoku {
        val ls = loadAndClean(relPath)
        val sudoku = parseSudoku(id, complexity, ls)
        return sudoku
    }

    protected open fun parseSudoku(
        id: Int,
        complexity: Complexity,
        ls: List<List<String>>
    ): Sudoku {
        val parser = StandardParser()
        val sudoku = parser.parseSudoku(id, type, complexity, ls)
        return sudoku
    }


    protected fun loadAndClean(relPath: Path): List<List<String>> {
        val f =  getSudokuFile(relPath)
        val ls: List<List<String>> = f.readLines()
            .map {
                it.replace("|", "")
                    .replace("-", "")
                    .replace("+", "")
                    .replace("'", "")
                    .trim()
            }.filter {
                it.isNotEmpty()
            }.map {
                it.trim().split("\\s+".toRegex())
            }

        return ls
    }

    open protected fun getSudokuFile(path: Path): File {
        val classLoader = javaClass.classLoader
        return File(classLoader!!.getResource(path.toString()).file)
    }





}