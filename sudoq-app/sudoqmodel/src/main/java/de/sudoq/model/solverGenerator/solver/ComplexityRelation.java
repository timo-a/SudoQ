package de.sudoq.model.solverGenerator.solver;

/**
 * Ein Enum um die Relationen zwischen Schwierigkeiten zu klassifizieren
 */
public enum ComplexityRelation {
	/**
	 * Das Sudoku war gemäß den Constraints ungültig
	 */
	INVALID,

	/**
	 * Das Sudoku lässt mehrere Lösungen zu
	 */
	AMBIGUOUS,

	/**
	 * Das Sudoku ist innerhalb der Constraints lösbar
	 */
	CONSTRAINT_SATURATION,

	/**
	 * Das Sudoku ist einfacher als die Constraints erlauben
	 */
	TOO_EASY,

	/**
	 * Das Sudoku ist viel einfacher als die Constraints erlauben
	 */
	MUCH_TOO_EASY,

	/**
	 * Das Sudoku ist schwieriger als die Constraints erlauben
	 */
	TOO_DIFFICULT,

	/**
	 * Das Sudoku ist viel schwieriger als die Constraints erlauben
	 */
	MUCH_TOO_DIFFICULT
}
