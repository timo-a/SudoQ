package de.sudoq.model.solverGenerator.utils.parser

import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType

class StandardParser : SudokuParser() {

    override fun parseSudoku(id:Int,
                             type: SudokuType,
                             complexity: Complexity,
                             ss: List<List<String>>): Sudoku {

        val pos2cellMap = object: HashMap<Position, Cell>(){
            init {
                for ((y, row) in ss.withIndex())
                    for ((x, cellString) in row.withIndex()) {
                        put(Position[x, y], parseCell(y*9+x, type, cellString))
                    }
            }
        }
        val sudoku = Sudoku(id, 0,type, complexity, pos2cellMap )
        return sudoku
    }

}