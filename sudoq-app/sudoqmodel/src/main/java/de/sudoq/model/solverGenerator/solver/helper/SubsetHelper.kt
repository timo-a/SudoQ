package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import java.util.*

abstract class SubsetHelper protected constructor(sudoku: SolverSudoku,
                                                  /**
                                                   * Die Stufe des Helpers, wobei diese die Anzahl der Ziffern und Felder angibt, welche die Naked-Bedingung erfüllen
                                                   * sollen
                                                   */
                                                  protected var level: Int, complexity: Int) : SolveHelper(sudoku, complexity) {
    /* Attributes */
    /**
     * Ein BitSet, welches alle Kandidaten des aktuell untersuchten Constraints enthält. Aus Performancegründen nicht
     * lokal definiert.
     */
    @JvmField protected var constraintSet //TODO call it candidateset?
            : BitSet

    /**
     * Das BitSet, welches das gerade untersuchte Subset darstellt. Aus Performancegründen nicht lokal definiert.
     */
    @JvmField protected var currentSet: CandidateSet

    /**
     * Die Positionen, welche zum aktuell untersuchten Subset gehören. Aus Performancegründen nicht lokal definiert.
     */
    @JvmField
    protected var subsetPositions //TODO make Stack? might be far more readable
            : MutableList<Position>

    /**
     * Ein BitSet für lokale Kopien zum vergleichen. Aus Performancegründen nicht lokal definiert.
     */
    protected var localCopy: CandidateSet

    /**
     * Speichert alle Constraints des zugrundeliegenden Sudokus.
     */
    protected var allConstraints //TODO see if it can be replaced by iterator or just the sudokutype, because we dont want to modify the list of constraints!!!
            : ArrayList<Constraint>
    /* Methods */
    /**
     * Sucht so lange nach einem Subset mit der im Konstruktor spezifizierten Größe level, bis entweder eines
     * gefunden wird oder alle Möglichkeiten abgearbeitet sind. Wird ein Subset gefunden, werden die entsprechenden
     * Kandidatenlisten upgedatet.
     *
     * Wurde eine Lösung gefunden, so wird eine SolveDerivation, die die Herleitung des NakedSubsets darstellt,
     * erstellt. Dabei werden in der SolveDerivation die Kandidatenlisten des betroffenen Constraints als
     * irrelevantCandidates, sowie das gefundene Subset als relevantCandidates dargestellt. Diese kann durch die
     * getDerivation Methode abgefragt werden.
     *
     * @param buildDerivation
     * Bestimmt, ob beim Finden eines Subsets eine Herleitung dafür erstellt werden soll, welche daraufhin
     * mit getDerivation abgerufen werden kann.
     * @return true, falls ein Subset gefunden wurde, false falls nicht
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
					*/found = updateNext(constraint, buildDerivation)

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
     * Berechnet das nächste Subset des spezifizierten BitSets mit der im Konstruktor definierten Größe "level",
     * ausgehend von demjenigen Subset, welches die niederwertigsten Kandidaten gesetzt hat. Das übergebene Subset muss
     * bereits entsprechend viele Kandidaten gesetzt haben. Es wird immer der hochwertigste Kandidat erhöht bis dieser
     * beim letzten Kandidaten angelangt ist, daraufhin wird der nächste Kandidat erhöht bis schließlich das
     * hochwertigste Subset berechnet wurde.
     *
     * new interpretation: if highest bit in current set can be moved to the right, do so
     * otherwise look from right to left for the first bit after(left of) a gap./fook for rightmost bit that can be moved to the right. move it to the right and set remaining bits next to it
     * Gospers hack is a beautiful implementation, but worthless here as it operates on (binary) numbers, which we cannot convert to/from BitSets
     *
     * @return true, falls es noch ein Subset gibt, false falls nicht
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
     * @param constraint
     * Der Constraint, in dem ein NakedSubset gesucht werden soll
     * @param buildDerivation
     * Gibt an, ob eine Herleitung für ein gefundenes Subset erstellt werden soll, welche über die
     * getDerivation Methode abgerufen werden kann
     * @return true, falls ein Subset gefunden wurde, false falls nicht
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
            var i: Int
            i = n - 1
            while (i > 0) {
                if (bitSet[i]) bitSet.clear(i) else if (bitSet[i - 1]) {
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
            var denseIndex: Int
            var sparseIndex: Int
            sparseIndex = pattern.nextSetBit(0)
            denseIndex = 0
            while (sparseIndex >= 0) {
                denseSet[denseIndex] = sparseSet[sparseIndex]
                if (sparseIndex == Int.MAX_VALUE) break // or (i+1) would overflow
                sparseIndex = pattern.nextSetBit(sparseIndex + 1)
                denseIndex++
            }
            return denseSet
        }

        fun inflate(denseSet: BitSet, pattern: BitSet): CandidateSet {
            val sparseSet = CandidateSet()
            var denseIndex: Int
            var sparseIndex: Int
            sparseIndex = pattern.nextSetBit(0)
            denseIndex = 0
            while (sparseIndex >= 0) {
                sparseSet[sparseIndex] = denseSet[denseIndex]
                if (sparseIndex == Int.MAX_VALUE) break // or (i+1) would overflow
                sparseIndex = pattern.nextSetBit(sparseIndex + 1)
                denseIndex++
            }
            return sparseSet
        }
    }
    //protected static HintTypes labels[];
    /* Constructors */ /**
     * Erzeugt einen neuen NakedHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
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

        //hintType = labels[level-1];
        allConstraints = this.sudoku.sudokuType!!.getConstraints()
        constraintSet = BitSet()
        currentSet = CandidateSet()
        subsetPositions = Stack()
        localCopy = CandidateSet()
    }
}