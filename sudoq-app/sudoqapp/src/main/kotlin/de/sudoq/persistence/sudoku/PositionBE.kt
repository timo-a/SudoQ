package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.Position
import de.sudoq.persistence.XmlAttribute
import de.sudoq.persistence.XmlTree
import de.sudoq.persistence.Xmlable

class PositionBE(var x: Int, var y: Int): Xmlable {

    override fun toXmlTree(): XmlTree {
        return toXmlTree("position")
    }

    fun toXmlTree(name: String): XmlTree {
        val representation = XmlTree(name)
        representation.addAttribute(XmlAttribute("x", "" + x))
        representation.addAttribute(XmlAttribute("y", "" + y))
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        //require(!fixed) { "Tried to manipulate a fixed position" }
        x = xmlTreeRepresentation.getAttributeValue("x")!!.toInt()
        y = xmlTreeRepresentation.getAttributeValue("y")!!.toInt()
    }

    companion object {

        fun fillFromXmlStatic(xmlTreeRepresentation: XmlTree): Position {
            return Position[
                    xmlTreeRepresentation.getAttributeValue("x")!!.toInt(),
                    xmlTreeRepresentation.getAttributeValue("y")!!.toInt()]
        }

    }
}