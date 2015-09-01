/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile;

/**
 * Dieses enum repräsentiert die verschiedenen Statistiken, die für ein
 * Spielerprofil gespeichert und abgerufen werden können.
 */
public enum Statistics {
	/**
	 * Die Anzahl der bereits beendeten Sudoku-Spiele
	 */
	playedSudokus,

	/**
	 * Die Anzahl der bereits beendeten Sudoku-Spiele mit Schwierigkeitsgrad
	 * einfach
	 */
	playedEasySudokus,

	/**
	 * Die Anzahl der bereits beendeten Sudoku-Spiele mit Schwierigkeitsgrad
	 * mittel
	 */
	playedMediumSudokus,

	/**
	 * Die Anzahl der bereits beendeten Sudoku-Spiele mit Schwierigkeitsgrad
	 * schwer
	 */
	playedDifficultSudokus,

	/**
	 * Die Anzahl der bereits beendeten Sudoku-Spiele mit Schwierigkeitsgrad
	 * höllisch
	 */
	playedInfernalSudokus,

	/**
	 * Die geringste Zeit in der ein Sudoku von diesem Spieler gelöst wurde
	 */
	fastestSolvingTime,

	/**
	 * Die maximale Anzahl an Punkten, die dieser Spieler in einem Spiel
	 * erreicht hat
	 */
	maximumPoints
	
}
