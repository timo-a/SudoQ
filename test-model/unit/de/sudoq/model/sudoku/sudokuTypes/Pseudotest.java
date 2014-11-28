package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.sudokuTypes.HyperSudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;
import de.sudoq.model.xml.XmlHelper;

public class Pseudotest {

	TypeBasic stHy = new HyperSudoku();

	public SudokuType usual(TypeBasic oldType){
		
		SudokuType s = new SudokuType(9, 9, 9);
		s.setTypeName(oldType.getEnumType());
		
		
		s.setNumberOfSymbols(oldType.getNumberOfSymbols());
		s.setDimensions(oldType.getSize());
		s.standardAllocationFactor = oldType.getStandardAllocationFactor();
		for(Constraint c : oldType.getConstraints())
			s.addConstraint(c);
		for (PermutationProperties p : oldType.permutationProperties)
			s.setOfPermutationProperties.add(p);
		
		Complexity[] comps = {Complexity.easy,
	                          Complexity.medium,
	                          Complexity.difficult,
	                          Complexity.infernal,
	                          Complexity.arbitrary};

        for(Complexity c : comps)
            s.ccb.specimen.put(c, oldType.buildComplexityConstraint(c));
        
		return s;
	}
	
	private void filestuff(SudokuType st){
		System.out.println(st.typeName);
		
		try {
			String type= st.getEnumType().toString();
			new XmlHelper().saveXml(st.toXmlTree(), new File("/home/timo/Code/android/Sudoq5/SudoQ/sudokus/"+type+"/"
			                                                                                         +type+".xml"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		st.fillFromXml(st.toXmlTree());
	}
	
	@Test
	public void createXmlTypes() {
		return;
		/*
		TypeStandard old = new StandardSudokuType9x9();
		SudokuType st = usual(old);
		st.setBlockDimensions(old.getBlockSize());
		filestuff(st);
		
		old = new StandardSudokuType4x4();   st = usual(old); st.setBlockDimensions(old.getBlockSize()); filestuff(st);
		old = new StandardSudokuType6x6();   st = usual(old); st.setBlockDimensions(old.getBlockSize()); filestuff(st);
		old = new StandardSudokuType16x16(); st = usual(old); st.setBlockDimensions(old.getBlockSize()); filestuff(st);
		old = new XSudoku();                 st = usual(old); st.setBlockDimensions(old.getBlockSize()); filestuff(st);
		old = new HyperSudoku();             st = usual(old); st.setBlockDimensions(old.getBlockSize()); filestuff(st);
		
//		filestuff(usual(new SamuraiSudokuType()));
		filestuff(usual(new SquigglyASudokuType9x9()));
		filestuff(usual(new SquigglyBSudokuType9x9()));
		TypeBasic oldy;
		oldy = new StairStepSudokuType9x9(); st = usual(oldy); st.setBlockDimensions(oldy.getBlockSize()); filestuff(st);
		oldy = new SamuraiSudokuType();      st = usual(oldy); st.setBlockDimensions(oldy.getBlockSize()); filestuff(st);
		*/
	}

	@Test
	public void getEnumTypeTest() {
		assertTrue(stHy.getEnumType() == SudokuTypes.HyperSudoku);
	}

	@Test
	public void getAllocationFactorTest() {
		assertTrue(stHy.getStandardAllocationFactor() == 0.25f);
	}

	@Test
	public void buildComplexityConstraintTest() {
		assertTrue(stHy.buildComplexityConstraint(null) == null);
		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.easy), new ComplexityConstraint(
				Complexity.easy, 40, 500, 800, 2)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.medium), new ComplexityConstraint(
				Complexity.medium, 32, 750, 1050, 3)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.easy), new ComplexityConstraint(
				Complexity.easy, 40, 500, 800, 2)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.difficult), new ComplexityConstraint(
				Complexity.difficult, 28, 1000, 2500, Integer.MAX_VALUE)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.infernal), new ComplexityConstraint(
				Complexity.infernal, 27, 2500, 25000, Integer.MAX_VALUE)));

		assertTrue(complexityEqual(stHy.buildComplexityConstraint(Complexity.arbitrary), new ComplexityConstraint(
				Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE)));
	}

	private boolean complexityEqual(ComplexityConstraint c1, ComplexityConstraint c2) {
		return c1.getComplexity() == c2.getComplexity() && c1.getAverageFields() == c2.getAverageFields()
				&& c1.getMinComplexityIdentifier() == c2.getMinComplexityIdentifier()
				&& c1.getMaxComplexityIdentifier() == c2.getMaxComplexityIdentifier()
				&& c1.getNumberOfAllowedHelpers() == c2.getNumberOfAllowedHelpers();
	}
}
