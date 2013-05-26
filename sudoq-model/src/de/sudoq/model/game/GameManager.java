/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Haiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.game;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.sudoq.model.files.FileManager;
import de.sudoq.model.profile.Profile;
import de.sudoq.model.sudoku.Sudoku;
import de.sudoq.model.sudoku.SudokuManager;
import de.sudoq.model.sudoku.complexity.Complexity;
import de.sudoq.model.sudoku.sudokuTypes.SudokuTypes;
import de.sudoq.model.xml.GameXmlHandler;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlHandler;
import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;

/**
 * Diese Klasse dient dem Erstellen und Laden von Sudokuspielen. Sie setzt das
 * Singleton Pattern um.
 */
public class GameManager {
	/** Attributes */

	private static final String ID = "id";
	private static final String FINISHED = "finished";
	private static final String PLAYED_AT = "played_at";
	private static final String SUDOKU_TYPE = "sudoku_type";
	private static final String COMPLEXITY = "complexity";

	/**
	 * Die einzige GameManager Instanz
	 */
	private static GameManager manager = new GameManager();

	private XmlHandler<Game> xmlHandler;

	/** Constructors */

	/**
	 * Instanziiert ein neues GameManager Objekt. Das kann nur innerhalb dieser
	 * Klasse passieren.
	 */
	private GameManager() {
		xmlHandler = new GameXmlHandler();
	};

	/** Methods */

	/**
	 * Gibt die einzige GameManager Instanz zurück.
	 * 
	 * @return Der GameManager
	 */
	public static GameManager getInstance() {
		return manager;
	}

	/**
	 * Diese Methode erzeugt ein neues Spiel des gewünschten Typs und legt die
	 * nötigen Dateien an.
	 * 
	 * @param type
	 *            Der Typ des Sudokus
	 * @param complexity
	 *            Die Schwierigkeit des Spiels
	 * @param gameType
	 *            Der Typ des Spiels
	 * @param assists
	 *            Die für dieses Game zu setzen Assistances
	 * @return Das neue Spiel
	 * 
	 * @throws IllegalArgumentException
	 *             falls gameType invalid ist.
	 * @see SudokuTypes
	 * @see Complexity
	 * @see GameType
	 * @see Game
	 */
	public Game newGame(SudokuTypes type, Complexity complexity, GameType gameType, AssistanceSet assists) {
		Sudoku sudoku = SudokuManager.getNewSudoku(type, complexity);

		new SudokuManager().usedSudoku(sudoku);//TODO warum instanziierung, wenn laut doc singleton?

		Game game = new Game(FileManager.getNextFreeGameId(), sudoku);
		game.setAssistances(assists);
		xmlHandler.saveAsXml(game);

		XmlTree games = getGamesXml();
		XmlTree gameTree = new XmlTree("game");
		gameTree.addAttribute(new XmlAttribute(ID, Integer.toString(game.getId())));
		gameTree.addAttribute(new XmlAttribute(SUDOKU_TYPE, Integer.toString(game.getSudoku().getSudokuType().getEnumType().ordinal())));
		gameTree.addAttribute(new XmlAttribute(COMPLEXITY, Integer.toString(game.getSudoku().getComplexity().ordinal())));
		gameTree.addAttribute(new XmlAttribute(PLAYED_AT, new SimpleDateFormat(GameData.dateFormat).format(new Date())));

		games.addChild(gameTree);
		saveGamesFile(games);

		return game;
	}

	/**
	 * Lädt ein bereits begonnenes Spiel des aktuellen Spielers mit der
	 * gegebenen ID. Ist diese ungültig, so wird eine IllegalArgumentException
	 * geworfen.
	 * 
	 * @param id
	 *            die Id des zu ladenden Spiels
	 * @return Das geladene Spiel, null falls kein Spiel zur angegebenen id
	 *         existiert
	 * @throws IllegalArgumentException
	 *             falls keine Spiel mit der entsprechenden id existiert
	 */
	public Game load(int id) {
		if (id <= 0) throw new IllegalArgumentException("unvalid id");
		Game game = new Game();
		// throws IllegalArgumentException
		new GameXmlHandler(id).createObjectFromXml(game);
		return game;
	}

