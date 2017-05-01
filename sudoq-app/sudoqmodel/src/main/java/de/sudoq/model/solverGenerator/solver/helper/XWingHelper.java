package de.sudoq.model.solverGenerator.solver.helper;

//TODO test this!!!

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.XWingDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;

/**Idea:
 *
 * Created by timo on 07.06.16.
 */
public class XWingHelper extends SolveHelper {

    /**
	 * Erzeugt einen neuen HiddenHelper für das spezifizierte Suduoku.
     * Idea:
     *   We have a '#' of 2 rows+2columns.
     *   The 4 intersection fields all feature a common note n.
     *   for the 2 rows (the 2 columns) the note appears only in the intersection i.e. is locked there -> they are the locked constraints
     *   therefore the columns (the rows) have them in the intersection too and n can be deleted everywhere else.
	 *
	 * @param sudoku
	 *            Das Sudoku auf dem dieser Helper operieren soll
	 * @param complexity
	 *            Die Schwierigkeit der Anwendung dieser Vorgehensweise
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
	 *
     */
	public XWingHelper(SolverSudoku sudoku, int complexity) {
		super(sudoku, complexity);
	}

    private void separateIntoRowColumn(List<Constraint> pool, List<Constraint> rows, List<Constraint> cols){
        for(Constraint c: pool) {
            switch (Utils.getGroupShape(c.getPositions())) {
                case Row:
                    rows.add(c);
                    break;
                case Column:
                    cols.add(c);
                    break;
            }
        }
    }


    @Override
    public boolean update(boolean buildDerivation) {

        List<Constraint> constraints = sudoku.getSudokuType().getConstraints();

        /* collect rows / cols */
        List<Constraint> rows = new ArrayList<>();
        List<Constraint> cols = new ArrayList<>();
        separateIntoRowColumn(constraints, rows, cols);

        /* compare all constraints, look for a '#': 2 rows, 2 col intersecting
        * move clockwise starting north-west i.e. topLeft*/
        for(int c1=0; c1< cols.size()-1; c1++)
            for(int r1=0; r1< rows.size()-1; r1++){
                Constraint col1 = cols.get(c1);
                Constraint row1 = rows.get(r1);
                Position topLeft = intersectionPoint(row1, col1);
                if(topLeft==null || !sudoku.getField(topLeft).isNotSolved())
                    continue;

                for(int c2=c1+1; c2< cols.size(); c2++){
                    Constraint col2 = cols.get(c2);

                    Position topRight = intersectionPoint(row1, col2);
                    if(topRight==null || !sudoku.getField(topRight).isNotSolved())
                        continue;

                    for(int r2=r1+1; r2< rows.size(); r2++) {
                        Constraint row2 = rows.get(r2);
                        Position bottomRight = intersectionPoint(col2, row2);
                        Position bottomLeft  = intersectionPoint(row2, col1);

                        if (bottomRight != null && sudoku.getField(bottomRight).isNotSolved()
                         && bottomLeft  != null && sudoku.getField(bottomLeft).isNotSolved()) {
                            /* we found a # of 2rows, 2 cols now check if 2 are locked ...*/

                            Position[] intersectionPoints = new Position[]{topLeft, topRight, bottomLeft, bottomRight};
                            if(testForLockedness(row1, row2, col1, col2, intersectionPoints, buildDerivation))
                                return true;

                        }
                    }
                }
            }
        return false;
    }


    private boolean testForLockedness(Constraint row1, Constraint row2, Constraint col1, Constraint col2, Position[] intersectionPoints, boolean buildDerivation){

        CandidateSet candidateNotes = intersectNotes(Arrays.asList(intersectionPoints));

        for (int note : candidateNotes.getSetBits())
            if(xWing(row1, row2, col1, col2, note, intersectionPoints, buildDerivation) ||
               xWing(col1, col2, row1, row2, note, intersectionPoints, buildDerivation))
                return true;

        return false;//in case candidateNotes.getSetBits() == {}

    }




    private boolean xWing(Constraint row1,
                          Constraint row2,
                          Constraint col1,
                          Constraint col2,
                          int note,
                          Position[] intersectionPoints,
                          boolean buildDerivation){
        //Xwing: row1, row2 haben in den schnittpunkten eine note die bez. Zeile nur dort vorkommt.
        //       note is therefor locked, can be deletd elsewhere in col1,col2
        boolean rowLocked = countOccurrences((short) note, row1) == 2 &&
                            countOccurrences((short) note, row2) == 2;

        boolean removableStuffInColumns = countOccurrences((short) note, col1) > 2 ||
                                          countOccurrences((short) note, col2) > 2;

        if ( rowLocked && removableStuffInColumns ) {

            List<Position> canBeDeleted = deleteNote(col1, col2, note, intersectionPoints);

            if (buildDerivation)
                buildDerivation(row1, row2, col1, col2, canBeDeleted, note);

            return true;
        }
        else
            return false;

    }

    private List<Position> deleteNote(Constraint col1, Constraint col2, int note,
                                      Position[] intersectionPoints){

        List<Position> canBeDeleted = new ArrayList<>();
        for (Position p : col1.getPositions())
            if (sudoku.getCurrentCandidates(p).isSet(note)) //don't Try getField.isNoteSet! that accesses the actual sudokus candidates
                canBeDeleted.add(p);
        for (Position p : col2)
            if (sudoku.getCurrentCandidates(p).isSet(note))
                canBeDeleted.add(p);

        for (Position p: intersectionPoints)
            canBeDeleted.remove(p);

            /* delete notes */
        for (Position p : canBeDeleted)
            sudoku.getCurrentCandidates(p).clear(note);

        return canBeDeleted;
    }

    private void buildDerivation(Constraint row1, Constraint row2, Constraint col1, Constraint col2,
                                 Iterable<Position> canBeDeleted, int note){
        XWingDerivation internalDerivation = new XWingDerivation();
        internalDerivation.setLockedConstraints(row1, row2);
        internalDerivation.setReducibleConstraints(col1, col2);
        for(Position p: canBeDeleted) {
            CandidateSet relevant = new CandidateSet();
            relevant.set(note);
            CandidateSet irrelevant = new CandidateSet();

            internalDerivation.addDerivationField(new DerivationField(p, relevant, irrelevant));
            internalDerivation.setNote(note);
        }
        lastDerivation = internalDerivation;
    }





    /** TODO some sort of Maybe would be better than returning null...
     * Determines the first T found to occur in a and b (by equals())
     * @param a
     * @param b
     * @param <T> any element in the list needs to have equals defined
     * @return an element i where i.equals(j) for  i € a, j € b, null iff none is found
     */
    private static <T> T intersectionPoint(Iterable<T> a, Iterable<T> b){

        for (T t1: a)
            for (T t2: b)
                if(t1.equals(t2))
                    return t1;

        return null;
    }

    private CandidateSet intersectNotes(List<Position> l){
        CandidateSet merged = new CandidateSet();
        if(!l.isEmpty())
            merged.assignWith(sudoku.getCurrentCandidates(l.get(0)));
            for (int i = 1; i < l.size(); i++)
                merged.and(sudoku.getCurrentCandidates(l.get(i))); //TODO in scala this could be a fold1 after mapping sudoku.getc..

        return merged;

    }

    /*
    *
    * */
    private short countOccurrences(short note, Iterable<Position> positions){
        short sum =0;
        for (Position p:positions)
            if(sudoku.getCurrentCandidates(p).isSet(note))
                sum++;

        return sum;
    }
}