

/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.sudoq.model.profile.Profile;
import de.sudoq.model.profile.ProfileManager;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;

/**
 * Die Klasse FileManager stellt eine Klasse zur Verwaltung des Dateisystems
 * innerhalb der App bereit. Setzt das Singleton-Entwurfsmuster um. ACHTUNG
 * nicht threadsave
 */
public final class FileManager {
	/** Attributes */

	private FileManager() {
	}

	/**
	 * Die Ordner für die jeweiligen Daten
	 */
	private static File sudokus;

	/* Methods */

	/**
	 * Erstellt die Singleton-Instanz des FileManagers
	 * 
	 * @param s
	 *            Ein Ordner fuer die Sudokus
	 * @throws IllegalArgumentException
	 *             falls einer der Ordner null oder nicht schreibbar ist
	 */
	public static void initialize(File s) {
		if (s == null || !s.canWrite()) {
			String err ="";
			if(s==null)
				   err += " s==null";
			else
					err += " s can't write";

			throw new IllegalArgumentException("invalid directories:"+err);
		}
		sudokus = s;

		//initSudokuDirectories();
	}


	// Profiles todo move all to profileManager








	// Games

	/**
	 * Gibt die Spieleliste Datei des aktuellen Profils zurück
	 * 
	 * @return das File welches auf die Spieleliste zeigt
	 */
	public static File getGamesFile(Profile p) {
		File currentProfile = p.getCurrentProfileDir();
		return new File(currentProfile, "games.xml");
	}

	/**
	 * Gibt das Game-Verzeichnis des aktuellen Profils zurueck
	 * 
	 * @return File, welcher auf das Game-Verzeichnis des aktuellen Profils
	 *         zeigt
	 */
	public static File getGamesDir(ProfileManager p) {
		File currentProfile = p.getCurrentProfileDir();
		File games = new File(currentProfile, "games");
		return games;
	}

	/**
	 * Gibt die XML eines Games des aktuellen Profils anhand seiner ID zurueck
	 * 
	 * @param id
	 *            ID des Games
	 * @return File, welcher auf die XML Datei des Games zeigt
	 */
	public static File getGameFile(int id, Profile p) {
		return new File(getGamesDir(p), "game_" + id + ".xml");
	}

	/**
	 * Loescht falls existierend das Spiel mit der gegebenen id des aktuellen
	 * Profils
	 * 
	 * @param id
	 *            die id des zu loeschenden Spiels
	 * @return ob es geloescht wurde.
	 */
	public static boolean deleteGame(int id, Profile p) {
		boolean game = getGameFile(id, p).delete();
		return game && getGameThumbnailFile(id, p).delete();
	}

	/**
	 * Gibt die naechste verfuegbare ID fuer ein Game zurueck
	 * 
	 * @return naechste verfuegbare ID
	 */
	public static int getNextFreeGameId(Profile p) {
		File gamesDir = getGamesDir(p);
		return gamesDir.list().length + 1;
	}

	// Thumbnails

	/**
	 * Returns the .png File for thumbnail of the game with id gameID
	 * 
	 * @param gameID
	 *            The ID of the game whos thumbnail is requested.
	 * 
	 * @return The thumbnail File.
	 */
	public static File getGameThumbnailFile(int gameID, ProfileManager p) {
		return new File(getGamesDir(p) + File.separator + "game_" +
                gameID + ".png");
	}

	// Sudokus

	/**
	 * Gibt das Verzeichnis der Sudokus zurueck
	 * 
	 * @return File, welcher auf das Verzeichnis mit den Sudokus zeigt
	 */
	public static File getSudokuDir() {
		return sudokus;
	}

	
	/**
	 * Gibt die Anzahl der Sudokus des gesuchten Typs zurueck
	 * 
	 * @param t
	 *            der gesuchte SudokuTyp
	 * @param c
	 *            die gesuchte Sudoku Schwierigkeit
	 * @return die Anzahl
	 */
	public static int getSudokuCountOf(SudokuTypes t, Complexity c) {
		return getSudokuDir(t, c).list().length;
	}

