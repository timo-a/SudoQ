package de.sudoq.persistence

import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path

class XmlHelperTests {
    private var helper = XmlHelper()

    fun getFile(path: String): File {
        val classLoader = javaClass.classLoader
        return File(classLoader.getResource(path)!!.file)
    }

    @Test
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, IOException::class)
    fun testLoadXml() {
        val sudokuTree: XmlTree = helper.loadXml(getFile("persistence/sudoku_example.xml"))!!
        // helper.buildXmlStructure(sudokuTree);
        sudokuTree.numberOfChildren `should be equal to` 2
        sudokuTree.name `should be equal to` "sudoku"
        val gameTree: XmlTree = helper.loadXml(getFile("persistence/game_example.xml"))!!
        // helper.buildXmlStructure(gameTree);
        gameTree.numberOfChildren `should be equal to` 4
        val gamesTree: XmlTree = helper.loadXml(getFile("persistence/games_example.xml"))!!
        // helper.buildXmlStructure(gamesTree);
        gamesTree.numberOfChildren `should be equal to` 1
    }

    @Test
    @Throws(IllegalArgumentException::class, IOException::class)
    fun testLoadXmlFileNotFoundException() {
        invoking {
            helper.loadXml(File("persistence/not_existing_imaginary_file.xml"))
        }.`should throw`(FileNotFoundException::class)
    }

    @Test
    @Throws(IOException::class)
    fun testLoadXmlIOException() {
        invoking {
            helper.loadXml(getFile("persistence/compromised.xml"))
        }.`should throw`(IOException::class)
    }

    @TempDir
    lateinit var tempDir: Path

    //TODO test content. it does not seem to bee working
    @Test
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, IOException::class)
    fun testSaveXml() {
        // GIVEN
        val testFile = tempDir.resolve("tmp.xml").toFile()
        val testFile2 = tempDir.resolve("tmp2.xml").toFile()
        val sudoku = XmlTree("sudoku")
        sudoku.addAttribute(XmlAttribute("id", "7845"))
        sudoku.addAttribute(XmlAttribute("type", "6"))
        sudoku.addAttribute(XmlAttribute("complexity", "3"))
        val fieldmap1 = XmlTree("fieldmap")
        fieldmap1.addAttribute(XmlAttribute("editable", "true"))
        fieldmap1.addAttribute(XmlAttribute("solution", "9"))
        val position1 = XmlTree("position")
        position1.addAttribute(XmlAttribute("x", "1"))
        position1.addAttribute(XmlAttribute("y", "8"))
        fieldmap1.addChild(position1)
        sudoku.addChild(fieldmap1)
        val fieldmap2 = XmlTree("fieldmap")
        fieldmap2.addAttribute(XmlAttribute("editable", "true"))
        fieldmap2.addAttribute(XmlAttribute("solution", "4"))
        val position2 = XmlTree("position")
        position2.addAttribute(XmlAttribute("x", "2"))
        position2.addAttribute(XmlAttribute("y", "6"))
        fieldmap2.addChild(position2)
        sudoku.addChild(fieldmap2)
        println(helper.buildXmlStructure(sudoku))
        // WHEN
        helper.saveXml(sudoku, testFile)
        val sudokuTest: XmlTree = helper.loadXml(testFile)!!
        // THEN
        println("------------------------------------------")
        println(helper.buildXmlStructure(sudokuTest))//todo compare before and after?
        sudokuTest.name `should be equal to` sudoku.name
        sudokuTest.numberOfChildren `should be equal to` sudoku.numberOfChildren
        var i = 0
        val iterator: Iterator<XmlTree> = sudokuTest.getChildren()
        while (iterator.hasNext()) {
            val sub: XmlTree = iterator.next()
            if (i == 0) {
                sub.getAttributeValue("solution") `should be equal to` "9"
            } else if (i == 1) {
                sub.getAttributeValue("solution") `should be equal to` "4"
            }
            sub.getAttributeValue("editable") `should be equal to` "true"
            sub.name `should be equal to` "fieldmap"
            sub.numberOfChildren `should be equal to` 1
            i++
        }
        i `should be equal to` 2
        val atomicTest = XmlTree("sudoku", "xyz")
        helper.saveXml(atomicTest, testFile2)
    }

    @Test
    @Throws(IllegalArgumentException::class, IOException::class)
    fun testSaveXmlIOException() {
        val file = tempDir.resolve("readOnly.xml").toFile()
        file.createNewFile()
        file.setWritable(false) `should be` true
        file.canWrite() `should be` false

        invoking {
            helper.saveXml(XmlTree("sudoku", ""), file)
        }.`should throw`(IOException::class)
    }

    @Test
    @Throws(IOException::class)
    fun testSaveXmlIllegalArgumentException4() {
        val file = tempDir.resolve("gzu.xml").toFile()
        invoking {
            helper.saveXml(XmlTree("name", ""), file)
        }.`should throw`(IllegalArgumentException::class)
    }
}