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

    @JvmStatic
    fun getText(context: Context, sd: SolveDerivation): String {
        Log.d("HintForm", sd.type.toString())

        return when (sd.type) {
            HintTypes.LastDigit -> lastDigitText(context, sd)
            HintTypes.LastCandidate -> lastCandidateText(context, sd)
            HintTypes.LeftoverNote -> leftoverNoteText(context, sd)
            HintTypes.NakedSingle -> nakedSingleText(context, sd)
            HintTypes.NakedPair,
            HintTypes.NakedTriple,
            HintTypes.NakedQuadruple,
            HintTypes.NakedQuintuple -> nakedMultiple(context, sd)
            HintTypes.HiddenSingle -> hiddenSingleText(context, sd)
            HintTypes.HiddenPair,
            HintTypes.HiddenTriple,
            HintTypes.HiddenQuadruple,
            HintTypes.HiddenQuintuple -> hiddenMultiple(context, sd)
            HintTypes.LockedCandidatesExternal -> lockedCandidates(context, sd)
            HintTypes.XWing -> xWing(context, sd)
            HintTypes.NoNotes -> {
                context.getString(R.string.hint_backtracking) +
                        context.getString(R.string.hint_fill_out_notes)
            }
            HintTypes.Backtracking -> context.getString(R.string.hint_backtracking)
            else -> "We found a hint, but did not implement a representation yet. That's a bug! Please send us a screenshot so we can fix it!"
        }
    }

    private fun aCellIsEmpty(sActivity: SudokuActivity): Boolean {
        val sudoku = sActivity.game.sudoku
        return sudoku!!.any { it.isCompletelyEmpty }
    }

    private fun lastDigitText(context: Context, sd: SolveDerivation): String {
        val d = sd as LastDigitDerivation
        val cs = d.constraintShape
        val shapeString = Utility.constraintShapeAccDet2string(context, cs)
        val shapeGender = Utility.getGender(context, cs)
        val shapeDeterminer = Utility.gender2AccDeterminer(context, shapeGender)
        val highlightSuffix = Utility.gender2AccSufix(context, shapeGender)
        return context.getString(R.string.hint_lastdigit)
                .replace("{shape}", shapeString)
                .replace("{determiner}", shapeDeterminer)
                .replace("{suffix}", highlightSuffix)
    }

    private fun lastCandidateText(context: Context, sd: SolveDerivation): String {
        return context.getString(R.string.hint_lastcandidate)
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
        return context.getString(R.string.hint_leftovernote)
                .replace("{note}", (d.note + 1).toString())
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
        sb.append(context.getString(R.string.hint_nakedsingle_note)
                .replace("{note}", note.toString() + ""))
        sb.append(' ')
        val shapeString = Utility.constraintShapeGenDet2string(context, getGroupShape(d.constraint!!))
        val shapePrepDet = Utility.getGender(context, getGroupShape(d.constraint!!))
        val prepDet = Utility.gender2inThe(context, shapePrepDet)
        sb.append(context.getString(R.string.hint_nakedsingle_remove)
                .replace("{prep det}", prepDet)
                .replace("{shape}", shapeString))
        return sb.toString()
    }

    private fun nakedMultiple(context: Context, sd: SolveDerivation): String {
        val bs: BitSet = (sd as NakedSetDerivation).subsetCandidates!!
        val symbols : MutableList<Int> = mutableListOf()

        //collect all set bits, we assume there will never be an overflow
        var i = bs.nextSetBit(0)
        while (i >= 0) {
            symbols.add(i)
            i = bs.nextSetBit(i + 1)
        }

        val symbolsString = symbols
                .map { + 1 } //add one for user representation
                .joinToString(", ", "{", "}", transform = {it.toString()})

        return context.getString(R.string.hint_nakedset).replace("{symbols}", symbolsString)
    }

    private fun hiddenSingleText(context: Context, sd: SolveDerivation): String { //TODO this should never be used but already be taken by a special hit that just says: Look at this cell, only one left can go;
        val bs = (sd as HiddenSetDerivation).getSubsetMembers()[0].relevantCandidates
        val note = (bs.nextSetBit(0) + 1).toString()
        return context.getString(R.string.hint_hiddensingle).replace("{note}", note)
    }

    private fun hiddenMultiple(context: Context, sd: SolveDerivation): String {
        val bs = (sd as HiddenSetDerivation).subsetCandidates
        val hiddenMultiples = bs!!.setBits
                .map { + 1 }
                .joinToString(", ", "{", "}", transform = {it.toString()})

        return context.getString(R.string.hint_hiddenset).replace("{symbols}", hiddenMultiples)
    }

    private fun lockedCandidates(context: Context, sd: SolveDerivation): String {
        val note = (sd as LockedCandidatesDerivation).getNote().toString()
        return context.getString(R.string.hint_lockedcandidates).replace("{note}", note)
    }

    private fun xWing(context: Context, sd: SolveDerivation): String {
        //int note = ((XWingDerivation) sd).getNote();
        return context.getString(R.string.hint_xWing)
    }
}