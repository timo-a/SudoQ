package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.sudoku.Constraint

/**
 * A block that is relevant to a derivation.
 *
 * @property block the relevant block
 */
class DerivationBlock(val block: Constraint) {}