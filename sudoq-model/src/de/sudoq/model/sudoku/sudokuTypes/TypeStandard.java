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

public abstract class TypeStandard extends TypeSquiggly {

	/**
	 * Instanziiert einen neuen StandardSudokuType. Die Constraints des erzeugten Typs haben ein
	 * UniqueConstraintBehavior und entsprechen den Zeilen, Spalten und Blöcken des Standard-Sudokus.
	 * 
	 * @param length
	 *            die Seitenlänge des Sudokus
	 */
	public TypeStandard(int length) {
		super(length, length);
		blockDimensions = getBlockDimensions(length);
		addBlockConstraints();
		initialisePermutationProperties();
	}

	private void initialisePermutationProperties() {
		permutationProperties.add(PermutationProperties.rotate90);
		permutationProperties.add(PermutationProperties.rotate180);
		permutationProperties.add(PermutationProperties.horizontal_Blockshift);
		permutationProperties.add(PermutationProperties.vertical_Blockshift);
		permutationProperties.add(PermutationProperties.inBlock_Collumnshift);
		permutationProperties.add(PermutationProperties.inBlock_Rowshift);
		permutationProperties.add(PermutationProperties.diagonal_up);
		permutationProperties.add(PermutationProperties.diagonal_down);
		permutationProperties.add(PermutationProperties.mirror_horizontal);
		permutationProperties.add(PermutationProperties.mirror_vertical);
	}

	/**
	 * Fügt BlockConstraints hinzu. Sind n Felder in einer Zeile, so werden n Blöcke zu je n Elementen hinzugefügt. Ist
	 * n keine Quadratzahl so sind die Blöcke breiter als hoch und dabei so quadratisch wie möglich.
	 */
	protected void addBlockConstraints() {
		int length = positionsGrid.length;

		int blockheight = blockDimensions.getY();
		int blockwidth = blockDimensions.getX();

		for (int i = 0; i < length; i++) {// Blöcke
			Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.BLOCK, "Block " + i);
			for (int j = 0; j < length; j++) {// Elemente

				c.addPosition(positionsGrid[i % blockheight * blockwidth + j % blockwidth][i / blockheight
						* blockheight + j / blockwidth]);
			}
			constraints.add(c);
		}
	}

	/**
	 * Errechnet aus der Länge des Sudokus die Abmessungen der Blöcke wobei die Differenz aus Höhe und Breite der Blöcke
	 * so klein wie möglich gehalten wird. Es gilt Breite >= Höhe
	 */
	protected Position getBlockDimensions(int length) {
		int y = (int) Math.sqrt(length + 0.0);
		while (length / y * y != length)
			y--;
		int x = length / y;

		return Position.get(x, y);
	}

}