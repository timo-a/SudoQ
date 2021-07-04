package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

/**
 * BranchinigPool maintains active branchings and recycles old objects
 *
 */
class BranchingPool {
	/**
	 * Stores discarded branching objects ready to be reused (we want to save on object instantiation for performance)
	 */
	private final Stack<Branching> branchingsReservoir;

	/**
	 * Ein Stack der erstellten und bereits vergebenen Maps
	 */
	private final Stack<Branching> branchingsInUse;


	/**
	 * Initialisiert einen neuen BranchingPool. Der Pool wird mit 2 PositionMaps initialisiert.
	 * 
	 */
	BranchingPool() {
		    branchingsInUse = new Stack<>();
		branchingsReservoir = new Stack<>();
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
		if (branchingsReservoir.isEmpty()) {
			ret = new Branching(p,candidate);
		}else{
			ret = branchingsReservoir.pop();
			ret.initializeWith(p,candidate);
		}

		branchingsInUse.push(ret); //store it among the used
		return ret;
	}

	/**
	 * Recycles most recent branching
	 */
	void recycleLastBranching() {
		if (!branchingsInUse.isEmpty()) {
			Branching returnedMap = branchingsInUse.pop(); //get last branching
			returnedMap.solutionsSet.clear();              //clear values
			branchingsReservoir.push(returnedMap);         //move to reservoir
		}
	}

	/**
	 * Recycles all branchings
	 */
	void recycleAllBranchings () {
		while (!this.branchingsInUse.isEmpty()) {
			recycleLastBranching();
		}
	}

	/**
	 * A branching object holds all the branching position and the candidate with wich the branch was started.
	 * It holds the candidates for every position BEFORE the branch took place in order to write them back if the branch is reverted
	 * as well as all solutions that wee entered into the sudoku SINCE the branch took place in order to delete them from the sudoku if the branch is reverted
	 * Alle Attribute sind package scope verfügbar, um diese direkt bearbeiten zu können.
	 * Aus Performancegründen wurde auf einen Zugriff durch Getter/Setter-Methoden verzichtet.
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
        final List<Position> solutionsSet; //TODO rename to sol..LIST??

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
			this.solutionsSet = new ArrayList<>();
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
