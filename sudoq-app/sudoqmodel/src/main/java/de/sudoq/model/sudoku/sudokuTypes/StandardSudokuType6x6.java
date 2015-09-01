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

public class StandardSudokuType6x6 extends TypeStandard {

	/**
	 * Erzeugt einen StandardSudokuType der Größe 6x6
	 */
	public StandardSudokuType6x6() {
		super(6);
		permutationProperties.remove(PermutationProperties.rotate90);
		permutationProperties.remove(PermutationProperties.rotate180);
		permutationProperties.remove(PermutationProperties.diagonal_up);
		permutationProperties.remove(PermutationProperties.diagonal_down);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SudokuTypes getEnumType() {
		return SudokuTypes.standard6x6;
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
				ret = new ComplexityConstraint(Complexity.easy, 26, 200, 350, 2);
				break;
			case medium:
				ret = new ComplexityConstraint(Complexity.medium, 17, 350, 550, 3);
				break;
			case difficult:
				ret = new ComplexityConstraint(Complexity.difficult, 12, 550, 800, Integer.MAX_VALUE);
				break;
			case infernal:
				ret = new ComplexityConstraint(Complexity.infernal, 10, 800, 10000, Integer.MAX_VALUE);
				break;
			case arbitrary:
				ret = new ComplexityConstraint(Complexity.arbitrary, 10, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);
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
		return 0.35f;
	}
}