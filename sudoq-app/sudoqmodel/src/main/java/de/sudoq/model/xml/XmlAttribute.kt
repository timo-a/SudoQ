/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

/**
 * Diese Klasse repräsentiert ein Attribut eines Xml Baumes
 *
 * @see XmlTree
 */
class XmlAttribute(name: String?, value: String?) {
    /* Attributes */
    /**
     * Diese Methode gibt des Namen des Attributes zurück.
     *
     * @return String Name des Attributes
     */
    /**
     * Name des Attributes
     */
    val name: String

    /**
     * Wert des Xml Attributes
     */
    private var value: String

    constructor(name: String?, value: Boolean) : this(name, "" + value) {}
    /* Methods */
    /**
     * Diese Methode gibt den Wert des Attributes zurück.
     *
     * @return String Wert des Attributes
     */
    fun getValue(): String {
        return value
    }

    override fun toString(): String {
        return "$name: $value"
    }

    /**
     * Setzt falls der Parameter nicht null ist value auf diesen
     *
     * @param value
     * der einzutragende Wert
     */
    protected fun setValue(value: String?) {
        if (value != null) {
            this.value = value
        }
    }

    /**
     * Diese Methode prüft ob ein weiteres Attribut des selben Typs ist, also
     * den gleichen Namen tragen
     *
     * @param attribute
     * Das zu vergleichende Attribut
     * @return True, wenn beide den selben Namen haben, sonst false
     * @throws IllegalArgumentException
     * Wird geworfen, falls das übergebene Attribut null ist
     */
    @Throws(IllegalArgumentException::class)
    fun isSameAttribute(attribute: XmlAttribute?): Boolean {
        requireNotNull(attribute)
        return attribute.name == name
    }
    /* Constructors */ /**
     * Dieser Konstruktor initialisiert ein neues Attribut mit gegebenem Wert.
     *
     * @param name
     * Name des Attributes
     * @param value
     * Wert des Attributes
     * @throws IllegalArgumentException
     * Wird geworfen, falls einer der übergebenen Strings null oder
     * der name leer ist
     */
    init {
        require(!(name == null || name == "" || value == null))
        this.name = name
        this.value = value
    }
}