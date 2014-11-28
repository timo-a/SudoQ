/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintBehavior;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;

public abstract class TypeSquiggly extends TypeUniversal {

	/**
	 * Konstruktor zum weitervererben
	 * 
	 * @param length
	 *            die maximale Ausdehnung des Sudokutyps
	 * @param numberOfSymbols
	 *            die maximale Anzahl an Symbolen
	 */
	public TypeSquiggly(int length, int numberOfSymbols) {
		super(length, numberOfSymbols);
		addRowConstraints(new UniqueConstraintBehavior());
		addCollumnConstraints(new UniqueConstraintBehavior());
	}

	/**
	 * Instanziiert einen neuen SquigglySudokuType. Die Constraints des erzeugten Typs haben ein
	 * UniqueConstraintBehavior und entsprechen den Zeilen, Spalten und Blöcken des Standard-Sudokus. Die Blöcke haben
	 * durch ein Pattern definierte Umrisse
	 * 
	 * @param constraintPattern
	 *            Ein constraintPattern für die Blöcke.
	 * 
	 */
	public TypeSquiggly(String constraintPattern) {
		super((int) Math.sqrt(constraintPattern.length() + 0.0), (int) Math.sqrt(constraintPattern.length() + 0.0));

		addRowConstraints(new UniqueConstraintBehavior());
		addCollumnConstraints(new UniqueConstraintBehavior());

		addBlockConstraints(constraintPattern, new UniqueConstraintBehavior());

	}

	/**
	 * Erzeugt die Zeilen-Constraints
	 * 
	 * @param constraintBehavior
	 *            Constraintverhalten für alle Constraints
	 * @throws IllegalArgumentException
	 *             falls constraintBehavior null ist
	 */
	private void addRowConstraints(ConstraintBehavior constraintBehavior) {

		for (int j = 0; j < length; j++) {
			Constraint c = new Constraint(constraintBehavior, ConstraintType.LINE, "Row " + j);
			for (int i = 0; i < length; i++) {
				c.addPosition(positionsGrid[i][j]);
			}
			constraints.add(c);
		}
	}

	/**
	 * Erzeugt die Spalten-Constraints
	 * 
	 * @param constraintBehavior
	 *            Constraintverhalten für alle Constraints
	 * @throws IllegalArgumentException
	 *             falls constraintBehavior null ist
	 */
	private void addCollumnConstraints(ConstraintBehavior constraintBehavior) {

		for (int i = 0; i < length; i++) {
			Constraint c = new Constraint(constraintBehavior, ConstraintType.LINE, "Column " + i);
			for (int j = 0; j < length; j++) {
				c.addPosition(positionsGrid[i][j]);
			}
			constraints.add(c);
		}
	}
}