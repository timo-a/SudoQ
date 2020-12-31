package de.sudoq.controller.sudoku;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Stack;

import de.sudoq.R;
import de.sudoq.controller.sudoku.hints.HintFormulator;
import de.sudoq.model.actionTree.Action;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solvingAssistant.SolvingAssistant;
import de.sudoq.view.SudokuCellView;
import de.sudoq.view.SudokuLayout;

/**
 * Created by timo on 29.10.16.
 */
public class AssistancesDialogFragment extends DialogFragment {

    private SudokuLayout sl;
    private Game game;
    private SudokuController controller;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction



        final SudokuActivity activity =  (SudokuActivity)getActivity();
        sl   = activity.getSudokuLayout();
        game = activity.getGame();
        controller = activity.getSudokuController();

        Stack<CharSequence> itemStack= new Stack<>();
        itemStack.addAll(Arrays.asList( getString(R.string.sf_sudoku_assistances_solve_surrender)
                , getString(R.string.sf_sudoku_assistances_back_to_valid_state)
                , getString(R.string.sf_sudoku_assistances_back_to_bookmark)
                , getString(R.string.sf_sudoku_assistances_check)
                , getString(R.string.sf_sudoku_assistances_solve_random)));

        SudokuCellView v = ((SudokuActivity)getActivity()).getCurrentCellView();
        if (v != null && v.getCell().isNotSolved())
            itemStack.add(getString(R.string.sf_sudoku_assistances_solve_specific));

        if (Profile.getInstance().getAssistances().isHelperSet())
            itemStack.add(getString(R.string.sf_sudoku_assistances_give_hint));

        if (Profile.getInstance().isDebugSet())
            itemStack.add(getString(R.string.sf_sudoku_assistances_crash));

        // TODO why this no work? final CharSequence[] items = (CharSequence[]) itemStack.toArray();
        CharSequence[] tmp   = new CharSequence[0];
        final CharSequence[] items = itemStack.toArray(tmp);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.sf_sudoku_assistances_title));

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        if (!controller.onSolveAll())
                            Toast.makeText(activity, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        game.goToLastCorrectState();
                        break;
                    case 2:
                        game.goToLastBookmark();
                        break;
                    case 3:
                        if (game.checkSudoku())
                            Toast.makeText(activity, R.string.toast_solved_correct, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity, R.string.toast_solved_wrong, Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        if (!controller.onSolveOne())
                            Toast.makeText(activity, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
                        break;
                }
				/* not inside switch, because they are at variable positions */
                if (items[item] == getString(R.string.sf_sudoku_assistances_solve_specific)){
                    if (!controller.onSolveCurrent(activity.getCurrentCellView().getCell())) {
                        Toast.makeText(activity, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
                    }

                }else if (items[item] == getString(R.string.sf_sudoku_assistances_give_hint)){
                    hint(activity);
                }
                else if (items[item] == getString(R.string.sf_sudoku_assistances_crash)){
                    throw new RuntimeException("This is a crash the user requested");
                }
                activity.getPanel().updateButtons();
            }
        });

        return builder.create();
    }

    private void hint(final SudokuActivity activity){
        final SolveDerivation sd = SolvingAssistant.giveAHint(game.getSudoku());
        if (sd == null) throw new AssertionError("derivation is null, maybe forgot to set lastDerivation = derivation?");
        TextView tv = (TextView) activity.findViewById(R.id.hintText);
        tv.setText(HintFormulator.getText(activity,  sd));
        activity.setModeHint();

        sl.getHintPainter().realizeHint(sd);
        sl.getHintPainter().invalidateAll();


        /* user pressed `continue`, game is resumed*/
        Button b = (Button) activity.findViewById(R.id.hintOkButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.setModeRegular();

                sl.getHintPainter().deleteAll();
                sl.invalidate();
                sl.getHintPainter().invalidateAll();
            }
        });

        Button bExecute = (Button) activity.findViewById(R.id.hintExecuteButton);
        bExecute.setVisibility(sd.hasActionListCapability() ? View.VISIBLE : View.GONE);

        /* user pressed `make it so` so we execute the action for him*/
        bExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.setModeRegular();

                sl.getHintPainter().deleteAll();
                sl.invalidate();
                sl.getHintPainter().invalidateAll();

                for (Action a : sd.getActionList(game.getSudoku())){

                    controller.onHintAction(a);
                    activity.onInputAction();
                    /* in case we delete a note in the focussed cell */
                    activity.getMediator().restrictCandidates();
                }
            }
        });


        //Toast.makeText(SudokuActivity.this, "a hint was requested: "+sd, Toast.LENGTH_LONG).show();

    }

//    private void restrictCandidates() {
//		this.virtualKeyboard.enableAllButtons();
//
//		Field currentField = this.sudokuView.getCurrentFieldView().getField();
//		SudokuType type = this.game.getSudoku().getSudokuType();
//		/* only if assistance 'input assistance' if enabled */
//		if (this.game.isAssistanceAvailable(Assistances.restrictCandidates)) {
//
//			/* save val of current view */
//			int save = currentField.getCurrentValue();
//
//			/* iterate over all symbols e.g. 0-8 */
//			for (int i = 0; i < type.getNumberOfSymbols(); i++) {
//				/* set cellval to current symbol */
//				currentField.setCurrentValue(i, false);
//				/* for every constraint */
//				for (Constraint c : type) {
//					/* if constraint not satisfied -> disable*/
//					boolean constraintViolated = !c.isSaturated(this.game.getSudoku());
//
//					/* Github Issue #116
//					 * it would be stupid if we were in the mode where notes are set
//					 * and would disable a note that has been set.
//					 * Because then, it can't be unset by the user*/
//					boolean noteNotSet = ! (noteMode && currentField.isNoteSet(i));
//
//					if (constraintViolated && noteNotSet) {
//						this.virtualKeyboard.disableButton(i);
//						break;
//					}
//				}
//				currentField.setCurrentValue(Field.EMPTYVAL, false);
//			}
//			currentField.setCurrentValue(save, false);
//
//		}
//	}

    @Override
    public void onDestroyView() {
        //this is added to prevent dialog from disappearing on orientation change.
        // http://stackoverflow.com/a/12434038/3014199
        //it fixes a bug in the supportLibrary
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

}