/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

/**
 * Erzeugt ein StandardSudokuType der Größe 9x9 bei dem zusätzlich die Diagonalen ein UniqueConstraintBehaviour erfüllen
 * müssen
 * 
 */
public class XSudoku extends TypeStandard {

	/**
	 * Erzeugt einen neuen XSudokutyp
	 */
	public XSudoku() {
		super(9);
		addXConstraints(positionsGrid);
		permutationProperties.remove(PermutationProperties.horizontal_Blockshift);
		permutationProperties.remove(PermutationProperties.vertical_Blockshift);
		permutationProperties.remove(PermutationProperties.inBlock_Collumnshift);
		permutationProperties.remove(PermutationProperties.inBlock_Rowshift);
	}

	/**
	 * fügt Constraints für die Diagonalen hinzu
	 * 
	 * @param positionsGrid
	 *            das Array dem die Positions entnommen werden
	 */
	protected void addXConstraints(Position[][] positionsGrid) {
		int length = positionsGrid.length;
		Constraint c1 = new Constraint(new UniqueConstraintBehavior(), ConstraintType.EXTRA,
				"Extra block diagonal down right");
		Constraint c2 = new Constraint(new UniqueConstraintBehavior(), ConstraintType.EXTRA,
				"Extra block diagonal up right");

		for (int i = 0; i < 9; i++) {
			c1.addPosition(positionsGrid[i][i]);
			c2.addPosition(positionsGrid[i][length - 1 - i]);
		}

		constraints.add(c1);
		constraints.add(c2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SudokuTypes getEnumType() {
		return SudokuTypes.Xsudoku;
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
				ret = new ComplexityConstraint(Complexity.easy, 40, 450, 750, 2);
				break;
			case medium:
				ret = new ComplexityConstraint(Complexity.medium, 32, 700, 1050, 3);
				break;
			case difficult:
				ret = new ComplexityConstraint(Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE);
				break;
			case infernal:
				ret = new ComplexityConstraint(Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE);
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