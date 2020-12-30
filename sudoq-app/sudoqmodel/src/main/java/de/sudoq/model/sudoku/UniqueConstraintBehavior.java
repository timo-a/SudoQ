/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

import java.util.ArrayList;
import java.util.List;

/**
 * Das UniqueConstraintBehavior repräsentiert ein Constraint-Verhalten, wobei kein Symbol innerhalb eines Constraints
 * doppelt vorkommen darf.
 */
public class UniqueConstraintBehavior implements ConstraintBehavior {
	/**
	 * Eine Liste der in der check-Methode gefundenen Ziffern. Wurde aus Performancegründen nicht lokal definiert.
	 */
	List<Integer> foundNumbers;

	/* Constructors */

	/**
	 * Instanziiert ein neues UniqueConstraintBehavior-Objekt.
	 */
	public UniqueConstraintBehavior() {
		this.foundNumbers = new ArrayList<>();
	}

	/* Methods */

	/**
	 * Überprüft, ob das spezifizierte Constraint das Unique-Verhalten erfüllt. D.h. es wird überprüft, ob innerhalb des
	 * Constraints kein Symbol in zwei Feldern eingetragen ist. Ist das Verhalten erfüllt, so wird true, andernfalls
	 * oder falls das spezifizierten Constraint null ist wird false zurückgegeben.
	 * 
	 * @return true, falls das spezifizierte Constraint dieses Verhalten erfüllt bzw. false falls es dies nicht tut oder
	 *         null übergeben wurde
	 */
	public boolean check(Constraint constraint, Sudoku sudoku) {
		int currentValue;
		foundNumbers.clear();

		ArrayList<Position> positions = constraint.getPositions();
		for (Position pos: positions) {
			currentValue = sudoku.getField(pos).getCurrentValue();
			if (currentValue != -1)
				if (foundNumbers.contains(currentValue))
					return false;
				else
					foundNumbers.add(currentValue);
		}

		return true;
	}
}
