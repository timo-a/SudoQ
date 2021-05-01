package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes

/**
 * Dieses Interface definiert eine einheitliche Schnittstelle für verschiedene Vorgehensweisen zum Lösen eines Sudokus.
 * Alle Helper aktualisieren bei erfolgreicher Anwendung der Vorgehensweise die Kandidatenlisten der Felder und
 * schränken so die Möglichkeiten ein Symbol in ein Feld einzutragen ein, womit i. A. andere Helper ihre Vorgehensweise
 * anwenden können.
 */
abstract class SolveHelper protected constructor(sudoku: SolverSudoku, complexity: Int) {
    /* Attributes */
    /**
     * Das Sudoku, auf welches dieser SolveHelper seine Vorgehensweise anwendet
     */
    @JvmField protected var sudoku: SolverSudoku
    /**
     * Gibt die Schwierigkeit der Anwendbarkeit dieses Helfers zurück. Dieser ist mit dem Konstruktor zu setzen.
     *
     * @return difficulty score of this helper
     */
    /**
     * Difficulty score of this helper e.g. 1 for `naked single` and 100 for `xwing` added up they give a score for the difficulty of a sudoku
     */
    val complexityScore: Int
    /**
     * Gibt die Herleitung des letzten update-Schrittes zurück, sofern dieser mit dem Parameter buildDerivation
     * aufgerufen wurde und erfolgreich war. Ansonsten wird false zurückgegeben.
     *
     * @return die Herleitung des letzten update-Schritte, falls vorhanden, sonst null
     */
    /**
     * Die Herleitung des letzten update-Schrittes;
     */
    open var derivation: SolveDerivation? = null
        protected set
    var hintType //public only for debugging
            : HintTypes? = null
    /* Methods */
    /**
     * Versucht die entsprechende Vorgehensweise solange auf das im konkreten SolveHelper gespeicherte Sudoku
     * anzuwenden, bis sie zum ersten Mal zum Erfolg führt oder sie nirgendwo anwendbar ist. Kann die Vorgehensweise
     * erfolgreich angewandt werden und wurde true für den Parameter buildDerivation übergeben, so wird dieses Vorgehen
     * als SolveDerivation gespeichert und kann über getDerivation abgerfragt werden. Führt die Vorgehensweise bei dem
     * spezifizierten Sudoku nicht zum Erfolg, so wird false zurückgegeben.
     *
     * @param buildDerivation
     * Bestimmt, ob eine Herleitung erstellt werden soll, welche durch die getDerivation-Methode abgerufen
     * werden kann oder nicht
     * @return true, falls dieser Helper angewendet werden konnte, false falls nicht
     */
    abstract fun update(buildDerivation: Boolean): Boolean
    /* Constructors */ /**
     * Creates a new SolveHelper for the specified Sudoku with the specified complexity.
     *
     * @param sudoku
     * sudoku to find a helper for. Mustn't be null
     * @param complexity
     * desired complexity for the final sudoku. Must be `>= 0`.
     */
    init {
        assert(sudoku != null)
        assert(complexity >= 0) { "complexity < 0 : $complexity" }
        this.sudoku = sudoku
        complexityScore = complexity
    }
}