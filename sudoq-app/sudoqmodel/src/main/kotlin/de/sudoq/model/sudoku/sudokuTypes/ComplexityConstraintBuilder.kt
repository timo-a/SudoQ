package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*

/**
 * Builder for Complexity constraints.
 */
class ComplexityConstraintBuilder : Xmlable {
    var specimen: MutableMap<Complexity?, ComplexityConstraint> = HashMap()

    constructor() {}
    constructor(specimen: MutableMap<Complexity?, ComplexityConstraint>) {
        this.specimen = specimen
    }

    fun getComplexityConstraint(complexity: Complexity?): ComplexityConstraint? {
        return if (complexity != null && specimen.containsKey(complexity)) {
            specimen[complexity]!!.clone() as ComplexityConstraint
        } else null
    }

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree(TITLE)
        for (v in specimen.values) {
            representation.addChild(v.toXmlTree())
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        specimen = HashMap()
        for (sub in xmlTreeRepresentation) {
            val cc = ComplexityConstraint()
            cc.fillFromXml(sub)
            specimen[cc.complexity] = cc
        }
    }

    companion object {
        const val TITLE = "ComplexityConstraintBuilder"
    }
}