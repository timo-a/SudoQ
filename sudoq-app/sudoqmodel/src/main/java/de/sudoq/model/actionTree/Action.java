/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree;

import de.sudoq.model.sudoku.Field;

/**
 * Diese Klasse repräsentiert eine Aktion auf einem Sudoku-Feld die vorwärts und
 * rückwärts ausgeführt werden kann.
 */
public abstract class Action {
	/* Attributes */

	/**
	 * Der Wert diff speichert die Unterschiede zwischen altem und neuem Wert.
	 */
	protected final int diff;

	/**
	 * Das Sudoku-Feld auf dem die Action ausgeführt wird
	 */
	protected Field field;

	/* Constructors */

	/**
	 * Ein geschützter Konstruktor um die Instanziierung von Actions außerhalb
	 * dieses Packages zu vermeiden. Wird er aufgerufen wird eine neue Action
	 * mit den gegebenen Parametern instanziiert. Ist das spezifizierte Field
	 * null, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param diff
	 *            Der Unterschied zwischen altem und neuem Wert
	 * @param field
	 *            Das zu bearbeitende Feld
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene Field null ist
	 */
	protected Action(int diff, Field field) {
		this.diff = diff;
		if (field != null) {
			this.field = field;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Führt diese Aktion aus.
	 */
	abstract public void execute();

	/**
	 * Macht diese Aktion rückgängig.
	 */
	abstract public void undo();

	/**
	 * Gibt die id des von dieser Action bearbeiteten Feldes zurück.
	 * 
	 * @return die Feld id.
	 */
	public int getFieldId() {
		return field.getId();
	}
	public Field getField() {
		return field;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().equals(this.getClass())) {
			Action other = (Action) o;
			return this.diff == other.diff && this.field.equals(other.field);
		}
		return false;
	}

	public abstract boolean inverse(Action a);

}
