package de.sudoq.model.solverGenerator.FastSolver;

import java.util.List;

import de.sudoq.external.dlx.Sudoku16DLX;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

public class DLXSolver implements FastSolver{

    private boolean hasSolution;

    private List<int[][]> solutions;

    public DLXSolver(Sudoku s){
        if (s.getSudokuType().getEnumType() == SudokuTypes.standard16x16){
            Sudoku16DLX sudoku = new Sudoku16DLX();
            hasSolution = sudoku.solve(sudoku2Array(s));
            solutions = sudoku.getSolutions();
        }else
            throw new IllegalArgumentException("only 16x16 are accepted at the moment!!!");
       // SudokuDLX sudoku = new SudokuDLX();
       // sudoku.solve(hardest);

    }

    private int[][] sudoku2Array(Sudoku s){
        Position dim = s.getSudokuType().getSize();
        int[][] sarray = new int[dim.getY()][dim.getX()];
        for (int r = 0; r < sarray.length; r++)
            for (int c = 0; c < sarray[0].length; c++){
                Field f = s.getField(Position.get(c,r));
                sarray[r][c] = f.isSolved() ? f.getCurrentValue() + 1
                                            : 0;
            }
        return sarray;
    }


    @Override
    public boolean hasSolution() {
        return hasSolution;
    }

    @Override
    public boolean isAmbiguous() {
        return solutions.size() > 1;
    }

    @Override
    public PositionMap<Integer> getSolutions(){
        PositionMap<Integer> pm = new PositionMap<>(Position.get(16,16));
        int[][] solution = solutions.get(0);
        for (int r = 0; r < solution.length; r++)
            for (int c = 0; c < solution[0].length; c++){
                pm.put(Position.get(c,r), solution[r][c]-1);
            }
        return pm;
    }
}

