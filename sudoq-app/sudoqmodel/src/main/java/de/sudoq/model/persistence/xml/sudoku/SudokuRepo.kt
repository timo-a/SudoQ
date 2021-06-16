package de.sudoq.model.persistence.xml.sudoku

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import de.sudoq.model.xml.XmlHelper
import java.io.File
import java.io.IOException
import java.util.*

//todo parametrize, so that an instance is per type, complexity
class SudokuRepo(private val outerSudokusDir: File,
                 type: SudokuTypes,
                 complexity: Complexity) : IRepo<SudokuBE> {

    private val sudokusDir : File = getSudokuDir(type, complexity)
    private val type: SudokuTypes = type
    private val complexity: Complexity = complexity

    constructor(sudokusDir: File, sudoku: Sudoku) : this(sudokusDir, sudoku.sudokuType!!.enumType!!, sudoku.complexity!!)
    constructor(sudokusDir: File, sudoku: SudokuBE) : this(sudokusDir, sudoku.sudokuType!!.enumType!!, sudoku.complexity!!)

    private val helper: XmlHelper = XmlHelper()



    override fun create(): SudokuBE {
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

    private fun getSudokuFile(id: Int) : File {
        return File(sudokusDir.absolutePath + File.separator + "sudoku_$id.xml")
    }

    override fun read(id: Int): SudokuBE {
        val obj = SudokuBE()
        val file = getSudokuFile(id)

        try {
            obj.fillFromXml(helper.loadXml(file)!!, outerSudokusDir)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when reading xml $file", e)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Something went wrong when filling obj from xml ", e)
        }
        return obj

    }

    override fun update(t: SudokuBE): SudokuBE {
        val file = File(getSudokuDir(t.sudokuType!!.enumType!!, t.complexity!!).absolutePath + File.separator + "sudoku_" + t.id + ".xml")

        try {
            val tree = t.toXmlTree()
            helper.saveXml(tree, file)
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when writing xml", e)
        }
        return read(t.id)
    }

    fun delete(t: SudokuBE) {
        val f = File(sudokusDir.absolutePath, "sudoku_${t.id}.xml")
        f.delete()
    }

    override fun delete(id: Int) {
        val f = File(sudokusDir.absolutePath, "sudoku_${id}.xml")
        f.delete()
    }

    /**
     * Gibt eine Referenz auf ein zufaelliges zu den Parametern passendem Sudoku
     * zurueck und null falls keins existiert
     * todo handle no available sudokus
     * @param type
     * der Typ des Sudokus
     * @param complexity
     * die Schwierigkeit des Sudokus
     * @return die Referenz auf die Datei
     */
    fun getRandomSudoku(): File? {
        return if (sudokusDir.list().isNotEmpty()) {
            val fileName = sudokusDir.list()[Random().nextInt(sudokusDir.list().size)]
            File(sudokusDir.absolutePath + File.separator + fileName)
        } else {
            null
        }
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
        val numbers = ArrayList<Int>()
        for (s in sudokusDir.list()) {
            numbers.add(s.substring(7, s.length - 4).toInt())
        }

        return generateSequence(1) { it + 1 }.first { !numbers.contains(it) }
    }

}