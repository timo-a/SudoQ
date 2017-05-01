/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

/*Never used, just here to show what's possible.
  Sudokus don't use this! or do they? not sure anymore...
  I say they wouln't be neccessary if the fully solved part was moved to uniquebehavior*/

/**
 * Das SumConstraintBehavior repräsentiert ein Constraint-Verhalten, wobei die Summe der Feld-Werte eines Constraints
 * eine vorgegebene Summe haben muss.
 */
public class SumConstraintBehavior implements ConstraintBehavior {
	/** Attributes */

	/**
	 * Die Summe, auf welche das SumConstraintBehavior die Felder des Constraints auf dem Sudoku ueberprueft.
	 */
	private int sum;

	/** Constructors */

	/**
	 * Instanziiert ein neues SumConstraintBehavior-Objekt, welches Constraints auf die spezifizierte Summe der
	 * zugehörigen Feldwerte überprüfen kann. Ist diese kleiner als 0, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param sum
	 *            Die Summe, die die Feldwerte in diesem Constraint haben müssen
	 */
	public SumConstraintBehavior(int sum) {
		if (sum >= 0) {
			this.sum = sum;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/** Methods */

	/**
	 * Überprüft, ob das spezifizierte Constraint dieses Verhalten auf dem uebergebenen Sudoku erfüllt. Dies bedeutet,
	 * dass überprüft wird, ob die Summe der aktuellen Feldwerte des Constraints kleiner ist als der im Konstruktor
	 * dieses Verhaltens vorgegebene Wert bzw. gleich diesem ist, falls alle Felder des Constraints befüllt sind. Ist
	 * das Verhalten erfüllt, so wird true, andernfalls oder falls das spezifizierte Constraint null ist wird false
	 * zurückgegeben.
	 * 
	 * @return true, falls das spezifizierte Constraint dieses Verhalten erfüllt bzw. false falls es dies nicht tut oder
	 *         null übergeben wurde
	 */
	@Override
	public boolean check(Constraint constraint, Sudoku sudoku) {
		int fieldSum = 0;
		boolean fullySolved = true;
		for (Position pos : constraint) {
			fieldSum += sudoku.getField(pos).getCurrentValue();
			if (sudoku.getField(pos).isNotSolved())
				fullySolved = false;
		}
		return (fieldSum == sum || (!fullySolved && fieldSum <= sum));
	}
}
