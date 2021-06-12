package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.xml.XmlHelper
import java.io.File

object SudokuTypeProvider {



    /**
     * Gibt die Sudoku-Typdatei für den spezifizierten Typ zurück.
     * @param type die Typ-Id
     * @return die entsprechende Sudoku-Typdatei
     */
    private fun getSudokuTypeFile(type: SudokuTypes, sudokuDir: File): File {
        //getDir(getString(R.string.path_rel_sudokus), MODE_PRIVATE)
        val ap = sudokuDir.absolutePath
        return File(ap + File.separator + type.toString() + File.separator + type.toString() + ".xml")
    }

    /**
     * Creates and returns a SudokuType subject to the specified Type Name.
     * If the type cannot be mapped to a type null is returned.
     *
     * @param type Enum Type of the SudokuType to create.
     * @return a [SudokuType] of null if type cannot be mapped
     */
    @JvmStatic //todo get a cleaner solution with repo?
    fun getSudokuType(type: SudokuTypes, sudokuDir: File): SudokuType? {
        val f = getSudokuTypeFile(type, sudokuDir)
        if (!f.exists()) {
            return null
        }
        val helper = XmlHelper()
        try {
            val t = SudokuType()
            val xt = helper.loadXml(f)!!
            t.fillFromXml(xt)
            return t
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}