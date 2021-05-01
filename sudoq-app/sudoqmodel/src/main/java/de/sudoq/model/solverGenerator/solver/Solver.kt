package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.Solution
import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.helper.*
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.PositionMap
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import java.util.*

/**
 * Diese Klasse bietet Methoden zum Lösen eines Sudokus. Sowohl einzelne Felder, als auch gesamte Sudokus können gelöst
 * werden. Auch das Validieren eines Sudokus auf Lösbarkeit ist möglich.
 */
open class Solver(sudoku: Sudoku) {

    /**
     * Returns the sudoku this solver is working on.
     * The returned object not identical to the object passed as parameter to the constructor!
     * A solverSudoku is returned, containing the original parameter-sudoku.
     *
     * @return Das Sudoku-Objekt, auf dem der Solver arbeitet
     */
    /**
     * Das Sudoku, welches von diesem Solver gelöst wird
     */
    @JvmField //todo remove when migration to kotlin complete
    var solverSudoku: SolverSudoku

    fun getSolverSudoku() : SolverSudoku { return solverSudoku}

    /**
     * Eine Liste von SolveHelpern, welche zum Lösen des Sudokus genutzt werden
     */
    @JvmField
	protected var helper: List<SolveHelper>

    /**
     * Die Anzahl der verfügbaren Helfer;
     */
    @JvmField
	protected var numberOfHelpers: Int

    /**
     * Eine Liste der Lösungen des letzten solveAll-Aufrufes
     * TODO make stack
     */
    private var lastSolutions: MutableList<Solution>? = null

    /**
     * A stack of branch points to track backtracking derivations in the solutionslist.
     * Goal is to be able to delete solutions from the derivation in case of a backtrack.
     * Every saved integer is the size of the derivation before the backtrack.
     *
     * E.g.  lastSolutions = [NakedSingle, HiddenPair, Backtrack, lastDigit, Backtrack]
     * ->  branchPoints  = [2,4]
     */
    private var branchPoints: Stack<Int>? = null

    /**
     * Das ComplexityConstraint für die Schwierigkeit des Sudokus.
     */
    private var complConstr: ComplexityConstraint?

