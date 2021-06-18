package de.sudoq.controller.menus

import android.content.Context
import de.sudoq.R
import de.sudoq.model.sudoku.Utils.ConstraintShape
import de.sudoq.model.sudoku.complexity.Complexity
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object Utility {
    private fun <T : Enum<*>?> enum2String(typeStrings: Array<String?>, e: T): String? {
        val index = e!!.ordinal
        return if (index >= typeStrings.size) null else typeStrings[index]
    }

    /* SudokuTypes */
    private fun getSudokuTypeValues(context: Context): Array<String?> { //we need a method because of the context...
        //we want to be independent of the order in which the enum fields are defined in their class
        //so we first create an empty array and fill it in an order unknown to us
        val typeStrings = arrayOfNulls<String>(SudokuTypes.values().size)
        typeStrings[SudokuTypes.standard4x4.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_standard_4x4)
        typeStrings[SudokuTypes.standard6x6.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_standard_6x6)
        typeStrings[SudokuTypes.standard9x9.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_standard_9x9)
        typeStrings[SudokuTypes.standard16x16.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_standard_16x16)
        typeStrings[SudokuTypes.Xsudoku.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_xsudoku)
        typeStrings[SudokuTypes.HyperSudoku.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_hyper)
        typeStrings[SudokuTypes.stairstep.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_stairstep_9x9)
        typeStrings[SudokuTypes.squigglya.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_squiggly_a_9x9)
        typeStrings[SudokuTypes.squigglyb.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_squiggly_b_9x9)
        typeStrings[SudokuTypes.samurai.ordinal] =
            context.getString(R.string.advanced_settings_restrict_item_samurai)
        return typeStrings
    }

    fun type2string(context: Context, st: SudokuTypes): String? {
        val typeStrings = getSudokuTypeValues(context)
        val index = st.ordinal
        return if (index >= typeStrings.size) null else typeStrings[index]
    }

    /* Complexities */
    private fun getComplexityValues(context: Context): Array<String?> {
        val typeStrings = arrayOfNulls<String>(Complexity.values().size)
        typeStrings[Complexity.easy.ordinal] = context.getString(R.string.complexity_easy)
        typeStrings[Complexity.medium.ordinal] = context.getString(R.string.complexity_medium)
        typeStrings[Complexity.difficult.ordinal] = context.getString(R.string.complexity_difficult)
        typeStrings[Complexity.infernal.ordinal] = context.getString(R.string.complexity_infernal)
        typeStrings[Complexity.arbitrary.ordinal] =
            "error" //only for compatibility. There's no reason to convert 'arbitrary' to a string
        return typeStrings
    }

    fun string2complexity(context: Context, string: String): Complexity? {
        val complexityStrings = getComplexityValues(context)
        return complexityStrings
            .withIndex()
            .find { (_, label) -> string == label }
            ?.let { (i, _) -> Complexity.values()[i] }
    }

    fun complexity2string(context: Context, st: Complexity): String? {
        val complexityStrings = getComplexityValues(context)
        val index = st.ordinal
        return if (index >= complexityStrings.size) null else complexityStrings[index]
    }

    /* Shapes */
    private fun getConstraintShapeValuesAccusativeDetermined(context: Context): Array<String?> {
        val typeStrings = arrayOfNulls<String>(ConstraintShape.values().size)
        typeStrings[ConstraintShape.Row.ordinal] =
            context.getString(R.string.constraintshape_row_accusative_determined)
        typeStrings[ConstraintShape.Column.ordinal] =
            context.getString(R.string.constraintshape_column_accusative_determined)
        typeStrings[ConstraintShape.Diagonal.ordinal] =
            context.getString(R.string.constraintshape_diagonal_accusative_determined)
        typeStrings[ConstraintShape.Block.ordinal] =
            context.getString(R.string.constraintshape_block_accusative_determined)
        return typeStrings
    }

    private fun getConstraintShapeValuesGenitiveDetermined(context: Context): Array<String?> {
        val typeStrings = arrayOfNulls<String>(ConstraintShape.values().size)
        typeStrings[ConstraintShape.Row.ordinal] =
            context.getString(R.string.constraintshape_row_genitive_determined)
        typeStrings[ConstraintShape.Column.ordinal] =
            context.getString(R.string.constraintshape_column_genitive_determined)
        typeStrings[ConstraintShape.Diagonal.ordinal] =
            context.getString(R.string.constraintshape_diagonal_genitive_determined)
        typeStrings[ConstraintShape.Block.ordinal] =
            context.getString(R.string.constraintshape_block_genitive_determined)
        return typeStrings
    }

    /** returns shape in accusative e.g. look at the row
     * @param context Context(to access localized strings)
     * @return a string representation of the constraint shape passed as enum.
     */
    fun constraintShapeAccDet2string(context: Context, cs: ConstraintShape): String? {
        val shapeStrings = getConstraintShapeValuesAccusativeDetermined(context)
        return enum2String(shapeStrings, cs)
    }

    fun constraintShapeGenDet2string(context: Context, cs: ConstraintShape): String? {
        val shapeStrings = getConstraintShapeValuesGenitiveDetermined(context)
        return enum2String(shapeStrings, cs)
    }

    /*  */
    private fun getConstraintShapeGender(context: Context): Array<String?> {
        return context.resources.getStringArray(R.array.shape_gender_values)
    }

    fun getGender(context: Context, cs: ConstraintShape): String? {
        return enum2String(getConstraintShapeGender(context), cs)
    }

    fun gender2AccDeterminer(context: Context, gender: String?): String? {
        return when (gender) {
            "m" -> context.getString(R.string.maskuline_accusative_determiner)
            "f" -> context.getString(R.string.feminine_accusative_determiner)
            "n" -> context.getString(R.string.neutral_accusative_determiner)
            else -> null
        }
    }

    fun gender2AccSufix(context: Context, gender: String?): String? {
        return when (gender) {
            "m" -> context.getString(R.string.maskuline_accusative_suffix)
            "f" -> context.getString(R.string.feminine_accusative_suffix)
            "n" -> context.getString(R.string.neutral_accusative_suffix)
            else -> null
        }
    }

    fun gender2inThe(context: Context, gender: String): String? {
        return parseStringArray(context, gender[0], R.array.gender2in_the)
    }

    //http://stackoverflow.com/questions/3013655/creating-hashmap-map-from-xml-resources
    private fun parseStringArray(
        context: Context,
        gender: Char,
        stringArrayResourceId: Int
    ): String? {
        val stringArray = context.resources.getStringArray(stringArrayResourceId)

        return stringArray
            .find { entry -> entry[0] == gender } //format is gender|inflection, where gender has len 1
            ?.substring(2)

    }

    /**
     * Kopiert die Dateien zwischen den angegeben Streams
     *
     * @param in
     * Der Eingabestream
     * @param out
     * Der Ausgabestream
     * @throws IOException
     * Wird geworfen, falls beim Lesen/Schreiben der Streams ein
     * Fehler auftritt
     */
    @Throws(IOException::class)
    fun copyFileOnStreamLevel(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }
}