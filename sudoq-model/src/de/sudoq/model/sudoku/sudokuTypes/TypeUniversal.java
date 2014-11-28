/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

import java.util.Map;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintBehavior;
import de.sudoq.model.sudoku.ConstraintType;

public abstract class TypeUniversal extends TypeBasic {

	/**
	 * Konstruktor zum weitervererben
	 * 
	 * @param length
	 *            die maximale Ausdehnung des Sudokutyps
	 * @param numberOfSymbols
	 *            die maximale Anzahl an Symbolen
	 */
	public TypeUniversal(int length, int numberOfSymbols) {
		super(length, numberOfSymbols);
	}

	/**
	 * Instanziiert einen universellen SudokuTyp. Die Constraints des erzeugten Typs werden durch constraintPattern
	 * festgelegt: Sich nicht überschneidende Constraints gleichen behaviors werden in einem String durch jeweils
	 * gleiche chars gekennzeichnet. Achtung: '.' kennzeichnet die Zugehörigkeit zu keinem Constraint. '\' ist der
	 * Escape-char von Java und sollte nicht verwendet werden. Bsp. für 4x4 Sudoku: pattern1= "aaaa" +"bbbb" +"cccc"
	 * +"dddd"
	 * 
	 * pattern2= "abcd" <- chars wiederverwenden geht wird aber +"abcd" nicht empfohlen, da nun in +"abcd" 8 Constraints
	 * je 2 den gleichennamen haben(blöd beim Testen). +"abcd"
	 * 
	 * pattern3= "eeff" +"eeff" +"gg.." +"gg.."
	 * 
	 * pattern4= "...." +"...." +"..hh" +"..hh"
	 * 
	 * Wann immer möglich sollte eine mathematische Zuordnung der Constraints den Pattern vorgezogen werden.
	 * 
	 * @param lenght
	 *            Länge des Sudokus
	 * @param numberOfSymbols
	 *            Anzahl vorkommender Symbole
	 * @param constraintPatterns
	 *            Map mit Constraintpattern und zugehörigem behavior.
	 * 
	 */
	public TypeUniversal(int lenght, int numberOfSymbols, Map<String, ConstraintBehavior> constraintPatterns) {
		super(lenght, numberOfSymbols);

		if (!validCollection(constraintPatterns))
			throw new IllegalArgumentException("constraintPatterns not valid");

		for (Map.Entry<String, ConstraintBehavior> e : constraintPatterns.entrySet()) {
			addBlockConstraints(e.getKey(), e.getValue());
		}
	}

	/**
	 * Setzt ein constraintPattern mit dediziertem behavior in Constraints um.
	 * 
	 * @param constraintPattern
	 *            ein ConstraintPattern
	 * @param constraintBehavior
	 *            das zugehörige Behavior
	 */
	protected void addBlockConstraints(String constraintPattern, ConstraintBehavior constraintBehavior) {
		if (constraintPattern == null || constraintBehavior == null)
			throw new IllegalArgumentException();
		if (constraintPattern.length() != dimensions.getX() * dimensions.getX())
			throw new IllegalArgumentException("arg constraintPattern has wrong size.");

		for (char s : extractSymbols(constraintPattern)) {// für jeden Constraint
			if (s != '.') {
				addExtraConstraint(constraintPattern, s, constraintBehavior);
			}
		}
	}

	/**
	 * extrahiert alle im String vorkommenden Symbole und gibt sie als char-Array zurück.
	 */
	private char[] extractSymbols(String constraintPattern) {

		String symbols = "";
		for (char c : constraintPattern.toCharArray())
			if (!symbols.contains("" + c))
				symbols += c;
		return symbols.toCharArray();
	}

	/**
	 * Erzeugt einen neuen Constraint aus einem String.
	 * 
	 * @param constraintPattern
	 *            Ein String der die Information über die Positionen des Constraints enthält.
	 * @param signChar
	 *            char der die Zugehörigkeit zum Constraint kennzeichnet. Alle anderen werden igroriert.
	 * @param constraintBehavior
	 *            das ConstraintBehavior des neuen Constraints.
	 * @throws IllegalArgumentException
	 *             wenn das constraintPattern nicht die richtige Länge hat oder constraintBehavior null ist.
	 */
	protected void addExtraConstraint(String constraintPattern, char signChar, ConstraintBehavior constraintBehavior) {
		if (constraintPattern == null || constraintBehavior == null)
			throw new IllegalArgumentException();
		if (constraintPattern.length() != dimensions.getX() * dimensions.getX())
			throw new IllegalArgumentException("arg constraintPattern has wrong size.");

		Constraint c = new Constraint(constraintBehavior, ConstraintType.BLOCK, "Block " + signChar);

		for (int i = 0; i < constraintPattern.length(); i++) {// Positionen
																// suchen
			if (constraintPattern.charAt(i) == signChar) {
				c.addPosition(positionsGrid[i % positionsGrid.length][i / positionsGrid.length]);
			}
		}
		constraints.add(c);
	}

	private boolean validCollection(Map<String, ConstraintBehavior> col) {

		if (col == null)
			return false;

		for (Map.Entry<String, ConstraintBehavior> e : col.entrySet()) {
			if (e.getKey() == null || e.getKey().length() != Math.pow(length + 0.0, 2.0) || e.getValue() == null)
				return false;
		}

		return true;
	}
}