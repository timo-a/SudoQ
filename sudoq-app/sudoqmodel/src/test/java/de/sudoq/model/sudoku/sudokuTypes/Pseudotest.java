package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

public class Pseudotest {

	SudokuType stHy = TypeBuilder.getType(SudokuTypes.HyperSudoku);

	public SudokuType usual(SudokuType oldType){

        SudokuType s = new SudokuType(oldType.getEnumType(), 9, 0f, Position.get(9,9),
                Position.get(1,1), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ComplexityConstraintBuilder());
		s.setNumberOfSymbols(oldType.getNumberOfSymbols());
		s.setDimensions(oldType.getSize());
		//s.standardAllocationFactor = oldType.getStandardAllocationFactor();
		for(Constraint c : oldType)
			s.addConstraint(c);
		for (PermutationProperties p : oldType.getPermutationProperties())
			s.getPermutationProperties().add(p);
		
		Complexity[] comps = {Complexity.easy,
	                          Complexity.medium,
	                          Complexity.difficult,
	                          Complexity.infernal,
	                          Complexity.arbitrary};

        for(Complexity c : comps)
            s.ccb.getSpecimen().put(c, oldType.buildComplexityConstraint(c));
        
		return s;
	}

    /*    private void filestuff(SudokuType st){
            System.out.println(st.getEnumType());
            
            try {
                String type= st.getEnumType().toString();
                new XmlHelper().saveXml(st.toXmlTree(), new File("/home/timo/Code/android/Sudoq5/SudoQ/sudokus/"+type+"/"
                                                                                                         +type+".xml"));
            } catch (IOException e) {
                
                e.printStackTrace();
            }
            st.fillFromXml(st.toXmlTree());
        }*/
    
    @Test
    void createXmlTypes() {
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
    void getEnumTypeTest() {
		assertSame(stHy.getEnumType(), SudokuTypes.HyperSudoku);
	}

    @Test
    void getAllocationFactorTest() {
        assertEquals(0.25f, stHy.getStandardAllocationFactor());
	}

    @Test
    void buildComplexityConstraintInitializedWithNullShouldReturnNull() {
		assertNull(stHy.buildComplexityConstraint(null), "passing null to buildComplexityConstraint should return null.");
	}

    //This tests just specification, is such a test relevant?
    @Test
    void buildComplexityConstraintEasy() {
		ComplexityConstraint reference = new ComplexityConstraint(
				Complexity.easy, 40, 500, 1500, 2);
		ComplexityConstraint test = stHy.buildComplexityConstraint(Complexity.easy);
		assertTrue(complexityEqual(test, reference));
	}

    @Test
    void buildComplexityConstraintMedium() {
		ComplexityConstraint reference = new ComplexityConstraint(
				Complexity.medium, 32, 1500, 3500, 3);
		ComplexityConstraint test = stHy.buildComplexityConstraint(Complexity.medium);
		assertTrue(complexityEqual(test, reference));
	}

    @Test
    void buildComplexityConstraintDifficult() {
		ComplexityConstraint reference = new ComplexityConstraint(
				Complexity.difficult, 28, 3500, 6000, Integer.MAX_VALUE);
		ComplexityConstraint test = stHy.buildComplexityConstraint(Complexity.difficult);
		assertTrue(complexityEqual(test, reference));
	}

    @Test
    void buildComplexityConstraintInfernal() {
		ComplexityConstraint reference = new ComplexityConstraint(
				Complexity.infernal, 27, 6000, 25000, Integer.MAX_VALUE);
		ComplexityConstraint test = stHy.buildComplexityConstraint(Complexity.infernal);
		assertTrue(complexityEqual(test, reference));
	}

    @Test
    void buildComplexityConstraintArbitrary() {
		ComplexityConstraint reference = new ComplexityConstraint(
				Complexity.arbitrary, 32, 1, Integer.MAX_VALUE, Integer.MAX_VALUE);
		ComplexityConstraint test = stHy.buildComplexityConstraint(Complexity.arbitrary);
		assertTrue(complexityEqual(test, reference));
	}

	private boolean complexityEqual(ComplexityConstraint c1, ComplexityConstraint c2) {
		return c1.getComplexity() == c2.getComplexity() && c1.getAverageCells() == c2.getAverageCells()
				&& c1.getMinComplexityIdentifier() == c2.getMinComplexityIdentifier()
				&& c1.getMaxComplexityIdentifier() == c2.getMaxComplexityIdentifier()
				&& c1.getNumberOfAllowedHelpers() == c2.getNumberOfAllowedHelpers();
	}
}
