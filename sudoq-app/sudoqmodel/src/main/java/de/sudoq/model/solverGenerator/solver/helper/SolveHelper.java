package de.sudoq.model.solverGenerator.solver.helper;

import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;

/**
 * Dieses Interface definiert eine einheitliche Schnittstelle für verschiedene Vorgehensweisen zum Lösen eines Sudokus.
 * Alle Helper aktualisieren bei erfolgreicher Anwendung der Vorgehensweise die Kandidatenlisten der Felder und
 * schränken so die Möglichkeiten ein Symbol in ein Feld einzutragen ein, womit i. A. andere Helper ihre Vorgehensweise
 * anwenden können.
 */
public abstract class SolveHelper {
	/** Attributes */

	/**
	 * Das Sudoku, auf welches dieser SolveHelper seine Vorgehensweise anwendet
	 */
	protected SolverSudoku sudoku;

	/**
	 * Die Schwierigkeit der Anwendbarkeit dieses Lösungshelfers
	 */
	private int complexity;

	/**
	 * Die Herleitung des letzten update-Schrittes;
	 */
	protected SolveDerivation lastDerivation;

	/** Constructors */

	/**
	 * Erzeugt einen neues SolveHelper für das spezifizierte Sudoku mit der angegebenen Schwierigkeit.
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem operiert werden soll
	 * @param complexity
	 *            Die Schwierigkeit der Anwendbarkeit dieses Helfers (muss >= 0 sein)
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das spezifizierte Sudoku null oder die angegebene Schwierigkeit kleiner als 0
	 *             ist
	 */
	protected SolveHelper(SolverSudoku sudoku, int complexity) {
		if (sudoku == null)
			throw new IllegalArgumentException("sudoku was null");
		if (complexity < 0)
			throw new IllegalArgumentException("complexity < 0 : " + complexity);

		this.sudoku = sudoku;
		this.complexity = complexity;
	}

	/** Methods */

	/**
	 * Versucht die entsprechende Vorgehensweise solange auf das im konkreten SolveHelper gespeicherte Sudoku
	 * anzuwenden, bis sie zum ersten Mal zum Erfolg führt oder sie nirgendwo anwendbar ist. Kann die Vorgehensweise
	 * erfolgreich angewandt werden und wurde true für den Parameter buildDerivation übergeben, so wird dieses Vorgehen
	 * als SolveDerivation gespeichert und kann über getDerivation abgerfragt werden. Führt die Vorgehensweise bei dem
	 * spezifizierten Sudoku nicht zum Erfolg, so wird false zurückgegeben.
	 * 
	 * @param buildDerivation
	 *            Bestimmt, ob eine Herleitung erstellt werden soll, welche durch die getDerivation-Methode abgerufen
	 *            werden kann oder nicht
	 * @return true, falls dieser Helper angewendet werden konnte, false falls nicht
	 */
	abstract public boolean update(boolean buildDerivation);

	/**
	 * Gibt die Herleitung des letzten update-Schrittes zurück, sofern dieser mit dem Parameter buildDerivation
	 * aufgerufen wurde und erfolgreich war. Ansonsten wird false zurückgegeben.
	 * 
	 * @return die Herleitung des letzten update-Schritte, falls vorhanden, sonst null
	 */
	public SolveDerivation getDerivation() {
		return this.lastDerivation;
	}

	/**
	 * Gibt die Schwierigkeit der Anwendbarkeit dieses Helfers zurück. Dieser ist mit dem Konstruktor zu setzen.
	 * 
	 * @return Die Schwierigkeit der Anwendbarkeit dieses Helfers
	 */
	public int getComplexity() {
		return this.complexity;
	}
}