	/**
	 * Gibt ein freies File fuer das gegebene Sudokus zurueck
	 * 
	 * @param sudoku
	 *            das zu speichernde Sudoku
	 * @return File, welcher auf die Datei des Sudokus zeigt
	 */
	public static File getNewSudokuFile(Sudoku sudoku) {
		return new File(getSudokuDir(sudoku).getAbsolutePath() + File.separator + "sudoku_" + getFreeSudokuIdFor(sudoku) + ".xml");
	}

	/**
	 * Loescht das uebergebene Sudoku von der Platte
	 * 
	 * @param sudoku
	 *            das zu loeschnde Sudoku
	 */
	public static void deleteSudoku(Sudoku sudoku) {
		if (!getSudokuFile(sudoku).delete()) {
			throw new IllegalArgumentException("Sudoku doesn't exist");
		}
	}

	/**
	 * Gibt eine Referenz auf ein zufaelliges zu den Parametern passendem Sudoku
	 * zurueck und null falls keins existiert
	 * 
	 * @param type
	 *            der Typ des Sudokus
	 * @param complexity
	 *            die Schwierigkeit des Sudokus
	 * @return die Referenz auf die Datei
	 */
	public static File getRandomSudoku(SudokuTypes type, Complexity complexity) {
		File dir = getSudokuDir(type, complexity);
		if (dir.list().length > 0) {
			String fileName = dir.list()[new Random().nextInt(dir.list().length)];
			return new File(dir.getAbsolutePath() + File.separator + fileName);
		} else {
			return null;
		}
	}


	/**
	 * Gibt den die Sudokus mit den gegebenen Parametern enthaltennden Ordner
	 * zurueck
	 * 
	 * @param type
	 *            der Typ des Sudokus
	 * @param complexity
	 *            die Schwierigkeit des Sudokus
	 * @return der Ordner
	 */
	private static File getSudokuDir(SudokuTypes type, Complexity complexity) {
		return new File(sudokus.getAbsolutePath() + File.separator + type.toString() + File.separator + complexity.toString());
	}
	
	/**
	 * Gibt den zum Sudoku passenden Ordner zurueck
	 * 
	 * @param s
	 *            das einzuordnende Sudoku
	 * @return den Ordner
	 */
	private static File getSudokuDir(Sudoku s) {
		return getSudokuDir(s.getSudokuType().getEnumType(), s.getComplexity());
	}

	/**
	 * Gibt die zum gegebenen Sudoku gehoerende Datei zurueck
	 * 
	 * @param s
	 *            das Sudoku
	 * @return das File
	 */
	public static File getSudokuFile(Sudoku s) {
		return new File(getSudokuDir(s).getAbsolutePath(), "sudoku_" + s.getId() + ".xml");
	}

	 /**
	 * Gibt die Sudoku-Typdatei für den spezifizierten Typ zurück.
	 * @param type die Typ-Id
	 * @return die entsprechende Sudoku-Typdatei
	 */
	public static File getSudokuTypeFile(SudokuTypes type) {
		String ap = sudokus.getAbsolutePath();
		return new File(ap + File.separator + type.toString() + File.separator + type.toString() +".xml");
	}


	/**
	 * Gibt die nächste verfügbare Sudoku ID zurück
	 * 
	 * @return nächste verfügbare Sudoku ID
	 */
	private static int getFreeSudokuIdFor(Sudoku sudoku) {
		ArrayList<Integer> numbers = new ArrayList<>();
		for (String s : getSudokuDir(sudoku).list()) {
			numbers.add(Integer.parseInt(s.substring(7, s.length() - 4)));
		}
		int i = 1;
		while (numbers.contains(i)) {
			i++;
		}
		return i;
	}

	/**
	 * Erzeugt falls noetig alle Sudoku Ordner fuer die Typen und
	 * Schwierigkeiten
	 */
	private static void initSudokuDirectories() {
		for (SudokuTypes t : SudokuTypes.values()) {
			File typeDir = new File(sudokus.getAbsoluteFile() + File.separator + t.toString());
			if (!typeDir.exists()) typeDir.mkdir();

			for (Complexity c : Complexity.values()) {
				new File(typeDir.getAbsolutePath() + File.separator + c.toString()).mkdir();
			}
		}
	}
}
