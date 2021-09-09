package de.sudoq.model.solverGenerator.FastSolver.DLX2;

import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class Standard16Solver implements FastSolver {

    int[][] array;
    boolean calculationDone;
    Hexadoku hexadoku;
    Sudoku s;

    public Standard16Solver(Sudoku s) {
        hexadoku = new Hexadoku();
        /* transform sudoku into array or 1-9 for symbols and 0 both for unknown and no cell */
        array = toArray(s);
        this.s = s;
    }

    private int[][] toArray(Sudoku s) {
        int[][] a = new int[16][16];
        Cell f;
        for (int y = 0; y < 16; y++)
            for (int x = 0; x < 16; x++) {
                f = s.getCell(Position.get(x, y));
                if (f != null && f.isSolved())
                    a[y][x] = f.getCurrentValue(); //s has values [0,8] so we need to add one.
            }
        return a;
    }


    @Override
    public boolean hasSolution() {
        ensureSolved();
        return hexadoku.solutions.size() > 0;
    }

    private void ensureSolved() {
        if (!calculationDone) {
            hexadoku.solve(array);
            calculationDone = true;
        }
    }

    @Override
    public PositionMap<Integer> getSolutions() {
        int[][] solution = hexadoku.solutions.get(0);
        PositionMap<Integer> pm = new PositionMap<>(Position.get(16, 16));
        for (Position p : s.getSudokuType().getValidPositions()) {
            pm.put(p, solution[p.getY()][p.getX()] - 1);
        }

        return pm;
    }

    @Override
    public boolean isAmbiguous() {
        ensureSolved();
        return hexadoku.solutions.size() >= 2;
    }

    @Override
    public Position getAmbiguousPos() {
        int[][] first = hexadoku.solutions.get(0);
        int[][] second = hexadoku.solutions.get(1);

        for (int r = 0; r < first.length; r++) {
            for (int c = 0; c < first[0].length; c++) {
                if (first[r][c] != second[r][c])
                    return Position.get(c, r);
            }
        }
        throw new IllegalStateException();
    }
}
