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
import android.view.View
import de.sudoq.controller.sudoku.ActionTreeController

/**
 * Klasse zur Darstellung von Verbindungslinien zwischen Elementen im ActionTree
 */
class BranchingLine(context: Context?, fromX: Int, fromY: Int, toX: Int, toY: Int) : View(context) {
    /**
     * Die Höhe der Linie
     */
    private val height: Int

    /**
     * Die Breite der Linie
     */
    private val width: Int

    /** Methods  */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val linePaint = Paint()
        linePaint.strokeWidth = 5f
        linePaint.style = Paint.Style.STROKE
        linePaint.color = ActionTreeElementView.DEFAULT_COLOR
        linePaint.alpha = 180
        linePaint.isAntiAlias = true
        canvas.drawLine((ActionTreeController.AT_RASTER_SIZE / 2).toFloat(), (ActionTreeController.AT_RASTER_SIZE / 2).toFloat(), (width + ActionTreeController.AT_RASTER_SIZE / 2).toFloat(), height.toFloat(), linePaint)
    }

    companion object {
        /** Attributes  */
        private val LOG_TAG = BranchingLine::class.java.simpleName
    }
    /** Constructors  */
    /**
     * Erzeugt die View für die Darstellung einer Linie für den ActionTree
     *
     * @param context
     * der Applikationskontext
     * @param fromX
     * der x Wert der Startposition in Rasterkoordinaten des
     * ActionTreeControllers
     * @param fromY
     * der y Wert der Startposition in Rasterkoordinaten des
     * ActionTreeControllers
     * @param toX
     * der x Wert der Endposition in Rasterkoordinaten des
     * ActionTreeControllers
     * @param toY
     * der y Wert der Endposition in Rasterkoordinaten des
     * ActionTreeControllers
     */
    init {
        width = toY - fromY
        height = toX - fromX
    }
}