    protected open fun makeHelperList(): List<SolveHelper> {
        // Initialize the helpers
        val helpers: MutableList<SolveHelper> = ArrayList()
        helpers.add(LastDigitHelper(solverSudoku, 1)) //only one field in
        helpers.add(LeftoverNoteHelper(solverSudoku, 1))

        //add subset helpers
        //naked and hidden sets complement eaxch other https://www.sadmansoftware.com/sudoku/hiddensubset.php
        //if a naked set $n$ exists with size $|n|$ then there exists a hidden set $h$ with size $|h| = |empty fields in constraint| - |n|$
        //the maximum number of empty fields is #Symbols -> if we look at naked sets up to $a$ we only need to look for hidden sets up to #Symbols - a -1
        // => we don't need to add all possible helpers:
        val numberOfNakedHelpers = solverSudoku.sudokuType!!.numberOfSymbols / 2 //half if #symbols is even, less than half otherwise
        val numberOfHiddenHelpers = solverSudoku.sudokuType!!.numberOfSymbols - numberOfNakedHelpers - 1 //we don't need the complement -> one less

        //no naked single at this point, they're hardcoded later in the program
        //TODO add naked single here and remove its extra loop in the solveX method
        var i = 1
        while (i <= numberOfHiddenHelpers && i == 1) {
            helpers.add(HiddenHelper(solverSudoku, i, i * 50 + 25))
            i++
        }
        var n = 2
        var h = 2
        while (n <= numberOfNakedHelpers || h <= numberOfHiddenHelpers) {
            if (n <= numberOfNakedHelpers) helpers.add(NakedHelper(solverSudoku, n, n * 50))
            if (h <= numberOfHiddenHelpers) helpers.add(HiddenHelper(solverSudoku, h, h * 50 + 25))
            n++
            h++
        }
        helpers.add(LockedCandandidatesHelper(solverSudoku, 600))
        helpers.add(XWingHelper(solverSudoku, 2000))
        helpers.add(Backtracking(solverSudoku, 10000))
        return helpers
    }
    /* Methods */
    /**
     * Ermittelt die Lösung für ein Feld, sowie dessen Herleitung. Die Herleitung wird als Solution-Objekt
     * zurückgegeben. Mit applySolution kann spezifiziert werden, dass die Lösung direkt in das Feld eingetragen werden
     * soll. Kann keine Lösung ermittelt werden, z.B. weil ein Feld falsch gelöst ist, so wird null zurückgegeben.
     * Hinweis: Ist ein Feld bereits inkorrekt gelöst, so kann der Aufruf dieser Methode dazu führen, dass ein weiteres
     * Feld falsch gelöst wird.
     *
     * @param applySolution
     * Gibt an, ob die Lösung direkt in das Feld eingetragen werden soll oder nicht
     * @return Ein Solution-Objekt, welches Schritte zur Herleitung der Lösung eines Feldes enthält bzw null, falls
     * keine Lösung gefunden wurde
     */
    fun solveOne(applySolution: Boolean): Solution? {
        solverSudoku.resetCandidates()

        // Look for constraint saturation at the beginning * if(this.sudoku.getSudokuType().exists((x => !x.isSaturated(this.sudoku)))
        for (con in solverSudoku.sudokuType!!) {     // return null
            if (!con.isSaturated(solverSudoku)) return null
        }
        val solution = Solution()
        var solvedField = false
        var didUpdate = true
        var isIncorrect = false

        //loop until we get the solution for a field TODO what if a helper solves a field directly? solvedField would never be true right? I DONT THINK WE ARE SUPPOSED TO 'SOLVE' IN HELPERS, JUST FIND
        while (!solvedField && didUpdate && !isIncorrect) {
            didUpdate = false
            if (isFilledCompletely) return null // if every field is already filled, no solution can be found, because that already happened
            if (isInvalid) {
                if (!solverSudoku.hasBranch()) {    // if sudoku is invalid && has no branches we're in a dead end
                    isIncorrect = true
                } else {
                    solverSudoku.killCurrentBranch() //if there is a branch, make a backstep
                    didUpdate = true
                }
            }

            /* try to solve fields where only one note is remaining TODO why not just make a naked single?!(efficiency?)*/for (p in solverSudoku.positions!!) {
                val b: BitSet = solverSudoku.getCurrentCandidates(p)
                if (b.cardinality() == 1) { //we found a field where only one note remains
                    if (!solverSudoku.hasBranch()) {
                        //if there are no branches create solution-object
                        solution.action = SolveActionFactory().createAction(b.nextSetBit(0), solverSudoku.getCell(p)!!)
                        val deriv = SolveDerivation()
                        deriv.addDerivationCell(DerivationCell(p, (b.clone() as BitSet), BitSet())) //since only one bit set, complement is an empty set
                        solution.addDerivation(deriv)
                        solvedField = true
                    } else {
                        solverSudoku.setSolution(p, b.nextSetBit(0)) //set solution that can be removed again (in case it's the wrong branch)
                        didUpdate = true
                    }
                }
            }

            // According to their priority use the helpers until one of them can
            // be applied
            if (!solvedField && !didUpdate && !isIncorrect) for (hel in helper) if (hel.update(true)) {
                solution.addDerivation(hel.derivation!!) //we don't check whether branches exist here?!
                didUpdate = true
                break
            }
        }

        // Apply solution if wanted
        if (!isIncorrect && solvedField && applySolution) solution.action!!.execute()
        return if (!isIncorrect && solvedField) solution else null
    }

