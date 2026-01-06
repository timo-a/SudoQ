package de.sudoq.model.solverGenerator.solver;

import de.sudoq.model.ports.persistence.ReadRepo;
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuBuilder;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuType;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

//import de.sudoq.model.utility.persistence.sudokuType.SudokuTypeRepo;

/**
 * Created by timo on 02.09.16.
 */
public class SudokuMockUps {

    //static File tmpSudokus = Files.createTempDirectory("junit").toFile();


    private static final ReadRepo<SudokuType> str = new SudokuTypeRepo4Tests();

    public static Sudoku stringTo9x9Sudoku(String pattern){
        Sudoku s = new SudokuBuilder(SudokuTypes.standard9x9, str).createSudoku();
        s.setComplexity(Complexity.arbitrary);
        return transform(s, pattern);
    }

    public static Sudoku stringTo16x16Sudoku(String pattern){
        Sudoku s = new SudokuBuilder(SudokuTypes.standard16x16, str).createSudoku();
        s.setComplexity(Complexity.arbitrary);
        return transformX(16, s, pattern);
    }

    /* expects values in [1,9] */
    public static Sudoku stringToSamuraiSudoku(String pattern){
        Sudoku s = new SudokuBuilder(SudokuTypes.samurai, str).createSudoku();
        s.setComplexity(Complexity.arbitrary);
        int dim = 21;
        for(int y=0; y<dim; y++)
            for(int x=0; x<dim; x++){
                char c = pattern.charAt(2*(dim*y+x));
                Cell f =  s.getCell(Position.get(x, y));
                if (f==null)
                    ;//pass
                else if (c == '.'){
                    //empty
                }else if ('0' < c && c <= '9'){
                    f.setCurrentValue(c-'0' -1);
                }else
                    throw new IllegalArgumentException("parse error");
            }


        return s;
    }


    public static Sudoku getLockedCandidates1(){
        //http://hodoku.sourceforge.net/en/tech_intersections.php //sudoku_1 in resources
        String pattern = "9    8     4       ¹²   ¹²⁷  ³⁶       ¹³⁵  ⁶⁷   ⁵⁷    \n"
                       + "³⁷   ⁶⁷    2       5    ¹⁷⁸  ³⁶       ¹³⁹  4    ⁷⁸⁹   \n"
                       + "³⁵⁷  ⁵⁶⁷   1       9    ⁷⁸   4        ³⁵   ⁶⁷⁸  2     \n"

                       + "⁵⁸   ¹⁴⁵   6       ¹⁴⁸  9    7        2    3    ⁴⁵⁸   \n"
                       + "⁵⁷⁸  ¹⁴⁵⁷  3       6    ¹⁴⁸  2        ⁵⁹   ⁷⁸   ⁴⁵⁷⁸⁹ \n"
                       + "2    ⁴⁷    9       ⁴⁸   3    5        6    1    ⁴⁷⁸   \n"

                       + "1    9     5       7    6    8        4    2    3     \n"
                       + "4    2     7       3    5    1        8    9    6     \n"
                       + "6    3     8       ²⁴   ²⁴   9        7    5    1     \n";

        return stringTo9x9Sudoku(pattern);
    }


    public static Sudoku getXWing(){
        String pattern = "⁵⁸ 4  1    7   2   9     ⁶⁸ 3  ⁵⁶ \n"
                       + "7  6  9    ¹⁸  ¹⁵⁸ 3     4  ⁵⁸ 2  \n"
                       + "⁵⁸ 3  2    6   4   ⁵⁸    7  1  9  \n"

                       + "4  ²⁸ 3    9   ⁵⁸  ²⁵⁶⁸  1  7  ⁵⁶ \n"
                       + "6  ²⁸ 7    ¹²⁸ ¹⁵⁸ 4     9  ⁵⁸ 3  \n"
                       + "1  9  5    3   7   ⁶⁸    ⁶⁸ 2  4  \n" 
                       
                       + "2  1  4    5   6   7     3  9  8  \n"
                       + "3  7  6    ²⁸  9   ²⁸    5  4  1  \n"
                       + "9  5  8    4   3   1     2  6  7  \n";

        return stringTo9x9Sudoku(pattern);
    }



