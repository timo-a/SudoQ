package de.sudoq.model.solverGenerator.solution;

import java.util.BitSet;

import de.sudoq.model.sudoku.Position;

/**
 * Ein DerivationField stellt die Kandidatenliste eines Feldes, sowie die
 * relevanten Kandidaten eines Lösungsschrittes zum Lösen eines Sudoku-Feldes
 * dar.
 */
public class DerivationCell {
    /* Attributes */

	/**
	 * Position of the Cell, whose candidates this DerivationCell represents
	 */
	private Position position;

	/**
	 * Die für die Lösung eines Feldes relevanten Kandidaten des zu diesem
	 * DerivationField gehörigen Feldes
	 */
	private BitSet relevantCandidates;

	/**
	 * Die in der Kandidatenliste vorhandenen, aber für den Lösungsschritt
	 * irrelevanten Kandidaten des zu diesem DerivationField gehörigen Feldes
	 */
	private BitSet irrelevantCandidates;

    /* Constructors */

	/**
	 * Dieser Konstruktor initiiert ein neues DerivationField, welches einen
	 * Lösungsschritt für das Sudoku-Feld an der spezifizierten Position mit den
	 * übergebenen relevanten und irrelevanten Kandidaten repräsentiert. Ist
	 * einer der Parameter null, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param pos
	 *            Die Position des Feldes, dessen Kandidaten dieses
	 *            DerivationField repräsentieren soll
	 * @param relevantCandidates
	 *            Die für den zugehörigen Lösungsschritt relevanten Kandidaten
	 *            des Feldes an der angegebenen Position
	 * @param irrelevantCandidates
	 *            Die in der Kandidatenliste vorhandenen, aber für den
	 *            Lösungsschritt irrelevanten Kandidaten des Feldes an der
	 *            angegebenen Position
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls einer der Parameter null ist
	 */
	public DerivationCell(Position pos, BitSet relevantCandidates, BitSet irrelevantCandidates) {
		if (pos == null || relevantCandidates == null || irrelevantCandidates == null)
			throw new IllegalArgumentException("one argument was null");
		this.position = pos;
		this.relevantCandidates = (BitSet) relevantCandidates.clone();
		this.irrelevantCandidates = (BitSet) irrelevantCandidates.clone();
	}

    /* Methods */

	/**
	 * Diese Methode gibt die Position des Sudoku-Feldes zurück, auf dass sich
	 * dieses DerivationField bezieht.
	 * 
	 * @return Die Position des Feldes, auf das sich dieses DerivationField
	 *         bezieht
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Diese Methode gibt das BitSet der für mit diesem DerivationField
	 * beschriebenen Lösungsschritt relevanten Kandidaten in dem der Position
	 * entsprechenden Sudoku-Feld zurück.
	 * 
	 * @return Einen Klon des BitSets, welches die relevanten Kandidaten des
	 *         zugehörigen Feldes für den Lösungsschritt repräsentiert
	 */
	public BitSet getRelevantCandidates() {
		return (BitSet) relevantCandidates.clone();
	}

	/**
	 * Diese Methode gibt das BitSet der in der Kandidatenliste vorhandenen,
	 * aber für den mit diesem DerivationField beschriebenen Lösungsschritt
	 * irrelevanten Kandidaten in dem der Position entsprechenden Sudoku-Feld
	 * zurück.
	 * 
	 * @return Einen Klon des BitSets, welches die in der Kandidatenliste
	 *         vorhandenen, aber für den Lösungsschritt irrelevanten Kandidaten
	 *         des zugehörigen Feldes repräsentiert
	 */
	public BitSet getIrrelevantCandidates() {
		return (BitSet) irrelevantCandidates.clone();
	}
}
