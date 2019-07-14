package de.sudoq.model.solverGenerator.FastSolver;

import java.util.List;

import de.sudoq.external.dlx.AbstractSudokuSolver;
import de.sudoq.external.dlx.Sudoku16DLX;
import de.sudoq.external.dlx.SudokuDLX;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class DLXSolver implements FastSolver{

    private boolean hasSolution;

    private List<int[][]> solutions;

    public DLXSolver(Sudoku s){
        AbstractSudokuSolver sudoku;
        switch (s.getSudokuType().getEnumType()){
            case standard16x16:
                sudoku = new Sudoku16DLX(); break;
            case samurai:
                sudoku = new DLXSudokuSamurai(); break;
            case Xsudoku:
                sudoku = new DLXSudokuX(); break;
            case standard9x9:
                sudoku = new SudokuDLX(); break;

            default:
                throw new IllegalArgumentException("only 16x16 are accepted at the moment!!!");
        }
        sudoku.solve(sudoku2Array(s));
        solutions = sudoku.getSolutions();

        // SudokuDLX sudoku = new SudokuDLX();
       // sudoku.solve(hardest);

    }

    private int[][] sudoku2Array(Sudoku s){
        Position dim = s.getSudokuType().getSize();
        int[][] sarray = new int[dim.getY()][dim.getX()];
        for (int r = 0; r < sarray.length; r++)
            for (int c = 0; c < sarray[0].length; c++){
                Field f = s.getField(Position.get(c,r));
                sarray[r][c] = f==null ? -1 //if pos doesn't exist e.g. (9,0) in SamuraiSudoku
                                       : f.isSolved() ? f.getCurrentValue() + 1
                                                      : 0;
            }
        return sarray;
    }


    @Override
    public boolean hasSolution() {
        return solutions.size() > 0;
    }

    @Override
    public boolean isAmbiguous() {
        return solutions.size() > 1;
    }

    @Override
    public Position getAmbiguousPos() {
        int[][] first = solutions.get(0);
        int[][] second= solutions.get(1);

        for (int r = 0; r < first.length; r++) {
            for (int c = 0; c < first[0].length; c++) {
                if (first[r][c] != second[r][c])
                    return Position.get(c,r);
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public PositionMap<Integer> getSolutions(){
        int[][] solution = solutions.get(0);
        PositionMap<Integer> pm = new PositionMap<>(Position.get(solution[0].length,solution.length));

        for (int r = 0; r < solution.length; r++)
            for (int c = 0; c < solution[0].length; c++){
                if (solution[r][c] != -1)
                    pm.put(Position.get(c,r), solution[r][c]-1);
            }
        return pm;
    }
}

