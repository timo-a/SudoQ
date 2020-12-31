/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import de.sudoq.controller.sudoku.CellInteractionListener;
import de.sudoq.controller.sudoku.board.CellViewPainter;
import de.sudoq.controller.sudoku.board.CellViewStates;
import de.sudoq.controller.sudoku.ObservableCellInteraction;
import de.sudoq.controller.sudoku.Symbol;
import de.sudoq.model.ModelChangeListener;
import de.sudoq.model.game.Game;
import de.sudoq.model.sudoku.Cell;
import de.sudoq.model.sudoku.Constraint;
import de.sudoq.model.sudoku.ConstraintType;
import de.sudoq.model.sudoku.Position;
import de.sudoq.model.sudoku.Sudoku;

/**
 * This subclass of a View represents a cell in a sudoku. It extends the functionality of the
 * Android View by user interaction and coloring in.
 */
public class SudokuCellView extends View implements ModelChangeListener<Cell>, ObservableCellInteraction {

	/* Attributes */

	private static final String LOG_TAG = SudokuCellView.class.getSimpleName();

	/**
	 * The cell represented by this view
	 * 
	 * @see Cell
	 */
	private Cell cell;

	/**
	 * List of the  selektion listeners
	 */
	private ArrayList<CellInteractionListener> cellSelectListener;

	/**
	 * A flag defining whether note mode is active
	 */
	private boolean noteMode;

	/**
	 * A list of cells, that are semantically connected to this one and should be highlighted if
	 * this cell is selected
	 */
	private ArrayList<SudokuCellView> connectedCells;

	/**
	 * The symbol this cell is filled with
	 */
	private String symbol;

	/**
	 * Indicates if this cell is currently selected
	 */
	private boolean selected;

	/**
	 * Indicates if this cell is connected to the one currently selected
	 */
	private boolean connected;

	/**
	 * Indicates if this cell is part of an extraConstraint
	 */
	private boolean isInExtraConstraint;

	/**
	 * should wrong symbols be highlighted
	 */
	private boolean markWrongSymbol;

	/**
	 * The game associated with this view
	 */
	private Game game;

	/* Constructors */

	/**
	 * Instantiates a SudokuCellView.
	 * 
	 * @param context
	 *            the application context
	 * @param game
	 *            the game this view is a part of
	 * @param cell
	 *            The cell represented by this SudokuCellView
	 * @param markWrongSymbol
	 *            indicates if an incorrect value should be highlighted
	 *            eoll
	 * @throws IllegalArgumentException
	 *             if one of the arguments is null
	 */
	public SudokuCellView(Context context, Game game, Cell cell, boolean markWrongSymbol) {
		super(context);
		this.markWrongSymbol = markWrongSymbol;
		this.cell = cell;
		this.symbol = Symbol.getInstance().getMapping(this.cell.getCurrentValue());
		this.game = game;

		this.cellSelectListener = new ArrayList<CellInteractionListener>();
		this.connectedCells = new ArrayList<SudokuCellView>();
		this.selected = false;
		this.connected = false;
		this.noteMode = false;
		this.isInExtraConstraint = false;

		Iterable<Constraint> constraints = game.getSudoku().getSudokuType();
		for (Constraint c : constraints) {
			if (c.getType().equals(ConstraintType.EXTRA) &&
				c.includes(game.getSudoku().getPosition(cell.getId()))) {
					this.isInExtraConstraint = true;
					break;
			}
		}

		updateMarking();
	}

	/* Methods */

	/**
	 * Draws the content of the associated cell on the canvas of this SudokuCellView.
	 * Sollte den AnimationHandler nutzen um vorab Markierungen/Färbung an dem
	 * Canvas Objekt vorzunehmen.
	 * 
	 * @param canvas
	 *            canvas object on which to draw
	 * @throws IllegalArgumentException
	 *             if canvas is null TODO really??
	 */
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//Log.d(LOG_TAG, "SudokuFieldView.onDraw()");

		this.symbol = Symbol.getInstance().getMapping(this.cell.getCurrentValue());
		CellViewPainter.getInstance().markCell(canvas, this, this.symbol, false, this.isInExtraConstraint && !this.selected);

