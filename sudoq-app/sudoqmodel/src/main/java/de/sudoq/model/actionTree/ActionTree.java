/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.actionTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.sudoq.model.ObservableModelImpl;
import de.sudoq.model.sudoku.Field;

/**
 * Diese Klasse repräsentiert die Menge aller Züge auf einem Sudoku. Sie erlaubt
 * von einem Zustand aus verschiedene Wege aus weiterzuverfolgen. Folglich
 * ergeben die Züge einen Baum.
 */
public class ActionTree extends ObservableModelImpl<ActionTreeElement> implements Iterable<ActionTreeElement> {

	/**
	 * Der Ursprungsknoten des Baumes
	 */
	protected ActionTreeElement rootElement;
	/**
	 * Zaehlt die Elemente um jedem Element eine eindeutige id zu geben.
	 */
	private int idCounter;

	public enum InsertStrategy {redundant, undo, upwards, regular, none}

	private InsertStrategy lastStrategy = InsertStrategy.none;
	public InsertStrategy getLastInsertStrategy(){return lastStrategy;}
	private List<Action> actionSequence = new ArrayList<>();

	/**
	 * Erzeugt und instanziiert einen neuen ActionTree
	 */
	public ActionTree() {
		idCounter = 1;
		Action mockAction = new Action(0, new Field(-1, 1)) {
			public void undo() { }
			public void execute() { }
			public boolean inverse(Action a){ return false; }
		};
		rootElement = new ActionTreeElement(idCounter++, mockAction, rootElement);
	}

	/* Methods */

	/**
	 * Diese Methode fügt die gegebene Action an der gegebenen Stelle zum Baum
	 * hinzu. Beide dürfen nicht null sein (NullPointerException).
	 * 
	 * @param action
	 *            Die hinzuzufügende Action
	 * @param mountingElement
	 *            Das Element unter dem die Aktion eingehangen werden soll. Es
	 *            wird NICHT überprüft, ob dieses Teil des Baums ist
	 * @return Das neue Element, das die gegebene Aktion enthält.
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eines der übergebenen Attribute null ist
	 *             (ausser der Baum ist noch leer, dann darf mountingElement
	 *             null sein)
	 */
	public ActionTreeElement add(Action action, ActionTreeElement mountingElement) {

		if (rootElement != null && mountingElement == null) {
				throw new IllegalArgumentException(); //There'a a root but no mounting el? -> throw exception 
		}

		//mount new tree node on specified parent node
		ActionTreeElement ate = new ActionTreeElement(idCounter++, action, mountingElement);


		//TODO as of now theres never a null root, because ActionTree starts with a dummy right?
		if (rootElement==null) {
			rootElement = ate;  //if there's no root, ate is root
		}
		
		notifyListeners(ate);
		//actionSequence.clear();
		return ate;
		
	}






	/**
	 * Diese Methode durchsucht den Baum nach dem Element mit der gegebenen id.
	 * Gegebenenfalls wird es zurückgegeben, andernfalls null.
	 * 
	 * @param id
	 *            Die id des zu suchenden Elements
	 * @return Das gefundene Element oder null
	 */
	public ActionTreeElement getElement(int id) {
		if ( 1 <= id && id < idCounter ) {
			//ActionTreeElement currentElement = rootElement;
			//Stack<ActionTreeElement> otherPaths = new Stack<ActionTreeElement>();

			for(ActionTreeElement ate:this)
				if(ate.getId() == id)
					return ate;

		}

		return null;
	}

	/**
	 * Gibt die Anzahl der Elemente im Baum zurück
	 * 
	 * @return die Anzahl
	 */
	public int getSize() {
		return idCounter - 1;
	}


	/**
	 * Gibt das Wurzelelemtn dieses Baums zurueck
	 * 
	 * @return das Wurzelelment
	 */
	public ActionTreeElement getRoot() {
		return rootElement;
	}

	/**
	 * Returns the shortest path in the tree from start to end.
	 * start and end are included in the path, unless they are identical, then an empty list is returned.
	 *
	 * @param start
	 *            der Startpunkt
	 * @param end
	 *            der Endpunkt
	 * @return den Weg
	 * @throws NullPointerException
	 *             falls start oder end null sind
	 */
	static public List<ActionTreeElement> findPath(ActionTreeElement start, ActionTreeElement end) {
		//Assumptions:
		//    every tree has a root with id == 0, so there is a common ancestor node by definition
		if (start.getId() == end.getId()) {
			return Collections.EMPTY_LIST;
		}

		// Ways from Start or End Element to the tree root
		LinkedList<ActionTreeElement> startToRoot = new LinkedList<>(Collections.singleton(start));
		LinkedList<ActionTreeElement> endToRoot = new LinkedList<>(Collections.singleton(end));

		/* back track until parents with same ids are found */
		LinkedList<ActionTreeElement> current = startToRoot;
		LinkedList<ActionTreeElement> other   = endToRoot;

		while (noCommonAncestorFoundMoreToGo(startToRoot, endToRoot)) {

			catchUp(current, other);//add to current until current.last.id <= other.last.id

			LinkedList<ActionTreeElement> tmp = current;//swap
			current = other;
			other = tmp;
		}

		// both last elements now have the same id (worst case it is 1 i.e. root)
		// if the last elements are not also identical, they must be from different trees
		if (startToRoot.getLast() != endToRoot.getLast()) {
			return null;//todo return [] instead?
		}

		// remove elements which are in both paths
		ActionTreeElement commonAncestor;
		do {
			commonAncestor = startToRoot.removeLast();
			endToRoot.removeLast();
		} while (!startToRoot.isEmpty() && !endToRoot.isEmpty()
				&& startToRoot.getLast() == endToRoot.getLast());

		// add the end-root way backwards
		startToRoot.addLast(commonAncestor);
		for (Iterator<ActionTreeElement> it = endToRoot.descendingIterator(); it.hasNext(); ) {
			startToRoot.addLast(it.next());
		}

		return startToRoot;
	}

	private static boolean noCommonAncestorFoundMoreToGo(LinkedList<ActionTreeElement> startToRoot, LinkedList<ActionTreeElement> endToRoot){
		int lastId1 = startToRoot.getLast().getId();
		int lastId2 = endToRoot.getLast().getId();
		boolean lastElementsDiffer = lastId1 != lastId2;

		boolean notBothRoot = lastId1 > 1 || lastId2 > 1;//not necessary when we are absolutely sure to end up at the same root node
		//maybe compare ids in last elements differ
		return lastElementsDiffer && notBothRoot;
	}

	/**
	 * adds parents to current until current's last element has an id lesser or equal other's
	 */
	private static void catchUp(LinkedList<ActionTreeElement> current, LinkedList<ActionTreeElement> other){
		while (current.getLast().getId() > other.getLast().getId()) {
			ActionTreeElement parent = current.getLast().getParent();
			current.addLast(parent);
		}

	}


	/**
	 * Gibt einen Iterator für die ActionTreeElemente zurück.
	 * 
	 * @return einen Iterator für die ActionTreeElemente
	 */
	public Iterator<ActionTreeElement> iterator() {
		return new ActionTreeIterator(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ActionTree) {
			ActionTree at = (ActionTree) obj;
			if (this.getSize() == at.getSize()) {
				Iterator<ActionTreeElement> at1 = this.iterator();
				Iterator<ActionTreeElement> at2 = at.iterator();
				while (at1.hasNext()) {
					// since the sites are equals at2.hasNext() is true
					if (!at1.next().equals(at2.next())) return false;
				}
				return true;
			}
		}
		return false;
	}

}
