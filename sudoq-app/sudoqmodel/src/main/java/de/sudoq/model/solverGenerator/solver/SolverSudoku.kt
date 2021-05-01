package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.solverGenerator.Generator
import de.sudoq.model.solverGenerator.solver.BranchingPool.Branching
import de.sudoq.model.sudoku.*
import java.util.*

/**
 * Eine für den Lösungsalgorithmus optimierte und erweiterte Sudoku Klasse
 */
class SolverSudoku : Sudoku {
    /* Attributes */
    /**
     * Eine Liste aller Positionen dieses Sudokus
     */
    var positions: MutableList<Position>? = null

    /**
     * Mappt die Positionen auf eine Liste von Constraints, zu welchen diese Position gehört
     */
    var constraints: PositionMap<ArrayList<Constraint>>? = null

    /**
     * Mappt die Positionen auf ein BitSet, welches die Kandidaten für dieses Feld repräsentiert nach jedem
     * Branching-Schritt repräsentiert
     */
    private var currentCandidates: PositionMap<CandidateSet?>? = null

    /**
     * Speichert die Positionen an denen gebrancht wurde. (implizit in den einzelnen branchings )
     * TODO warum nehmen wir nicht branchPool.usedBranchings?
     */
    var branchings: Stack<Branching?>? = null

    /**
     * Der BranchingPool zum Verwalten der Branchings.
     */
    private var branchPool: BranchingPool? = null

    /**
     * Der PositionMapPool zum Verwalten der für das Branching benötigten PositionMaps.
     */
    private var positionPool: PositionMapPool? = null
    /**
     * Gibt die Schwierigkeit dieses Sudokus zurück.
     *
     * @return Die Schwierigkeit dieses Sudokus
     */
    /**
     * Die Summe der Schwierigkeit aller auf diesem Sudoku ausgeführten Operationen zum Lösen
     */
    var complexityValue = 0
        private set
    private val neighborDirectory //better to have that static in the type
            : Map<Constraint, List<Constraint>>? = null

    enum class Initialization {
        NEW_CANDIDATES, USE_EXISTING
    }

    /**
     * Instanziiert ein neues SolverSudoku, welches sich auf das spezifizierte Sudoku bezieht.
     *
     * @param sudoku
     * Das Sudoku das zu dem dieses SolverSudoku gehört
     * Parameter and created object will be different objects with indepentent values,
     * can be modified independently
     */
    constructor(sudoku: Sudoku) : super(sudoku.sudokuType!!) {
        initializeSolverSudoku(sudoku, Initialization.NEW_CANDIDATES)
    }

    /**
     * Instanziiert ein neues SolverSudoku, welches sich auf das spezifizierte Sudoku bezieht.
     *
     * @param sudoku
     * Das Sudoku das zu dem dieses SolverSudoku gehört
     * Parameter and created object will be different objects with indepentent values,
     * can be modified independently
     * @param mode
     * The initialization mode
     */
    constructor(sudoku: Sudoku, mode: Initialization?) : super(sudoku.sudokuType!!) {
        initializeSolverSudoku(sudoku, mode)
    }

    /* Initializes this object according to the passed sudoku
	 * the passed sudoku is neither modified nor stored
	 *
	 *
	 */
    fun initializeSolverSudoku(sudoku: Sudoku, mode: Initialization?) {
        complexity = sudoku.complexity //transfer complexity as well

        // initialize the list of positions
        //this.positions = new ArrayList<>(fields.keySet());
        positions = ArrayList() //for debugging we need the same as once
        positions.addAll(cells!!.keys)


        /* For debugging, we need predictable order */positions = Generator.getPositions(sudoku) //TODO remove again


        // initialize new SolverSudoku with the fields of the specified one
        for (p in positions) cells!![p!!] = (sudoku.getCell(p!!)!!.clone() as Cell)

        // initialize the constraints lists for each position and the initial
        // candidates for each field
        constraints = PositionMap(sudokuType!!.size!!)
        for (p in positions) constraints!!.put(p!!, ArrayList())


        //if we were functional
        //this.constraints = new PositionMap<>(this.getSudokuType().getSize());
        //this.positions.stream().forEach(p -> this.constraints.put(p, new ArrayList<>()));

        // add the constraints each position belongs to to the list
        val allConstraints: Iterable<Constraint>? = sudoku.sudokuType
        for (constr in allConstraints!!) for (pos in constr.getPositions()) constraints!![pos]!!.add(constr)

        // initialize the candidates map
        positionPool = PositionMapPool(sudokuType!!.size, positions)
        branchPool = BranchingPool()
        currentCandidates = positionPool!!.positionMap

        // initialize the candidate lists and branchings
        branchings = Stack()
        when (mode) {
            Initialization.NEW_CANDIDATES -> resetCandidates()
            Initialization.USE_EXISTING ->                //solverSudoku's fields take the candidates/notes from sudoku
                for (p in positions) if (sudoku.getCell(p!!)!!.isNotSolved) {
                    for (i in sudokuType!!.symbolIterator) if (sudoku.getCell(p!!)!!.isNoteSet(i) != currentCandidates!![p!!]!![i]) currentCandidates!![p!!]!!.flip(i)
                }
        }
    }