    /**
     * Löst das gesamte Sudoku, sofern keine Felder fehlerhaft gelöst sind. Ist buildDerivation true, so wird die
     * Herleitung der Lösung erstellt und kann durch die getDerivation Methode abgerufen werden. Ist applySolutions
     * true, so werden die Lösungen direkt in das Sudoku eingetragen, ist es false, so müssen die Lösungen aus der
     * Herleitung selbst ausgeführt werden.
     *
     * @param buildDerivation
     * Gibt an, ob eine Herleitung der Lösung erstellt werden soll
     * @param applySolutions
     * Gibt an, ob die Lösungen direkt in das Sudoku eingetragen werden sollen
     * @return true, falls das Sudoku gelöst werden konnte, false falls nicht
     */
    fun solveAll(buildDerivation: Boolean, applySolutions: Boolean): Boolean {
        //System.out.println("start of solveAll2");
        //print9x9(sudoku);
        val copy = PositionMap<Int>(solverSudoku.sudokuType!!.size!!)
        for (p in solverSudoku.positions!!) {
            copy.put(p, solverSudoku.getCell(p)!!.currentValue)
        }
        val solved = solveAll(buildDerivation, false, false)

        //System.out.println("solved: "+solved);
        //print9x9(sudoku);

        // Restore old state if solutions shall not be applied or if sudoku could not be solved
        if (!applySolutions || !solved) {
            for (p in solverSudoku.positions!!) {
                solverSudoku.getCell(p)!!.setCurrentValue(copy[p]!!, false)
            }
        }
        return solved
    }

    /**
     * Gibt eine Liste von Lösungherleitungen zurück, die durch den letzten Aufruf der solveAll-Methode erzeigt wurde.
     * Wurde die solveAll-Methode noch nicht bzw. ohne den Parameter buildSolution aufgerufen, so wird null
     * zurückgegeben.
     *
     * @return Eine Liste der Herleitungen für den letzten Aufruf der solveAll-Methode, oder null, falls dieser Methode
     * noch nicht oder ohne den Parameter buildSolution aufgerufen wurde
     */
    val solutions: List<Solution>?
        get() = lastSolutions

    val hintCounts: Map<HintTypes, Int>
        get() {
            checkNotNull(lastSolutions) { "lastsolutions is null -> no counts can be generated" }
            val values = HintTypes.values()
            val hist = IntArray(values.size)
            for (s in lastSolutions!!)
                for (sd in s.getDerivations())
                    hist[sd.type!!.ordinal]++
            val hm = EnumMap<HintTypes, Int>(HintTypes::class.java)
            for (i in hist.indices)
                if (hist[i] > 0)
                    hm[values[i]] = hist[i]
            return hm
        }//switch to separator after first element

    val hintCountString: String
        get() {
            val em: Map<HintTypes, Int> = try {
                hintCounts
            } catch (ise: IllegalStateException) {
                return "lastSolutions is zero"
            }

            return em.keys.joinToString(separator = " ") { h -> "${em[h].toString()} $h" }
        }

    val hintScore: Int
        get() = hintCounts.keys.sumOf { h -> hintCounts[h]!! * getHintScore(h) }

    private fun getHintScore(h: HintTypes): Int {
        return if (h === HintTypes.NakedSingle)
                    10
               else {
                 if (hintscores.isEmpty())
                   for (sh in helper)
                     hintscores[sh.hintType!!] = sh.complexityScore
                 hintscores[h]!!
        }
    }

