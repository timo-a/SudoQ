package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.BitSet;

import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;

public abstract class SubsetHelper extends SolveHelper {
	/** Attributes */

	/**
	 * Die Stufe des Helpers, wobei diese die Anzahl der Ziffern und Felder angibt, welche die Naked-Bedingung erfüllen
	 * sollen
	 */
	protected int level;

	/**
	 * Ein BitSet, welches alle Kandidaten des aktuell untersuchten Constraints enthält. Aus Performancegründen nicht
	 * lokal definiert.
	 */
	protected BitSet constraintSet;

	/**
	 * Das BitSet, welches das gerade untersuchte Subset darstellt. Aus Performancegründen nicht lokal definiert.
	 */
	protected BitSet currentSet;

	/**
	 * Die Positionen, welche zum aktuell untersuchten Subset gehören. Aus Performancegründen nicht lokal definiert.
	 */
	protected Position[] subsetPositions;

	/**
	 * Ein BitSet für lokale Kopien zum vergleichen. Aus Performancegründen nicht lokal definiert.
	 */
	protected BitSet localCopy;

	/**
	 * Speichert alle Constraints des zugrundeliegenden Sudokus.
	 */
	protected ArrayList<Constraint> allConstraints;

	/** Constructors */

	/**
	 * Erzeugt einen neuen NakedHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
	 * dabei der Größe der Symbolmenge nach der gesucht werden soll.
	 * 
	 * @param sudoku
	 *            Das Sudoku auf dem dieser Helper operieren soll
	 * @param level
	 *            Das Größe der Symbolmenge auf die der Helper hin überprüft
	 * @param complexity
	 *            Die Schwierigkeit der Anwendung dieser Vorgehensweise
	 * @param hidden
	 *            Definiert, ob nach versteckten oder nach sichtbaren Subsets gesucht werden soll
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
	 */
	protected SubsetHelper(SolverSudoku sudoku, int level, int complexity) {
		super(sudoku, complexity);
		if (level <= 0)
			throw new IllegalArgumentException("level <= 0 : " + level);
		this.level = level;

		this.allConstraints = this.sudoku.getSudokuType().getConstraints();
		this.constraintSet = new BitSet();
		this.currentSet = new BitSet();
		this.subsetPositions = new Position[this.level];
		this.localCopy = new BitSet();
	}

	/** Methods */

	/**
	 * Sucht so lange nach einem NakedSubset mit der im Konstruktor spezifizierten Größe level, bis entweder eines
	 * gefunden wird oder alle Möglichkeiten abgearbeitet sind. Wird ein Subset gefunden, werden die entsprechenden
	 * Kandidatenlisten upgedatet.
	 * 
	 * Wurde eine Lösung gefunden, so wird eine SolveDerivation, die die Herleitung des NakedSubsets darstellt,
	 * erstellt. Dabei werden in der SolveDerivation die Kandidatenlisten des betroffenen Constraints als
	 * irrelevantCandidates, sowie das gefundene Subset als relevantCandidates dargestellt. Diese kann durch die
	 * getDerivation Methode abgefragt werden.
	 * 
	 * @param buildDerivation
	 *            Bestimmt, ob beim Finden eines Subsets eine Herleitung dafür erstellt werden soll, welche daraufhin
	 *            mit getDerivation abgerufen werden kann.
	 * @return true, falls ein Subset gefunden wurde, false falls nicht
	 */
	public boolean update(boolean buildDerivation) {
		int currentCandidate = -1;
		lastDerivation = null;
		boolean found = false;

		ArrayList<Position> positions;
		for (int constrNum = 0; constrNum < allConstraints.size(); constrNum++) {
			if (allConstraints.get(constrNum).hasUniqueBehavior()) {
				// Save the constraint being checked for naked subsets.
				// Combine all the candidate lists in this constraint
				constraintSet.clear();
				positions = allConstraints.get(constrNum).getPositions();
				for (int i = 0; i < positions.size(); i++) {
					if (this instanceof HiddenHelper
							|| this.sudoku.getCurrentCandidates(positions.get(i)).cardinality() <= this.level)
						constraintSet.or(this.sudoku.getCurrentCandidates(positions.get(i)));
				}

				if (constraintSet.cardinality() >= this.level) {
					// Initialize the current set to check for naked subset by
					// selecting the first candidates in this constraint
					currentSet.clear();
					currentCandidate = -1;
					for (int i = 0; i < this.level; i++) {
						currentCandidate = constraintSet.nextSetBit(currentCandidate + 1);
						currentSet.set(currentCandidate);
					}

					found = updateNext(allConstraints.get(constrNum), buildDerivation);

					// Stop searching if a subset was found
					if (found) {
						break;
					}
				}
			}
		}

		return found;
	}

