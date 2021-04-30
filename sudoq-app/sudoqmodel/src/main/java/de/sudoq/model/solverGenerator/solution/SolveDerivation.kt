package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Sudoku
import java.util.*

/**
 * Ein Objekt dieser Klasse stellt einen Herleitungsschritt für die Lösung eines
 * Sudoku-Feldes dar. Dazu enthält es eine Liste von DerivationFields und
 * DerivationBlocks, die Informationenen über die entsprechend relevanten
 * Blöcke, sowie Kandidaten in den beteiligten Feldern enthalten.
 */
/*abstract*/ open class SolveDerivation @JvmOverloads constructor(
        /**
         * A string holding the name of the technique that led to this derivation
         */
        var type: HintTypes? = null) {
    /* Attributes */
    /**
     * A textual illustration of this solution step
     */
    private var description: String? = null

    /**
     * Eine Liste von DerivationFields, die für diesen Lösungsschritt relevant
     * sind
     */
    private val cells: MutableList<DerivationCell>

    /**
     * Eine Liste von DerivationBlocks, die für diesen Lösungsschritt relevant
     * sind
     */
    private val blocks: MutableList<DerivationBlock>
    protected var hasActionListCapability = false
    /* Methods */
    /**
     * Accepts a string description of what this derivation does
     * @param descrip String that describes the derivation
     */
    fun setDescription(descrip: String?) {
        description = descrip
    }

    /**
     * Diese Methode fügt das spezifizierte DerivationField zur Liste der
     * DerivationFields dieses SolveDerivation-Objektes hinzu. Ist das
     * übergebene Objekt null, so wird es nicht hinzugefügt.
     *
     * @param cell
     * Das DerivationField, welches dieser SolveDerivation
     * hinzugefügt werden soll
     */
    fun addDerivationCell(cell: DerivationCell?) {
        if (cell != null) cells.add(cell)
    }

    /**
     * Diese Methode fügt den spezifizierten DerivationBlock zur Liste der
     * DerivationBlocks dieses SolveDerivation-Objektes hinzu. Ist das übegebene
     * Objekt null, so wird es nicht hinzugefügt.
     *
     * @param block
     * Der DerivationBlock, welcher dieser SolveDerivation
     * hinzugefügt werden soll
     */
    fun addDerivationBlock(block: DerivationBlock?) {
        if (block == null) return
        blocks.add(block)
    }

    /**
     * Diese Methode gibt einen Iterator zurück, mit dem über die diesem Objekt
     * hinzugefügten DerivationFields iteriert werden kann.
     *
     * @return Ein Iterator, mit dem über die DerivationFields dieses
     * SolveDerivation-Objektes iteriert werden kann
     */
    val cellIterator: Iterator<DerivationCell>
        get() = cells.iterator()

    /**
     * Diese Methode gibt einen Iterator zurück, mithilfe dessen über die diesem
     * Objekt hinzugefügten DerivationBlocks iteriert werden kann.
     *
     * @return Ein Iterator, mit dem über die DerivationBlocks dieses
     * SolveDerivation-Objektes iteriert werden kann
     */
    val blockIterator: Iterator<DerivationBlock>
        get() = blocks.iterator()
    val derivationBlocks: List<DerivationBlock>
        get() = blocks

    fun hasActionListCapability(): Boolean {
        return hasActionListCapability
    }

    open fun getActionList(sudoku: Sudoku): List<Action?> {
        return ArrayList()
    }

    override fun toString(): String {
        return description!!
    }
    /* Constructors */ /**
     * Initiiert ein neues SolveDerivation-Objekt.
     */
    init {
        cells = ArrayList()
        blocks = ArrayList()
    }
}