    /**
     * This method changes the complexity value!!! amd maybe lastSolutions! use AmbiguityChecker instead.
     * Überprüft das gegebene Sudoku auf Validität entpsrechend dem spezifizierten ComplexityConstraint. Es wird
     * versucht das Sudoku mithilfe der im ComplexityConstraint für die im Sudoku definierte Schwierigkeit definierten
     * SolveHelper und Anzahl an Schritten versucht zu lösen. Das Ergbnis wird durch ein ComplexityRelation Objekt
     * zurückgegeben. Wird eine PositionsMap übergeben, kann über den Parameter `in` spezifiziert werden, ob die
     * eine Eingabe ist und dazu genutzt werden soll, die Lösung anzugeben und zu validieren oder ob das Objekt mit der
     * Korrekten Lösung befüllt werden soll.
     *
     * @param solution
     * In diese Map wird die ermittelte Lösung geschrieben
     *
     * @return Ein ComplexityRelation-Objekt, welches die Constraint-gemäße Lösbarkeit beschreibt
     */
    fun validate(solution: PositionMap<Int?>?): ComplexityRelation {
        var result = ComplexityRelation.INVALID
        var solved = false
        val ambiguous = false

        //map position -> value
        val copy = PositionMap<Int>(solverSudoku.sudokuType!!.size!!)
        for (p in solverSudoku.positions!!) {
            copy.put(p, solverSudoku.getCell(p)!!.currentValue)
        }

        /////debug
        //int q = this.sudoku.getComplexityValue();
        //solveAll(true,false,false);
        //int r = this.sudoku.getComplexityValue();
        /////debug ende


        //if a solution is found according to the complexity constraints
        if (solveAll(true, true, false)) {
            solved = true
            // store the correct solution
            if (solution != null) {
                for (p in solverSudoku.positions!!) {
                    val curVal = solverSudoku.getCell(p)!!.currentValue
                    solution.put(p, curVal)
                }
            }
        }

        //severalSolutionsExist() overwrites the complexity value, so we query it here already.
        val complexity = solverSudoku.complexityValue


        //this overwrites the existing sudoku
        //if (solved && severalSolutionsExist()) //TODO maybe try to fix by adding fields and only then return invalid?
        //	ambiguous = true;
        //this.sudoku.complexityValue is overwritten by the attempt at finding a second solution

        // restore initial state
        for (p in solverSudoku.positions!!) solverSudoku.getCell(p)!!.setCurrentValue(copy[p]!!, false)


        // depending on the result, return an int
        val minComplextiy = complConstr!!.minComplexityIdentifier
        val maxComplextiy = complConstr!!.maxComplexityIdentifier

        //if (ambiguous)
        //	result = ComplexityRelation.AMBIGUOUS;
        /*else*/if (solved) {
            if (maxComplextiy * 1.2 < complexity) result = ComplexityRelation.MUCH_TOO_DIFFICULT else if (maxComplextiy < complexity && complexity <= maxComplextiy * 1.2) result = ComplexityRelation.TOO_DIFFICULT else if (minComplextiy < complexity && complexity <= maxComplextiy) result = ComplexityRelation.CONSTRAINT_SATURATION else if (minComplextiy * 0.8 < complexity && complexity <= minComplextiy) result = ComplexityRelation.TOO_EASY else if (complexity <= minComplextiy * 0.8) result = ComplexityRelation.MUCH_TOO_EASY
            /*   0.8 minC      minC               maxC            1.2 maxC
		    much too easy| too easy|   saturation     |too difficult      | Much too difficult         */
        }

        // System.out.println(sudoku.getComplexityValue() + "(" + sudoku.getComplexityScore() + ") " + result);
        return result
    }

    /**
     * Returns the solutions of the last `solve`-call. Undefined if last solve failed e.g. invalid.
     * @return the solutions of the last `solve`-call.
     */
    val solutionsMap: PositionMap<Int>
        get() {
            val solutions = PositionMap<Int>(solverSudoku.sudokuType!!.size!!)
            for (p in solverSudoku.positions!!) {
                val curVal = solverSudoku.getCell(p)!!.currentValue
                solutions.put(p, curVal)
            }
            return solutions
        }

    /**
     * Indicates whether further solutions exist for a sudoku where we've already found one.
     * (potentially) modifies sudoku.
     *
     * @return true if another solution exists
     */
    fun severalSolutionsExist(): Boolean {
        //lastsolutions might be set to null, e.g. if we kill branch but dont find another solution
        //if we want to call getSolutions later on we'll be interested in the first solution anyway

        //List<Solution> ls = lastSolutions; //we just don't build a derivation, ls should be left unchanged
        while (advanceBranching(false) == Branchresult.SUCCESS) if (solveAll(false, false, true)) //why is it invalid if solved and another solve?
            return true

        //lastSolutions = ls;
        return false
    }

