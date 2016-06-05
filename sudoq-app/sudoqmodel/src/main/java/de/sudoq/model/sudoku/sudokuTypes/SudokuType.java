/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.sudoku.sudokuTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.solverGenerator.solver.helper.Helpers;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.complexity.ComplexityFactory;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Ein SudokuType repräsentiert die Eigenschaften eines spezifischen Sudoku-Typs. Dazu gehören insbesondere die
 * Constraints, die einen Sudoku-Typ beschreiben.
 */
public class SudokuType implements Iterable<Constraint>, ComplexityFactory, Xmlable {
	/** Attributes */

	protected SudokuTypes typeName;
	
	protected float standardAllocationFactor;
	
	/**
	 * Ein Positionobjekt das in seinen Koordinaten die Anzahl an Spalten und Zeilen hält
	 */
	protected Position dimensions;

	/**
	 * The Dimensions of one quadratic block, i.e. for a normal 9x9 Sudoku: 3,3.
	 * But for Squiggly or Stairstep: 0,0
	 * and for 4x4: 2,2
	 */
	protected Position blockSize = Position.get(0, 0);

	/**
	 * Die Anzahl von Symbolen die in die Felder eines Sudokus dieses Typs eingetragen werden können.
	 */
	private int numberOfSymbols;

	/**
	 * Die Liste der Constraints, die diesen Sudoku-Typ beschreiben
	 */
	protected List<Constraint> constraints;

	/** 
	 * Alle Positions die in Teil eines Constraints sind -> auf ein Feld verweisen
	 * (Bei Samurai sind das ja nicht alle) 
	 * */
	protected List<Position> positions;

	/**
	 * eine List die zulässige Transformationen am Sudokutyp hält
	 */
	protected List<PermutationProperties> setOfPermutationProperties;

	protected List<Helpers> helperList;

	protected ComplexityConstraintBuilder ccb;
	
	public SudokuType(){
		this.constraints = new ArrayList<Constraint>();
		this.positions   = new ArrayList<Position>();
		this.setOfPermutationProperties = new SetOfPermutationProperties();
		this.helperList = new ArrayList<Helpers>();
		this.ccb = new ComplexityConstraintBuilder();
	}
		
	
	/**
	 * Konstruktor für einen SudokuTyp
	 * 
         *
         * @param width
         *            width of the sudoku in fields
         * @param height
         *            height of the sudoku in fields
	 * @param numberOfSymbols
	 *            die Anzahl an Symbolen die dieses Sudoku verwendet
	 */
	public SudokuType(int width, int height, int numberOfSymbols) {
		if (numberOfSymbols < 0)
			throw new IllegalArgumentException("Number of symbols < 0 : " + numberOfSymbols);
		if (width < 0)
			throw new IllegalArgumentException("Sudoku width < 0 : " + width);
		if (height < 0)
			throw new IllegalArgumentException("Sudoku height < 0 : " + height);
		this.typeName = null;
		this.standardAllocationFactor = -1.0f;
		this.numberOfSymbols = numberOfSymbols;
		this.dimensions = Position.get(width, height);
		this.constraints = new ArrayList<Constraint>();
		this.positions   = new ArrayList<Position>();
		this.setOfPermutationProperties = new SetOfPermutationProperties();
		this.helperList = new ArrayList<Helpers>();
		this.ccb = new ComplexityConstraintBuilder();
	}

	/** Methods */

	/**
	 * Gibt die Größe eines Sudokus dieses Typs zurück. Die Größe wird durch ein Position-Objekt repräsentiert, wobei
	 * die x-Koordinate die maximale Anzahl horizontaler Felder eines Sudokus dieses Typs beschreibt, die y-Koordinaten
	 * die maximale Anzahl vertikaler Felder.
	 * 
	 * @return Ein Position-Objekt, welches die maximale Anzahl horizontaler bzw. vertikaler Felder eines Sudokus dieses
	 *         Typs beschreibt
	 */
	public Position getSize() {
		return dimensions;
	}

