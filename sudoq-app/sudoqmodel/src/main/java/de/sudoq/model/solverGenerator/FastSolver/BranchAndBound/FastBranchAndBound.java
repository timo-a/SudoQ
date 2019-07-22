package de.sudoq.model.solverGenerator.FastSolver.BranchAndBound;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Stack;

import de.sudoq.model.solverGenerator.AmbiguityChecker;
import de.sudoq.model.solverGenerator.solution.DerivationField;
import de.sudoq.model.solverGenerator.solution.Solution;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solver.ComplexityRelation;
import de.sudoq.model.solverGenerator.solver.Solver;
import de.sudoq.model.solverGenerator.solver.helper.Backtracking;
import de.sudoq.model.solverGenerator.solver.helper.HiddenHelper;
import de.sudoq.model.solverGenerator.solver.helper.LastDigitHelper;
import de.sudoq.model.solverGenerator.solver.helper.LeftoverNoteHelper;
import de.sudoq.model.solverGenerator.solver.helper.LockedCandandidatesHelper;
import de.sudoq.model.solverGenerator.solver.helper.NakedHelper;
import de.sudoq.model.solverGenerator.solver.helper.SolveHelper;
import de.sudoq.model.solverGenerator.solver.helper.XWingHelper;
import de.sudoq.model.solvingAssistant.HintTypes;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.PositionMap;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.ComplexityConstraint;

public class FastBranchAndBound extends Solver {
    /**
     * Creates a new solver for {@code sudoku}.
     * If the argument is null, a IllegalArgumentException is thrown.
     * All methods of this class refer to this sudoku object.
     *
     * @param sudoku Sudoku to be solved by this solver
     * @throws IllegalArgumentException if {@code sudoku == null}
     */
    public FastBranchAndBound(Sudoku sudoku) {
        super(sudoku);
    }



   	protected List<SolveHelper> makeHelperList(){
        List<SolveHelper> helpers = super.makeHelperList();
        helpers.remove(helpers.size()-3); //remove LockedCandidateHelper
        helpers.remove(helpers.size()-2); //remove XWing
        return helpers;
	}


    public boolean solveAll() {

        numberOfHelpers = this.helper.size();

        // Look for constraint saturation at the beginning
        if (!this.sudoku.getSudokuType().checkSudoku(this.sudoku)) {
            return false;
        }

        boolean solved = false;
        boolean didUpdate = true;
        boolean isUnsolvable = false;

        int solver_counter = 0;
        while (!solved           //if `solved` we're done
                && didUpdate     //if we didn't do an update in the last iteration, we won't do one the next iteration either
                && !isUnsolvable) { //if we found out there is no solution, no need to try further
            didUpdate = false;

            ////////////////////////
            //if (solver_counter == 14 || solver_counter == 17 || solver_counter == 25) {
            //	System.out.println("Breakpoint " + solver_counter);
            //}


            // try to solve the sudoku
            solved = isFilledCompletely();

            if (!solved && isInvalid()) {
                if ( advanceBranching(false) == Branchresult.SUCCESS)
                    didUpdate = true;
                else
                    isUnsolvable = true;
            }

            if(!solved && !didUpdate && !isUnsolvable && useHelper())
                didUpdate = true;



            solver_counter++;
            ////////////////////////

            // UNCOMMENT THE FOLLOWING TO PRINT THE WHOLE SUDOKU AFTER EACH LOOP
            /*if(solver_counter % 1000 == 0){
                System.out.println("sc: "+ solver_counter);
                print9x9(sudoku);
            }*/
        }

        return solved;
    }

    public boolean solveAll2() {


        this.sudoku.resetCandidates();

        numberOfHelpers = this.helper.size();

        // Look for constraint saturation at the beginning
        if (!this.sudoku.getSudokuType().checkSudoku(this.sudoku)) {
            return false;
        }

        boolean solved = false;
        boolean didUpdate = true;
        boolean isUnsolvable = false;

        int solver_counter = 0;
        while (!solved           //if `solved` we're done
                && didUpdate     //if we didn't do an update in the last iteration, we won't do one the next iteration either
                && !isUnsolvable) { //if we found out there is no solution, no need to try further
            didUpdate = false;


            // try to solve the sudoku
            solved = isFilledCompletely();

            if (!solved && isInvalid()) {
                if ( advanceBranching(false) == Branchresult.SUCCESS)
                    didUpdate = true;
                else
                    isUnsolvable = true;
            }

            // try to update naked singles
            // currently, NakedSolver(1) doesnt set any entries because it is made with 2-5 and just reducing notes in mind
            if (!solved && !didUpdate && !isUnsolvable && updateNakedSingles())
                    didUpdate = true;


            if(!solved && !didUpdate && !isUnsolvable && useHelper())
                didUpdate = true;

            solver_counter++;
            ////////////////////////

            // UNCOMMENT THE FOLLOWING TO PRINT THE WHOLE SUDOKU AFTER EACH LOOP
            /*if(solver_counter % 1000 == 0){
                System.out.println("sc: "+ solver_counter );
                print9x9(sudoku);
            }*/
        }

        return solved;
    }




