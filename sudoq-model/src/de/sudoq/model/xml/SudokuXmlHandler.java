/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.io.File;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Der XmlHandler zum Laden und Speichern von Sudokus
 */
public class SudokuXmlHandler extends XmlHandler<Sudoku> {

	private Complexity complexity;
	private SudokuTypes type;

	/**
	 * Erzeugt einen SudokuXmlHandler, der ein neues sudoku file anlegt
	 */
	public SudokuXmlHandler() {
		this(null, null);
	}

	/**
	 * Erzeugt einen SudokuXmlHandler zum Laden eines zufälligen Sudokus mit den
	 * gegebenen Parametern
	 * 
	 * @param type
	 *            der Typ des zu ladenden Sudokus
	 * @param complexity
	 *            die Schwierigkeit des zu ladenden Sudokus
	 */
	public SudokuXmlHandler(SudokuTypes type, Complexity complexity) {
		this.type = type;
		this.complexity = complexity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected File getFileFor(Sudoku s) {
		if (type != null && complexity != null) {
			return FileManager.getRandomSudoku(type, complexity);
		} else if (s.getId() <= 0) {
			return FileManager.getNewSudokuFile(s);
		} else {
			return FileManager.getSudokuFile(s);
		}
	}

	@Override
	protected void modifySaveTree(XmlTree tree) {
		tree.addAttribute(new XmlAttribute("id", file.getName().substring(7, file.getName().length() - 4)));
	}

}
