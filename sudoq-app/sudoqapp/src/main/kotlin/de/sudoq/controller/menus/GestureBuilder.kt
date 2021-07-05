/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.menus

import android.gesture.Gesture
import android.gesture.GestureOverlayView
import android.gesture.GestureOverlayView.OnGesturePerformedListener
import android.gesture.GestureStore
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import de.sudoq.R
import de.sudoq.controller.SudoqCompatActivity
import de.sudoq.controller.sudoku.InputListener
import de.sudoq.controller.sudoku.Symbol
import de.sudoq.controller.sudoku.Symbol.Companion.createSymbol
import de.sudoq.controller.sudoku.Symbol.Companion.getInstance
import de.sudoq.controller.sudoku.board.CellViewPainter.Companion.instance
import de.sudoq.controller.sudoku.board.CellViewStates
import de.sudoq.model.profile.ProfileManager
import de.sudoq.view.VirtualKeyboardLayout
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Der GestureBuilder gestattet es dem Benutzer selber Gesten für die Benutzung im Spiel zu definieren, das ermöglicht eine wesentlich höhere Erkennungsrate als mitgelieferte Gesten.
 * @author Anrion
 */
class GestureBuilder : SudoqCompatActivity(), OnGesturePerformedListener, InputListener {

    /**
     * Fängt Gesteneingaben des Benutzers ab
     */
    private var gestureOverlay: GestureOverlayView? = null

    /**
     * Hält die von der Activity unterstützten Gesten
     */
    private val gestureStore = GestureStore()

    /**
     * Flag für das Löschen von einer Gesten.
     */
    private var deleteSpecific = false

    /**
     * Der aktuelle Symbolsatz, für den Gesten eingetragen werden können.
     */
    private val currentSymbolSet = Symbol.MAPPING_NUMBERS_HEX_LETTERS

    /**
     * Das aktuell ausgewählte Symbol, dass der GestureLibrary hinzugefügt werden soll.
     */
    private var currentSelectedSymbol: String? = null

    /**
     * Virtuelles Keyboard, zum Auswählen des Symbols, für das eine Geste angelegt werden soll.
     */
    private var virtualKeyboard: VirtualKeyboardLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gesturebuilder)
        createSymbol(currentSymbolSet)
        virtualKeyboard =
            findViewById<View>(R.id.gesture_builder_virtual_keyboard) as VirtualKeyboardLayout
        inflateGestures()
        refreshKeyboard()
    }

    override fun onResume() {
        refreshKeyboard()
        super.onResume()
    }

    /**
     * Erzeugt die View für die Gesteneingabe
     */
    private fun inflateGestures() {
        val pm = ProfileManager(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        val gestureFile = pm.getCurrentGestureFile()
        try {
            gestureStore.load(FileInputStream(gestureFile))
        } catch (e: FileNotFoundException) {
            try {
                gestureFile.createNewFile()
            } catch (ioe: IOException) {
                Log.w(LOG_TAG, "Gesture file cannot be loaded!")
            }
        } catch (e: IOException) {
            check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
            pm.loadCurrentProfile()
            pm.isGestureActive = false
            Toast.makeText(this, R.string.error_gestures_no_library, Toast.LENGTH_SHORT).show()
        }
        gestureOverlay = GestureOverlayView(this)
        gestureOverlay!!.addOnGesturePerformedListener(this)
        val gestureLayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        gestureOverlay!!.layoutParams = gestureLayoutParams
        gestureOverlay!!.setBackgroundColor(Color.BLACK)
        gestureOverlay!!.background.alpha = 127
        gestureOverlay!!.visibility = View.INVISIBLE
        gestureOverlay!!.gestureStrokeType = GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE
        val frameLayout = findViewById<View>(R.id.gesture_builder_layout) as FrameLayout
        frameLayout.addView(gestureOverlay)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar_gesture_builder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all_gestures -> {
                val gestures = gestureStore.gestureEntries
                val gestureIterator = gestures.iterator()
                while (gestureIterator.hasNext()) {
                    val gestureName = gestureIterator.next() as String
                    gestureIterator.remove()
                }
                saveGestures()
                refreshKeyboard()
            }
            R.id.action_delete_single_gesture -> deleteSpecific = true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Speichert den aktuellen Satz an Gesten om Profile Ordner des aktuellen Benutzers
     */
    private fun saveGestures() {
        val pm = ProfileManager(getDir(getString(R.string.path_rel_profiles), MODE_PRIVATE))
        val gestureFile = pm.getCurrentGestureFile()
        try {
            gestureStore.save(FileOutputStream(gestureFile))
        } catch (e: IOException) {
            check(!pm.noProfiles()) { "there are no profiles. this is  unexpected. they should be initialized in splashActivity" }
            pm.loadCurrentProfile()
            pm.isGestureActive = false
            Toast.makeText(this, R.string.error_gestures_no_library, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Wurde eine Geste akzeptiert, wird diese im GestureStore gespeichert. (Das ist noch nicht persistent)
     */
    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {
        gestureStore.addGesture(currentSelectedSymbol, gesture)
        saveGestures()
        gestureOverlay!!.visibility = View.INVISIBLE
        refreshKeyboard()
    }

    /**
     * Markiert die Tasten der Tastatur so, dass alle Tasten mit Symbolen, für die bereits Gesten existieren Gelb markiert sind.
     */
    private fun markAlreadyCapturedSymbols() {
        val gestures = gestureStore.gestureEntries
        for (sym in currentSymbolSet)
            if (gestures.contains(sym))
                virtualKeyboard!!.markCell(
                    getInstance().getAbstract(sym),
                    CellViewStates.SELECTED_NOTE
                )
    }

    /**
     * Aktuallisiert die Tastatur.
     */
    private fun refreshKeyboard() {
        instance!!.setMarking(virtualKeyboard!!, CellViewStates.KEYBOARD)
        createSymbol(currentSymbolSet)
        virtualKeyboard!!.refresh(currentSymbolSet.size)
        virtualKeyboard!!.isActivated = true
        virtualKeyboard!!.enableAllButtons()
        markAlreadyCapturedSymbols()
        virtualKeyboard!!.registerListener(this)
    }

    override fun onBackPressed() {
        if (gestureOverlay!!.isShown) {
            gestureOverlay!!.visibility = View.INVISIBLE
        } else {
            saveGestures()
            super.onBackPressed()
        }
    }

    /**
     * Wird auf eine Taste gedrückt, wird die Prozedur zum eingeben von Gesten gestartet.
     *
     * @param symbol Das ausgewählte Symbol
     */
    override fun onInput(symbol: Int) {
        val gestures = gestureStore.gestureEntries
        if (deleteSpecific) {
            if (gestures.contains(getInstance().getMapping(symbol))) {
                gestureStore.removeEntry(getInstance().getMapping(symbol))
                refreshKeyboard()
            }
            saveGestures()
            deleteSpecific = false
        } else {
            currentSelectedSymbol = getInstance().getMapping(symbol)
            val textView = TextView(gestureOverlay!!.context)
            textView.setTextColor(Color.YELLOW)
            textView.text =
                " " + gestureOverlay!!.context.getString(R.string.gesture_builder_define_gesture) + " "
            textView.textSize = 18f
            gestureOverlay!!.addView(
                textView,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL
                )
            )
            gestureOverlay!!.visibility = View.VISIBLE
        }
    }

    companion object {
        private val LOG_TAG = GestureBuilder::class.java.simpleName
    }
}