    /**
     * Setzt die Kandidatenlisten aller Felder zurück, sodass alle Kandidatenlisten komplett befüllt sind. "Komplett"
     * wird anhand des größten Constraints in dem sich dieses Feld befindet bemessen. Anschließend werden die
     * Kandidatenlisten bzgl. ConstraintSaturation upgedatet.
     */
    fun resetCandidates() {
        complexityValue = 0

        // delete the branchings
        branchPool!!.recycleAllBranchings()
        positionPool!!.returnAll()
        branchings!!.clear()
        currentCandidates = positionPool!!.positionMap
        // set the candidate lists of all fields to maximum
        for (p in positions!!) if (cells!![p]!!.isNotSolved) currentCandidates!![p]!![0] = sudokuType!!.numberOfSymbols


        /*this.positions.stream()
						.filter(p -> fields.get(p).isNotSolved())
						.forEach(p -> this.currentCandidates.get(p).set(0, getSudokuType().getNumberOfSymbols()));
		functional*/updateCandidates()
    }

    /**
     * Initialisiert einen neuen Zweig, indem der aktuelle Stand der Kandidatenlisten kopiert und auf den
     * Branching-Stack gepusht wird. Der Branch wird an der spezifizierten Position vorgenommen. Dabei wird der
     * spezifizierte Kandidat als temporäre Lösung für das übergebene Feld gesetzt.
     *
     * @param pos
     * Die Position an der gebrancht werden soll
     * @param candidate
     * The candidate that is guessed as start of this branch
     *
     * @throws IllegalArgumentException
     * Wird geworfen, falls die spezifizierte Position null oder nicht in dem Sudoku vorhanden ist
     */
    fun startNewBranch(pos: Position?, candidate: Int) {
        require(!(pos == null || cells!![pos] == null)) { "Position was null or does not exist in this sudoku." }

        // initialize a new branch and copy candidate lists of current branch
        val branch = branchPool!!.getBranching(pos, candidate) //create new branch
        branch!!.candidates = currentCandidates //store current candidates there
        currentCandidates = positionPool!!.positionMap //current candidates in a new (empty) PositionMap
        for (p in positions!!) currentCandidates!![p]!!.or(branch.candidates!![p]) //fill currentCandidates with candidates from before branching
        branchings!!.push(branch) //put branch (i.e. a backup of what we had before this method was called) on branchings (which seems to be identical to branchpool.branchesinactiveuse)

        //the candidate given as parameter is entered as a (user solution)
        currentCandidates!![pos]!!.clear()
        currentCandidates!![pos]!!.set(candidate)
    }

