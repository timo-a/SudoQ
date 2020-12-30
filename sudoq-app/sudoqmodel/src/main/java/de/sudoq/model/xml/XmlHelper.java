/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Dies ist eine Helfer-Klasse, die das Laden und Speichern von XmlTree Objekten
 * in Xml Dateien ermöglicht.
 * 
 * @see XmlTree
 */
public class XmlHelper {

	/* Attributes */

	/**
	 * Unterstützte Typen von Xml Dateien
	 */
	private final String[] SUPPORTEDDTDS = { "sudoku",  "game",     "games", 
			                                 "profile", "profiles", "sudokutype"};

	/**
	 * Prämbel für geschriebene Xml Dateien
	 */
	private final String XmlPREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";

	/**
	 * Systempfad, an dem die DTD Spezifikationen hinterlegt wurden
	 */
	private final String XmlDTDPATH = "./";

	/**
	 * Wurzel einer eingelesenen Xml Baumstruktur
	 */
	private XmlTree xmlReadTreeRoot;

	/**
	 * Stack für das Einlesen von Xml Dateien, speichert derzeitige
	 * Hierarchietiefe
	 */
	private Stack<XmlTree> xmlReadStack;

	/* Methods */

	/**
	 * Diese Methode lädt den Inhalt einer Xml Datei in ein XmlTree Objekt.
	 * 
	 * @param xmlFile
	 *            Xml Datei aus der gelesen werden soll
	 * @return Xml Baum der eingelesenen Datei
	 * @see XmlTree
	 * @throws FileNotFoundException
	 *             Wird geworfen, falls die spezifizierte Datei nicht existiert
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene Argument null ist
	 * @throws IOException
	 *             Wird geworfen, wenn Probleme beim Lesen der Datei auftraten
	 *             oder z.B. die Xml Datei kompromittiert ist
	 */
	public XmlTree loadXml(File xmlFile) throws FileNotFoundException, IllegalArgumentException, IOException {
		if (xmlFile == null) {
			throw new IllegalArgumentException();
		}
		if (!xmlFile.exists()) {
			throw new FileNotFoundException();
		}
		return readXmlTree(new InputSource(xmlFile.getAbsolutePath()));
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
	private XmlTree readXmlTree(InputSource input) throws IllegalArgumentException, IOException {
		XMLReader xr;
		xmlReadTreeRoot = null;
		xmlReadStack = new Stack<>();
		try {
			xr = XMLReaderFactory.createXMLReader();
			XmlSAXHandler handler = new XmlSAXHandler();
			xr.setFeature("http://xml.org/sax/features/namespaces", false);
			xr.setFeature("http://xml.org/sax/features/validation", false);
			xr.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			xr.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			xr.parse(input);
		} catch (SAXException e) {
			throw new IOException();
		}
		return xmlReadTreeRoot;
	}

	/**
	 * Diese Methode speichert ein XmlTree Objekt in einer Xml Datei.
	 * 
	 * @param xmlTree
	 *            Xml Baum, der die zu schreibenden Daten enthält
	 * @param xmlFile
	 *            Xml Datei in die geschrieben werden soll
	 * @see XmlTree
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls eines der Argumente null ist
	 * @throws IOException
	 *             Wird geworfen, wenn Probleme beim Schreiben der Datei
	 *             auftraten
	 */
	public void saveXml(XmlTree xmlTree, File xmlFile) throws IllegalArgumentException, IOException {

		if (xmlFile == null || xmlTree == null) {
			throw new IllegalArgumentException();
		}
		// Check if the write operation is supported for this type of xml tree
		boolean supported = false;
		for (String dtd : SUPPORTEDDTDS) {
			if (xmlTree.getName().equals(dtd)) {
				supported = true;
				break;
			}
		}
		if (!supported) {
			throw new IllegalArgumentException("XmlTree Object is of an unsupported type.");
		}

		FileOutputStream oustream = new FileOutputStream(xmlFile);
		OutputStreamWriter osw = new OutputStreamWriter(oustream);

		osw.write(XmlPREAMBLE);
		osw.write("<!DOCTYPE " + xmlTree.getName() + " SYSTEM \"" + XmlDTDPATH + xmlTree.getName() + ".dtd\">\n");

		osw.write(buildXmlStructure(xmlTree));

		osw.flush();
		osw.close();

	}

	/** TODO if no children make just one tag instead of opening tag + closing tag
	 * Gibt eine String Repäsentation des eingegebenen Xml Baumes zurück
	 * 
	 * @param tree
	 *            der umzuwandelnde XmlBaum
	 * @return Die String Repräsentation des Xml Baumes
	 * @throws IllegalArgumentException
	 *             Wird geworfen, wenn der eingegebene Xml Baum null ist
	 */
	public String buildXmlStructure(XmlTree tree) throws IllegalArgumentException {
		if (tree == null) {
			throw new IllegalArgumentException();
		}
		// write the opening tag
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(tree.getName());
		// write attributes
		for (Iterator<XmlAttribute> i = tree.getAttributes(); i.hasNext();) {
			XmlAttribute attribute = i.next();
			sb.append(" ");
			sb.append(attribute.getName());
			sb.append("=\"");
			sb.append(attribute.getValue());
			sb.append("\"");
		}

		// check if there are subtree elements
		if (tree.getChildren().hasNext()) {
			sb.append(">\n");
			// write the subtree elements
			for (XmlTree sub : tree) {
				sb.append(buildXmlStructure(sub));
			}

		} else {
			// write the content if there is any
			sb.append(">");
			sb.append(tree.getContent());
		}
		// close the tag again
		sb.append("</");
		sb.append(tree.getName());
		sb.append(">\n");
		return sb.toString();
	}

	/**
	 * Klasse für das Einlesen von Xml Dateien mit dem SAX Parser
	 */
	private class XmlSAXHandler extends DefaultHandler {

		/**
		 * Wird vom SAX Parser aufgerufen, falls ein Xml Element beginnt
		 */
		public void startElement(String uri, String name, String qName, Attributes atts) {

			if ("".equals(uri)) {
				// Check if this is the first element of the document
				if (xmlReadTreeRoot != null) {
					XmlTree sub = new XmlTree(qName);
					// read and add attributes of the current element
					for (int i = 0; i < atts.getLength(); i++) {
						sub.addAttribute(new XmlAttribute(atts.getQName(i), atts.getValue(i)));
					}
					xmlReadStack.lastElement().addChild(sub);

					// move one layer deeper into xml hirarchie
					xmlReadStack.push(sub);
				} else {
					xmlReadTreeRoot = new XmlTree(qName);
					// read and add attributes of the current element
					for (int i = 0; i < atts.getLength(); i++) {
						xmlReadTreeRoot.addAttribute(new XmlAttribute(atts.getQName(i), atts.getValue(i)));
					}
					// move one layer deeper into xml hirarchie
					xmlReadStack.push(xmlReadTreeRoot);
				}
			}
		}

		/**
		 * Wird vom SAX Parser aufgerufen, wenn ein Xml Element schließt
		 */
		public void endElement(String uri, String name, String qName) {

			if ("".equals(uri)) {
				// move one layer up in xml hirarchie
				xmlReadStack.pop();
			}
		}
	}
}
