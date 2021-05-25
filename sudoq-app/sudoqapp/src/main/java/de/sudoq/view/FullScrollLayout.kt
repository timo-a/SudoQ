/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import de.sudoq.view.FullScrollLayout

/**
 * Eine ScrollView, welche sowohl horizontales, als auch vertikales Scrollen
 * ermöglicht.
 */
class FullScrollLayout : LinearLayout {
    /**
     * Gibt den aktuellen Zoom-Faktor dieses Layouts zurück.
     *
     * @return Der aktuelle Zoom-Faktor
     */
    /**
     * Setzt den aktuellen Zoom-Faktor des Layouts.
     *
     * @param newZoom
     * Der neue Zoomfaktor
     */
    /**
     * Der aktuelle Zoom Faktor.
     */
    var zoomFactor = 0f

    /**
     * Der View, der horizontales Scrollen erlaubt und im View für vertikales
     * Scrollen enthalten ist.
     */
    private var horizontalScrollView: HorizontalScroll? = null

    /**
     * Der View, der vertikales Scrollen erlaubt und den View für horizontales
     * Scrollen enthällt.
     */
    private var verticalScrollView: VerticalScroll? = null

    /**
     * Aktueller X- und Y-Wert der ScrollViews.
     */
    private val current: Point = Point(0, 0)

    /**
     * Der Zoom-Gesten-Detektor
     */
    private var scaleGestureDetector: ScaleGestureDetector

    /**
     * Die View, die sich in diesem FullScrollLayout befindet.
     */
    private var childView: ZoomableView? = null

