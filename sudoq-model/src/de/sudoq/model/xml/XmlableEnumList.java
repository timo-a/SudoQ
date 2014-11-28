/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.util.ArrayList;
import java.util.Iterator;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * An xmlable ArrayList of PermutationProperties
 */
public class XmlableEnumList <T extends Enum<T>> extends ArrayList<T> implements Xmlable{

	public final String rootName;
	
	/*
	 * error in fillfrom at TODO
	 * */
	@Deprecated
	public XmlableEnumList(String rootName) {
		super();
		this.rootName = rootName;
	}
	
	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree(rootName);
		for (int i=0; i<this.size(); i++){
			XmlAttribute xa = new XmlAttribute(""+i,""+this.get(i).ordinal());
			representation.addAttribute(xa);
		}
		return representation;
	}

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation) throws IllegalArgumentException {
		this.clear();
		this.ensureCapacity(xmlTreeRepresentation.getNumberOfAttributes());
		for (Iterator<XmlAttribute> iterator = xmlTreeRepresentation.getAttributes(); iterator.hasNext();) {
			XmlAttribute xa = iterator.next();
			int index = Integer.parseInt(xa.getName());
			//T enumVal = T.values()[Integer.parseInt(xa.getValue())]; //TODO wont work wg. type erasure?
//			this.set(index,enumVal);
		}		
	}
}