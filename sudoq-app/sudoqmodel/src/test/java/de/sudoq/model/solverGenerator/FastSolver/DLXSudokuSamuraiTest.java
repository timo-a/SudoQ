package de.sudoq.model.solverGenerator.FastSolver;

import org.junit.Test;

import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSudokuSamurai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DLXSudokuSamuraiTest extends DLXSudokuSamurai {

    @Test
    public void testGetIndex(){
        assertEquals(0, getIdx(1,1,1));
        assertEquals(1, getIdx(1,1,2));
        assertEquals(9, getIdx(1,2,1));
        assertEquals("sudoku1 last entry should be followed by sudoku2 first entry",
                getIdx(9, 9, 9) + 1, getIdx(1, 13, 1));
        assertEquals("sudoku2 last entry should be followed by sudoku3 first entry",
                getIdx(9,21,9) + 1,getIdx(13,1,1)); // 1458
        assertEquals("sudoku3 last entry should be followed by sudoku4 first entry",
                getIdx(21,9,9) + 1, getIdx(13,13,1)); // 2187

        assertEquals("sudoku4 last entry should be followed by sudoku5-top first entry",
                getIdx(21,21,9) + 1,getIdx( 7,10,1)); // 2187

        assertEquals("sudoku5-top last entry should be followed by sudoku5-middle first entry",
                getIdx( 9,12,9)+1,getIdx(10, 7,1)); // 2187
        assertEquals("sudoku5-middle last entry should be followed by sudoku5-bottom first entry",
                getIdx(12, 15,9)+1, getIdx(13,10,1)); // 2187
        assertEquals(3320,getIdx(15,12,9)); // 2915


    }
}
