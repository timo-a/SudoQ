package de.sudoq.controller.sudoku.hints;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.pm.PackageInfoCompat;

import java.util.BitSet;

import de.sudoq.R;
import de.sudoq.controller.menus.Utility;
import de.sudoq.controller.sudoku.SudokuActivity;
import de.sudoq.model.solverGenerator.solution.HiddenSetDerivation;
import de.sudoq.model.solverGenerator.solution.LastDigitDerivation;
import de.sudoq.model.solverGenerator.solution.LeftoverNoteDerivation;
import de.sudoq.model.solverGenerator.solution.LockedCandidatesDerivation;
import de.sudoq.model.solverGenerator.solution.NakedSetDerivation;
import de.sudoq.model.solverGenerator.solution.SolveDerivation;
import de.sudoq.model.solverGenerator.solution.XWingDerivation;
import de.sudoq.model.sudoku.CandidateSet;
import de.sudoq.model.sudoku.Field;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.Utils;
import de.sudoq.model.sudoku.Utils.ConstraintShape;

/**
 * Created by timo on 04.10.16.
 */
public class HintFormulator {

    private static final String LOG_TAG = "HintFormulator";

    public static String getText(Context context, SolveDerivation sd){
        String text;
        Log.d("HintForm",sd.getType().toString());
        switch (sd.getType()) {
            case LastDigit:
                text = lastDigitText(context, sd);
                break;

            case LastCandidate:
                text = lastCandidateText(context, sd);
                break;

            case LeftoverNote:
                text = leftoverNoteText(context, sd);
                break;

            case NakedSingle:
                text = nakedSingleText(context, sd);
                break;

            case NakedPair:
            case NakedTriple:
            case NakedQuadruple:
            case NakedQuintuple:
                text = nakedMultiple(context, sd);
                break;

            case HiddenSingle:
                text = hiddenSingleText(context, sd);
                break;

            case HiddenPair:
            case HiddenTriple:
            case HiddenQuadruple:
            case HiddenQuintuple:
                text = hiddenMultiple(context, sd);
                break;

            case LockedCandidatesExternal:
                text = lockedCandidates(context, sd);
                break;

            case XWing:
                text = xWing(context, sd);
                break;

            case NoNotes:
                text = context.getString(R.string.hint_backtracking);
                text += context.getString(R.string.hint_fill_out_notes);
                break;

            case Backtracking:
                text = context.getString(R.string.hint_backtracking);
                break;

            default:
                text = "We found a hint, but did not implement a representation yet. That's a bug! Please send us a screenshot so we can fix it!";
        }
        return text;
    }

    private static boolean aFieldIsEmpty(SudokuActivity sActivity){
        Sudoku sudoku = sActivity.getGame().getSudoku();
        for (Field f : sudoku)
            if (f.isCompletelyEmpty()){
                return true;
            }
        return false;
    }


    private static String lastDigitText(Context context, SolveDerivation sd){
        LastDigitDerivation d = (LastDigitDerivation) sd;
        ConstraintShape cs = d.getConstraintShape();

        String shapeString     = Utility.constraintShapeAccDet2string(context, cs);
        String shapeGender     = Utility.getGender(context, cs);
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
        ConstraintShape cs = d.getConstraintShape();

        String shapeString =  Utility.constraintShapeAccDet2string(context, cs);
        String shapeGender     = Utility.getGender(context,cs);
        String shapeDeterminer = Utility.gender2AccDeterminer(context, shapeGender);
        String highlightSuffix = Utility.gender2AccSufix(context, shapeGender);


        int versionNumber = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long longVersionCode= PackageInfoCompat.getLongVersionCode(pInfo);
            versionNumber = (int) longVersionCode;
            //versionNumber = pinfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "leftovernotetext is called. and versionNumber is: " + versionNumber);

        return context.getString(R.string.hint_leftovernote).replace("{note}",  d.getNote()+1+"")
                                                            .replace("{shape}", shapeString)
                                                            .replace("{determiner}" ,shapeDeterminer)
                                                            .replace("{suffix}"     ,highlightSuffix);
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

        StringBuilder seq = new StringBuilder();
        seq.append("{");
        int i = bs.nextSetBit(0);

        seq.append(i+1);

        for (i=bs.nextSetBit(i+1); i >= 0; i = bs.nextSetBit(i+1)) {
            seq.append(", ");
            seq.append(i+1);
        }

        seq.append("}");
        return context.getString(R.string.hint_nakedset).replace("{symbols}",seq.toString());
    }

    private static String hiddenSingleText(Context context, SolveDerivation sd){//TODO this should never be used but already be taken by a special hit that just says: Look at this field, only one left can go;
        BitSet bs = ((HiddenSetDerivation) sd).getSubsetMembers().get(0).getRelevantCandidates();
        String note = (bs.nextSetBit(0)+1)+"";
        return context.getString(R.string.hint_hiddensingle).replace("{note}", note);
    }

    private static String hiddenMultiple(Context context, SolveDerivation sd){
        CandidateSet bs = ((HiddenSetDerivation) sd).getSubsetCandidates();

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        int[] setCandidates = bs.getSetBits();

        sb.append(setCandidates[0]+1);

        for (int i=1; i < setCandidates.length; i++) {
            sb.append(", ");
            sb.append(setCandidates[i]+1);
        }
        sb.append("}");

        return context.getString(R.string.hint_hiddenset).replace("{symbols}",sb.toString());
    }

    private static String lockedCandidates(Context context, SolveDerivation sd){
        String note = ((LockedCandidatesDerivation) sd).getNote() +"";
        return context.getString(R.string.hint_lockedcandidates).replace("{note}", note);
    }

    private static String xWing(Context context, SolveDerivation sd){
        //int note = ((XWingDerivation) sd).getNote();
        return context.getString(R.string.hint_xWing);
    }

}
