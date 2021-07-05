/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

import java.io.File
import java.io.IOException

/**
 * This generic class aids in handling the xml representations of classes that implement [Xmlable].
 *
 * @param T the xmlable type
 */
abstract class XmlHandler<T : Xmlable> {

    /** Helper for saving and loading XML files */
    private val helper: XmlHelper = XmlHelper()

    @JvmField
    protected var file: File? = null

    /**
     * Stores the passed [Xmlable] in an xml file.
     *
     * @param obj the [Xmlable] to serialize
     *
     * @throws IllegalArgumentException if saving goes wrong todo throw io exception instead?
     */
    fun saveAsXml(obj: T) {
        try {
            file = getFileFor(obj)
            val tree = obj.toXmlTree()
            modifySaveTree(tree!!)
            helper.saveXml(tree, file!!)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when writing xml", e)
        }
    }

    /**
     * Loads an Xmlable (in place) from an xml file.
     *
     * @param obj empty [Xmlable]
     * @return filled [Xmlable]
     */
    fun createObjectFromXml(obj: T): T {
        try {
            obj.fillFromXml(helper.loadXml(getFileFor(obj))!!)
        } catch (e: IOException) {
            throw IllegalArgumentException(
                "Something went wrong when reading xml " + getFileFor(obj),
                e
            )
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Something went wrong when filling obj from xml ", e)
        }
        return obj
    }

    /**
     * Lets subclasses modify the XMLTree
     *
     * @param tree the [XmlTree] to modify
     */
    protected open fun modifySaveTree(tree: XmlTree) {}

    /**
     * Returns a file that points to the storage place of the given object.
     *
     * @param obj the object for wich to retrieve the file
     * @return the file pointing to the param.
     */
    protected abstract fun getFileFor(obj: T): File

}