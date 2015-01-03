/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import de.sudoq.model.ObservableModelImpl;
import de.sudoq.model.files.FileManager;
import de.sudoq.model.game.GameSettings;
import de.sudoq.model.game.Assistances;
import de.sudoq.model.sudoku.SudokuManager;
import de.sudoq.model.xml.ProfileXmlHandler;
import de.sudoq.model.xml.XmlAttribute;
import de.sudoq.model.xml.XmlHandler;
import de.sudoq.model.xml.XmlHelper;
import de.sudoq.model.xml.XmlTree;
import de.sudoq.model.xml.Xmlable;

/**
 * Diese statische Klasse ist ein Wrapper für das aktuell geladene und durch
 * SharedPreferences von der Android-API verwaltete Spielerprofil.
 * 
 */
public class Profile extends ObservableModelImpl<Profile> implements Xmlable {
	/** Attributes */

	public  static final int    INITIAL_TIME_RECORD = 5999;

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String CURRENT = "current";

	/**
	 * Konstante die signalisiert, dass es kein aktuelles Spiel gibt
	 */
	public static final int NO_GAME = -1;

	/**
	 * Konstante die signalisiert, dass ein neues Profil noch keinen namen hat
	 * */
	public static final String DEFAULT_PROFILE_NAME = "unnamed";

	private static Profile instance;
	/**
	 * Name des Profils
	 */
	private String name;

	/**
	 * ID des Profils
	 */
	private int profileId = -1;

	/**
	 * ID des aktuellen Games
	 */
	private int currentGameId;
	
	/**
	 * AssistanceSet mit den Hilfestellungen
	 */
	private GameSettings gameSettings = new GameSettings();

	int[] statistics;

	private XmlHandler<Profile> xmlHandler = new ProfileXmlHandler();

	/** Constructors */

	/**
	 * Da die Klasse statisch ist hat sie einen privaten Konstruktor.
	 */
	private Profile() {
	}

	/**
	 * Diese Methode gibt eine Instance dieser Klasse zurück, wird sie erneut
	 * aufgerufen, so wird dieselbe Instanz zurückgegeben.
	 * 
	 * @return Die Instanz dieses Profile Singletons
	 */
	public static synchronized Profile getInstance() {
		if (instance == null) {
			instance = new Profile();
			instance.loadCurrentProfile();
		}

		return instance;
	}

	/** Methods */

	/**
	 * Laed das aktuelle Profil aus der profiles.xml
	 */
	public void loadCurrentProfile() {
		if (!FileManager.getProfilesFile().exists()) {
			FileManager.createProfilesFile();
			createProfile();
			return;
		}

		XmlTree profiles = getProfilesXml();
		this.profileId = Integer.parseInt(profiles.getAttributeValue(CURRENT));
		xmlHandler.createObjectFromXml(this);
		FileManager.setCurrentProfile(profileId);
		notifyListeners(this);
	}

	/**
	 * Loescht das aktuelle Profil, falls mehr als 1 Profil vorhanden ist. Nimmt
	 * als neues Profil das erste nicht geloeschte in der profiles.xml
	 */
	public void deleteProfile() {
		if (getNumberOfAvailableProfiles() > 1) {
			XmlTree oldProfiles = getProfilesXml();
			XmlTree profiles = new XmlTree(oldProfiles.getName());

			for (XmlTree profile : oldProfiles) {
				if (Integer.valueOf((profile.getAttributeValue(ID))) != this.profileId) {
					profiles.addChild(profile);
				}
			}
			saveProfilesFile(profiles);
			FileManager.deleteProfile(this.profileId);

			this.setProfile(Integer.parseInt(profiles.getChildren().next().getAttributeValue(ID)));
		}
	}

	/**
	 * Gibt die Anzahl der nicht geloeschten Profile zurueck
	 * 
	 * @return Anzahl nicht geloeschter Profile
	 */
	public int getNumberOfAvailableProfiles() {
		return getProfilesXml().getNumberOfChildren();
	}

