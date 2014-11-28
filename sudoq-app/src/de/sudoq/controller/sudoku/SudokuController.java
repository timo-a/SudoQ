/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.sudoku;

import de.sudoq.model.actionTree.NoteActionFactory;
import de.sudoq.model.actionTree.SolveActionFactory;
import de.sudoq.model.game.Game;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.Statistics;
import de.sudoq.model.sudoku.Field;

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
	public void onNoteAdd(Field field, int value) {
		game.addAndExecute(new NoteActionFactory().createAction(value, field));
	}

	/**
	 * {@inheritDoc}
	 */
	public void onNoteDelete(Field field, int value) {
		game.addAndExecute(new NoteActionFactory().createAction(value, field));
	}

	/**
	 * {@inheritDoc}
	 */
	public void onAddEntry(Field field, int value) {
		game.addAndExecute(new SolveActionFactory().createAction(value, field));
		if (this.game.isFinished()) {
			updateStatistics();
			handleFinish(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void onDeleteEntry(Field field) {
		game.addAndExecute(new SolveActionFactory().createAction(Field.EMPTYVAL, field));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onSolveOne() {
		boolean res = this.game.solveField();
		if (this.game.isFinished()) {
			updateStatistics();
			handleFinish(false);
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onSolveCurrent(Field field) {
		boolean res = this.game.solveField(field);
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
		boolean res = false;

		for (Field f : this.game.getSudoku()) {
			if (!f.isNotWrong()) {
				this.game.addAndExecute(new SolveActionFactory().createAction(Field.EMPTYVAL, f));
			}
		}

		res = game.solveAll();

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
		case infernal:
			Profile.getInstance().setStatistic(Statistics.playedInfernalSudokus, Profile.getInstance().getStatistic(Statistics.playedInfernalSudokus) + 1);
			break;
		case difficult:
			Profile.getInstance().setStatistic(Statistics.playedDifficultSudokus, Profile.getInstance().getStatistic(Statistics.playedDifficultSudokus) + 1);
			break;
		case medium:
			Profile.getInstance().setStatistic(Statistics.playedMediumSudokus, Profile.getInstance().getStatistic(Statistics.playedMediumSudokus) + 1);
			break;
		case easy:
			Profile.getInstance().setStatistic(Statistics.playedEasySudokus, Profile.getInstance().getStatistic(Statistics.playedEasySudokus) + 1);
			break;
		}
		Profile.getInstance().setStatistic(Statistics.playedSudokus, Profile.getInstance().getStatistic(Statistics.playedSudokus) + 1);
		if (Profile.getInstance().getStatistic(Statistics.fastestSolvingTime) > game.getTime()) {
			Profile.getInstance().setStatistic(Statistics.fastestSolvingTime, game.getTime());
		}
		if (Profile.getInstance().getStatistic(Statistics.maximumPoints) < game.getScore()) {
			Profile.getInstance().setStatistic(Statistics.maximumPoints, game.getScore());
		}
	}

}