    /**
     * Solves the Löst das gesamte spezifizierte Sudoku. Die Lösung wird als Liste von Solution-Objekten zurückgeliefert, deren
     * Reihenfolge die Reihenfolge der Lösungsschritte des Algorithmus, realisiert durch die SolveHelper, repräsentiert.
     * Ist das Sudoku invalid und kann somit nicht eindeutig gelöst werden, so wird false zurückgegeben.
     *
     * @param buildDerivation
     * Bestimmt, ob die Herleitung der Lösung oder lediglich eine leere Liste zurückgegeben werden soll
     * @param followComplexityConstraints
     * Bestimmt, ob zum Lösen die Constraints der Komplexität des Sudokus befolgt werden müssen
     * @param validation
     * Besagt, dass dieser Lösungsversuch zum Validieren gedacht ist und daher die Kandidatenlisten nicht
     * zurückgesetzt werden sollen und ob die Schwierigkeit jedes Lösungsschrittes zum Sudoku hinzugefügt
     * werden soll
     * @return true if sudoku can be solved
     * false otherwise
     * @throws IllegalArgumentException
     * Wird geworfen, falls followComplexityConstraints true ist, jedoch keine Constraint-Definition für den
     * Sudokutyp und die Schwierigkeit vorhanden ist
     */
    fun solveAll(buildDerivation: Boolean, followComplexityConstraints: Boolean, validation: Boolean): Boolean {
        if (!validation) solverSudoku.resetCandidates()
        try {
            if (followComplexityConstraints) {
                //if complexity is relevant restrict helpers
                complConstr = solverSudoku.sudokuType!!.buildComplexityConstraint(solverSudoku.complexity)
                numberOfHelpers = complConstr!!.numberOfAllowedHelpers //TODO specifying a max helper would be clearer
            } else {
                numberOfHelpers = helper.size
            }
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("Invalid sudoku complexity to follow constraints")
        }

        // Look for constraint saturation at the beginning
        if (!solverSudoku.sudokuType!!.checkSudoku(solverSudoku)) {
            return false
        }
        var solved = false
        var didUpdate = true
        var isUnsolvable = false
        if (buildDerivation) {
            lastSolutions = ArrayList()
            branchPoints = Stack()
        }
        var solver_counter = 0
        while (!solved //if `solved` we're done
                && didUpdate //if we didn't do an update in the last iteration, we won't do one the next iteration either
                && !isUnsolvable) { //if we found out there is no solution, no need to try further
            didUpdate = false


            // try to solve the sudoku
            solved = isFilledCompletely
            if (!solved && isInvalid) {
                if (advanceBranching(buildDerivation) == Branchresult.SUCCESS)
                    didUpdate = true
                else
                    isUnsolvable = true
                /*
				 * if sudoku is invalid, has no branches and no solution was found,
				 * it is invalid if there was already a solution
				 * there is no further one, so it is solved correct if there is a branch, make a backstep
				 */
                /*if (!this.sudoku.hasBranch()) {
					isUnsolvable = true;
				} else {
					if (buildDerivation) {
						while (lastSolutions.size() > branchPoints.peek()) {
							lastSolutions.remove(lastSolutions.size() - 1);
						}
						branchPoints.pop();
					}
					this.sudoku.killCurrentBranch();
					didUpdate = true;
				}*/
            }

            // try to update naked singles
            if (!solved && !didUpdate && !isUnsolvable) {
                if (updateNakedSingles(buildDerivation, !validation)) {
                    didUpdate = true
                }
            }
            if (useHelper(solved, didUpdate, isUnsolvable, buildDerivation, validation)) {
                didUpdate = true
            }
            solver_counter++
            ////////////////////////
            // UNCOMMENT THE FOLLOWING TO PRINT THE WHOLE SUDOKU AFTER EACH LOOP
            /*if(solver_counter % 10000 == 0){
				System.out.println("sc: "+ solver_counter + "   bf: "+ sudoku.getBranchLevel());
				print9x9(sudoku);
			}*/
        }
        if (!solved) {
            lastSolutions = null
        } /*else if (buildDerivation) {
			lastSolutions.remove(lastSolutions.size() - 1); //TODO why remove last element???
		}this was from when we made a new solution in advance -> we had to remove the last one*/

        // depending on the result, return an int
        return solved
    }

    //
    //  if (!solved && isInvalid()) {
    //         advanceBranching()
    //
    //  }
    //
    protected enum class Branchresult {
        SUCCESS, UNSOLVABLE
    }