	/**
	 * Diese Methode ändert die Einstellungen, die mit dieser Klasse abgerufen
	 * werden können auf die spezifizierten SharedPreferences. Sind diese null,
	 * so wird nichts geändert und es wird false zurückgegeben. War die Änderung
	 * erfolgreich, so wird true zurückgegeben.
	 * 
	 * @param profileID
	 *            Die ID des Profils, zu dem gewechselt werden soll.
	 * 
	 * @return boolean true, falls die Änderung erfolgreich war, false
	 *         andernfalls
	 */
	public boolean changeProfile(int profileID) {
		int oldProfileID = getCurrentProfileID();
		if (profileID == oldProfileID)
			return true;
		xmlHandler.saveAsXml(this); // save current
		return setProfile(profileID);
	}

	/**
	 * Setzt das neue Profil ohne das alte zu speichern (zB nach löschen)
	 */
	private boolean setProfile(int profileID) {
		this.profileId = profileID;
		xmlHandler.createObjectFromXml(this); // load new values

		// set current profile in profiles.xml
		XmlTree profiles = getProfilesXml();
		profiles.updateAttribute(new XmlAttribute(CURRENT, Integer.toString(profileID)));
		saveProfilesFile(profiles);
		FileManager.setCurrentProfile(profileID);
		notifyListeners(this);

		return false;

	}

	/**
	 * Wird von der PlayerPreference aufgerufen, falls sie verlassen wird und
	 * speichert Aenderungen an der profile.xml fuer dieses Profil sowie an der
	 * profiles.xml, welche Informationen ueber alle Profile enthaelt
	 */
	public void saveChanges() {
		xmlHandler.saveAsXml(this);
		XmlTree profiles = getProfilesXml();
		for (XmlTree profile : profiles) {
			if (Integer.valueOf(profile.getAttributeValue(ID)) == this.getCurrentProfileID()) {
				profile.updateAttribute(new XmlAttribute(NAME, this.getName()));
			}
		}
		saveProfilesFile(profiles);
	}

	/**
	 * Diese Methode erstellt ein neues Profil.
	 */
	public void createProfile() {
		int newProfileID = getNewProfileID();
		if (getCurrentProfileID() != -1) {
			xmlHandler.saveAsXml(this); // save current profile xml
		}
		this.profileId = newProfileID;
		this.setDefaultValues();
		FileManager.createProfileFiles(newProfileID);

		// add into profiles.xml and save it
		XmlTree profiles = getProfilesXml();
		XmlTree profileTree = new XmlTree("profile");
		profileTree.addAttribute(new XmlAttribute(ID, Integer.toString(newProfileID)));
		profileTree.addAttribute(new XmlAttribute(NAME, getName()));
		profiles.addChild(profileTree);
		profiles.updateAttribute(new XmlAttribute(CURRENT, Integer.toString(newProfileID)));
		saveProfilesFile(profiles);

		// save new profile xml
		xmlHandler.saveAsXml(this);
		FileManager.setCurrentProfile(newProfileID);
		notifyListeners(this);
	}

	private void setDefaultValues() {
		this.name = DEFAULT_PROFILE_NAME;
		this.currentGameId = -1;
		this.gameSettings = new GameSettings();
		this.gameSettings.setAssistance(Assistances.markRowColumn);
//		this.gameSettings.setGestures(false);
		this.statistics = new int[Statistics.values().length];
		this.statistics[Statistics.fastestSolvingTime.ordinal()] = INITIAL_TIME_RECORD;
		notifyListeners(this);
	}

	/**
	 * Gibt eine neue Profil ID zurück. Bestehende Profile dürfen nicht gelöscht
	 * werden, sonder als solches markiert werden.
	 * 
	 * @return
	 */
	public int getNewProfileID() {
		ArrayList<Integer> used = new ArrayList<Integer>();
		for (XmlTree profile : getProfilesXml()) {
			used.add(Integer.parseInt(profile.getAttributeValue(ID)));
		}
		int i = 1;
		while (used.contains(i)) {
			i++;
		}
		return i;
	}

