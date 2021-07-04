/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.complexity;

/**
 * Diese statische Klassen bietet Methoden zum erzeugen von
 * ComplexityConstraints entsprechend einer vorgegebenen Schwierigkeit.
 */
public interface ComplexityFactory {
    /* Methods */
	
    /**
     * Diese Methode erzeugt ein ComplexityConstraint, welches Vorgaben für ein Sudokus des spezifizierten Schwierigkeites enthält.
     * Ist die spezifizierten Complexity ungültig oder ist kein Constraint dafür definiert, so wird null zurückgegeben.
     *
     * @param complexity Die Schwierigkeit für die ein ComplexityConstraint-Objekt erzeugt werden soll
     * @return Ein ComplexityConstraint-Objekt für die spezifizierten Schwierigkeit oder null, falls diese ungültig ist
     */
    ComplexityConstraint buildComplexityConstraint(Complexity complexity);
    
    /**
     * Gibt den Faktor zurück, der die Anzahl der Felder angibt, welche mit dem das Sudoku durch den Generierungsalgorithmus gut zufällig belegt und
     * gleichzeitig validiert werden kann.
     * 
     * @return Der Standardfaktor für die Felderbelegung dieses Sudokutyps
     */
    float getStandardAllocationFactor();
}
