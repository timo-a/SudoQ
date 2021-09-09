

/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.utility;

import java.io.File;

import de.sudoq.model.profile.ProfileSingleton;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;

/**
 * Die Klasse FileManager stellt eine Klasse zur Verwaltung des Dateisystems
 * innerhalb der App bereit. Setzt das Singleton-Entwurfsmuster um. ACHTUNG
 * nicht threadsave
 */
public final class FileManager {
    /**
     * Die Ordner für die jeweiligen Daten
     */
    private static File sudokus;

    /**
     * Attributes
     */

    private FileManager() {
    }

    /* Methods */

    /**
     * Erstellt die Singleton-Instanz des FileManagers
     *
     * @param s Ein Ordner fuer die Sudokus
     * @throws IllegalArgumentException falls einer der Ordner null oder nicht schreibbar ist
     */
    public static void initialize(File s) {
        if (s == null || !s.canWrite()) {
            String err = s == null ? "s == null" : "s can't write";
            throw new IllegalArgumentException("invalid directories: " + err);
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
    public static File getGamesFile(ProfileSingleton p) {
        File currentProfile = p.getCurrentProfileDir();
        return new File(currentProfile, "games.xml");
    }

    // Sudokus

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