    public static Sudoku stringToSudoku(SudokuTypes type, String pattern){
        Sudoku sudoku = new SudokuBuilder(type, str).createSudoku();
        sudoku.setComplexity(Complexity.arbitrary);
        int xLim = sudoku.getSudokuType().getSize().getX();

        String[] candidates = pattern.split("\\s+");
        for(Position pos : sudoku.getSudokuType().getValidPositions()) {
            String currentEntry = candidates[xLim * pos.getY() + pos.getX()];
                Cell f =  sudoku.getCell(pos);
                clearCandidates(f, sudoku);

                if("0123456789".contains(currentEntry))
                    f.setCurrentValue(Integer.parseInt(currentEntry)-1);
                else if(currentEntry.equals("."))
                    ;//pass -> completely empty
                else
                    for(Character c:currentEntry.toCharArray())
                        f.toggleNote(Character.getNumericValue(c)-1);

         }
        return sudoku;
    }

    private static Sudoku transform(Sudoku sudoku, String pattern){
        String[] candidates = pattern.split("\\s+");
        for (Position pos : sudoku.getSudokuType().getValidPositions()) {
            String gu = candidates[9* pos.getY()+pos.getX()];
            Cell f =  sudoku.getCell(pos);
            clearCandidates(f,sudoku);

            switch (gu.length()){
                case 0: break;
                case 1: {
                    if(gu.charAt(0) == '.') {
                        ; //pass '.' -> kein eintrag, keine notizen
                    } else {
                        f.setCurrentValue(Integer.parseInt(gu) - 1);
                    }
                    break;
                }
                default:
                    for(Character c:gu.toCharArray())
                        f.toggleNote(Character.getNumericValue(c)-1);
                }
        }
        return sudoku;
    }




    private static Sudoku transformX(int dim, Sudoku sudoku, String pattern){
        String[] candidates = pattern.split("\\s+");
        for(int y=0; y<dim; y++)
            for(int x=0; x<dim; x++){
                String gu = candidates[dim*y+x];
                Cell f =  sudoku.getCell(Position.get(x, y));
                clearCandidates(f,sudoku);

                if (gu.equals(".")){
                    //empty
                }else if ('0' <= gu.charAt(0) &&
                                 gu.charAt(0) <= '9'){
                    f.setCurrentValue(Integer.parseInt(gu)-1);
                }else
                    for(Character c:gu.toCharArray())
                            f.toggleNote(Character.getNumericValue(c)-1);
                }

        return sudoku;
    }

    private static void clearCandidates(Cell f, Sudoku sudoku){
        for(int i : sudoku.getSudokuType().getSymbolIterator())
            if(f.isNoteSet(i))
                f.toggleNote(i);
    }

    private static void setCandidates(Cell f, Sudoku sudoku){
        for(int i : sudoku.getSudokuType().getSymbolIterator())
            if(!f.isNoteSet(i))
                f.toggleNote(i);
    }


    public static String increase9By1(String pattern){
        return pattern.replace("8","9")
                      .replace("7","8")
                      .replace("6","7")
                      .replace("5","6")
                      .replace("4","5")
                      .replace("3","4")
                      .replace("2","3")
                      .replace("1","2")
                      .replace("0","1");
    }


    /* untested
    private static Sudoku transform1Constraint(String pattern){
        pattern = "2 4 5 ²⁴³ ⁶";
        String[] candidates = pattern.split("\\s+");
        List<Position> posList = new Stack<>();
        for(int i=0; i< candidates.length; i++)
            posList.add(Position.get(i,0));

        Constraint c = new Constraint(new UniqueConstraintBehavior(), ConstraintType.LINE);
        for(Position p:posList)
            c.addPosition(p);

        SudokuType t = new SudokuType();
        t.addConstraint(c);
        t.setDimensions(Position.get(candidates.length, 1));
        Sudoku sudoku = new Sudoku(t);
        for(Position p: posList){
            String filling = candidates[p.getX()];
            Cell f =  sudoku.getCell(p);
            switch (filling.length()){
                case 0: break;
                case 1: f.setCurrentValue(Integer.parseInt(filling)-1);
                    break;
                default:
                    for(Character ch:filling.toCharArray())
                        f.toggleNote(Character.getNumericValue(ch));
            }
        }
        return sudoku;
    }*/
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