    /**
     * Entfernt den aktuellen Zweig und löscht den gesetzten Wert aus der Kandidatenliste des Feldes, welches für das
     * Branching genutzt wurde. Alles Änderungen in dem Zweig werden zurückgesetzt. Ist kein aktueller Zweig vorhanden,
     * so wird nichts getan.
     */
    fun killCurrentBranch() {
        // there is a slight chance that we work with an unsolvable sudoku
        // then we might backtrack to a point where there are no branches left
        // and in that case returning null is better than failing
        //if(this.branchings.isEmpty())
        //    return null;

        /* We're talking about two branches here:
		   B the current branch at the start of the method
		   A that which was before B (possibly another branch) */

        // delete old branch and remove the candidate used for branching from
        // candidates list
        if (branchings!!.empty()) return
        val lastBranching = branchings!!.pop()
        currentCandidates = lastBranching!!.candidates //override current branch B with A
        for (p in lastBranching.solutionsSet) cells!![p!!]!!.setCurrentValue(Cell.EMPTYVAL, false) //remove solutions added in B
        complexityValue -= lastBranching.complexityValue //substract cmplx scores of techniques that are not used after all

        //BitSet branchCandidates = this.currentCandidates.get(lastBranching.position);//candidates of A at critical pos of A
        //branchCandidates.clear(lastBranching.candidate);//since we're deleting B, guessing this candidate led to failure -> it is not part of solution, we need to delete it
        branchPool!!.recycleLastBranching()
        positionPool!!.returnPositionMap()
        //if (branchCandidates.isEmpty()) {
        //    //no candidate was applicable -> backtrack even further
        //	/*return*/ killCurrentBranch();
        //} else {
        //	//return lastBranching.position; //return
        //}
    }

    val lastBranch: Branching?
        get() = branchings!!.peek()
    val firstBranchPosition: Position?
        get() = branchings!!.peek()!!.position

    /**
     * Updatet die Kandidatenlisten aller Felder dahingehend, dass alle Kandidaten, die die Constraints bei deren
     * Eintragung in ein Feld nicht erfüllen würden aus der jeweiligen Kandidatenliste entfernt werden.
     */
    fun updateCandidates() {
        var updatedConstraints: ArrayList<Constraint>
        var updatedPositions: ArrayList<Position?>
        var isInvalid = false
        for (position in positions!!) {
            if (!isInvalid && !getCell(position)!!.isNotSolved) {
                // Update fields in unique constraints
                updatedConstraints = constraints!![position]!!
                for (uConstraint in updatedConstraints) {
                    if (!isInvalid && uConstraint.hasUniqueBehavior()) {
                        updatedPositions = uConstraint.getPositions()
                        var up = 0
                        while (up < updatedPositions.size && !isInvalid) {
                            val updatedPosition: Position = updatedPositions[up]
                            currentCandidates!![updatedPosition]!!.clear(getCell(position)!!.currentValue)
                            if (currentCandidates!![updatedPosition]!!.isEmpty
                                    && getCell(updatedPosition)!!.isNotSolved) isInvalid = true
                            up++
                        }
                    }
                }
            } else {
                /* Update candidates in non-unique constraints */
                var hasNonUnique = false
                updatedConstraints = constraints!![position]!!
                for (updatedConstraint in updatedConstraints) {
                    if (!updatedConstraint.hasUniqueBehavior()) {
                        hasNonUnique = true
                        break
                    }
                }
                //boolean hasNonUnique = updatedConstraints.stream().anyMatch(c -> !c.hasUniqueBehavior());
                if (hasNonUnique) {
                    var currentCell: Cell? = null
                    var currentCandidatesSet: BitSet? = null
                    currentCell = cells!![position]
                    currentCandidatesSet = currentCandidates!![position]
                    var currentCandidate = -1
                    val numberOfCandidates = currentCandidatesSet!!.cardinality()
                    for (i in 0 until numberOfCandidates) {
                        currentCandidate = currentCandidatesSet.nextSetBit(currentCandidate + 1)
                        currentCell!!.setCurrentValue(currentCandidate, false)
                        for (updatedConstraint in updatedConstraints) if (!updatedConstraint.isSaturated(this)) currentCandidatesSet.clear(currentCandidate)
                        currentCell.setCurrentValue(Cell.EMPTYVAL, false)
                    }
                }
            }
        }
    }

