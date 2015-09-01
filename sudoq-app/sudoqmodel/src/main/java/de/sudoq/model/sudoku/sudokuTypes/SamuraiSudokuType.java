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
 * Erzeugt einen SamuraiSudokuType
 */
public class SamuraiSudokuType extends TypeUniversal {

	/**
	 * Erzeugt einen neuen SamuraiSudokuTyp
	 */
	public SamuraiSudokuType() {
		super(21, 9);
		addConstraints();
		blockDimensions = Position.get(3, 3);
		permutationProperties.add(PermutationProperties.rotate90);// we can do
																	// much mini
																	// permutations
																	// here, but
																	// not now
		permutationProperties.add(PermutationProperties.rotate180);
		permutationProperties.add(PermutationProperties.mirror_horizontal);
		permutationProperties.add(PermutationProperties.mirror_vertical);
		permutationProperties.add(PermutationProperties.diagonal_up);
		permutationProperties.add(PermutationProperties.diagonal_down);
	}

	private void addConstraints() {

		addRows();
		addColumns();
		for (int i = 0; i < 9; i++) {
			addBlock(Position.get(0 + i % 3 * 3, 0 + i / 3 * 3), i);
		}
		for (int i = 0; i < 9; i++) {
			addBlock(Position.get(12 + i % 3 * 3, 0 + i / 3 * 3), i + 9);
		}
		for (int i = 0; i < 9; i++) {
			addBlock(Position.get(0 + i % 3 * 3, 12 + i / 3 * 3), i + 18);
		}
		for (int i = 0; i < 9; i++) {
			addBlock(Position.get(12 + i % 3 * 3, 12 + i / 3 * 3), i + 27);
		}

		addBlock(Position.get(9, 6), 36);
		addBlock(Position.get(6, 9), 37);
		addBlock(Position.get(9, 9), 38);
		addBlock(Position.get(12, 9), 39);
		addBlock(Position.get(9, 12), 40);

	}

	private void addRows() {
		for (int i = 0; i < 9; i++) {
			addRow(Position.get(0, 0 + i), 9, i);
		}
		for (int i = 0; i < 9; i++) {
			addRow(Position.get(12, 0 + i), 9, i + 9);
		}
		for (int i = 0; i < 9; i++) {
			addRow(Position.get(0, 12 + i), 9, i + 18);
		}
		for (int i = 0; i < 9; i++) {
			addRow(Position.get(12, 12 + i), 9, i + 27);
		}
		for (int i = 0; i < 9; i++) {
			addRow(Position.get(6, 6 + i), 9, i + 36);
		}
	}

	private void addColumns() {
		for (int i = 0; i < 9; i++) {
			addColumn(Position.get(0 + i, 0), 9, i);
		}
		for (int i = 0; i < 9; i++) {
			addColumn(Position.get(12 + i, 0), 9, i + 9);
		}
		for (int i = 0; i < 9; i++) {
			addColumn(Position.get(0 + i, 12), 9, i + 18);
		}
		for (int i = 0; i < 9; i++) {
			addColumn(Position.get(12 + i, 12), 9, i + 27);
		}
		for (int i = 0; i < 9; i++) {
			addColumn(Position.get(6 + i, 6), 9, i + 36);
		}
	}

	private void addRow(Position start, int lenght, int number) {
		Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE, "Row " + number);
		for (int i = 0; i < lenght; i++) {
			c.addPosition(positionsGrid[start.getX() + i][start.getY()]);
		}
		constraints.add(c);
	}

	private void addColumn(Position start, int lenght, int number) {
		Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE, "Column " + number);
		for (int i = 0; i < lenght; i++) {
			c.addPosition(positionsGrid[start.getX()][start.getY() + i]);
		}
		constraints.add(c);
	}

	// length is 9
	private void addBlock(Position start, int number) {
		Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.BLOCK, "Block " + number);
		for (int i = 0; i < 9; i++) {
			c.addPosition(positionsGrid[start.getX() + i % 3][start.getY() + i / 3]);
		}
		constraints.add(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SudokuTypes getEnumType() {
		return SudokuTypes.samurai;
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
				ret = new ComplexityConstraint(Complexity.easy, 70, 1000, 1500, 2);
				break;
			case medium:
				ret = new ComplexityConstraint(Complexity.medium, 55, 1500, 2300, 3);
				break;
			case difficult:
				ret = new ComplexityConstraint(Complexity.difficult, 40, 2300, 4000, Integer.MAX_VALUE);
				break;
			case infernal:
				ret = new ComplexityConstraint(Complexity.infernal, 30, 4000, 25000, Integer.MAX_VALUE);
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
		return 0.05f;
	}
}