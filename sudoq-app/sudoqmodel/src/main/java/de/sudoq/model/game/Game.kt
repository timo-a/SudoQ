/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.actionTree.*
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuManager
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*

/**
 * This class represents a sudoku game.
 * Functions as a Facade towards the controller.
 */
class Game : Xmlable {

    /**
     * Unique id for the game
     */
    var id: Int
        private set

    /**
     * The sudoku of the game.
     */
    var sudoku: Sudoku? = null //todo make nonnullable
        private set

    /**
     * manages the game state
     */
    var stateHandler: GameStateHandler? = null //todo make non-nullable
        private set

    /**
     * Passed time since start of the game in seconds
     */
    var time = 0
        private set

    /**
     * Total sum of used assistances in this game.
     */
    var assistancesCost = 0
        private set

    /**
     * game settings
     */
    private var gameSettings: GameSettings? = null //TODO make non-nullable

    /**
     * Indicates if game is finished
     */
    private var finished = false

    /**
     * Protected constructor to prevent instatiation outside this package.
     * (apparently thats not possible in kotlin...)
     * Available assistances are set from current profile. TODO really? make explicit instead
     *
     * @param id ID of the game
     * @param sudoku Sudoku of the new game
     */
    constructor(id: Int, sudoku: Sudoku) {//todo check visibility - make internal?
        this.id = id
        gameSettings = GameSettings()
        this.sudoku = sudoku
        this.time = 0
        stateHandler = GameStateHandler()
    }

    /**
     * creates a completely empty game
     */
    // package scope!
    internal constructor() {//TODO who uses this? can it be removed?
        id = -1
    }

    /**
     * Adds time to the game
     *
     * @param time Time to add in seconds
     */
    fun addTime(time: Int) {
        this.time += time
    }

    /**
     * The score of the game
     */
    val score: Int
        get() {
            var scoreFactor = 0
            fun power(expo : Double) : Int = sudoku!!.sudokuType?.numberOfSymbols?.let { Math.pow(it.toDouble(), expo).toInt()}!!
            when (sudoku!!.complexity) {
                Complexity.infernal ->  scoreFactor = power(4.0)
                Complexity.difficult -> scoreFactor = power(3.5)
                Complexity.medium ->    scoreFactor = power(3.0)
                Complexity.easy ->      scoreFactor = power(2.5)
            }
            return (scoreFactor * 10 / ((time + assistancesTimeCost) / 60.0f)).toInt()
        }

    val assistancesTimeCost: Int
        get() = assistancesCost * 60

    /**
     * Checks the sudoku for correctness.
     * This is an assistance so the total assistance cost is increased.
     *
     * @return true, if sudoku is correct so far, false otherwise
     */
    fun checkSudoku(): Boolean {
        assistancesCost += 1
        return checkSudokuValidity()
    }

    /**
     * Checks the sudoku for correctness
     *
     * @return true, if sudoku is correct so far, false otherwise
     */
    private fun checkSudokuValidity(): Boolean {
        val correct = !sudoku!!.hasErrors()
        if (correct) {
            currentState.markCorrect()
        } else {
            currentState.markWrong()
        }
        return correct
    }

    /**
     * Executes the passed [Action] and saves it in the [ActionTree].
     *
     * @param action the [Action] to perform.
     */
    fun addAndExecute(action: Action) {
        if (finished) return
        stateHandler!!.addAndExecute(action)
        sudoku!!.getCell(action.cellId)?.let { updateNotes(it) }
        if (isFinished()) finished = true
    }

    /**
     * Updates the notes in den constraints of the cell by removing the cells current value from the notes.
     * Is only executed if the respective assistance is available.
     *
     * @param cell the modified Cell
     */
    private fun updateNotes(cell: Cell) {
        if (!isAssistanceAvailable(Assistances.autoAdjustNotes)) return
        val editedPos = sudoku!!.getPosition(cell.id)
        val value = cell.currentValue

        /*this.sudoku.getSudokuType().getConstraints().stream().filter(c -> c.includes(editedPos))
                                                             .flatMap(c -> c.getPositions().stream())
                                                             .filter(changePos -> this.sudoku.getField(changePos).isNoteSet(value))
                                                             .forEachOrdered(changePos -> this.addAndExecute(new NoteActionFactory().createAction(value, this.sudoku.getField(changePos))));
        should work, but to tired to try*/

        for (c in sudoku!!.sudokuType!!) {
            if (c.includes(editedPos!!)) {
                for (changePos in c) {
                    if (sudoku!!.getCell(changePos)?.isNoteSet(value)!!) {
                        addAndExecute(NoteActionFactory().createAction(value, sudoku!!.getCell(changePos)!!))
                    }
                }
            }
        }
    }

