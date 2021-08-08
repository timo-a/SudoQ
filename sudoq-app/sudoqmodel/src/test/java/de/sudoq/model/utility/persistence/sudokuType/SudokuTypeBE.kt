package de.sudoq.model.utility.persistence.sudokuType

import de.sudoq.model.solverGenerator.solver.helper.Helpers
import de.sudoq.model.sudoku.Constraint
import de.sudoq.model.sudoku.ConstraintType
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.UniqueConstraintBehavior
import de.sudoq.model.sudoku.sudokuTypes.ComplexityConstraintBuilder
import de.sudoq.model.sudoku.sudokuTypes.PermutationProperties
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable
import java.util.*

/*class SudokuTypeBE : Xmlable {

    var enumType: SudokuTypes? = null

    var numberOfSymbols: Int = 0

    var standardAllocationFactor: Float = 0f

    var size: Position? = null

    var blockSize: Position = Position.get(0, 0)

    var constraints: MutableList<Constraint>

    var permutationProperties: List<PermutationProperties>

    var helperList: MutableList<Helpers>

    var ccb: ComplexityConstraintBuilder

    constructor() {
        constraints = ArrayList()
        permutationProperties = SetOfPermutationProperties()
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
        permutationProperties: List<PermutationProperties>,
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
        representation.addChild(size!!.toXmlTree("size"))
        representation.addChild(blockSize.toXmlTree("blockSize"))
        for (c in constraints) {
            representation.addChild(c.toXmlTree())
        }
        representation.addChild((permutationProperties as SetOfPermutationProperties).toXmlTree())
        val hList = XmlTree("helperList")
        for (i in helperList.indices) {
            hList.addAttribute(XmlAttribute("i", "" + helperList[i].ordinal))
        }
        representation.addChild(hList)
        // todo move those tests to app anyway (or mock) representation.addChild(ccb.toXmlTree())

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
                "size" -> size = Position.fillFromXmlStatic(sub)
                "blockSize" -> blockSize = Position.fillFromXmlStatic(sub)
                "constraint" -> {
                    val c = Constraint(UniqueConstraintBehavior(), ConstraintType.LINE)
                    c.fillFromXml(sub)
                    constraints.add(c)
                }
                SetOfPermutationProperties.SET_OF_PERMUTATION_PROPERTIES -> {
                    permutationProperties = SetOfPermutationProperties()
                    (permutationProperties as SetOfPermutationProperties).fillFromXml(sub) //cast neccessary because setOPP is defined as
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
                //title is now in app
                /*ComplexityConstraintBuilder.TITLE -> {
                    ccb = ComplexityConstraintBuilder()
                    ccb.fillFromXml(sub)
                }*/
                else -> {
                }
            }
        }
    }

}*/