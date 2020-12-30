/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

import java.util.List;
import java.util.Stack;

import de.sudoq.model.ObservableModelImpl;
import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.ActionTree;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.model.actionTree.SolveAction;

/**
 * Diese Klasse verwaltet den Zustand eines Spiels durch einen ActionTree und stellt Funktionalität für die Verwaltung
 * des Zustandes zur Verfügung.
 */
public class GameStateHandler extends ObservableModelImpl<ActionTreeElement> {
	/* Attributes */

	/**
	 * Die Datenstruktur um die Züge und ihre Abfolge zu speichern
	 */
	private final ActionTree actionTree;
	/**
	 * Der aktuelle Zustand, die Aktion darin muss bereits ausgeführt sein
	 */
	private ActionTreeElement currentState;
	/**
	 * Ein Stack um bei undo über Verzweigungen hinweg den Rückweg zu speichern
	 */
	private final Stack<ActionTreeElement> undoStack;

	/**
	 * Eine locking Variable um zu verhindern, dass durch Listener waehrend Veraenderungen weitere Veraenderung
	 * hinzugefuegt werden
	 */
	private boolean locked;

	/* Constructors */

	/**
	 * Erzeugt und instanziiert einen neuen GameStateHandler.
	 */
	public GameStateHandler() {
		actionTree = new ActionTree();
		undoStack = new Stack<>();

		currentState = actionTree.getRoot();

		locked = false;
	}

	/**
	 * Die Methode gibt den ActionTree zurück.
	 *
	 * @return Der ActionTree
	 */
	public ActionTree getActionTree() {
		return actionTree;
	}

	/**
	 * Die Methode gibt den aktuellen Zustand zurück.
	 *
	 * @return Das ActionTreeElement mit dem aktuellen Zustand
	 */
	public ActionTreeElement getCurrentState() {
		return currentState;
	}

	/**
	 * Fügt die gegebene Aktion dem ActionTree hinzu und führt sie aus.
	 *
	 * @param action Die auszuführende Aktion
	 * @throws IllegalArgumentException falls Action null ist
	 */
	public void addAndExecute(Action action) {
		// if another change is in progress dont execute!
		if (!locked) {
			locked = true;

			addStrategic(action);

			notifyListeners(currentState);

			locked = false;
		}
	}

	//private
	private void addStrategic(Action action){
		if(isActionRedundant(currentState, action)) {
			currentState = findExistingChildren(currentState, action).get(0);
			action.execute();

		}else if(isActionAStepBack(currentState, action)) {
			currentState = currentState.getParent();
			action.execute();

		}else if(isSolveOnSameField(action)) {

			SolveAction intended = (SolveAction) action;
			SolveAction above = (SolveAction) currentState.getAction();
			SolveAction liftedAction = intended.add(above);
			currentState.undo();
			currentState = currentState.getParent();
			addStrategic(liftedAction);
		} else	{
			currentState = actionTree.add(action, currentState);
			currentState.execute();
		}

	}



	/* check if action already in Tree,
	      i.e. we went back in actionTree but are doing same steps again */
	public boolean isActionRedundant(ActionTreeElement mountingElement, Action action){

		return !findExistingChildren(mountingElement, action).isEmpty();
	}

	private List<ActionTreeElement> findExistingChildren(ActionTreeElement mountingElement, Action action){
		List<ActionTreeElement> l = new Stack<>();

		if (mountingElement != null) {
			for(ActionTreeElement ateI : mountingElement.getChildrenList())
				if(ateI.actionEquals(action))
					l.add(ateI);
		}
		return l;
	}

	private boolean isActionAStepBack(ActionTreeElement mountingElement, Action action) {
		return mountingElement.getAction().inverse(action);
	}

	private boolean isSolveOnSameField(Action action){
		return currentState != actionTree.getRoot() && bothSolveActions(currentState, action) && isActionOnSameField(currentState, action);
	}

	private boolean bothSolveActions(ActionTreeElement mountingElement, Action action) {
		Action actionAbove = mountingElement.getAction();
		return (action instanceof SolveAction) && (actionAbove instanceof SolveAction);
	}

