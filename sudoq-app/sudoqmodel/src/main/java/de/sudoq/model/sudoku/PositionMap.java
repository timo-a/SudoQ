/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku;

/**
 * Diese Klasse stellt eine Map von Positions auf generische Objekte zur Verfügung. Da das Mapping direkt über die
 * 2D-Koordinaten der Positions vorgenommen wird, ist dieses Mapping effizienter als HashMaps oder TreeMaps.
 * 
 * @param <T>
 *            Ein belibiger Typ, auf den Positions abgebildet werden sollen
 */
public class PositionMap<T> implements Cloneable {
	/**
	 * Die BoundingBox-Abmessungen der Positionen dieser PositionMap
	 */
	Position dimension;

	/**
	 * Das Werte-Array dieser PositionMap
	 */
	T[][] values;

	/**
	 * Initialisiert eine neue Position-Map für so viele Einträge, wie eine Matrix der spezifizierten Dimension hat. Die
	 * Größe muss in beiden Komponenten mindestens 1 sein.
	 * 
	 * @param dimension
	 *            Die Größe der PositionMap
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls die spezifizierte Position null oder eine der Dimensionskomponenten 0 ist
	 */
	@SuppressWarnings("unchecked")
	public PositionMap(Position dimension) {
		if (dimension == null || dimension.getX() < 1 || dimension.getY() < 1)
			throw new IllegalArgumentException("Specified dimension or one of its components was null.");

		this.dimension = dimension;
		this.values = (T[][]) new Object[dimension.getX()][dimension.getY()];
	}

	/**
	 * Fügt das spezifizierte Objekt an der spezifizierten Position ein. Ist dort bereits ein Objekt vorhanden, so wird
	 * dieses überschrieben.
	 * 
	 * @param pos
	 *            Die Position, an der das BitSet eingefügt werden soll
	 * @param object
	 *            Das Objekt, welches eingefügt werden soll
	 * @return der Wert der vorher an pos lag, oder null falls es keinen gab
	 */
	public T put(Position pos, T object) {
		if (pos == null || object == null || pos.getX() > this.dimension.getX() 
				                          || pos.getY() > this.dimension.getY())
			throw new IllegalArgumentException();

		T ret = values[pos.getX()][pos.getY()];
		values[pos.getX()][pos.getY()] = object;
		return ret;
	}

	/**
	 * Gibt das BitSet, welches der spezifizierten Position zugewiesen wurden zurück. Wurde keines zugewiesen, so wird
	 * null zurückgegeben.
	 * 
	 * @param pos
	 *            Die Position, dessen zugewiesenes BitSet abgefragt werden soll
	 * @return Das BitSet, welches der spezifizierten Position zugeordnet wurde oder null, falls keines zugewiesen wurde
	 */
	public T get(Position pos) {
		if (pos == null)
			throw new IllegalArgumentException("pos was null");
		if (pos.getX() > this.dimension.getX())
			throw new IllegalArgumentException("x coordinate of pos was > " + this.dimension.getX() + ": " + pos.getX());
		if (pos.getY() > this.dimension.getY())
			throw new IllegalArgumentException("y coordinate of pos was > " + this.dimension.getY() + ": " + pos.getY());
		assert pos != null;
		assert pos.getX() < this.dimension.getX();
		assert pos.getY() < this.dimension.getY();
		assert pos.getX() >= 0;
		assert pos.getY() >= 0;
		return values[pos.getX()][pos.getY()];
	}

	/**
	 * Gibt eine "deep copy" dieser PositionMap zurück. Es wird dazu die clone-Methode aller in dieser PositionMap
	 * befindlichen Objekte aufgerufen.
	 * 
	 * @return Eine "deep copy" dieser PositionMap
	 */
	@Override
	public PositionMap<T> clone() {
		PositionMap<T> result = new PositionMap<T>(this.dimension);
		for (int i = 0; i < this.dimension.getX(); i++) {
			for (int j = 0; j < this.dimension.getY(); j++) {
				if (this.values[i][j] != null)
					result.put(Position.get(i, j), this.values[i][j]);
			}
		}

		return result;
	}

}
