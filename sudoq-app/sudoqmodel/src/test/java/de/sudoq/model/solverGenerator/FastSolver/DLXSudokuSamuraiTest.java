package de.sudoq.model.solverGenerator.FastSolver;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSudokuSamurai;

class DLXSudokuSamuraiTest extends DLXSudokuSamurai {

    @Test
    void getIndex(){
        assertEquals(0, getIdx(1,1,1));
        assertEquals(1, getIdx(1,1,2));
        assertEquals(9, getIdx(1,2,1));
        assertEquals(getIdx(9, 9, 9) + 1,
                getIdx(1, 13, 1), "sudoku1 last entry should be followed by sudoku2 first entry");
        assertEquals(getIdx(9,21,9) + 1,
                getIdx(13,1,1),"sudoku2 last entry should be followed by sudoku3 first entry"); // 1458
        assertEquals(getIdx(21,9,9) + 1,
                getIdx(13,13,1), "sudoku3 last entry should be followed by sudoku4 first entry"); // 2187

        assertEquals(getIdx(21,21,9) + 1,
                getIdx( 7,10,1),"sudoku4 last entry should be followed by sudoku5-top first entry"); // 2187

        assertEquals(getIdx( 9,12,9)+1,
                getIdx(10, 7,1),"sudoku5-top last entry should be followed by sudoku5-middle first entry"); // 2187
        assertEquals(getIdx(12, 15,9)+1,
                getIdx(13,10,1), "sudoku5-middle last entry should be followed by sudoku5-bottom first entry"); // 2187
        assertEquals(3320,getIdx(15,12,9)); // 2915


    }
}
