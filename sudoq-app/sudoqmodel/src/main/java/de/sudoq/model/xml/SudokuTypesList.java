/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * An xmlable ArrayList of PermutationProperties
 */
public class SudokuTypesList extends ArrayList<SudokuTypes> implements Xmlable{

	public static final String ROOT_NAME    = "SudokuTypesList";
	private       final String ELEMENT_NAME = "Type";
	private       final String TYPE_ID      = "TypeID";
	
	public ArrayList<SudokuTypes> getAllTypes(){
        return new ArrayList<>(Arrays.asList(SudokuTypes.values()));
	}
	
	public SudokuTypesList(){
		this.addAll(getAllTypes());
	}
	
	
	public boolean isTypeWanted(SudokuTypes t){
		return contains(t); 
	}
	
	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree(ROOT_NAME);
		for (SudokuTypes p:this){
			String index = Integer.toString(representation.getNumberOfAttributes());
			representation.addAttribute(new XmlAttribute(TYPE_ID+"_"+index, Integer.toString(p.ordinal())));
		}
		
		return representation;
	}
	
	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation) throws IllegalArgumentException {
		clear();
		for (XmlAttribute xa : xmlTreeRepresentation.getAttributes2()) {
			if (xa.getName().startsWith(TYPE_ID)) {
				SudokuTypes st = SudokuTypes.values()[Integer.parseInt(xa.getValue())]; 
				add(st); //right order not guaranteed(if s.o. messes with xml)
			}
		}		
	}
}