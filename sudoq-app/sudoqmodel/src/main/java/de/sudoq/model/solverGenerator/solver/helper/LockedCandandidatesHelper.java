package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.LockedCandidatesDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;

/**Idea: We have 2 intersecting groups:
 *   I = I_only + intersection
 *   J = J_only + intersection
 *
 *   a note that within I only appears in intersection, locks the note for J:
 *   it has to be in intersection and cannot be in J_only
 *
 * Created by timo on 07.06.16.
 */
public class LockedCandandidatesHelper extends SolveHelper {

    /**
	 * Erzeugt einen neuen HiddenHelper für das spezifizierte Suduoku mit dem spezifizierten level. Der level entspricht
	 * dabei der Größe der Symbolmenge nach der gesucht werden soll.
	 *
	 * @param sudoku
	 *            Das Sudoku auf dem dieser Helper operieren soll
	 * @param complexity
	 *            Die Schwierigkeit der Anwendung dieser Vorgehensweise
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das Sudoku null oder das level oder die complexity kleiner oder gleich 0 ist
	 */
	public LockedCandandidatesHelper(SolverSudoku sudoku, int complexity) {
		super(sudoku, complexity);
        hintType = HintTypes.LockedCandidatesExternal;
	}

    @Override
    public boolean update(boolean buildDerivation) {
        boolean success=false;

        List<Constraint> constraints = new ArrayList<Constraint>();
        for (Constraint c:sudoku.getSudokuType()) {
            constraints.add(c);
        }
        /* compare all constraints */
        for(int i=0; i< constraints.size(); i++)
            for(int j=i+1; j<constraints.size(); j++){
                /* if they intersect */
                Constraint constraintI = constraints.get(i);
                Constraint constraintJ = constraints.get(j);
                List<Position> positionsI = constraintI.getPositions();
                List<Position> positionsJ = constraintJ.getPositions();
                if(intersect(positionsI, positionsJ)){
                    // get disjunctive sets of positions:
                    List<Position> common  = intersection(positionsI, positionsJ);
                    List<Position> cutoutI = cut(positionsI, common); //Constraint I without intersection
                    List<Position> cutoutJ = cut(positionsJ, common); //Constraint J without intersection

                    //all notes of such a set
                    BitSet commonNotes  = collectNotes(common);
                    BitSet cutoutINotes = collectNotes(cutoutI);
                    BitSet cutoutJNotes = collectNotes(cutoutJ);

                    /* look for notes of I that appear only in the intersection, and in J as well*/
                    //notes that appear in the intersection but not the rest of I
                    BitSet inIntersectionButNotInI = (BitSet) commonNotes.clone();
                    inIntersectionButNotInI.andNot(cutoutINotes);

                    //do they also appear in rest of J?
                    BitSet removableNotes = (BitSet) inIntersectionButNotInI.clone();
                    removableNotes.and(cutoutJNotes);
                    List<Position> toBeRemovedFrom = cutoutJ;
                    Constraint reducibleConstraint = constraintJ;
                    Constraint lockedConstraint    = constraintI;

                    if(removableNotes.isEmpty()) {
                        //now do the other direction
                        /* look for notes of J that appear only in the intersection, and in I as well*/
                        //notes that appear in the intersection but not the rest of J
                        BitSet inIntersectionButNotInJ = (BitSet) commonNotes.clone();
                        inIntersectionButNotInJ.andNot(cutoutJNotes);

                        //do they also appear in rest of I?
                        removableNotes = (BitSet) inIntersectionButNotInJ.clone();
                        removableNotes.and(cutoutINotes);
                        toBeRemovedFrom = cutoutI;
                        reducibleConstraint = constraintI;
                        lockedConstraint    = constraintJ;
                    }
                    if(!removableNotes.isEmpty()) {
                        success = true;

                        if(buildDerivation){
                            buildDerivation(lockedConstraint, reducibleConstraint, removableNotes, toBeRemovedFrom);
                        }

                        //remove first of all removable notes
                        int first = removableNotes.nextSetBit(0);

                        for (Position p : toBeRemovedFrom)
                            sudoku.getCurrentCandidates(p).clear(first);

                        return true;
                    }
                }
            }
        return false;
    }

    private void buildDerivation(Constraint lockedConstraint, Constraint reducibleConstraint,
                                 BitSet removableNotes, List<Position> toBeRemovedFrom){
                            /* since the derivations seem to be never used, I'm a bit sloppy here...
                            *  as Blocks the intersecting constraints are added
                            *  as fields all fields that have a note removed are added*/

        int first = removableNotes.nextSetBit(0);

        LockedCandidatesDerivation lastDerivation = new LockedCandidatesDerivation();
        lastDerivation.setLockedConstraint(lockedConstraint);
        lastDerivation.setReducibleConstraint(reducibleConstraint);
        lastDerivation.setRemovableNotes(removableNotes);
        BitSet relevantCandidates = new BitSet();
        relevantCandidates.set(first);
        for(Position p : toBeRemovedFrom)
            if(sudoku.getCurrentCandidates(p).get(first)){
                BitSet irrelevantCandidates = (BitSet) sudoku.getCurrentCandidates(p).clone();
                irrelevantCandidates.clear(first);
                lastDerivation.addDerivationField(new DerivationField(p,relevantCandidates,irrelevantCandidates));
            }
        //Todo better: list fields where it is removed
        lastDerivation.setDescription("Note "+(first+1) );
        this.lastDerivation = lastDerivation;

    }



    /**
     * Determines whether Lists a,b have a common(by equals) element
     * @param a
     * @param b
     * @param <T> any element in the list needs to have equals defined
     * @return true iff i.equals(j) == true for at least one i € a, j € b
     */
    private static <T> boolean intersect(List<T> a, List<T> b){

        for (T t1: a)
            for (T t2: b)
                if(t1.equals(t2))
                    return true;

        return false;
    }

    public static <T> List<T> intersection(List<T> a, List<T> b){
        List <T> intersection = new ArrayList<>();
        for (T t1: a)
            for (T t2: b)
                if(t1.equals(t2))
                    intersection.add(t1);

        return intersection;
    }

    private static <T> List<T> cut(List<T> a, List<T> b){
        List <T> cut = new ArrayList<>(a);
        cut.removeAll(b);
        return cut;
    }

    private BitSet collectNotes(List<Position> l){
        BitSet merged = new BitSet();
        if(!l.isEmpty())
            for(Position p: l)
                merged.or(sudoku.getCurrentCandidates(p));

        return merged;
    }
}