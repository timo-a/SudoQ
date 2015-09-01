package de.sudoq.model.solverGenerator.solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Ein Objekt dieser Klasse stellt einen Herleitungsschritt für die Lösung eines
 * Sudoku-Feldes dar. Dazu enthält es eine Liste von DerivationFields und
 * DerivationBlocks, die Informationenen über die entsprechend relevanten
 * Blöcke, sowie Kandidaten in den beteiligten Feldern enthalten.
 */
public class SolveDerivation {
	/** Attributes */

	/**
	 * Eine Liste von DerivationFields, die für diesen Lösungsschritt relevant
	 * sind
	 */
	private List<DerivationField> fields;

	/**
	 * Eine Liste von DerivationBlocks, die für diesen Lösungsschritt relevant
	 * sind
	 */
	private List<DerivationBlock> blocks;

	/** Constructors */

	/**
	 * Initiiert ein neues SolveDerivation-Objekt.
	 */
	public SolveDerivation() {
		this.fields = new ArrayList<DerivationField>();
		this.blocks = new ArrayList<DerivationBlock>();
	}

	/** Methods */

	/**
	 * Diese Methode fügt das spezifizierte DerivationField zur Liste der
	 * DerivationFields dieses SolveDerivation-Objektes hinzu. Ist das
	 * übergebene Objekt null, so wird es nicht hinzugefügt.
	 * 
	 * @param field
	 *            Das DerivationField, welches dieser SolveDerivation
	 *            hinzugefügt werden soll
	 */
	public void addDerivationField(DerivationField field) {
		if (field == null)
			return;
		this.fields.add(field);
	}

	/**
	 * Diese Methode fügt den spezifizierten DerivationBlock zur Liste der
	 * DerivationBlocks dieses SolveDerivation-Objektes hinzu. Ist das übegebene
	 * Objekt null, so wird es nicht hinzugefügt.
	 * 
	 * @param block
	 *            Der DerivationBlock, welcher dieser SolveDerivation
	 *            hinzugefügt werden soll
	 */
	public void addDerivationBlock(DerivationBlock block) {
		if (block == null)
			return;
		this.blocks.add(block);
	}

	/**
	 * Diese Methode gibt einen Iterator zurück, mit dem über die diesem Objekt
	 * hinzugefügten DerivationFields iteriert werden kann.
	 * 
	 * @return Ein Iterator, mit dem über die DerivationFields dieses
	 *         SolveDerivation-Objektes iteriert werden kann
	 */
	public Iterator<DerivationField> getFieldIterator() {
		return fields.iterator();
	}

	/**
	 * Diese Methode gibt einen Iterator zurück, mithilfe dessen über die diesem
	 * Objekt hinzugefügten DerivationBlocks iteriert werden kann.
	 * 
	 * @return Ein Iterator, mit dem über die DerivationBlocks dieses
	 *         SolveDerivation-Objektes iteriert werden kann
	 */
	public Iterator<DerivationBlock> getBlockIterator() {
		return blocks.iterator();
	}
}
