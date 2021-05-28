/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku.hints

import android.content.Context
import android.view.View
import de.sudoq.model.solverGenerator.solution.*
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.view.Hints.*
import de.sudoq.view.SudokuLayout
import java.util.*

/**
 * Manages all hintrelated views
 */
class HintPainter(sl: SudokuLayout) {
    var viewList: Vector<View>
    private val context: Context
    private val sl: SudokuLayout
    fun realizeHint(sd: SolveDerivation) {
        var v: View? = null
        when (sd.type) {
            HintTypes.LastDigit -> v = LastDigitView(context, sl, sd)
            HintTypes.LastCandidate -> v = LastCandidateView(context, sl, sd)
            HintTypes.LeftoverNote -> v = LeftoverNoteView(context, sl, (sd as LeftoverNoteDerivation))
            HintTypes.NakedSingle, HintTypes.NakedPair, HintTypes.NakedTriple, HintTypes.NakedQuadruple, HintTypes.NakedQuintuple -> v = NakedSetView(context, sl, (sd as NakedSetDerivation))
            HintTypes.HiddenSingle, HintTypes.HiddenPair, HintTypes.HiddenTriple, HintTypes.HiddenQuadruple, HintTypes.HiddenQuintuple -> v = HiddenSetView(context, sl, (sd as HiddenSetDerivation))
            HintTypes.LockedCandidatesExternal -> v = LockedCandidatesView(context, sl, (sd as LockedCandidatesDerivation))
            HintTypes.XWing -> v = XWingView(context, sl, (sd as XWingDerivation))
            HintTypes.NoNotes -> v = NoNotesView(context, sl, (sd as NoNotesDerivation))
        }
        if (v != null) {
            viewList.add(v)
            sl.addView(v, sl.width, sl.height)
        }
    }

    /** Methods  */
    fun invalidateAll() {
        for (v in viewList) v.invalidate()
    }

    /**
     * Delete all hints from object-internal storage as well as from the SudokuLayout
     */
    fun deleteAll() {
        for (v in viewList) sl.removeView(v)
        viewList.clear()
    }

    /**
     * Have this Object update all its views layouts from the changed sudokulayout object
     */
    fun updateLayout() {
        val newHeight = sl.height
        val newWidth = sl.width
        for (v in viewList) {
            v.layoutParams.height = newHeight
            v.layoutParams.width = newWidth
        }
    }

    companion object {
        /** Attributes  */
        private val LOG_TAG = HintPainter::class.java.simpleName
    }

    /** Constructors  */
    init {
        viewList = Vector()
        context = sl.context
        this.sl = sl
    }
}