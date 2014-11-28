/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

import java.util.BitSet;

/**
 * Diese Klasse repräsentiert einen Satz von Assistances, also für jede
 * Assistance ob diese gesetzt ist oder nicht.
 * 
 */
public class AssistanceSet {
	/**
	 * Ein BitSet, welches die gesetzten bzw. nicht gesetzten Assistances
	 * repräsentiert
	 */
	private BitSet assistances;

	/**
	 * Instanziiert ein neues AssistanceSet in welchem alle Assistances
	 * deaktiviert sind.
	 */
	public AssistanceSet() {
		this.assistances = new BitSet();
	}

	/**
	 * Setzt die spezifizierte Hilfestellung in diesem AssistanceSet. Ist diese
	 * null, so wird nichts getan
	 * 
	 * @param assistance
	 *            Die Hilfestellung die gesetzt werden soll
	 */
	public void setAssistance(Assistances assistance) {
		if (assistance == null)
			return;

		this.assistances.set((int) Math.pow(2, assistance.ordinal() + 1));
	}

	/**
	 * Löscht die spezifizierte Hilfestellung in diesem AssistanceSet. Ist diese
	 * null, so wird nichts getan.
	 * 
	 * @param assistance
	 *            Die Hilfestellung die gelöscht werden soll
	 */
	public void clearAssistance(Assistances assistance) {
		if (assistance == null)
			return;

		this.assistances.clear((int) Math.pow(2, assistance.ordinal() + 1));
	}

	/**
	 * Gibt zurück, ob in diesem AssistanceSet die gegebene Hilfestellung
	 * gesetzt ist. Ist diese< ungültig, so wird false zurückgegeben.
	 * 
	 * @param assistance
	 *            Die Hilfestellung die abgefragt werden soll
	 * @return true, falls die Hilfestellung gesetzt ist, false falls nicht oder
	 *         falls das Assistance null ist
	 */
	public boolean getAssistance(Assistances assistance) {
		if (assistance == null)
			return false;

		return this.assistances.get((int) Math.pow(2, assistance.ordinal() + 1));
	}

	/**
	 * Generiert aus diesem AssistanceSet, einen String aus 0en und 1en. Dieser
	 * kann mittels der fromString-Methode wieder eingelesen werden.
	 * 
	 * @return String Repräsentation dieses AssistanceSets
	 */
	public String convertToString() {
		StringBuilder bitstring = new StringBuilder();
		for (Assistances assist : Assistances.values()) {
			if (getAssistance(assist)) {
				bitstring.append("1");
			} else {
				bitstring.append("0");
			}
		}
		return bitstring.toString();
	}

	/**
	 * Generiert aus einer String Repräsentation der aktivierten Hilfestellungen
	 * durch 0en und 1en ein AssistanceSet
	 * 
	 * @param representation
	 *            String Repräsentation der Hilfestellungen
	 * @return AssistanceSet, welches die Hilfestellungen repräsentiert
	 * @throws IllegalArgumentException
	 *             Wird geworfen, wenn aus dem angegebenen String kein
	 *             AssistanceSet generiert werden kann.
	 */
	public static AssistanceSet fromString(String representation) throws IllegalArgumentException {
		AssistanceSet set = new AssistanceSet();
		int i = 0;
		for (Assistances assist : Assistances.values()) {
			try {
				if (representation.charAt(i) == '1') {
					set.setAssistance(assist);
				}
			} catch (Exception exc) {
				throw new IllegalArgumentException();
			}
			i++;
		}
		return set;
	}

}
