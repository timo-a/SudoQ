/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree;

import java.util.ArrayList;
import java.util.Iterator;

import de.sudoq.model.ObservableModelImpl;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Diese Klasse repräsentiert ein einzelnes Element des Aktionsbaums.
 */
public class ActionTreeElement extends ObservableModelImpl<ActionTreeElement> implements Comparable<ActionTreeElement>,
		Iterable<ActionTreeElement> {
	/* Attributes */

	/**
	 * Eine eindeutige identifikationsnummer für dieses Element
	 */
	private int id;

	/**
	 * Die Aktion dieses Elements
	 */
	private Action action;
	/**
	 * Das Elternelement dieses Elements
	 */
	private ActionTreeElement previous;
	/**
	 * Die Liste aller Kindelemente dieses Elements
	 */
	private ArrayList<ActionTreeElement> following;
	/**
	 * Gibt an ob der Zustand im Baum markiert ist.
	 */
	private boolean marked;
	/**
	 * Gibt an ob dieser Zug bezüglich des zugrunde liegenden Sudokus ein falscher Zug war
	 * Wenn false bedeutet das nicht, dass der Zug keine Fehler sein kann.
	 */
	private boolean mistake;
	/**
	 * Gibt an, dass dieser Zug das Sudoku in einem korrekten Zustand hinterlässt.
	 * Wenn false bedeutet das nicht, dass dieser Zug falsch sein muss.
	 */
	private boolean correct;

	/**
	 * Konstante für XmlAttribut
	 */
	public static final String ID = "id";
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String PARENT = "parent";
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String DIFF = "value";
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String FIELD_ID = "field_id";//corresponds to cell_id, left unchanged
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String ACTION_TYPE = "action_type";
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String MARKED = "marked";
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String MISTAKE = "mistake";
	/**
	 * Konstante für XmlAttribut
	 */
	public static final String CORRECT = "correct";

	/* Constructors */

	/**
	 * Erzeugt und instanziiert ein neues ActionTreeElement mit den gegebenen Parametern. Ist die ID ungültig oder das
	 * Elternelement null, so wird eine IllegalArgumentException geworfen.
	 * 
	 * @param id
	 *            Die eindeutige ID für dieses Element
	 * @param action
	 *            die diesem ActionTreeElement zugrundeliegende Aktion
	 * @param parent
	 *            Das Elternelement
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die ID ungültig oder die Action null ist.
	 */
	public ActionTreeElement(int id, Action action, ActionTreeElement parent) {
		if (action == null) {
			throw new IllegalArgumentException();
		} else {
			this.id = id;
			this.action = action;
			this.marked = false;
			this.mistake = false;
			this.correct = false;

			this.previous = parent;
			if (previous != null) {
				parent.addChild(this);
			}

			following = new ArrayList<>();
		}
	}

	public Action getAction() {
		return action;
	}


	/**
	 * Diese Methode führt die Aktion dieses Elementes aus.
	 * 
	 * @see Action
	 */
	public void execute() {
		action.execute();
	}

	/**
	 * Diese Methode macht die Aktion dieses Elementes rückgängig.
	 * 
	 * @return the parent ActionTreeElement
	 * @see Action
	 */
	public ActionTreeElement undo() {
		action.undo();
		return previous;
	}

	/**
	 * Gibt die id dieses Elements zurueck.
	 * 
	 * @return die id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Überprüft, ob dieses Element mehrere mögliche nachfolgende Elemente hat.
	 * 
	 * @return true falls mehrere Kindelemente vorhanden sind, andernfalls false
	 */
	public boolean isSplitUp() {
		return following.size() > 1;
	}

	/**
	 * Fuegt der Liste der Kinderelemente das gegebene hinzu.\ Ist es null passiert nichts.
	 * 
	 * @param child
	 *            das hinzuzufuegende Kindelement
	 */
	void addChild(ActionTreeElement child) {
		if (child != null) {
			following.add(child);
		}
	}

	/**
	 * Gibt das Elternelement dieses Knotens zurück.
	 * 
	 * @return Das Elternelement
	 */
	public ActionTreeElement getParent() {
		return previous;
	}

	/**
	 * Gibt die Liste aller Kindelemente zurück.
	 * 
	 * @return Die Liste aller Kindelemente.
	 */
	// ja, package scope TODO refactor and make package scope again
	public ArrayList<ActionTreeElement> getChildrenList() {
		return following;
	}

	/**
	 * Gibt einen Iterator ueber die Liste aller Kindelemente zurück.
	 * 
	 * @return Die Iterator aller Kindelemente.
	 */
	public Iterator<ActionTreeElement> getChildren() {
		return following.iterator();
	}

	/**
	 * Ueberprueft ob dieses Element im Baum markiert ist
	 * 
	 * @return true falls markiert, andernfalls false
	 */
	public boolean isMarked() {
		return marked;
	}

	/**
	 * Markiert dieses Element
	 */
	public void mark() {
		marked = true;
		notifyListeners(this);
	}
	
	/**
	 * Markiert diesen Zug als Fehler
	 */
	public void markWrong() {
		mistake = true;
	}
	
	/**
	 * gibt zurück ob dieser Zug in seinem Sudoku eine Fehler war
	 * @return true falls es ein Fehler war, false heißt, dass der Zug unbestimmt ist
	 */
	public boolean isMistake() {
		return mistake;
	}
	
	/**
	 * Markiert diesen Zug als in jedem Fall korrekt
	 */
	public void markCorrect() {
		correct = true;
	}
	
	/**
	 * gibt zurück ob dieser Zug in seinem Sudoku korrekt ist
	 * @return true falls er korrekt ist, false heißt, dass der Zug unbestimmt ist
	 */
	public boolean isCorrect() {
		return correct;
	}

	/**
	 * @see Xmlable#toXmlTree()
	 * @return den resultierenden XmlTree
	 */
	public XmlTree toXml() {
		if (action.cell.getId() <= 0)
			return null;
		XmlTree xml = new XmlTree("action");

		xml.addAttribute(new XmlAttribute(ID, Integer.toString(getId())));
		xml.addAttribute(new XmlAttribute(PARENT, previous == null ? "" : Integer.toString(previous.getId())));
		xml.addAttribute(new XmlAttribute(DIFF, Integer.toString(action.diff)));
		xml.addAttribute(new XmlAttribute(FIELD_ID, Integer.toString(action.cell.getId())));
		xml.addAttribute(new XmlAttribute(ACTION_TYPE, action.getClass().getSimpleName()));
		xml.addAttribute(new XmlAttribute(MARKED, Boolean.toString(marked)));
		if (mistake) {
			xml.addAttribute(new XmlAttribute(MISTAKE, Boolean.toString(true)));
		} 
		if (correct) {
			xml.addAttribute(new XmlAttribute(CORRECT, Boolean.toString(true)));
		}

		return xml;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(ActionTreeElement another) {
		return this.id - another.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof ActionTreeElement) {
			ActionTreeElement ate = (ActionTreeElement) o;
			return this.id == ate.id && this.marked == ate.marked && this.action.equals(ate.action);
		}
		return false;
	}

	/**
	 * Returns whether the action held by this ActionTreeElement is equal to the action passed as parameter
	 * @param o external action to compare
	 * @return true if this.action.equals(o) else false
	 * 
	 */
	public boolean actionEquals(Object o){
		if (o instanceof Action){//actually unneccessary as action checks again
			Action a = (Action) o;
			return this.action.equals(a);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<ActionTreeElement> iterator() {
		return getChildren();
	}
}
