package de.sudoq.controller.sudoku;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.util.BitSet;

import de.sudoq.R;
import de.sudoq.controller.menus.Utility;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solution.NakedSetDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;

/**
 * Created by timo on 04.10.16.
 */
public class HintFormulator {
    public static String getText(Context context, SolveDerivation sd){
        String text;
        Log.d("HintForm",sd.getType().toString());
        switch (sd.getType()){
            case LastDigit:   text = lastDigitText(context, sd);    break;

            case NakedSingle:{
                BitSet bs = ((NakedSetDerivation) sd).getSubsetMembers().get(0).getRelevantCandidates();
                int note = bs.nextSetBit(0)+1;
                StringBuilder sb = new StringBuilder();
                sb.append("Look there's a naked single! ");
                sb.append(note);
                sb.append(" is the only note left in the highlighted field. It can be set there and removed from all other Fields in this group");
                text = sb.toString();}
                break;

            case NakedPair:
            case NakedTriple:
            case NakedQuadruple:
            case NakedQuintuple:
                BitSet bs = ((NakedSetDerivation) sd).getSubsetCandidates();

                StringBuilder sb = new StringBuilder();
                sb.append("Look there's a naked set! {");

                int i = bs.nextSetBit(0);

                sb.append(i+1);

                for (i=bs.nextSetBit(i+1); i >= 0; i = bs.nextSetBit(i+1)) {
                    sb.append(',');
                    sb.append(i+1);
                }

                sb.append("} will in some way be distributed among the highlighted fields. They can be removed from all other fields in this group");
                text = sb.toString();
                break;

            case Backtracking:
                text = context.getString(R.string.hint_backtracking);
                break;
            default:
                text = "We found a hint, but did not implement a representation yet.";
        }
        return text;
    }

    private static String lastDigitText(Context context, SolveDerivation sd){
        LastDigitDerivation d = (LastDigitDerivation) sd;
        String shapeString     = Utility.constraintShapeAccDet2string(context, d.getConstraintShape());
        String shapeGender     = Utility.getGender(context,d.getConstraintShape());
        String shapeDeterminer = Utility.gender2AccDeterminer(context, shapeGender);
        String highlightSuffix = Utility.gender2AccSufix(context, shapeGender);
        return context.getString(R.string.hint_lastdigit).replace("{shape}"      ,shapeString)
                                                         .replace("{determiner}" ,shapeDeterminer)
                                                         .replace("{suffix}"     ,highlightSuffix);
    }

}