	/**
	 * Gibt die nach Zeit und ob beendet oder nicht sortierte Liste aller Spiele
	 * des aktuellen Spielers zurück
	 * 
	 * @return die Liste
	 */
	public List<GameData> getGameList() {
		List<GameData> list = new ArrayList<GameData>();
		for (XmlTree game : getGamesXml()) {
			list.add(new GameData(Integer.parseInt(game.getAttributeValue(ID)),
					game.getAttributeValue(PLAYED_AT), Boolean.parseBoolean(game.getAttributeValue(FINISHED)),
					SudokuTypes.values()[Integer.parseInt(game.getAttributeValue(SUDOKU_TYPE))],
					Complexity.values()[Integer.parseInt(game.getAttributeValue(COMPLEXITY))]));

		}
		java.util.Collections.sort(list);
		java.util.Collections.reverse(list);
		return list;
	}

	/**
	 * Speichert ein gegebenes Game in XML.
	 * 
	 * @param game
	 *            Das zu speichernde Game.
	 */
	public void save(Game game) {
		xmlHandler.saveAsXml(game);
		XmlTree games = getGamesXml();
		// boolean passed = false;
		for (XmlTree g : games) {
			if (Integer.parseInt(g.getAttributeValue(ID)) == game.getId()) {
				// TODO anpassen
				g.updateAttribute(new XmlAttribute(PLAYED_AT, new SimpleDateFormat(GameData.dateFormat).format(new Date())));
				g.updateAttribute(new XmlAttribute(FINISHED, Boolean.toString(game.isFinished())));
				// passed = true;
				break;
			}
		}
		// assert passed : "Broken games";
		Profile.getInstance().saveChanges();
		saveGamesFile(games);
	}

	/**
	 * Loescht nicht mehr existierende Spiele aus der Games Liste Existieren
	 * neue Spiele, die nicht in der Liste sind muss die Liste inkonsistent
	 * sein.
	 * 
	 * @throws IllegalStateException
	 *             Falls die Liste inkonsistent ist
	 */
	public void updateGamesList() {
		XmlTree games = getGamesXml();
		XmlTree newGames = new XmlTree(games.getName());
		// int counter = 0;
		for (XmlTree g : games) {
			if (FileManager.getGameFile(Integer.parseInt(g.getAttributeValue(ID))).exists()) {
				newGames.addChild(g);
				// counter++;
			}
		}
		// assert counter == FileManager.getGamesDir().list().length :
		// "Inconsistent games list";
		saveGamesFile(newGames);
	}

	/**
	 * Loescht das Spiel mit der gegebenen Id aus dem Speicher und aus der
	 * Liste. Existiert kein Spiel mit der Id passiert nichts
	 * 
	 * @param id
	 *            die id des zu loeschenden Spiels
	 */
	public void deleteGame(int id) {
		if (id == Profile.getInstance().getCurrentGame()) {
			Profile.getInstance().setCurrentGame(Profile.NO_GAME);
		}
		FileManager.deleteGame(id);
		updateGamesList();
	}

	/**
	 * Loescht alle fertigen Spiele aus dem Speicher und aus der Spieleliste
	 */
	public void deleteFinishedGames() {
		XmlTree games = getGamesXml();
		for (XmlTree g : games) {
			if (Boolean.parseBoolean(g.getAttributeValue(FINISHED))) {
				FileManager.deleteGame(Integer.parseInt(g.getAttributeValue(ID)));
			}
		}
		updateGamesList();
	}

	private void saveGamesFile(XmlTree games) {
		try {
			new XmlHelper().saveXml(games, FileManager.getGamesFile());
		} catch (IOException e) {
			throw new IllegalStateException("Profil broken", e);
		}
	}

	private XmlTree getGamesXml() {
		try {
			return new XmlHelper().loadXml(FileManager.getGamesFile());
		} catch (IOException e) {
			throw new IllegalStateException("Profil broken", e);
		}
	}
}
