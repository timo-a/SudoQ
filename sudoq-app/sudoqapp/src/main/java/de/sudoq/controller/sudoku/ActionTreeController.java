/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import de.sudoq.R;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.actionTree.ActionTreeElement;
import de.sudoq.view.FullScrollLayout;
import de.sudoq.view.ZoomableView;
import de.sudoq.view.actionTree.ActionElement;
import de.sudoq.view.actionTree.ActionTreeElementView;
import de.sudoq.view.actionTree.ActiveElement;
import de.sudoq.view.actionTree.BookmarkedElement;
import de.sudoq.view.actionTree.BranchingElement;
import de.sudoq.view.actionTree.BranchingLine;

/**
 * Reagiert auf Interaktionen des Benutzers mit dem Aktionsbaum.
 */
public class ActionTreeController implements ActionTreeNavListener, ModelChangeListener<ActionTreeElement> {
	/** Attributes */

	/**
	 * Der Log-Tag
	 */
	private static final String LOG_TAG = ActionTreeController.class.getSimpleName();

	/**
	 * Kontext, von dem der ActionTreeController verwendet wird
	 */
	private SudokuActivity context;

	/**
	 * Die ScrolLView des ActionTrees
	 */
	private FullScrollLayout actionTreeScroll;

	/**
	 * Das Layout, in dem der ActionTree angezeigt wird
	 */
	private ActionTreeLayout relativeLayout;

	/**
	 * Das Layout für den ActionTree. 
	 */
	private class ActionTreeLayout extends RelativeLayout implements ZoomableView {

