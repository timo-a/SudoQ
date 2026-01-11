package de.sudoq.persistence.sudokyType

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import de.sudoq.persistence.sudokuType.SudokuTypeBE
import de.sudoq.persistence.sudokuType.SudokuTypeMapper
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

class DeserializationTests {

    val directory = getFromResourceDirectory()

    private fun getFromResourceDirectory(): File {
        return File("src/main/assets/sudokus")
    }

    fun getSudokuTypeFile(type: SudokuTypes): File {
        return File(directory.absolutePath + File.separator + type.toString() + File.separator + type.toString() + ".xml")
    }

    @ParameterizedTest
    @EnumSource(value=SudokuTypes::class)
    fun `should deserialize`(type: SudokuTypes) {
        val file = getSudokuTypeFile(type)
        file `should not be` null
        file.exists() `should be` true

        val helper = XmlHelper()
        val be = SudokuTypeBE()
        val xt = helper.loadXml(file)!!
        be.fillFromXml(xt)
        val type = SudokuTypeMapper.fromBE(be)

        //round 2
        val tree2 = SudokuTypeMapper.toBE(type).toXmlTree()
        //todo write to virtual file
        val be2 = SudokuTypeBE()
        be2.fillFromXml(tree2)
        val serdered = SudokuTypeMapper.fromBE(be2)
        //serdered `should be equal to` type todo equals definieren
        serdered.enumType `should be equal to` type.enumType
        serdered.numberOfSymbols `should be equal to` type.numberOfSymbols
        serdered.getStandardAllocationFactor() `should be equal to` type.getStandardAllocationFactor()
        serdered.size `should be equal to` type.size
        serdered.blockSize `should be equal to` type.blockSize
        serdered.permutationProperties `should be equal to` type.permutationProperties
        serdered.helperList `should be equal to` type.helperList
        //serdered.ccb `should be equal to` type.ccb
        //serdered.constraints `should be equal to` type.constraints
        serdered.constraints.size `should be equal to` type.constraints.size
    }
}