    protected boolean useHelper() {
		for (int i = 0; i < numberOfHelpers; i++) {
			SolveHelper hel = helper.get(i);

			//if a helper can be applied
			if (hel.update(false)) {
                this.sudoku.addComplexityValue(hel.getComplexityScore(), !(hel instanceof Backtracking));
				return true;
			}
		}

		return false;

	}

    protected boolean updateNakedSingles() {
        boolean hasNakedSingle;   //indicates that one was found in the last iteration -> continue to iterate
        boolean foundNakedSingle = false; //indicates that at least one was found
        // Iterate trough the fields to look if each field has only one
        // candidate left = solved
        do {
            hasNakedSingle = false;
            for (Position p : this.sudoku.getPositions()) {
                BitSet b = this.sudoku.getCurrentCandidates(p);
                if (b.cardinality() == 1) {
                    sudoku.setSolution(p, b.nextSetBit(0));//execute, since only one candidate, take first
                    hasNakedSingle   = true;
                    foundNakedSingle = true;
                    this.sudoku.addComplexityValue(10, true);
                }
            }
        } while(hasNakedSingle);


        return foundNakedSingle;
    }







    /**
     * This method changes the complexity value!!! amd maybe lastSolutions! use AmbiguityChecker instead.
     * Überprüft das gegebene Sudoku auf Validität entpsrechend dem spezifizierten ComplexityConstraint. Es wird
     * versucht das Sudoku mithilfe der im ComplexityConstraint für die im Sudoku definierte Schwierigkeit definierten
     * SolveHelper und Anzahl an Schritten versucht zu lösen. Das Ergbnis wird durch ein ComplexityRelation Objekt
     * zurückgegeben.
     *
     * Assumptions:
     *   - the sudoku has a solution
     *
     * @param solution
     *            In diese Map wird die ermittelte Lösung geschrieben
     *
     * @return Ein ComplexityRelation-Objekt, welches die Constraint-gemäße Lösbarkeit beschreibt
     */
    public ComplexityRelation validate() {
        ComplexityRelation result = ComplexityRelation.INVALID;

        boolean solved  = false;
        ComplexityConstraint complConstr = sudoku.getSudokuType().buildComplexityConstraint(sudoku.getComplexity());


        this.sudoku.resetCandidates();

        numberOfHelpers = this.helper.size();

        /////////////////////////////////////////////////

        boolean didUpdate = true;
        boolean isUnsolvable = false;

        int solver_counter = 0;
        while (!solved           //if `solved` we're done
                && didUpdate     //if we didn't do an update in the last iteration, we won't do one the next iteration either
                && !isUnsolvable) { //if we found out there is no solution, no need to try further
            didUpdate = false;


            // try to solve the sudoku
            solved = isFilledCompletely();

            if (!solved && isInvalid()) {
                if ( advanceBranching(false) == Branchresult.SUCCESS){
                    didUpdate = true;
                    if (sudoku.getBranchLevel() > 10)                   // we don't want to generate sudokus where it is necessary to backtrack more than 10 times
                        return ComplexityRelation.MUCH_TOO_DIFFICULT;   // so we can stop here.
                }
                else
                    isUnsolvable = true;
            }

            // try to update naked singles
            // currently, NakedSolver(1) doesnt set any entries because it is made with 2-5 and just reducing notes in mind
            if (!solved && !didUpdate && !isUnsolvable && updateNakedSingles())
                didUpdate = true;


            if(!solved && !didUpdate && !isUnsolvable && useHelper())
                didUpdate = true;

            solver_counter++;


            // UNCOMMENT THE FOLLOWING TO PRINT THE WHOLE SUDOKU AFTER EACH LOOP
            /*if(solver_counter % 10000 == 0){
                System.out.println("sc: "+ solver_counter );
                print9x9(sudoku);
            }*/
        }


///////////////////////////s


        int complexity = this.sudoku.getComplexityValue();
        //System.out.println("cmplx: "+complexity);
        // depending on the result, return an int
        int minComplextiy = complConstr.getMinComplexityIdentifier();
        int maxComplextiy = complConstr.getMaxComplexityIdentifier();

        if (solved) {

            if      (maxComplextiy * 1.2 < complexity                                      ) result = ComplexityRelation.MUCH_TOO_DIFFICULT;
            else if (maxComplextiy       < complexity && complexity <= maxComplextiy * 1.2 ) result = ComplexityRelation.TOO_DIFFICULT;
            else if (minComplextiy       < complexity && complexity <= maxComplextiy       ) result = ComplexityRelation.CONSTRAINT_SATURATION;
            else if (minComplextiy * 0.8 < complexity && complexity <= minComplextiy       ) result = ComplexityRelation.TOO_EASY;
            else if (                                    complexity <= minComplextiy * 0.8 ) result = ComplexityRelation.MUCH_TOO_EASY;
			/*   0.8 minC      minC               maxC            1.2 maxC
		    much too easy| too easy|   saturation     |too difficult      | Much too difficult         */
        }

        return result;
    }


}
