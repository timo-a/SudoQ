package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import org.apache.commons.lang3.Validate
import java.io.File
import java.nio.file.Path
import java.text.ParseException
import java.util.stream.IntStream.range
import kotlin.collections.HashMap

open class PrettySudokuRepo2(val typeRepo: SudokuTypeRepo4Tests) : IRepo<Sudoku> {

    override fun create(): Sudoku {
        TODO("Not yet implemented")
    }

    fun read(path: Path, complexity: Complexity): Sudoku {
        val ls = loadAndClean(path)
        val filename = path.fileName.toString()
        val id: Int = filename
            .removeSuffix(".pretty")
            .takeLastWhile { it.isDigit() }
            .toInt()
        val typeE: SudokuTypes = when (filename.split("_")[0]) {
            "9" -> SudokuTypes.standard9x9
            "x" -> SudokuTypes.Xsudoku
            else -> {
                throw NotImplementedError("Type $filename not implemented")
            }
        }
        val type = typeRepo.read(typeE.ordinal)
        val sudoku = parseSudoku(id, type, complexity, ls)
        return sudoku
    }

    protected fun loadAndClean(relPath: Path): List<List<String>> {
        val f =  getSudokuFile(relPath)
        val ls: List<List<String>> = f.readLines()
            .map {
                it.replace("|", "")
                .replace("-", "")
                .replace("+", "")
            }.filter {
                it.isNotEmpty()
            }.map {
                it.trim().split("\\s+".toRegex())
            }

        return ls
    }

    open protected fun getSudokuFile(path: Path): File {
        val classLoader = javaClass.classLoader
        return File(classLoader.getResource(path.toString())!!.file)
    }

    protected fun parseSudoku(id:Int, type: SudokuType, complexity: Complexity, ss: List<List<String>>): Sudoku {
        val pos2cellMap = object: HashMap<Position, Cell>(){
            init {
                for ((y, row) in ss.withIndex())
                    for ((x, cellString) in row.withIndex()) {
                        put(Position[x, y], parseCell(y*9+x, cellString))
                    }
            }
        }
        val sudoku = Sudoku(id, 0,type, complexity, pos2cellMap )
        return sudoku
    }

    protected fun parseCell(id: Int, s: String): Cell {

        val c = when {
            //single digit -> already solved
            s in ("0123456789") -> Cell(false, parseValue(s), id,9)
            //.2 -> empty with solution 2 (-1)
            s.startsWith(".") -> Cell(true, parseValue(s.removePrefix(".")), id,9)
            s.all { it in "123456789¹²³⁴⁵⁶⁷⁸⁹" } -> {
                val solution: Int = s
                    .filter { it in "123456789" }
                    .map { parseValue(it.toString()) }
                    .single()
                Cell(true, solution, id,9)
                    .also { it.toggleNote(solution) }
                    .also {
                        s.forEach { c ->
                        if (c in "¹²³⁴⁵⁶⁷⁸⁹")
                            it.toggleNote(c - '¹')
                    }
                }
            }
            else -> {throw IllegalArgumentException("Invalid value for cell: $s")}
        }
        return c
    }

    protected fun parseValue(s: String) :Int {
        return s.toInt() - 1
    }



    override fun update(t: Sudoku): Sudoku {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun ids(): List<Int> {
        TODO("Not yet implemented")
    }

    override fun read(id: Int): Sudoku {
        TODO("Not yet implemented")
    }


}