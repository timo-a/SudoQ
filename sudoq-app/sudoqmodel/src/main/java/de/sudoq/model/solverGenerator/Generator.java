/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Diese Klasse stellt verschiedene Methoden zum Erstellen eines validen, neuen
 * Sudokus zur Verfügung. Dazu gibt es sowohl die Möglichkeit ein gänzlich neues
 * Sudoku mit einer spezifizierten Schwierigkeit erzeugen zu lassen, als auch
 * ein vorhandenes Sudoku durch Transformationen in ein Äquivalentes überführen
 * zu lassen.
 * 
 * @see Sudoku
 * @see Solver
 */
public class Generator {
	/** Attributes */

	private Random random;

	/* Constructors */

	/**
	 * Initiiert ein neues Generator-Objekt.
	 */
	public Generator() {
		random = new Random(0);
	}//Todo remove 0 again

	/* Methods */

	/**
	 * creates a sudoku of type @param{type} and difficulty @param{complexity} and appends it
	 * (together with the callback object) to the queue of sudokus being generated.
	 * If the queue is empty and there is no sudoku being generated at the moment,
	 * generation of the new sudoku beginns immediately.
	 * Otherwise it begins after all sudokus in the queue are generated.
	 * 
	 * Ist das spezifizierte SudokuType-Objekt oder das GeneratorCallback-Objekt
	 * null, oder hat das Complexity-Argument einen ungültigen Wert, so wird
	 * false zurückgegeben. Ansonsten ist der Rückgabewert true.
	 * 
	 * @param type
	 *            Der SudokuTypes-Enum Wert, aus welchem ein Sudoku erstellt und
	 *            generiert werden soll
	 * @param complexity
	 *            Die Komplexität des zu erstellenden Sudokus
	 * @param callbackObject
	 *            Das Objekt, dessen Callback-Methode aufgerufen werden soll,
	 *            sobald der Generator fertig ist
	 * @return true, falls ein leeres Sudoku erzeugt und der Warteschlange
	 *         hinzugefügt werden konnte, false andernfalls
	 */
	public boolean generate(SudokuTypes type, Complexity complexity, GeneratorCallback callbackObject) {
		if (type == null || complexity == null || callbackObject == null)
			return false;

		// Create sudoku
		Sudoku sudoku = new SudokuBuilder(type).createSudoku();
		sudoku.setComplexity(complexity);

		Thread t = new Thread(new GenerationAlgo(sudoku, callbackObject, random));
		t.start();

		// Initiate new random object
		random = new Random();

		return true;
	}


	/**
	 * returns all positions of non-null Fields of sudoku
	 * @param sudoku a sudoku object
	 *
	 * @return list of positions whose corresponding {@code Field} objects are not null
	 */
	public static List<Position> getPositions(Sudoku sudoku){
		List<Position> p = new ArrayList<>();

		for (int x = 0; x < sudoku.getSudokuType().getSize().getX(); x++)
			for (int y = 0; y < sudoku.getSudokuType().getSize().getY(); y++)
				if (sudoku.getCell(Position.get(x, y)) != null)
					p.add(Position.get(x, y));

		return p;
	}

	/**
	 * NUR ZU DEBUG-ZWECKEN: Setzt das Random-Objekt dieses Sudokus, um einen
	 * reproduzierbaren, deterministischen Ablauf des Generator zu provozieren.
	 * Das Random-Objekt muss vor jedem Aufruf der generate-Methode neu gesetzt
	 * werden.
	 * 
	 * @param rnd
	 *            Das zu setzende random Objekt.
	 */
	void setRandom(Random rnd) {
		this.random = rnd;
	}

}
