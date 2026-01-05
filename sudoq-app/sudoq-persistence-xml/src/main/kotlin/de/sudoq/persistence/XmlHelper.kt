/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.persistence

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.helpers.XMLReaderFactory
import java.io.*
import java.util.*

/**
 * Helper class that enables loading and saving [XmlTree]s to files.
 */
class XmlHelper {

    /** Unterstützte Typen von Xml Dateien */
    private val SUPPORTEDDTDS =
        arrayOf("sudoku", "game", "games", "profile", "profiles", "sudokutype")

    /** Preamble for written Xml Files
     */
    private val XmlPREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"

    /** System path, where DTD Specifications are stored */
    private val XmlDTDPATH = "./"

    /** Root of the read xml */
    private var xmlReadTreeRoot: XmlTree? = null

    /** Stack for readind xmlfiles, saves current dept in hierarchy */
    private var xmlReadStack: Stack<XmlTree>? = null

    /**
     * This function loads the content of an xml file into an [XmlTree].
     *
     * @param xmlFile File to read
     * @return [XmlTree] of the file
     *
     * @throws FileNotFoundException
     * @throws IOException If there are problems reading the file
     */
    @Throws(FileNotFoundException::class, IOException::class)
    fun loadXml(xmlFile: File): XmlTree? {
        if (!xmlFile.exists()) {
            throw FileNotFoundException()
        }
        return readXmlTree(InputSource(xmlFile.absolutePath))
    }

    /** Reads an XmlTree from an input source. */
    @Throws(IllegalArgumentException::class, IOException::class)
    private fun readXmlTree(input: InputSource): XmlTree? {
        val xr: XMLReader
        xmlReadTreeRoot = null
        xmlReadStack = Stack()
        try {
            xr = XMLReaderFactory.createXMLReader()
            val handler: XmlSAXHandler = XmlSAXHandler()
            xr.setFeature("http://xml.org/sax/features/namespaces", false)
            xr.setFeature("http://xml.org/sax/features/validation", false)
            xr.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false)
            xr.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            xr.contentHandler = handler
            xr.errorHandler = handler
            xr.parse(input)
        } catch (e: SAXException) {
            throw IOException()
        }
        return xmlReadTreeRoot
    }

    /**
     * This function saves an [XmlTree] to a file.
     *
     * @param xmlTree XmlTree to persist
     * @param xmlFile file in which to write
     *
     * @throws IOException if there are problems writing to the file
     */
    @Throws(IOException::class)
    fun saveXml(xmlTree: XmlTree, xmlFile: File) {
        // Check if the write operation is supported for this type of xml tree
        var supported = false
        for (dtd in SUPPORTEDDTDS) {
            if (xmlTree.name == dtd) {
                supported = true
                break
            }
        }
        require(supported) { "XmlTree Object is of an unsupported type." }
        val oustream = FileOutputStream(xmlFile)
        val osw = OutputStreamWriter(oustream)
        osw.write(XmlPREAMBLE)
        osw.write(
            """<!DOCTYPE ${xmlTree.name} SYSTEM "$XmlDTDPATH${xmlTree.name}.dtd">
"""
        )//todo can \n be used for next line?
        osw.write(buildXmlStructure(xmlTree))
        osw.flush()
        osw.close()
    }

    /** TODO if no children make just one tag instead of opening tag + closing tag
     * Converts [XmlTree] to String
     *
     * @param tree an [XmlTree]
     * @return String representation of the tree
     */
    fun buildXmlStructure(tree: XmlTree): String {
        // write the opening tag
        val sb = StringBuilder()
        sb.append("<")
        sb.append(tree.name)
        // write attributes
        val i = tree.getAttributes()
        while (i.hasNext()) {
            val attribute = i.next()
            sb.append(" ")
            sb.append(attribute.name)
            sb.append("=\"")
            sb.append(attribute.value)
            sb.append("\"")
        }

        // check if there are subtree elements
        if (tree.getChildren().hasNext()) {
            sb.append(">\n")
            // write the subtree elements
            for (sub in tree) {
                sb.append(buildXmlStructure(sub))
            }
        } else {
            // write the content if there is any
            sb.append(">")
            sb.append(tree.content)
        }
        // close the tag again
        sb.append("</")
        sb.append(tree.name)
        sb.append(">\n")
        return sb.toString()
    }

    /**
     * Class for reading Xml files with SAX Parser
     */
    private inner class XmlSAXHandler : DefaultHandler() {
        /**
         * Called by SAX Parser if an element begins
         */
        override fun startElement(uri: String, name: String, qName: String, atts: Attributes) {
            if ("" == uri) {
                // Check if this is the first element of the document
                if (xmlReadTreeRoot != null) {
                    val sub = XmlTree(qName)
                    // read and add attributes of the current element
                    for (i in 0 until atts.length) {
                        sub.addAttribute(XmlAttribute(atts.getQName(i), atts.getValue(i)))
                    }
                    xmlReadStack!!.lastElement().addChild(sub)

                    // move one layer deeper into xml hirarchie
                    xmlReadStack!!.push(sub)
                } else {
                    xmlReadTreeRoot = XmlTree(qName)
                    // read and add attributes of the current element
                    for (i in 0 until atts.length) {
                        xmlReadTreeRoot!!.addAttribute(
                            XmlAttribute(
                                atts.getQName(i),
                                atts.getValue(i)
                            )
                        )
                    }
                    // move one layer deeper into xml hirarchie
                    xmlReadStack!!.push(xmlReadTreeRoot)
                }
            }
        }

        /**
         * Called by SAX Parser to close an element
         */
        override fun endElement(uri: String, name: String, qName: String) {
            if ("" == uri) {
                // move one layer up in xml hirarchie
                xmlReadStack!!.pop()
            }
        }
    }
}