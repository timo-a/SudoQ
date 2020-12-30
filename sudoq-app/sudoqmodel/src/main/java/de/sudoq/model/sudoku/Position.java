/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Eine Position repräsentiert eine zweidimensionale, kartesische Koordinate.
 * implementiert als Flyweight (not quite, but Position.get(x,y) gives memoized objects)
 */
public class Position implements Xmlable {
	/** Attributes */

	
	/**
	 * Identifiziert, ob diese Position ein Flyweight ist und somit nicht geändert werden darf.
	 */
	 private boolean fixed;
	
	/**
	 * Die x-Koordinate der Position
	 */
	private int x;

	/**
	 * Die y-Koordinate der Position
	 */
	private int y;

	/**
	 * Das statische Position-Array
	 */
	private static Position[][] positions;

	/** Constructors */

	/**
	 * Instanziiert ein neues Position-Objekt mit den spezifizierten x- und y-Koordinaten. Ist eine der Koordinaten
	 * kleiner als 0, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param x
	 *            Die x-Koordinate der zu erzeugenden Position
	 * @param y
	 *            Die y-Koordinate der zu erzeugenden Position
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eine der Koordinaten kleiner als 0 ist
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		this.fixed = true;
	}

	/**
	 * Gibt ein Positions-Objekt mit den spezifizierten x- und y-Koordinaten. Ist eine der Koordinaten kleiner als 0, so
	 * wird eine IllegalArgumentException geworfen.
	 * 
	 * @param x
	 *            Die x-Koordinate der zu erzeugenden Position
	 * @param y
	 *            Die y-Koordinate der zu erzeugenden Position
	 * @return das neue Position Objekt
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eine der Koordinaten kleiner als 0 ist
	 */
	public static Position get(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("a parameter is less than zero");
		}

		if (positions == null) {
			initializePositions();
		}
		if (x < 25 && y < 25) {
			return positions[x][y];
		} else {
			Position pos = new Position(x, y);
			pos.fixed = false;
			return pos;
		}
	}

	/**
	 * Initialisiert das Positions-Array für effiziere Positionsverwaltung.
	 */
	private static void initializePositions() {
		positions = new Position[25][25];
		for (int x = 0; x < 25; x++) {
			for (int y = 0; y < 25; y++) {
				positions[x][y] = new Position(x, y);
			}
		}
	}

	/** Methods */

	/**
	 * Gibt die x-Koordinate dieser Position zurück.
	 * 
	 * @return Die x-Koordinate dieser Position
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gibt die y-Koordinate dieser Position zurück.
	 * 
	 * @return Die y-Koordinate dieser Position
	 */
	public int getY() {
		return y;
	}

	/**
	 * Vergleicht mit einem anderen Positionobjekt auf Gleichheit bzgl. der Koordinaten
	 * 
	 * @return true wenn obj vom Typ Position und mit diesem Objekt in den Koordinaten übereinstimmt, sonst false
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Position)) {
			return false;
		} else {
			Position other = (Position) obj;
			return this.x == other.x && this.y == other.y;
		}
	}

	/**
	 * Generates a unique hashcode for coordinates {@code < 65519}
	 */
	public int hashCode() {
		int p = 65519;
		int q = 65521;
		return x * p + y * q;
	}

	/**
	 * returns a distance vector by subtracting the parameter. (both objects remain unchanged)
	 * @param p a position to substract from this position
	 * @return distance between this and other as position(this-p)
     */
	public Position distance(Position p){
		return new Position(x-p.getX(), y-p.getY());
	}


	/**
	 * Gibt eine String-Repräsentation dieser Position zurück.
	 */
	public String toString() {
		return this.x + ", " + this.y;
	}

	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree("position");
		representation.addAttribute(new XmlAttribute("x", "" + x));
		representation.addAttribute(new XmlAttribute("y", "" + y));
		return representation;
	}
	public XmlTree toXmlTree(String name) {
		XmlTree representation = new XmlTree(name);
		representation.addAttribute(new XmlAttribute("x", "" + x));
		representation.addAttribute(new XmlAttribute("y", "" + y));
		return representation;
	}
	
	
	

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation)
			throws IllegalArgumentException {
		if (this.fixed)
			throw new IllegalArgumentException("Tried to manipulate a fixed position");
		this.x = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("x"));
		this.y = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("y"));
	}

	public static Position fillFromXmlStatic(XmlTree xmlTreeRepresentation) {
		return get(Integer.parseInt(xmlTreeRepresentation.getAttributeValue("x"))
		          ,Integer.parseInt(xmlTreeRepresentation.getAttributeValue("y")));
	}
}