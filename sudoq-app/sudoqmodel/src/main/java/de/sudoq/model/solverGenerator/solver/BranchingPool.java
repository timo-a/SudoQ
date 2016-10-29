package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

class BranchingPool {
	/**
	 * Stores discarded branching objects ready to be reused (we want to save on object instantiation for performance)
	 */
	private Stack<Branching> recycledBranchings;

	/**
	 * Ein Stack der erstellten und bereits vergebenen Maps
	 */
	private Stack<Branching> usedBranchings;


	/**
	 * Initialisiert einen neuen BranchingPool. Der Pool wird mit 2 PositionMaps initialisiert.
	 * 
	 */
	BranchingPool() {
		    usedBranchings = new Stack<>();
		recycledBranchings = new Stack<>();
	}

	/**
	 * Returns an unused Branching initialized with Position {@code p} and Candidate {@code candidate}.
	 * If possible a branch is recycled, otherwise newly instantiated.
	 *
	 * @return a Branching initialized with {@code p} and {@code candidate}
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die spezifizierte Position null ist
	 */
	Branching getBranching(Position p, int candidate) {
		if (p == null)
			throw new IllegalArgumentException("Position was null");

		/* fetch a new Branching. Preferrably recycle one from unused */
		Branching ret;
		/* if no unused Branchings ready */
		if (recycledBranchings.isEmpty()) {
			ret = new Branching(p,candidate);
		}else{
			ret = recycledBranchings.pop();
			ret.initializeWith(p,candidate);
		}

		usedBranchings.push(ret); //store it among the used
		return ret;
	}

	/**
	 * Recycles most recent branching
	 */
	void recycleLastBranching() {
		if (!usedBranchings.isEmpty()) {
			Branching returnedMap = usedBranchings.pop();
			returnedMap.solutionsSet.clear();
			recycledBranchings.push(returnedMap);
		}
	}

	/**
	 * Recycles all branchings
	 */
	void recycleAllBranchings () {
		while (!this.usedBranchings.isEmpty()) {
			recycleLastBranching();
		}
	}

	/**
	 * Ein Branching-Objekt beschreibt einen Zweig der temporären Lösung mit dessen zugrundeliegender Einstiegsposition,
	 * dem Kandidaten, der für den Einstieg gewählt wurde und einer Liste von Lösungen, die in diesem Branch eingetragen
	 * wurden und nach dessen Entfernen zurückgesetzt werden müssen. Alle Attribute sind package scope verfügbar, um
	 * diese direkt bearbeiten zu können. Aus Performancegründen wurde auf einen Zugriff durch Getter/Setter-Methoden
	 * verzichtet.
	 */
	class Branching {
		/**
		 * Die Position, an der gebrancht wurde
		 */
		Position position;

		/**
		 * Der Kandidate mit dem gebrancht wurde
		 */
		int candidate;

		/**
		 * Die Liste von Positionen an denen in diesem Branch eine Lösung eingetragen wurde.
		 */
		List<Position> solutionsSet; //TODO rename to sol..LIST??

		/**
		 * Eine Map, welche für jede Position dessen Kandidaten vor dem Branchen speichert.
		 */
		PositionMap<CandidateSet> candidates;

		/**
		 * Der Komplexitätswert für diesen Branch
		 */
		int complexityValue;

		/**
		 * Erstellt ein neues Branching mit einem leeren SolutionSet.
		 * position and candidate are set by parameter values, complexity value is set to 0
		 */
		protected Branching(Position p, int candidate) {
			this.solutionsSet = new ArrayList<Position>();
			initializeWith(p, candidate);
		}

		//we have an extra method here to ensure that reinitialization as well as initialization are invariant with regard to pos, can, complxVal
		protected void initializeWith(Position p, int candidate){
			this.position       =p;
			this.candidate      =candidate;
			this.complexityValue=0;
		}

	}

}
