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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.ObservableModelImpl;

/**
 * Diese Klasse repräsentiert die Menge aller Züge auf einem Sudoku. Sie erlaubt
 * von einem Zustand aus verschiedene Wege aus weiterzuverfolgen. Folglich
 * ergeben die Züge einen Baum.
 */
public class ActionTree extends ObservableModelImpl<ActionTreeElement> implements Iterable<ActionTreeElement> {
	/** Attributes */

	/**
	 * Der Ursprungsknoten des Baumes
	 */
	protected ActionTreeElement rootElement;
	/**
	 * Zaehlt die Elemente um jedem Element eine eindeutige id zu geben.
	 */
	private int idCounter;

	/** Constructors */

	/**
	 * Erzeugt und instanziiert einen neuen ActionTree
	 */
	public ActionTree() {
		idCounter = 1;
		rootElement = null;
	}

	/** Methods */

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
		
		/* check if action already in Tree, i.e. we went back in actionTree but are doing same steps again */
		boolean redundandAction = false;
		ActionTreeElement ate = null;
		if (mountingElement != null) {
			ArrayList<ActionTreeElement> children = mountingElement.getChildrenList();
			for(ActionTreeElement ateI : children)
				if(ateI.actionEquals(action)){
					ate = ateI;
					redundandAction = true;
					break;
				}
		}		
		
		/* */
		if(!redundandAction)
		{
			ate = new ActionTreeElement(idCounter, action, mountingElement);
			idCounter++;
		}
		
		
		if (rootElement==null) {
			rootElement = ate;  //if there's no root, ate is root
		}
		
		notifyListeners(ate);
		
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
		if (id < idCounter && id >= 1) {
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
	 * Gibt zurück ob der Baum leer ist
	 * 
	 * @return true falls er leer ist, false falls Elemente enthalten sind
	 */
	public boolean isEmpty() {
		return idCounter == 1;
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
	 * Überprüft den Baum ausgehend vom Wurzelelement auf Konsistenz. Es dürfen
	 * keine Zyklen auftreten.
	 * 
	 * @return true falls der Baum konsistent ist, andernfalls false
	 */
	public boolean isConsitent() {
		LinkedList<ActionTreeElement> elements = new LinkedList<ActionTreeElement>();
		for (ActionTreeElement ate : this) {
			if (elements.contains(ate)) {
				return false;
			}
			elements.add(ate);
		}
		return true;
	}

	/**
	 * Gibt einen Weg zwischen den zwei gegebenen Elementen repraesentiert durch
	 * eine Liste zurueck. Die Liste beginnt mit start und endet mit end. Kann
	 * kein Weg gefunden werden (weil die Elemente in verschiedenen Baeumen
	 * sind) wird null zurueckgeben. Ist start gleich end wird eine leere Liste
	 * zurückgegeben
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
		if (start.equals(end)) {
			return new LinkedList<ActionTreeElement>();
		}

		// Ways from Start or End Element to the tree root
		LinkedList<ActionTreeElement> startToRoot = new LinkedList<ActionTreeElement>();
		LinkedList<ActionTreeElement> endToRoot = new LinkedList<ActionTreeElement>();

		ActionTreeElement current, other;
		boolean path; // is start or end current
		if (start.getId() > end.getId()) {
			current = start;
			other = end;
			path = true;
		} else {
			current = end;
			other = start;
			path = false;
		}

		while (startToRoot.isEmpty() || endToRoot.isEmpty()
				|| (startToRoot.getLast() != endToRoot.getLast()
				&& (startToRoot.getLast().getParent() != null
				|| endToRoot.getLast().getParent() != null))) {

			while (current != null && (other == null || current.getId() >= other.getId())) {
				(path ? startToRoot : endToRoot).addLast(current);
				current = current.getParent();
			}

			ActionTreeElement tmp = current;
			current = other;
			other = tmp;
			path = !path;
		}

		// nodes not in the same tree?
		if (startToRoot.getLast() != endToRoot.getLast()) {
			return null;
		}

		// remove elements which are in both paths
		while (!startToRoot.isEmpty() && !endToRoot.isEmpty() && startToRoot.getLast() == endToRoot.getLast()) {
			startToRoot.removeLast();
			endToRoot.removeLast();
		}

		// readd the last removed element
		if (!startToRoot.isEmpty()) { // its itself a split up point
			startToRoot.add(startToRoot.getLast().getParent());
		} else { // its start itself
			startToRoot.add(start);
		}
		// add the end-root way backwards
		while (!endToRoot.isEmpty()) {
			startToRoot.add(endToRoot.getLast());
			endToRoot.removeLast();
		}

		return startToRoot;
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