    /**
     * Returns the state of the game to the given node in the action tree.
     * TODO what if the node is not in the action tree?
     *
     * @param ate The ActionTreeElement in which the state of the Sudoku is to be returned.
     *
     */
    fun goToState(ate: ActionTreeElement) {
        stateHandler!!.goToState(ate)
    }

    /**
     * Undoes the last action. Goes one step back in the action tree.
     */
    fun undo() {
        stateHandler!!.undo()
    }

    /**
     * Redo the last [Action]. Goes one step forward in the action tree.
     */
    fun redo() {
        stateHandler!!.redo()
    }

    /**
     * The action tree node of the current state.
     */
    val currentState: ActionTreeElement
        get() = stateHandler!!.currentState!! //todo find a way to ensure it can never be null (the implicit root)

    /**
     * Marks the current state to better find it later.
     */
    fun markCurrentState() {
        stateHandler!!.markCurrentState() //TODO what doe this mean is it a book mark?
    }

    /**
     * Checks if the given [ActionTreeElement] is marked.
     *
     * @param ate the ActionTreeElement to check
     * @return true iff it is marked
     */
    fun isMarked(ate: ActionTreeElement?): Boolean {
        return stateHandler!!.isMarked(ate)
    }

    /**
     * Checks if the sudoku is solved completely and correct.
     *
     * @return true iff sudoku is finished (and correct)
     */
    fun isFinished(): Boolean {
        return finished || sudoku!!.isFinished
    }

    /**
     * Tries to solve the specified [Cell] and returns if that attempt was successful.
     * If the [Sudoku] is invalid or has mistakes false is returned.
     *
     * @param cell The cell to solve
     * @return true, if cell could be solved, false otherwise
     */
    fun solveCell(cell: Cell?): Boolean { //TODO don't accept null
        if (sudoku!!.hasErrors() || cell == null) return false
        assistancesCost += 3
        val solution = cell.solution
        return if (solution != Cell.EMPTYVAL) {
            addAndExecute(SolveActionFactory().createAction(solution, cell))
            true
        } else {
            false
        }
    }

    /**
     * Tries to solve a randomly selected [Cell] and returns whether that was successful.
     *
     * @return true, if a cell could be solved, false otherwise
     */
    fun solveCell(): Boolean {
        if (sudoku!!.hasErrors()) return false
        assistancesCost += 3
        for (f in sudoku!!) {
            if (f.isNotSolved) {
                addAndExecute(SolveActionFactory().createAction(f.solution, f))
                break
            }
        }
        return true

        /*
         * Solution solution = solver.getHint(); if (solution != null) {
         * stateHandler.addAndExecute(solution.getAction()); return true; } else { return false; }
         */
    }

    /**
     * Solves the entire sudoku.
     *
     * @return true, iff the sudoku could be solved, false otherwise
     */
    fun solveAll(): Boolean {
        if (sudoku!!.hasErrors()) return false
        val unsolvedCells: MutableList<Cell> = ArrayList()
        for (f in sudoku!!) {
            if (f.isNotSolved) {
                unsolvedCells.add(f)
            }
        }
        val rnd = Random()
        while (unsolvedCells.isNotEmpty()) {
            val nr = rnd.nextInt(unsolvedCells.size)
            val a = SolveActionFactory().createAction(unsolvedCells[nr].solution, unsolvedCells[nr])
            addAndExecute(a)
            unsolvedCells.removeAt(nr)
        }
        assistancesCost += Int.MAX_VALUE / 80
        return true
        /*
         * if (solver.solveAll(false, false, false) != null) { for (Field f : unsolvedFields) { this.addAndExecute(new
         * SolveActionFactory().createAction(f.getCurrentValue(), f)); } return true; } else { return false; }
         */
    }

    /**
     * Goes back to the last correctly solved state in the action tree.
     * If current state is correct, nothing happens.
     * This is an assistance, so the AssistanceCost is increased.
     */
    fun goToLastCorrectState() {
        assistancesCost += 3
        while (!checkSudokuValidity()) {
            undo()
        }
        currentState.markCorrect()
    }

    /**
     * Goes back to the last book mark in the [ActionTree].
     * If the current state is already bookmarked, nothing happens.
     * Goes back to the root if there is no bookmark. TODO maybe better if nothing happens in that case
     */
    fun goToLastBookmark() {
        while (stateHandler!!.currentState != stateHandler!!.actionTree.root
                && !stateHandler!!.currentState!!.isMarked) {
            undo()
        }
    }

