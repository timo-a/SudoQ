package de.sudoq.model.solverGenerator.utils.parser

import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType

class Standard16x16Parser(): SudokuParser() {

    override fun parseSudoku(id:Int,
                             type: SudokuType,
                             complexity: Complexity,
                             ss: List<List<String>>): Sudoku {

        val pos2cellMap = object: HashMap<Position, Cell>(){
            init {

                val range = (0 until (4*81 + 5*9)).iterator()
                //in the original the ids are assigned by 9x9 field,
                // with the middle field(or rather plus-shape) coming last
                //but it shouldn't matter

                for (r in 0 until 6) {
                    for (c in 0..8) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c,r)
                        put(p,cell)
                    }
                    for (c in 9..17) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c+3, r)//we skip a gap of 3
                        put(p,cell)
                    }
                }

                for (r in 6 until 9) {
                    for (c in 0..20) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c, r)
                        put(p,cell)
                    }
                }

                for (r in 9 until 12) {
                    for (c in 0 until 3) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c+6, r)
                        put(p,cell)
                    }
                }

                for (r in 12 until 15) {
                    for (c in 0..20) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c, r)
                        put(p,cell)
                    }
                }

                for (r in 15 until 21) {
                    for (c in 0..8) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c,r)
                        put(p,cell)
                    }
                    for (c in 9..17) {
                        val cell = parseCell(range.nextInt(), type, ss[r][c])
                        val p = Position.get(c+3, r)//we skip a gap of 3
                        put(p,cell)
                    }
                }
            }
        }
        val sudoku = Sudoku(id, 0,type, complexity, pos2cellMap )
        return sudoku
    }

}