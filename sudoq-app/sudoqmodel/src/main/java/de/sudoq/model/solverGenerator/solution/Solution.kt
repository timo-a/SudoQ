package de.sudoq.model.solverGenerator.solution

import de.sudoq.model.actionTree.Action
import java.util.*

/**
 * Ein Solution-Objekt repräsentiert einen Lösungsschritt für ein Sudoku. Es
 * setzt sich zusammen aus einer konkreten Action, die auf das Sudoku angewendet
 * ein Feld löst und den Derivations, die die Herleitung für eine Lösung
 * beschreiben (siehe dazu die Klassen SolveDerivation und Action).
 */
class Solution {
    /* Attributes */
    /**
     * Gibt die Action zurück, die diesem Solution-Objekt zugewiesen wurde.
     *
     * @return Die Action, die diesem Solution-Objekt zugewiesen wurde
     */
    /**
     * Diese Methode setzt die Action dieses Solution-Objektes auf die
     * spezifizierte. Ist diese null, so wird nichts geändert.
     *
     * @param action
     * Die Action, die diesem Solution-Objekt zugewiesen werden soll
     */
    /**
     * Die Action, die das zu dieser Solution gehörige Feld löst oder null,
     * falls diese Solution kein Feld löst
     */
    var action: Action? = null
        set(action) {
            if (action != null) field = action
        }

    /**
     * Eine Liste von SolveDerivations, die die Herleitung für die Action
     * repräsentieren
     */
    private val derivations: MutableList<SolveDerivation>
    /* Methods */
    /**
     * Diese Methode fügt die spezifizierten SolveDerivation zu der Liste der
     * SolveDerivations dieses Solution-Objektes hinzu. Ist diese null, so wird
     * sie nicht hinzugefügt.
     *
     * @param derivation
     * Die SolveDerivation, die diesem Solution-Objekt hinzugefügt
     * werden soll
     */
    fun addDerivation(derivation: SolveDerivation?) {
        if (derivation != null) derivations.add(derivation)
    }

    /**
     * Diese methode gibt einen Iterator zurück, mithilfe dessen über die diesem
     * Solution-Objekt hinzugefügten SolveDerivation iteriert werden kann.
     *
     * @return Einen Iterator, mit dem über die SolveDerivations dieses
     * Solution-Objektes iteriert werden kann
     */
    val derivationIterator: Iterator<SolveDerivation>
        get() = derivations.iterator()

    fun getDerivations(): List<SolveDerivation> {
        return derivations
    }
    /* Constructors */ /**
     * Initiiert ein neues Solution-Objekt.
     */
    init {
        derivations = ArrayList()
    }
}