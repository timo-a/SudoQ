package testutils

import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import de.sudoq.persistence.sudokuType.SudokuTypeBE
import java.io.File

object SudokuTypeProvider {

    fun getType(type: SudokuTypes): SudokuType {

        val typeFile = getSudokuTypeFile(type)
        val typeBE = readSudokuType(typeFile)
        return fromBE(typeBE)
    }

    /**
     * @param type the type enum
     * @return the xml file specifying the type
     */
    private fun getSudokuTypeFile(type: SudokuTypes): File {
        val classLoader = javaClass.classLoader
        return File(classLoader
            !!.getResource("persistence/SudokuTypeRepo/$type.xml")
                .file)
    }

    /**
     * Creates and returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type null is returned.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    private fun readSudokuType(xmlFile: File): SudokuTypeBE {
        if (!xmlFile.exists()) {
            throw IllegalStateException("file does not exist")
        }
        val helper = XmlHelper()
        try {
            val t = SudokuTypeBE()
            val xt = helper.loadXml(xmlFile)!!
            t.fillFromXml(xt)
            return t
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalStateException("Something went wrong loading sudoku type from ${xmlFile.name}")
    }

    fun fromBE(sudokuTypeBE: SudokuTypeBE): SudokuType {
        return SudokuType(
            sudokuTypeBE.enumType!!,
            sudokuTypeBE.numberOfSymbols,
            sudokuTypeBE.standardAllocationFactor,
            sudokuTypeBE.size!!,
            sudokuTypeBE.blockSize,
            sudokuTypeBE.constraints,
            sudokuTypeBE.permutationProperties,
            sudokuTypeBE.helperList,
            sudokuTypeBE.ccb
        )
    }
}