	/**
	 * Diese Methode gibt die ID des aktuelen Profils zurück.
	 * 
	 * @return Die ID des aktuellen Profils.
	 */
	public int getCurrentProfileID() {
		return this.profileId;
	}

	/**
	 * Diese Methode gibt die ID des aktuellen Spiels zurück, wenn es existiert,
	 * andernfalls -1.
	 * 
	 * @return Die ID des aktuellen Spiels. -1, wenn es kein aktuelles Spiel
	 *         gibt.
	 */
	public int getCurrentGame() {
		return this.currentGameId;
	}

	/**
	 * Diese Methode setzt das aktuelle Spiel auf die gegebene id
	 * 
	 * @param id
	 *            die id des nun aktuellen Spiels
	 */
	public void setCurrentGame(int id) {
		this.currentGameId = id;
	}

	/**
	 * Diese Methode gibt den Namen des aktuellen Spielerprofils zurück.
	 * 
	 * @return Der Name des aktuellen Spielers
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Setzt den Namen des aktuellen Spielerprofils
	 * 
	 * @param name
	 *            Der Name, der gesetzt werden soll
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt ein AssistanceSet, welches die zurzeit im Profil aktivierten bzw.
	 * deaktivierten Hilfestellungen repräsentiert. Es kann durch die Methoden
	 * der {@link Assistances}-Klasse ausgelesen werden.
	 * 
	 * @return Ein AssistanceSet, welches die im Profil aktivierten
	 *         Hilfestellungen repräsentiert
	 */
	public GameSettings getAssistances() {
		return this.gameSettings;
	}

	/**
	 * Diese Methode gibt zurück, ob die Gesteneingabe im aktuellen
	 * Spielerprofil aktiv ist oder nicht.
	 * 
	 * @return true, falls die Gesteneingabe im aktuellen Profil aktiv ist,
	 *         false andernfalls
	 */
	public boolean isGestureActive() {
		return this.gameSettings.isGesturesSet();
	}

	/*Advanced Settings*/
	
	/**
	 * Setzt die Verwendung von Gesten in den Preferences auf den übergebenen
	 * Wert.
	 * 
	 * @param value
	 *            true, falls Gesten gesetzt werden sollen, false falls nicht
	 */
	public void setGestureActive(boolean value) {
		this.gameSettings.setGestures(value);
	}
	
	public void setLefthandActive(boolean value) {
		this.gameSettings.setLefthandMode(value);
	}
	
	public void setHelperActive(boolean value) {
		this.gameSettings.setHelper(value);
	}
	
	/**
	 * Gibt eine String Liste mit allen Profilnamen zurück.
	 * 
	 * @return die Namensliste
	 */
	public ArrayList<String> getProfilesNameList() {
		ArrayList<String> profilesList = new ArrayList<String>();
		XmlTree profiles = getProfilesXml();
		for (XmlTree profile : profiles) {
			profilesList.add(profile.getAttributeValue(NAME));
		}
		return profilesList;
	}

	/**
	 * Gibt eine Integer Liste mit allen Profilids zurück.
	 * 
	 * @return die Idliste
	 */
	public ArrayList<Integer> getProfilesIdList() {
		ArrayList<Integer> profilesList = new ArrayList<Integer>();
		XmlTree profiles = getProfilesXml();
		for (XmlTree profile : profiles) {
			profilesList.add(Integer.valueOf(profile.getAttributeValue(ID)));
		}
		return profilesList;
	}

	/**
	 * Setzt eine Assistance in den Preferences auf true oder false.
	 * 
	 * @param assistance
	 *            Assistance, welche gesetzt werden soll
	 * @param value
	 *            true um die Assistance anzuschalten, sonst false
	 */
	public void setAssistance(Assistances assistance, boolean value) {
		if (value) this.gameSettings.setAssistance(assistance);
		else this.gameSettings.clearAssistance(assistance);
		// notifyListeners(this);
	}

