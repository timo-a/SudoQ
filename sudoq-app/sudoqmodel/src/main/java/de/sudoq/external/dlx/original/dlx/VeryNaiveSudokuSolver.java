package dlx;

import java.util.Arrays;

// Warning, this will never work since it's too slow, only for
// theorethical purposes

public class VeryNaiveSudokuSolver extends AbstractSudokuSolver{
    
    private void solve(int[][] sudoku, int idx){
        int size = sudoku.length;
        if(idx == size*size){
            if(isSolution(sudoku)){
                System.out.println("Found a solution via very naive algorithm: ");
                printSolution(sudoku);
                System.out.println();
            }
        } else{
            int row = idx / size;
            int col = idx % size;
            if(sudoku[row][col] != 0){ // the square is already filled in, don't do anything 
                solve(sudoku,idx+1);
            } else{
                for(int i = 1; i <= 9; i++){
                    sudoku[row][col] = i;
                    solve(sudoku,idx+1); // continue with the next square
                    sudoku[row][col] = 0; // unmake move
                }
            }
        }
    }
    
    // Precondition: `sudoku` all contains numbers from 1..9 and is a 9x9 board
    // Returns true if and only if sudoku is a valid solved sudoku board
    private boolean isSolution(int[][] sudoku){
        final int N = 9;
        final int side = 3;
        boolean[] mask = new boolean[N+1];
        
        // Check rows
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                int num = sudoku[i][j];
                if(mask[num]) return false;
                mask[num] = true;
            }
            Arrays.fill(mask,false);
        }
        
        // Check columns
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                int num = sudoku[j][i];
                if(mask[num]) return false;
                mask[num] = true;
            }
            Arrays.fill(mask,false);
        }
        
        // Check subsquares
        
        for(int i = 0; i < N; i += side){
            for(int j = 0; j < N; j+= side){
                Arrays.fill(mask,false);
                for(int di = 0; di < side; di++){
                    for(int dj = 0; dj < side; dj++){
                        int num = sudoku[i+di][j+dj];
                        if(mask[num]) return false;
                    }
                }
            }
        }
        
        return true; // Everything validated!
    }

    @Override
    protected void runSolver(int[][] sudoku){
        solve(sudoku,0);
    }

}
