package de.sudoq.persistence.sudokuType

import de.sudoq.model.solverGenerator.solver.helper.Helpers
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.persistence.Xmlable
import de.sudoq.persistence.sudoku.*
import de.sudoq.persistence.sudoku.sudokuTypes.SetOfPermutationPropertiesBE
import java.util.*

class SudokuTypeBE : Xmlable {

    var enumType: SudokuTypes? = null

    var numberOfSymbols: Int = 0

    var standardAllocationFactor: Float = 0f

    var size: Position? = null

    var blockSize: Position = Position.get(0, 0)

    var constraints: MutableList<Constraint>

    var permutationProperties: SetOfPermutationPropertiesBE

    var helperList: MutableList<Helpers>

    var ccb: ComplexityConstraintBuilder

    constructor() {
        constraints = ArrayList()
        permutationProperties = SetOfPermutationPropertiesBE()
        helperList = ArrayList()
        ccb = ComplexityConstraintBuilder()
    }


    constructor(
        enumType: SudokuTypes,
        numberOfSymbols: Int,
        standardAllocationFactor: Float,
        size: Position,
        blockSize: Position,
        constraints: MutableList<Constraint>,
        permutationProperties: SetOfPermutationPropertiesBE,
        helperList: MutableList<Helpers>,
        ccb: ComplexityConstraintBuilder
    ) {
        this.enumType = enumType
        this.numberOfSymbols = numberOfSymbols
        this.standardAllocationFactor = standardAllocationFactor
        this.size = size
        this.blockSize = blockSize
        this.constraints = constraints
        this.permutationProperties = permutationProperties
        this.helperList = helperList
        this.ccb = ccb
    }

    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("sudokutype")
        representation.addAttribute(XmlAttribute("typename", "" + enumType!!.ordinal))
        representation.addAttribute(XmlAttribute("numberOfSymbols", "" + numberOfSymbols))
        representation.addAttribute(
            XmlAttribute(
                "standardAllocationFactor",
                standardAllocationFactor.toString()
            )
        )
        representation.addChild(PositionMapper.toBE(size!!).toXmlTree("size"))
        representation.addChild(PositionMapper.toBE(blockSize).toXmlTree("blockSize"))
        for (c in constraints) {
            representation.addChild(ConstraintMapper.toBE(c).toXmlTree())
        }
        representation.addChild(permutationProperties.toXmlTree())
        val hList = XmlTree("helperList")
        for (i in helperList.indices) {
            hList.addAttribute(XmlAttribute("i", "" + helperList[i].ordinal))
        }
        representation.addChild(hList)
        representation.addChild(CCBMapper.toBE(ccb).toXmlTree())

        // TODO complexity builderdata
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        enumType =
            SudokuTypes.values()[xmlTreeRepresentation.getAttributeValue("typename")!!.toInt()]
        numberOfSymbols = xmlTreeRepresentation.getAttributeValue("numberOfSymbols")!!.toInt()
        standardAllocationFactor =
            xmlTreeRepresentation.getAttributeValue("standardAllocationFactor")!!.toFloat()
        for (sub in xmlTreeRepresentation) {
            when (sub.name) {
                "size" -> size = PositionBE.fillFromXmlStatic(sub)
                "blockSize" -> blockSize = PositionBE.fillFromXmlStatic(sub)
                "constraint" -> {
                    val cBE = ConstraintBE()
                    cBE.fillFromXml(sub)
                    val c = ConstraintMapper.fromBE(cBE)
                    constraints.add(c)
                }
                SetOfPermutationPropertiesBE.SET_OF_PERMUTATION_PROPERTIES -> {
                    permutationProperties = SetOfPermutationPropertiesBE()
                    permutationProperties.fillFromXml(sub)
                }
                "helperList" -> {
                    helperList = ArrayList(sub.numberOfAttributes)
                    val jterator = sub.getAttributes()
                    while (jterator.hasNext()) {
                        val xa = jterator.next()
                        val index = xa.name.toInt()
                        val h = Helpers.values()[xa.value.toInt()]
                        helperList[index] = h
                    }
                }
                CCBBE.TITLE -> {
                    var ccbbe = CCBBE()
                    ccbbe.fillFromXml(sub)
                    ccb = CCBMapper.fromBE(ccbbe)
                }
                else -> {
                }
            }
        }
    }

}