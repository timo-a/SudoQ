/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.Hints

import android.content.Context
import android.graphics.Color
import android.view.View
import de.sudoq.model.solverGenerator.solution.LockedCandidatesDerivation
import de.sudoq.view.SudokuLayout

/**
 * Diese Subklasse des von der Android API bereitgestellten Views stellt ein
 * einzelnes Feld innerhalb eines Sudokus dar. Es erweitert den Android View um
 * Funktionalität zur Benutzerinteraktion und Färben.
 */
class LockedCandidatesView(context: Context, sl: SudokuLayout, d: LockedCandidatesDerivation) : HintView(context, sl, d) {
    /**
     * Erstellt einen SudokuFieldView und initialisiert die Attribute der
     * Klasse.
     *
     * @param context    der Applikationskontext
     * @throws IllegalArgumentException Wird geworfen, falls eines der Argumente null ist
     */
    init {
        val lockedConstraintV: View = HighlightedConstraintView(context, sl, d.lockedConstraint, Color.BLUE)
        highlightedObjects.add(lockedConstraintV)
        val reducibleConstraintV: View = HighlightedConstraintView(context, sl, d.reducibleConstraint, Color.GREEN)
        highlightedObjects.add(reducibleConstraintV)
    }
}