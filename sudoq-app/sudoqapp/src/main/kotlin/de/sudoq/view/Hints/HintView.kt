package de.sudoq.view.Hints

import android.content.Context
import android.graphics.Canvas
import android.view.View
import de.sudoq.model.solverGenerator.solution.SolveDerivation
import de.sudoq.view.SudokuLayout
import java.util.*

/**
 * Created by timo on 20.10.16.
 */
open class HintView(context: Context, var sl: SudokuLayout, d: SolveDerivation) : View(context) {
    var derivation: SolveDerivation = d
    var highlightedObjects: MutableList<View> = Stack()
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (v in highlightedObjects) v.draw(canvas)
    }

}