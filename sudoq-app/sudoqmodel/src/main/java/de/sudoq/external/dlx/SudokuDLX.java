package de.sudoq.external.dlx;


import java.util.*;
import de.sudoq.external.dlx.DancingLinks.*;
import de.sudoq.external.dlx.SudokuHandler;

public class SudokuDLX extends AbstractSudokuSolver{

    public SudokuDLX(){
        super(9,3);
    }

    // sudoku has numbers 1-9. A 0 indicates an empty cell that we will need to
    // fill in.
    private int[][] makeExactCoverGrid(int[][] sudoku){
        int[][] R = sudokuExactCover();
        for(int i = 1; i <= S; i++){
            for(int j = 1; j <= S; j++){
                int n = sudoku[i - 1][j - 1];
                if (n != 0){ // zero out in the constraint board
                    for(int num = 1; num <= S; num++){
                        if (num != n){
                            Arrays.fill(R[getIdx(i, j, num)], 0);
                        }
                    }
                }
            }
        }
        return R;
    }

    // Returns the base exact cover grid for a SUDOKU puzzle
    private int[][] sudokuExactCover(){
        int[][] R = new int[9 * 9 * 9][9 * 9 * 4];

        int hBase = 0;

        // row-column constraints
        for(int r = 1; r <= S; r++){
            for(int c = 1; c <= S; c++, hBase++){
                for(int n = 1; n <= S; n++){
                    R[getIdx(r, c, n)][hBase] = 1;
                }
            }
        }

        // row-number constraints
        for(int r = 1; r <= S; r++){
            for(int n = 1; n <= S; n++, hBase++){
                for(int c1 = 1; c1 <= S; c1++){
                    R[getIdx(r, c1, n)][hBase] = 1;
                }
            }
        }

        // column-number constraints

        for(int c = 1; c <= S; c++){
            for(int n = 1; n <= S; n++, hBase++){
                for(int r1 = 1; r1 <= S; r1++){
                    R[getIdx(r1, c, n)][hBase] = 1;
                }
            }
        }

        // box-number constraints

        for(int br = 1; br <= S; br += side){
            for(int bc = 1; bc <= S; bc += side){
                for(int n = 1; n <= S; n++, hBase++){
                    for(int rDelta = 0; rDelta < side; rDelta++){
                        for(int cDelta = 0; cDelta < side; cDelta++){
                            R[getIdx(br + rDelta, bc + cDelta, n)][hBase] = 1;
                        }
                    }
                }
            }
        }

        return R;
    }

    // row [1,S], col [1,S], num [1,S]
    private int getIdx(int row, int col, int num){
        return (row - 1) * S * S + (col - 1) * S + (num - 1);
    }

    public void generateAllSolutions(){ // starts printing ALL valid sudokus. Obviously you'll have to abort
        int[][] cover = sudokuExactCover();
        DancingLinks dlx = new DancingLinks(cover, new SudokuHandler(S));
        dlx.runSolver();
    }


    protected void runSolver(int[][] sudoku){
        int[][] cover = makeExactCoverGrid(sudoku);
        DancingLinks dlx = new DancingLinks(cover, new SudokuHandler(S));
        dlx.runSolver();
    }


}