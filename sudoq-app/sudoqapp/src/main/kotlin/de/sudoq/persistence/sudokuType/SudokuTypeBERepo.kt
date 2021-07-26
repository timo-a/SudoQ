package de.sudoq.persistence.sudokuType

import de.sudoq.model.persistence.IRepo
import de.sudoq.persistence.sudokuType.SudokuTypeBE
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlHelper
import java.io.File

class SudokuTypeBERepo(private val sudokuDir: File) : IRepo<SudokuTypeBE> {
    override fun create(): SudokuTypeBE {
        TODO("Not yet implemented")
    }

    override fun read(id: Int): SudokuTypeBE {
        val st: SudokuTypes = SudokuTypes.values()[id]
        return getSudokuType(st)
    }

    /**
     * Gibt die Sudoku-Typdatei für den spezifizierten Typ zurück.
     * @param type die Typ-Id
     * @return die entsprechende Sudoku-Typdatei
     */
    private fun getSudokuTypeFile(type: SudokuTypes): File {
        return File(sudokuDir.absolutePath + File.separator + type.toString() + File.separator + type.toString() + ".xml")
    }

    /**
     * Creates and returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type null is returned.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    private fun getSudokuType(type: SudokuTypes): SudokuTypeBE {
        val f = getSudokuTypeFile(type)
        if (!f.exists()) {
            throw IllegalStateException("no sudoku type file found for $type")
        }
        val helper = XmlHelper()
        try {
            val t = SudokuTypeBE()
            val xt = helper.loadXml(f)!!
            t.fillFromXml(xt)
            return t
        } catch (e: Exception) {
            e.printStackTrace()
        }
        throw IllegalStateException("Something went wrong loading sudoku type for $type")
    }


    override fun update(t: SudokuTypeBE): SudokuTypeBE {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }

    override fun ids(): List<Int> {
        TODO("Not yet implemented")
    }


}