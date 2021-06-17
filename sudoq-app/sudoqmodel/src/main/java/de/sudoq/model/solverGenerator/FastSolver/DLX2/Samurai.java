package de.sudoq.model.solverGenerator.FastSolver.DLX2;////////////////////////////////////////////////////////////////////////////////
//
//  Samurai - Samurai Sudoku solver.
//
//  Copyright (C) 2006	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//
//   Samurai Sudoku puzzles are made up of five overlapping puzzles
//   like this:
//
//      +---+---+---+   +---+---+---+
//      |   |   |   |   |   |   |   |
//      +---+---+---+   +---+---+---+
//      |   | 1 |   |   |   | 2 |   |
//      +---+---+---+---+---+---+---+
//      |   |   | A |   | B |   |   |
//      +---+---+---+---+---+---+---+
//              |   | 3 |   |
//      +---+---+---+---+---+---+---+
//      |   |   | C |   | D |   |   |
//      +---+---+---+---+---+---+---+
//      |   | 4 |   |   |   | 5 |   |
//      +---+---+---+   +---+---+---+
//      |   |   |   |   |   |   |   |
//      +---+---+---+   +---+---+---+
//
//   The slots in the four squares labelled A, B, C, D that overlap
//   the centre and outer four puzzles must be solved for both
//   puzzles.
//
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//
//   Class Samurai defines some puzzles from the UK Saturday Times and
//   solves them.
//
///////////////////////////////////////////////////////////////////////////////

import java.util.List;
import java.util.Stack;

class Samurai {
    static final int PUZZLE_SIDE = 21;
    static final int PUZZLE_SIZE = 441;
    static final int SUDOKU_SIDE = 9;
    static final int SUDOKU_SIZE = 81;


    // Solve a puzzle.
    static final int SQUARE_SIDE = 3;

