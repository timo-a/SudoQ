package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.XWingDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.*
import java.util.*

//TODO test this!!!
/**
 * Erzeugt einen neuen HiddenHelper für das spezifizierte Suduoku.
 * Idea:
 * We have a '#' of 2 rows+2columns.
 * The 4 intersection fields all feature a common note n.
 * for the 2 rows (the 2 columns) the note appears only in the intersection i.e. is locked there -&gt; they are the locked constraints
 * therefore the columns (the rows) have them in the intersection too and n can be deleted everywhere else.
 */
class XWingHelper(sudoku: SolverSudoku, complexity: Int) : SolveHelper(sudoku, complexity) {

    init {
        hintType = HintTypes.XWing
    }

    private fun separateIntoRowColumn(pool: Iterable<Constraint>, rows: MutableList<Constraint>, cols: MutableList<Constraint>) {
        for (c in pool) {
            when (getGroupShape(c.getPositions())) {
                Utils.ConstraintShape.Row -> rows.add(c)
                Utils.ConstraintShape.Column -> cols.add(c)
            }
        }
    }

    override fun update(buildDerivation: Boolean): Boolean {
        val constraints: Iterable<Constraint> = sudoku.sudokuType!!

        /* collect rows / cols */
        val rows: MutableList<Constraint> = ArrayList()
        val cols: MutableList<Constraint> = ArrayList()
        separateIntoRowColumn(constraints, rows, cols)

        /* compare all constraints, look for a '#': 2 rows, 2 col intersecting
        * move clockwise starting north-west i.e. topLeft*/
        for (c1 in 0 until cols.size - 1) for (r1 in 0 until rows.size - 1) {
            val col1 = cols[c1]
            val row1 = rows[r1]
            val topLeft = intersectionPoint(row1, col1)
            if (topLeft == null || !sudoku.getCell(topLeft)!!.isNotSolved) continue
            for (c2 in c1 + 1 until cols.size) {
                val col2 = cols[c2]

                //avoid overlapping columns as can happen with samurai sudokus
                if (intersectionPoint<Position?>(col1, col2) != null) continue
                val topRight = intersectionPoint(row1, col2)
                if (topRight == null || !sudoku.getCell(topRight)!!.isNotSolved) continue
                for (r2 in r1 + 1 until rows.size) {
                    val row2 = rows[r2]

                    //avoid overlapping columns as can happen with samurai sudokus
                    if (intersectionPoint<Position?>(row1, row2) != null) continue
                    val bottomRight = intersectionPoint(col2, row2)
                    val bottomLeft = intersectionPoint(row2, col1)
                    if (bottomRight != null && sudoku.getCell(bottomRight)!!.isNotSolved
                     && bottomLeft  != null && sudoku.getCell(bottomLeft)!!.isNotSolved) {
                        /* we found a # of 2rows, 2 cols now check if 2 are locked ...*/
                        val intersectionPoints = arrayOf(topLeft, topRight, bottomLeft, bottomRight)
                        if (testForLockedness(row1, row2, col1, col2, intersectionPoints, buildDerivation))
                            return true
                    }
                }
            }
        }
        return false
    }

    private fun testForLockedness(row1: Constraint, row2: Constraint,
                                  col1: Constraint, col2: Constraint,
                                  intersectionPoints: Array<Position>, buildDerivation: Boolean): Boolean {
        val candidateNotes = intersectNotes(Arrays.asList(*intersectionPoints))
        for (note in candidateNotes.setBits) if (xWing(row1, row2, col1, col2, note, intersectionPoints, buildDerivation) ||
                xWing(col1, col2, row1, row2, note, intersectionPoints, buildDerivation)) return true
        return false //in case candidateNotes.getSetBits() == {}
    }

    private fun xWing(row1: Constraint,
                      row2: Constraint,
                      col1: Constraint,
                      col2: Constraint,
                      note: Int,
                      intersectionPoints: Array<Position>,
                      buildDerivation: Boolean): Boolean {
        //Xwing: row1, row2 haben in den schnittpunkten eine note die bez. Zeile nur dort vorkommt.
        //       note is therefor locked, can be deletd elsewhere in col1,col2
        val rowLocked = countOccurrences(note.toShort(), row1).toInt() == 2 && countOccurrences(note.toShort(), row2).toInt() == 2
        val removableStuffInColumns = countOccurrences(note.toShort(), col1) > 2 ||
                countOccurrences(note.toShort(), col2) > 2
        return if (rowLocked && removableStuffInColumns) {
            val canBeDeleted = deleteNote(col1, col2, note, intersectionPoints)
            if (buildDerivation) buildDerivation(row1, row2, col1, col2, canBeDeleted, note)
            true
        } else false
    }

    private fun deleteNote(col1: Constraint, col2: Constraint, note: Int,
                           intersectionPoints: Array<Position>): List<Position> {
        val canBeDeleted: MutableList<Position> = ArrayList()
        for (p in col1.getPositions()) if (sudoku.getCurrentCandidates(p).isSet(note)) //don't Try getField.isNoteSet! that accesses the actual sudokus candidates
            canBeDeleted.add(p)
        for (p in col2) if (sudoku.getCurrentCandidates(p).isSet(note)) canBeDeleted.add(p)
        for (p in intersectionPoints) canBeDeleted.remove(p)

        /* delete notes */
        for (p in canBeDeleted) sudoku.getCurrentCandidates(p).clear(note)
        return canBeDeleted
    }

    private fun buildDerivation(row1: Constraint, row2: Constraint, col1: Constraint, col2: Constraint,
                                canBeDeleted: Iterable<Position>, note: Int) {
        val internalDerivation = XWingDerivation()
        internalDerivation.setLockedConstraints(row1, row2)
        internalDerivation.setReducibleConstraints(col1, col2)
        for (p in canBeDeleted) {
            val relevant = CandidateSet()
            relevant.set(note)
            val irrelevant = CandidateSet()
            internalDerivation.addDerivationCell(DerivationCell(p, relevant, irrelevant))
            internalDerivation.note = note
        }
        derivation = internalDerivation
    }

    /** intersect the notes of all cells at the given positions
     *
     * requires a non empty list
     */
    private fun intersectNotes(l: List<Position>): CandidateSet {
        require(l.isNotEmpty())

        val sets = l.map(sudoku::getCurrentCandidates)

        val init = sets.first().clone() as CandidateSet

        sets.slice(1 until l.size)
            .forEach(init::and)

        return init
    }

    /*
    *
    * */
    private fun countOccurrences(note: Short, positions: Iterable<Position>): Short {
        return positions.filter { p ->  sudoku.getCurrentCandidates(p).isSet(note.toInt()) }
                        .size
                        .toShort()
    }

    companion object {
        /** TODO some sort of Maybe would be better than returning null...
         * Determines the first T found to occur in a and b (by equals())
         * @param a
         * @param b
         * @param <T> any element in the list needs to have equals defined
         * @return an element i where i.equals(j) for  i € a, j € b, null iff none is found
        </T> */
        private fun <T> intersectionPoint(a: Iterable<T>, b: Iterable<T>): T? {
            for (t1 in a) for (t2 in b) if (t1 == t2) return t1
            return null
        }
    }

}