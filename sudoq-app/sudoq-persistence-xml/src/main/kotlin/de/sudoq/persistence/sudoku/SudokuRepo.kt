package de.sudoq.persistence.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.persistence.XmlHelper
import java.io.File
import java.io.IOException

class SudokuRepo(
    private val outerSudokusDir: File,
    type: SudokuTypes,
    complexity: Complexity,
    private val sudokuTypeRepo: IRepo<SudokuType>
) : IRepo<Sudoku> {

    private val sudokusDir: File = getSudokuDir(type, complexity)

    constructor(sudokusDir: File, sudoku: Sudoku, sudokuTypeRepo: IRepo<SudokuType>) : this(
        sudokusDir,
        sudoku.sudokuType.enumType,
        sudoku.complexity!!,
        sudokuTypeRepo
    )

    private val helper: XmlHelper = XmlHelper()


    override fun create(): Sudoku {
        val sudokuBE = SudokuBE()
        sudokuBE.id = getFreeSudokuId()
        val file = getSudokuFile(sudokuBE.id)

        try {
            val tree = sudokuBE.toXmlTree()
            helper.saveXml(tree, file)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when writing xml", e)
        }
        return read(sudokuBE.id)
    }

    private fun getSudokuFile(id: Int): File {
        return File(sudokusDir.absolutePath + File.separator + "sudoku_$id.xml")
    }

    override fun read(id: Int): Sudoku {
        val obj = SudokuBE()
        val file = getSudokuFile(id)

        try {
            obj.fillFromXml(helper.loadXml(file)!!, sudokuTypeRepo)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when reading xml $file", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Something went wrong when filling obj from xml ", e)
        }
        return SudokuMapper.fromBE(obj)

    }

    override fun update(t: Sudoku): Sudoku {
        val file = File(
            getSudokuDir(
                t.sudokuType.enumType,
                t.complexity!!
            ).absolutePath + File.separator + "sudoku_" + t.id + ".xml"
        )

        try {
            val tree = SudokuMapper.toBE(t).toXmlTree()
            helper.saveXml(tree, file)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when writing xml", e)
        }
        return read(t.id)
    }

    fun delete(t: Sudoku) {
        val f = File(sudokusDir.absolutePath, "sudoku_${t.id}.xml")
        f.delete()
    }

    override fun delete(id: Int) {
        val f = File(sudokusDir.absolutePath, "sudoku_${id}.xml")
        f.delete()
    }

    override fun ids(): List<Int> {
        return sudokusDir.list().map { it.substring(7, it.length - 4).toInt() }
    }

    /**
     * Gibt den die Sudokus mit den gegebenen Parametern enthaltennden Ordner
     * zurueck
     *
     * @param type
     * der Typ des Sudokus
     * @param complexity
     * die Schwierigkeit des Sudokus
     * @return der Ordner
     */
    private fun getSudokuDir(type: SudokuTypes, complexity: Complexity): File {
        return File(outerSudokusDir.absolutePath + File.separator + type.toString() + File.separator + complexity.toString())
    }

    /**
     * Gibt die nächste verfügbare Sudoku ID zurück
     *
     * @return nächste verfügbare Sudoku ID
     */
    private fun getFreeSudokuId(): Int {
        val numbers = ids()
        return generateSequence(1) { it + 1 }.first { !numbers.contains(it) }
    }


}