	public Position getBlockSize(){
		return blockSize;
	}
	/**
	 * Gibt eine Liste mit zulässigen Transformationen an diesem Sudoku aus.
	 * 
	 * @return eine Liste mit zulässigen Transformationen an diesem Sudoku
	 */
	public List<PermutationProperties> getPermutationProperties() {
		return setOfPermutationProperties;
	}

	/**
	 * Überprüft, ob das spezifizierte Sudoku die Vorgaben aller Constraints dieses SudokuTyps erfüllt. Ist dies der
	 * Fall, so wir true zurückgegeben. Erfüllt es die Vorgaben nicht, oder wird null übergeben, so wird false
	 * zurückgegeben.
	 * 
	 * @param sudoku
	 *            Das Sudoku, welches auf Erfüllung der Constraints überprüft werden soll
	 * @return true, falls das Sudoku alle Constraints erfüllt, false falls es dies nicht tut oder null ist
	 */
	public boolean checkSudoku(Sudoku sudoku) {
		if (sudoku == null)
			return false;

		boolean allSaturated = true;

		for (Constraint c : this.constraints) {
			if (!c.isSaturated(sudoku))
				allSaturated = false;
		}

		return allSaturated;
	}

	/**
	 * Gibt einen Iterator für die Constraints dieses Sudoku-Typs zurück.
	 * 
	 * @return Einen Iterator für die Constraints dieses Sudoku-Typs
	 */
	public Iterator<Constraint> iterator() {
		return constraints.iterator();
	}

	private class Positions implements Iterable<Position>{
		public Iterator<Position> iterator(){
			return positions.iterator();
		}
	}
	
	/**
	 * Returns an iterator over all positions in this type
	 */
	public Iterable<Position> getValidPositions() {
		//return positions.iterator();
		return new Positions();
	}

	
	
	/**
	 * Gibt die Anzahl der Symbole eines Sudokus dieses Typs zurück.
	 * 
	 * @return Die Anzahl der Symbole in einem Sudoku dieses Typs
	 */
	public int getNumberOfSymbols() {
		return this.numberOfSymbols;
	}

	/**
	 * Gibt einen ComplexityContraint für eine Schwierigkeit complexity zurück.
	 * 
	 * @param complexity
	 *            eine Schwierigkeit zu der ein ComplexityConstraint erzeugt werden soll
	 */
	public ComplexityConstraint buildComplexityConstraint(Complexity complexity) {
		return ccb.getComplexityConstraint(complexity);
	}

	/**
	 * Gibt den Standard Belegungsfaktor zurück
	 */
	public float getStandardAllocationFactor() {
		return 0.35f;
	}

	/**
	 * Gibt den Sudoku-Typ als String zurück.
	 * 
	 * @return Sudoku-Typ als String
	 */
	public String toString() {
		return "" + this.typeName;
	}
	
	/**
	 * Setzt den Typ auf den spezifizierten Wert.
	 * @param type Typ
	 */
	public void setTypeName(SudokuTypes type) {
		if (type != null) 
			this.typeName = type;
	}
	
	public void setDimensions(Position p) {
		this.dimensions = p;
	}

	public void setNumberOfSymbols(int numberOfSymbols) {
		if (numberOfSymbols > 0) 
			this.numberOfSymbols = numberOfSymbols;
	}
	
	
	/**
	 * Gibt das enum dieses Typs zurück.
	 * @return Enum dieses Typs
	 */
	public SudokuTypes getEnumType() {
		return this.typeName;
	}

	/**
	 * Gibt eine Liste der Constraints, welche zu diesem Sudokutyp gehören zurück. Hinweis: Wenn möglich stattdessen den
	 * Iterator benutzen.
	 * 
	 * @return Eine Liste der Constraints dieses Sudokutyps.
	 */
	public ArrayList<Constraint> getConstraints() {
		return (ArrayList<Constraint>) this.constraints;
	}
	
	
	//make a method that returns an iterator over all positions !=null. I think we need this a lot
	