		// Draw notes if field has no value
		if (this.cell.isNotSolved()) {
			drawNotes(canvas);
		}
	}

	/**
	 * draws notes into the cell
	 * 
	 * @param canvas
	 *            the canvas on which to draw
	 */
	private void drawNotes(Canvas canvas) {
		Paint notePaint = new Paint();
		notePaint.setAntiAlias(true);
		int noteTextSize = getHeight() / Symbol.getInstance().getRasterSize();
		notePaint.setTextSize(noteTextSize);
		notePaint.setTextAlign(Paint.Align.CENTER);
		notePaint.setColor(Color.BLACK);
		for (int i = 0; i < Symbol.getInstance().getNumberOfSymbols(); i++) {
			if (this.cell.isNoteSet(i)) {
				String note = Symbol.getInstance().getMapping(i);
				canvas.drawText(note + "",
						        (i % Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize / 2,
						        (i / Symbol.getInstance().getRasterSize()) * noteTextSize + noteTextSize,
						        notePaint);
			}
		}
	}

	/**
	 * notifyListener(); {@inheritDoc}
	 */
	public void onModelChanged(Cell obj) {
		for (CellInteractionListener listener : cellSelectListener) {
			listener.onCellChanged(this);
		}

		updateMarking();
	}

	/**
	 * Diese Methode verarbeitet alle Touch Inputs, die der Benutzer macht und
	 * leitet sie an den ShowViewListener weiter.
	 * 
	 * @param touchEvent
	 *            Das TouchEvent das von der API kommt und diese Methode
	 *            aufgerufen hat
	 * @return true falls das TouchEvent behandelt wurde, false falls nicht
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls das übergebene MotionEvent null ist
	 */
	@Override
	public boolean onTouchEvent(MotionEvent touchEvent) {
		for (CellInteractionListener listener : cellSelectListener) {
			listener.onCellSelected(this);
		}

		return false;
	}

	
	

	/**
	 * Returns the cell associated by this view
	 * 
	 * @return the cell associated with this view
	 */
	public Cell getCell() {
		return this.cell;
	}

	/**
	 * Setzt den Notizstatus gemäß des Parameters
	 * 
	 * @param state
	 *            true, um den Notizmodus ein-, bzw. false um ihn auszuschalten
	 */
	public void setNoteState(boolean state) {
		this.noteMode = state;
		this.updateMarking();
	}

	/**
	 * Adds the passed SudokuCellView as connected to this cell so that selecting this cell will
	 * highlight it as well. If it is null, nothing will happen.
	 *
	 * @param view
	 *            the View to connect with this one
	 */
	public void addConnectedCell(SudokuCellView view) {
		if (view != null && !this.connectedCells.contains(view)) {
			this.connectedCells.add(view);
		}
	}

	/**
	 * Sets this View as selected.
	 *
	 * @param markConnected
	 *            Determines if cells connected to this cell (row / column) should be highlighted
	 */
	public void select(boolean markConnected) {
		if (this.game.isFinished()) {
			this.connected = true;
		} else {
			this.selected = true;
		}
		if (markConnected) {
			for (SudokuCellView f : this.connectedCells) {
				f.markConnected();
			}
		}

		this.updateMarking();
	}

	/**
	 * Resets highlighting for this and connected cellViews.
	 * 
	 * @param updateConnected
	 *            determines if connected cells should also be reset.
	 */
	public void deselect(boolean updateConnected) {
		this.selected = false;
		this.connected = false;
		if (updateConnected) {
			for (SudokuCellView fv : this.connectedCells) {
				fv.deselect(false);
			}
		}

		this.updateMarking();
	}

	/**
	 * Highlights this cell as connected with the currently selected.
	 */
	public void markConnected() {
		this.connected = true;
		updateMarking();
	}

	/**
	 * Updates the highlighting of this CellView
	 */
	private void updateMarking() {
		boolean editable = this.cell.isEditable();
		//TODO no idea what 'wrong' is doing, i just etracted it for clarity
		boolean wrong    = this.markWrongSymbol && !this.cell.isNotWrong() && checkConstraint();
		CellViewStates state;
		if (this.connected) 
			state = editable ?                  wrong ? CellViewStates.CONNECTED_WRONG
			                                          : CellViewStates.CONNECTED
			    
			                 : CellViewStates.SELECTED_FIXED;
			
		else if (this.selected) 
			state = editable ? this.noteMode ?  wrong ? CellViewStates.SELECTED_NOTE_WRONG
				                                      : CellViewStates.SELECTED_NOTE
				                             :  wrong ? CellViewStates.SELECTED_INPUT_WRONG
				                                      : CellViewStates.SELECTED_INPUT
			                 : CellViewStates.SELECTED_FIXED;
			
		else
			state = editable ?                  wrong ? CellViewStates.DEFAULT_WRONG
			                                          : CellViewStates.DEFAULT
			                 : CellViewStates.FIXED;
		
		CellViewPainter.getInstance().setMarking(this, state);
		invalidate();
	}

	/**
	 * Returns true if the value of this cell violates the Constraints.
	 * Only UniqueConstraints are checked. If the cell is part of another constraint type false is
	 * returned.
	 * 
	 * @return true, if the value of this cell violates the UniqueConstraints or is part of another
	 *         ConstraintType, false otherwise
	 */
	private boolean checkConstraint() {
		Iterable<Constraint> constraints = this.game.getSudoku().getSudokuType();
		Sudoku sudoku = this.game.getSudoku();
		for (Constraint c : constraints) {
			if (c.includes(sudoku.getPosition(this.cell.getId()))) {
				if (c.hasUniqueBehavior()) {
					for (Position pos : c.getPositions()) {
						//if a different position has the same value
						if (pos != sudoku.getPosition(this.cell.getId())
							&& sudoku.getCell(pos).getCurrentValue() == this.cell.getCurrentValue()) {
							return true;
						}
					}
				} else {
					return true;//if no unique-constraint -> automatically satisfied
				}
			}
		}

		return false;
	}

	/**
	 * Returns whether this cell is in note mode
	 * 
	 * @return true iff this cell is in note mode
	 */
	public boolean isNoteMode() {
		return this.noteMode;
	}

	public void registerListener(CellInteractionListener listener) {
		this.cellSelectListener.add(listener);
	}

	public void removeListener(CellInteractionListener listener) {

	}

	/**
	 * Notifies all registered listeners about interaction with this SudokuCellView.
	 */
	public void notifyListener() {
		for (CellInteractionListener listener : cellSelectListener) {
			listener.onCellSelected(this);
		}
	}

}
