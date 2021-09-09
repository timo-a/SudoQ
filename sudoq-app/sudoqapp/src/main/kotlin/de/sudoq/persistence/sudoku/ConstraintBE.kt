package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.ConstraintBehavior
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.UniqueConstraintBehavior
import de.sudoq.persistence.XmlAttribute
import de.sudoq.persistence.XmlTree
import de.sudoq.persistence.Xmlable
import java.util.ArrayList

//Todo instead of dummy values, use an interface with constructor(xt: XmlTree)
class ConstraintBE(var behavior: ConstraintBehavior = UniqueConstraintBehavior(),
                   var type: ConstraintType = ConstraintType.LINE,
                   var name: String = "dummyname",
                   val positions: MutableList<Position> = ArrayList())
    : Xmlable {




    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("constraint")
        representation.addAttribute(XmlAttribute("behavior", behavior.javaClass.toString()))
        representation.addAttribute(XmlAttribute("name", name))
        representation.addAttribute(XmlAttribute("type", "" + type.ordinal))
        for (pos in positions) {
            representation.addChild(PositionMapper.toBE(pos).toXmlTree())
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        val behavior = xmlTreeRepresentation.getAttributeValue("behavior")!!
        if (behavior.contains("Unique")) {
            this.behavior = UniqueConstraintBehavior()
        } else {
            throw IllegalArgumentException("Undefined constraint behavior")
        }
        name = xmlTreeRepresentation.getAttributeValue("name")!!
        type = ConstraintType.values()[xmlTreeRepresentation.getAttributeValue("type")!!.toInt()]
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "position") {
                val positionBE = PositionBE(-1,-1)
                positionBE.fillFromXml(sub)
                addPosition(PositionMapper.fromBE(positionBE))
            }
        }
    }
    /**
     * Adds a [Position] to the constraint if not already there
     * @param position The [Position] to add to this constraint
     */
    private fun addPosition(position: Position) {
        if (position !in positions) {
            positions.add(position)
        }
    }

}