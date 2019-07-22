package de.sudoq.model.solverGenerator.FastSolver.DLX2;

////////////////////////////////////////////////////////////////////////////////
//
//  Hexadoku - Hexadoku solver.
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
//   Class Hexadoku defines some puzzles and solves them.
//
///////////////////////////////////////////////////////////////////////////////

import java.util.List;
import java.util.Stack;

public class Hexadoku
{
    List<int[][]> solutions;
	Hexadoku(){
		solutions = new Stack<>();
	}

    public static void main(String[] args)
    {
        // Create an instance.

        Hexadoku hexadoku = new Hexadoku();

        // Define some puzzles.

        int[][] puzzle1 =
                {{11, 3, 7,12, -1,15,-1,-1,  2, 6,-1, 0,  8,-1,-1,-1},
                        { 8,-1, 6,-1, -1,11,14,-1, 15,-1,-1, 4,  9, 5,-1,-1},
                        {-1,-1,-1,-1, -1,-1,-1, 0,  5,-1,-1,-1, -1, 2,15,11},
                        {-1, 0,-1, 5, -1,-1,-1,-1, -1,-1, 8,-1, 12,-1,-1, 6},

                        {-1,-1,10, 1, -1, 5,-1,14, -1,15, 4,-1,  7,-1,-1,-1},
                        { 2, 4,-1,-1, 12, 8,-1,-1, -1,-1, 6,10, -1,11,13, 1},
                        {-1,-1,-1,-1,  6,-1, 0,10, -1,12,-1,-1, -1, 4,-1,14},
                        {-1,-1, 3,-1,  4,-1,-1, 2,  9, 0, 5,14,  6,15,-1,-1},

                        {-1,-1, 9, 0, 14, 3, 7,15, 11,-1,-1, 6, -1,13,-1,-1},
                        { 5,-1, 4,-1, -1,-1, 9,-1, 10,14,-1, 8, -1,-1,-1,-1},
                        { 7, 6,12,-1,  2, 0,-1,-1, -1,-1, 3,15, -1,-1,10, 5},
                        {-1,-1,-1, 3, -1,10, 4,-1, 12,-1, 0,-1, 15, 1,-1,-1},

                        { 0,-1,-1, 9, -1,14,-1,-1, -1,-1,-1,-1,  3,-1, 5,-1},
                        { 3,12,13,-1, -1,-1,-1,11, 14,-1,-1,-1, -1,-1,-1,-1},
                        {-1,-1, 1, 7, 15,-1,-1, 8, -1,10, 9,-1, -1,14,-1,12},
                        {-1,-1,-1, 6,  0,-1,12, 3, -1,-1,13,-1, 10, 8, 2,15}};

        int[][] puzzle2 =
                {{10,-1,-1,-1, -1,-1, 4, 5,  8,-1, 6, 2, -1,12, 1, 7},
                        { 4, 3,12,-1, -1,-1,-1,14, -1,11,-1,-1, -1, 0,-1, 6},
                        {-1,-1, 9,15,  1,-1,-1,-1, -1,-1,13,-1,  4,-1,-1,-1},
                        { 2,-1,11,-1, 13,-1,10,12, -1,-1,-1, 1,  8,-1,-1, 9},

                        { 5,-1,-1,14,  8, 1, 3,15, -1, 7,-1,-1, 13,-1,-1,-1},
                        {11, 2,-1,-1, -1,-1,13,-1,  1,-1, 8,-1, -1, 4,15,-1},
                        {-1,-1,-1,-1, -1,-1,-1, 2, -1,-1,-1,-1, -1,-1, 6,-1},
                        {-1,-1,-1,-1,  7,-1,-1, 9, -1,-1, 0,10,  1, 2,-1,-1},

                        {-1,-1, 8,11,  4, 6,-1,-1,  2,-1,-1, 0, -1,-1,-1,-1},
                        {-1, 1,-1,-1, -1,-1,-1,-1,  3,-1,-1,-1, -1,-1,-1,-1},
                        {-1,14, 4,-1, -1,15,-1, 0, -1, 8,-1,-1, -1,-1,12, 1},
                        {-1,-1,-1, 9, -1,-1,11,-1, 10,15, 4, 7,  6,-1,-1, 8},

                        { 1,-1,-1, 3,  6,-1,-1,-1,  7, 2,-1,11, -1, 8,-1,15},
                        {-1,-1,-1, 5, -1, 9,-1,-1, -1,-1,-1, 6,  0, 7,-1,-1},
                        {14,-1, 6,-1, -1,-1,15,-1,  9,-1,-1,-1, -1, 1,10,11},
                        {15, 9,13,-1,  5, 7,-1,11,  4, 3,-1,-1, -1,-1,-1,14}};

        int[][] puzzle3 =
                {{  8,  0, -1,  2, -1, -1, -1, -1,  -1,  5, -1, -1, -1,  6,  3, -1},
                        {-1,  9, -1, -1,  8, -1, 10, -1,  2, -1,  6, -1, 11, -1, 15, -1 },
                        {-1, -1,  6, -1,  1, 13, -1,  0, -1, -1, -1, 12, -1, -1,  8, -1 },
                        {-1, -1, -1, -1,  2,  3, -1, -1, -1, -1, -1, -1,  7, -1, 10, -1 },

                        { 0, -1, 11, 13, -1, -1, -1, -1,  1,  3, -1, -1,  9, 10, -1, -1 },
                        {-1, -1, 15, -1,  5,  7, -1, 10, -1, -1, -1, -1, -1, -1, 11, -1 },
                        { 2,  1, -1,  6, 15, -1,  9,  8, 13, -1, 11, -1, -1, -1, -1,  3 },
                        {-1, -1, -1, -1, -1, -1, -1,  1, -1, -1,  9, -1, -1, -1, -1,  8 },

                        {13, -1, -1,  8, -1, -1, -1, 15, -1, -1, -1, -1, -1, -1,  9, 10 },
                        {11, -1, -1, -1,  4,  6, -1,  3, -1, -1,  0, -1, -1, -1, 13,  7 },
                        {-1, 10, -1,  1, 12, -1, -1, -1, -1,  8,  2, -1, -1, -1, -1, -1 },
                        { 3, -1, -1, 12, -1, -1, -1,  2, -1,  6, -1, 13, -1, 11, -1, -1 },

                        {-1, 13, 12,  3,  7, 15, -1, -1,  0, -1,  5, -1, -1, -1, -1, -1 },
                        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12 },
                        { 4, -1,  1, 11, -1, -1, 12,  6, -1, -1, -1, -1, 10, -1, -1, -1 },
                        {-1, -1, -1, 14, -1, -1,  2, -1, -1,  1, -1, -1, -1,  3,  4, -1 }};













