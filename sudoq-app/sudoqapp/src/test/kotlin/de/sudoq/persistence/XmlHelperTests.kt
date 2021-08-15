package de.sudoq.persistence

import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class XmlHelperTests {
    private var helper = XmlHelper()

    @Test
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, IOException::class)
    fun testLoadXml() {
        val sudokuTree: XmlTree = helper.loadXml(File("res/sudoku_example.xml"))!!
        // helper.buildXmlStructure(sudokuTree);
        sudokuTree.numberOfChildren `should be equal to` 2
        sudokuTree.name `should be equal to` "sudoku"
        val gameTree: XmlTree = helper.loadXml(File("res/game_example.xml"))!!
        // helper.buildXmlStructure(gameTree);
        gameTree.numberOfChildren `should be equal to` 4
        val gamesTree: XmlTree = helper.loadXml(File("res/games_example.xml"))!!
        // helper.buildXmlStructure(gamesTree);
        gamesTree.numberOfChildren `should be equal to` 1
    }

    @Test
    @Throws(IllegalArgumentException::class, IOException::class)
    fun testLoadXmlFileNotFoundException() {
        invoking {
            helper.loadXml(File("res/not_existing_imaginary_file.xml"))
        }.`should throw`(FileNotFoundException::class)
    }

    @Test
    @Throws(IOException::class)
    fun testLoadXmlIOException() {
        invoking {
            helper.loadXml(File("res/compromised.xml"))
        }.`should throw`(IOException::class)
    }

    //TODO test content. it does not seem to bee working
    @Test
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, IOException::class)
    fun testSaveXml() {
        val testFile = File("res/tmp.xml")
        testFile.setWritable(true)
        val testFile2 = File("res/tmp2.xml")
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
        helper.saveXml(sudoku, testFile)
        val sudokuTest: XmlTree = helper.loadXml(testFile)!!
        println("------------------------------------------")
        println(helper.buildXmlStructure(sudokuTest))
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
        val file = File("res/tmp.xml")
        file.setWritable(false)
        // this test will fail if you use linux and the file gets created on a
        // ntfs partition.
        // seems that the java file implementation uses linux tools like chmod -
        // chmod doesnt work on ntfs...
        file.canWrite().`should be false`()

        invoking {
            helper.saveXml(XmlTree("sudoku", ""), file)
        }.`should throw`(IOException::class)
    }

    @Test
    @Throws(IOException::class)
    fun testSaveXmlIllegalArgumentException4() {
        invoking {
            helper.saveXml(XmlTree("name", ""), File("res/tmp.xml"))
        }.`should throw`(IllegalArgumentException::class)
    }
}