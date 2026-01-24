package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku

object SudokuTestUtilities {
    fun printSudoku(sudoku: Sudoku) {
        val sb = StringBuilder()
        for (j in 0..<sudoku.sudokuType.size.y) {
            for (i in 0..<sudoku.sudokuType.size.x) {
                val value = sudoku.getCell(Position[i, j])!!.currentValue
                var op = value.toString() + ""
                if (value.toString().length < 2) op = " " + value
                if (value == -1) op = "--"
                sb.append(op + ", ")
            }
            sb.append("\n")
        }
        println(sb)
    }
}
