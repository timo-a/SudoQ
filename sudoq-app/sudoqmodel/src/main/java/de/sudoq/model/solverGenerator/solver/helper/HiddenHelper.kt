package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.HiddenSetDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * Dieser konkrete SolveHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus.
 * Der HiddenHelper sucht innerhalb der Constraints eines Sudokus nach n Kandidaten,
 * die lediglich noch in denselben n Feldern vorkommen.
 * (n entspricht dem level des Helpers).
 * Ist dies der Fall, müssen diese n Kandidaten in den n Feldern in irgendeiner
 * Kombination eingetragen werden und daher können alle übrigen symbole aus diesen n feldern entfert werden.
 *
 * [¹²³⁴][¹²³⁴][³..][³..] -&gt; [¹²][¹²][³..][³..]
 * Looks for a constraint and a set of n candidates(i.e. distinct symbols that are not a solution).
 * For these candidates must hold that they exclusively appear in n unsolved fields.
 * In that case, they are all the solutions to these n fields (in some order) and all other candidates within these n fields can be removed
 *
 */
open class HiddenHelper(sudoku: SolverSudoku?, level: Int, complexity: Int) : SubsetHelper(sudoku!!, level, complexity) {
    private val labels = arrayOf(HintTypes.HiddenSingle,
            HintTypes.HiddenPair,
            HintTypes.HiddenTriple,
            HintTypes.HiddenQuadruple,
            HintTypes.HiddenQuintuple,
            HintTypes.Hidden__6_tuple,
            HintTypes.Hidden__7_tuple,
            HintTypes.Hidden__8_tuple,
            HintTypes.Hidden__9_tuple,
            HintTypes.Hidden_10_tuple,
            HintTypes.Hidden_11_tuple,
            HintTypes.Hidden_12_tuple,
            HintTypes.Hidden_13_tuple)
    private override var derivation: HiddenSetDerivation? = null

    /**
     * Collect all candidates appearing in this constraint.
     * This is 'hidden'-specific code for the template method in superclass
     *
     * @param constraint Constraint object
     * @return BitSet of all candidates in the constraint
     */
    override fun collectPossibleCandidates(constraint: Constraint): BitSet {
        val constraintSet = BitSet()
        for (pos in constraint.getPositions()) {
            constraintSet.or(sudoku.getCurrentCandidates(pos))
        }
        //now we have constraintSet of all candidates in the constraint
        return constraintSet
    }

    /**
     * {@inheritDoc}
     */
    override fun updateNext(constraint: Constraint, buildDerivation: Boolean): Boolean {
        var foundSubset: Boolean
        derivation = null
        val positions = constraint.getPositions()
        do {

            // Count and save all the positions whose candidates are a subset of
            // currentSet i.e. the one to be checked for,
            val subsetCount = filterForSubsets(positions)
            //subsetPositions = {p | p ∈ positions, p.candidates ⊆ currentSet && p.candidates ∩ currentSet ≠ ø }

            // If a subset was found, look through all fields in the subset whether there is one that has more candidates than currentSet -> something can be removed
            foundSubset = false
            if (subsetCount == level) {
                for (pos in subsetPositions) {
                    val currentCandidates: BitSet = sudoku.getCurrentCandidates(pos)
                    localCopy.assignWith(currentCandidates) // localCopy <- currentCand...
                    currentCandidates.and(currentSet)
                    if (currentCandidates != localCopy) {
                        // If something changed, a field could be updated, so
                        // the helper is applied
                        // If the derivation shall be returned, add the updated
                        // field to the derivation object
                        if (buildDerivation) {
                            if (derivation == null) {
                                derivation = initializeDerivation(constraint)
                                derivation = derivation
                            }
                            val relevantCandidates = currentCandidates.clone() as BitSet
                            val irrelevantCandidates: BitSet = localCopy
                            irrelevantCandidates.andNot(currentSet)
                            val field = DerivationCell(pos, relevantCandidates, irrelevantCandidates)
                            derivation!!.addDerivationCell(field)
                        }
                        foundSubset = true
                    }
                }
            }
        } while (!foundSubset && constraintSet.cardinality() > level && getNextSubset())
        return foundSubset
    }

    /*
     * counts the number of Positions whose candidates are a subset of currentSet -> eligible
     * stores the first 'level' of those in subsetPositions
     */
    private fun filterForSubsets(positions: List<Position>): Int {
        subsetPositions.clear()
        for (pos in positions) {
            val currentCandidates: BitSet = sudoku.getCurrentCandidates(pos)
            if (!currentCandidates.isEmpty && currentCandidates.intersects(currentSet)) //TODO why check for empty??
                subsetPositions.add(pos)
        }
        return subsetPositions.size
    }

    private fun initializeDerivation(constraint: Constraint): HiddenSetDerivation {
        val derivation = HiddenSetDerivation(hintType!!)
        derivation.setDescription("hidden helper ($hintType)")
        derivation.constraint = constraint
        derivation.setSubsetCandidates(currentSet)
        for (p in subsetPositions) {
            val relevantCandidates = sudoku.getCurrentCandidates(p).clone() as BitSet
            relevantCandidates.and(currentSet)
            val field = DerivationCell(p, relevantCandidates, BitSet())
            derivation.addSubsetCell(field)
        }
        return derivation
    }

    /**
     * Erzeugt einen neuen HiddenHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
     * dabei der Größe der Symbolmenge nach der gesucht werden soll.
     *
     * @param sudoku
     * Das Sudoku auf dem dieser Helper operieren soll
     * @param level
     * Das Größe der Symbolmenge auf die der Helper hin überprüft
     * @param complexity
     * Die Schwierigkeit der Anwendung dieser Vorgehensweise
     * @throws IllegalArgumentException
     * Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
     */
    init {
        require(!(level <= 0 || level > labels.size)) { "level must be ∈ [1," + labels.size + "] but is " + level }
        hintType = labels[level - 1]
    }
}