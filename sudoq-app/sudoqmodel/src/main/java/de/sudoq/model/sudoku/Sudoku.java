/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.ObservableModelImpl;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Diese Klasse repräsentiert ein Sudoku mit seinem Typ, seinen Feldern und seinem Schwierigkeitsgrad.
 */
public class Sudoku extends ObservableModelImpl<Field> implements Iterable<Field>, Xmlable, ModelChangeListener<Field> {
	/** Attributes */

	/**
	 * Eine Identifikationsnummer, die ein Sudoku eindeutig identifiziert
	 */
	private int id;

	private int transformCount = 0;

	/**
	 * Eine Map, welche jeder Position des Sudokus ein Feld zuweist
	 */
	protected HashMap<Position, Field> fields;

	private int fieldIdCounter;
	private Map<Integer, Position> fieldPositions;

	/**
	 * Der Typ dieses Sudokus
	 */
	private SudokuType type;

	/**
	 * Der Schwierigkeitsgrad dieses Sudokus
	 */
	private Complexity complexity;

	/** Constructors */

	/**
	 * Instanziiert ein Sudoku-Objekt mit dem spezifizierten SudokuType. Ist dieser null, so wird eine
	 * IllegalArgumentException geworfen. Alle Felder werden als editierbar ohne vorgegebene Lösung gesetzt.
	 * 
	 * @param type
	 *            Der Typ des zu erstellenden Sudokus
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene Typ null ist
	 */
	public Sudoku(SudokuType type) {
		this(type, new PositionMap<Integer>(type == null ? Position.get(1, 1) : type.getSize()),//TODO warum so kompliziert? wenn type == null fliegt eh eine exception
				   new PositionMap<Boolean>(type == null ? Position.get(1, 1) : type.getSize()));
	}

	/**
	 * Instanziiert ein Sudoku-Objekt mit dem spezifizierten SudokuType. Ist dieser null, so wird eine
	 * IllegalArgumentException geworfen.
	 * 
	 * @param type
	 *            Der Typ des zu erstellenden Sudokus
	 * @param map
	 *            Eine Map von Positions auf Lösungswerte. Werte in vorgegebenen Feldern sind verneint. (nicht negiert,
	 *            sondern bitweise verneint)
	 * @param setValues
	 *            Eine Map wo jede Position mit dem Wert true einen vorgegebenen Wert markiert
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene Typ null ist
	 */
	public Sudoku(SudokuType type, PositionMap<Integer> map, PositionMap<Boolean> setValues) {
		if (type == null) {
			throw new IllegalArgumentException();
		}

		fieldIdCounter = 1;
		fieldPositions = new HashMap<Integer, Position>();

		this.type = type;
		this.fields = new HashMap<Position, Field>();
		this.complexity = null;

		// iterate over the constraints of the type and create the fields
		for (Constraint constraint : type) {
			for (Position position : constraint) {
				if (!fields.containsKey(position)) {

					Field f;
					Integer solution = map == null ? null : map.get(position);
					if (solution != null) {
						boolean editable = setValues    == null ||
							    setValues.get(position) == null || 
							    setValues.get(position) == false;
						f = new Field(editable, solution, fieldIdCounter,type.getNumberOfSymbols());
					} else {
						f = new Field(fieldIdCounter, type.getNumberOfSymbols());
					}

					fields.put(position, f);
					fieldPositions.put(fieldIdCounter++, position);
					f.registerListener(this);
				}
			}
		}
	}

	/**
	 * Erzeugt ein vollständig leeres Sudoku, welches noch gefüllt werden muss. DO NOT USE THIS METHOD (if you are not
	 * from us)
	 */
	Sudoku() {
		id = -1;
	}

	/** Methods */

	/**
	 * Gibt die id dieses Sudokus zurueck
	 * 
	 * @return die id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gibt an wie oft dieses Sudoku bereits transformiert wurde
	 * 
	 * @return die anzahl der Transformationen
	 */
	public int getTransformCount() {
		return transformCount;
	}

