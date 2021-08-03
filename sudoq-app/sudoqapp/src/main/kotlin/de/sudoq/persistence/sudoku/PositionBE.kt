package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.Position
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable

class PositionBE(var x: Int, var y: Int): Xmlable {

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("position")
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