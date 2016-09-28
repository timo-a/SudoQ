/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

/**
 * Diese Klasse repräsentiert ein Attribut eines Xml Baumes
 * 
 * @see XmlTree
 */
public class XmlAttribute {

	/** Attributes */

	/**
	 * Name des Attributes
	 */
	private String name;

	/**
	 * Wert des Xml Attributes
	 */
	private String value;

	/** Constructors */

	/**
	 * Dieser Konstruktor initialisiert ein neues Attribut mit gegebenem Wert.
	 * 
	 * @param name
	 *            Name des Attributes
	 * @param value
	 *            Wert des Attributes
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls einer der übergebenen Strings null oder
	 *             der name leer ist
	 */
	public XmlAttribute(String name, String value) throws IllegalArgumentException {
		if (name == null || name.equals("") || value == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.value = value;
	}

	public XmlAttribute(String name, Boolean value) throws IllegalArgumentException {
		this(name, ""+value);
	}



	/** Methods */

	/**
	 * Diese Methode gibt des Namen des Attributes zurück.
	 * 
	 * @return String Name des Attributes
	 */
	public String getName() {
		return name;
	}

	/**
	 * Diese Methode gibt den Wert des Attributes zurück.
	 * 
	 * @return String Wert des Attributes
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setzt falls der Parameter nicht null ist value auf diesen
	 * 
	 * @param value
	 *            der einzutragende Wert
	 */
	protected void setValue(String value) {
		if (value != null) {
			this.value = value;
		}
	}

	/**
	 * Diese Methode prüft ob ein weiteres Attribut des selben Typs ist, also
	 * den gleichen Namen tragen
	 * 
	 * @param attribute
	 *            Das zu vergleichende Attribut
	 * @return True, wenn beide den selben Namen haben, sonst false
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene Attribut null ist
	 */
	public boolean isSameAttribute(XmlAttribute attribute) throws IllegalArgumentException {
		if (attribute == null) {
			throw new IllegalArgumentException();
		}
		return attribute.getName().equals(name);
	}
}
