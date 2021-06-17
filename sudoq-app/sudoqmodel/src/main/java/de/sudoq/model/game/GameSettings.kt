/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game

import de.sudoq.model.xml.SudokuTypesList
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*
import kotlin.math.pow

/**
 * This class holds all settings concerning a [Game]:
 * - a set of [Assistances], i.e. their availability.
 * - additional options like lefthandmode, hints...
 */
open class GameSettings : Xmlable {

    /**
     * A BitSet representing available [Assistances]
     */
    private val assistances: BitSet = BitSet()

    var isLefthandModeSet = false
        private set

    var isHelperSet = false
        private set

    var isGesturesSet = false
        private set

    val wantedTypesList: SudokuTypesList = SudokuTypesList()

    /**
     * Sets an assistance to true
     *
     * @param assistance The assistance to set
     */
    fun setAssistance(assistance: Assistances) {
        assistances.set(
            2.0.pow((assistance.ordinal + 1).toDouble()).toInt()
        ) //TODO that looks wrong...
    }

    /**
     * Sets an assistance as unavailable.
     *
     * @param assistance The assistance to set
     */
    fun clearAssistance(assistance: Assistances) {
        assistances.clear(2.0.pow((assistance.ordinal + 1).toDouble()).toInt())
    }

    /**
     * Checks if an assistance is set
     *
     * @param assistance [Assistances] to check
     * @return true, if assistance is set, false otherwise
     */
    open fun getAssistance(assistance: Assistances): Boolean {
        return assistances[2.0.pow((assistance.ordinal + 1).toDouble()).toInt()]
    }

    /* additional settings */
    fun setGestures(value: Boolean) {
        isGesturesSet = value
    }

    fun setLefthandMode(value: Boolean) {
        isLefthandModeSet = value
    }

    fun setHelper(value: Boolean) {
        isHelperSet = value
    }

    /* to and from string */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("gameSettings")
        representation.addAttribute(
            XmlAttribute(
                "assistances",
                convertAssistancesToString()
            )
        ) //TODO scrap that, representation as 0,1 is ugly -> save all with name, then make all of the boolean assistances enums
        representation.addAttribute(XmlAttribute("gestures", isGesturesSet))
        representation.addAttribute(XmlAttribute("left", isLefthandModeSet))
        representation.addAttribute(XmlAttribute("helper", isHelperSet))
        representation.addChild(wantedTypesList.toXmlTree())
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        assistancesfromString(xmlTreeRepresentation.getAttributeValue("assistances")!!)
        isGesturesSet =
            java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("gestures"))
        isLefthandModeSet =
            java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("left"))
        isHelperSet =
            java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("helper"))
        for (xt in xmlTreeRepresentation) if (xt.name == SudokuTypesList.ROOT_NAME) wantedTypesList.fillFromXml(
            xt
        )
    }

    /**
     * Generates a String of "0" and "1" from the AssistanceSet.
     * The String car be parsed again with [assistancesfromString].
     *
     * @return String representation of the AssistanceSet
     */
    private fun convertAssistancesToString(): String {
        val bitstring = StringBuilder()
        for (assist in Assistances.values()) bitstring.append(if (getAssistance(assist)) "1" else "0")
        return bitstring.toString()
    }

    /**
     * Reads the Assistance set from a String of "0" and "1"s
     *
     * @param representation String representation of the assistances
     *
     * @throws IllegalArgumentException on parse error
     */
    @Throws(IllegalArgumentException::class)
    private fun assistancesfromString(representation: String) {

        for ((i, assist) in Assistances.values().withIndex()) {
            try {
                if (representation[i] == '1') {
                    setAssistance(assist)
                }
            } catch (exc: Exception) {
                throw IllegalArgumentException()
            }
        }
    }

}