/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityFactory;

/**
 * Ein SudokuType repräsentiert die Eigenschaften eines spezifischen Sudoku-Typs. Dazu gehören insbesondere die
 * Constraints, die einen Sudoku-Typ beschreiben.
 */
public abstract class TypeBasic implements Iterable<Constraint>, ComplexityFactory {
	/** Attributes */

	/**
	 * Ein Positionobjekt das in seinen Koordinaten die Anzahl an Spalten und Zeilen hält
	 */
	protected Position dimensions;

	/**
	 * Ein Positionobjekt das in seinen Koordinaten die Anzahl an Spalten und Zeilen in einem Block hält
	 */
	protected Position blockDimensions;

	/**
	 * Die Anzahl von Symbolen die in die Felder eines Sudokus dieses Typs eingetragen werden können.
	 */
	private int numberOfSymbols;

	/**
	 * Die Liste der Constraints, die diesen Sudoku-Typ beschreiben
	 */
	protected List<Constraint> constraints;

	/**
	 * Ein Positionarray das alle Positionen hält
	 */
	protected Position[][] positionsGrid;

	/**
	 * die maximale Kantenlänge des Sudokus
	 */
	protected int length;

	/**
	 * eine List die zulässige Transformationen am Sudokutyp hält
	 */
	protected List<PermutationProperties> permutationProperties;

	/**
	 * Konstruktor für einen SudokuTyp
	 * 
	 * @param length
	 *            die maximale Kantenlänge des SudokuTyps
	 * @param numberOfSymbols
	 *            die Anzahl an Symbolen die dieses Sudoku verwendet
	 */
	public TypeBasic(int length, int numberOfSymbols) {
		if (numberOfSymbols < 0)
			throw new IllegalArgumentException("Number of symbols < 0 : " + numberOfSymbols);
		if (length < 0)
			throw new IllegalArgumentException("Sudoku length < 0 : " + length);
		this.length = length;
		this.numberOfSymbols = numberOfSymbols;
		dimensions = Position.get(length, length);
		constraints = new ArrayList<Constraint>();
		positionsGrid = new Position[length][length];
		// füllen
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				positionsGrid[i][j] = Position.get(i, j);
			}
		}

		permutationProperties = new ArrayList<PermutationProperties>();
	}

	/** Methods */

	/**
	 * Gibt die Größe eines Sudokus dieses Typs zurück. Die Größe wird durch ein Position-Objekt repräsentiert, wobei
	 * die x-Koordinate die maximale Anzahl horizontaler Felder eines Sudokus dieses Typs beschreibt, die y-Koordinaten
	 * die maximale Anzahl vertikaler Felder.
	 * 
	 * @return Ein Position-Objekt, welches die maximale Anzahl horizontaler bzw. vertikaler Felder eines Sudokus dieses
	 *         Typs beschreibt
	 */
	public Position getSize() {
		return dimensions;
	}

	/**
	 * Gibt die Größe des oberen linken Blocks eines Sudokus dieses Typs zurück. Die Größe wird durch ein
	 * Position-Objekt repräsentiert, wobei die x-Koordinate die maximale Anzahl horizontaler Felder eines Sudoku-Blocks
	 * dieses Typs beschreibt, die y-Koordinaten die maximale Anzahl vertikaler Felder.
	 * 
	 * @return Ein Position-Objekt, welches die maximale Anzahl horizontaler bzw. vertikaler Felder des oberen linken
	 *         Blocks eines Sudokus dieses Typs beschreibt
	 */
	public Position getBlockSize() {
		return blockDimensions;
	}

	/**
	 * Gibt eine Liste mit zulässigen Transformationen an diesem Sudoku aus.
	 * 
	 * @return eine Liste mit zulässigen Transformationen an diesem Sudoku
	 */
	public List<PermutationProperties> getPermutationProperties() {
		return permutationProperties;
	}

	/**
	 * Überprüft, ob das spezifizierte Sudoku die Vorgaben aller Constraints dieses SudokuTyps erfüllt. Ist dies der
	 * Fall, so wir true zurückgegeben. Erfüllt es die Vorgaben nicht, oder wird null übergeben, so wird false
	 * zurückgegeben.
	 * 
	 * @param sudoku
	 *            Das Sudoku, welches auf Erfüllung der Constraints überprüft werden soll
	 * @return true, falls das Sudoku alle Constraints erfüllt, false falls es dies nicht tut oder null ist
	 */
	public boolean checkSudoku(Sudoku sudoku) {
		if (sudoku == null)
			return false;

		boolean allSaturated = true;

		for (int i = 0; i < this.constraints.size(); i++) {
			if (!this.constraints.get(i).isSaturated(sudoku))
				allSaturated = false;
		}

		return allSaturated;
	}

	/**
	 * Gibt einen Iterator für die Constraints dieses Sudoku-Typs zurück.
	 * 
	 * @return Einen Iterator für die Constraints dieses Sudoku-Typs
	 */
	public Iterator<Constraint> iterator() {
		return constraints.iterator();
	}

	/**
	 * Gibt die Anzahl der Symbole eines Sudokus dieses Typs zurück.
	 * 
	 * @return Die Anzahl der Symbole in einem Sudoku dieses Typs
	 */
	public int getNumberOfSymbols() {
		return this.numberOfSymbols;
	}

	/**
	 * Gibt den Sudoku-Typ als Enum zurück.
	 * 
	 * @return Sudoku-Typ als Enum
	 */
	public abstract SudokuTypes getEnumType();

	/**
	 * Gibt einen ComplexityContraint für eine Schwierigkeit complexity zurück.
	 * 
	 * @param complexity
	 *            eine Schwierigkeit zu der ein ComplexityConstraint erzeugt werden soll
	 */
	public abstract ComplexityConstraint buildComplexityConstraint(Complexity complexity);

	/**
	 * Gibt den Standard Belegungsfaktor zurück
	 */
	public abstract float getStandardAllocationFactor();

	/**
	 * Gibt den Sudoku-Typ als String zurück.
	 * 
	 * @return Sudoku-Typ als String
	 */
	public String toString() {
		return getEnumType().toString();
	}

	/**
	 * Gibt eine Liste der Constraints, welche zu diesem Sudokutyp gehören zurück. Hinweis: Wenn möglich stattdessen den
	 * Iterator benutzen.
	 * 
	 * @return Eine Liste der Constraints dieses Sudokutyps.
	 */
	public ArrayList<Constraint> getConstraints() {
		return (ArrayList<Constraint>) this.constraints;
	}
}