    /** if there is a branch, delete it and make a the next one:
     * if there are more candidates, choose the next one
     * otherwise, delete branches until there are
     *
     * @param buildDerivation indicates whether a derivation is to be constructed
     * @return the Branchresult
     */
    protected fun advanceBranching(buildDerivation: Boolean): Branchresult {
        return if (!solverSudoku.hasBranch()) {
            Branchresult.UNSOLVABLE // possible output nr1: insolvable
        } else {
            val branchingPos = solverSudoku.lastBranch!!.position
            val branchingCandidate = solverSudoku.lastBranch!!.candidate

            /* delete all solutions (including the backtracking-derivation) since the last branch */if (buildDerivation) {
                while (lastSolutions!!.size > branchPoints!!.peek()) {
                    lastSolutions!!.removeAt(lastSolutions!!.size - 1)
                }
                branchPoints!!.pop()
            }
            solverSudoku.killCurrentBranch()
            val candidates = solverSudoku.getCurrentCandidates(branchingPos!!)
            val nextCandidate = candidates.nextSetBit(branchingCandidate + 1)
            if (nextCandidate != -1) {

                //TODO new Backtracking(this.sudoku).update(tre)
                solverSudoku.startNewBranch(branchingPos, nextCandidate)
                if (buildDerivation) {
                    branchPoints!!.push(lastSolutions!!.size)
                    //copied from class Backtracking (because we need a custom candidate here).
                    val lastDerivation = SolveDerivation(HintTypes.Backtracking)
                    val irrelevantCandidates: BitSet = candidates //startNewBranch() deletes candidates in currendCandidates, and branchpool can't be accessed from here, so we need to use saved bitset
                    val relevantCandidates = BitSet()
                    relevantCandidates.set(nextCandidate)
                    irrelevantCandidates.clear(nextCandidate)
                    val derivField = DerivationCell(branchingPos,
                            relevantCandidates,
                            irrelevantCandidates)
                    lastDerivation.addDerivationCell(derivField)
                    lastDerivation.setDescription("Backtrack different candidate")
                }
                Branchresult.SUCCESS
            } else {
                //no candidate was applicable -> backtrack even further
                advanceBranching(buildDerivation)
            }
        }
    }
    /*
if there is another candidate -> advance
            noother           -> backtrack i.e. killanother branch

 */
    /**
     * According to their priority use the helpers until one of them can
     * be applied.
     *
     * @param solved is the sudoku already solved?
     * @param didUpdate was an update performed?
     * @param isUnsolvable is it unsolvable?
     * @param buildDerivation is a derivation to be built?
     * @param validation should difficulty scores be collected?
     * @return true if any helper could be applied, false if no helper could be applied
     */
    protected fun useHelper(solved: Boolean, didUpdate: Boolean, isUnsolvable: Boolean, buildDerivation: Boolean, validation: Boolean): Boolean {
        if (!solved && !didUpdate && !isUnsolvable) {
            for (i in 0 until numberOfHelpers) {
                val hel = helper[i]

                //if a helper can be applied
                if (hel.update(buildDerivation)) {
                    if (!validation)
                        solverSudoku.addComplexityValue(hel.complexityScore, hel !is Backtracking)
                    if (buildDerivation) {
                        val newSolution = Solution()
                        newSolution.addDerivation(hel.derivation!!)
                        lastSolutions!!.add(newSolution)
                        if (hel is Backtracking) {
                            branchPoints!!.push(lastSolutions!!.size - 1)
                            //System.out.println("Backtracking!");
                        }
                    }
                    return true
                }
            }
        }
        return false
    }

