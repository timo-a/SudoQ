package de.sudoq.model.solverGenerator.solver

import de.sudoq.model.ports.persistence.ReadRepo
import de.sudoq.model.solverGenerator.utils.SudokuTypeRepo4Tests
import de.sudoq.model.sudoku.Cell
import de.sudoq.model.sudoku.Position
import de.sudoq.model.sudoku.Sudoku
import de.sudoq.model.sudoku.SudokuBuilder
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuType
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes

//import de.sudoq.model.utility.persistence.sudokuType.SudokuTypeRepo;
/**
 * Created by timo on 02.09.16.
 */
object SudokuMockUps {
    //static File tmpSudokus = Files.createTempDirectory("junit").toFile();
    private val str: ReadRepo<SudokuType> = SudokuTypeRepo4Tests()

    fun stringTo9x9Sudoku(pattern: String): Sudoku {
        val s = SudokuBuilder(SudokuTypes.standard9x9, str).createSudoku()
        s.complexity = Complexity.arbitrary
        return transform(s, pattern)
    }

    @JvmStatic
    fun stringTo16x16Sudoku(pattern: String): Sudoku {
        val s = SudokuBuilder(SudokuTypes.standard16x16, str).createSudoku()
        s.complexity = Complexity.arbitrary
        return transformX(16, s, pattern)
    }

    /* expects values in [1,9] */
    @JvmStatic
    fun stringToSamuraiSudoku(pattern: String): Sudoku {
        val s = SudokuBuilder(SudokuTypes.samurai, str).createSudoku()
        s.complexity = Complexity.arbitrary
        val dim = 21
        for (y in 0..<dim) for (x in 0..<dim) {
            val c = pattern[2 * (dim * y + x)]
            val f = s.getCell(Position[x, y])
            if (f == null) ; else if (c == '.') {
                //empty
            } else if (c in '1'..'9') {
                f.currentValue = c.code - '0'.code - 1
            } else throw IllegalArgumentException("parse error")
        }


        return s
    }


    val lockedCandidates1: Sudoku
        get() {
            //http://hodoku.sourceforge.net/en/tech_intersections.php //sudoku_1 in resources
            val pattern =
                ("9    8     4       ¹²   ¹²⁷  ³⁶       ¹³⁵  ⁶⁷   ⁵⁷    \n"
                        + "³⁷   ⁶⁷    2       5    ¹⁷⁸  ³⁶       ¹³⁹  4    ⁷⁸⁹   \n"
                        + "³⁵⁷  ⁵⁶⁷   1       9    ⁷⁸   4        ³⁵   ⁶⁷⁸  2     \n"

                        + "⁵⁸   ¹⁴⁵   6       ¹⁴⁸  9    7        2    3    ⁴⁵⁸   \n"
                        + "⁵⁷⁸  ¹⁴⁵⁷  3       6    ¹⁴⁸  2        ⁵⁹   ⁷⁸   ⁴⁵⁷⁸⁹ \n"
                        + "2    ⁴⁷    9       ⁴⁸   3    5        6    1    ⁴⁷⁸   \n"

                        + "1    9     5       7    6    8        4    2    3     \n"
                        + "4    2     7       3    5    1        8    9    6     \n"
                        + "6    3     8       ²⁴   ²⁴   9        7    5    1     \n")

            return stringTo9x9Sudoku(pattern)
        }


    val xWing: Sudoku
        get() {
            val pattern = ("⁵⁸ 4  1    7   2   9     ⁶⁸ 3  ⁵⁶ \n"
                    + "7  6  9    ¹⁸  ¹⁵⁸ 3     4  ⁵⁸ 2  \n"
                    + "⁵⁸ 3  2    6   4   ⁵⁸    7  1  9  \n"

                    + "4  ²⁸ 3    9   ⁵⁸  ²⁵⁶⁸  1  7  ⁵⁶ \n"
                    + "6  ²⁸ 7    ¹²⁸ ¹⁵⁸ 4     9  ⁵⁸ 3  \n"
                    + "1  9  5    3   7   ⁶⁸    ⁶⁸ 2  4  \n"

                    + "2  1  4    5   6   7     3  9  8  \n"
                    + "3  7  6    ²⁸  9   ²⁸    5  4  1  \n"
                    + "9  5  8    4   3   1     2  6  7  \n")

            return stringTo9x9Sudoku(pattern)
        }


    fun stringToSudoku(type: SudokuTypes, pattern: String): Sudoku {
        val sudoku = SudokuBuilder(type, str).createSudoku()
        sudoku.complexity = Complexity.arbitrary
        val xLim = sudoku.sudokuType.size.x

        val candidates =
            pattern.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (pos in sudoku.sudokuType.validPositions) {
            val currentEntry = candidates[xLim * pos.y + pos.x]
            val f = sudoku.getCell(pos)
            SudokuMockUps.clearCandidates(f!!, sudoku)

            if ("0123456789".contains(currentEntry)) f.currentValue = currentEntry.toInt() - 1
            else if (currentEntry == ".") ;
            else for (c in currentEntry.toCharArray()) f.toggleNote(
                Character.getNumericValue(c) - 1
            )
        }
        return sudoku
    }

    private fun transform(sudoku: Sudoku, pattern: String): Sudoku {
        val candidates =
            pattern.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (pos in sudoku.sudokuType.validPositions) {
            val gu = candidates[9 * pos.y + pos.x]
            val f = sudoku.getCell(pos)
            SudokuMockUps.clearCandidates(f!!, sudoku)

            when (gu.length) {
                0 -> {}
                1 -> {
                    if (gu[0] == '.') {
                        //pass '.' -> kein eintrag, keine notizen
                    } else {
                        f.currentValue = gu.toInt() - 1
                    }
                }

                else -> for (c in gu.toCharArray()) f.toggleNote(Character.getNumericValue(c) - 1)
            }
        }
        return sudoku
    }


    private fun transformX(dim: Int, sudoku: Sudoku, pattern: String): Sudoku {
        val candidates =
            pattern.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (y in 0..<dim) for (x in 0..<dim) {
            val gu = candidates[dim * y + x]
            val f = sudoku.getCell(Position[x, y])
            SudokuMockUps.clearCandidates(f!!, sudoku)

            if (gu == ".") { /* empty */ }
            else if (gu.get(0) in '0'..'9')
                f.currentValue = gu.toInt() - 1
            else
                for (c in gu.toCharArray()) f.toggleNote(Character.getNumericValue(c) - 1)
        }

        return sudoku
    }

    private fun clearCandidates(f: Cell, sudoku: Sudoku) {
        for (i in sudoku.sudokuType.symbolIterator) if (f.isNoteSet(i)) f.toggleNote(i)
    }

    private fun setCandidates(f: Cell, sudoku: Sudoku) {
        for (i in sudoku.sudokuType.symbolIterator) if (!f.isNoteSet(i)) f.toggleNote(i)
    }


    fun increase9By1(pattern: String): String {
        return pattern.replace("8", "9")
            .replace("7", "8")
            .replace("6", "7")
            .replace("5", "6")
            .replace("4", "5")
            .replace("3", "4")
            .replace("2", "3")
            .replace("1", "2")
            .replace("0", "1")
    } /* untested
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
} /*
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


