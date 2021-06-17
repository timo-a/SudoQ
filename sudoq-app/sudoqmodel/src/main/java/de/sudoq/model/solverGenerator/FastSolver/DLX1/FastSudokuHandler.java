package de.sudoq.model.solverGenerator.FastSolver.DLX1;

import java.util.List;

public class FastSudokuHandler extends SudokuHandler {

    private final List<int[][]> solutions;

    public FastSudokuHandler(int boardSize, List<int[][]> solutions) {
        super(boardSize);
        this.solutions = solutions;
    }

    @Override
    public void handleSolution(List<DancingLinks.DancingNode> answer) {
        int[][] result = parseBoard(answer);
        solutions.add(result);
    }
}