/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku

/**
 * Diese Klasse stellt eine Map von Positions auf generische Objekte zur Verfügung. Da das Mapping direkt über die
 * 2D-Koordinaten der Positions vorgenommen wird, ist dieses Mapping effizienter als HashMaps oder TreeMaps.
 *
 * @param <T>
 * Ein belibiger Typ, auf den Positions abgebildet werden sollen
</T> */
class PositionMap<T>(dimension: Position?) : Cloneable {
    /**
     * Die BoundingBox-Abmessungen der Positionen dieser PositionMap
     */
    var dimension: Position

    /**
     * Das Werte-Array dieser PositionMap
     */
    var values: Array<Array<T>>

    /**
     * Fügt das spezifizierte Objekt an der spezifizierten Position ein. Ist dort bereits ein Objekt vorhanden, so wird
     * dieses überschrieben.
     *
     * @param pos
     * Die Position, an der das BitSet eingefügt werden soll
     * @param object
     * Das Objekt, welches eingefügt werden soll
     * @return der Wert der vorher an pos lag, oder null falls es keinen gab
     */
    fun put(pos: Position?, `object`: T?): T {
        require(!(pos == null || `object` == null || pos.x > dimension.x || pos.y > dimension.y))
        val ret = values[pos.x][pos.y]
        values[pos.x][pos.y] = `object`
        return ret
    }

    /**
     * Gibt das BitSet, welches der spezifizierten Position zugewiesen wurden zurück. Wurde keines zugewiesen, so wird
     * null zurückgegeben.
     *
     * @param pos
     * Die Position, dessen zugewiesenes BitSet abgefragt werden soll
     * @return Das BitSet, welches der spezifizierten Position zugeordnet wurde oder null, falls keines zugewiesen wurde
     */
    operator fun get(pos: Position?): T {
        requireNotNull(pos) { "pos was null" }
        require(pos.x <= dimension.x) { "x coordinate of pos was > " + dimension.x + ": " + pos.x }
        require(pos.y <= dimension.y) { "y coordinate of pos was > " + dimension.y + ": " + pos.y }
        assert(pos.x < dimension.x)
        assert(pos.y < dimension.y)
        assert(pos.x >= 0)
        assert(pos.y >= 0)
        return values[pos.x][pos.y]
    }

    /**
     * Gibt eine "deep copy" dieser PositionMap zurück. Es wird dazu die clone-Methode aller in dieser PositionMap
     * befindlichen Objekte aufgerufen.
     *
     * @return Eine "deep copy" dieser PositionMap
     */
    public override fun clone(): PositionMap<T> {
        val result = PositionMap<T>(dimension)
        for (i in 0 until dimension.x) {
            for (j in 0 until dimension.y) {
                if (values[i][j] != null) result.put(Position[i, j], values[i][j])
            }
        }
        return result
    }

    /**
     * Initialisiert eine neue Position-Map für so viele Einträge, wie eine Matrix der spezifizierten Dimension hat. Die
     * Größe muss in beiden Komponenten mindestens 1 sein.
     *
     * @param dimension
     * Die Größe der PositionMap
     * @throws IllegalArgumentException
     * Wird geworfen, falls die spezifizierte Position null oder eine der Dimensionskomponenten 0 ist
     */
    init {
        require(!(dimension == null || dimension.x < 1 || dimension.y < 1)) { "Specified dimension or one of its components was null." }
        this.dimension = dimension
        values = Array(dimension.x) { arrayOfNulls<Any>(dimension.y) } as Array<Array<T?>>
    }
}