        // Solve them.

        hexadoku.solve(puzzle1);
        hexadoku.solve(puzzle2);
        hexadoku.solve(puzzle3);
    }

    // Solve a puzzle.

    void solve(int[][] puzzle)
    {
        // Create a new Dancing Links.

        DancingLinks dl = new DancingLinks(puzzle);

        // Print the puzzle for checking.

        //printSudoku(puzzle);

        // Solve it.

        dl.solve(this);
    }

    // Print a puzzle or solution.

    void printSudoku(int[][] result)
    {
        for (int r = 0; r < PUZZLE_SIDE; r++)
        {
            for (int c = 0; c < PUZZLE_SIDE; c++)

                // Print a '.' if empty.

                if (result[r][c] < 0)
                    System.out.print(". ");

                    // Else a hex digit in upper case.

                else
                    System.out.print(Integer.toString(result[r][c],
                            16).toUpperCase() + " ");

            System.out.println();
        }

        System.out.println("-------------------------------");
    }

///////////////////////////////////////////////////////////////////////////////
//
//  Define constants for the dimensions of the puzzle.
//
///////////////////////////////////////////////////////////////////////////////

    static final int PUZZLE_SIDE = 16;
    static final int SQUARE_SIDE = 4;
    static final int PUZZLE_SIZE = 256;
    static final int COLUMN_SIZE = 1024;

