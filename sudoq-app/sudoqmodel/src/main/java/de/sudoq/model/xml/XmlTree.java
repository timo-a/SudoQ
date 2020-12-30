/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Diese Klasse repräsentiert eine zu Xml kompatible Baumstruktur
 */
public class XmlTree implements Iterable<XmlTree> {

	/* Attributes */

	/**
	 * Name des Xml Objektes
	 */
	private String name;

	/**
	 * Inhalt des Xml Objektes
	 */
	private String content;

	/**
	 * Anzahl der Unterobjekte des Xml Objektes
	 */
	private int numberOfChildren;

	/**
	 * Menge der Attribute dieses Xml Objekts
	 */
	private List<XmlAttribute> attributes;

	/**
	 * Menge der Unterobjekte dieses Xml Objekts
	 */
	private List<XmlTree> children;

	/* Constructors */

	/**
	 * Dieser Konstruktor initialisiert einen neuen Xml Baum Objekt mit
	 * gegebenem Namen und leerem Inhalt.
	 * 
	 * @param name
	 *            Name der Wurzel Xml Baumes
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene String null oder leer ist
	 */
	public XmlTree(String name) throws IllegalArgumentException {
		if (name == null || name.equals("")) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.content = "";
		this.attributes = new ArrayList<>();
		this.children = new ArrayList<>();
		this.numberOfChildren = 0;
	}

	/**@deprecated content can not be read apparently.
	 * Dieser Konstruktor initialisiert einen neuen Xml Baum Objekt mit
	 * gegebenem Namen und leerem Inhalt.
	 * 
	 * @param name
	 *            Name der Wurzel Xml Baumes
	 * @param content
	 *            Inhalt des Xml Objektes
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls einer der übergebenen Strings null oder
	 *             der name leer ist
	 */
	public XmlTree(String name, String content) throws IllegalArgumentException {
		this(name);
		if (content == null) {
			throw new IllegalArgumentException();
		}
		this.content = content;
	}

	/* Methods */

	/**
	 * Diese Methode gibt des Namen der Wurzel des Baumes zurück.
	 * 
	 * @return String Name der Wurzel
	 */
	public String getName() {
		return name;
	}

	/**
	 * Diese Methode gibt den Inhalt des Xml Objektes zurück.
	 * 
	 * @return String Name der Wurzel
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Diese Methode gibt den Wert des Attributes mit dem angegebenen Namen oder
	 * null, falls dieses nicht existiert, zurück.
	 * 
	 * @param name
	 *            Name des Attributes dessen Wert erfragt wird.
	 * @return Wert des Attributes oder null, falls das Attribut nicht
	 *         existiert.
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene Name null ist
	 */
	public String getAttributeValue(String name) throws IllegalArgumentException {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		for (XmlAttribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute.getValue();
			}
		}
		return null;
	}

	/**
	 * Diese Methode gibt eine Liste alle Unterobjekte des Xml Baumes zurück.
	 * 
	 * @return Liste aller Unterobjekte oder null, falls keine Unterobjekte
	 *         existieren
	 */
	public Iterator<XmlTree> getChildren() {
		return children.iterator();
	}
	
	
	
	
	

	/**
	 * Diese Methode gibt eine Liste aller Attribute des Xml Baumes zurück.
	 * 
	 * @return Liste aller Attribute oder null, falls keine Attribute existieren
	 */
	public Iterator<XmlAttribute> getAttributes() {
		return attributes.iterator();
	}

	/* zum bequem drüberiterieren*/
	public Iterable<XmlAttribute> getAttributes2() {
		return new AttributesIterator();
	}

	
	/**
	 * Diese Methode fügt ein Unterobjekt an diesen Xml Baum an.
	 * 
	 * @param child
	 *            Anzufügendes Unterobjekt
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene XmlTree null ist
	 */
	public void addChild(XmlTree child) throws IllegalArgumentException {
		if (child == null) {
			throw new IllegalArgumentException();
		}
		children.add(child);
		numberOfChildren++;
	}

	/**
	 * Diese Methode fügt dem Xml Objekt ein Attribut hinzu, falls kein Attribut
	 * dieses Namens existiert.
	 * 
	 * @param attribute
	 *            Anzfügendes Attribut
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene XmlTree null ist
	 */
	public void addAttribute(XmlAttribute attribute) throws IllegalArgumentException {
		if (attribute == null) {
			throw new IllegalArgumentException();
		}
		for (XmlAttribute attr : attributes) {
			if (attr.isSameAttribute(attribute)) {
				return;
			}
		}
		attributes.add(attribute);
	}

	/**
	 * Setzt oder updatetet das gegebene Attribut. Ist es null passiert nichts
	 * 
	 * @param attribute
	 *            das zu setzende Attribut
	 */
	public void updateAttribute(XmlAttribute attribute) {
		if (attribute != null) {
			for (XmlAttribute attr : attributes) {
				if (attr.isSameAttribute(attribute)) {
					attr.setValue(attribute.getValue());
					return;
				}
			}
			attributes.add(attribute);
		}
	}

	/**
	 * Gibt die Anzahl der Unterobjekte des Xml Objekts zurück.
	 * 
	 * @return Anzahl der Sub-Objekte
	 */
	public int getNumberOfChildren() {
		return numberOfChildren;
	}	
	
	/**
	 * Gibt die Anzahl der Attribute des Xml Objekts zurück.
	 * 
	 * @return Anzahl der Attribute
	 */
	public int getNumberOfAttributes() {
		return attributes.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<XmlTree> iterator() {
		return getChildren();
	}

	
	
	public class AttributesIterator implements Iterable<XmlAttribute>{

		@Override
		public Iterator<XmlAttribute> iterator() {
			return attributes.iterator();
		}
		
	}
	
}
