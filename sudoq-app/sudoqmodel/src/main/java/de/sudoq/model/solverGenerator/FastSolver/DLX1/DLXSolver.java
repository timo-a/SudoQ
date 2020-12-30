package de.sudoq.model.solverGenerator.FastSolver.DLX1;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;

public class DLXSolver implements FastSolver {

    private boolean calculationDone;

    private List<int[][]> solutions;

    private AbstractSudokuSolver solver;
    private int[][] array;

    public DLXSolver(Sudoku s){

        switch (s.getSudokuType().getEnumType()){
            case standard16x16:
                solver = new Sudoku16DLX(); break;
            case samurai:
                solver = new DLXSudokuSamurai(); break;
            case Xsudoku:
                solver = new DLXSudokuX(); break;
            case standard9x9:
                solver = new SudokuDLX(); break;

            default:
                throw new IllegalArgumentException("only 16x16 are accepted at the moment!!!");
        }

        array = sudoku2Array(s);

        // SudokuDLX solver = new SudokuDLX();
       // solver.solve(hardest);

    }

    private static int[][] sudoku2Array(Sudoku s){
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
        if (!calculationDone){
            solver.solve(array);
            solutions = solver.getSolutions();
            calculationDone=true;
        }
        return solutions.size() > 0;
    }

    @Override
    public boolean isAmbiguous() {
        if (!calculationDone){
            /*
            * If the sudoku only has one solution, we have to go through the whole search space
            * to rule out further solutions.
            * as this takes a long time we rather want to stop after x minutes and treat it as if there are no further solution
            *
            * */
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Collection c = Collections.singletonList(new AmbiguousTask());
            try {
                executor.invokeAll(c , 5, TimeUnit.MINUTES); // Timeout of 5 minutes.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.shutdown();
        }


        return solutions != null && solutions.size() > 1;
    }



    private class AmbiguousTask implements Callable{
        @Override
        public Object call() throws Exception {
            solver.solve(array);
            solutions = solver.getSolutions();
            calculationDone=true;
            return null;
        }
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

    @Deprecated
    public List<int[][]> getBothSolutionsForDebugPurposes(){
        return solutions;
    }

}