    /**
     * Instanziiert ein neues ScrollLayout mit den gegebenen Parametern
     *
     * @param context
     * der Applikationskontext
     * @param set
     * das Android AttributeSet
     */
    constructor(context: Context?, set: AttributeSet?) : super(context, set) {
        initialize()
        scaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())
    }

    /**
     * Instanziiert ein neues FullScrollLayout, welches auf Wunsch als
     * qudratisch festgelegt wird.
     *
     * @param context
     * Der Kontext, in dem dieses Layout angelegt wird
     */
    constructor(context: Context?) : super(context) {
        initialize()
        scaleGestureDetector = ScaleGestureDetector(context, ScaleGestureListener())
    }

    /**
     * Initialisiert ein neues Layout.
     */
    private fun initialize() {
        removeAllViews()
        if (zoomFactor == 0f) {
            zoomFactor = 1.0f
        }
        verticalScrollView = VerticalScroll(context)
        horizontalScrollView = HorizontalScroll(context)
        verticalScrollView!!.addView(horizontalScrollView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        this.addView(verticalScrollView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    /**
     * Fügt eine View zu diesem Layout hinzu. Ist bereits eine vorhanden, so
     * wird diese gelöscht. Die spezifizierte View muss ZoomableView implementiere, sonst wird nichts getan.
     */
    override fun addView(v: View) {
        if (v is ZoomableView) {
            horizontalScrollView!!.removeAllViews()
            childView = v
            horizontalScrollView!!.addView(v, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        }
    }

    /**
     * Verarbeitet TouchEvents mit einem Finger, also klicken und scrollen.
     *
     * @param event
     * Das MotionEvent
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 1) { //just one finger -> scroll
            horizontalScrollView!!.performTouch(event)
            verticalScrollView!!.performTouch(event)
        } else if (childView != null) { //2 or more fingers
            scaleGestureDetector.onTouchEvent(event)
        }
        return true
    }

    /**
     * {@inheritDoc}
     */
    override fun scrollTo(x: Int, y: Int) {
        // Has to be changed because the tree algo uses different coords.
        current.scrollValueX = (x - width / 2).toFloat()
        current.scrollValueY = (y - height / 2).toFloat() //so apparently "tree algo" needs this... it is parameter are passed with inverse transformation in onscale, to even it out
        verticalScrollView!!.post {
            verticalScrollView!!.scrollTo(current.scrollValueX.toInt(), current.scrollValueY.toInt())
            current.scrollValueY = verticalScrollView!!.scrollY.toFloat()
        }
        horizontalScrollView!!.post {
            horizontalScrollView!!.scrollTo(current.scrollValueX.toInt(), current.scrollValueY.toInt())
            current.scrollValueX = horizontalScrollView!!.scrollX.toFloat()
        }
    }

    /**
     * Setzt den Zoom zurück.
     */
    fun resetZoom() {
        childView!!.zoom(1.0f)
        zoomFactor = 1.0f
        scrollTo(0, 0)
    }

    /**
     * Diese Klasse überschreibt das onTouch-Event der ScrollView, sodass dieses
     * an dieses FullScrollLayout weitergereicht wird. Durch die
     * performTouch-Methode kann das Event zurückgereicht werden.
     */
    private inner class VerticalScroll(context: Context?) : ScrollView(context) {
        override fun onTouchEvent(event: MotionEvent): Boolean {
            return false
        }

        /**
         * Für das übergebene Touch-Event aus.
         *
         * @param event
         * Das auszuführende Touch-Event
         */
        fun performTouch(event: MotionEvent) {
            try {
                event.x
                event.y
                super.onTouchEvent(event)
                current.scrollValueY = this.scrollY.toFloat()
            } catch (e: Exception) {
                // Old android versions sometimes throw an exception when
                // putting and Event of one view in the onTouch of
                // another view. We just catch that and do nothing
            }
        }

        /**
         * Instanziiert eine neue vertikale ScrollView.
         *
         * @param context
         * Der Kontext
         */
        init {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }
    }

    /**
     * Diese Klasse überschreibt das onTouch-Event der HorizontalScrollView,
     * sodass dieses an dieses FullScrollLayout weitergereicht wird. Durch die
     * performTouch-Methode kann das Event zurückgereicht werden.
     */
    private inner class HorizontalScroll(context: Context?) : HorizontalScrollView(context) {
        override fun onTouchEvent(event: MotionEvent): Boolean {
            return false
        }

        /**
         * Für das übergebene Touch-Event aus.
         *
         * @param event
         * Das auszuführende Touch-Event
         */
        fun performTouch(event: MotionEvent) {
            try {
                event.x
                event.y
                super.onTouchEvent(event)
                current.scrollValueX = this.scrollX.toFloat()
            } catch (e: Exception) {
                // Old android versions sometimes throw an exception when
                // putting and Event of one view in the onTouch of
                // another view. We just catch that and do nothing
            }
        }

        /**
         * Instanziiert eine neue horizontale ScrollView.
         *
         * @param context
         * Der Kontext
         */
        init {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }
    }

    private inner class ScaleGestureListener : SimpleOnScaleGestureListener() {
        var focus: Point? = null
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            /* TODO still buggy, if sudokuLayout.maxzoom is unrestrained focuspoint appears ~1 cell next to where it is supposed to be. try out by painting focus point in hintpainter */
            if (detector.scaleFactor < 0.01) return false

            /* compute the new absolute zoomFactor (∈ [1,2]) by multiplying the old one with the scale Factor*/
            val scaleFactor = detector.scaleFactor
            var newZoom = zoomFactor * scaleFactor

            // Don't let the object get too large/small.
            val lowerLimit = childView!!.minZoomFactor
            val upperLimit = childView!!.maxZoomFactor
            newZoom = Math.max(Math.min(newZoom, upperLimit), lowerLimit) //ensure newZoom ∈ [lowerLim,upperLim]
            if (!childView!!.zoom(newZoom)) {
                return false
            }
            zoomFactor = newZoom

            /* NB: we scale in comparison to the case zoom = 1.0, not in comparison to the current one
			 * if we just scale everything on the canvas, fp (focusPoint) and tl (i.e. topleft corner of window) are out of sync.
			 * especially tl is initially 0,0 so it doesn't scale anywhere...
			 *
			 * in the normalized case fp-tl is fp (bec tl==0)
			 * we want that distance to be kept so we subtract it from the scaled value for fp namely fp* zoom.
			 * hence lt = focus * zoom - focus
			 * */current.scrollValueX = focus!!.scrollValueX * zoomFactor - focus!!.scrollValueX
            current.scrollValueY = focus!!.scrollValueY * zoomFactor - focus!!.scrollValueY
            /* even out the transformation on scrollTo */transform(current)
            scrollTo2(current)
            Log.d(LOG_TAG, "Scaled to: " + current.toString() + "    "
                    + "Focus: " + focus.toString() + "   "
                    + "zoom: " + zoomFactor)
            return true
        }

        override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
            Log.d(LOG_TAG, "On scale begin-------------------------(")
            focus = Point(scaleGestureDetector.focusX, scaleGestureDetector.focusY)
            (childView as SudokuLayout?)!!.focusX = focus!!.scrollValueX //only for debug
            (childView as SudokuLayout?)!!.focusY = focus!!.scrollValueY
            return true
        }

        override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {
            Log.d(LOG_TAG, "On scale end----------------)")
        }

        /* transform to even out inverse transformation in scrollTo(x,y). A trafo exists there because of "tree algo" */
        private fun transform(p: Point) {
            p.scrollValueX += (width / 2).toFloat()
            p.scrollValueY += (height / 2).toFloat()
        }

        private fun scrollTo2(p: Point) {
            /* save for log */
            current.scrollValueX = p.scrollValueX
            current.scrollValueY = p.scrollValueY
            scrollTo(p.scrollValueX.toInt(), p.scrollValueY.toInt())
        }
    }

    private inner class Point(
            /**
             * Gibt den aktuell gescrollten X-Wert zurück.
             *
             * @return der aktuell gescrollte X-Wert
             */
            var scrollValueX: Float,
            /**
             * Gibt den aktuell gescrollten Y-Wert zurück.
             *
             * @return der aktuell gescrollte Y-Wert
             */
            var scrollValueY: Float) {
        override fun toString(): String {
            return scrollValueX as Int.toString() + "," + scrollValueY.toInt()
        }
    }

    companion object {
        /**
         * Der Log-Tag
         */
        private val LOG_TAG = FullScrollLayout::class.java.simpleName
    }
}