///////////////////////////////////////////////////////////////////////////////
//
//  Class DancingLinks implements the Dancing Links algorithm adapted
//  for Sudoku puzzles.
//
///////////////////////////////////////////////////////////////////////////////

    class DancingLinks
    {
        Hexadoku hexadoku;
        boolean stop;
        int index;
        Column h;
        Node[] o;

        // Create a column head and add 1024 (256 x 4) columns. 4096
        // (16 x 16 x 16) rows of four nodes are added to the
        // columns. If a row is part of the puzzle it is removed from
        // the matrix and added to the solution.

        DancingLinks(int[][] p)
        {
            // Column row head.

            h = new Column(null, 0);
            Column[] m = new Column[COLUMN_SIZE];

            // Create the row of columns.

            for (int i = 0; i < COLUMN_SIZE; i++)
                m[i] = new Column(h, 0);

            // List of rows that are part of the solution.

            Node[] l = new Node[PUZZLE_SIZE];
            int i = 0;

            // For each row, column and possible digit.

            for (int r = 0; r < PUZZLE_SIDE; r++)
                for (int c = 0; c < PUZZLE_SIDE; c++)
                    for (int d = 0; d < PUZZLE_SIDE; d++)
                    {
                        // Calculate row number.

                        int k = (r * PUZZLE_SIZE) + (c * PUZZLE_SIDE) + d;

                        // Create the row of nodes.

                        Node n = new Node(m[(r * PUZZLE_SIDE) + c], k);
                        n.add(new Node(m[(PUZZLE_SIZE * 1) +
                                (r * PUZZLE_SIDE) + d], k));
                        n.add(new Node(m[(PUZZLE_SIZE * 2) +
                                (c * PUZZLE_SIDE) + d], k));
                        n.add(new Node(m[(PUZZLE_SIZE * 3) +
                                ((((r / SQUARE_SIDE) * SQUARE_SIDE) +
                                        (c / SQUARE_SIDE)) * PUZZLE_SIDE) +
                                d], k));

                        // If this row is in the puzzle, add it to the
                        // list.

                        if (p[c][r] == d)
                            l[i++] = n;
                    }

            // Create an array for the output.

            o = new Node[PUZZLE_SIZE];

            // Remove the rows in the list and add them to the output.

            for (int j = 0; j < i; j++)
            {
                l[j].remove();
                o[index++] = l[j];
            }


        }

        // Rearrange the output to match the puzzle.

        void report(int[] o)
        {
            // Create an array for the result.

            int a[][] = new int[PUZZLE_SIDE][PUZZLE_SIDE];

            // Convert the row number back to row, column, digit.

            for (int i = 0; i < PUZZLE_SIZE; i++)
            {
                int v = o[i];

                int d = v % PUZZLE_SIDE;
                int c = (v / PUZZLE_SIDE) % PUZZLE_SIDE;
                int r = (v / PUZZLE_SIZE) % PUZZLE_SIDE;

                a[c][r] = d;
            }

            // Report the result.

            //hexadoku.printSudoku(a);


        }

        // Start the search process.

        void solve(Hexadoku h)
        {
            hexadoku = h;
            search(index);
        }

        // This is the procedure search(k) from the Dancing Links
        // algorithm with an added feature to printSudoku only one
        // solution.

        void search(int k)
        {
            // If a result has already been found, return.

            if (stop)
                return;

            // If there are no more columns, printSudoku the result.

            if (h.r == h)
            {
                int[] a = new int[k];

                // Extract the row numbers.

                for (int i = 0; i < k; i++)
                    a[i] = o[i].n;

                // Report the result and set the stop flag.

                report(a);
                stop = true;
            }

            // Else find the shortest column and cover it.

            else
            {
                Column c = null;
                int s = Integer.MAX_VALUE;



                // Find the shortest column.

                for (Column j = (Column) h.r; j != h; j = (Column) j.r)
                    if (s > j.s)
                    {
                        c = j;
                        s = j.s;
                    }

                // Cover it.

                c.cover();

                // For each row in the column...

                for (Node r = c.d; r != c; r = r.d)
                {
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

    class Node
    {
        Node l;
        Node r;
        Node u;
        Node d;
        Column c;
        int n;

        // Create a self referencing node.

        Node(Column c, int n)
        {
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

        void remove()
        {
            Node n = this;

            // Cover this node's column and move on to the next right.

            do
            {
                n.c.cover();
                n = n.r;
            }

            // While we haven't got back to this node.

            while (n != this);
        }

        // Add a node to the left of this node.

        void add(Node n)
        {
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

    class Column extends Node
    {
        int s;

        // Create a self referencing column using the Node constructor.

        Column(Column c, int n)
        {
            super(null, n);

            // If the column isn't null, add this one to it.

            if (c != null)
                c.add(this);
        }

        // This is the procedure cover(c) from the Dancing Links
        // algorithm.

        void cover()
        {
            // Cover this column.

            r.l = l;
            l.r = r;

            // For all the rows in this column going down...

            for (Node i = d; i != this; i = i.d)

                // For all the nodes in this row except this one,
                // going right...

                for (Node j = i.r; j != i; j = j.r)
                {
                    // Cover this row.

                    j.u.d = j.d;
                    j.d.u = j.u;

                    // Adjust the column size.

                    j.c.s--;
                }
        }

        // This is the procedure uncover(c) from the Dancing Links
        // algorithm.

        void uncover()
        {
            // For all the rows in this column going up...

            for (Node i = u; i != this; i = i.u)

                // For all the nodes in this row except this one,
                // going left...

                for (Node j = i.l; j != i; j = j.l)
                {
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

        void add(Column c)
        {
            c.l = this.l;
            c.r = this;

            this.l.r = c;
            this.l = c;
        }

        // Add a node to the end of this column.

        void add(Node n)
        {
            n.u = this.u;
            n.d = this;

            this.u.d = n;
            this.u = n;

            // Increment the column size.

            s++;
        }
    }
}
