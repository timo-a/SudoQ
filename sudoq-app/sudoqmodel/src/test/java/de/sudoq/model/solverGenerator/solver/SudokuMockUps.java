package de.sudoq.model.solverGenerator.solver;

import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Created by timo on 02.09.16.
 */
public class SudokuMockUps {

    public static Sudoku getLockedCandidates1(){
        Sudoku s = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
		s.setComplexity(Complexity.arbitrary);
        //http://hodoku.sourceforge.net/en/tech_intersections.php
        String pattern = "9    8     4       ¹²   ¹²⁷  ³⁶       ¹³⁵  ⁶⁷   ⁵⁷    \n"
                       + "³⁷   ⁶⁷    2       5    ¹⁷⁸  ³⁶       ¹³⁹  4    ⁷⁸⁹   \n"
                       + "³⁵⁷  ⁵⁶⁷   1       9    ⁷⁸   4        ³⁵   ⁶⁷⁸  2     \n"

                       + "⁵⁸   ¹⁴⁵   6       ¹⁴⁸  9    7        2    3    ⁴⁵⁸   \n"
                       + "⁵⁷⁸  ¹⁴⁵⁷  3       6    ¹⁴⁸  2        ⁵⁹   ⁷⁸   ⁴⁵⁷⁸⁹ \n"
                       + "2    ⁴⁷    9       ⁴⁸   3    5        6    1    ⁴⁷⁸   \n"

                       + "1    9     5       7    6    8        4    2    3     \n"
                       + "4    2     7       3    5    1        8    9    6     \n"
                       + "6    3     8       ²⁴   ²⁴   9        7    5    1     \n";

        return transform(s, pattern);
    }


    public static Sudoku getXWing(){
        Sudoku s = new SudokuBuilder(SudokuTypes.standard9x9).createSudoku();
        s.setComplexity(Complexity.arbitrary);
        String pattern = "⁵⁸ 4  1    7   2   9     ⁶⁸ 3  ⁵⁶ \n"
                       + "7  6  9    ¹⁸  ¹⁵⁸ 3     4  ⁵⁸ 2  \n"
                       + "⁵⁸ 3  2    6   4   ⁵⁸    7  1  9  \n"

                       + "4  ²⁸ 3    9   ⁵⁸  ²⁵⁶⁸  1  7  ⁵⁶ \n"
                       + "6  ²⁸ 7    ¹²⁸ ¹⁵⁸ 4     9  ⁵⁸ 3  \n"
                       + "1  9  5    3   7   ⁶⁸    ⁶⁸ 2  4  \n" 
                       
                       + "2  1  4    5   6   7     3  9  8  \n"
                       + "3  7  6    ²⁸  9   ²⁸    5  4  1  \n"
                       + "9  5  8    4   3   1     2  6  7  \n";

        return transform(s, pattern);
    }



    private static Sudoku transform(Sudoku sudoku, String pattern){
        String[] candidates = pattern.split("\\s+");
        for(int y=0; y<9; y++)
            for(int x=0; x<9; x++){
                String gu = candidates[9*y+x];
                Field f =  sudoku.getField(Position.get(x, y));
                for(int i = 0; i< sudoku.getSudokuType().getNumberOfSymbols(); i++)
                    if(f.isNoteSet(i))
                        f.toggleNote(i);

                switch (gu.length()){
                    case 0: break;
                    case 1: f.setCurrentValue(Integer.parseInt(gu)-1);
                            break;
                    default:
                            for(Character c:gu.toCharArray())
                              f.toggleNote(Character.getNumericValue(c));


                }
            }
        return sudoku;
    }
}

/*
0, 0 -> -1
1, 0 ->  3
2, 0 -> -1
3, 0 ->  0
4, 0 -> -1
5, 0 -> -1
6, 0 -> -1
7, 0 ->  6
8, 0 -> -1
*/
/*
0, 1 -> -1
1, 1 ->  1
2, 1 -> -1
3, 1 -> -1
4, 1 ->  8
5, 1 -> -1
6, 1 -> -1
7, 1 -> -1
8, 1 -> -1
5, 2 ->  5
4, 3 -> -1
4, 4 ->  1
3, 5 ->  5
2, 6 -> -1
1, 7 -> -1
8, 7 -> -1
0, 8 -> -1
8, 8 -> -1
0, 7 ->  3
1, 6 -> -1
8, 6 ->  8
2, 5 -> -1
3, 4 -> -1
4, 2 -> -1
7, 8 ->  6
1, 5 -> -1
8, 5 -> -1
0, 6 -> -1
7, 6 -> -1
3, 3 -> -1
2, 4 -> -1
3, 2 -> -1
7, 7 ->  8
6, 8 -> -1
0, 5 -> -1
2, 3 -> -1
7, 5 -> -1
1, 4 ->  3
8, 4 ->  1
6, 7 -> -1
5, 8 ->  0
1, 3 -> -1
8, 3 -> -1
2, 2 -> -1
6, 5 ->  3
0, 4 -> -1
7, 4 -> -1
5, 7 -> -1
6, 6 -> 0
4, 8 -> -1
0, 3 -> -1
1, 2 -> 2
8, 2 -> -1
7, 3 -> -1
6, 4 -> 2
4, 7 -> -1
5, 6 -> -1
3, 8 -> -1
0, 2 -> -1
7, 2 -> 8
6, 3 -> -1
5, 4 -> -1
5, 5 -> -1
4, 6 -> 6
3, 7 -> 2
2, 8 -> -1
4, 5 -> -1
5, 3 -> 2
3, 6 -> -1
6, 2 -> -1
2, 7 -> -1
1, 8 -> -1

*/