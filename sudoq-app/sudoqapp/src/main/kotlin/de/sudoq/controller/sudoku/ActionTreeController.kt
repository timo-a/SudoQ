/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import de.sudoq.R
import de.sudoq.model.ModelChangeListener
import de.sudoq.model.actionTree.ActionTreeElement
import de.sudoq.view.FullScrollLayout
import de.sudoq.view.ZoomableView
import de.sudoq.view.actionTree.*

/**
 * Reagiert auf Interaktionen des Benutzers mit dem Aktionsbaum.
 */
class ActionTreeController(
    /**
     * Kontext, von dem der ActionTreeController verwendet wird
     */
    private val context: SudokuActivity
) : ActionTreeNavListener, ModelChangeListener<ActionTreeElement> {

    /**
     * Die ScrolLView des ActionTrees
     */
    private val actionTreeScroll: FullScrollLayout

    /**
     * Das Layout, in dem der ActionTree angezeigt wird
     */
    private var relativeLayout: ActionTreeLayout? = null

    /**
     * Das Layout für den ActionTree.
     */
    private inner class ActionTreeLayout(context: Context) : RelativeLayout(context), ZoomableView {
        /**
         * {@inheritDoc}
         */
        override fun zoom(factor: Float): Boolean {
            AT_RASTER_SIZE = (factor * 70).toInt()
            MAX_ELEMENT_VIEW_SIZE = AT_RASTER_SIZE - 2
            refresh()
            return true
        }

        /**
         * {@inheritDoc}
         */
        override fun getMinZoomFactor(): Float {
            return 0.2f
        }

        /**
         * {@inheritDoc}
         */
        override fun getMaxZoomFactor(): Float {
            return 2.0f
        }
    }

    /**
     * Die View des aktuellen Elements
     */
    private var activeElementView: ActiveElement? = null

    /**
     * Das aktuelle Element
     */
    private var active: ActionTreeElement? = null

    /**
     * Die aktuelle X-Position der ScrollView
     */
    private var activeX = 0 //todo use Pair or subclass Pair with properties x,y

    /**
     * Die aktuelle Y-Position der ScrollView
     */
    private var activeY = 0

    /**
     * Initial x coord of the ActionTrees root element.
     */
    private var rootElementInitX = 0

    /**
     * Initial y coord of the ActionTrees root element.
     */
    private var rootElementInitY = 0

    /**
     * Das Layout in dem sich der ActionTree befindet.
     */
    private val actionTreeLayout: RelativeLayout

    /**
     * Die Höhe des Aktionsbaumes beim letzten Zeichnen.
     */
    private var actionTreeHeight = 0

    /**
     * Die Breite des Aktionsbaumes beim letzten Zeichnen.
     */
    private var actionTreeWidth = 0

    /**
     * Die aktuelle Ausrichtung des Geräts
     */
    private var orientation = 0

    /**
     * Erzeugt die View des ActionTrees.
     */
    private fun inflateActionTree() {
        rootElementInitX = 1 // frameLayout.getHeight() / 2;
        rootElementInitY = 1 // frameLayout.getWidth() / 2;
        relativeLayout = ActionTreeLayout(context)

        // Setting active element
        active = context.game!!.stateHandler!!.currentState
        val root = context.game!!.stateHandler!!.actionTree.root

        // Get screen orientation
        orientation = context.resources.configuration.orientation

        // Draw elements
        actionTreeHeight = 0
        actionTreeWidth = 0
        drawElementsUnder(root, rootElementInitX, rootElementInitY)

        // Dummy element for a margin at bottom
        Log.d(LOG_TAG, "ActionTree height: $actionTreeHeight")
        Log.d(LOG_TAG, "ActionTree width: $actionTreeWidth")
        val view = View(context)
        val viewLayoutParams = RelativeLayout.LayoutParams(AT_RASTER_SIZE, AT_RASTER_SIZE)
        viewLayoutParams.topMargin =
            (if (orientation == Configuration.ORIENTATION_PORTRAIT) actionTreeHeight else actionTreeWidth + 1) * AT_RASTER_SIZE
        viewLayoutParams.leftMargin =
            (if (orientation == Configuration.ORIENTATION_PORTRAIT) actionTreeWidth else actionTreeHeight + 1) * AT_RASTER_SIZE
        view.layoutParams = viewLayoutParams
        relativeLayout!!.addView(view)

        // Add active element view
        relativeLayout!!.addView(activeElementView)

        // Put the new RelativeLayout containing the ActionTree into the
        // ScrollView
        actionTreeScroll.addView(relativeLayout!!)
    }

    /**
     * Zeichnet die Elemente unter dem spezifizierten.
     *
     * @param root
     * Das Ausgangselement
     * @param x
     * Die Position des Elements in x-Richtung
     * @param y
     * Die Position des Elements in y-Richtung
     * @return Die Anzahl der unter dem übergebenen Element gezeichneten
     * Elemente
     */
    private fun drawElementsUnder(root: ActionTreeElement, x: Int, y: Int): Int {
        var root: ActionTreeElement? = root
        var x = x
        var split = false
        while (root != null) {
            drawElementAt(root, x, y)
            if (root.isSplitUp()) {
                split = true
                break
            }
            root = if (root.hasChildren()) root.iterator().next() else null
            if (root != null) {
                drawLine(x, y, x + 1, y)
            }
            x++
        }
        var dy = 0
        if (split) {
            for (child in root!!) {
                drawLine(x, y, x + 1, y + dy)
                dy += drawElementsUnder(child, x + 1, y + dy)
            }
        }
        actionTreeHeight = if (x > actionTreeHeight) x else actionTreeHeight
        actionTreeWidth = if (y > actionTreeWidth) y else actionTreeWidth
        return if (dy > 0) dy else 1
    }

    /**
     * Zeichnet an der angegebenen Stelle das spezifizierte Element.
     *
     * @param element
     * Das zu zeichnende Element
     * @param x
     * Die x-Position an der gezeichnet werden soll
     * @param y
     * Die y-Position an der gezeichnet werden soll
     */
    private fun drawElementAt(element: ActionTreeElement, x: Int, y: Int) {
        var x = x
        var y = y
        if (orientation != Configuration.ORIENTATION_PORTRAIT) {
            x = y.also { y = x }
        }
        var view: ActionTreeElementView = ActionElement(context, null, element)
        if (element.isMarked) {
            view = BookmarkedElement(context, view, element)
        }
        if (element.isSplitUp()) {
            view = BranchingElement(context, view, element)
        }
        view.registerActionTreeNavListener(context)
        view.registerActionTreeNavListener(this)
        if (element === active) {
            activeElementView = ActiveElement(context, view, element)
            activeX = x * AT_RASTER_SIZE
            activeY = y * AT_RASTER_SIZE
            val viewLayoutParams = RelativeLayout.LayoutParams(
                AT_RASTER_SIZE,
                AT_RASTER_SIZE
            )
            viewLayoutParams.topMargin = x * AT_RASTER_SIZE
            viewLayoutParams.leftMargin = y * AT_RASTER_SIZE
            activeElementView!!.layoutParams = viewLayoutParams
        } else {
            val viewLayoutParams = RelativeLayout.LayoutParams(
                AT_RASTER_SIZE,
                AT_RASTER_SIZE
            )
            viewLayoutParams.topMargin = x * AT_RASTER_SIZE
            viewLayoutParams.leftMargin = y * AT_RASTER_SIZE
            view.layoutParams = viewLayoutParams
        }
        if (element.isCorrect) {
            view.changeColor(ActionTreeElementView.CORRECT_COLOR)
        }
        if (element.isMistake) {
            view.changeColor(ActionTreeElementView.WRONG_COLOR)
        }
        relativeLayout!!.addView(view)
    }

    /**
     * Zeichnet eine Linie von/bis zu den spezifizierten Positionen.
     *
     * @param fromX
     * Startposition x-Richtung
     * @param fromY
     * Startposition y-Richtung
     * @param toX
     * Endposition x-Richtung
     * @param toY
     * Endposition y-Richtung
     */
    private fun drawLine(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawLineP(fromX, fromY, toX, toY)
        } else {
            drawLineP(fromY, fromX, toY, toX)
        }
    }

    /**
     * Zeichnet eine Linie von/bis zu den spezifizierten Positionen.
     *
     * @param fromX
     * Startposition x-Richtung
     * @param fromY
     * Startposition y-Richtung
     * @param toX
     * Endposition x-Richtung
     * @param toY
     * Endposition y-Richtung
     */
    private fun drawLineP(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val branchingLine = BranchingLine(
            context, fromX * AT_RASTER_SIZE, fromY * AT_RASTER_SIZE,
            toX * AT_RASTER_SIZE + AT_RASTER_SIZE / 2, toY * AT_RASTER_SIZE
        )
        val branchingLineLayoutParams = RelativeLayout.LayoutParams(
            toY * AT_RASTER_SIZE
                    - fromY * AT_RASTER_SIZE + AT_RASTER_SIZE,
            toX * AT_RASTER_SIZE - fromX * AT_RASTER_SIZE + AT_RASTER_SIZE
        )
        branchingLineLayoutParams.topMargin = fromX * AT_RASTER_SIZE
        branchingLineLayoutParams.leftMargin = fromY * AT_RASTER_SIZE
        branchingLine.layoutParams = branchingLineLayoutParams
        relativeLayout!!.addView(branchingLine)
    }


    /**
     * Aktualisiert die ActionTree Ansicht neu
     */
    fun refresh() {
        inflateActionTree()
        actionTreeLayout.visibility = View.VISIBLE
    }
    /** Methods  */
    /**
     * {@inheritDoc}
     */
    override fun onHoverTreeElement(ate: ActionTreeElement) {
        context.game!!.goToState(ate)
    }

    /**
     * {@inheritDoc}
     */
    override fun onLoadState(ate: ActionTreeElement) {
        context.game!!.goToState(ate)
    }

    /**
     * Macht den ActionTree sichtbar oder auch nicht gemäß dem Parameter
     *
     * @param show
     * true falls der Baum sichtbar sein soll false falls nicht
     */
    fun setVisibility(show: Boolean) {
        if (show) {
            inflateActionTree()
            Log.d(
                LOG_TAG,
                "Show action tree: Element: (" + activeY + AT_RASTER_SIZE / 2 + ", " + activeX + AT_RASTER_SIZE / 2 + ")"
            )
            actionTreeScroll.scrollTo(
                activeY + AT_RASTER_SIZE / 2, activeX + AT_RASTER_SIZE
                        / 2
            )
            actionTreeLayout.visibility = View.VISIBLE
        } else {
            actionTreeLayout.visibility = View.INVISIBLE
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun onModelChanged(obj: ActionTreeElement) {
        if (context.isActionTreeShown) {
            refresh()
        }
    }

    companion object {
        /** Attributes  */
        /**
         * Der Log-Tag
         */
        private val LOG_TAG = ActionTreeController::class.java.simpleName

        /**
         * Die Größe des intern verwendeten Rasters in Pixeln
         */
        var AT_RASTER_SIZE = 70

        /**
         * Maximale erlaubte Größe in Pixeln eines Elements
         */
        var MAX_ELEMENT_VIEW_SIZE = 68
    }
    /** Constructors  */
    /**
     * Erstellt einen neuen ActionTreeController. Wirft eine
     * IllegalArgumentException, falls der context null ist.
     *
     * @param context
     * Kontext, von welchem der ActionTreeController verwendet werden
     * soll
     * @throws IllegalArgumentException
     * Wird geworfen, falls der übergebene Context null ist
     */
    init {
        this.context.game!!.stateHandler!!.registerListener(this)
        actionTreeLayout =
            context.findViewById<View>(R.id.sudoku_action_tree_layout) as RelativeLayout
        actionTreeScroll =
            context.findViewById<View>(R.id.sudoku_action_tree_scroll) as FullScrollLayout
    }
}