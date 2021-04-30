package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.sudoku.Position
import java.util.*

/**
 * Ein DerivationField stellt die Kandidatenliste eines Feldes, sowie die
 * relevanten Kandidaten eines Lösungsschrittes zum Lösen eines Sudoku-Feldes
 * dar.
 */
class DerivationCell(pos: Position?, relevantCandidates: BitSet?, irrelevantCandidates: BitSet?) {
    /* Attributes */
    /**
     * Diese Methode gibt die Position des Sudoku-Feldes zurück, auf dass sich
     * dieses DerivationField bezieht.
     *
     * @return Die Position des Feldes, auf das sich dieses DerivationField
     * bezieht
     */
    /**
     * Position of the Cell, whose candidates this DerivationCell represents
     */
    val position: Position

    /**
     * Die für die Lösung eines Feldes relevanten Kandidaten des zu diesem
     * DerivationField gehörigen Feldes
     */
    private val relevantCandidates: BitSet

    /**
     * Die in der Kandidatenliste vorhandenen, aber für den Lösungsschritt
     * irrelevanten Kandidaten des zu diesem DerivationField gehörigen Feldes
     */
    private val irrelevantCandidates: BitSet
    /* Methods */
    /**
     * Diese Methode gibt das BitSet der für mit diesem DerivationField
     * beschriebenen Lösungsschritt relevanten Kandidaten in dem der Position
     * entsprechenden Sudoku-Feld zurück.
     *
     * @return Einen Klon des BitSets, welches die relevanten Kandidaten des
     * zugehörigen Feldes für den Lösungsschritt repräsentiert
     */
    fun getRelevantCandidates(): BitSet {
        return relevantCandidates.clone() as BitSet
    }

    /**
     * Diese Methode gibt das BitSet der in der Kandidatenliste vorhandenen,
     * aber für den mit diesem DerivationField beschriebenen Lösungsschritt
     * irrelevanten Kandidaten in dem der Position entsprechenden Sudoku-Feld
     * zurück.
     *
     * @return Einen Klon des BitSets, welches die in der Kandidatenliste
     * vorhandenen, aber für den Lösungsschritt irrelevanten Kandidaten
     * des zugehörigen Feldes repräsentiert
     */
    fun getIrrelevantCandidates(): BitSet {
        return irrelevantCandidates.clone() as BitSet
    }
    /* Constructors */ /**
     * Dieser Konstruktor initiiert ein neues DerivationField, welches einen
     * Lösungsschritt für das Sudoku-Feld an der spezifizierten Position mit den
     * übergebenen relevanten und irrelevanten Kandidaten repräsentiert. Ist
     * einer der Parameter null, so wird eine IllegalArgumentException geworfen.
     *
     * @param pos
     * Die Position des Feldes, dessen Kandidaten dieses
     * DerivationField repräsentieren soll
     * @param relevantCandidates
     * Die für den zugehörigen Lösungsschritt relevanten Kandidaten
     * des Feldes an der angegebenen Position
     * @param irrelevantCandidates
     * Die in der Kandidatenliste vorhandenen, aber für den
     * Lösungsschritt irrelevanten Kandidaten des Feldes an der
     * angegebenen Position
     * @throws IllegalArgumentException
     * Wird geworfen, falls einer der Parameter null ist
     */
    init {
        require(!(pos == null || relevantCandidates == null || irrelevantCandidates == null)) { "one argument was null" }
        position = pos
        this.relevantCandidates = relevantCandidates.clone() as BitSet
        this.irrelevantCandidates = irrelevantCandidates.clone() as BitSet
    }
}