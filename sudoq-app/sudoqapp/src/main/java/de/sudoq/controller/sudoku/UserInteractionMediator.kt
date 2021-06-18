/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import android.content.Context
import android.gesture.Gesture
import android.gesture.GestureOverlayView
import android.gesture.GestureOverlayView.OnGesturePerformedListener
import android.gesture.GestureStore
import android.gesture.Prediction
import android.util.Log
import android.view.*
import android.widget.Toast
import de.sudoq.R
import de.sudoq.controller.sudoku.CellInteractionListener.SelectEvent
import de.sudoq.controller.sudoku.board.CellViewStates
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.Game
import de.sudoq.model.profile.Profile
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.view.GestureInputOverlay
import de.sudoq.view.SudokuCellView
import de.sudoq.view.SudokuLayout
import de.sudoq.view.VirtualKeyboardLayout
import java.util.*

/**
 * Ein Vermittler zwischen einem Sudoku und den verschiedenen
 * Eingabemöglichkeiten, also insbesondere Tastatur und Gesten-View.
 */
class UserInteractionMediator(
    virtualKeyboard: VirtualKeyboardLayout,
    sudokuView: SudokuLayout?,
    game: Game?,
    gestureOverlay: GestureInputOverlay?,
    gestureStore: GestureStore
) : OnGesturePerformedListener, InputListener, CellInteractionListener, ObservableActionCaster {
    /**
     * Flag für den Notizmodus.
     */
    private var noteMode = false

    /**
     * Die SudokuView, die die Anzeige eines Sudokus mit seinen Feldern
     * übernimmt.
     */
    private val sudokuView: SudokuLayout?

    /**
     * Virtuelles Keyboard, welches beim Antippen eines Feldes angezeigt wird.
     */
    private val virtualKeyboard: VirtualKeyboardLayout

    /**
     * Das aktuelle Spiel.
     */
    private val game: Game?

    /**
     * Eine Liste der ActionListener.
     */
    private val actionListener: MutableList<ActionListener?>

    /**
     * Die Gesten-View.
     */
    private val gestureOverlay: GestureInputOverlay?

    /**
     * Die Bibliothek für die Gesteneingabe.
     */
    private val gestureStore: GestureStore
    override fun onInput(symbol: Int) {
        val currentField = sudokuView!!.currentCellView
        for (listener in actionListener) {
            if (noteMode) {
                if (currentField!!.cell.isNoteSet(symbol)) {
                    listener!!.onNoteDelete(currentField.cell, symbol)
                    restrictCandidates() //because github issue #116 see below
                    //in case we deleted a now impossible,
                    // we immediately restrict so it cant be selected again
                } else {
                    listener!!.onNoteAdd(currentField.cell, symbol)
                }
            } else {
                if (symbol == currentField!!.cell.currentValue) {
                    listener!!.onDeleteEntry(currentField.cell)
                } else {
                    listener!!.onAddEntry(currentField.cell, symbol)
                }
            }
        }
        updateKeyboard()
    }

    override fun onCellSelected(view: SudokuCellView, e: SelectEvent) {
        if (!game!!.isFinished()) {
            val c = view.context
            val p = Profile.getInstance(
                c.getDir(
                    c.getString(R.string.path_rel_profiles),
                    Context.MODE_PRIVATE
                )
            )
            if (p.isGestureActive) {
                cellSelectedGestureMode(view, e)
            } else {
                cellSelectedNumPadMode(view, e)
            }
        }
        updateKeyboard()
    }

    private fun cellSelectedNumPadMode(view: SudokuCellView, e: SelectEvent) {
        var currentField = sudokuView!!.currentCellView
        val freshlySelected = currentField != view
        if (freshlySelected) {
            noteMode = e == SelectEvent.Long
            sudokuView.currentCellView = view

            //unpdate currentField
            currentField?.deselect(true)
            currentField = view
            currentField.setNoteState(noteMode)
            currentField.select(game!!.isAssistanceAvailable(Assistances.markRowColumn))
        } else {
            noteMode = !noteMode
            currentField!!.setNoteState(noteMode)
        }
        if (currentField.cell.isEditable) {
            restrictCandidates()
            virtualKeyboard.isActivated = true
        } else {
            virtualKeyboard.isActivated = false
        }
    }

    private fun cellSelectedGestureMode(view: SudokuCellView?, e: SelectEvent) {
        var currentCellView = sudokuView!!.currentCellView
        val currentCell: Cell
        /* select for the first time -> set a solution */
        val freshlySelected = currentCellView != view
        if (freshlySelected) {
            Log.d("gesture-verify", "cellSelectedGestureMode: freshly selected")
            noteMode = e == SelectEvent.Long
            Log.d("gesture-verify", "cellSelectedGestureMode: this.noteMode =" + noteMode)
            sudokuView.currentCellView = view
            currentCellView?.deselect(true)
            currentCellView = view
            currentCellView?.setNoteState(noteMode)
            currentCellView!!.select(game!!.isAssistanceAvailable(Assistances.markRowColumn))
            currentCell = currentCellView.cell
            if (currentCell.isEditable) {
                restrictCandidates()
                virtualKeyboard.isActivated = true
            } else {
                virtualKeyboard.isActivated = false
            }
            if (e == SelectEvent.Long && currentCell.isEditable) {
                //Long press -> user can input note directly
                Log.d("gesture-verify", "cellSelectedGestureMode: noteMode: $noteMode")
                restrictCandidates()
                if (noteMode) gestureOverlay!!.activateForNote() else gestureOverlay!!.activateForEntry()
            }
            /* second click on the same cell*/
        } else {
            currentCell = currentCellView!!.cell
            /* set solution via touchy swypy*/
            if (currentCell.isEditable) {

                //long press switches between selected for note / entry
                if (e == SelectEvent.Long) {
                    noteMode = !noteMode
                    currentCellView.setNoteState(noteMode)
                    return
                }
                restrictCandidates()
                if (noteMode) gestureOverlay!!.activateForNote() else gestureOverlay!!.activateForEntry()
            } else {
                //if it is not editable don't do anything
                //this.noteMode = !this.noteMode;
                //restrictCandidates();
            }
        }
    }

    /**
     * Aktualisiert die Anzeige der Tastatur.
     */
    fun updateKeyboard() {
        val currentField = sudokuView!!.currentCellView
        for (i in game!!.sudoku!!.sudokuType!!.symbolIterator) {
            var state: CellViewStates
            state =
                if (currentField != null && i == currentField.cell.currentValue && !noteMode) CellViewStates.SELECTED_INPUT_BORDER else if (currentField != null && currentField.cell.isNoteSet(
                        i
                    ) && noteMode
                ) CellViewStates.SELECTED_NOTE_BORDER else CellViewStates.DEFAULT_BORDER
            virtualKeyboard.markCell(i, state)
        }
        virtualKeyboard.invalidate()
    }

    override fun notifyListener() {}
    override fun registerListener(listener: ActionListener) {
        actionListener.add(listener)
    }

    override fun removeListener(listener: ActionListener) {
        actionListener.remove(listener)
    }

    /**
     * Setzt den Zustand der Tastatur. Diese wird entsprechend (nicht)
     * angezeigt.
     *
     * @param activated
     * Gibt den zu setzenden Zustand an
     */
    fun setKeyboardState(activated: Boolean) {
        virtualKeyboard.isActivated = activated
    }

    override fun onCellChanged(view: SudokuCellView) {
        updateKeyboard()
    }

    /**
     * Wird aufgerufen, sobald der Benutzer eine Geste eingibt.
     *
     * @param overlay
     * GestureOverlay, auf welchem die Geste eingegeben wurde
     * @param gesture
     * Geste, die der Benutzer eingegeben hat
     * @throws IllegalArgumentException
     * Wird geworfen, falls eines der Argumente null ist
     */
    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {
        val predictions = gestureStore.recognize(gesture)
        if (predictions.isEmpty()) return  //no predictions made
        val prediction = predictions[0]
        if (prediction.score > 1.5) {
            for (listener in actionListener) {
                if (noteMode) updateNoteFromGesture(
                    listener,
                    prediction
                ) else updateEntryFromGesture(listener, prediction)
            }
        }
        overlay.removeAllViews()
    }

    private fun updateEntryFromGesture(listener: ActionListener?, prediction: Prediction) {
        val currentCell = sudokuView!!.currentCellView!!.cell
        val currentValue: String = Symbol.getInstance().getMapping(currentCell.currentValue)
        if (prediction.name == currentValue) {
            listener!!.onDeleteEntry(currentCell)
        } else {
            var number: Int = Symbol.getInstance().getAbstract(prediction.name)
            val save = currentCell.currentValue
            if (number >= game!!.sudoku!!.sudokuType!!.numberOfSymbols) number = -1
            if (number != -1 && game.isAssistanceAvailable(Assistances.restrictCandidates)) {
                currentCell.setCurrentValue(number, false)
                for (c in game.sudoku!!.sudokuType!!) {
                    if (!c.isSaturated(game.sudoku!!)) {
                        number = -2
                        break
                    }
                }
                currentCell.setCurrentValue(save, false)
            }
            if (number != -1 && number != -2) {
                listener!!.onAddEntry(currentCell, number)
                gestureOverlay!!.visibility = View.INVISIBLE
            } else if (number == -1) {
                Toast.makeText(
                    sudokuView.context,
                    sudokuView.context.getString(R.string.toast_invalid_symbol), Toast.LENGTH_SHORT
                ).show()
            } else if (number == -2) {
                Toast.makeText(
                    sudokuView.context,
                    sudokuView.context.getString(R.string.toast_restricted_symbol),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateNoteFromGesture(listener: ActionListener?, prediction: Prediction) {
        val currentCell = sudokuView!!.currentCellView!!.cell
        var predictedNote: Int = Symbol.getInstance().getAbstract(prediction.name)
        if (currentCell.isNoteSet(predictedNote)) {
            listener!!.onNoteDelete(currentCell, predictedNote)
        } else {
            val save = sudokuView.currentCellView!!.cell.currentValue
            if (predictedNote >= game!!.sudoku!!.sudokuType!!.numberOfSymbols) predictedNote = -1
            if (predictedNote != -1 && game.isAssistanceAvailable(Assistances.restrictCandidates)) {
                sudokuView.currentCellView!!.cell.setCurrentValue(predictedNote, false)
                for (c in game.sudoku!!.sudokuType!!) {
                    if (!c.isSaturated(game.sudoku!!)) {
                        predictedNote = -2
                        break
                    }
                }
                currentCell.toggleNote(predictedNote)
            }
            if (predictedNote != -1 && predictedNote != -2) {
                listener!!.onNoteAdd(currentCell, predictedNote)
                gestureOverlay!!.visibility = View.INVISIBLE
            } else if (predictedNote == -1) {
                Toast.makeText(
                    sudokuView.context,
                    sudokuView.context.getString(R.string.toast_invalid_symbol), Toast.LENGTH_SHORT
                ).show()
            } else if (predictedNote == -2) {
                Toast.makeText(
                    sudokuView.context,
                    sudokuView.context.getString(R.string.toast_restricted_symbol),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Schränkt die Kandidaten auf der Tastatur ein.
     */
    fun restrictCandidates() {
        virtualKeyboard.enableAllButtons()
        val currectFieldView = sudokuView!!.currentCellView ?: return
        //maybe there is no focus, then pass
        val currentCell = currectFieldView.cell
        val type = game!!.sudoku!!.sudokuType
        /* only if assistance 'input assistance' if enabled */if (game.isAssistanceAvailable(
                Assistances.restrictCandidates
            )
        ) {
            val allPossible = getRestrictedSymbolSet(game.sudoku, currentCell, noteMode)
            for (i in type!!.symbolIterator) if (!allPossible.contains(i)) virtualKeyboard.disableButton(
                i
            )
        }
    }

    /* compute the symbols that the keyboard offers if `input assistance`
          i.e. "grey out values that apprear in the same constraint" is selected.
       caution
          */
    @Synchronized
    private fun getRestrictedSymbolSet(
        s: Sudoku?, currentCell: Cell,
        noteMode: Boolean
    ): Set<Int> {
        val restrictedSet: MutableSet<Int> = HashSet()
        val type = s!!.sudokuType
        val relevantConstraints: MutableList<Constraint> = ArrayList()
        for (c in type!!) if (c.getPositions()
                .contains(s.getPosition(currentCell.id))
        ) relevantConstraints.add(c)

        /* save val of current view */
        val save = currentCell.currentValue

        /* iterate over all symbols e.g. 0-8 */for (i in type.symbolIterator) {

            /* set cellval to current symbol */
            currentCell.setCurrentValue(i, false)
            var possible = true
            /* for every constraint */for (c in relevantConstraints) {

                /* if constraint not satisfied -> disable */
                if (!c.isSaturated(s)) {
                    possible = false
                    break
                }
            }
            if (possible) restrictedSet.add(i)
            currentCell.setCurrentValue(Cell.EMPTYVAL, false) // unneccessary
        }
        currentCell.setCurrentValue(save, false)

        /* Github Issue #116
		 * it would be stupid if we were in the mode where notes are set
		 * and would disable a now impossible note that had been set by user.
		 * Because then, it can't be unset by the user */
        val setNotes: MutableSet<Int> = HashSet()
        if (noteMode) for (i in type.symbolIterator) if (currentCell.isNoteSet(i)) setNotes.add(i)
        restrictedSet.addAll(setNotes)
        return restrictedSet
    }

    /**
     * Instanziiert einen neuen UserInteractionMediator.
     *
     * @param virtualKeyboard
     * Das virtuelle Keyboard, auf dem der Benutzer Eingaben
     * vornehmen kann
     * @param sudokuView
     * Die View des Sudokus
     * @param game
     * Das aktuelle Spiel
     * @param gestureOverlay
     * Die Gesten-View auf der der Benutzer Gesten eingeben kann
     * @param gestureStore
     * Die Bibliothek der Gesten
     */
    init {
        actionListener = ArrayList()
        this.game = game
        this.sudokuView = sudokuView
        this.virtualKeyboard = virtualKeyboard
        this.virtualKeyboard.registerListener(this)
        this.gestureOverlay = gestureOverlay
        this.gestureStore = gestureStore
        this.gestureOverlay!!.addOnGesturePerformedListener(this)
        this.sudokuView!!.registerListener(this)
    }
}