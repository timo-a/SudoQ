/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Ein Constraint-Objekt beschreibt eine Zusammenfassung von Feldern (bzw. dessen Positionen) in einem Sudoku, sodass
 * diese zusammen eine bestimmte Vorgabe erfüllen müssen, die von einem ConstraintBehavior-Objekt beschrieben wird. Im
 * Standardsudoku sind beispielsweise die Zeilen, Spalten und Blöcke Constraint-Objekte.
 */
public class Constraint implements Iterable<Position>, Xmlable {
	/** Attributes */

	/**
	 * Eine Liste der Positionen aller Felder, die zusammen eine Vorgabe erfüllen müssen
	 */
	private List<Position> positions;

	/**
	 * Das Objekt welches die Vorgabe beschreibt, die die Felder dieses Constraint-Objektes erfüllen müssen
	 */
	private ConstraintBehavior behavior;

	/**
	 * Der Name dieses Constraints
	 */
	private String name;

	/**
	 * Der Typ des Constraints
	 */
	private ConstraintType type;

	/** Constructors */

	/**
	 * Dieser Konstruktor instanziiert ein neues Constraint-Objekt mit dem spezifizierten ConstraintBehavior. Ist dieses
	 * null, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param behavior
	 *            Das Verhalten, welches dieses Constraint haben soll
	 * @param type
	 *            Der Typ des Constraints
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das spezifizierte ConstraintBehavior-Objekt null ist
	 */
	public Constraint(ConstraintBehavior behavior, ConstraintType type) {
		if (behavior != null && type != null) {
			this.behavior = behavior;
			positions = new ArrayList<Position>();
			this.type = type;
		} else {
			throw new IllegalArgumentException();
		}
		this.name = "Constraint with " + behavior;
	}

	/**
	 * Dieser Konstruktor instanziiert ein neues Constraint-Objekt mit dem spezifizierten ConstraintBehavior. Das
	 * Constraint erhält den spezifizierten Namen, welcher mit der toString-Methode ausgegeben wird. Ist der Name null,
	 * so wird "Constraint with (behavior)" als Name gespeichert.
	 * 
	 * @param behavior
	 *            Das Verhalten, welches dieses Constraint haben soll
	 * @param type
	 *            Der Typ des Constraints
	 * @param name
	 *            Die Bezeichnung dieses Constraints. Überlappende Constraints sollten mit "extra block" beginnen,
	 *            Blöcke mit "Block", Spalten mit "Column", Zeilen mit "Row".
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das spezifizierte ConstraintBehavior-Objekt null ist
	 */
	public Constraint(ConstraintBehavior behavior, ConstraintType type, String name) {
		if (behavior != null && type != null) {
			this.behavior = behavior;
			positions = new ArrayList<Position>();
			this.type = type;
		} else {
			throw new IllegalArgumentException();
		}
		this.name = name == null ? "Constraint with " + behavior : name;
	}

	/** Methods */

	/**
	 * Diese Methode fügt diesem Constraint-Objekt die spezifizierte Position hinzu. Ist diese null, so wird nichts
	 * getan.
	 * 
	 * @param position
	 *            Die Position die diesem Constraint hinzugefügt werden soll
	 */
	public void addPosition(Position position) {
		if (position != null && !positions.contains(position)) {
			positions.add(position);
		}
	}

	/**
	 * Diese Methode ermittelt, ob das spezifizierte Sudoku dieses Constraint erfüllt, also auf den hinzugefügten
	 * Feldern, das spezifizierte ConstraintBehavior erfüllt. Ist dies nicht der Fall, ist das spezifizierte Sudoku null
	 * oder enthält das Sudoku nicht alle Positionen, die dieses Constraint überprüfen möchte, so wird false
	 * zurückgegeben. Erfüllt das Sudoku die Vorgaben, so wird true zurückgegeben.
	 * 
	 * @param sudoku
	 *            Das Sudoku bei dem überprüft werden soll, ob dieses Constraint erfüllt ist
	 * @return true falls das Sudoku die Vorgaben erfüllt, false falls es dies nicht tut oder null ist
	 */
	public boolean isSaturated(Sudoku sudoku) {
		return sudoku != null && behavior.check(this, sudoku);
	}

	/**
	 * Diese Methode gibt einen Iterator zurück, mithilfe dessen über die Positionen der Felder, die diesem Constraint
	 * zugewiesen sind iteriert werden kann.
	 * 
	 * @return Einen Iterator mit dem über die diesem Constraint zugewiesen Positionen iteriert werden kann
	 */
	@Override
	public Iterator<Position> iterator() {
		return positions.iterator();
	}

	/**
	 * Gibt zurück ob dieses Constraint die gegebene Position beinhaltet.
	 * 
	 * @param p
	 *            die Position von der Überprüft werden soll ob sie enthalten ist.
	 * @return true falls sie enthalten ist, andernfalls false.
	 */
	public boolean includes(Position p) {
		return positions.contains(p);
	}

	/**
	 * Gibt die Anzahl der Positions, welche laut dem Constraint eine bestimmte Vorgabe erfuellen müssen, zurueck.
	 * 
	 * @return Die Anzahl an Positions, die diesem Constraint zugeordnet sind
	 */
	public int getSize() {
		return positions.size();
	}

	/**
	 * Gibt zurück, ob das Verhalten dieses Constraints das UniqueConstraintBehavior ist.
	 * 
	 * @return true, falls dieses Constraint ein UniqueConstraint-Verhalten hat, false andernfalls
	 */
	public boolean hasUniqueBehavior() {
		return (behavior instanceof UniqueConstraintBehavior);
	}

	/**
	 * Gibt eine Kurzrepräsentation dieses Objektes als String zurück.
	 * 
	 * @return eine Kurzrepräsentation dieses Objektes als String
	 */
	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * Gibt eine Liste der Positionen, welche zu diesem Constraint gehören zurück. Hinweis: Wenn möglich stattdessen den
	 * Iterator benutzen.
	 * 
	 * @return Eine Liste der Positionen, welche zu diesem Constraint gehören.
	 */
	public ArrayList<Position> getPositions() {
		return (ArrayList<Position>) this.positions;
	}

	/**
	 * Gibt den Typ dieses Constraints zurück.
	 * 
	 * @return Der Typ dieses Constraints
	 */
	public ConstraintType getType() {
		return this.type;
	}

	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree("constraint");
		representation.addAttribute(new XmlAttribute("behavior", behavior.getClass().toString()));
		representation.addAttribute(new XmlAttribute("name", name));
		representation.addAttribute(new XmlAttribute("type", "" + type.ordinal()));
		for (Position pos : positions) {
			representation.addChild(pos.toXmlTree());
		}
		return representation;
	}

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation)
			throws IllegalArgumentException {
		String behavior = xmlTreeRepresentation.getAttributeValue("behavior");
		if (behavior.contains("Unique")) {
			this.behavior = new UniqueConstraintBehavior();
		} else {
			throw new IllegalArgumentException("Undefined constraint behavior");
		}

		this.name = xmlTreeRepresentation.getAttributeValue("name");
		this.type = ConstraintType.values()[Integer.parseInt(xmlTreeRepresentation.getAttributeValue("type"))];

		for (XmlTree sub : xmlTreeRepresentation) {
			if (sub.getName().equals("position")) {
				addPosition(Position.fillFromXmlStatic(sub));
			}
		}
	}

}
