package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.HashMap

class CCBBE(var specimen: MutableMap<Complexity?, ComplexityConstraint> = HashMap())
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
        specimen = HashMap()
        for (sub in xmlTreeRepresentation) {
            val ccBE = ComplexityConstraintBE()
            ccBE.fillFromXml(sub)
            val cc = ComplexityConstraintMapper.fromBE(ccBE)
            specimen[cc.complexity] = cc
        }
    }

    companion object {
        const val TITLE = "ComplexityConstraintBuilder"
    }
}