    // Print the solution.
    static final int COLUMN_SIZE = 1692;

///////////////////////////////////////////////////////////////////////////////
//
//  Define constants for the dimensions of the puzzle.
//
///////////////////////////////////////////////////////////////////////////////
    static final int[][] SAMURAI_SQUARE =
            {{0}, {0}, {0}, {}, {1}, {1}, {1},
                    {0}, {0}, {0}, {}, {1}, {1}, {1},
                    {0}, {0}, {0, 2}, {2}, {1, 2}, {1}, {1},
                    {}, {}, {2}, {2}, {2}, {}, {},
                    {3}, {3}, {2, 3}, {2}, {2, 4}, {4}, {4},
                    {3}, {3}, {3}, {}, {4}, {4}, {4},
                    {3}, {3}, {3}, {}, {4}, {4}, {4}};
    static final int[][] SUDOKU_ROW =
            {{0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {-1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8}};
    static final int[][] SUDOKU_COLUMN =
            {{0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {-1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {0, 1, 2, 3, 4, 5, 6, 7, 8},
                    {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8}};
    List<int[][]> solutions;
    Samurai() {
        solutions = new Stack<>();
    }

    public static void main(String[] args) {
        // Create an instance.

        Samurai samurai = new Samurai();

        // Define some puzzles.

        int[][] puzzle1 =
                {{0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 6, 0, 0},
                        {0, 0, 7, 3, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 5},
                        {0, 5, 0, 0, 9, 0, 0, 6, 3, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 4, 0},

                        {0, 8, 0, 0, 7, 3, 0, 0, 0, 0, 0, 0, 0, 4, 0, 3, 0, 0, 8, 0, 0},
                        {0, 0, 6, 2, 0, 0, 8, 0, 0, 0, 0, 0, 7, 0, 0, 0, 9, 0, 5, 0, 0},
                        {4, 0, 0, 5, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 7, 0},

                        {0, 3, 0, 0, 5, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                        {0, 0, 8, 0, 0, 9, 0, 3, 0, 0, 4, 9, 6, 8, 0, 4, 0, 0, 2, 0, 0},
                        {0, 0, 9, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 8, 0},

                        {0, 0, 0, 0, 0, 0, 9, 0, 0, 6, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 0},

                        {0, 0, 0, 0, 3, 0, 0, 4, 0, 0, 1, 5, 8, 0, 0, 0, 0, 6, 0, 0, 0},
                        {0, 0, 0, 9, 0, 0, 0, 2, 0, 3, 0, 0, 0, 9, 0, 0, 0, 4, 0, 0, 6},
                        {0, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 2, 0},

                        {0, 0, 0, 3, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 3, 0, 0},
                        {7, 0, 0, 0, 1, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 7, 0, 0},
                        {0, 6, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 3, 5, 6, 0, 0, 0, 0, 8, 0},

                        {3, 0, 0, 2, 9, 0, 0, 7, 0, 0, 0, 0, 0, 0, 0, 2, 6, 0, 0, 0, 1},
                        {0, 0, 5, 0, 0, 4, 0, 0, 8, 0, 0, 0, 0, 0, 3, 0, 0, 5, 0, 0, 0},
                        {0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 0, 9, 0, 0}};

        int[][] puzzle2 =
                {{4, 9, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 8, 0, 0, 0, 6, 7, 9},
                        {8, 0, 0, 0, 0, 0, 0, 9, 7, 0, 0, 0, 0, 4, 0, 8, 0, 0, 0, 0, 3},
                        {1, 0, 0, 0, 0, 0, 6, 0, 8, 0, 0, 0, 0, 0, 2, 0, 9, 0, 0, 0, 4},

                        {0, 0, 0, 3, 0, 5, 0, 7, 0, 0, 0, 0, 0, 0, 0, 2, 0, 6, 0, 0, 0},
                        {0, 0, 5, 0, 4, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0},
                        {0, 8, 0, 2, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 9, 0, 0, 0},

                        {7, 0, 3, 0, 0, 0, 0, 0, 5, 0, 0, 0, 7, 0, 0, 0, 2, 0, 1, 0, 0},
                        {2, 6, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 7, 0, 2, 0},
                        {0, 0, 0, 0, 0, 0, 7, 1, 6, 0, 5, 0, 4, 2, 9, 0, 0, 0, 7, 3, 0},

                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0},

                        {0, 9, 7, 0, 0, 0, 3, 4, 8, 0, 1, 0, 9, 7, 6, 0, 0, 0, 0, 0, 0},
                        {0, 3, 0, 7, 0, 0, 0, 0, 9, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 9, 2},
                        {0, 0, 4, 0, 9, 0, 0, 0, 7, 0, 0, 0, 3, 0, 0, 0, 0, 0, 1, 0, 8},

                        {0, 0, 0, 8, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 9, 0, 8, 0},
                        {0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 8, 0, 4, 0, 0},
                        {0, 0, 0, 5, 0, 7, 0, 0, 0, 0, 0, 0, 0, 2, 0, 4, 0, 1, 0, 0, 0},

                        {7, 0, 0, 0, 5, 0, 2, 0, 0, 0, 0, 0, 7, 0, 5, 0, 0, 0, 0, 0, 6},
                        {5, 0, 0, 0, 0, 9, 0, 8, 0, 0, 0, 0, 2, 8, 0, 0, 0, 0, 0, 0, 9},
                        {4, 6, 9, 0, 0, 0, 5, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 5}};

        int[][] mypuzzle = {
                {0, 0, 0, 0, 2, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 6, 0, 0},
                {0, 6, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0},
                {5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0},
                {7, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 8, 4, 2, 0, 0, 0, 0},
                {0, 0, 8, 0, 1, 6, 0, 0, 0, 0, 0, 0, 1, 0, 0, 5, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 5, 0},
                {1, 8, 9, 0, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 7, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 5, 0, 4, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 9, 6, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 4, 7, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {7, 0, 9, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 7, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 9, 0, 0, 2, 0, 0},
                {0, 5, 0, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 6, 0, 8, 0, 0, 9, 0},
                {8, 0, 0, 2, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 5, 0, 4},
                {0, 2, 3, 0, 1, 6, 5, 0, 0, 0, 0, 0, 0, 4, 7, 0, 2, 0, 0, 0, 0},
                {5, 1, 7, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 3, 1, 4, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };

        int[][] mypuzzle1 = new int[21][21];

        // Solve them.

        samurai.solve(puzzle1);
        samurai.solve(puzzle2);
        samurai.solve(mypuzzle);
        for (int i = 1; i < 9; i++)
            samurai.solve(shift(mypuzzle, i));
    }

///////////////////////////////////////////////////////////////////////////////
//
//   Declare an array which defines which squares are in which puzzle,
//   including those in two puzzles, and those that aren't in any
//   puzzle, an array which defines which puzzle row a samurai row is
//   in, and an array which defines which puzzle column a samurai
//   column is in.
//
///////////////////////////////////////////////////////////////////////////////

    static int[][] shift(int[][] orig, int offset) {
        int[][] newpuzzle = new int[21][21];
        for (int r = 0; r < 21; r++)
            for (int c = 0; c < 21; c++)
                if (orig[r][c] != 0)
                    newpuzzle[r][c] = (orig[r][c] + offset - 1) % 9 + 1;

        return newpuzzle;
    }

    public static void printSudoku(int[][] solution) {
        for (int r = 0; r < PUZZLE_SIDE; r++) {
            for (int c = 0; c < PUZZLE_SIDE; c++)
                if (solution[r][c] > 0)
                    System.out.print(solution[r][c] + " ");

                else
                    System.out.print(". ");

            System.out.println();
        }

        System.out.println("-----------------------------------------");
    }

    void solve(int[][] puzzle) {
        // Create a new Dancing Links.

        DancingLinks dl = new DancingLinks(puzzle);

        // Print the puzzle for checking.

        //printSudoku(puzzle);

        // Solve the puzzle.

        dl.solve(this);
    }

///////////////////////////////////////////////////////////////////////////////
//
//  Class DancingLinks implements the Dancing Links algorithm adapted
//  for Samurai Sudoku puzzles.
//
///////////////////////////////////////////////////////////////////////////////

    class DancingLinks {
        Samurai samurai;
        boolean stop;
        int index;
        Column h;
        Node[] o;

        // Create a column head and add 1692 (21 x 21 = 441 slots + 5
        // puzzles x 9 rows x 9 digits = 405 + 5 puzzles x 9 columns x
        // 9 digits = 405 + 49 slots x 9 digits = 441) columns. Empty
        // columns will be removed. 3645 (9 rows x 9 columns x 9
        // digits x 5 puzzles) rows of nodes are added to the
        // columns. Those rows corresponding to slots in one puzzle
        // will have four nodes, those overlapping two puzzles will
        // have six nodes. If a row is part of the puzzle it is
        // removed from the matrix and added to the solution.

        DancingLinks(int[][] p) {
            // Column row head.

            h = new Column(null, 0);
            Column[] m = new Column[COLUMN_SIZE];

            // Create the row of columns.

            for (int i = 0; i < COLUMN_SIZE; i++)
                m[i] = new Column(h, 0);

            // List of rows that are part of the solution.

            Node[] l = new Node[PUZZLE_SIZE];
            int i = 0;

            // For each samurai (big) row, column and possible digit.

            for (int r = 0; r < PUZZLE_SIDE; r++)
                for (int c = 0; c < PUZZLE_SIDE; c++)
                    for (int d = 0; d < SUDOKU_SIDE; d++) {
                        // Calculate row number for possible
                        // move in samurai (big) puzzle.

                        int k = 1 + (r * PUZZLE_SIDE * SUDOKU_SIDE) +
                                (c * SUDOKU_SIDE) + d;

                        // See what samurai (big) square we're in.

                        int s = (c / 3) + ((r / 3) * 7);

                        // If the slot is in a puzzle create a row of
                        // nodes.

                        if (SAMURAI_SQUARE[s].length > 0) {
                            // Create a node for the slot.

                            Node n = new Node(m[(r * PUZZLE_SIDE) + c], k);

                            // For each puzzle that this slot is in...

                            for (int j = 0; j < SAMURAI_SQUARE[s].length; j++) {
                                // Find which puzzle the slot is in.

                                int pz = SAMURAI_SQUARE[s][j];

                                // Find the puzzle row and column.

                                int pr = SUDOKU_ROW[pz][r];
                                int pc = SUDOKU_COLUMN[pz][c];

                                // Add a node for the puzzle, row and
                                // digit.

                                n.add(new Node(m[PUZZLE_SIZE +
                                        (pz * SUDOKU_SIZE) +
                                        (pr * SUDOKU_SIDE) + d], k));

                                // Add a node for the puzzle, column
                                // and digit.

                                n.add(new Node(m[PUZZLE_SIZE + 405 +
                                        (pz * SUDOKU_SIZE) +
                                        (pc * SUDOKU_SIDE) + d], k));
                            }

                            // Add a node for the samurai (big) square
                            // and digit.

                            n.add(new Node(m[PUZZLE_SIZE + 405 + 405 +
                                    (s * SUDOKU_SIDE) + d], k));

                            // If this row is in the puzzle, add it to the
                            // list.

                            if (p[c][r] == (d + 1))
                                l[i++] = n;
                        }
                    }

            // There will be empty columns corresponding to the unused
            // slots in the samurai (big) puzzle. Remove the empty
            // columns.

            for (Column c = (Column) h.r; c != h; c = (Column) c.r)
                if (c.s == 0)
                    c.cover();

            // Create an array for the output.

            o = new Node[PUZZLE_SIZE];

            // Remove the rows in the list and add them to the output.

            for (int j = 0; j < i; j++) {
                l[j].remove();
                o[index++] = l[j];
            }

        }

        // Rearrange the output to match the puzzle.

        void report(int[] o) {
            // Create an array for the result.

            int[][] a = new int[PUZZLE_SIDE][PUZZLE_SIDE];

            // Convert the row number back to row, column, digit.

            for (int i = 0; i < o.length; i++) {
                int v = o[i];

                int d = v % SUDOKU_SIDE;
                int c = (v / SUDOKU_SIDE) % PUZZLE_SIDE;
                int r = (v / (PUZZLE_SIDE * SUDOKU_SIDE)) % PUZZLE_SIDE;

                a[c][r] = d + 1;
            }

            solutions.add(a);
            //System.out.println("found solution");
        }

        // Start the search process.

        void solve(Samurai s) {
            samurai = s;
            search(index);
        }

        // This is the procedure search(k) from the Dancing Links
        // algorithm with an added feature to printSudoku only one
        // solution.

        void search(int k) {
            // If a result has already been found, return.

            if (stop)
                return;

            // If there are no more columns, printSudoku the result.

            if (h.r == h) {
                int[] a = new int[k];

                // Extract the row numbers.

                for (int i = 0; i < k; i++)
                    a[i] = o[i].n - 1;

                // Report the result and set the stop flag.

                report(a);
                if (solutions.size() >= 2)
                    stop = true;
            }

            // Else find the shortest column and cover it.

            else {
                Column c = null;
                int s = Integer.MAX_VALUE;

                // Find the shortest column.

                for (Column j = (Column) h.r; j != h; j = (Column) j.r)
                    if (s > j.s) {
                        c = j;
                        s = j.s;
                    }

                // Cover it.

                c.cover();

                // For each row in the column...

                for (Node r = c.d; r != c; r = r.d) {
                    // Skip this if a result has been found.

                    if (stop)
                        break;

                    // Save the row in the output array.

                    o[k] = r;

                    // For each node in this row, cover it's column.

                    for (Node j = r.r; j != r; j = j.r)
                        j.c.cover();

                    // Recurse with k + 1.

                    search(k + 1);

                    // For each node in this row, uncover it's column.

                    for (Node j = r.l; j != r; j = j.l)
                        j.c.uncover();
                }

                // Uncover the column.

                c.uncover();
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////
//
//  Class Node has four links: left, right, up, down, which reference
//  adjacent nodes, a link which references the column and a row
//  number.
//
///////////////////////////////////////////////////////////////////////////////

    class Node {
        Node l;
        Node r;
        Node u;
        Node d;
        Column c;
        int n;

        // Create a self referencing node.

        Node(Column c, int n) {
            this.l = this;
            this.r = this;

            this.u = this;
            this.d = this;

            // Column and row number.

            this.c = c;
            this.n = n;

            // If the column isn't null, add this node to it.

            if (c != null)
                c.add(this);
        }

        // Remove a row of nodes.

        void remove() {
            Node n = this;

            // Cover this node's column and move on to the next right.

            do {
                n.c.cover();
                n = n.r;
            }

            // While we haven't got back to this node.

            while (n != this);
        }

        // Add a node to the left of this node.

        void add(Node n) {
            n.l = this.l;
            n.r = this;

            this.l.r = n;
            this.l = n;
        }
    }

///////////////////////////////////////////////////////////////////////////////
//
//  Class Column inherits the links from the Node class and has a column size.
//
///////////////////////////////////////////////////////////////////////////////

    class Column extends Node {
        int s;

        // Create a self referencing column using the Node constructor.

        Column(Column c, int n) {
            super(null, n);

            if (c != null)
                c.add(this);
        }

        // This is the procedure cover(c) from the Dancing Links
        // algorithm.

        void cover() {
            // Cover this column.

            r.l = l;
            l.r = r;

            // For all the rows in this column going down...

            for (Node i = d; i != this; i = i.d)

                // For all the nodes in this row except this one,
                // going right...

                for (Node j = i.r; j != i; j = j.r) {
                    // Cover this row.

                    j.u.d = j.d;
                    j.d.u = j.u;

                    // Adjust the column size.

                    j.c.s--;
                }
        }

        // This is the procedure uncover(c) from the Dancing Links
        // algorithm.

        void uncover() {
            // For all the rows in this column going up...

            for (Node i = u; i != this; i = i.u)

                // For all the nodes in this row except this one,
                // going left...

                for (Node j = i.l; j != i; j = j.l) {
                    // Uncover this row.

                    j.u.d = j;
                    j.d.u = j;

                    // Adjust the column size.

                    j.c.s++;
                }

            // Uncover this column.

            r.l = this;
            l.r = this;
        }

        // Add a column to the left of this column.

        void add(Column c) {
            c.l = this.l;
            c.r = this;

            this.l.r = c;
            this.l = c;
        }

        // Add a node to the end of this column.

        void add(Node n) {
            n.u = this.u;
            n.d = this;

            this.u.d = n;
            this.u = n;

            // Increment the column size.

            s++;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