    /**
     * Sucht nach NakedSingles und trägt diese daraufhin als Lösung für das jeweilige Feld ein. Gibt zurück, ob ein
     * NakedSingle gefunden wurde.
     *
     * @param addDerivations
     * Bestimmt, ob die Herleitung der Lösungen zurückgegeben oder lediglich eine leere Liste zurückgegeben
     * werden soll
     * @param addComplexity
     * Bestimmt, ob der Schwierigkeitswert beim Finden eines NakedSingles dem Sudoku hinzugefügt werden soll
     * @return Eine Liste der Herleitungen der Lösungen oder null, falls keine gefunden wurde
     */
    protected fun updateNakedSingles(addDerivations: Boolean, addComplexity: Boolean): Boolean {
        var hasNakedSingle: Boolean //indicates that one was found in the last iteration -> continue to iterate
        var foundNakedSingle = false //indicates that at least one was found
        // Iterate trough the fields to look if each field has only one
        // candidate left = solved
        val newSolution = Solution()
        do {
            hasNakedSingle = false
            for (p in solverSudoku.positions!!) {
                val b: BitSet = solverSudoku.getCurrentCandidates(p)
                if (b.cardinality() == 1) {
                    if (addDerivations) {
                        val deriv = SolveDerivation(HintTypes.NakedSingle)
                        deriv.addDerivationCell(DerivationCell(p, (b.clone() as BitSet),
                                BitSet()))
                        deriv.setDescription("debug: naked single via Solver.updateNakedSingles")
                        /*
						we dont do actions for the other helpers either and
 -						several actions per solution would only ovewrite themselves
 						SolveAction action = (SolveAction) new SolveActionFactory().createAction(b.nextSetBit(0),
								this.sudoku.getField(p));
						sol.setAction(action);*/
                        newSolution.addDerivation(deriv)
                        //lastSolutions.add(new Solution());
                    }
                    solverSudoku.setSolution(p, b.nextSetBit(0)) //execute, since only one candidate, take first
                    if (addComplexity) {
                        solverSudoku.addComplexityValue(10, true)
                    }
                    hasNakedSingle = true
                    foundNakedSingle = true
                }
            }
        } while (hasNakedSingle)
        if (foundNakedSingle && addDerivations) lastSolutions!!.add(newSolution)
        return foundNakedSingle
    }/* look for no solution entered && no candidates left *///for (Position p : this.sudoku.positions)

    /**
     * Überprüft, ob das Sudoku invalide ist, also ob es ein ungelöstes Feld gibt, für das keine Kandidaten mehr
     * vorhanden sind.
     *
     * @return true, falls das Sudoku aktuell invalide ist, false falls nicht
     */
    protected val isInvalid: Boolean
        get() {
            fun invalid(vp: Position): Boolean =
                    solverSudoku.getCurrentCandidates(vp).isEmpty //no solution entered
                    && solverSudoku.getCell(vp)!!.isNotSolved //no candidates left

            return solverSudoku.sudokuType!!.validPositions.any(::invalid)
        }

    /**
     * Überprüft, ob im Sudoku in jedem Feld eine Lösung eingetragen ist.
     * Keine Überprüfung auf Richtigkeit.
     *
     * @return true, falls das Sudoku gelöst ist, false andernfalls
     */
    protected val isFilledCompletely: Boolean
        get() = solverSudoku.positions!!.map(solverSudoku::getCell)
                                        .none { it!!.isNotSolved }


    /**
     * intended for debugging only
     * @return the iterator for helper
     */
    fun helperIterator(): Iterable<SolveHelper> {
        return object : Iterable<SolveHelper> {
            override fun iterator(): Iterator<SolveHelper> {
                return helper.iterator()
            }
        }
    }

    companion object {
        private val hintscores: MutableMap<HintTypes, Int> = EnumMap(HintTypes::class.java)
        fun print9x9(sudoku: Sudoku) {
            println(sudoku)
        }
    }
    /* Constructors */ /**
     * Creates a new solver for `sudoku`.
     * If the argument is null, a IllegalArgumentException is thrown.
     * All methods of this class refer to this sudoku object.
     *
     * @param sudoku
     * Sudoku to be solved by this solver
     * @throws IllegalArgumentException
     * if `sudoku == null`
     */
    init {
        solverSudoku = SolverSudoku(sudoku)
        complConstr = sudoku.sudokuType!!.buildComplexityConstraint(sudoku.complexity)
        helper = makeHelperList()
        numberOfHelpers = helper.size
    }
}