	/**
	 * Zaehlt den transform Counter um 1 hoch
	 */
	public void increaseTransformCount() {
		transformCount++;
	}

	/**
	 * Gibt das Feld, welches sich an der spezifizierten Position befindet zurück. Ist position null oder in diesem
	 * Sudoku unbelegt, so wird null zurückgegeben.
	 * 
	 * @param position
	 *            Die Position, dessen Feld abgefragt werden soll
	 * @return Das Feld an der spezifizierten Position oder null, falls dies nicht existiert oder null übergeben wurde
	 */
	public Field getField(Position position) {
		if (position == null)
			return null;
		return fields.get(position);
	}

	/**
	 * Belegt die spezifizierte Position mit einem neuen Field
	 * 
	 * @param field
	 *            das neue Field
	 * @param position
	 *            die Position des neuen Fields
	 */
	public void setField(Field field, Position position) {
		if (field == null || position == null)
			return;
		fields.put(position, field);
		fieldPositions.put(field.getId(), position);
	}

	/**
	 * Gibt das Feld, das die gegebene id hat zurück. Ist id noch nicht vergeben wird null zurückgegeben
	 * 
	 * @param id
	 *            Die id des Feldes das ausgegeben werden soll
	 * @return Das Feld an der spezifizierten Position oder null, falls dies nicht existiert oder die id ungültig war
	 */
	public Field getField(int id) {
		return getField(fieldPositions.get(id));
	}

	/**
	 * Gibt die Position des Feldes, das die gegebene id hat zurück. Ist id noch nicht vergeben wird null zurückgegeben
	 * 
	 * @param id
	 *            Die id des Feldes der Position die ausgegeben werden soll
	 * @return Die spezifizierte Position oder null, falls diese nicht existiert oder die id ungültig war
	 */
	public Position getPosition(int id) {
		return fieldPositions.get(id);
	}

	/**
	 * Gibt einen Iterator zurück, mithilfe dessen über alle Felder dieses Sudokus iteriert werden kann.
	 * 
	 * @return Ein Iterator mit dem über alle Felder dieses Sudokus iteriert werden kann
	 */
	@Override
	public Iterator<Field> iterator() {
		return fields.values().iterator();
	}

	/**
	 * Gibt den Schwierigkeitsgrad dieses Sudokus zurück.
	 * 
	 * @return Der Schwierigkeitsgrad dieses Sudokus
	 */
	public Complexity getComplexity() {
		return complexity;
	}

	/**
	 * Gibt den Typ dieses Sudokus zurück.
	 * 
	 * @return Der Typ dieses Sudokus
	 */
	public SudokuType getSudokuType() {
		return type;
	}

	/**
	 * Setzt den Schwierigkeitsgrad dieses Sudokus auf den Spezifizierten. Ist dieser ungültig so wird nichts getan.
	 * 
	 * @param complexity
	 *            Der Schwierigkeitsgrad auf den dieses Sudoku gesetzt werden soll
	 */
	public void setComplexity(Complexity complexity) {
		this.complexity = complexity;
	}

