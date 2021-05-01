package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import java.util.*

/**
 *
 * @property level Level of the Helpers, where the number indicates the symbols and Cells that
 * satisfy the `Naked` constrain.
 *  @param complexity desired complexity for the final sudoku. Must be `>= 0`.
 */
abstract class SubsetHelper protected constructor(sudoku: SolverSudoku,
                                                  protected var level: Int,
                                                  complexity: Int) : SolveHelper(sudoku, complexity) {

    /**
     * A BitSet comprising all candidates of the currently inspected constraint.
     */
    @JvmField
	protected var constraintSet : BitSet  = BitSet()//TODO call it candidateset?

    /**
     * A CandidateSet comprising all candidates of the currently inspected subset.
     */
    @JvmField
	protected var currentSet: CandidateSet = CandidateSet()

    /**
     * The positions of the currently inspected subset.
     */
    @JvmField
	protected var subsetPositions : MutableList<Position> = Stack() //TODO make Stack? might be far more readable

    /**
     * Ein BitSet to compare local copies.
     */
    @JvmField
	protected var localCopy: CandidateSet = CandidateSet()
    //this is for performance so no new object has to be created


    /**
     * All [Constraint]s of the Sudoku.
     */
    //TODO see if it can be replaced by iterator or just the sudokutype, because we dont want to modify the list of constraints!!!
    protected var allConstraints : ArrayList<Constraint> = this.sudoku.sudokuType!!.getConstraints()

    /**
     * Searches for a subset with the size `level` (specified in the constructor),
     * until one is found or, all subsets have been tried.
     * If a Subset is found, the respective candidate lists are updated.
     *
     * If a solution is found a SolveDerivation is created.
     * In the [SolveDerivation] the candidate lists of the constraints in question are represented
     * as irrelevant candidates and the found subset as is represented as relevantCandidates.
     *
     * @param buildDerivation specifies if a derivation should be created if a subset is found.
     * @return true iff a subset is found
     */
    override fun update(buildDerivation: Boolean): Boolean {
        derivation = null
        var found = false

        //ArrayList<Position> positions;
        //iterate over all 'unique'-Constraints
        for (constraint in allConstraints) {
            if (constraint.hasUniqueBehavior()) {
                constraintSet = collectPossibleCandidates(constraint)

                //if we find 'level' or more candidates
                if (constraintSet.cardinality() >= level) {

                    //initializeWith currentSet with the first 'level' candidates in constraintSet
                    //TODO is there a better, clearer way?
                    currentSet.clear()
                    var currentCandidate = -1
                    for (i in 0 until level) {
                        currentCandidate = constraintSet.nextSetBit(currentCandidate + 1)
                        currentSet.set(currentCandidate)
                    }

                    /* this should be better
					currentSet = (BitSet) constraintSet.clone();
					while (currentSet.cardinality() > this.level)
						currentSet.clear(currentSet.length()-1);
					*/
                    found = updateNext(constraint, buildDerivation)

                    // Stop searching if a subset was found
                    if (found) {
                        break
                    }
                }
            }
        }
        return found
    }

    protected abstract fun collectPossibleCandidates(constraint: Constraint): BitSet

    //todo make this an iterator?

    /**
     * Calculates the next subset with the size `level`, from the current subset
     * The most significant candidate is increased until the last candidate is reached.
     * Then the next candidate is increased until the last subset has been calculated.
     *
     * new interpretation: if highest bit in current set can be moved to the right, do so
     * otherwise look from right to left for the first bit after(left of) a gap./fook for rightmost bit that can be moved to the right. move it to the right and set remaining bits next to it
     * Gospers hack is a beautiful implementation, but worthless here as it operates on (binary) numbers, which we cannot convert to/from BitSets
     *
     * @return true, if there is another subset, false otherwise
     */
    protected fun getNextSubset(): Boolean {
        var nextSetExists = false
        val allCandidates = constraintSet //rename for clarity, holds all candiates(set to 1) in the current constraint
        val lastBitSet = currentSet.length() - 1
        // Get the last set candidate

        // Calculate next candidate set if existing
        var nextCandidate = lastBitSet
        var currentCandidate = allCandidates.nextSetBit(lastBitSet + 1) //test if there is another candidate -> we can shift
        if (currentCandidate != -1) //if we found one
            currentCandidate++ //increment for coming 'if' namely 'if (allCandidates.nextSetBit(nextCandidate + 1) != currentCandidate) {' left side is either -1 => no next set or currentCandidate, but we want ineq in that case , so increment it. Why not writing '-1' in the left side? because loop: the limit won't always be the last bit, it is shifted to the left.
        while (!nextSetExists && nextCandidate != -1) { //initially true iff currentSet not empty
            // If the next candidate can be increased without interfering with
            // the next one, do it
            if (allCandidates.nextSetBit(nextCandidate + 1) != currentCandidate) {
                nextSetExists = true
                currentSet.clear(nextCandidate)
                currentCandidate = nextCandidate
                while (currentSet.cardinality() < level) { //fill remaining
                    currentCandidate = allCandidates.nextSetBit(currentCandidate + 1)
                    currentSet.set(currentCandidate)
                }
            }


            // If no set was found, get the next highest candidate to manipulate it
            // save nextCandidate in currentCandidate and have the new nextCandidate point to the next set bit below where it's currently pointing at; then delete old one
            if (!nextSetExists) {
                currentCandidate = nextCandidate
                nextCandidate = -1 //
                while (currentSet.nextSetBit(nextCandidate + 1) < currentCandidate) { // == nextCandidate = currentSet.previousSetBit(nextCandidate)
                    nextCandidate = currentSet.nextSetBit(nextCandidate + 1) // but we don't have the api for it
                }
                currentSet.clear(currentCandidate)
            }
        }
        return nextSetExists
    }

    /* these are all untested :-( but they are better to understand than current getNextSubset TODO substitute current one for these  */
    protected fun getNextSubset2(): Boolean {
        val current = contract(currentSet, constraintSet)

        /* are all true bits at the end? == are all k bits at the end true?*/
        var lastElement = true
        for (i in 0 until constraintSet.cardinality()) lastElement = lastElement and current[constraintSet.cardinality() - i]
        return if (lastElement) false // we're done
        else {
            //advance to next combination, project back to our candidates
            val nextOne = step(current, constraintSet.cardinality(), currentSet.cardinality())
            currentSet = inflate(nextOne, constraintSet)
            true
        }
    }

    /**
     * Searches for the next subset of the size `level` (defined in constructor) in the passed
     * [de.sudoq.model.sudoku.Constraint] with the specified candidate-Set
     * `de.sudoq.model.solverGenerator.solver.helper.SubsetHelper#candidateSet`, as well as
     * the current Subset. All candidate lists are checked that have been found via
     * [de.sudoq.model.solverGenerator.solver.helper.SubsetHelper.getNextSubset] from the
     * specified Subset on.
     *
     * @param constraint [Constraint], in which to look for a subset
     * @param buildDerivation specifies if a derivation should be created if a subset is found.
     * @return true, iff a Subset was found
     */
    protected abstract fun updateNext(constraint: Constraint, buildDerivation: Boolean): Boolean

    companion object {
        /**
         * Provided a combination and values n,k calculate the next step in a loop of 'n choose k' combinations.
         *
         * @param bitSet an initial or intermediate combination
         * @param n supposed length of bitSet i.e. nr of bits to choose from
         * @param k size of the subsets
         * @return the next combination
         */
        private fun step(bitSet: BitSet, n: Int, k: Int): BitSet {
            //idea: looking from right if there is a '1' that can be moved to the right, do so
            //      all other '1' right from it(that didn't have a '0' to their right) are lined up directly to the right of it
            //e.g. |11  11| -> |1 111 |
            var i: Int = n - 1
            while (i > 0) {
                if (bitSet[i])
                    bitSet.clear(i)
                else if (bitSet[i - 1]) {
                    bitSet.clear(i - 1)
                    break
                }
                i--
            }
            while (bitSet.cardinality() < k) {
                bitSet.set(i)
                i++
            }
            return bitSet
        }

        fun contract(sparseSet: BitSet, pattern: BitSet): BitSet {
            val denseSet = BitSet()

            //loop over true bits of pattern as described in JavaDoc of 'nextSetBit'
            var sparseIndex: Int
            sparseIndex = pattern.nextSetBit(0)
            var denseIndex: Int = 0
            while (sparseIndex >= 0) {//todo refactor to for loop?
                denseSet[denseIndex] = sparseSet[sparseIndex]
                if (sparseIndex == Int.MAX_VALUE) break // or (i+1) would overflow
                sparseIndex = pattern.nextSetBit(sparseIndex + 1)
                denseIndex++
            }
            return denseSet
        }

        fun inflate(denseSet: BitSet, pattern: BitSet): CandidateSet {
            val sparseSet = CandidateSet()
            var sparseIndex: Int
            sparseIndex = pattern.nextSetBit(0)
            var denseIndex: Int = 0
            while (sparseIndex >= 0) {
                sparseSet[sparseIndex] = denseSet[denseIndex]
                if (sparseIndex == Int.MAX_VALUE) break // or (i+1) would overflow
                sparseIndex = pattern.nextSetBit(sparseIndex + 1)
                denseIndex++
            }
            return sparseSet
        }
    }

}