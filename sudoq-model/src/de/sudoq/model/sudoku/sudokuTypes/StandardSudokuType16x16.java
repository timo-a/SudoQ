/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

public class StandardSudokuType16x16 extends TypeStandard {

	/**
	 * Instanziiert einen neuen StandardSudokuType der Größe 16x16. Die
	 * Constraints des erzeugten Typs haben ein UniqueConstraintBehavior und
	 * entsprechen den Zeilen, Spalten und Blöcken des Standard-Sudokus.
	 */
	public StandardSudokuType16x16() {
		super(16);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SudokuTypes getEnumType() {
		return SudokuTypes.standard16x16;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComplexityConstraint buildComplexityConstraint(Complexity complexity) {
		ComplexityConstraint ret = null;

		if (complexity != null) {
			switch (complexity) {
			case easy:
				ret = new ComplexityConstraint(Complexity.easy, 190, 900, 1300, 2);
				break;
			case medium:
				ret = new ComplexityConstraint(Complexity.medium, 140, 1400, 2200, 3);
				break;
			case difficult:
				ret = new ComplexityConstraint(Complexity.difficult, 120, 2400, 3000, Integer.MAX_VALUE);
				break;
			case infernal:
				ret = new ComplexityConstraint(Complexity.infernal, 105, 3000, 25000, Integer.MAX_VALUE);
				break;
			case arbitrary:
				ret = new ComplexityConstraint(Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);
				break;
			}
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getStandardAllocationFactor() {
		return 0.25f;
	}

}