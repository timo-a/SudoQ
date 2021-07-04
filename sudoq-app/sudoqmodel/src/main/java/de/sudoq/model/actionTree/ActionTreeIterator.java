/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Diese Klasse stellt einen Iterator bereit, mit dem über ActionTreeElemente
 * iteriert werden kann.
 */
public class ActionTreeIterator implements Iterator<ActionTreeElement> {
	/* Attributes */

	/**
	 * Der ActionTree über den iteriert wird
	 */
	private final ActionTree actionTree;

	/**
	 * Das aktuelle Element beim Iterieren
	 */
	private ActionTreeElement currentElement;

	private final Stack<ActionTreeElement> otherPaths;

	/* Constructors */

	/**
	 * Erzeugt und instanziiert ein neues ActionTreeElement mit den gegebenen
	 * Parametern. Ist der übergebene ActionTree null, so wird eine
	 * IllegalArgumentException geworfen.
	 * 
	 * @param tree
	 *            Der ActionTree über den iteriert werden soll
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene ActionTree null ist
	 */
	public ActionTreeIterator(ActionTree tree) {
		this.actionTree = tree;
		currentElement = actionTree.rootElement;
		otherPaths = new Stack<>();
	}

	/* Methods */

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return currentElement != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public ActionTreeElement next() {
		if (currentElement == null) {
			throw new NoSuchElementException();
		}

		ActionTreeElement ret = currentElement;

		for (int i = currentElement.getChildrenList().size() - 1; i >= 0; i--) {
			otherPaths.push(currentElement.getChildrenList().get(i));
		}

		if (!otherPaths.empty()) {
			currentElement = otherPaths.pop();
		} else {
			currentElement = null;
		}

		return ret;
	}

	/**
	 * Nicht unterstützt. Wirft eine UnsupportedOperationException.
	 * 
	 * @throws UnsupportedOperationException
	 *             Wird immer geworfen, da nicht unterstützt
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
