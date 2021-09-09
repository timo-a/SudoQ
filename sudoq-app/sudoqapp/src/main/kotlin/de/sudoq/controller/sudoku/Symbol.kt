/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Klasse zum Mapping der intern genutzen Zahlen auf darstellbare Zeichen
 */
class Symbol private constructor(mapping: Array<String>) : Iterable<Int> {
    /**
     * Der aktuell verwendete Satz von Symbolen.
     */
    var symbolSet: Array<String>?
        private set

    /**
     * Diese Methode gibt das gemappte Symbol zurueck, das von der View
     * gezeichnet wird.
     *
     * @param abstractSymbol
     * die interne id/Repräsentation des Symbols
     * @return Das zu zeichnende Symbol.
     */
    fun getMapping(abstractSymbol: Int): String {
        return if (abstractSymbol != -1 && abstractSymbol < symbolSet!!.size) symbolSet!![abstractSymbol] else " "
    }

    /**
     * Gibt die Representation eines gegebenen Symbols als Ganzahl zurueck.
     *
     * @param symbol
     * Das Symbol als String.
     * @return Die Ganzahlrepresentation des gegebenen Symbols.
     */
    fun getAbstract(symbol: String): Int {
        var match = -1
        for (i in symbolSet!!.indices) {
            if (symbolSet!![i] == symbol) {
                match = i
            }
        }
        return match
    }

    /**
     * Gibt die Anzahl der Symbole in diesem Mapping zurueck.
     *
     * @return die Anzahl der Symbole.
     */
    fun getNumberOfSymbols(): Int {
        return symbolSet!!.size
    }

    /**
     * Gibt die Größe eines Feldes des Rasters für die Notizen innerhalb eines
     * Feldes zurück.
     *
     * @return Die Feldgröße des Notizrasters.
     */
    fun getRasterSize(): Int {
        return if (symbolSet != null) {
            ceil(sqrt(symbolSet!!.size.toDouble())).toInt()
        } else {
            throw IllegalStateException("No symbol set! Symbol not instanciated!")
        }
    }

    /// for iterating over symbols
    override fun iterator(): MutableIterator<Int> {
        return object : MutableIterator<Int> {
            val N = symbolSet!!.size
            var counter = 0
            override fun hasNext(): Boolean {
                return counter < N
            }

            override fun next(): Int {
                return counter++
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    companion object {
        /**
         * Die Standardsymbole für 1-4 Sudokus todo move from array to list
         */
        val MAPPING_NUMBERS_FOUR = arrayOf("1", "2", "3", "4")

        /**
         * Die Standardsymbole für 1-4 Sudokus
         */
        val MAPPING_NUMBERS_SIX = arrayOf("1", "2", "3", "4", "5", "6")

        /**
         * Die Standardsymbole für 1-9 Sudokus
         */
        val MAPPING_NUMBERS_NINE = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9")

        /**
         * Die Standardsymbole für 1-16 Sudokus
         */
        @JvmField
        val MAPPING_NUMBERS_HEX_LETTERS =
            arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G")
        val MAPPING_NUMBERS_HEX_DIGITS = (1..16).map { it.toString() }.toTypedArray()

        /**
         * Die statische Instanz dieses Singletons.
         */
        private var instance: Symbol? = null

        /**
         * Gibt die Instanz des Symbols zurück.
         *
         * @return Die Instanz des Symbols.
         * @throws IllegalArgumentException
         * wenn das Symbol noch nicht initialisiert wurde.
         */
        @JvmStatic
        fun getInstance(): Symbol {
            return instance ?: throw IllegalStateException("Symbol not instanciated!")
        }

        /**
         * Erstellt eine neue Instanz des Symbols und setzt den zu verwendenden
         * Symbolsatz.
         *
         * @param mapping
         * Den Symbolsatz, der verwendet werden soll. Einige sind als
         * statische Felder im Symbol verfügbar.
         */
        @JvmStatic
        fun createSymbol(mapping: Array<String>) {
            instance = Symbol(mapping)
        }
    }

    /**
     * Setzt ein neues Mapping für die Symbole
     *
     * @param mapping
     * Das zu setzende Mapping
     */
    init {
        symbolSet = mapping
    }
}