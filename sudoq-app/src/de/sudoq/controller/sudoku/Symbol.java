/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

/**
 * Klasse zum Mapping der intern genutzen Zahlen auf darstellbare Zeichen
 */
public class Symbol {
	/**
	 * Die Standardsymbole für 1-4 Sudokus
	 */
	public static final String[] MAPPING_NUMBERS_FOUR = new String[] { "1", "2", "3", "4" };

	/**
	 * Die Standardsymbole für 1-4 Sudokus
	 */
	public static final String[] MAPPING_NUMBERS_SIX = new String[] { "1", "2", "3", "4", "5", "6" };
	
	/**
	 * Die Standardsymbole für 1-9 Sudokus
	 */
	public static final String[] MAPPING_NUMBERS_NINE = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
	
	/**
	 * Die Standardsymbole für 1-16 Sudokus
	 */
	public static final String[] MAPPING_NUMBERS_HEX_LETTERS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G" };
	public static final String[] MAPPING_NUMBERS_HEX_DIGGITS = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" };

	/**
	 * Die statische Instanz dieses Singletons.
	 */
	private static Symbol instance;

	/**
	 * Der aktuell verwendete Satz von Symbolen.
	 */
	protected String[] symbolSet;

	/**
	 * Setzt ein neues Mapping für die Symbole
	 * 
	 * @param mapping
	 *            Das zu setzende Mapping
	 */
	private Symbol(String[] mapping) {
		this.symbolSet = mapping;
	}

	/**
	 * Gibt die Instanz des Symbols zurück.
	 * 
	 * @return Die Instanz des Symbols.
	 * @throws IllegalArgumentException
	 *             wenn das Symbol noch nicht initialisiert wurde.
	 */
	public static Symbol getInstance() {
		if (instance != null) {
			return instance;
		} else {
			throw new IllegalStateException("Symbol not instanciated!");
		}
	}

	/**
	 * Erstellt eine neue Instanz des Symbols und setzt den zu verwendenden
	 * Symbolsatz.
	 * 
	 * @param mapping
	 *            Den Symbolsatz, der verwendet werden soll. Einige sind als
	 *            statische Felder im Symbol verfügbar.
	 */
	public static void createSymbol(String[] mapping) {
		instance = new Symbol(mapping);
	}

	/**
	 * Diese Methode gibt das gemappte Symbol zurueck, dass von der View
	 * gezeichnet wird.
	 * 
	 * @param abstractSymbol
	 *            die interne id/Repräsentation des Symbols
	 * @return Das zu zeichnende Symbol.
	 */
	public String getMapping(int abstractSymbol) {
		return (abstractSymbol != -1 && abstractSymbol < this.symbolSet.length) ? this.symbolSet[abstractSymbol] : " ";
	}

	/**
	 * Gibt die Representation eines gegebenen Symbols als Ganzahl zurueck.
	 * 
	 * @param symbol
	 *            Das Symbol als String.
	 * @return Die Ganzahlrepresentation des gegebenen Symbols.
	 */
	public int getAbstract(String symbol) {
		int match = -1;
		for (int i = 0; i < this.symbolSet.length; i++) {
			if (String.valueOf(this.symbolSet[i]).equals(symbol)) {
				match = i;
			}
		}
		return match;
	}

	/**
	 * Gibt die Anzahl der Symbole in diesem Mapping zurueck.
	 * 
	 * @return die Anzahl der Symbole.
	 */
	public int getNumberOfSymbols() {
		return symbolSet.length;
	}

	/**
	 * Gibt die Größe eines Feldes des Rasters für die Notizen innerhalb eines
	 * Feldes zurück.
	 * 
	 * @return Die Feldgröße des Notizrasters.
	 */
	public int getRasterSize() {
		if (this.symbolSet != null) {
			return (int) Math.ceil(Math.sqrt(this.symbolSet.length));
		} else {
			throw new IllegalStateException("No symbol set! Symbol not instanciated!");
		}
	}
}
