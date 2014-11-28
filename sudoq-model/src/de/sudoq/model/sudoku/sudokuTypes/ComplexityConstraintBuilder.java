package de.sudoq.model.sudoku.sudokuTypes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

public class ComplexityConstraintBuilder implements Xmlable {

	public Map<Complexity, ComplexityConstraint> specimen = new HashMap<Complexity, ComplexityConstraint>();
	
	public ComplexityConstraintBuilder(){
		
	}

	public ComplexityConstraintBuilder(Map<Complexity, ComplexityConstraint> specimen){
		this.specimen = specimen;
	}
	
	public ComplexityConstraint getComplexityConstraint(Complexity complexity){
		if (complexity != null && specimen.containsKey(complexity)) {
			return (ComplexityConstraint) specimen.get(complexity).clone();
		}
		return null;
	}
	
	
	public final String TITLE = "ComplexityConstraintBuilder";
	
	@Override
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree(TITLE);
		for (ComplexityConstraint v : specimen.values()){
			representation.addChild( v.toXmlTree() );
		}
		return representation;
	}

	@Override
	public void fillFromXml(XmlTree xmlTreeRepresentation) throws IllegalArgumentException {
		specimen = new HashMap<Complexity, ComplexityConstraint>();
		for (Iterator<XmlTree> iterator = xmlTreeRepresentation.getChildren(); iterator.hasNext();) {
			XmlTree sub = iterator.next();
			ComplexityConstraint cc = new ComplexityConstraint();
			cc.fillFromXml(sub);
			specimen.put(cc.getComplexity(), cc);
		}

	}

}
