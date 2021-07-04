package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.sudoq.model.actionTree.Action;

/**
 * Ein Solution-Objekt repräsentiert einen Lösungsschritt für ein Sudoku. Es
 * setzt sich zusammen aus einer konkreten Action, die auf das Sudoku angewendet
 * ein Feld löst und den Derivations, die die Herleitung für eine Lösung
 * beschreiben (siehe dazu die Klassen SolveDerivation und Action).
 */
public class Solution {
	/* Attributes */

	/**
	 * Die Action, die das zu dieser Solution gehörige Feld löst oder null,
	 * falls diese Solution kein Feld löst
	 */
	private Action action;

	/**
	 * Eine Liste von SolveDerivations, die die Herleitung für die Action
	 * repräsentieren
	 */
	private List<SolveDerivation> derivations;

	/* Constructors */

	/**
	 * Initiiert ein neues Solution-Objekt.
	 */
	public Solution() {
		derivations = new ArrayList<>();
	}

	/* Methods */

	/**
	 * Diese Methode setzt die Action dieses Solution-Objektes auf die
	 * spezifizierte. Ist diese null, so wird nichts geändert.
	 * 
	 * @param action
	 *            Die Action, die diesem Solution-Objekt zugewiesen werden soll
	 */
	public void setAction(Action action) {
		if (action != null)
			this.action = action;
	}

	/**
	 * Gibt die Action zurück, die diesem Solution-Objekt zugewiesen wurde.
	 * 
	 * @return Die Action, die diesem Solution-Objekt zugewiesen wurde
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Diese Methode fügt die spezifizierten SolveDerivation zu der Liste der
	 * SolveDerivations dieses Solution-Objektes hinzu. Ist diese null, so wird
	 * sie nicht hinzugefügt.
	 * 
	 * @param derivation
	 *            Die SolveDerivation, die diesem Solution-Objekt hinzugefügt
	 *            werden soll
	 */
	public void addDerivation(SolveDerivation derivation) {
		if (derivation != null)
			derivations.add(derivation);
	}

	/**
	 * Diese methode gibt einen Iterator zurück, mithilfe dessen über die diesem
	 * Solution-Objekt hinzugefügten SolveDerivation iteriert werden kann.
	 * 
	 * @return Einen Iterator, mit dem über die SolveDerivations dieses
	 *         Solution-Objektes iteriert werden kann
	 */
	public Iterator<SolveDerivation> getDerivationIterator() {
		return derivations.iterator();
	}

	public List<SolveDerivation> getDerivations() {
		return derivations;
	}
}
