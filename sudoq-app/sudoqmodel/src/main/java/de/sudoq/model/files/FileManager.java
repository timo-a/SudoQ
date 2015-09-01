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
	private static File profiles;
	private static File sudokus;

	private static int currentProfileId = -1;

	/** Methods */

	/**
	 * Erstellt die Singleton-Instanz des FileManagers
	 * 
	 * @param p
	 *            Ein Ordner fuer die Profile
	 * @param s
	 *            Ein Ordner fuer die Sudokus
	 * @throws IllegalArgumentException
	 *             falls einer der Ordner null oder nicht schreibbar ist
	 */
	public static void initialize(File p, File s) {
		if (p == null || s == null || !p.canWrite() || !s.canWrite()) {
			String err ="";
			if     (p==null)
				            err += " p==null";
			else if(s==null)
				            err += " s==null";
			else if(!p.canWrite())
				            err += " p can't write";
			else
			                err += " s can't write";

			throw new IllegalArgumentException("unvalid directories:"+err);
		}
		profiles = p;
		sudokus = s;

		//initSudokuDirectories();
	}

	/**
	 * Setzt die id des aktuellen Profils für den FileManager, sodass die Pfade
	 * zur Verfügung stehen
	 * 
	 * @param id
	 */
	public static void setCurrentProfile(int id) {
		currentProfileId = id;
	}

	/**
	 * Löscht rekursiv das gegebene Verzeichnis
	 * 
	 * @param f
	 *            das Verzeichnis
	 * @throws IOException
	 *             falls etwas nicht gelöscht werden konnte
	 */
	public static void deleteDir(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles())
				deleteDir(c);
		}
		if (!f.delete())
			throw new FileNotFoundException("Failed to delete file: " + f);
	}

	// Profiles

	/**
	 * Gibt die Anzahl der aktuell existierenden Profile zurück (macht keine
	 * Rechenfehler :-)
	 * 
	 * @return die Anzahl der Profile
	 */
	public static int getNumberOfProfiles() {
		int count = profiles.list().length;
		if (getProfilesFile().exists()) {
			count--;
		}
		return count;
	}

	/**
	 * Gibt das Verzeichnis des aktuellen Profils zurueck
	 * 
	 * @return File, welcher auf das aktuelle Profilverzeichnis zeigt
	 */
	private static File getCurrentProfileDir() {
		return getProfileDirFor(currentProfileId);
	}

	/**
	 * Gibt das Verzeichnis des Profils mit der gegebenen id zurueck
	 * 
	 * @return File, welcher auf das Profilverzeichnis zeigt
	 */
	private static File getProfileDirFor(int id) {
		return new File(profiles.getAbsolutePath() + File.separator + "profile_" + id);
	}

	/**
	 * Gibt die XML-Datei das aktuellen Profils zurück
	 * 
	 * @param id
	 *            die id des Profils dessen xml gesucht ist
	 * 
	 * @return File, welcher auf die XML Datei des aktuellen Profils zeigt
	 */
	public static File getProfileXmlFor(int id) {
		return new File(getProfileDirFor(id), "profile.xml");
	}

	/**
	 * Gibt das Verzeichis zurück, in denen die Profil-Ornder liegen
	 * 
	 * @return File, welcher auf das Verzeichnis mit den Profilordnern zeigt
	 */
	public static File getProfilesDir() {
		return new File(profiles.getAbsolutePath());
	}
	
	/**
	 * Gibt die Date zurück, in der die Gesten des Benutzers gespeichert werden
	 * 
	 * @return File, welcher auf die Gesten-Datei des Benutzers zeigt
	 */
	public static File getCurrentGestureFile() {
		return new File(getCurrentProfileDir(), "gestures");
	}

	/**
	 * Erstellt die Ordnerstruktur und nötige Dateien für das Profil mit der
	 * übergebenen ID
	 * 
	 * @param id
	 *            ID des Profils
	 */
	public static void createProfileFiles(int id) {
		new File(profiles.getAbsolutePath() + File.separator + "profile_" + id).mkdir();
		new File(profiles.getAbsolutePath() + File.separator + "profile_" + id + File.separator + "games").mkdir();
		File games = new File(profiles.getAbsolutePath() + File.separator + "profile_" + id + File.separator + "games.xml");
		try {
			new XmlHelper().saveXml(new XmlTree("games"), games);
		} catch (IOException e) {
			throw new IllegalStateException("Unvalid Profil", e);
		}
	}

	/**
	 * Erzeugt die profiles.xml Datei, wenn noch kein Profil vorhanden ist.
	 */
	public static void createProfilesFile() {
		File profilesXML = new File(profiles.getAbsolutePath() + File.separator + "profiles.xml");
		try {
			new XmlHelper().saveXml(new XmlTree("profiles"), profilesXML);
		} catch (IOException e) {
			throw new IllegalStateException("Couldnt create profiles.xml", e);
		}
	}

	/**
	 * Gibt die Profilliste Datei zurueck
	 * 
	 * @return das File welches auf die Profilliste zeigt
	 */
	public static File getProfilesFile() {
		return new File(getProfilesDir(), "profiles.xml");
	}

	/**
	 * Löscht das Profil der gegebenen ID und alle seine Daten Die Profil Liste
	 * bleibt unverändert! ACHTUNG: Überprüft nicht ob es noch andere Profile
	 * gibt. Setzt die currentProfileId nicht!
	 * 
	 * @param id
	 *            die id des zu löschenden Profils
	 */
	public static void deleteProfile(int id) {
		try {
			File dir = getProfileDirFor(id);
			if (dir.exists()) {
				deleteDir(dir);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to delete given Profile");
		}
	}

	// Games

	/**
	 * Gibt die Spieleliste Datei des aktuellen Profils zurück
	 * 
	 * @return das File welches auf die Spieleliste zeigt
	 */
	public static File getGamesFile() {
		return new File(getCurrentProfileDir(), "games.xml");
	}

	/**
	 * Gibt das Game-Verzeichnis des aktuellen Profils zurueck
	 * 
	 * @return File, welcher auf das Game-Verzeichnis des aktuellen Profils
	 *         zeigt
	 */
	public static File getGamesDir() {
		return new File(getCurrentProfileDir(), "games");
	}

	/**
	 * Gibt die XML eines Games des aktuellen Profils anhand seiner ID zurueck
	 * 
	 * @param id
	 *            ID des Games
	 * @return File, welcher auf die XML Datei des Games zeigt
	 */
	public static File getGameFile(int id) {
		return new File(getGamesDir(), "game_" + id + ".xml");
	}

	/**
	 * Loescht falls existierend das Spiel mit der gegebenen id des aktuellen
	 * Profils
	 * 
	 * @param id
	 *            die id des zu loeschenden Spiels
	 * @return ob es geloescht wurde.
	 */
	public static boolean deleteGame(int id) {
		boolean game = getGameFile(id).delete();
		return game && getGameThumbnailFile(id).delete();
	}

	/**
	 * Gibt die naechste verfuegbare ID fuer ein Game zurueck
	 * 
	 * @return naechste verfuegbare ID
	 */
	public static int getNextFreeGameId() {
		return getGamesDir().list().length + 1;
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
	public static File getGameThumbnailFile(int gameID) {
		return new File(getGamesDir() + File.separator + "game_" +
				Integer.toString(gameID) + ".png");
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
		ArrayList<Integer> numbers = new ArrayList<Integer>();
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
