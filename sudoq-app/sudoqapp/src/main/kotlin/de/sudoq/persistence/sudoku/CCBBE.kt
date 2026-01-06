package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.persistence.XmlTree
import de.sudoq.persistence.Xmlable
import java.util.HashMap

class CCBBE(var specimen: Map<Complexity, ComplexityConstraint> = HashMap())
    : Xmlable {

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree(TITLE)
        for (v in specimen.values) {
            representation.addChild(ComplexityConstraintMapper.toBE(v).toXmlTree())
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        specimen = xmlTreeRepresentation.associate {
            val ccBE = ComplexityConstraintBE()
            ccBE.fillFromXml(it)
            val cc = ComplexityConstraintMapper.fromBE(ccBE)
            cc.complexity to cc
        }
    }

    companion object {
        const val TITLE = "ComplexityConstraintBuilder"
    }
}