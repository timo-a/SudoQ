/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.io.File;
import java.io.IOException;

/**
 * Diese Generische Klasse dient zur Handhabung der XML Repräsentation von
 * Objekten, die das Xmlable Interface implementieren.
 * 
 * @param <T>
 *            der Typ der konkret umgewandelt werden soll
 */
public abstract class XmlHandler<T extends Xmlable> {

	/** Attributes */

	/**
	 * Helfer für das Speichern und Laden von XML Dateien
	 */
	private XmlHelper helper;

	protected File file;

	/** Constructors */

	/**
	 * Dieser Konstruktor initialisiert einen neuen XmlHandler.
	 */
	public XmlHandler() {
		helper = new XmlHelper();
	}

	/** Methods */

	/**
	 * Speichert ein übegebenes Objekt, das das Xmlable Interface implementiert,
	 * in eine XML Datei.
	 * 
	 * @param obj
	 *            Objekt, das Xmlable implementiert
	 * @see Xmlable
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene Objekt null ist
	 */
	public void saveAsXml(T obj) {
		try {
			file = getFileFor(obj);
			XmlTree tree = obj.toXmlTree();
			modifySaveTree(tree);
			helper.saveXml(tree, file);
		} catch (IOException e) {
			throw new IllegalArgumentException("Something went wrong when writing xml", e);
		}
	}

	/**
	 * Lädt ein Objekt, das Xmlable implementiert, aus einer XML Datei.
	 * 
	 * @param obj
	 *            leeres Objekt, das Xmlable implementiert
	 * @return Objekt, welches Xmlable implementiert
	 * @see Xmlable
	 */
	public T createObjectFromXml(T obj) {
		try {
			obj.fillFromXml(helper.loadXml(getFileFor(obj)));
		} catch (IOException e) {
			throw new IllegalArgumentException("Something went wrong when reading xml " + getFileFor(obj), e);
		}
		return obj;
	}

	/**
	 * Lässt Subklassen den XMLTree falls nötig anpassen
	 * 
	 * @param tree
	 *            der Ursprüngliche Baum
	 */
	protected void modifySaveTree(XmlTree tree) {
	}

	/**
	 * Gibt ein File welches auf den Speicherort des gegebenen Objektes zeigt
	 * zurueck
	 * 
	 * @param obj
	 *            das zu speichernde/ladende Objekt
	 * @return das darauf zeigende File
	 */
	abstract protected File getFileFor(T obj);
}
