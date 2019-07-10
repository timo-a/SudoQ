package dlx;

public class NaiveSudokuSolver extends AbstractSudokuSolver{
    
    private void solve(int[][] board, int ind){
        if(ind == S*S){
            System.out.println("Solution via naive algorithm found: ");
            printSolution(board);
            System.out.println();
        }
        else{
            int row = ind / S;
            int col = ind % S;
            
            // Advance forward on cells that are prefilled
            if(board[row][col] != 0) solve(board, ind+1);
            else{
                // we are positioned on something we need to fill in. Try all possibilities
                for(int i = 1; i <= 9; i++){
                    if(consistent(board, row, col, i)){
                        board[row][col] = i;
                        solve(board,ind+1);
                        board[row][col] = 0; // unmake move
                    }   
                }
            }
            // no solution
        }

    }
      
    // Check whether putting "c" into index "ind" leaves the board in a consistent state
    private boolean consistent(int[][] board, int row, int col, int c) {        
        // check columns/rows
        for(int i = 0; i < S; i++){
            if(board[row][i] == c) return false;
            if(board[i][col] == c) return false;
        }
        
        // Check subsquare
        
        int rowStart = row - row % side; 
        int colStart = col - col % side;
        
        for(int m = 0; m < side; m++){
            for(int k = 0; k < side; k++){
                if(board[rowStart + k][colStart + m] == c) return false;
            }
        }
        return true;
    }

    
   
    protected void runSolver(int[][] sudoku){
        solve(sudoku,0);
    }

}
