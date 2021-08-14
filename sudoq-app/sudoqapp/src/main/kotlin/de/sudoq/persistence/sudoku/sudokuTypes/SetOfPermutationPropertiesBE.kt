package de.sudoq.persistence.sudoku.sudokuTypes

import de.sudoq.model.sudoku.sudokuTypes.PermutationProperties
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.persistence.Xmlable


class SetOfPermutationPropertiesBE : ArrayList<PermutationProperties>, Xmlable {

    constructor() : super()
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(elements: Collection<PermutationProperties>) : super(elements)

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree(SET_OF_PERMUTATION_PROPERTIES)
        for (p in this) {
            val xt = XmlTree(PERMUTATION_PROPERTY)
            xt.addAttribute(XmlAttribute(TAG_PROPERTY_NR, "" + p.ordinal))
            representation.addChild(xt)
        }
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        for (sub in xmlTreeRepresentation) {
            if (sub.name == PERMUTATION_PROPERTY) {
                add(
                    PermutationProperties.values()[sub.getAttributeValue(TAG_PROPERTY_NR)!!.toInt()]
                )
            }
        }
    }

    companion object {
        const val SET_OF_PERMUTATION_PROPERTIES = "SetOfPermutationProperties"
        private const val PERMUTATION_PROPERTY = "PermutationProperty"
        private const val TAG_PROPERTY_NR = "permutationNr"
    }

}