		public ActionTreeLayout(Context context) {
			super(context);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean zoom(float factor) {
			AT_RASTER_SIZE = (int)(factor * 70);
			MAX_ELEMENT_VIEW_SIZE = AT_RASTER_SIZE - 2;
			refresh();
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getMinZoomFactor() {
			return 0.2f;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public float getMaxZoomFactor() {
			return 2.0f;
		}
		
	}
	
	/**
	 * Die View des aktuellen Elements
	 */
	private ActionTreeElementView activeElementView;

	/**
	 * Das aktuelle Element
	 */
	private ActionTreeElement active;

	/**
	 * Die aktuelle X-Position der ScrollView
	 */
	private int activeX;

	/**
	 * Die aktuelle Y-Position der ScrollView
	 */
	private int activeY;

	/**
	 * Die Größe des intern verwendeten Rasters in Pixeln
	 */
	public static int AT_RASTER_SIZE = 70;

	/**
	 * Maximale erlaubte Größe in Pixeln eines Elements
	 */
	public static int MAX_ELEMENT_VIEW_SIZE = 68;

	/**
	 * Initial x coord of the ActionTrees root element.
	 */
	private int rootElementInitX;

	/**
	 * Initial y coord of the ActionTrees root element.
	 */
	private int rootElementInitY;

	/**
	 * Das Layout in dem sich der ActionTree befindet.
	 */
	private RelativeLayout actionTreeLayout;

	/**
	 * Die Höhe des Aktionsbaumes beim letzten Zeichnen.
	 */
	private int actionTreeHeight;

	/**
	 * Die Breite des Aktionsbaumes beim letzten Zeichnen.
	 */
	private int actionTreeWidth;

	/**
	 * Die aktuelle Ausrichtung des Geräts
	 */
	private int orientation;

	/** Constructors */

	/**
	 * Erstellt einen neuen ActionTreeController. Wirft eine
	 * IllegalArgumentException, falls der context null ist.
	 * 
	 * @param context
	 *            Kontext, von welchem der ActionTreeController verwendet werden
	 *            soll
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls der übergebene Context null ist
	 */
	public ActionTreeController(SudokuActivity context) {
		if (context == null) {
			throw new IllegalArgumentException("Unvalid param context!");
		}
		this.context = context;
		this.context.getGame().getStateHandler().registerListener(this);

		this.actionTreeLayout = (RelativeLayout) context.findViewById(R.id.sudoku_action_tree_layout);
		this.actionTreeScroll = (FullScrollLayout) context.findViewById(R.id.sudoku_action_tree_scroll);
	}

	/**
	 * Erzeugt die View des ActionTrees.
	 */
	private void inflateActionTree() {
		this.rootElementInitX = 1;// frameLayout.getHeight() / 2;
		this.rootElementInitY = 1;// frameLayout.getWidth() / 2;

		this.relativeLayout = new ActionTreeLayout(context);

		// Setting active element
		active = context.getGame().getStateHandler().getCurrentState();
		ActionTreeElement root = context.getGame().getStateHandler().getActionTree().getRoot();

		// Get screen orientation
		orientation = context.getResources().getConfiguration().orientation;

		// Draw elements
		this.actionTreeHeight = 0;
		this.actionTreeWidth = 0;
		drawElementsUnder(root, this.rootElementInitX, this.rootElementInitY);

		// Dummy element for a margin at bottom
		Log.d(LOG_TAG, "ActionTree height: " + this.actionTreeHeight);
		Log.d(LOG_TAG, "ActionTree width: " + this.actionTreeWidth);
		View view = new View(context);
		RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(AT_RASTER_SIZE, AT_RASTER_SIZE);
		viewLayoutParams.topMargin = (orientation == Configuration.ORIENTATION_PORTRAIT ? actionTreeHeight : actionTreeWidth + 1) * AT_RASTER_SIZE;
		viewLayoutParams.leftMargin = (orientation == Configuration.ORIENTATION_PORTRAIT ? actionTreeWidth : actionTreeHeight + 1) * AT_RASTER_SIZE;
		view.setLayoutParams(viewLayoutParams);
		this.relativeLayout.addView(view);

		// Add active element view
		this.relativeLayout.addView(this.activeElementView);

		// Put the new RelativeLayout containing the ActionTree into the
		// ScrollView
		this.actionTreeScroll.addView(relativeLayout);
	}

	/**
	 * Zeichnet die Elemente unter dem spezifizierten.
	 * 
	 * @param root
	 *            Das Ausgangselement
	 * @param x
	 *            Die Position des Elements in x-Richtung
	 * @param y
	 *            Die Position des Elements in y-Richtung
	 * @return Die Anzahl der unter dem übergebenen Element gezeichneten
	 *         Elemente
	 */
	private int drawElementsUnder(ActionTreeElement root, int x, int y) {
		boolean split = false;
		while (root != null) {
			drawElementAt(root, x, y);

			if (root.isSplitUp()) {
				split = true;
				break;
			}

			root = root.getChildren().hasNext() ? root.getChildren().next() : null;
			if (root != null) {
				drawLine(x, y, x + 1, y);
			}
			x++;
		}

		int dy = 0;
		if (split) {
			for (ActionTreeElement child : root) {
				drawLine(x, y, x + 1, y + dy);
				dy += drawElementsUnder(child, x + 1, y + dy);
			}
		}

		this.actionTreeHeight = x > this.actionTreeHeight ? x : this.actionTreeHeight;
		this.actionTreeWidth = y > this.actionTreeWidth ? y : this.actionTreeWidth;
		return dy > 0 ? dy : 1;
	}

	/**
	 * Zeichnet an der angegebenen Stelle das spezifizierte Element.
	 * 
	 * @param element
	 *            Das zu zeichnende Element
	 * @param x
	 *            Die x-Position an der gezeichnet werden soll
	 * @param y
	 *            Die y-Position an der gezeichnet werden soll
	 */
	private void drawElementAt(ActionTreeElement element, int x, int y) {
		if (orientation != Configuration.ORIENTATION_PORTRAIT) {
			int temp = x;
			x = y;
			y = temp;
			
		}
		ActionTreeElementView view = new ActionElement(this.context, null, element);

		if (element.isMarked()) {
			view = new BookmarkedElement(this.context, view, element);
		}
		if (element.isSplitUp()) {
			view = new BranchingElement(this.context, view, element);
		}

		view.registerActionTreeNavListener(this.context);
		view.registerActionTreeNavListener(this);

		if (element == this.active) {
			this.activeElementView = new ActiveElement(this.context, view, element);

			this.activeX = x * AT_RASTER_SIZE;
			this.activeY = y * AT_RASTER_SIZE;
			RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(AT_RASTER_SIZE,
					AT_RASTER_SIZE);
			viewLayoutParams.topMargin = x * AT_RASTER_SIZE;
			viewLayoutParams.leftMargin = y * AT_RASTER_SIZE;
			
			this.activeElementView.setLayoutParams(viewLayoutParams);
		} else {
			RelativeLayout.LayoutParams viewLayoutParams = new RelativeLayout.LayoutParams(AT_RASTER_SIZE,
					AT_RASTER_SIZE);
			viewLayoutParams.topMargin = x * AT_RASTER_SIZE;
			viewLayoutParams.leftMargin = y * AT_RASTER_SIZE;
			view.setLayoutParams(viewLayoutParams);
		}

		if (element.isCorrect()) {
			view.changeColor(ActionTreeElementView.CORRECT_COLOR);
		}
		if (element.isMistake()) {
			view.changeColor(ActionTreeElementView.WRONG_COLOR);
		}

		this.relativeLayout.addView(view);
	}

	/**
	 * Zeichnet eine Linie von/bis zu den spezifizierten Positionen.
	 * 
	 * @param fromX
	 *            Startposition x-Richtung
	 * @param fromY
	 *            Startposition y-Richtung
	 * @param toX
	 *            Endposition x-Richtung
	 * @param toY
	 *            Endposition y-Richtung
	 */
	private void drawLine(int fromX, int fromY, int toX, int toY) {
		if (orientation != Configuration.ORIENTATION_PORTRAIT) {
			int tempFrom = fromX;
			int tempTo = toX;
			fromX = fromY;
			fromY = tempFrom;
			toX = toY;
			toY = tempTo;
			
		}
		BranchingLine branchingLine = new BranchingLine(this.context, fromX * AT_RASTER_SIZE, fromY * AT_RASTER_SIZE,
				(toX * AT_RASTER_SIZE + AT_RASTER_SIZE / 2), (toY * AT_RASTER_SIZE));
		RelativeLayout.LayoutParams branchingLineLayoutParams = new RelativeLayout.LayoutParams((toY * AT_RASTER_SIZE
				- fromY * AT_RASTER_SIZE + AT_RASTER_SIZE),
				(toX * AT_RASTER_SIZE - fromX * AT_RASTER_SIZE + AT_RASTER_SIZE));
		branchingLineLayoutParams.topMargin = fromX * AT_RASTER_SIZE;
		branchingLineLayoutParams.leftMargin = fromY * AT_RASTER_SIZE;
		branchingLine.setLayoutParams(branchingLineLayoutParams);
		this.relativeLayout.addView(branchingLine);
	}

	/**
	 * Aktualisiert die ActionTree Ansicht neu
	 */
	public void refresh() {
		inflateActionTree();
		this.actionTreeLayout.setVisibility(View.VISIBLE);
	}

	/** Methods */

	/**
	 * {@inheritDoc}
	 */
	public void onHoverTreeElement(ActionTreeElement ate) {
		context.getGame().goToState(ate);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onLoadState(ActionTreeElement ate) {
		context.getGame().goToState(ate);
	}

	/**
	 * Macht den ActionTree sichtbar oder auch nicht gemäß dem Parameter
	 * 
	 * @param show
	 *            true falls der Baum sichtbar sein soll false falls nicht
	 */
	public void setVisibility(boolean show) {
		if (show) {
			inflateActionTree();
			Log.d(LOG_TAG, "Show action tree: Element: (" + this.activeY + ActionTreeController.AT_RASTER_SIZE / 2 + ", " + this.activeX + ActionTreeController.AT_RASTER_SIZE / 2 + ")");
			this.actionTreeScroll.scrollTo(this.activeY + ActionTreeController.AT_RASTER_SIZE / 2, this.activeX + ActionTreeController.AT_RASTER_SIZE
				/ 2);
					
			this.actionTreeLayout.setVisibility(View.VISIBLE);
		} else {
			this.actionTreeLayout.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onModelChanged(ActionTreeElement obj) {
		if (this.context.isActionTreeShown()) {
			refresh();
		}
	}
}
