package de.sudoq.controller.sudoku;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Stack;

import de.sudoq.R;
import de.sudoq.controller.sudoku.hints.HintFormulator;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solvingAssistant.SolvingAssistant;
import de.sudoq.view.SudokuFieldView;
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

        SudokuFieldView v = ((SudokuActivity)getActivity()).getCurrentFieldView();
        if (v != null && v.getField().isEmpty())
            itemStack.add(getString(R.string.sf_sudoku_assistances_solve_specific));

        if (Profile.getInstance().getAssistances().isHelperSet())
            itemStack.add(getString(R.string.sf_sudoku_assistances_give_hint));

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
                    if (!controller.onSolveCurrent(activity.getCurrentFieldView().getField())) {
                        Toast.makeText(activity, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show();
                    }

                }else if (items[item] == getString(R.string.sf_sudoku_assistances_give_hint)){
                    hint(activity);
                }
                activity.updateButtons();
            }
        });

        return builder.create();
    }

    private void hint(final SudokuActivity activity){
        SolveDerivation sd = SolvingAssistant.giveAHint(game.getSudoku());

        TextView tv = (TextView) activity.findViewById(R.id.hintText);
        tv.setText(HintFormulator.getText(activity.getBaseContext(),  sd));
        activity.setModeHint();

        sl.getHintPainter().realizeHint(sd);
        sl.getHintPainter().invalidateAll();

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


        //Toast.makeText(SudokuActivity.this, "a hint was requested: "+sd, Toast.LENGTH_LONG).show();

    }

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