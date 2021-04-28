/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

import java.util.*

/**
 * Diese Klasse repräsentiert eine zu Xml kompatible Baumstruktur
 */
class XmlTree(name: String?) : Iterable<XmlTree?> {
    /* Attributes */
    /**
     * Diese Methode gibt des Namen der Wurzel des Baumes zurück.
     *
     * @return String Name der Wurzel
     */
    /**
     * Name des Xml Objektes
     */
    val name: String
    /**
     * Diese Methode gibt den Inhalt des Xml Objektes zurück.
     *
     * @return String Name der Wurzel
     */
    /**
     * Inhalt des Xml Objektes
     */
    var content: String
        private set
    /**
     * Gibt die Anzahl der Unterobjekte des Xml Objekts zurück.
     *
     * @return Anzahl der Sub-Objekte
     */
    /**
     * Anzahl der Unterobjekte des Xml Objektes
     */
    var numberOfChildren: Int
        private set

    /**
     * Menge der Attribute dieses Xml Objekts
     */
    private val attributes: MutableList<XmlAttribute>

    /**
     * Menge der Unterobjekte dieses Xml Objekts
     */
    private val children: MutableList<XmlTree>

    /**@param name
     * Name der Wurzel Xml Baumes
     * @param content
     * Inhalt des Xml Objektes
     * @throws IllegalArgumentException
     * Wird geworfen, falls einer der übergebenen Strings null oder
     * der name leer ist
     */
    @Deprecated("""content can not be read apparently.
	  Dieser Konstruktor initialisiert einen neuen Xml Baum Objekt mit
	  gegebenem Namen und leerem Inhalt.
	  
	  """)
    constructor(name: String?, content: String?) : this(name) {
        requireNotNull(content)
        this.content = content
    }
    /* Methods */
    /**
     * Diese Methode gibt den Wert des Attributes mit dem angegebenen Namen oder
     * null, falls dieses nicht existiert, zurück.
     *
     * @param name
     * Name des Attributes dessen Wert erfragt wird.
     * @return Wert des Attributes oder null, falls das Attribut nicht
     * existiert.
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene Name null ist
     */
    @Throws(IllegalArgumentException::class)
    fun getAttributeValue(name: String?): String? {
        requireNotNull(name)
        for (attribute in attributes) {
            if (attribute.name == name) {
                return attribute.value
            }
        }
        return null
    }

    /**
     * Diese Methode gibt eine Liste alle Unterobjekte des Xml Baumes zurück.
     *
     * @return Liste aller Unterobjekte oder null, falls keine Unterobjekte
     * existieren
     */
    fun getChildren(): MutableIterator<XmlTree> {
        return children.iterator()
    }

    /**
     * Diese Methode gibt eine Liste aller Attribute des Xml Baumes zurück.
     *
     * @return Liste aller Attribute oder null, falls keine Attribute existieren
     */
    fun getAttributes(): Iterator<XmlAttribute> {
        return attributes.iterator()
    }

    /* zum bequem drüberiterieren*/
    val attributes2: Iterable<XmlAttribute>
        get() = AttributesIterator()

    /**
     * Diese Methode fügt ein Unterobjekt an diesen Xml Baum an.
     *
     * @param child
     * Anzufügendes Unterobjekt
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene XmlTree null ist
     */
    @Throws(IllegalArgumentException::class)
    fun addChild(child: XmlTree?) {
        requireNotNull(child)
        children.add(child)
        numberOfChildren++
    }

    /**
     * Diese Methode fügt dem Xml Objekt ein Attribut hinzu, falls kein Attribut
     * dieses Namens existiert.
     *
     * @param attribute
     * Anzfügendes Attribut
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene XmlTree null ist
     */
    @Throws(IllegalArgumentException::class)
    fun addAttribute(attribute: XmlAttribute?) {
        requireNotNull(attribute)
        for (attr in attributes) {
            if (attr.isSameAttribute(attribute)) {
                return
            }
        }
        attributes.add(attribute)
    }

    /**
     * Setzt oder updatetet das gegebene Attribut. Ist es null passiert nichts
     *
     * @param attribute
     * das zu setzende Attribut
     */
    fun updateAttribute(attribute: XmlAttribute?) {
        if (attribute != null) {
            for (attr in attributes) {
                if (attr.isSameAttribute(attribute)) {
                    attr.value = attribute.value
                    return
                }
            }
            attributes.add(attribute)
        }
    }

    /**
     * Gibt die Anzahl der Attribute des Xml Objekts zurück.
     *
     * @return Anzahl der Attribute
     */
    val numberOfAttributes: Int
        get() = attributes.size

    /**
     * {@inheritDoc}
     */
    override fun iterator(): MutableIterator<XmlTree> {
        return getChildren()
    }

    inner class AttributesIterator : Iterable<XmlAttribute?> {
        override fun iterator(): MutableIterator<XmlAttribute> {
            return attributes.iterator()
        }
    }
    /* Constructors */ /**
     * Dieser Konstruktor initialisiert einen neuen Xml Baum Objekt mit
     * gegebenem Namen und leerem Inhalt.
     *
     * @param name
     * Name der Wurzel Xml Baumes
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene String null oder leer ist
     */
    init {
        require(!(name == null || name == ""))
        this.name = name
        content = ""
        attributes = ArrayList()
        children = ArrayList()
        numberOfChildren = 0
    }
}