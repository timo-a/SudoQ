/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.util.ArrayList;

import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/** Do never ever try to do this generic. NEVER
 * An xmlable ArrayList of PermutationProperties
 */
public abstract class XmlableEnumList extends ArrayList<Integer> implements Xmlable{

	public final String rootName;
	String enumType;
		
	
//TODO replace setofpermutationproperties by this
	public XmlableEnumList(String rootName, String enumType) {
		super();
		this.rootName = rootName;
		this.enumType = enumType;
	}
	
	public boolean add(Enum<?> h){
		return this.add(h.ordinal());
	}
	
	abstract public ArrayList<?> getList();
		
	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree(rootName);
		for (Integer i: this){
			String index = ""+representation.getNumberOfChildren(); 
			String value = ""+this.get(i);
			XmlAttribute xa = new XmlAttribute(index,value);
			representation.addAttribute(xa);
		}
		return representation;
	}

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation) throws IllegalArgumentException {
		this.clear();
		this.ensureCapacity(xmlTreeRepresentation.getNumberOfAttributes());
		for (XmlAttribute xa : xmlTreeRepresentation.getAttributes2()) {
			int index = Integer.parseInt(xa.getName());
			int value = Integer.parseInt(xa.getValue()); //TODO wont work wg. type erasure?
			this.set(index, value);
		}		
	}
}