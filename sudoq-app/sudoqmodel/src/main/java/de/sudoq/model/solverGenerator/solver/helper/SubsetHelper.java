package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.CandidateSet;
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
	protected BitSet constraintSet;//TODO call it candidateset?

	/**
	 * Das BitSet, welches das gerade untersuchte Subset darstellt. Aus Performancegründen nicht lokal definiert.
	 */
	protected CandidateSet currentSet;

	/**
	 * Die Positionen, welche zum aktuell untersuchten Subset gehören. Aus Performancegründen nicht lokal definiert.
	 */
	protected List<Position> subsetPositions; //TODO make Stack? might be far more readable

	/**
	 * Ein BitSet für lokale Kopien zum vergleichen. Aus Performancegründen nicht lokal definiert.
	 */
	protected CandidateSet localCopy;

	/**
	 * Speichert alle Constraints des zugrundeliegenden Sudokus.
	 */
	protected ArrayList<Constraint> allConstraints; //TODO see if it can be replaced by iterator or just the sudokutype, because we dont want to modify the list of constraints!!!


	//protected static HintTypes labels[];

	/* Constructors */

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
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
	 */
	protected SubsetHelper(SolverSudoku sudoku, int level, int complexity) {
		super(sudoku, complexity);

		//hintType = labels[level-1];

		this.level = level;

		this.allConstraints  = this.sudoku.getSudokuType().getConstraints();
		this.constraintSet   = new BitSet();
		this.currentSet      = new CandidateSet();
		this.subsetPositions = new Stack<>();
		this.localCopy       = new CandidateSet();
	}

	/* Methods */

	/**
	 * Sucht so lange nach einem Subset mit der im Konstruktor spezifizierten Größe level, bis entweder eines
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

		//ArrayList<Position> positions;
		//iterate over all 'unique'-Constraints
		for (Constraint constraint : allConstraints) {
			if (constraint.hasUniqueBehavior()) {

				constraintSet = collectPossibleCandidates(constraint);

				//if we find 'level' or more candidates
				if (constraintSet.cardinality() >= this.level) {

					//initializeWith currentSet with the first 'level' candidates in constraintSet
					//TODO is there a better, clearer way?
					currentSet.clear();
					currentCandidate = -1;
					for (int i = 0; i < this.level; i++) {
						currentCandidate = constraintSet.nextSetBit(currentCandidate + 1);
						currentSet.set(currentCandidate);
					}

					/* this should be better
					currentSet = (BitSet) constraintSet.clone();
					while (currentSet.cardinality() > this.level)
						currentSet.clear(currentSet.length()-1);
					*/

					found = updateNext(constraint, buildDerivation);

					// Stop searching if a subset was found
					if (found) {
						break;
					}
				}
			}
		}
		return found;
	}

	protected abstract BitSet collectPossibleCandidates(Constraint constraint);

	//todo make this an iterator?
	/**
	 * Berechnet das nächste Subset des spezifizierten BitSets mit der im Konstruktor definierten Größe "level",
	 * ausgehend von demjenigen Subset, welches die niederwertigsten Kandidaten gesetzt hat. Das übergebene Subset muss
	 * bereits entsprechend viele Kandidaten gesetzt haben. Es wird immer der hochwertigste Kandidat erhöht bis dieser
	 * beim letzten Kandidaten angelangt ist, daraufhin wird der nächste Kandidat erhöht bis schließlich das
	 * hochwertigste Subset berechnet wurde.
	 *
	 * new interpretation: if highest bit in current set can be moved to the right, do so
	 *                     otherwise look from right to left for the first bit after(left of) a gap./fook for rightmost bit that can be moved to the right. move it to the right and set remaining bits next to it
	 * Gospers hack is a beautiful implementation, but worthless here as it operates on (binary) numbers, which we cannot convert to/from BitSets
	 * 
	 * @return true, falls es noch ein Subset gibt, false falls nicht
	 */
	protected boolean getNextSubset() {
		boolean nextSetExists = false;
		final BitSet allCandidates = constraintSet; //rename for clarity, holds all candiates(set to 1) in the current constraint

		int lastBitSet = currentSet.length() - 1;
		// Get the last set candidate

		// Calculate next candidate set if existing
		int nextCandidate = lastBitSet;
		int currentCandidate  = allCandidates.nextSetBit(lastBitSet + 1);//test if there is another candidate -> we can shift
		if (currentCandidate != -1) //if we found one
			currentCandidate++;  //increment for coming 'if' namely 'if (allCandidates.nextSetBit(nextCandidate + 1) != currentCandidate) {' left side is either -1 => no next set or currentCandidate, but we want ineq in that case , so increment it. Why not writing '-1' in the left side? because loop: the limit won't always be the last bit, it is shifted to the left.

		while (!nextSetExists && nextCandidate != -1) {//initially true iff currentSet not empty
			// If the next candidate can be increased without interfering with
			// the next one, do it
			if (allCandidates.nextSetBit(nextCandidate + 1) != currentCandidate) {
				nextSetExists = true;
				currentSet.clear(nextCandidate);
				currentCandidate = nextCandidate;
				while (currentSet.cardinality() < this.level) {//fill remaining
					currentCandidate = allCandidates.nextSetBit(currentCandidate + 1);
					currentSet.set(currentCandidate);
			}	}


			// If no set was found, get the next highest candidate to manipulate it
			// save nextCandidate in currentCandidate and have the new nextCandidate point to the next set bit below where it's currently pointing at; then delete old one
			if (!nextSetExists) {
				currentCandidate = nextCandidate;
				nextCandidate = -1;                                                   //
				while (currentSet.nextSetBit(nextCandidate + 1) < currentCandidate) { // == nextCandidate = currentSet.previousSetBit(nextCandidate)
					nextCandidate = currentSet.nextSetBit(nextCandidate + 1);         // but we don't have the api for it
				}
				currentSet.clear(currentCandidate);
			}
		}

		return nextSetExists;
	}


	/* these are all untested :-( but they are better to understand than current getNextSubset TODO substitute current one for these  */

	protected boolean getNextSubset2() {
		BitSet current = contract(currentSet, constraintSet);

		/* are all true bits at the end? == are all k bits at the end true?*/
		boolean lastElement = true;
		for(int i = 0; i < constraintSet.cardinality(); i++)
			lastElement &= current.get(constraintSet.cardinality()-i);

		if(lastElement)
			return false; // we're done
		else {
			//advance to next combination, project back to our candidates
			BitSet nextOne = step(current, constraintSet.cardinality(), currentSet.cardinality());
			currentSet = inflate(nextOne, constraintSet);
			return true;
		}
	}


	/**
	 * Provided a combination and values n,k calculate the next step in a loop of 'n choose k' combinations.
	 *
	 * @param bitSet
	 * @param n supposed length of bitSet i.e. nr of bits to choose from
	 * @param k size of the subsets
     * @return
     */
	private static BitSet step(BitSet bitSet, int n, int k){
		//idea: looking from right if there is a '1' that can be moved to the right, do so
		//      all other '1' right from it(that didn't have a '0' to their right) are lined up directly to the right of it
		//e.g. |11  11| -> |1 111 |
        int i;
		for (i= n-1; i>0; i--){

			if(bitSet.get(i))
				bitSet.clear(i);
			else
				if(bitSet.get(i-1)){
					bitSet.clear(i-1);
					break;
				}
		}
		for(;bitSet.cardinality() < k;i++)
			bitSet.set(i);

		return bitSet;
	}

	public static BitSet contract(BitSet sparseSet, BitSet pattern){

		BitSet denseSet = new BitSet();

		//loop over true bits of pattern as described in JavaDoc of 'nextSetBit'
		int denseIndex;
		int sparseIndex;
		for (sparseIndex = pattern.nextSetBit(0), denseIndex=0;
			 sparseIndex >= 0;
			 sparseIndex = pattern.nextSetBit(sparseIndex+1), denseIndex++) {

			denseSet.set(denseIndex, sparseSet.get(sparseIndex));

			if (sparseIndex == Integer.MAX_VALUE) break; // or (i+1) would overflow

		}
		return denseSet;
	}

	public static CandidateSet inflate(BitSet denseSet, BitSet pattern){
		CandidateSet sparseSet = new CandidateSet();

		int denseIndex;
		int sparseIndex;
		for (sparseIndex = pattern.nextSetBit(0), denseIndex=0;
			 sparseIndex >= 0;
			 sparseIndex = pattern.nextSetBit(sparseIndex+1), denseIndex++) {

			sparseSet.set(sparseIndex, denseSet.get(denseIndex));

			if (sparseIndex == Integer.MAX_VALUE) break; // or (i+1) would overflow

		}
		return sparseSet;
	}

	/**
	 * Sucht das nächste Subset der im Konstruktor definierten Größe {@code level} im spezifizierten {@link Constraint}
	 * mit dem spezifizierten Kandidaten-Set {@code set}, sowie dem aktuellen Subset. Es werden alle mittels der
	 * {@link this.getNextSubset}-Methode ab dem spezifizierten Subset ermittelten Kandidatenlisten überprüft.
	 * 
	 * @param constraint
	 *            Der Constraint, in dem ein NakedSubset gesucht werden soll
	 * @param buildDerivation
	 *            Gibt an, ob eine Herleitung für ein gefundenes Subset erstellt werden soll, welche über die
	 *            getDerivation Methode abgerufen werden kann
	 * @return true, falls ein Subset gefunden wurde, false falls nicht
	 */
	abstract protected boolean updateNext(Constraint constraint, boolean buildDerivation);
}
