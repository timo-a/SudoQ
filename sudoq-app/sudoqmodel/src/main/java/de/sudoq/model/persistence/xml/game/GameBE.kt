package de.sudoq.model.persistence.xml.game

import de.sudoq.model.actionTree.ActionTreeElement
import de.sudoq.model.actionTree.NoteActionFactory
import de.sudoq.model.actionTree.SolveAction
import de.sudoq.model.actionTree.SolveActionFactory
import de.sudoq.model.game.GameSettings
import de.sudoq.model.game.GameStateHandler
import de.sudoq.model.persistence.xml.sudoku.SudokuBE
import de.sudoq.model.persistence.xml.sudoku.SudokuMapper
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable2
import java.io.File
import java.util.*

class GameBE : Xmlable2 {

    /** Unique id for the game */
    var id: Int = -1

    /** Passed time since start of the game in seconds */
    var time = -1

    /**
     * Total sum of used assistances in this game.
     */
    var assistancesCost = 0
        private set

    /** id of current ActionTree node */
    private val currentTurnId: Int
        get() = currentState.id

    /**
     * The action tree node of the current state.
     */
    val currentState: ActionTreeElement
        get() = stateHandler!!.currentState!! //todo find a way to ensure it can never be null (the implicit root)

    /**
     * The sudoku of the game.
     */
    var sudoku: Sudoku? = null //todo make nonnullable

    /** manages the game state */
    var stateHandler: GameStateHandler? = null //todo make non-nullable

    var gameSettings: GameSettings? = null //TODO make non-nullable

    /** Indicates if game is finished */
    var finished = false

    /**
     * {@inheritDoc}
     */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("game")
        representation.addAttribute(XmlAttribute(ID, "" + id))
        representation.addAttribute(XmlAttribute(FINISHED, "" + finished))
        representation.addAttribute(XmlAttribute("time", "" + time))
        representation.addAttribute(XmlAttribute("currentTurnId", "" + currentTurnId))
        representation.addChild(gameSettings!!.toXmlTree())
        representation.addAttribute(XmlAttribute("assistancesCost", "" + assistancesCost))
        representation.addChild(SudokuMapper.toBE(sudoku!!).toXmlTree())
        val actionList = ArrayList<ActionTreeElement>()
        for (ate in stateHandler!!.actionTree) {
            actionList.add(ate)
        }
        actionList.sort()
        for (ate in actionList) {

            //add if not null
            ate.toXml()?.let { representation.addChild(it) }

        }
        return representation
    }

    override fun fillFromXml(xmlTreeRepresentation: XmlTree, sudokuDir: File) {
        id = xmlTreeRepresentation.getAttributeValue("id")!!.toInt()
        time = xmlTreeRepresentation.getAttributeValue("time")!!.toInt()
        val currentStateId = xmlTreeRepresentation.getAttributeValue("currentTurnId")!!.toInt()

        // Problems:
        // - What about corrupt files? is the game validated after it has been
        // filled?
        assistancesCost = xmlTreeRepresentation.getAttributeValue("assistancesCost")!!.toInt()
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "sudoku") {
                val sudokuBE = SudokuBE()
                sudokuBE.fillFromXml(sub, sudokuDir)
                sudoku = SudokuMapper.fromBE(sudokuBE)
            } else if (sub.name == "gameSettings") {
                gameSettings = GameSettings()
                gameSettings!!.fillFromXml(sub)
            }
        }
        stateHandler = GameStateHandler()
        for (sub in xmlTreeRepresentation) {//todo give xmltree handler its own xml capabilities
            if (sub.name == "action") {
                val diff = sub.getAttributeValue(ActionTreeElement.DIFF)!!.toInt()

                // put the action to the parent action
                val attributeValue = sub.getAttributeValue(ActionTreeElement.PARENT)
                val parentID = attributeValue!!.toInt()
                val parent = stateHandler!!.actionTree.getElement(parentID)
                goToState(parent!!)//since we don't serialize the root node there should always be a parent

                // if(!sub.getAttributeValue(ActionTreeElement.PARENT).equals(""))
                // is not necessary since the root action comes from the gsh so

                // every element has e parent
                val field_id = sub.getAttributeValue(ActionTreeElement.FIELD_ID)!!.toInt()
                val f = sudoku!!.getCell(field_id)!!
                if (sub.getAttributeValue(ActionTreeElement.ACTION_TYPE) == SolveAction::class.java.simpleName) {
                    stateHandler!!.addAndExecute(
                        SolveActionFactory().createAction(
                            f.currentValue + diff,
                            f
                        )
                    )
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
        finished =
            java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("finished"))
        goToState(stateHandler!!.actionTree.getElement(currentStateId)!!)
    }

    /**
     * Returns the state of the game to the given node in the action tree.
     * TODO what if the node is not in the action tree?
     *
     * @param ate The ActionTreeElement in which the state of the Sudoku is to be returned.
     *
     */
    private fun goToState(ate: ActionTreeElement) {
        stateHandler!!.goToState(ate)
    }

    /** Marks the current state to better find it later */
    private fun markCurrentState() {
        stateHandler!!.markCurrentState() //TODO what doe this mean is it a book mark?
    }

    /**
     * to fill from xml*/
    constructor() {//TODO who uses this? can it be removed?
        id = -1
    }

    constructor(
        id: Int,
        time: Int,
        assistancesCost: Int,
        sudoku: Sudoku,
        stateHandler: GameStateHandler,
        gameSettings: GameSettings,
        finished: Boolean
    ) {

        this.id = id
        this.time = time
        this.assistancesCost = assistancesCost
        this.sudoku = sudoku
        this.stateHandler = stateHandler
        this.gameSettings = gameSettings
        this.finished = finished
    }

    companion object {

        const val ID = "id"
        const val FINISHED = "finished"
        const val PLAYED_AT: String = "played_at"
        const val SUDOKU_TYPE = "sudoku_type"
        const val COMPLEXITY = "complexity"
    }
}