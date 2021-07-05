package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.sudoku.CandidateSet
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import java.util.*
import kotlin.collections.ArrayList

/**
 * BranchinigPool maintains active branchings and recycles old objects
 *
 */
class BranchingPool {
    /**
     * Stores discarded branching objects ready to be reused (we want to save on object instantiation for performance)
     */
    private val branchingsReservoir: Stack<Branching>

    /**
     * Ein Stack der erstellten und bereits vergebenen Maps
     */
    private val branchingsInUse: Stack<Branching>

    /**
     * Returns an unused Branching initialized with Position `p` and Candidate `candidate`.
     * If possible a branch is recycled, otherwise newly instantiated.
     *
     * @return a Branching initialized with `p` and `candidate`
     * @throws IllegalArgumentException
     * Wird geworfen, falls die spezifizierte Position null ist
     */
    fun getBranching(p: Position, candidate: Int): Branching {

        /* fetch a new Branching. Preferrably recycle one from unused */
        val ret: Branching
        /* if no unused Branchings ready */
        if (branchingsReservoir.isEmpty()) {
            ret = Branching(p, candidate)
        } else {
            ret = branchingsReservoir.pop()
            ret.initializeWith(p, candidate)
        }
        branchingsInUse.push(ret) //store it among the used
        return ret
    }

    /**
     * Recycles most recent branching
     */
    fun recycleLastBranching() {
        if (!branchingsInUse.isEmpty()) {
            val returnedMap = branchingsInUse.pop() //get last branching
            returnedMap.solutionsSet.clear() //clear values
            branchingsReservoir.push(returnedMap) //move to reservoir
        }
    }

    /**
     * Recycles all branchings
     */
    fun recycleAllBranchings() {
        while (!branchingsInUse.isEmpty()) {
            recycleLastBranching()
        }
    }

    /**
     * A branching object holds all the branching position and the candidate with wich the branch was started.
     * It holds the candidates for every position BEFORE the branch took place in order to write them back if the branch is reverted
     * as well as all solutions that wee entered into the sudoku SINCE the branch took place in order to delete them from the sudoku if the branch is reverted
     * Alle Attribute sind package scope verfügbar, um diese direkt bearbeiten zu können.
     * Aus Performancegründen wurde auf einen Zugriff durch Getter/Setter-Methoden verzichtet.
     */
    inner class Branching(p: Position, candidate: Int) {
        /**
         * Die Position, an der gebrancht wurde
         */
        @JvmField
        var position: Position? = null

        /**
         * Der Kandidate mit dem gebrancht wurde
         */
        @JvmField
        var candidate = 0

        /**
         * Die Liste von Positionen an denen in diesem Branch eine Lösung eingetragen wurde.
         */
        //TODO rename to sol..LIST??
        val solutionsSet: MutableList<Position> = ArrayList()

        /**
         * Eine Map, welche für jede Position dessen Kandidaten vor dem Branchen speichert.
         */
        var candidates: PositionMap<CandidateSet>? = null

        /**
         * Der Komplexitätswert für diesen Branch
         */
        var complexityValue = 0

        //we have an extra method here to ensure that reinitialization as well as initialization are invariant with regard to pos, can, complxVal
        fun initializeWith(p: Position, candidate: Int) {
            position = p
            this.candidate = candidate
            complexityValue = 0
        }

        /**
         * Erstellt ein neues Branching mit einem leeren SolutionSet.
         * position and candidate are set by parameter values, complexity value is set to 0
         */
        init {
            initializeWith(p, candidate)
        }
    }

    /**
     * Initialisiert einen neuen BranchingPool. Der Pool wird mit 2 PositionMaps initialisiert.
     *
     */
    init {
        branchingsInUse = Stack()
        branchingsReservoir = Stack()
    }
}