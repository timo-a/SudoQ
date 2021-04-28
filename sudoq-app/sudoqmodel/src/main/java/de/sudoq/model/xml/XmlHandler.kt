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
 * Diese Generische Klasse dient zur Handhabung der XML Repräsentation von
 * Objekten, die das Xmlable Interface implementieren.
 *
 * @param <T>
 * der Typ der konkret umgewandelt werden soll
</T> */
abstract class XmlHandler<T : Xmlable?> {
    /* Attributes */
    /**
     * Helfer für das Speichern und Laden von XML Dateien
     */
    private val helper: XmlHelper
    protected var file: File? = null
    /* Methods */
    /**
     * Speichert ein übegebenes Objekt, das das Xmlable Interface implementiert,
     * in eine XML Datei.
     *
     * @param obj
     * Objekt, das Xmlable implementiert
     * @see Xmlable
     *
     * @throws IllegalArgumentException
     * Wird geworfen, falls das übergebene Objekt null ist
     */
    fun saveAsXml(obj: T) {
        try {
            file = getFileFor(obj)
            val tree = obj!!.toXmlTree()
            modifySaveTree(tree)
            helper.saveXml(tree, file)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when writing xml", e)
        }
    }

    /**
     * Lädt ein Objekt, das Xmlable implementiert, aus einer XML Datei.
     *
     * @param obj
     * leeres Objekt, das Xmlable implementiert
     * @return Objekt, welches Xmlable implementiert
     * @see Xmlable
     */
    fun createObjectFromXml(obj: T): T {
        try {
            obj!!.fillFromXml(helper.loadXml(getFileFor(obj)))
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when reading xml " + getFileFor(obj), e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Something went wrong when filling obj from xml ", e)
        }
        return obj
    }

    /**
     * Lässt Subklassen den XMLTree falls nötig anpassen
     *
     * @param tree
     * der Ursprüngliche Baum
     */
    protected open fun modifySaveTree(tree: XmlTree?) {}

    /**
     * Gibt ein File welches auf den Speicherort des gegebenen Objektes zeigt
     * zurueck
     *
     * @param obj
     * das zu speichernde/ladende Objekt
     * @return das darauf zeigende File
     */
    abstract fun getFileFor(obj: T): File
    /* Constructors */ /**
     * Dieser Konstruktor initialisiert einen neuen XmlHandler.
     */
    init {
        helper = XmlHelper()
    }
}