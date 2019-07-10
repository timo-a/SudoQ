package de.sudoq.model.solverGenerator.FastSolver;

import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public interface FastSolver {

    public boolean hasSolution();
    public boolean isAmbiguous();
    public PositionMap<Integer> getSolutions();
}
