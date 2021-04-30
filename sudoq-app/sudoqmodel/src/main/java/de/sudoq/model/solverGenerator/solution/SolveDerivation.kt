package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Sudoku
import java.util.*

/**
 * A Derivation on the way to a solution of a Cell.
 *
 * @property type the technique that led to this derivation
 */
open class SolveDerivation @JvmOverloads constructor(var type: HintTypes?) {

    /**
     * A textual illustration of this solution step
     */
    private var description: String? = null

    /**
     * A list of [DerivationCell]s, that are relevant fo this step
     */
    private val cells: MutableList<DerivationCell> = ArrayList()

    /**
     * A list of [DerivationBlock]s that are relevant for this step
     */
    private val blocks: MutableList<DerivationBlock> = ArrayList()

    protected var hasActionListCapability = false //maybe cleaner to have classes implement interface ActionList

    constructor() : this(null)


    /**
     * Accepts a string description of what this derivation does
     * @param descrip String that describes the derivation
     */
    fun setDescription(description: String) {
        this.description = description
    }

    /**
     * Adds a DerivationCell
     *
     * @param cell [DerivationCell] to add
     */
    fun addDerivationCell(cell: DerivationCell) {
        cells.add(cell)
    }

    /**
     * Adds a DerivationBlock
     *
     * @param cell [DerivationBlock] to add
     */
    fun addDerivationBlock(block: DerivationBlock) {
        blocks.add(block)
    }

    /**
     * Iterator over [DerivationCell]s
     *
     * @return Iterator over [DerivationCell]s
     */
    val cellIterator: Iterator<DerivationCell>
        get() = cells.iterator()

    /**
     * Iterator over [DerivationBlock]s
     *
     * @return Iterator over [DerivationBlock]s
     */
    val blockIterator: Iterator<DerivationBlock>
        get() = blocks.iterator()

    val derivationBlocks: List<DerivationBlock>
        get() = blocks

    fun hasActionListCapability(): Boolean {
        return hasActionListCapability
    }

    open fun getActionList(sudoku: Sudoku): List<Action> {
        return ArrayList()
    }

    override fun toString(): String {
        return description!!
    }
}