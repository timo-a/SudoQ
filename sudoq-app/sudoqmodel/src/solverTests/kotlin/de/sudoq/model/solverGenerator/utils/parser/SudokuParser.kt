package de.sudoq.model.solverGenerator.utils.parser

import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import java.text.ParseException
import java.util.stream.IntStream.range

open class SudokuParser {

    open fun parseSudoku(id:Int, type: SudokuType, complexity: Complexity, ss: List<List<String>>): Sudoku {
        val pos2cellMap = object: HashMap<Position, Cell>(){
            init {
                for ((y, row) in ss.withIndex())
                    for ((x, cellString) in row.withIndex()) {
                        put(Position.get(x,y), parseCell(y*0+x, type, cellString))
                    }
            }
        }
        val sudoku = Sudoku(id, 0,type, complexity, pos2cellMap )
        return sudoku
    }

    protected fun parseCell(id: Int, type: SudokuType, s: String): Cell {
        val c = Cell(id, type.numberOfSymbols)

        fun a(s: String): Boolean {return true}

        when(s) {
            //single digit -> already solved
            in ("0".."9") -> c.currentValue = parseValue(s)
            //dot -> e
            "." -> range(0, c.numberOfValues).forEach {
                c.toggleNote(it)
            }
            else -> s.forEach {
                if (it in "¹²³⁴⁵⁶⁷⁸⁹")
                    c.toggleNote(it - '¹')
                else
                    throw ParseException("Cell could not be parsed: >$s<", -1)
            }
        }

        return c
    }

    protected fun parseValue(s: String) :Int {
        return s.toInt() - 1
    }

}