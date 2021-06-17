package de.sudoq.model.solverGenerator.FastSolver.DLX1;

import java.util.List;

public class SamuraiSudokuHandler extends FastSudokuHandler {
    public SamuraiSudokuHandler(int boardSize, List<int[][]> solutions) {
        super(boardSize, solutions);
    }

    public static void printResult(int[][] cover) {
        for (int[] row : cover) {
            for (int e : row) {
                System.out.print(e != 0 ? String.format("%1$2s ", e)
                        : "   ");
            }
            System.out.println();
        }
    }

    protected int[][] parseBoard(List<DancingLinks.DancingNode> answer) {
        /* every row in the table we passed to the dancing links algorithm
         * represents a possible candidate at a position in the sudoku.
         * The `rows` of linked nodes in the output represent the actual candidates in the solution.
         * The first `#cells` columns are constraints that require for every cell 1 to `#cells`
         * that it is filled with a number.
         * After that come the row/col/block constraints, each taking up 9 columns:
         *  each column requiring one symbol to be contained in the row/col/block.
         * From the first part, we infer the position (by inverting {@Code DLXSudokuSamurai.getIndex})
         * From the second part we infer the value (all numbers have their constraints in the same order -> modulo 9)
         *
         * */

        int[][] result = new int[size][size];
        for (DancingLinks.DancingNode n : answer) {
            DancingLinks.DancingNode rcNode = n;

            /* We supply the dancing links algorithm with a table but get back a linked list that has no leftmost node ecause you can always go left
             *  the columns are numbered starting from 0 to ...
             *  so in order go get to the leftmost node we cycle through and take the node with the minimum column name
             *  */
            int min = Integer.parseInt(rcNode.C.name);
            for (DancingLinks.DancingNode tmp = n.R; tmp != n; tmp = tmp.R) {
                int val = Integer.parseInt(tmp.C.name);
                if (val < min) {
                    min = val;
                    rcNode = tmp;
                }
            }
            //infer coordinates from col-index
            int ans1 = Integer.parseInt(rcNode.C.name);
            int[] rc = getCoordinates(ans1);
            int r = rc[0];
            int c = rc[1];
            //infer value from col-index of next column referencing the position
            int ans2 = Integer.parseInt(rcNode.R.C.name);

            ans2 -= 369; //subtract number of cells (here it is a multiple of #cells so probably unnecessary but makes it clearer)
            int num = (ans2 % 9) + 1; //add one -> [1,9]
            result[r][c] = num;
        }
        //printResult(result);
        //System.out.println();
        /*int [][] check = new int[21][21];
        for (int i = 0; i < 369; i++) {
            int[] rc = getCoordinates(i);
            check[rc[0]][rc[1]] = i;
        }
        for (int[] row : check) {
            for  (int e : row) {
                System.out.print( e != 0 ? String.format("%1$4s ", e)
                        : "     " );
            }
            System.out.println();
        }

        System.exit(9);*/
        return result;
    }

    private int[] getCoordinates(int i) {
        if (i < 0)
            throw new IllegalArgumentException();
        if (i < 81) {
            return getCoordinates9x9(i, 0, 0);
        } else if (i < 81 * 2) {
            return getCoordinates9x9(i % 81, 0, 12);
        } else if (i < 81 * 3) {
            return getCoordinates9x9(i % 81, 12, 0);
        } else if (i < 81 * 4) {
            return getCoordinates9x9(i % 81, 12, 12);
        } else if (i < 81 * 4 + 9) {
            i -= 81 * 4;
            return new int[]{6 + i / 3, 9 + i % 3};
        } else if (i < 81 * 4 + 9 + 3 * 9) {
            i -= 81 * 4 + 9;
            return new int[]{9 + i / 9, 6 + i % 9};
        } else if (i < 81 * 4 + 9 + 3 * 9 + 9) {
            i -= 81 * 4 + 9 + 3 * 9;
            return new int[]{12 + i / 3, 9 + i % 3};
        } else
            return null;

    }

    private int[] getCoordinates9x9(int i, int offsetR, int offsetC) {
        return new int[]{offsetR + i / 9, offsetC + i % 9};
    }

}
