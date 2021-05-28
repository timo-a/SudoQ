package de.sudoq.controller.sudoku.hints

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import de.sudoq.R
import de.sudoq.controller.menus.Utility
import de.sudoq.controller.sudoku.SudokuActivity
import de.sudoq.model.solverGenerator.solution.*
import de.sudoq.model.solvingAssistant.HintTypes
import de.sudoq.model.sudoku.getGroupShape
import java.util.*

/**
 * Created by timo on 04.10.16.
 */
object HintFormulator {
    private const val LOG_TAG = "HintFormulator"
    fun getText(context: Context, sd: SolveDerivation): String {
        var text: String
        Log.d("HintForm", sd.type.toString())
        when (sd.type) {
            HintTypes.LastDigit -> text = lastDigitText(context, sd)
            HintTypes.LastCandidate -> text = lastCandidateText(context, sd)
            HintTypes.LeftoverNote -> text = leftoverNoteText(context, sd)
            HintTypes.NakedSingle -> text = nakedSingleText(context, sd)
            HintTypes.NakedPair, HintTypes.NakedTriple, HintTypes.NakedQuadruple, HintTypes.NakedQuintuple -> text = nakedMultiple(context, sd)
            HintTypes.HiddenSingle -> text = hiddenSingleText(context, sd)
            HintTypes.HiddenPair, HintTypes.HiddenTriple, HintTypes.HiddenQuadruple, HintTypes.HiddenQuintuple -> text = hiddenMultiple(context, sd)
            HintTypes.LockedCandidatesExternal -> text = lockedCandidates(context, sd)
            HintTypes.XWing -> text = xWing(context, sd)
            HintTypes.NoNotes -> {
                text = context.getString(R.string.hint_backtracking)
                text += context.getString(R.string.hint_fill_out_notes)
            }
            HintTypes.Backtracking -> text = context.getString(R.string.hint_backtracking)
            else -> text = "We found a hint, but did not implement a representation yet. That's a bug! Please send us a screenshot so we can fix it!"
        }
        return text
    }

    private fun aCellIsEmpty(sActivity: SudokuActivity): Boolean {
        val sudoku = sActivity.game.sudoku
        for (f in sudoku!!) if (f.isCompletelyEmpty) {
            return true
        }
        return false
    }

    private fun lastDigitText(context: Context, sd: SolveDerivation): String {
        val d = sd as LastDigitDerivation
        val cs = d.constraintShape
        val shapeString = Utility.constraintShapeAccDet2string(context, cs)
        val shapeGender = Utility.getGender(context, cs)
        val shapeDeterminer = Utility.gender2AccDeterminer(context, shapeGender)
        val highlightSuffix = Utility.gender2AccSufix(context, shapeGender)
        return context.getString(R.string.hint_lastdigit).replace("{shape}", shapeString)
                .replace("{determiner}", shapeDeterminer)
                .replace("{suffix}", highlightSuffix)
    }

    private fun lastCandidateText(context: Context, sd: SolveDerivation): String {
        val sb = StringBuilder()
        sb.append(context.getString(R.string.hint_lastcandidate))
        return sb.toString()
    }

    private fun leftoverNoteText(context: Context, sd: SolveDerivation): String {
        val d = sd as LeftoverNoteDerivation
        val cs = d.constraintShape
        val shapeString = Utility.constraintShapeAccDet2string(context, cs)
        val shapeGender = Utility.getGender(context, cs)
        val shapeDeterminer = Utility.gender2AccDeterminer(context, shapeGender)
        val highlightSuffix = Utility.gender2AccSufix(context, shapeGender)
        var versionNumber = -1
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
            versionNumber = longVersionCode.toInt()
            //versionNumber = pinfo.versionCode;
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        Log.d(LOG_TAG, "leftovernotetext is called. and versionNumber is: $versionNumber")
        return context.getString(R.string.hint_leftovernote).replace("{note}", d.note + 1 + "")
                .replace("{shape}", shapeString)
                .replace("{determiner}", shapeDeterminer)
                .replace("{suffix}", highlightSuffix)
    }

    private fun nakedSingleText(context: Context, sd: SolveDerivation): String {
        val d = sd as NakedSetDerivation
        val bs = d.getSubsetMembers()[0].relevantCandidates
        val note = bs.nextSetBit(0) + 1
        val sb = StringBuilder()
        sb.append(context.getString(R.string.hint_nakedsingle_look))
        sb.append(' ')
        sb.append(context.getString(R.string.hint_nakedsingle_note).replace("{note}", note.toString() + ""))
        sb.append(' ')
        val shapeString = Utility.constraintShapeGenDet2string(context, getGroupShape(d.constraint!!))
        val shapePrepDet = Utility.getGender(context, getGroupShape(d.constraint!!))
        val prepDet = Utility.gender2inThe(context, shapePrepDet)
        sb.append(context.getString(R.string.hint_nakedsingle_remove).replace("{prep det}", prepDet)
                .replace("{shape}", shapeString))
        return sb.toString()
    }

    private fun nakedMultiple(context: Context, sd: SolveDerivation): String {
        val bs: BitSet? = (sd as NakedSetDerivation).subsetCandidates
        val seq = StringBuilder()
        seq.append("{")
        var i = bs!!.nextSetBit(0)
        seq.append(i + 1)
        i = bs.nextSetBit(i + 1)
        while (i >= 0) {
            seq.append(", ")
            seq.append(i + 1)
            i = bs.nextSetBit(i + 1)
        }
        seq.append("}")
        return context.getString(R.string.hint_nakedset).replace("{symbols}", seq.toString())
    }

    private fun hiddenSingleText(context: Context, sd: SolveDerivation): String { //TODO this should never be used but already be taken by a special hit that just says: Look at this cell, only one left can go;
        val bs = (sd as HiddenSetDerivation).getSubsetMembers()[0].relevantCandidates
        val note = (bs.nextSetBit(0) + 1).toString() + ""
        return context.getString(R.string.hint_hiddensingle).replace("{note}", note)
    }

    private fun hiddenMultiple(context: Context, sd: SolveDerivation): String {
        val bs = (sd as HiddenSetDerivation).subsetCandidates
        val sb = StringBuilder()
        sb.append("{")
        val setCandidates = bs!!.setBits
        sb.append(setCandidates[0] + 1)
        for (i in 1 until setCandidates.size) {
            sb.append(", ")
            sb.append(setCandidates[i] + 1)
        }
        sb.append("}")
        return context.getString(R.string.hint_hiddenset).replace("{symbols}", sb.toString())
    }

    private fun lockedCandidates(context: Context, sd: SolveDerivation): String {
        val note = (sd as LockedCandidatesDerivation).getNote().toString() + ""
        return context.getString(R.string.hint_lockedcandidates).replace("{note}", note)
    }

    private fun xWing(context: Context, sd: SolveDerivation): String {
        //int note = ((XWingDerivation) sd).getNote();
        return context.getString(R.string.hint_xWing)
    }
}