    /**
     * Updatet die Kandidatenlisten aller Felder, die in einem Constraint liegen in dem sich auch die spezifizierte
     * Position befindet dahingehend, dass alle Kandidaten, die die Constraints bei deren Eintragung in ein Feld nicht
     * erfüllen würden aus der jeweiligen Kandidatenliste entfernt werden. Ist die übergebene Position null, so wird
     * nichts getan
     *
     * @param pos
     * Die Position des Feldes, wessen Veränderung ein Update der Kandidatenlisten erfordert
     * @param candidate
     * Der Kandidat, der in dem angegebenen Feld entfernt wurde
     */
    fun updateCandidates(pos: Position?, candidate: Int) {
        if (pos == null) return
        val updatedConstraints = constraints!![pos]!!
        var updatedPositions: ArrayList<Position?>
        var checkedConstraints: ArrayList<Constraint>
        for (constr in updatedConstraints) {
            updatedPositions = constr.getPositions()
            for (uPos in updatedPositions) if (cells!![uPos!!]!!.isNotSolved) if (constr.hasUniqueBehavior()) currentCandidates!![uPos]!!.clear(candidate) else {
                var currentCandidate = -1
                val numberOfCandidates = currentCandidates!![uPos]!!.cardinality()
                for (i in 0 until numberOfCandidates) {
                    currentCandidate = currentCandidates!![uPos]!!.nextSetBit(currentCandidate + 1)
                    cells!![uPos]!!.setCurrentValue(currentCandidate, false)
                    checkedConstraints = constraints!![uPos]!!
                    for (checkedConstraint in checkedConstraints) {
                        if (!checkedConstraint.isSaturated(this)) currentCandidates!![uPos]!!.clear(currentCandidate)
                    }
                    cells!![uPos]!!.setCurrentValue(Cell.EMPTYVAL, false)
                }
            }
        }
    }

    /**
     * Setzt die temporären Lösung für das Feld an der spezifizierten Position auf den angegebenen Kandidaten. Es werden
     * alle abhängigen Kandidatenlisten upgedatet. Beim Entfernene des aktuellen Zweiges wird die eingetragene Lösung
     * wieder gelöscht.
     *
     * @param pos
     * Die Position, an der die Lösung eingetragen werden soll
     * @param candidate
     * Die temporäre Lösung, die eingetragen werden soll
     */
    fun setSolution(pos: Position?, candidate: Int) {
        if (pos == null || candidate < 0) return
        cells!![pos]!!.setCurrentValue(candidate, false)
        currentCandidates!![pos]!!.clear()
        if (hasBranch()) branchings!!.peek()!!.solutionsSet.add(pos)
        updateCandidates(pos, candidate)
    }

    /**
     * Gibt zurück, ob auf diesem Sudoku ein Branch erzeugt wurde oder nicht.
     *
     * @return true, falls auf diesem Sudoku ein Branch erzeugt wurde, false falls nicht
     */
    fun hasBranch(): Boolean {
        return !branchings!!.isEmpty()
    }

    /**
     * Returns the number of branches the sudoku is currently in i.e. the number of guesses that are
     * currently used.
     * @return
     * 0 if no branching has taken place, i.e. all logically derived, no guessing
     * 1 if 1 guess (even if we guess 4 first, run into dead end and guess 5 that's one guess!)
     * ...
     */
    val branchLevel: Int
        get() = branchings!!.size

    /**
     * Gibt die Kandidatenliste der spezifizierten Position zurück.
     *
     * @param pos
     * Die Position, dessen Kandidatenliste abgerufen werden soll
     * @return Die Kandidatenliste der übergebenen Position
     * @throws IllegalArgumentException
     * Wird geworfen, falls die spezifizierte Position ungültig ist
     */
    fun getCurrentCandidates(pos: Position?): CandidateSet? {
        return currentCandidates!![pos!!]
    }

    /**
     * Erhöht den Schwierigkeitswert dieses Sudokus um den spezifizierten Wert. Ist dieser kleiner als 0, so wird nichts
     * getan.
     *
     * @param value
     * Der Wert, um den die Schwierigkeit dieses Sudokus erhöht werden soll
     * @param applyToBranch
     * Gibt an, ob der Wert auf den aktuellen Branch oder das gesamte Sudoku angewendet werden soll
     */
    fun addComplexityValue(value: Int, applyToBranch: Boolean) {
        if (value > 0) {
            if (branchings!!.size > 0) branchings!!.peek()!!.complexityValue += value
            complexityValue += value
        }
    }