    /**
     * Sets the available assistances.
     *
     * @param assistances Die Assistances die für dieses Game gesetzt werden soll
     */
    fun setAssistances(assistances: GameSettings) {
        gameSettings = assistances

        /* calculate costs of passive assistances add them to the total assistance cost */
        if (isAssistanceAvailable(Assistances.autoAdjustNotes)) assistancesCost += 4
        if (isAssistanceAvailable(Assistances.markRowColumn)) assistancesCost += 2
        if (isAssistanceAvailable(Assistances.markWrongSymbol)) assistancesCost += 6
        if (isAssistanceAvailable(Assistances.restrictCandidates)) assistancesCost += 12
    }

    /**
     * Checks whether the given assistance is available.
     *
     * @param assist The assistance to check for availability
     *
     * @return true, if the assistance is available
     */
    fun isAssistanceAvailable(assist: Assistances): Boolean {//TODO don't accept null
        return gameSettings!!.getAssistance(assist)
    }

    val isLefthandedModeActive: Boolean
        get() = gameSettings!!.isLefthandModeSet

    /**
     * {@inheritDoc}
     */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("game")
        representation.addAttribute(XmlAttribute("id", "" + id))
        representation.addAttribute(XmlAttribute("finished", "" + finished))
        representation.addAttribute(XmlAttribute("time", "" + time))
        representation.addAttribute(XmlAttribute("currentTurnId", "" + currentState.id))
        representation.addChild(gameSettings!!.toXmlTree())
        representation.addAttribute(XmlAttribute("assistancesCost", "" + assistancesCost))
        representation.addChild(sudoku!!.toXmlTree())
        val actionList = ArrayList<ActionTreeElement>()
        for (ate in stateHandler!!.actionTree) {
            actionList.add(ate)
        }
        actionList.sort()
        for (ate in actionList) {
            if (ate.toXml() != null)
                representation.addChild(ate.toXml())
            // TODO cant add child do for null just nothing?
        }
        return representation
    }

    /**
     * {@inheritDoc}
     */
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        id = xmlTreeRepresentation.getAttributeValue("id").toInt()
        time = xmlTreeRepresentation.getAttributeValue("time").toInt()
        val currentStateId = xmlTreeRepresentation.getAttributeValue("currentTurnId").toInt()

        // Problems:
        // - What about corrupt files? is the game validated after it has been
        // filled?
        assistancesCost = xmlTreeRepresentation.getAttributeValue("assistancesCost").toInt()
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "sudoku") {
                sudoku = SudokuManager.emptySudokuToFillWithXml
                sudoku!!.fillFromXml(sub)
            } else if (sub.name == "gameSettings") {
                gameSettings = GameSettings()
                gameSettings!!.fillFromXml(sub)
            }
        }
        stateHandler = GameStateHandler()
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "action") {
                val diff = sub.getAttributeValue(ActionTreeElement.DIFF).toInt()

                // put the action to the parent action
                val attributeValue = sub.getAttributeValue(ActionTreeElement.PARENT)
                val parentID = attributeValue.toInt()
                val parent = stateHandler!!.actionTree.getElement(parentID)
                goToState(parent!!)//since we don't serialize the root node there should always be a parent

                // if(!sub.getAttributeValue(ActionTreeElement.PARENT).equals(""))
                // is not necessary since the root action comes from the gsh so

                // every element has e parent
                val field_id = sub.getAttributeValue(ActionTreeElement.FIELD_ID).toInt()
                val f = sudoku!!.getCell(field_id)!!
                if (sub.getAttributeValue(ActionTreeElement.ACTION_TYPE) == SolveAction::class.java.simpleName) {
                    stateHandler!!.addAndExecute(SolveActionFactory().createAction(f.currentValue + diff,
                            f))
                } else { // if(sub.getAttributeValue(ActionTreeElement.ACTION_TYPE).equals(NoteAction.class.getSimpleName()))
                    stateHandler!!.addAndExecute(NoteActionFactory().createAction(diff, f))
                }
                if (java.lang.Boolean.parseBoolean(sub.getAttributeValue(ActionTreeElement.MARKED))) {
                    markCurrentState()
                }
                var s = sub.getAttributeValue(ActionTreeElement.MISTAKE)
                if (s != null && java.lang.Boolean.parseBoolean(s)) {
                    currentState.markWrong()
                }
                s = sub.getAttributeValue(ActionTreeElement.CORRECT)
                if (s != null && java.lang.Boolean.parseBoolean(s)) {
                    currentState.markCorrect()
                }
            }
        }
        finished = java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("finished"))
        goToState(stateHandler!!.actionTree.getElement(currentStateId)!!)
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(obj: Any?): Boolean {//todo refactor
        if (obj is Game) {
            val g = obj
            return (id == g.id
                    && sudoku == g.sudoku
                    && stateHandler!!.actionTree == g.stateHandler!!.actionTree
                    && currentState.equals(g.currentState))
        }
        return false
    }
}