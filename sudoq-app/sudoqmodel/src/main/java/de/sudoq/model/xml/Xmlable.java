/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

/**
 * Dieses Interface definiert eine Schnittstelle für Klassen, die durch XML
 * Dateien repräsentiert werden können müssen.
 */
public interface Xmlable {

	/* Methods */

	/**
	 * Erzeugt ein XmlTree Objekt, welches die, für eine Speicherrepräsentation
	 * notwendigen, Daten enthält.
	 * 
	 * @return XML-Baum-Repräsentation der implementierenden Klasse
	 */
    XmlTree toXmlTree();

	/**
	 * Lädt Daten aus einer XML Repräsentation der Klasse in die
	 * implementierende Klasse
	 * 
	 * @param xmlTreeRepresentation
	 *            Repräsentation der implementierenden Klasse.
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die Xml Repräsentation eine nicht
	 *             unterstützte Struktur aufweist.
	 */
    void fillFromXml(XmlTree xmlTreeRepresentation) throws IllegalArgumentException;

}
