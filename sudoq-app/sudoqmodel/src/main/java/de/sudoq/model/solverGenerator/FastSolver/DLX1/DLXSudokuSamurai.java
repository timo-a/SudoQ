package de.sudoq.model.solverGenerator.FastSolver.DLX1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class DLXSudokuSamurai extends AbstractSudokuSolver {

    public DLXSudokuSamurai() {
        super(9+3+9, 3);
    }

    protected int[][] makeExactCoverGrid(int[][] sudoku){
        int[][] R = sudokuExactCover();
        for(int i = 1; i <= S; i++){
            for(int j = 1; j <= S; j++){
                int n = sudoku[i - 1][j - 1];
                if (n > 0){ // zero out in the constraint board
                    /* if a candidate is set at a position, set all other candidates at this position to 0 over all constraint-columns*/
                    for(int num = 1; num <= 9; num++){
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
        int nrCells = 9*9 * 4 + 3*3*5;
        int[][] R = new int[ nrCells * 9 ] //9 possible symbols for every cell -> all candidates
                           [ nrCells  //every cell needs to be filled
                           +  3 * 81  //row, col, block constraints for sudoku 1
                           +  3 * 81  //row, col, block constraints for sudoku 2
                           //deb+ 2*81
                           +  3 * 81  //row, col, block constraints for sudoku 3
                           +  3 * 81  //row, col, block constraints for sudoku 4
                           +  2 * 81  //row, col,       constraints for sudoku 5
                           +     5*9  //          block constraints for sudoku 5
                           ];
        int hBase = 0; //counts constraints

        // row-column constraints - every (defined) Field needs to be filled
        for(int r = 1; r <= 9; r++)
            for(int c = 1; c <= 9; c++, hBase++)
                for(int n = 1; n <= 9; n++)
                    R[getIdx(r, c, n)][hBase] = 1;

        for(int r = 1; r <= 9; r++)
            for(int c = 1; c <= 9; c++, hBase++)
                for(int n = 1; n <= 9; n++)
                    R[getIdx(r, c+12, n)][hBase] = 1;

        for(int r = 1; r <= 9; r++)
            for(int c = 1; c <= 9; c++, hBase++)
                for(int n = 1; n <= 9; n++)
                    R[getIdx(r+12, c, n)][hBase] = 1;

        for(int r = 1; r <= 9; r++)
            for(int c = 1; c <= 9; c++, hBase++)
                for(int n = 1; n <= 9; n++)
                    R[getIdx(r+12, c+12, n)][hBase] = 1;


        for(int r = 1; r <= 3; r++) for(int c = 1; c <= 3; c++, hBase++)
                                    for(int n = 1; n <= 9; n++) R[getIdx(r+6, c+9, n)][hBase] = 1;
        for(int r = 1; r <= 3; r++) for(int c = 1; c <= 9; c++, hBase++)
                                    for(int n = 1; n <= 9; n++) R[getIdx(r+9, c+6, n)][hBase] = 1;
        for(int r = 1; r <= 3; r++) for(int c = 1; c <= 3; c++, hBase++)
                                    for(int n = 1; n <= 9; n++) R[getIdx(r+12, c+9, n)][hBase] = 1;




        /* First Sudoku */
        hBase = setSudoku(R, hBase, 0,0);
        /* Second Sudoku */
        //hBase = setSudokuRowCol(R, hBase, 0,12);
        hBase = setSudoku(R, hBase, 0,12);
        /* Third Sudoku */
        hBase = setSudoku(R, hBase, 12,0);
        /* Foorth Sudoku */
        hBase = setSudoku(R, hBase, 12,12);

        /**/
        hBase = setSudokuRowCol(R, hBase, 6,6);
        hBase = setBlock(R, hBase, 7,10);
        hBase = setBlock(R, hBase, 10,7);
        hBase = setBlock(R, hBase, 10,10);
        hBase = setBlock(R, hBase, 10,13);
        hBase = setBlock(R, hBase, 13,10);



        return R;
    }

    private int setSudoku(int[][] R, int hBase, int rowOffset, int colOffset){
        hBase = setSudokuRowCol(R, hBase, rowOffset, colOffset);

        // box-number constraints
        hBase = setBlocks(R, hBase, rowOffset,colOffset);

        return hBase;
    }

    private int setSudokuRowCol(int[][] R, int hBase, int rowOffset, int colOffset){
        // row-number constraints of the first sudoku
        for(int r = 1; r <= 9; r++){
            for(int n = 1; n <= 9; n++, hBase++){
                for(int c1 = 1; c1 <= 9; c1++){
                    R[getIdx(r+rowOffset, c1+colOffset, n)][hBase] = 1;
                }
            }
        }

        // column-number constraints

        for(int c = 1; c <= 9; c++){
            for(int n = 1; n <= 9; n++, hBase++){
                for(int r1 = 1; r1 <= 9; r1++){
                    R[getIdx(r1+rowOffset, c+colOffset, n)][hBase] = 1;
                }
            }
        }


        return hBase;
    }

    public int setBlocks(int[][] R, int hBase, int rowOffset, int colOffset){
        // box-number constraints

        for(int br = 1; br <= 9; br += side)
            for(int bc = 1; bc <= 9; bc += side)
                hBase = setBlock(R, hBase, br + rowOffset, bc + colOffset);

        return hBase;

    }

    private int setBlock(int[][] R, int hBase, int row, int col){
        // box-number constraints

        for(int n = 1; n <= 9; n++, hBase++)
            for(int rDelta = 0; rDelta < side; rDelta++)
                 for(int cDelta = 0; cDelta < side; cDelta++) {
                     R[getIdx(row + rDelta, col + cDelta, n)][hBase] = 1;
                 }
        return hBase;

    }


    private boolean defined(int row,int col){
        return within9x9(row, col)         || within9x9(row, col-12)
                                           ||
                          within9x9(row-6, col-6)
                                           ||
               within9x9(row-12, col) || within9x9(row-12, col-12);

    }
    private boolean within9x9(int row, int col){
        return 1 <= row && row <= 9
            && 1 <= col && col <= 9;
    }

    // assigns rows in the dlx grid to the natural ((1,1) to (21,21)) positions of the samurai sudoku.
    // playable positins are assigned a row
    // unplayable positions e.g. (10,0) are undefined behaviour
    //rows are assigned in the following order (per shape left to right, top to bottom):
    // topleft-sudoku, topright-sudoku
    // bottomleft-sudoku, bottomright-sudoku,
    // middle-sudoku the cross that is still missing:
    //      top part, middle part, bottom part
    protected static int getIdx(int row, int col, int num){
        int nrSymbols = 9;
        int nrCols    = 9;

        int cellOffset =0, rowOffset=0, colOffset=0;

              if (      row <= 9 &&       col <= 9){  //topleft sudoku. no offset needed assign   0-80
        }else if (      row <= 9 && 13 <= col     ){  //topright sudoku                  assign  81-161
                                                     cellOffset =   81;                colOffset=12;
        }else if (13 <= row      &&       col <= 9){  //bottomleft sudoku                assign 162-242
                                                     cellOffset = 2*81; rowOffset=12;
        }else if (13 <= row      && 13 <= col     ){ //bottomright sudoku                assign 243-323
                                                     cellOffset = 3*81; rowOffset=12;  colOffset=12;

        //the cross in the middle that isn't assigned yet
        }else if (      row <=  9){                  // top 3x3
                                                cellOffset =  4*81   ; rowOffset= 6; colOffset=9;   nrCols = 3; //thinking in block terms now
        }else if (      row <= 12){                  //middle 3x9
                                                cellOffset = (4*81+9); rowOffset= 9; colOffset= 6;
        }else  /*(      row <= 15)*/{                //bottom 3x3
                                                cellOffset = 4*81+4*9; rowOffset=12; colOffset=9;   nrCols = 3;
        }
        //offset cells from previous sudokus. There are 9 notes per position
        return cellOffset*9
                + (row-rowOffset - 1) * nrCols * nrSymbols + (col-colOffset - 1) * nrSymbols + (num - 1);
    }

    public void solve(int[][] sudoku){
            int[][] cover = makeExactCoverGrid(sudoku);
            FastSudokuHandler fsh = new SamuraiSudokuHandler(S, solutions);
            DancingLinks dlx = new DancingLinks(cover, fsh);
            dlx.runSolver();
    }


    public static void printSudoku(int[][] sudoku){
        char c;
        for (int[] row : sudoku) {
            for (int e : row) {

                switch (e){
                    case -1: c = ' '; break;
                    case  0: c = '.'; break;
                    default: c = (char) ('0'+e); break;
                }
                System.out.print(" " + c);
            }
            System.out.println();
            
        }
    }

    public static void printCover(int[][] cover) {
        System.out.println(cover2String(cover));
    }

    public static String cover2String(int[][] cover) {
        String t = "";
        for (int[] row : cover) {
            for  (int e : row) {
                t += e + " ";
            }
            t += '\n';
        }
        return t;
    }

    public static void cover2File(int[][] cover, String path) throws IOException {
        FileWriter fw = new FileWriter(path);
        for (int[] row : cover) {
            String t = "";
            for  (int e : row) {
                t += e + " ";
            }
            fw.write(t);
        }
        fw.close();
    }


}
