/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

import java.util.BitSet;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Diese Klasse repräsentiert alle Einstellungen zum Spiel:
 * -einen Satz von Assistances, also für jede
 * Assistance ob diese gesetzt ist oder nicht.
 * -zusätzliche optionen, wie lefthandmode, hints...
 */
public class GameSettings implements Xmlable{
	/**
	 * Ein BitSet, welches die gesetzten bzw. nicht gesetzten Assistances
	 * repräsentiert
	 */
	private BitSet assistances;
	private boolean lefthandMode;
	private boolean helper;
	private boolean gestures;

	/**
	 * Instanziiert ein neues AssistanceSet in welchem alle Assistances
	 * deaktiviert sind.
	 */
	public GameSettings() {
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

	/* additional settings */
	public void setGestures(boolean value) {
		gestures = value;
	}
	
	public boolean isGesturesSet(){
		return gestures;
	}
	
	public void setLefthandMode(boolean value){
		this.lefthandMode = value;
	}
	
	public boolean isLefthandModeSet(){
		return lefthandMode;
	}
	
	public void setHelper(boolean value){
		this.helper = value;
	}
	
	public boolean isHelperSet(){
		return helper;
	}
	
	/* to and from string */
	
	@Override
	public XmlTree toXmlTree() {
        XmlTree representation = new XmlTree("gameSettings");
        representation.addAttribute(new XmlAttribute("assistances", this.convertAssistancesToString()));
        representation.addAttribute(new XmlAttribute("gestures",   "" + gestures));
        representation.addAttribute(new XmlAttribute("left",   "" + lefthandMode));
        representation.addAttribute(new XmlAttribute("helper", "" + helper));
		return representation;
	}

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation)
			throws IllegalArgumentException {
		
		AssistancesfromString(xmlTreeRepresentation.getAttributeValue("assistances"));
		gestures     = Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("gestures"));
		lefthandMode = Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("left"));
        helper       = Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("helper"));
	}
	
	
	
	
	/**
	 * Generiert aus dem AssistanceSet, einen String aus 0en und 1en. Dieser
	 * kann mittels der fromString-Methode wieder eingelesen werden.
	 * 
	 * @return String Repräsentation dieses AssistanceSets
	 */
	private String convertAssistancesToString() {
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
	 * Füllt aus einer String Repräsentation der aktivierten Hilfestellungen
	 * durch 0en und 1en das AssistanceSet
	 * 
	 * @param representation
	 *            String Repräsentation der Hilfestellungen
	 * @throws IllegalArgumentException
	 *             Wird geworfen, wenn aus dem angegebenen String kein
	 *             AssistanceSet generiert werden kann.
	 */
	private void AssistancesfromString(String representation) throws IllegalArgumentException {
		int i = 0;
		for (Assistances assist : Assistances.values()) {
			try {
				if (representation.charAt(i) == '1') {
					setAssistance(assist);
				}
			} catch (Exception exc) {
				throw new IllegalArgumentException();
			}
			i++;
		}
	}

}