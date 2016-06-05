package de.sudoq.model.solverGenerator.solver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;

class BranchingPool {
	/**
	 * Eine Liste der erstellten, noch nicht vergebenen Maps
	 */
	private Stack<Branching> unusedBranchings;

	/**
	 * Ein Stack der erstellten und bereits vergebenen Maps
	 */
	private Stack<Branching> usedBranchings;

	/**
	 * Die Anzahl der im Pool befindlichen PositionMaps
	 */
	private int numberOfAllocatedBranchings;

	/**
	 * Initialisiert einen neues PositionMapPool mit PositionMaps der spezifizierten Größe. Ist diese null oder ist eine
	 * Dimension gleich 0, so wird eine IllegalArgumentException geworfen. Der Pool wird mit 2 PositionMaps
	 * initialisiert.
	 * 
	 * @param dimension
	 *            Die Größe der verwalteten PositionMaps
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die Dimension null oder in einer Komponente 0 ist
	 */
	BranchingPool() {
		usedBranchings = new Stack<>();
		unusedBranchings = new Stack<>();
		numberOfAllocatedBranchings = 2;
		unusedBranchings.push(new Branching());
		unusedBranchings.push(new Branching());
	}

	/**
	 * Gibt eine PositionMap entsprechend der aktuell gesetzten Größe zurück. Ist der Pool leer, so wird seine Größe
	 * verdoppelt.
	 * 
	 * @return Eine PositionMap entsprechend der aktuell gesetzten Größe
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die spezifizierte Position null ist
	 */
	Branching getBranching(Position p, int candidate) {
		if (p == null)
			throw new IllegalArgumentException("Position was null");
		if (unusedBranchings.isEmpty()) {
			/*add nOAB new Branchings to unusedBranchings*/
			for (int i = 0; i < numberOfAllocatedBranchings; i++) {
				unusedBranchings.add(new Branching());
			}
			numberOfAllocatedBranchings *= 2;
		}

		Branching ret = unusedBranchings.pop();
		ret.position = p;
		ret.candidate = candidate;
		ret.complexityValue = 0;
		usedBranchings.push(ret);
		return ret;
	}

	/**
	 * Gibt die zuletzt geholte PositionMap an den Pool zurück.
	 */
	void returnBranching() {
		if (!usedBranchings.isEmpty()) {
			Branching returnedMap = usedBranchings.pop();
			returnedMap.solutionsSet.clear();
			unusedBranchings.push(returnedMap);
		}
	}

	/**
	 * Gibt alle PositionMaps an den Pool zurück.
	 */
	void returnAll() {
		while (!this.usedBranchings.isEmpty()) {
			returnBranching();
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
		PositionMap<BitSet> candidates;

		/**
		 * Der Komplexitätswert für diesen Branch
		 */
		int complexityValue;

		/**
		 * Erstellt ein neues Branching mit einem leeren SolutionSet. Alle anderen Attribute werden nicht initialisiert.
		 */
		protected Branching() {
			this.solutionsSet = new ArrayList<Position>();
		}

	}

}
