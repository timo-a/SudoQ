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
 * This class represents a tree that models an xml structure
 */
class XmlTree(name: String) : Iterable<XmlTree> {

    /** Name of the Xml tree (root) */
    val name: String

    /** content of the Xml tree */
    var content: String
        private set

    /** Number of child nodes. */
    var numberOfChildren: Int //todo redundant since we can just query the list object
        private set

    /** List of Attributes in this node */
    private val attributes: MutableList<XmlAttribute>

    /** List of child nodes */
    private val children: MutableList<XmlTree>

    init {
        require(name != "")
        this.name = name
        content = ""
        attributes = ArrayList()
        children = ArrayList()
        numberOfChildren = 0
    }

    @Deprecated("content can not be read apparently.")
    constructor(name: String, content: String) : this(name) {
        this.content = content
    }

    /**
     * Retrieves an attribute value by name.
     *
     * @param name Name of the attribute.
     * @return Value of the attribute or null if not found.
     */
    fun getAttributeValue(name: String): String? {
        return attributes.firstOrNull { it.name == name }?.value
    }

    /**
     * Returns a list of all child nodes of this node.
     *
     * @return a list of all child nodes.
     */
    fun getChildren(): MutableIterator<XmlTree> {
        return children.iterator()
    }

    /**
     * Returns a list of all attributes of this node
     *
     * @return a list of all attributes of this node
     */
    fun getAttributes(): Iterator<XmlAttribute> {
        return attributes.iterator()
    }

    /* for convenient iteration todo check if still needed */
    val attributes2: Iterable<XmlAttribute>
        get() = AttributesIterator()

    /**
     * {@inheritDoc}
     */
    override fun iterator(): MutableIterator<XmlTree> {
        return getChildren()
    }

    inner class AttributesIterator : Iterable<XmlAttribute> {
        override fun iterator(): Iterator<XmlAttribute> {
            return attributes.iterator()
        }
    }


    /**
     * Adds a child node to the tree
     *
     * @param child [XmlTree] to add
     */
    fun addChild(child: XmlTree) {
        children.add(child)
        numberOfChildren++
    }

    /**
     * Adds an attribute.
     *
     * @param attribute [XmlAttribute] to add
     */
    fun addAttribute(attribute: XmlAttribute) {
        if (attributes.none { it.isSameAttribute(attribute) })
            attributes.add(attribute)
    }

    /**
     * Sets or updates a given attribute
     * If it is null nothing happens.
     *
     * @param attribute [XmlAttribute] to update
     */
    fun updateAttribute(attribute: XmlAttribute?) {//todo can we require nonnull?
        if (attribute != null) {
            var existingAttribute = attributes.firstOrNull { it.isSameAttribute(attribute) }

            if (existingAttribute != null)
                existingAttribute.value = attribute.value
            else
                attributes.add(attribute)
        }
    }

    /**
     * Returns the number of attributes of this node.
     *
     * @return the number of attributes of this node.
     */
    val numberOfAttributes: Int
        get() = attributes.size

}