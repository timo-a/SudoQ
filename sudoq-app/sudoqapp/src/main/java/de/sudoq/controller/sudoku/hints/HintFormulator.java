package de.sudoq.controller.sudoku.hints;

import android.content.Context;
import android.util.Log;

import java.util.BitSet;

import de.sudoq.R;
import de.sudoq.controller.menus.Utility;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solution.LeftoverNoteDerivation;
import de.sudoq.model.solverGenerator.solution.LockedCandidatesDerivation;
import de.sudoq.model.solverGenerator.solution.NakedSetDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solution.XWingDerivation;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Utils;

/**
 * Created by timo on 04.10.16.
 */
public class HintFormulator {
    public static String getText(Context context, SolveDerivation sd){
        String text;
        Log.d("HintForm",sd.getType().toString());
        switch (sd.getType()){
            case LastDigit:     text = lastDigitText(context, sd);     break;

            case LastCandidate: text = lastCandidateText(context, sd); break;

            case LeftoverNote:  text = leftoverNoteText(context, sd); break;

            case NakedSingle:   text = nakedSingleText(context, sd);   break;

            case NakedPair:
            case NakedTriple:
            case NakedQuadruple:
            case NakedQuintuple: text = nakedMultiple(context, sd);    break;

            case HiddenSingle:   text = hiddenSingleText(context, sd); break;

            case HiddenPair:
            case HiddenTriple:
            case HiddenQuadruple:
            case HiddenQuintuple: text = hiddenMultiple(context, sd);  break;

            case LockedCandidatesExternal: text = lockedCandidates(context, sd);  break;

            case XWing: text = xWing(context, sd); break;

            case Backtracking:
                text = context.getString(R.string.hint_backtracking);  break;
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

    private static String lastCandidateText(Context context, SolveDerivation sd){
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.hint_lastcandidate));
        return sb.toString();
    }

    private static String leftoverNoteText(Context context, SolveDerivation sd){
        LeftoverNoteDerivation d = (LeftoverNoteDerivation) sd;

        String shapeString =  Utility.constraintShapeAccDet2string(context, Utils.getGroupShape(d.getConstraint()));

        return context.getString(R.string.hint_leftovernote).replace("{note}",  d.getNote()+1+"")
                                                            .replace("{shape}", shapeString)   ;
    }

    private static String nakedSingleText(Context context, SolveDerivation sd){
        NakedSetDerivation d = (NakedSetDerivation) sd;
        BitSet bs = d.getSubsetMembers().get(0).getRelevantCandidates();
        int note = bs.nextSetBit(0)+1;
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.hint_nakedsingle_look));
        sb.append(' ');
        sb.append(context.getString(R.string.hint_nakedsingle_note).replace("{note}", note+""));
        sb.append(' ');

        String shapeString =  Utility.constraintShapeGenDet2string(context, Utils.getGroupShape(d.getConstraint()));
        String shapePrepDet = Utility.getGender(context, Utils.getGroupShape(d.getConstraint()));
        String prepDet      = Utility.gender2inThe(context, shapePrepDet);

        sb.append(context.getString(R.string.hint_nakedsingle_remove).replace("{prep det}", prepDet)
                                                                     .replace("{shape}",    shapeString));
        return sb.toString();
    }

    private static String nakedMultiple(Context context, SolveDerivation sd){
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
        return sb.toString();
    }

    private static String hiddenSingleText(Context context, SolveDerivation sd){//TODO this should never be used but already be taken by a special hit that just says: Look at this field, only one left can go;
        BitSet bs = ((NakedSetDerivation) sd).getSubsetMembers().get(0).getRelevantCandidates();
        int note = bs.nextSetBit(0)+1;
        StringBuilder sb = new StringBuilder();
        sb.append("Look there's a hidden single! ");
        sb.append(note);
        sb.append(" is the only note left in the highlighted field. It can be set there.");
        return sb.toString();
    }

    private static String hiddenMultiple(Context context, SolveDerivation sd){
        CandidateSet bs = ((NakedSetDerivation) sd).getSubsetCandidates();

        StringBuilder sb = new StringBuilder();
        sb.append("Look there's a hidden set! {");

        int[] setCandidates = bs.getSetBits();

        sb.append(setCandidates[0]+1);

        for (int i=1; i < setCandidates.length; i++) {
            sb.append(',');
            sb.append(setCandidates[i]+1);
        }

        sb.append("} will in some way be distributed among the highlighted fields. Therefore any other candidates in these fields can be removed.");
        return sb.toString();
    }

    private static String lockedCandidates(Context context, SolveDerivation sd){
        int note = ((LockedCandidatesDerivation) sd).getNote();
        return "there's a locked candidate: "+note+" appears only once in the blue group. It can therefore be deleted in all other fields in the green row/col/block?.";
    }

    private static String xWing(Context context, SolveDerivation sd){
        //int note = ((XWingDerivation) sd).getNote();
        return "If you look at the intersections of green and blue groups some notes appear nowhere else in the lue groups. They can therefore be deleted in the green ones.";
    }

}
