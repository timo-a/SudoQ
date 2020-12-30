package de.sudoq.model.solverGenerator.solution;

import de.sudoq.model.sudoku.Constraint;

/**
 * Ein DerivationBlock stellt ein Constraint dar, welches für einen
 * Lössungsschritt zum Lösen eines Sudoku-Feldes relevant ist.
 */
public class DerivationBlock {
	/* Attributes */

	/**
	 * Das Constraint, welches von diesem DerivationBlock als relevant für den
	 * zugehörigen Lösungsschritt repräsentiert wird
	 */
	private Constraint block;

	/* Constructors */

	/**
	 * Dies Methode initiiert einen neuen DerivationBlock für das
	 * spezifiziertem, für einen Lössungsschritt relevante Constraint. Ist
	 * dieses null, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param block
	 *            Das Constraint, welches von diesem DerivationBlock als für
	 *            einen Lössungsschritt relevant repräsentiert werden soll
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das spezifizierten Constraint null ist
	 */
	public DerivationBlock(Constraint block) {
		if (block == null)
			throw new IllegalArgumentException("specified block was null");
		this.block = block;
	}

	/* Methods */

	/**
	 * Diese Methode gibt das Constraint zurück, welches diesem DerivationBlock
	 * zugewiesen wurde.
	 * 
	 * @return Das Constraint, welches diesem DerivationBlock zugewiesen wurde
	 */
	public Constraint getBlock() {
		return block;
	}
}
