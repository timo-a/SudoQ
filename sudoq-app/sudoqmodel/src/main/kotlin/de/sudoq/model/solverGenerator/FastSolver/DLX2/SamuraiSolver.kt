package de.sudoq.model.solverGenerator.FastSolver.DLX2;

import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class SamuraiSolver implements FastSolver {

    int[][] array;
    boolean calculationDone;
    Samurai samurai;
    Sudoku s;

    public SamuraiSolver(Sudoku s) {
        samurai = new Samurai();
        /* transform sudoku into array or 1-9 for symbols and 0 both for unknown and no cell */
        array = toArray(s);
        this.s = s;
    }

    private int[][] toArray(Sudoku s) {
        int[][] a = new int[21][21];
        for(Position pos : s.getSudokuType().getValidPositions()) {
            Cell f = s.getCell(pos);
            if (f != null && f.isSolved())
                a[pos.getY()][pos.getX()] = f.getCurrentValue() + 1; //s has values [0,8] so we need to add one.
        };
        return a;
    }


    @Override
    public boolean hasSolution() {
        ensureSolved();
        return samurai.solutions.size() > 0;
    }

    private void ensureSolved() {
        if (!calculationDone) {
            samurai.solve(array);
            calculationDone = true;
        }
    }

    @Override
    public PositionMap<Integer> getSolutions() {
        int[][] solution = samurai.solutions.get(0);
        return new PositionMap<>(Position.get(21, 21), s.getSudokuType().getValidPositions(),
                position -> solution[position.getY()][position.getX()] - 1);
    }

    @Override
    public boolean isAmbiguous() {
        ensureSolved();
        return samurai.solutions.size() >= 2;
    }

    @Override
    public Position getAmbiguousPos() {
        int[][] first = samurai.solutions.get(0);
        int[][] second = samurai.solutions.get(1);

        for (int r = 0; r < first.length; r++) {
            for (int c = 0; c < first[0].length; c++) {
                if (first[r][c] != second[r][c])
                    return Position.get(c, r);
            }
        }
        throw new IllegalStateException();
    }
}
