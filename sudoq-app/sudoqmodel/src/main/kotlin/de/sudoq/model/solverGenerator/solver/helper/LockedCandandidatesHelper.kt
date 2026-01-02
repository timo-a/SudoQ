package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.LockedCandidatesDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import java.util.*

/**Idea: We have 2 intersecting groups:
 * I = I_only + intersection
 * J = J_only + intersection
 *
 * a note that within I only appears in intersection, locks the note for J:
 * it has to be in intersection and cannot be in J_only
 *
 */
class LockedCandandidatesHelper(sudoku: SolverSudoku, complexity: Int) :
    SolveHelper(sudoku, complexity) {

    override fun update(buildDerivation: Boolean): Boolean {
        var success = false
        val constraints: MutableList<Constraint> = ArrayList()
        for (c in sudoku.sudokuType) {
            constraints.add(c)
        }
        /* compare all constraints */for (i in constraints.indices) for (j in i + 1 until constraints.size) {
            /* if they intersect */
            val constraintI = constraints[i]
            val constraintJ = constraints[j]
            val positionsI: List<Position> = constraintI.getPositions()
            val positionsJ: List<Position> = constraintJ.getPositions()
            if (intersect(positionsI, positionsJ)) {
                // get disjunctive sets of positions:
                val common = intersection(positionsI, positionsJ)
                val cutoutI = cut(positionsI, common) //Constraint I without intersection
                val cutoutJ = cut(positionsJ, common) //Constraint J without intersection

                //all notes of such a set
                val commonNotes = collectNotes(common)
                val cutoutINotes = collectNotes(cutoutI)
                val cutoutJNotes = collectNotes(cutoutJ)

                /* look for notes of I that appear only in the intersection, and in J as well*/
                //notes that appear in the intersection but not the rest of I
                val inIntersectionButNotInI = commonNotes.clone() as BitSet
                inIntersectionButNotInI.andNot(cutoutINotes)

                //do they also appear in rest of J?
                var removableNotes = inIntersectionButNotInI.clone() as BitSet
                removableNotes.and(cutoutJNotes)
                var toBeRemovedFrom = cutoutJ
                var reducibleConstraint = constraintJ
                var lockedConstraint = constraintI
                if (removableNotes.isEmpty) {
                    //now do the other direction
                    /* look for notes of J that appear only in the intersection, and in I as well*/
                    //notes that appear in the intersection but not the rest of J
                    val inIntersectionButNotInJ = commonNotes.clone() as BitSet
                    inIntersectionButNotInJ.andNot(cutoutJNotes)

                    //do they also appear in rest of I?
                    removableNotes = inIntersectionButNotInJ.clone() as BitSet
                    removableNotes.and(cutoutINotes)
                    toBeRemovedFrom = cutoutI
                    reducibleConstraint = constraintI
                    lockedConstraint = constraintJ
                }
                if (!removableNotes.isEmpty) {
                    success = true
                    if (buildDerivation) {
                        buildDerivation(
                            lockedConstraint,
                            reducibleConstraint,
                            removableNotes,
                            toBeRemovedFrom
                        )
                    }

                    //remove first of all removable notes
                    val first = removableNotes.nextSetBit(0)
                    for (p in toBeRemovedFrom) sudoku.getCurrentCandidates(p).clear(first)
                    return true
                }
            }
        }
        return false
    }

    private fun buildDerivation(
        lockedConstraint: Constraint, reducibleConstraint: Constraint,
        removableNotes: BitSet, toBeRemovedFrom: List<Position>
    ) {
        /* since the derivations seem to be never used, I'm a bit sloppy here...
                            *  as Blocks the intersecting constraints are added
                            *  as fields all fields that have a note removed are added*/
        val first = removableNotes.nextSetBit(0)
        val lastDerivation = LockedCandidatesDerivation()
        lastDerivation.lockedConstraint = lockedConstraint
        lastDerivation.reducibleConstraint = reducibleConstraint
        lastDerivation.setRemovableNotes(removableNotes)
        val relevantCandidates = BitSet()
        relevantCandidates.set(first)
        for (p in toBeRemovedFrom) if (sudoku.getCurrentCandidates(p)[first]) {
            val irrelevantCandidates = sudoku.getCurrentCandidates(p).clone() as BitSet
            irrelevantCandidates.clear(first)
            lastDerivation.addDerivationCell(
                DerivationCell(
                    p,
                    relevantCandidates,
                    irrelevantCandidates
                )
            )
        }
        //Todo better: list fields where it is removed
        lastDerivation.setDescription("Note " + (first + 1))
        derivation = lastDerivation
    }

    private fun collectNotes(l: List<Position>): BitSet {
        val merged = BitSet()
        if (l.isNotEmpty()) for (p in l) merged.or(sudoku.getCurrentCandidates(p))
        return merged
    }

    companion object {
        /**
         * Determines whether Lists a,b have a common(by equals) element
         * @param a
         * @param b
         * @param <T> any element in the list needs to have equals defined
         * @return true iff i.equals(j) == true for at least one i € a, j € b
        </T> */
        private fun <T> intersect(a: List<T>, b: List<T>): Boolean {
            for (t1 in a) for (t2 in b) if (t1 == t2) return true
            return false
        }

        fun <T> intersection(a: List<T>, b: List<T>): List<T> {
            val intersection: MutableList<T> = ArrayList()
            for (t1 in a) for (t2 in b) if (t1 == t2) intersection.add(t1)
            return intersection
        }

        private fun <T> cut(a: List<T>, b: List<T>): List<T> {
            val cut: MutableList<T> = ArrayList(a)
            cut.removeAll(b)
            return cut
        }
    }

    /**
     * Erzeugt einen neuen HiddenHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
     * dabei der Größe der Symbolmenge nach der gesucht werden soll.
     *
     * @param sudoku
     * Das Sudoku auf dem dieser Helper operieren soll
     * @param complexity
     * Die Schwierigkeit der Anwendung dieser Vorgehensweise
     * @throws IllegalArgumentException
     * Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
     */
    init {
        hintType = HintTypes.LockedCandidatesExternal
    }
}