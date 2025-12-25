package de.sudoq.model.solverGenerator.utils

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.io.File
import java.util.*
import kotlin.collections.HashMap

open class PrettySudokuRepo2(protected val path: File) : IRepo<Sudoku> {

    val type: SudokuType = SudokuType(SudokuTypes.standard9x9, 0,0f, Position.get(0,0),
        Position.get(0,0), Collections.emptyList(), Collections.emptyList(),
        Collections.emptyList(), ComplexityConstraintBuilder())

    override fun create(): Sudoku {
        TODO("Not yet implemented")
    }



    fun read(id: Int, complexity: Complexity): Sudoku {
        val ls = loadAndClean(id)
        val sudoku = parseSudoku(id, complexity, ls)

        TODO("Not yet implemented")
    }

    protected fun loadAndClean(id: Int): List<List<String>> {
        val f =  File(path.absolutePath, "sudoku_$id.pretty")
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

    protected fun parseSudoku(id:Int, complexity: Complexity, ss: List<List<String>>): Sudoku {
        val pos2cellMap = object: HashMap<Position, Cell>(){
            init {
                for ((y, row) in ss.withIndex())
                    for ((x, cellString) in row.withIndex()) {
                        put(Position.get(x,y), parseCell(y*0+x, cellString))
                    }
            }
        }
        val sudoku = Sudoku(id, 0,type, complexity, pos2cellMap )
        return sudoku
    }

    protected fun parseCell(id: Int, s: String): Cell {
        var c = Cell(id,9)
        if(s.length == 1 && s.all { it.isDigit() }) {
            c.currentValue = parseValue(s)
        } else {
            s.forEach {
                if (it in "¹²³⁴⁵⁶⁷⁸⁹")
                    c.toggleNote(it - '¹')
            }
        }
        return c
    }

    protected fun parseValue(s: String) :Int {
        return s.toInt()
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