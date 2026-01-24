package de.sudoq.model.sudoku.sudokuTypes

import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.complexity.ComplexityConstraint
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Pseudotest {
    var stHy: SudokuType = TypeBuilder.getType(SudokuTypes.HyperSudoku)

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
    fun createXmlTypes() {
        return
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
    fun getEnumTypeTest() {
        Assertions.assertSame(stHy.enumType, SudokuTypes.HyperSudoku)
    }

    @Test
    fun getAllocationFactorTest() {
        Assertions.assertEquals(0.25f, stHy.getStandardAllocationFactor())
    }

    //This tests just specification, is such a test relevant?
    @Test
    fun buildComplexityConstraintEasy() {
        val reference = ComplexityConstraint(
            Complexity.easy, 40, 500, 1500, 2
        )
        val test = stHy.buildComplexityConstraint(Complexity.easy)
        complexityEqual(test!!, reference)
    }

    @Test
    fun buildComplexityConstraintMedium() {
        val reference = ComplexityConstraint(
            Complexity.medium, 32, 1500, 3500, 3
        )
        val test = stHy.buildComplexityConstraint(Complexity.medium)
        complexityEqual(test!!, reference)
    }

    @Test
    fun buildComplexityConstraintDifficult() {
        val reference = ComplexityConstraint(
            Complexity.difficult, 28, 3500, 6000, Int.MAX_VALUE
        )
        val test = stHy.buildComplexityConstraint(Complexity.difficult)
        complexityEqual(test!!, reference)
    }

    @Test
    fun buildComplexityConstraintInfernal() {
        val reference = ComplexityConstraint(
            Complexity.infernal, 27, 6000, 25000, Int.MAX_VALUE
        )
        val test = stHy.buildComplexityConstraint(Complexity.infernal)
        complexityEqual(test!!, reference)
    }

    @Test
    fun buildComplexityConstraintArbitrary() {
        val reference = ComplexityConstraint(
            Complexity.arbitrary, 32, 1, Int.MAX_VALUE, Int.MAX_VALUE
        )
        val test = stHy.buildComplexityConstraint(Complexity.arbitrary)
        complexityEqual(test!!, reference)
    }

    private fun complexityEqual(c1: ComplexityConstraint, c2: ComplexityConstraint) {
        c1.complexity `should be equal to` c2.complexity
        c1.averageCells `should be equal to` c2.averageCells
        c1.minComplexityIdentifier `should be equal to` c2.minComplexityIdentifier
        c1.maxComplexityIdentifier `should be equal to` c2.maxComplexityIdentifier
        c1.numberOfAllowedHelpers `should be equal to` c2.numberOfAllowedHelpers
    }
}
