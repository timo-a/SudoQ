//package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import de.sudoq.model.files.FileManagerTests;
//import de.sudoq.model.solverGenerator.AmbiguityChecker;
//import de.sudoq.model.solverGenerator.FastSolver.BranchAndBound.FastBranchAndBound;
//import de.sudoq.model.solverGenerator.FastSolver.DLX1.DLXSolver;
//import de.sudoq.model.solverGenerator.FastSolver.DLX1.Sudoku16DLX;
//import de.sudoq.model.solverGenerator.FastSolver.FastAmbiguityChecker;
//import de.sudoq.model.solverGenerator.FastSolver.FastSolver;
//import de.sudoq.model.solverGenerator.FastSolver.FastSolverFactory;
//import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
//import de.sudoq.model.solverGenerator.solver.Solver;
//import de.sudoq.model.solverGenerator.solver.SudokuMockUps;
//import de.sudoq.model.sudoku.Sudoku;
//
//import static de.sudoq.model.solverGenerator.RegressionBoards16.increase16By1;
//
//import static de.sudoq.model.solverGenerator.FastSolver.BranchAndBound.RegressionTest.pattern2;
//
//public class RegressionTestSlow {
//
//    @BeforeClass
//    public static void init() {
//        FileManagerTests.init();
//    }
//
//    @Test( timeout = 5*60*1000 )
//    public void test1() {
//        String pattern = "8  .  .  4  .  .  .  .  .  .  .  .  .  7 10  . "
//                + " .  9  .  .  7  . 14  . 10  .  2  .  8  . 15  . "
//                + " .  . 12  .  1  .  . 10  .  .  . 14  .  .  6  . "
//                + " .  .  .  . 12  8  .  .  .  .  .  .  .  .  4  . "
//                + " .  .  .  8  .  .  .  .  . 10  .  . 15  .  .  . "
//                + " .  . 15  .  .  0  .  7  .  .  .  .  .  . 11  . "
//                + " 2  3  .  .  .  .  9 12  .  .  .  .  .  .  .  0 "
//                + " .  .  .  .  .  .  .  8  .  .  0  .  .  .  . 10 "
//                + "13  .  . 12  .  .  . 15  .  .  .  .  .  .  8  9 "
//                + "11  .  .  .  4  .  .  3  .  .  1  .  .  . 13 12 "
//                + " .  .  .  1  .  .  .  .  . 13  .  .  .  .  .  . "
//                + " .  .  . 15  .  .  .  9  .  6  .  0  . 11  .  . "
//                + " .  . 13  3  .  .  .  .  .  .  5  .  .  .  .  . "
//                + " .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  4 "
//                + " .  .  . 10  .  . 12  .  .  .  .  . 11  .  .  . "
//                + " .  .  . 14  .  .  4  .  .  9  .  .  .  8  1  . ";
//
//        String pattern2 = increase16By1(pattern);
//
//        Sudoku s = SudokuMockUps.stringTo16x16Sudoku(pattern2);
//
//        System.out.println(" ambiguitychecker "+AmbiguityChecker.isAmbiguous(s));
//        System.out.println("fambiguitychecker "+ FastAmbiguityChecker.isAmbiguous2(s));
//        System.out.println("fambiguitychecker "+ FastAmbiguityChecker.isAmbiguous(s));
//
//        //System.exit(9);
//
//        FastSolver fs = FastSolverFactory.getSolver(s);
//        boolean fast = fs.isAmbiguous();
//
//        System.out.println("fast ambiguous "+fast);
//
//        Solver solver = new Solver(s);
//        ComplexityRelation cr = solver.validate(null);
//
//        System.out.println("solver validate "+cr);
//
//        FastSolver dlxsolver = new DLXSolver(s);
//        boolean dlx1 = dlxsolver.isAmbiguous();
//
//            /*for (int[][] board : ((DLXSolver) dlxsolver).getBothSolutionsForDebugPurposes())
//                for (int[] row : board){
//                    for (int e : row)
//                        System.out.print(String.format("%1$3s", e));
//
//                    System.out.println();
//                }*/
//
//
//
//        System.out.println("dlx ambiguous "+dlx1);
//
//        boolean solverResult = new Solver(s).solveAll(true,true,false);
//        System.out.println("solver.solveall  "+solverResult);
//
//    }
//
//    /**
//     * Braucht ewig
//     */
//    @Test( timeout = 5*60*1000 )
//    public void test2_backtrack(){
//
//        Sudoku sudoku = SudokuMockUps.stringTo16x16Sudoku(increase16By1(pattern2));
//        Solver solver = new Solver(sudoku);
//        solver.solveAll(false, false,false);
//    }
//
//
//    /**
//     * Braucht ewig
//     *
//     */
//    @Test
//    public void test2_fastbacktrack(){
//
//        Sudoku sudoku = SudokuMockUps.stringTo16x16Sudoku(increase16By1(pattern2));
//        FastBranchAndBound solver = new FastBranchAndBound(sudoku);
//        solver.solveAll2();
//    }
//
//}