	/**
	 * Berechnet das nächste Subset des spezifizierten BitSets mit der im Konstruktor definierten Größe "level",
	 * ausgehend von demjenigen Subset, welches die niederwertigsten Kandidaten gesetzt hat. Das übergebene Subset muss
	 * bereits entsprechend viele Kandidaten gesetzt haben. Es wird immer der hochwertigste Kandidate erhöht bis dieser
	 * beim letzten Kandidaten angelangt ist, daraufhin wird der nächste Kandidat erhöht bis schließlich das
	 * hochwertigste Subset berechnet wurde.
	 * 
	 * @param set
	 *            Das Set aus dem das Subset berechnet werden soll
	 * @param subset
	 *            Das Subset, aus dem das nächste Subset aus dem spezifizierten BitSet berechnet werden soll
	 * @return true, falls es noch ein Subset gibt, false falls nicht
	 */
	protected boolean getNextSubset() {
		boolean nextSetExists = false;

		// Get the last set candidate
		int currentCandidate = currentSet.length() - 1;

		// Calculate next candidate set if existing
		int nextCandidate = currentCandidate;
		currentCandidate = constraintSet.nextSetBit(currentCandidate + 1);
		if (currentCandidate != -1)
			currentCandidate++;

		while (!nextSetExists && nextCandidate != -1) {
			// If the next candidate can be increased without interfering with
			// the next one, do it
			if (constraintSet.nextSetBit(nextCandidate + 1) != currentCandidate) {
				nextSetExists = true;
				currentSet.clear(nextCandidate);
				currentCandidate = nextCandidate;
				while (currentSet.cardinality() < this.level) {
					currentCandidate = constraintSet.nextSetBit(currentCandidate + 1);
					currentSet.set(currentCandidate);
				}
			}

			// If no set was found, get the next candidate to manipulate it
			if (!nextSetExists) {
				currentCandidate = nextCandidate;
				nextCandidate = -1;
				while (currentSet.nextSetBit(nextCandidate + 1) < currentCandidate) {
					nextCandidate = currentSet.nextSetBit(nextCandidate + 1);
				}
				currentSet.clear(currentCandidate);
			}
		}

		return nextSetExists;
	}

	/**
	 * Sucht das nächste Subset der im Konstruktor definierten Größe {@code level} im spezifizierten {@link Constraint}
	 * mit dem spezifizierten Kandidaten-Set {@code set}, sowie dem aktuellen Subset. Es werden alle mittels der
	 * {@link getNextSubset}-Methode ab dem spezifizierten Subset ermittelten Kandidatenlisten überprüft.
	 * 
	 * @param constraint
	 *            Das Constraint, in dem ein NakedSubset gesucht werden soll
	 * @param buildDerivation
	 *            Gibt an, ob eine Herleitung für ein gefundenes Subset erstellt werden soll, welche über die
	 *            getDerivation Methode abgerufen werden kann
	 * @return true, falls ein Subset gefunden wurde, false falls nicht
	 */
	abstract protected boolean updateNext(Constraint constraint, boolean buildDerivation);
}
