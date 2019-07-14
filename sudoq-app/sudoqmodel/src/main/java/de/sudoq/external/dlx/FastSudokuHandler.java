package de.sudoq.external.dlx;

import java.util.List;

public class FastSudokuHandler extends SudokuHandler{

    private List<int[][]> solutions;

    public FastSudokuHandler(int boardSize, List<int[][]> solutions) {
        super(boardSize);
        this.solutions = solutions;
    }

    @Override
    public void handleSolution(List<DancingLinks.DancingNode> answer){
        int[][] result = parseBoard(answer);
        solutions.add(result);
    }
}