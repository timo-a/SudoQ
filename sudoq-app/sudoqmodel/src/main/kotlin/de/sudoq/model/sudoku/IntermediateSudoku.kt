package de.sudoq.model.sudoku

import de.sudoq.model.sudoku.sudokuTypes.SudokuType

open class IntermediateSudoku(
    type: SudokuType,
    cells: HashMap<Position, Cell>, //todo why isn't this a [PositionMap]?)
    protected val cellPositions: MutableMap<Int, Position>
): AbstractSudoku<Cell>(type) {

    /** Eine Map, welche jeder Position des Sudokus ein Feld zuweist */
    var cells: HashMap<Position, Cell> = cells
        private set

    constructor(type: SudokuType) : this(type, PositionMap.Builder<Int>(type.size).build(), setOf())

    /**
     * All Cells are set as editable.
     *
     * @param type Type of the Sudoku
     * @param map A Map from Positions to solution values. Values in pre-filled Cells are negated. (actually bitwise negated)
     * @param setValues A Map from Position to whether the value is pre-filled.
     */
    constructor(type: SudokuType, map: PositionMap<Int>, setValues: Set<Position>)
            : this(type, HashMap(), HashMap()) {
        var cellIdCounter = 1

        // iterate over the constraints of the type and create the fields
        type.flatMap(Constraint::getPositions).distinct().forEach { position ->
            val solution = if (map.contains(position)) map[position] else null
            cells[position] = when {
                solution != null -> {
                    val editable = position !in setValues
                    Cell(editable, solution, cellIdCounter, type.numberOfSymbols)
                }
                else -> {
                    Cell(cellIdCounter, type.numberOfSymbols)
                }
            }
            cellPositions[cellIdCounter++] = position
        }
    }

    /**
     * Returns the [Cell] at the specified [Position].
     *
     * @param position Position of the cell
     * @return Cell at the [Position]
     * @throws IllegalArgumentException if the position is not mapped to a [Cell].
     */
    fun getCell(position: Position): Cell {
        return requireNotNull(cells[position])
    }

    /**
     * Returns the [Cell] at the id.
     *
     * @param id ID of the [Cell] to return
     * @return the [Cell] at the specified id
     */
    fun getCell(id: Int): Cell {
        val p = cellPositions.getValue(id)
        return getCell(p)
    }
}