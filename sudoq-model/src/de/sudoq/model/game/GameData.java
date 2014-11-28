/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Eine Klasse um die zu einem Spiel zugehörigen Daten zu sammeln ohne jeweils
 * das ganze Spiel laden zu müssen.
 */
public class GameData implements Comparable<GameData> {

	private int id;
	private Date playedAt;
	private boolean finished;
	private SudokuTypes type;
	protected final static String dateFormat = "yyyy:MM:dd HH:mm:ss";
	private Complexity complexity;

	/**
	 * Erzeugt ein GameData Objekt mit den gegebenen Paramtern zurück
	 * 
	 * @param id
	 *            die id des Spiels
	 * @param playedAt
	 *            das Datum als es zuletzt gespielt wurde
	 * @param finished
	 *            ob es beendet ist
	 * @param type
	 *            der Typ des zugehörigen Sudokus
	 * @param complexity
	 *            die Schwierigkeit des zugehörigen Sudokus
	 * @throws IllegalArgumentException
	 *             falls playedAt null ist oder nicht zu einem Datum parsebar
	 *             ist
	 */
	public GameData(int id, String playedAt, boolean finished, SudokuTypes type, Complexity complexity) {
		if (playedAt == null)
			throw new IllegalArgumentException();
		this.type = type;
		this.id = id;
		try {
			this.playedAt = new SimpleDateFormat(dateFormat).parse(playedAt);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		this.finished = finished;
		this.complexity = complexity;

	}

	/**
	 * Gibt die Schwierigkeit des zum Spiel gehörenden Sudokus zurück
	 * 
	 * @return die Schwierigkeit
	 */
	public Complexity getComplexity() {
		return complexity;
	}

	/**
	 * Gibt den Typ des zum Spiel zugehörigen Sudokus zurück
	 * 
	 * @return die Schwierigkeit
	 */
	public SudokuTypes getType() {
		return type;
	}

	/**
	 * Gibt die id des Spiels zurück
	 * 
	 * @return die id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gibt das Datum, an dem das Spiel zuletzt gespielt wurde zurück
	 * 
	 * @return das Datum
	 */
	public Date getPlayedAt() {
		return playedAt;
	}

	/**
	 * Gibt zurück ob das Spiel fertig gespielt wurde
	 * 
	 * @return true falls fertig, false andernfalls
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(GameData another) {
		if (this.finished == another.finished) {
			return playedAt.compareTo(another.playedAt);
		} else {
			return this.finished ? -1 : 1;
		}
	}
}
