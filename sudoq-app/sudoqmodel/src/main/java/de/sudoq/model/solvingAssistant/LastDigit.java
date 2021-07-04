package de.sudoq.model.solvingAssistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.Position;

/**
 * Created by timo on 27.04.16.
 */
//TODO make an optional<SolveAction>
public class LastDigit {
    public static SolveAction findOne(Sudoku sudoku){
        /* for every constraint */

        for (Constraint c : sudoku.getSudokuType())
            if(c.hasUniqueBehavior()) {
                Vector<Position> v = new Vector<>();
                for(Position p : c.getPositions())
                    if(sudoku.getCell(p).isNotSolved())
                        v.add(p);
                if(v.size() == 1){
                    /* We found an instance where only one field is empty */
                    //
                    Position solutionField = v.get(0); //position that needs to be filled
                    //make List with all values entered in this constraint
                    List<Integer> otherSolutions = new ArrayList<>();
                    for(Position p : c.getPositions())
                        if(p!=solutionField)
                            otherSolutions.add(sudoku.getCell(p).getCurrentValue());
                    //make list with all possible values
                    List<Integer> possibleSolutions = new ArrayList<>();
                    for(int i : sudoku.getSudokuType().getSymbolIterator())
                        possibleSolutions.add(i);
                    /* cut away all other solutions */
                    possibleSolutions.removeAll(otherSolutions);
                    if(possibleSolutions.size()==1) {
                        /* only one solution remains -> there were no doubles */
                        int solutionValue = possibleSolutions.get(0);
                        return new SolveActionLastDigit(solutionField, solutionValue, c.getPositions());
                    }

                }
            }
        
        return null;

    }
}
