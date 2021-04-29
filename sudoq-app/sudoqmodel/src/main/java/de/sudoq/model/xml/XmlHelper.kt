/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xml.sax.XMLReader
import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.helpers.XMLReaderFactory
import java.io.*
import java.util.*

/**
 * Dies ist eine Helfer-Klasse, die das Laden und Speichern von XmlTree Objekten
 * in Xml Dateien ermöglicht.
 *
 * @see XmlTree
 */
class XmlHelper {
    /* Attributes */
    /**
     * Unterstützte Typen von Xml Dateien
     */
    private val SUPPORTEDDTDS = arrayOf("sudoku", "game", "games",
            "profile", "profiles", "sudokutype")

    /**
     * Prämbel für geschriebene Xml Dateien
     */
    private val XmlPREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"

    /**
     * Systempfad, an dem die DTD Spezifikationen hinterlegt wurden
     */
    private val XmlDTDPATH = "./"

    /**
     * Wurzel einer eingelesenen Xml Baumstruktur
     */
    private var xmlReadTreeRoot: XmlTree? = null

    /**
     * Stack für das Einlesen von Xml Dateien, speichert derzeitige
     * Hierarchietiefe
     */
    private var xmlReadStack: Stack<XmlTree>? = null
    /* Methods */
    /**
     * Diese Methode lädt den Inhalt einer Xml Datei in ein XmlTree Objekt.
     *
     * @param xmlFile
     * Xml Datei aus der gelesen werden soll
     * @return Xml Baum der eingelesenen Datei
     * @see XmlTree
     *
     * @throws FileNotFoundException
     * Wird geworfen, falls die spezifizierte Datei nicht existiert
     * @throws IllegalArgumentException
     * Wird geworfen, falls das übergebene Argument null ist
     * @throws IOException
     * Wird geworfen, wenn Probleme beim Lesen der Datei auftraten
     * oder z.B. die Xml Datei kompromittiert ist
     */
    @Throws(FileNotFoundException::class, IllegalArgumentException::class, IOException::class)
    fun loadXml(xmlFile: File?): XmlTree? {
        requireNotNull(xmlFile)
        if (!xmlFile.exists()) {
            throw FileNotFoundException()
        }
        return readXmlTree(InputSource(xmlFile.absolutePath))
    }
    //	/**
    //	 * Diese Methode generiert einen Xml-Baum aus einem String, der eine valide
    //	 * Struktur besitzt.
    //	 *
    //	 * @param structure
    //	 *            Xml-Baum in einer String Repaesentation
    //	 * @return XmlTree Repraesentation der Xml Struktur
    //	 * @throws IllegalArgumentException
    //	 *             Wird geworfen, wenn der gegebene Xml String eine invalide
    //	 *             Struktur aufweist.
    //	 */
    //	 public XmlTree buildXmlTree(String structure) throws
    //	 IllegalArgumentException {
    //	 try {
    //	 return readXmlTree(new InputSource(new StringReader(structure)));
    //	 } catch (IOException exc) {
    //	 throw new IllegalArgumentException();
    //	 }
    //	 }
    /**
     * Bereitet das Lesen einer Xml Quelle zu einem XmlTree Objekt vor und
     * fuehrt diese Operation aus.
     */
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
     * Diese Methode speichert ein XmlTree Objekt in einer Xml Datei.
     *
     * @param xmlTree
     * Xml Baum, der die zu schreibenden Daten enthält
     * @param xmlFile
     * Xml Datei in die geschrieben werden soll
     * @see XmlTree
     *
     * @throws IllegalArgumentException
     * Wird geworfen, falls eines der Argumente null ist
     * @throws IOException
     * Wird geworfen, wenn Probleme beim Schreiben der Datei
     * auftraten
     */
    @Throws(IllegalArgumentException::class, IOException::class)
    fun saveXml(xmlTree: XmlTree?, xmlFile: File?) {
        require(!(xmlFile == null || xmlTree == null))
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
        osw.write("""<!DOCTYPE ${xmlTree.name} SYSTEM "$XmlDTDPATH${xmlTree.name}.dtd">
""")
        osw.write(buildXmlStructure(xmlTree))
        osw.flush()
        osw.close()
    }

    /** TODO if no children make just one tag instead of opening tag + closing tag
     * Gibt eine String Repäsentation des eingegebenen Xml Baumes zurück
     *
     * @param tree
     * der umzuwandelnde XmlBaum
     * @return Die String Repräsentation des Xml Baumes
     * @throws IllegalArgumentException
     * Wird geworfen, wenn der eingegebene Xml Baum null ist
     */
    @Throws(IllegalArgumentException::class)
    fun buildXmlStructure(tree: XmlTree?): String {
        requireNotNull(tree)
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
     * Klasse für das Einlesen von Xml Dateien mit dem SAX Parser
     */
    private inner class XmlSAXHandler : DefaultHandler() {
        /**
         * Wird vom SAX Parser aufgerufen, falls ein Xml Element beginnt
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
                        xmlReadTreeRoot!!.addAttribute(XmlAttribute(atts.getQName(i), atts.getValue(i)))
                    }
                    // move one layer deeper into xml hirarchie
                    xmlReadStack!!.push(xmlReadTreeRoot)
                }
            }
        }

        /**
         * Wird vom SAX Parser aufgerufen, wenn ein Xml Element schließt
         */
        override fun endElement(uri: String, name: String, qName: String) {
            if ("" == uri) {
                // move one layer up in xml hirarchie
                xmlReadStack!!.pop()
            }
        }
    }
}