package de.sudoq.model.sudoku.sudokuTypes;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.sudoq.model.sudoku.ConstraintBehavior;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.UniqueConstraintBehavior;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.sudoku.sudokuTypes.TypeBasic;
import de.sudoq.model.sudoku.sudokuTypes.TypeUniversal;

public class TypeUniversalTests {

	PositionMap<Integer> map = new PositionMap<Integer>(Position.get(9, 9));

	int[] su1 = { 9, 5, 8, 3, 1, 2, 7, 6, 4, 4, 6, 1, 5, 7, 9, 8, 2, 3, 3, 7, 2, 4, 6, 8, 9, 5, 1, 8, 9, 6, 1, 2, 3, 5,
			4, 7, 1, 4, 3, 7, 9, 5, 2, 8, 6, 5, 2, 7, 6, 8, 4, 3, 1, 9, 7, 8, 5, 9, 4, 1, 6, 3, 2, 2, 1, 9, 8, 3, 6, 4,
			7, 5, 6, 3, 4, 2, 5, 7, 1, 9, 8 };

	@Test
	public void initialisationTest() {
		try {
			TypeBasic t = new TestUniSudoku(null);
			fail();
		} catch (Exception e) {
		}
		try {
			Map<String, ConstraintBehavior> m = new HashMap<String, ConstraintBehavior>();
			m.put("abcd", new UniqueConstraintBehavior());
			TypeBasic t = new TestUniSudoku(m);
		} catch (Exception e) {
		}
		try {
			Map<String, ConstraintBehavior> m = new HashMap<String, ConstraintBehavior>();

			m.put("abcd", null);
			TypeBasic t = new TestUniSudoku(m);
			fail();
		} catch (Exception e) {
		}
		try {
			Map<String, ConstraintBehavior> m = new HashMap<String, ConstraintBehavior>();
			m.put(null, null);
			TypeBasic t = new TestUniSudoku(m);
			fail();
		} catch (Exception e) {
		}
		try {
			Map<String, ConstraintBehavior> m = new HashMap<String, ConstraintBehavior>();
			m.put("abc", null);
			TypeBasic t = new TestUniSudoku(m);
			fail();
		} catch (Exception e) {
		}

		assertTrue(3 != Math.pow(2 + 0.0, 2.0));
		assertTrue(4 == Math.pow(2 + 0.0, 2.0));

	}

	@Test
	public void addExtraConstraintTest() {
		Map<String, ConstraintBehavior> m = new HashMap<String, ConstraintBehavior>();
		TypeUniversal t = new TestUniSudoku(m);
		try {
			t.addExtraConstraint(null, '9', null);
			fail();
		} catch (Exception e) {
		}
		try {
			t.addExtraConstraint("abcd", '4', null);
			fail();
		} catch (Exception e) {
		}
		try {
			t.addExtraConstraint("abc", '4', null);
			fail();
		} catch (Exception e) {
		}
		try {
			t.addExtraConstraint("abc", '4', new UniqueConstraintBehavior());
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void addBlockConstraintsTest() {
		Map<String, ConstraintBehavior> m = new HashMap<String, ConstraintBehavior>();
		TypeUniversal t = new TestUniSudoku(m);
		try {
			t.addBlockConstraints(null, null);
			fail();
		} catch (Exception e) {
		}
		try {
			t.addBlockConstraints("abcd", null);
			fail();
		} catch (Exception e) {
		}
		try {
			t.addBlockConstraints("abc", null);
			fail();
		} catch (Exception e) {
		}
		try {
			t.addBlockConstraints("abc", new UniqueConstraintBehavior());
			fail();
		} catch (Exception e) {
		}
	}
}

class TestUniSudoku extends TypeUniversal {

	public TestUniSudoku(Map<String, ConstraintBehavior> m) {
		super(2, 2, m);
	}

	@Override
	public SudokuTypes getEnumType() {
		return null;
	}

	@Override
	public ComplexityConstraint buildComplexityConstraint(Complexity complexity) {
		return null;
	}

	@Override
	public float getStandardAllocationFactor() {
		return 0;
	}

}