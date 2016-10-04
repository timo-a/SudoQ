package de.sudoq.controller.sudoku;

import android.content.Context;
import android.util.Log;

import de.sudoq.R;
import de.sudoq.controller.menus.Utility;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;

/**
 * Created by timo on 04.10.16.
 */
public class HintFormulator {
    public static String getText(Context context, SolveDerivation sd){
        String text;
        Log.d("HintForm",sd.getType().toString());
        switch (sd.getType()){
            case LastDigit:
                LastDigitDerivation d = (LastDigitDerivation) sd;
                String shapeString     = Utility.constraintShapeAccDet2string(context, d.getConstraintShape());
                String shapeGender     = Utility.getGender(context,d.getConstraintShape());
                String shapeDeterminer = Utility.gender2AccDeterminer(context, shapeGender);
                String highlightSuffix = Utility.gender2AccSufix(context, shapeGender);
                text = context.getString(R.string.hint_lastdigit).replace("{shape}"      ,shapeString)
                                                                 .replace("{determiner}" ,shapeDeterminer)
                                                                 .replace("{suffix}"     ,highlightSuffix);
                break;

            case Backtracking:
                text = context.getString(R.string.hint_backtracking);
                break;
            default:
                text = "We found a hint, but did not implement a representation yet.";
        }
        return text;
    }
}