	private boolean isActionOnSameField(ActionTreeElement mountingElement, Action action) {

		boolean sameField = mountingElement.getAction().getField().equals(action.getField());
		return (action instanceof SolveAction) && sameField;
	}



	/**
	 * Führt alle nötigen Aktionen aus, damit das Sudoku nach Ausführung dieser Methode wieder im gleichen Zustand wie
	 * nach der ersten Ausführung der gegebenen Aktion ist.
	 * 
	 * @param target
	 *            Das ActionTreeElement in dessen Zustand das Sudoku überführt werden soll
	 * @throws NullPointerException
	 *             falls target null
	 */
	public void goToState(ActionTreeElement target) {
		locked = true;
		boolean onlyUndo = true;

		List<ActionTreeElement> listWay = ActionTree.findPath(currentState, target);
		ActionTreeElement[] way = new ActionTreeElement[listWay.size()];
		listWay.toArray(way);

		for (int i = 1; i < way.length; i++) {
			if (way[i - 1].getParent() == way[i]) {//are we going backwards?
				way[i - 1].undo();
				if (way[i].isSplitUp()) {
					undoStack.push(way[i - 1]);
				}

			} else {
				onlyUndo = false;
				if (i - 2 >= 0 && way[i - 2].getParent() != way[i - 1]) {
					way[i - 1].execute();
				}
				if (way[i] == target) {
					target.execute();
				}
			}
		}

		if (!onlyUndo) {
			undoStack.clear();
		}
		currentState = target;
		notifyListeners(currentState);

		locked = false;
	}

	/**
	 * Gibt zurück, ob die letzte Aktion rückgängig gemacht werden kann
	 * 
	 * @return true, falls die letzte Aktion rückgängig gemacht werden kann, false falls es keine Aktion gibt, die
	 *         rückgängig gemacht werden kann
	 */
	public boolean canUndo() {
		return currentState.getParent() != null;
	}

	/**
	 * Macht die letzte Aktion rückgängig. Ein Schritt rückwärts in der Versionshistorie.
	 */
	public void undo() {
		locked = true;

		if (currentState.getParent() != null) {
			ActionTreeElement oldElement = currentState;
			currentState = currentState.undo();

			if (currentState.isSplitUp()) {
				undoStack.push(oldElement);
			}

			notifyListeners(currentState);
		}

		locked = false;
	}

	/**
	 * Gibt zurück, ob ub der Versionshistorie einen Schritt vorwärts gegangen werden kann.
	 * 
	 * @return true, falls ein Schritt vorwärts gegangen werden kann, false falls nicht
	 */
	public boolean canRedo() {
		return (currentState.isSplitUp() && !undoStack.empty())
				|| (!currentState.isSplitUp() && currentState.getChildren().hasNext());
	}

	/**
	 * Geht falls möglich und eindeutig einen Schritt vorwärts in der Versionshistorie. Ist diese verzweigt, aber der
	 * Schritt rückwärts erfolgte über undo(), wird dieses undo rückgängig gemacht.
	 */
	public void redo() {
		locked = true;

		if (currentState.isSplitUp()) {
			if (!undoStack.empty()) {
				currentState = undoStack.pop();
				currentState.execute();
				notifyListeners(currentState);
			}
		} else {
			if (currentState.getChildren().hasNext()) {
				currentState = currentState.getChildren().next();
				currentState.execute();
				notifyListeners(currentState);
			}
		}

		locked = false;
	}

	/**
	 * Markiert den aktuellen Zustand um ihn später einfacher wiederzufinden
	 */
	public void markCurrentState() {
		currentState.mark();
	}

	/**
	 * Überprüft, ob das gegebene ActionTreeElement markiert ist.
	 * 
	 * @param ate
	 *            das zu überprüfende ActionTreeElement
	 * @return true falls es markiert ist und false falls nicht oder null ist
	 */
	public boolean isMarked(ActionTreeElement ate) {
		return ate != null && ate.isMarked();
	}
}
