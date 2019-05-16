/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile;

import java.util.BitSet;

import de.sudoq.model.game.Assistances;
import de.sudoq.model.xml.SudokuTypesList;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Diese Klasse repräsentiert alle Einstellungen zum Spiel:
 * -einen Satz von Assistances, also für jede
 * Assistance ob diese gesetzt ist oder nicht.
 * -zusätzliche optionen, wie lefthandmode, hints...
 */
public class AppSettings implements Xmlable{

	private boolean debug;


	/* additional settings */

	public void setDebug(boolean value) {
		this.debug = value;
	}

	public boolean isDebugSet() {
		return debug;
	}


	/* to and from string */
	
	@Override
	public XmlTree toXmlTree() {
        XmlTree representation = new XmlTree("appSettings");
        representation.addAttribute(new XmlAttribute("debug",    debug));
		return representation;
	}
	
	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation)
			throws IllegalArgumentException {
		
		debug  = Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("debug"));
	}

}