	/**
	 * Diese Methode teilt mit, ob die spezifizierte Hilfestellung im aktuellen
	 * Spielerprofil aktiviert ist. Ist dies der Fall, so wird true
	 * zurückgegeben. Ist die Hilfestellung nicht aktiv oder ungültig, so wird
	 * false zurückgegeben.
	 * 
	 * @param asst
	 *            Die Hilfestellung von der überprüft werden soll, ob sie im
	 *            aktuellen Profil aktiviert ist
	 * @return boolean true, falls die spezifizierte Hilfestellung im aktuellen
	 *         Profil aktiviert ist, false falls sie es nicht oder ungültig ist
	 */
	public boolean getAssistance(Assistances asst) {
		return this.gameSettings.getAssistance(asst);
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlTree toXmlTree() {
		XmlTree representation = new XmlTree("profile");
		representation.addAttribute(new XmlAttribute("id", String.valueOf(getCurrentProfileID())));
		representation.addAttribute(new XmlAttribute("currentGame", String.valueOf(getCurrentGame())));
		representation.addAttribute(new XmlAttribute("name", getName()));
		representation.addChild(gameSettings.toXmlTree());
		for (Statistics stat : Statistics.values()) {
			representation.addAttribute(new XmlAttribute(stat.name(), getStatistic(stat) + ""));
		}
		return representation;
	}

	/**
	 * {@inheritDoc}
	 */
	public void fillFromXml(XmlTree xmlTreeRepresentation) {
		setCurrentGame(Integer.parseInt(xmlTreeRepresentation.getAttributeValue("currentGame")));
		setName(xmlTreeRepresentation.getAttributeValue("name"));
		
		for (Iterator<XmlTree> iterator = xmlTreeRepresentation.getChildren(); iterator.hasNext();) {
            XmlTree sub = iterator.next();
            if(sub.getName().equals("gameSettings")){
            	gameSettings = new GameSettings();
            	gameSettings.fillFromXml(sub);
            }
        }
        /*TODO try this	
		for(XmlTree xt: xmlTreeRepresentation){
			
		}*/
		
		this.statistics = new int[Statistics.values().length];
		for (Statistics stat : Statistics.values()) {
			this.statistics[stat.ordinal()] = Integer.parseInt(xmlTreeRepresentation.getAttributeValue(stat.name()));
		}
	}

	private void saveProfilesFile(XmlTree profiles) {
		try {
			new XmlHelper().saveXml(profiles, FileManager.getProfilesFile());
		} catch (IOException e) {
			throw new IllegalStateException("Something went wrong writing profiles.xml", e);
		}
	}

	private XmlTree getProfilesXml() {
		try {
			return new XmlHelper().loadXml(FileManager.getProfilesFile());
		} catch (IOException e) {
			throw new IllegalStateException("Something went wrong reading profiles.xml", e);
		}
	}

	/**
	 * Setzt den Wert der gegebenen Statistik für dieses Profil auf den
	 * gegebenen Wert
	 * 
	 * @param stat
	 *            die zu setzende Statistik
	 * @param value
	 *            der einzutragende Wert
	 */
	public void setStatistic(Statistics stat, int value) {
		if (stat == null)
			return;
		this.statistics[stat.ordinal()] = value;
	}

	/**
	 * Diese Methode gibt den Wert der spezifizierten Statistik im aktuellen
	 * Spielerprofil zurück. Ist die spezifizierte Statistik ungültig, so wird
	 * null zurückgegeben.
	 * 
	 * @param stat
	 *            Die Statistik, dessen Wert abgerufen werden soll
	 * @return Der Wert der spezifizierten Statistik als String, oder null falls
	 *         diese ungültig ist
	 */
	public int getStatistic(Statistics stat) {
		if (stat == null)
			return -1;
		return this.statistics[stat.ordinal()];
	}

}
