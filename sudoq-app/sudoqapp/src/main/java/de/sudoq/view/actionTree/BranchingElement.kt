/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view.actionTree

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import de.sudoq.controller.sudoku.ActionTreeController
import de.sudoq.model.actionTree.ActionTreeElement

/**
 * Diese Subklasse des ActionTreeElements definiert die Erscheinung eines
 * gespeicherten Lesezeichens im Aktionsbaum.
 */
class BranchingElement
/** Constructors  */
(context: Context, inner: ActionTreeElementView, ate: ActionTreeElement) :
        ActionTreeElementView(context, inner, ate) {

    /**
     * {@inheritDoc}
     */
    override fun paintCanvas(canvas: Canvas) {
        val elementPaint = Paint()
        elementPaint.color = actionColor
        elementPaint.style = Paint.Style.STROKE
        elementPaint.strokeWidth = 8f
        elementPaint.isAntiAlias = true
        val radius = (ActionTreeController.MAX_ELEMENT_VIEW_SIZE.toFloat() / 3).toInt()
        canvas.drawCircle((ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2).toFloat(), (ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2).toFloat(), radius.toFloat(), elementPaint)

        // Paint innerPaint = new Paint();
        // innerPaint.setColor(actionColor);
        // innerPaint.setAntiAlias(true);
        // int innerRadius = (int) ((float)
        // ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 6);
        // canvas.drawCircle(ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2,
        // ActionTreeController.MAX_ELEMENT_VIEW_SIZE / 2, innerRadius,
        // innerPaint);
    }
}