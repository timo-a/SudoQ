/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.complexity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dieses enum repräsentiert die Schwierigkeit eines spezielles Sudokus, wobei
 * invalid keine Schwierigkeit im eigentlichen Sinne, sondern eine Kennung für
 * ein ungültiges Sudoku ist.
 */
public enum Complexity {
	/**
	 * Der einfachste Schwierigkeitsgrad für ein Sudokus: Es sind viele Felder
	 * vorgegeben und diese sind leicht zu lösen.
	 */
	easy,

	/**
	 * Der mittlere Schwierigkeitsgrad für ein Sudoku: Es sind weniger Felder
	 * vorgegeben und es dieses sind schwerer zu lösen als bei easy.
	 */
	medium,

	/**
	 * Der hohe Schwierigkeitsgrad für ein Sudoku: Es sind wenige Felder
	 * vorgegeben und diese sind nur mit komplizierten Techniken lösbar.
	 */
	difficult,

	/**
	 * Der höchste Schwierigkeitsgrad für ein Sudoku: Es sind sehr wenige Felder
	 * vorgegeben und diese sind nur mit den schwersten Techniken (auch mit
	 * Raten bzw. Backtracking) lösbar.
	 */
	infernal,

	/**
	 * Dies ist kein Schwierigkeitsgrad im eigentlichen Sinne, sondern ein
	 * Kennzeichen für ein ungültiges Sudoku.
	 */
	// invalid,

	/**
	 * Ein Dummy für beliebige Schwierigkeit. Es werden keine Anforderungen
	 * gestellt.
	 */
	arbitrary;
	
	public static Iterable<Complexity> playableValues(){
		
		List<Complexity> l = new ArrayList<>(Arrays.asList(values()));
		l.remove(arbitrary);
		return l;
	}
}
