package de.sudoq.controller.sudoku

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import de.sudoq.R
import de.sudoq.controller.sudoku.hints.HintFormulator.getText
import de.sudoq.model.game.Game
import de.sudoq.model.profile.ProfileSingleton
import de.sudoq.model.solvingAssistant.SolvingAssistant.giveAHint
import de.sudoq.view.SudokuLayout
import java.util.*

/**
 * Created by timo on 29.10.16.
 */
class AssistancesDialogFragment : DialogFragment() {
    private var sl: SudokuLayout? = null
    private var game: Game? = null
    private var controller: SudokuController? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val activity = activity as SudokuActivity
        sl = activity.sudokuLayout
        game = activity.game
        controller = activity.sudokuController
        val itemStack = Stack<CharSequence?>()
        itemStack.addAll(
            listOf(
                getString(R.string.sf_sudoku_assistances_solve_surrender),
                getString(R.string.sf_sudoku_assistances_back_to_valid_state),
                getString(R.string.sf_sudoku_assistances_back_to_bookmark),
                getString(R.string.sf_sudoku_assistances_check),
                getString(R.string.sf_sudoku_assistances_solve_random)
            )
        )
        val v = (getActivity() as SudokuActivity).currentCellView
        if (v != null && v.cell.isNotSolved) itemStack.add(getString(R.string.sf_sudoku_assistances_solve_specific))
        val p = ProfileSingleton.getInstance(
            activity.getDir(
                getString(R.string.path_rel_profiles),
                Context.MODE_PRIVATE
            )
        )
        if (p.assistances.isHelperSet) itemStack.add(getString(R.string.sf_sudoku_assistances_give_hint))
        if (p.appSettings.isDebugSet) itemStack.add(getString(R.string.sf_sudoku_assistances_crash))

        // TODO why this no work? final CharSequence[] items = (CharSequence[]) itemStack.toArray();
        val items = itemStack.toTypedArray()
        val builder = AlertDialog.Builder(getActivity()!!)
        builder.setTitle(getString(R.string.sf_sudoku_assistances_title))
        builder.setItems(items) { dialog, item ->
            when (item) {
                0 -> if (!controller!!.onSolveAll()) Toast.makeText(
                    activity,
                    R.string.toast_solved_wrong,
                    Toast.LENGTH_SHORT
                ).show()
                1 -> game!!.goToLastCorrectState()
                2 -> game!!.goToLastBookmark()
                3 -> if (game!!.checkSudoku()) Toast.makeText(
                    activity,
                    R.string.toast_solved_correct,
                    Toast.LENGTH_SHORT
                ).show() else Toast.makeText(
                    activity,
                    R.string.toast_solved_wrong,
                    Toast.LENGTH_LONG
                ).show()
                4 -> if (!controller!!.onSolveOne()) Toast.makeText(
                    activity,
                    R.string.toast_solved_wrong,
                    Toast.LENGTH_SHORT
                ).show()
            }
            /* not inside switch, because they are at variable positions */
            if (items[item] === getString(R.string.sf_sudoku_assistances_solve_specific)) {
                if (!controller!!.onSolveCurrent(activity.currentCellView!!.cell)) {
                    Toast.makeText(activity, R.string.toast_solved_wrong, Toast.LENGTH_SHORT).show()
                }
            } else if (items[item] === getString(R.string.sf_sudoku_assistances_give_hint)) {
                hint(activity)
            } else if (items[item] === getString(R.string.sf_sudoku_assistances_crash)) {
                throw RuntimeException("This is a crash the user requested")
            }
            activity.panel!!.updateButtons()
        }
        return builder.create()
    }

    private fun hint(activity: SudokuActivity) {
        val sd = giveAHint(game!!.sudoku!!)
        val tv = activity.findViewById<View>(R.id.hintText) as TextView
        tv.text = getText(activity, sd)
        activity.setModeHint()
        sl!!.hintPainter.realizeHint(sd)
        sl!!.hintPainter.invalidateAll()


        /* user pressed `continue`, game is resumed*/
        val b = activity.findViewById<View>(R.id.hintOkButton) as Button
        b.setOnClickListener {
            activity.setModeRegular()
            sl!!.hintPainter.deleteAll()
            sl!!.invalidate()
            sl!!.hintPainter.invalidateAll()
        }
        val bExecute = activity.findViewById<View>(R.id.hintExecuteButton) as Button
        bExecute.visibility = if (sd.hasActionListCapability()) View.VISIBLE else View.GONE

        /* user pressed `make it so` so we execute the action for him*/
        bExecute.setOnClickListener {
            activity.setModeRegular()
            sl!!.hintPainter.deleteAll()
            sl!!.invalidate()
            sl!!.hintPainter.invalidateAll()
            for (a in sd.getActionList(game!!.sudoku!!)) {
                controller!!.onHintAction(a)
                activity.onInputAction()
                /* in case we delete a note in the focussed cell */
                activity.mediator!!.restrictCandidates()
            }
        }


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
    override fun onDestroyView() {
        //this is added to prevent dialog from disappearing on orientation change.
        // http://stackoverflow.com/a/12434038/3014199
        //it fixes a bug in the supportLibrary
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }
}