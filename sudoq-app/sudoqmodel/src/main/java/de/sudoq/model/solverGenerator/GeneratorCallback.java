/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.solverGenerator;

import java.util.List;

import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.sudoku.Sudoku;

/**
 * Dieses Interface muss implementiert werden, um die generate-Methode des
 * Generator zu benutzen. Dieser führt die Generierung in einem anderen Thread
 * aus und ruft die generationFinished-Methode dieses Callback-Objektes auf,
 * sobald er fertig ist.
 */
public interface GeneratorCallback {
	/** Methods */

	/**
	 * Diese Methode wird vom Generator aufgerufen, nachdem er die Generierung
	 * eines Sudokus abgeschlossen hat. Das übergebene Sudoku soll dabei ein
	 * generiertes, valides Sudoku sein. Ist das spezifizierte Sudoku null, so
	 * wird nichts ausgeführt.
	 * 
	 * @param sudoku
	 *            Das vom Generator erzeugte, valide Sudoku
	 */
	public void generationFinished(Sudoku sudoku);

	public void generationFinished(Sudoku sudoku, List<Solution> s);
}
