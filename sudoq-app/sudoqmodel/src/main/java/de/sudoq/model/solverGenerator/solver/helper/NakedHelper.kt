package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.NakedSetDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * Dieser konkrete SolveHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus.
 * Der NakedHelper sucht innerhalb der Constraints eines Sudokus nach n elementigen Teilmengen der Kandidaten, die in n Feldern vorkommen. (n
 * entspricht dem level des Helpers). Kommen in n Feldern lediglich dieselben n Kandidaten vor, so können diese
 * Kandidaten aus den anderen Listen des Constraints entfernt werden.
 *
 *
 * If there are n fields with only n distinct candidates in them, those candidates can't appear anywhere else.
 */
open class NakedHelper(sudoku: SolverSudoku, level: Int, complexity: Int) : SubsetHelper(sudoku, level, complexity) {
    private val labels = arrayOf(
            HintTypes.NakedSingle,
            HintTypes.NakedPair,
            HintTypes.NakedTriple,
            HintTypes.NakedQuadruple,
            HintTypes.NakedQuintuple,
            HintTypes.Naked__6_tuple,
            HintTypes.Naked__7_tuple,
            HintTypes.Naked__8_tuple,
            HintTypes.Naked__9_tuple,
            HintTypes.Naked_10_tuple,
            HintTypes.Naked_11_tuple,
            HintTypes.Naked_12_tuple,
            HintTypes.Naked_13_tuple)

    /**
     * collect all candidates appearing in fields with maximum `level` candidates.
     * This is 'naked'-specific code for the template method in superclass
     *
     * @param constraint Constraint whose candidates are to be filtered
     * @return the possible candidates
     */
    override fun collectPossibleCandidates(constraint: Constraint): BitSet {
        val possibleCandidates = BitSet()
        for (pos in constraint.getPositions()) {
            val currentCandidates: BitSet = sudoku.getCurrentCandidates(pos)
            val nrCandidates = currentCandidates.cardinality().toByte()
            if (nrCandidates in 1..level) //we only want up to n candidates per field
                possibleCandidates.or(currentCandidates)
        }
        //now we have constraintSet of all candidates in the constraint
        return possibleCandidates
    }

    /**
     * {@inheritDoc}
     */
    override fun updateNext(constraint: Constraint, buildDerivation: Boolean): Boolean {
        var foundSubset = false
        derivation = null
        val positions = Stack<Position>()
        for (p in constraint.getPositions()) if (sudoku.getCell(p)!!.isNotSolved) positions.add(p)
        do {
            val subsetCount = filterForSubsets(positions) //subsetPositions = {p | p ∈ positions, p.candidates ⊆ currentSet && |p.candidates| ∈ [1,level]}
            if (subsetCount == level) {
                /* store all fields other than the n fields of the subset in externalPositions */
                val externalPositions: MutableList<Position> = Stack()
                externalPositions.addAll(positions)
                externalPositions.removeAll(subsetPositions)
                for (pos in externalPositions) {
                    val currentPosCandidates: BitSet = sudoku.getCurrentCandidates(pos)
                    /* if currentPosCandidates contains candidates from the current subset i.e. something can be deleted */if (currentPosCandidates.intersects(currentSet)) {
                        //save original candidates
                        localCopy.assignWith(currentPosCandidates)

                        //delete all candidates that appear in currentSet
                        currentPosCandidates.andNot(currentSet)

                        /* We found a subset that does delete candidates,
                           initialize derivation obj. and fill it during remaining cycles of pos  */
                        if (buildDerivation) {
                            if (derivation == null) { //is this the first time?
                                derivation = initializeDerivation(constraint)
                            }
                            //what was deleted?
                            val relevant = localCopy.clone() as BitSet
                            val irrelevant = localCopy.clone() as BitSet
                            relevant.and(currentSet) //deleted notes
                            irrelevant.andNot(currentSet) //remaining notes
                            val field = DerivationCell(pos, relevant, irrelevant)
                            (derivation as NakedSetDerivation).addExternalCell(field)
                        }
                        foundSubset = true
                    }
                }
            }
        } while (!foundSubset && constraintSet.cardinality() > level && getNextSubset())
        return foundSubset
    }

    /*
     * stores all positions whose candidates are a subset of currentSet and have <= 'level' candidates -> eligible
     * and returns their number.
     * @return number of positions found
     */
    private fun filterForSubsets(positions: List<Position>): Int {
        subsetPositions.clear()
        for (pos in positions) {
            val currentCandidates = sudoku.getCurrentCandidates(pos)
            val nrCandidates = currentCandidates.cardinality()
            if (nrCandidates in 1..level)
                if (currentCandidates.isSubsetOf(currentSet))
                    subsetPositions.add(pos)
        }
        return subsetPositions.size
    }

    private fun initializeDerivation(constraint: Constraint): NakedSetDerivation {
        val derivation = NakedSetDerivation(hintType!!)
        derivation.setDescription("naked helper ($hintType)")
        derivation.constraint = constraint
        derivation.setSubsetCandidates(currentSet)
        for (p in subsetPositions) {
            val relevantCandidates = sudoku.getCurrentCandidates(p).clone() as BitSet
            val field = DerivationCell(p, relevantCandidates, BitSet())
            derivation.addSubsetCell(field)
        }
        return derivation
    }

    /**
     * Erzeugt einen neuen NakedHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
     * dabei der Größe der Symbolmenge nach der gesucht werden soll.
     *
     * @param sudoku     Das Sudoku auf dem dieser Helper operieren soll
     * @param level      Das Größe der Symbolmenge auf die der Helper hin überprüft
     * @param complexity Die Schwierigkeit der Anwendung dieser Vorgehensweise
     * @throws IllegalArgumentException Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
     */
    init {
        require(level in 1..labels.size) { "level must be ∈ [1," + labels.size + "] but is " + level }
        hintType = labels[level - 1]
    }
}