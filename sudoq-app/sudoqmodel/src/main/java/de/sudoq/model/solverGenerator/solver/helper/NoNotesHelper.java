package de.sudoq.model.solverGenerator.solver.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import de.sudoq.model.solverGenerator.solution.DerivationBlock;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solution.NoNotesDerivation;
import de.sudoq.model.solverGenerator.solver.SolverSudoku;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Utils;

/**
 * Created by timo on 25.09.16.
 */

public class NoNotesHelper extends SolveHelper {


    public NoNotesHelper(SolverSudoku sudoku, int complexity) throws IllegalArgumentException {
        super(sudoku, complexity);
    }


    @Override
    public boolean update(boolean buildDerivation) {
        boolean foundOne = false;
        Position candidate;
        Vector<Position> emptyPos = new Vector<>();
        for (Position p : sudoku.getSudokuType().getValidPositions())
            if(sudoku.getField(p).isCompletelyEmpty())
                 emptyPos.add(p);

        foundOne = emptyPos.size() > 0;

        if (buildDerivation){
            //Todo replace by filling in all notes and apply other helpers repeatedly? but how can we limit this to allempty positions?
            //create map from pos to constraint
            Set<Position> emptyPosSet = new HashSet<>(emptyPos);
            Map<Position, List<Constraint>> cmap = new HashMap<>();
            for(Constraint c : sudoku.getSudokuType().getConstraints())
                if(c.hasUniqueBehavior())
                    for(Position p :c.getPositions())
                        if(emptyPosSet.contains(p))
                            if(cmap.containsKey(p))
                                cmap.get(p).add(c);
                            else
                                cmap.put(p, new ArrayList<Constraint>(Arrays.asList(c)));




            lastDerivation = new NoNotesDerivation(HintTypes.NoNotes);
            Set<Integer> allSymbbols = new HashSet<Integer>();
            for(int i = 0; i < sudoku.getSudokuType().getNumberOfSymbols(); i++)
                allSymbbols.add(i);

            for(Position p : emptyPos) {
                Set<Integer> allCandidates = new HashSet<Integer>(allSymbbols);

                for(Constraint c : cmap.get(p)){
                    for (Position pi : c.getPositions()){
                        Field f = sudoku.getField(pi);
                        if(f.isSolved())
                            allCandidates.remove(f.getCurrentValue());
                    }

                }
                BitSet relevant = new BitSet();
                BitSet irrelevant = new BitSet();
                for (Integer i: allSymbbols)
                    if (allCandidates.contains(i))
                        relevant.set(i);
                    else
                        irrelevant.set(i);

                lastDerivation.addDerivationField(new DerivationField(p, relevant, irrelevant));
            }
        }

        return foundOne;
    }
}