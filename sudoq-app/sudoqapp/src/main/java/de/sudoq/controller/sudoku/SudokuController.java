/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import android.content.Context;

import de.sudoq.R;
import de.sudoq.model.actionTree.Action;
import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.Statistics;
import de.sudoq.model.sudoku.Cell;

/**
 * Der SudokuController ist dafür zuständig auf Aktionen des Benutzers mit dem
 * Spielfeld zu reagieren.
 */
public class SudokuController implements AssistanceRequestListener, ActionListener {
	/** Attributes */

	/**
	 * Hält eine Referenz auf das Game, welches Daten über das aktuelle Spiel
	 * enthält
	 */
	private Game game;

	/**
	 * Die SudokuActivity.
	 */
	private SudokuActivity context;

	/** Constructors */

	/**
	 * Erstellt einen neuen SudokuController. Wirft eine
	 * IllegalArgumentException, falls null übergeben wird.
	 * 
	 * @param game
	 *            Game, auf welchem der SudokuController arbeitet
	 * @param context
	 *            der Applikationskontext
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls null übergeben wird
	 */
	public SudokuController(Game game, SudokuActivity context) {
		if (game == null || context == null) {
			throw new IllegalArgumentException("Unvalid param!");
		}
		this.game = game;
		this.context = context;
	}

	/**
	 * Debugging
	 *
	 * @throws IllegalArgumentException
	 *             Wird geworfen, falls null übergeben wird
	 */
	private void getsucc(boolean illegal){
		if (illegal)
			throw new IllegalArgumentException("tu");
	}


	/** Methods */

	/**
	 * {@inheritDoc}
	 */
	public void onRedo() {
		game.redo();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onUndo() {
		game.undo();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNoteAdd(Cell cell, int value) {
		game.addAndExecute(new NoteActionFactory().createAction(value, cell));
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNoteDelete(Cell cell, int value) {
		game.addAndExecute(new NoteActionFactory().createAction(value, cell)); //TODO same code as onNoteAdd why?
	}

	/**
	 * {@inheritDoc}
	 */
	public void onAddEntry(Cell cell, int value) {
		game.addAndExecute(new SolveActionFactory().createAction(value, cell));
		if (this.game.isFinished()) {
			updateStatistics();
			handleFinish(false);
		}
	}

	public void onHintAction(Action a) {
		game.addAndExecute(a);
		if (this.game.isFinished()) {
			updateStatistics();
			handleFinish(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDeleteEntry(Cell cell) {
		game.addAndExecute(new SolveActionFactory().createAction(Cell.EMPTYVAL, cell));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onSolveOne() {
		boolean res = this.game.solveCell();
		if (this.game.isFinished()) {
			updateStatistics();
			handleFinish(false);
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onSolveCurrent(Cell cell) {
		boolean res = this.game.solveCell(cell);
		if (this.game.isFinished()) {
			updateStatistics();
			handleFinish(false);
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onSolveAll() {

		for (Cell f : this.game.getSudoku()) {
			if (!f.isNotWrong()) {
				this.game.addAndExecute(new SolveActionFactory().createAction(Cell.EMPTYVAL, f));
			}
		}

		boolean res = game.solveAll();

		if (res)
			handleFinish(true);
		return res;
	}

	/**
	 * Zeigt einen Gewinndialog an, der fragt, ob das Spiel beendet werden soll.
	 * 
	 * @param surrendered
	 *            TODO
	 */
	private void handleFinish(boolean surrendered) {
		context.setFinished(true, surrendered);
	}

	/**
	 * Updatet die Spielerstatistik des aktuellen Profils in der App.
	 */
	private void updateStatistics() {
		switch (game.getSudoku().getComplexity()) {
			case infernal:  incrementStatistic(Statistics.playedInfernalSudokus);  break;
			case difficult: incrementStatistic(Statistics.playedDifficultSudokus); break;
			case medium:    incrementStatistic(Statistics.playedMediumSudokus);    break;
			case easy:      incrementStatistic(Statistics.playedEasySudokus);      break;
		}
		incrementStatistic(Statistics.playedSudokus);
		Profile p = Profile.Companion.getInstance(context.getDir(context.getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));

		if (p.getStatistic(Statistics.fastestSolvingTime) > game.getTime()) {
			p.setStatistic(Statistics.fastestSolvingTime, game.getTime());
		}
		if (p.getStatistic(Statistics.maximumPoints) < game.getScore()) {
			p.setStatistic(Statistics.maximumPoints, game.getScore());
		}
	}

	private void incrementStatistic(Statistics s){ //TODO this should probably be in model...
		Profile p = Profile.Companion.getInstance(context.getDir(context.getString(R.string.path_rel_profiles), Context.MODE_PRIVATE));
		p.setStatistic(s,  p.getStatistic(s) + 1);
	}

}
