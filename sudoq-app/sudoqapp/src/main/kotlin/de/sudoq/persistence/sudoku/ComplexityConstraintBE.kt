package de.sudoq.persistence.sudoku

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable

class ComplexityConstraintBE(
    var complexity: Complexity = Complexity.arbitrary,
    var averageCells: Int = 0,
    var minComplexityIdentifier: Int = 0,
    var maxComplexityIdentifier: Int = 0,
    var numberOfAllowedHelpers: Int = 0
) : Xmlable {

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree(COMPLEXITY_CONSTRAINT)
        representation.addAttribute(XmlAttribute(COMPLEXITY, "" + complexity.ordinal))
        representation.addAttribute(XmlAttribute(AVERAGE_FIELDS, "" + averageCells))
        representation.addAttribute(
            XmlAttribute(MIN_COMPLEXITY_IDENTIFIER, "" + minComplexityIdentifier)
        )
        representation.addAttribute(
            XmlAttribute(MAX_COMPLEXITY_IDENTIFIER, "" + maxComplexityIdentifier)
        )
        representation.addAttribute(
            XmlAttribute(NUMBER_OF_ALLOWED_HELPERS, "" + numberOfAllowedHelpers)
        )
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        complexity =
            Complexity.values()[xmlTreeRepresentation.getAttributeValue(COMPLEXITY)!!.toInt()]
        averageCells = xmlTreeRepresentation.getAttributeValue(AVERAGE_FIELDS)!!.toInt()
        minComplexityIdentifier =
            xmlTreeRepresentation.getAttributeValue(MIN_COMPLEXITY_IDENTIFIER)!!.toInt()
        maxComplexityIdentifier =
            xmlTreeRepresentation.getAttributeValue(MAX_COMPLEXITY_IDENTIFIER)!!.toInt()
        numberOfAllowedHelpers =
            xmlTreeRepresentation.getAttributeValue(NUMBER_OF_ALLOWED_HELPERS)!!.toInt()
    }


    companion object {
        const val COMPLEXITY_CONSTRAINT = "complexityConstraint"
        private const val COMPLEXITY = "complexity"
        private const val AVERAGE_FIELDS = "averageFields"
        private const val MIN_COMPLEXITY_IDENTIFIER = "minComplexity"
        private const val MAX_COMPLEXITY_IDENTIFIER = "maxComplexity"
        private const val NUMBER_OF_ALLOWED_HELPERS = "numberOfAllowedHelpers"
    }
}