	/**
	 * Gibt an, ob das Sudoku vollstaendig ausgefuellt und korrekt geloest ist.
	 * 
	 * @return true, falls das Sudoku ausgefüllt und gelöst ist, sonst false
	 */
	public boolean isFinished() {
		for (Field field : fields.values()) {
			if (!field.isSolvedCorrect()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree("sudoku");
		if (id > 0) {
			representation.addAttribute(new XmlAttribute("id", "" + id));
		}
		representation.addAttribute(new XmlAttribute("transformCount", "" + transformCount));
		representation.addAttribute(new XmlAttribute("type", "" + this.getSudokuType().getEnumType().ordinal()));
		if (complexity != null) {
			representation.addAttribute(new XmlAttribute("complexity", "" + this.getComplexity().ordinal()));
		}

		for (Map.Entry<Position, Field> field : fields.entrySet()) {
			if (field.getValue() != null) {
				XmlTree fieldmap = new XmlTree("fieldmap");
				fieldmap.addAttribute(new XmlAttribute("id", "" + field.getValue().getId()));
				fieldmap.addAttribute(new XmlAttribute("editable", "" + field.getValue().isEditable()));
				fieldmap.addAttribute(new XmlAttribute("solution", "" + field.getValue().getSolution()));
				XmlTree position = new XmlTree("position");
				position.addAttribute(new XmlAttribute("x", "" + field.getKey().getX()));
				position.addAttribute(new XmlAttribute("y", "" + field.getKey().getY()));
				fieldmap.addChild(position);
				representation.addChild(fieldmap);
			}
		}

		return representation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation) {
		// initialisation

		fieldIdCounter = 1;
		fieldPositions = new HashMap<Integer, Position>();

		fields = new HashMap<Position, Field>();

		try {
			id = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("id"));
		} catch (NumberFormatException e) {
			id = -1;
		}
		SudokuTypes enumType = SudokuTypes.values()[Integer.parseInt(xmlTreeRepresentation.getAttributeValue("type"))];
		type = SudokuBuilder.createType(enumType);
		transformCount = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("transformCount"));

		String compl = xmlTreeRepresentation.getAttributeValue("complexity");
		complexity = compl == null ? null : Complexity.values()[Integer.parseInt(compl)];

		// build the fields
		for (XmlTree sub : xmlTreeRepresentation) {
			if (sub.getName().equals("fieldmap")) {
				int     fieldId  = Integer.parseInt(    sub.getAttributeValue("id"));
				boolean editable = Boolean.parseBoolean(sub.getAttributeValue("editable"));
				int     solution = Integer.parseInt(    sub.getAttributeValue("solution"));
				int x = -1, y = -1;
				// check if there is only one child element
				if (sub.getNumberOfChildren() != 1) {
					throw new IllegalArgumentException();
				}
				XmlTree position = sub.getChildren().next();
				if (position.getName().equals("position")) {
					x = Integer.parseInt(position.getAttributeValue("x"));
					y = Integer.parseInt(position.getAttributeValue("y"));
				}
				Position pos = Position.get(x, y);
				Field field = new Field(editable, solution, fieldId, type.getNumberOfSymbols());
				field.registerListener(this);
				fields.put(pos, field);
				fieldPositions.put(Integer.valueOf(fieldId), pos);
				fieldIdCounter++;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onModelChanged(Field changedField) {
		notifyListeners(changedField);
	}

	/**
	 * Setzt die Identifikationsnummer des Sudokus.
	 * 
	 * @param id
	 *            Die eindeutige Identifikationsnummer
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Sudoku) {

			boolean allEqual = true;
			Sudoku other = (Sudoku) obj;

			allEqual &= complexity == other.getComplexity();
			allEqual &= other.getSudokuType().getEnumType().equals(type.getEnumType());

			for (Map.Entry<Position, Field> fieldpos : fields.entrySet()) {
				Field field = fieldpos.getValue();
				Field compare = other.getField(Integer.valueOf(field.getId()));
				allEqual &= compare != null;
				allEqual &= field.equals(compare);
			}
			return allEqual;
		}
		return false;
	}

	/**
	 * Gibt zurück, ob dieses Sudoku in den aktuell gesetzten Werten Fehler enthält, d.h. ob es ein Feld gibt, dessen
	 * aktueller Wert nicht der korrekten Lösung entspricht.
	 * 
	 * @return true, falls es in dem Sudoku falsch gelöste Felder gibt, false andernfalls
	 */
	public boolean hasErrors() {
		for (Field f : this.fields.values()) {
			if (!f.isNotWrong()) {
				return true;
			}
		}
		return false;
	}
}