	public void addConstraint(Constraint c) {
		if (c != null) {
			this.constraints.add(c);
		}
	}
	
	public static SudokuType getSudokuType(SudokuTypes type) {
		if (type == null ) {
			return null;
		}
		File f = FileManager.getSudokuTypeFile(type);
		if (!f.exists()) {
			return null;
		}
		XmlHelper helper = new XmlHelper();
		try {
			SudokuType t = new SudokuType();
			XmlTree xt = helper.loadXml(f);
			t.fillFromXml(xt);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Integer> getSudokuTypeIds() {
		List<Integer> ids = new ArrayList<>();
		File f = FileManager.getSudokuDir();
		for (File id : f.listFiles()) {
			if (id.isDirectory()) {
				System.out.println(id.getName());
				ids.add(Integer.parseInt(id.getName()));
			}
		}
		return ids;
	}

	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree("sudokutype");
		representation.addAttribute(new XmlAttribute("typename", "" + typeName.ordinal()));
		representation.addAttribute(new XmlAttribute("numberOfSymbols", "" + numberOfSymbols));
		representation.addAttribute(new XmlAttribute("standardAllocationFactor", Float.toString(standardAllocationFactor)));
		representation.addChild(dimensions.toXmlTree("size"));
		representation.addChild(blockSize.toXmlTree("blockSize"));
		for (Constraint c : constraints) {
			representation.addChild(c.toXmlTree());
		}
		representation.addChild( ( (SetOfPermutationProperties) setOfPermutationProperties).toXmlTree());
		XmlTree hList = new XmlTree("helperList");
		for(int i=0; i< helperList.size(); i++){
			hList.addAttribute(new XmlAttribute("i", "" + helperList.get(i).ordinal()));
		}
		representation.addChild(hList);
		representation.addChild(ccb.toXmlTree());
		
		// TODO complexity builderdata
		
		return representation;
	}

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation) throws IllegalArgumentException {
		typeName = SudokuTypes.values()[Integer.parseInt(xmlTreeRepresentation.getAttributeValue("typename"))];
		numberOfSymbols = Integer.parseInt(xmlTreeRepresentation.getAttributeValue("numberOfSymbols")); 
		standardAllocationFactor = Float.parseFloat(xmlTreeRepresentation.getAttributeValue("standardAllocationFactor"));
		for (XmlTree sub : xmlTreeRepresentation) {
			switch (sub.getName()) {
			case "size":
				dimensions = Position.fillFromXmlStatic(sub);
				break;

			case "blockSize":
				blockSize = Position.fillFromXmlStatic(sub);
				break;
				
			case "constraint":
				Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
				c.fillFromXml(sub);
				constraints.add(c);
				break;
				
			case SetOfPermutationProperties.SET_OF_PERMUTATION_PROPERTIES:
				setOfPermutationProperties = new SetOfPermutationProperties();
				((SetOfPermutationProperties) setOfPermutationProperties).fillFromXml(sub);  //cast neccessary because setOPP is defined as 
				break;
				
			case "helperList":
				helperList = new ArrayList<>(sub.getNumberOfAttributes());
				for(Iterator<XmlAttribute> jterator = sub.getAttributes(); jterator.hasNext();){
					XmlAttribute xa = jterator.next();
					int index = Integer.parseInt(xa.getName());
					Helpers h = Helpers.values()[Integer.parseInt(xa.getValue())];
					helperList.set(index, h);
				}
				break;
				
			case ComplexityConstraintBuilder.TITLE:
				ccb = new ComplexityConstraintBuilder();
				ccb.fillFromXml(sub);
				break;
				
			default:
				break;
			}
		}
		initPositionsList();
	}


	private void initPositionsList() {
		positions = new ArrayList<>();
		for(Constraint c: constraints)
			for(Position p: c)
				if(!positions.contains(p))
					positions.add(p);
		
	}
}
