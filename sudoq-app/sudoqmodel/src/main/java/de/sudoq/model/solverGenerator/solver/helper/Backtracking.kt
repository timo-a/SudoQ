package de.sudoq.model.solverGenerator.solver.helper

import de.sudoq.model.solverGenerator.solution.DerivationCell
import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.model.solverGenerator.solver.SolverSudoku
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * Dieser konkrete SolverHelper implementiert eine Vorgehensweise zum Lösen eines Sudokus.
 * Das Backtracking ist ein Trial and Error Verfahren,
 * wobei beginnend bei einem Feld systematisch versucht wird ein Symbol einzutragen,
 * sodass die Constraints weiterhin erfüllt sind.
 * Ist dies der Fall, so wird dasselbe mit dem nächsten Feld getan, bis entweder in
 * einem Feld beim Eintragen jedes Symbols die Constraints verletzt sind
 * oder die Kandidatenliste eines anderen Feldes
 * leer wird oder aber das gesamte Sudoku befüllt ist.
 * In den ersten beiden Fällen wird ein Feld zurückgegangen(Backtracking)
 * und dort mit dem nächsten Symbol fortgefahren.
 */
class Backtracking(sudoku: SolverSudoku, complexity: Int) : SolveHelper(sudoku, complexity) {

    init {
        hintType = HintTypes.Backtracking
    }

    /**
     * Wendet das Backtracking-Verfahren an und ermittelt damit die Lösung für alle Felder des Sudokus. Es wird die
     * Kandidatenliste desjenigen Feldes mit der kleinsten Kandidatenliste upgedated, sodass in dieser nur noch die
     * korrekte Lösung vorhanden ist. Gibt es keine eindeutige kleinste Kandidatenliste, so wird diese zufällig
     * ausgewählt.
     *
     * @param buildDerivation
     * Bestimmt, ob beim Finden eines Subsets eine Herleitung dafür erstellt werden soll, welche daraufhin
     * mit getDerivation abgerufen werden kann.
     * @return true, falls das Backtracking angewendet werden konnte, false falls nicht
     */
    override fun update(buildDerivation: Boolean): Boolean {
        super.derivation = null

        /* find among positions with several possible candidates one with minimal #candidates */

        //filter for all positions with more than one candidate
        val ambiguousPositions = ambiguous
        if (ambiguousPositions.isEmpty()) return false

        //take the position with minimal candidates
        val leastCandidatesPosition = getMinimalCandidatesPosition(ambiguousPositions)

        /*  */
        val candidates = sudoku.getCurrentCandidates(leastCandidatesPosition).clone() as BitSet
        val chosenCandidate = sudoku.getCurrentCandidates(leastCandidatesPosition).nextSetBit(0)
        sudoku.startNewBranch(leastCandidatesPosition, chosenCandidate)

        // UNCOMMENT THE FOLLOWING TO PRINT BACKTRACKING TRACE
        /*
		 * for (int i = 0; i < sudoku.branchings.size(); i++) { System.out.print(" "); }
		 * System.out.println(leastCandidatesPosition + "(#" + leastCandidates + "): " + chosenCandidate);
		 */if (buildDerivation) {
            super.derivation = SolveDerivation(HintTypes.Backtracking)
            val relevantCandidates = BitSet()
            relevantCandidates.set(chosenCandidate)
            candidates.clear(chosenCandidate)
            val derivField = DerivationCell(leastCandidatesPosition, relevantCandidates,
                    candidates)
            derivation!!.addDerivationCell(derivField)
            derivation!!.setDescription("Backtrack")
        }
        return true
    }

    private val ambiguous: List<Position>
        get() {
            val ambiguousPositions = Stack<Position>()
            for (p in sudoku.positions!!) {
                val cardinality = sudoku.getCurrentCandidates(p).cardinality()
                if (cardinality > 1) ambiguousPositions.push(p)
            }
            return ambiguousPositions
        }

    private fun getMinimalCandidatesPosition(ambiguousPositions: List<Position>): Position {
        val compareCardinality: Comparator<Position> = object : Comparator<Position> {
            private fun getCardinality(p: Position): Int {
                return sudoku.getCurrentCandidates(p).cardinality()
            }

            override fun compare(p1: Position, p2: Position): Int {
                return getCardinality(p1) - getCardinality(p2) //neg if cardinality(p1) < cardinality(p2), zero if eq, pos ...
            }
        }
        return Collections.min(ambiguousPositions, compareCardinality)
    }
}