    fun getPositions(): List<Position>? {
        return positions
    }

    /**
     * Diese Klasse stellt einen Pool von PositionMaps auf BitSets zur Verfügung, sodass benutzte PositionMaps nicht
     * verfallen, sondern im Pool behalten und für eine weitere Nutzung vorgehalten werden.
     */
    private class PositionMapPool(
            /**
             * Die Größe der Verwalteten PositionMaps
             */
            private val currentDimension: Position?,
            /**
             * Die Positionen
             */
            private val positions: List<Position>?) {
        /**
         * Eine Liste der erstellten, noch nicht vergebenen Maps
         */
        private val unusedMaps: Stack<PositionMap<CandidateSet?>>

        /**
         * Ein Stack der erstellten und bereits vergebenen Maps
         */
        private val usedMaps: Stack<PositionMap<CandidateSet?>>

        /**
         * Gibt eine PositionMap entsprechend der aktuell gesetzten Größe zurück. Ist der Pool leer, so wird seine Größe
         * verdoppelt.
         *
         * @return Eine PositionMap entsprechend der aktuell gesetzten Größe
         */
        val positionMap: PositionMap<CandidateSet?>
            get() {
                if (unusedMaps.size == 0) {
                    unusedMaps.add(initialisePositionMap())
                }
                val ret = unusedMaps.pop()
                usedMaps.push(ret)
                return ret
            }

        /**
         * Gibt die zuletzt geholte PositionMap an den Pool zurück.
         */
        fun returnPositionMap() {
            if (!usedMaps.isEmpty()) {
                val returnedMap = usedMaps.pop()
                for (pos in positions!!) {
                    returnedMap[pos]!!.clear()
                }
                unusedMaps.push(returnedMap)
            }
        }

        /**
         * Initialisiert eine neue PositionMap der im Konstruktor definierten Größe und gibt diese zurück.
         *
         * @return Eine neue PositionMap der im Konstruktor definierten Größe
         */
        private fun initialisePositionMap(): PositionMap<CandidateSet?> {
            val newMap = PositionMap<CandidateSet?>(currentDimension!!)
            for (pos in positions!!) {
                newMap.put(pos, CandidateSet())
            }
            return newMap
        }

        /**
         * Gibt alle PositionMaps an den Pool zurück.
         */
        fun returnAll() {
            while (!usedMaps.empty()) {
                returnPositionMap()
            }
        }

        /**
         * Initialisiert einen neues PositionMapPool mit PositionMaps der spezifizierten Größe. Die dimension sollte
         * nicht null oder gleich 0 sein, die positions sollten ebenfalls nicht null sein und denen des Sudokus
         * entsprechen.
         *
         * @param dimension
         * Die Größe der verwalteten PositionMaps
         */
        init {
            // Keine Überprüfung der Eingabesituation, da nur lokal genutzt
            usedMaps = Stack()
            unusedMaps = Stack()
            unusedMaps.push(initialisePositionMap())
            unusedMaps.push(initialisePositionMap())
        }
    }

    companion object {
        /**
         * Determines whether Lists a,b have a common(by equals) element
         * @param a first list
         * @param b second list
         * @param <T> any element in the list needs to have equals defined
         * @return true iff i.equals(j) == true for at least one i € a, j € b
        </T> */
        fun <T> intersect(a: Iterable<T>, b: Iterable<T>): Boolean {
            for (t1 in a) for (t2 in b) if (t1 == t2) return true
            return false
        } //	/**
        //	 * 	creates a perfect clone
        // 	 */
        //	@Override
        //	public Object clone(){
        //		SolverSudoku clone = new SolverSudoku(this.type);
        //		clone.id             = this.id;
        //		clone.transformCount = this.transformCount;
        //		clone.fields = new HashMap<>();
        //
        //		for(Map.Entry<Position, Field> e : this.fields.entrySet())
        //			clone.fields.put(e.getKey(), (Field) e.getValue().clone());
        //
        //		clone.fieldIdCounter = this.fieldIdCounter;
        //
        //		clone.fieldPositions = new HashMap<>(this.fieldPositions);
        //
        //		clone.complexity = this.complexity;
        //
        //		return clone;
        //	}
    }
}