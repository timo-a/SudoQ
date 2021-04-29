/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.files.FileManager
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.xml.*
import java.io.IOException
import java.util.*

/**
 * This static class is a wrapper for the currently loaded player profile
 * which is maintained by SharedPreferences of the Android-API.
 *
 */
class Profile private constructor() : ObservableModelImpl<Profile>(), Xmlable {
//private constructor because class is static
//TODO split into profile handler and profile

    /**
     * Name of the current player profiles
     */
    var name: String? = null

    /**
     * ID of the player profile
     */
    var currentProfileID = -1
        private set

    /**
     * ID of the current [Game]
     */
    var currentGame = 0

    /**
     * AssistanceSet representing the available Assistances
     */
    var assistances = GameSettings()
        private set

    /**
     * AppSettings object representing settings bound to the app in general
     */
    var appSettings = AppSettings()
        private set

    var statistics: IntArray? = null

    private val xmlHandler: XmlHandler<Profile> = ProfileXmlHandler()

    /**
     * Loads the current [Profile] from profiles.xml
     */
    fun loadCurrentProfile() {
        if (!FileManager.getProfilesFile().exists()) {
            FileManager.createProfilesFile()
            createProfile()
            return
        }
        val profiles = profilesXml
        currentProfileID = profiles.getAttributeValue(CURRENT)!!.toInt()
        xmlHandler.createObjectFromXml(this)
        FileManager.setCurrentProfile(currentProfileID)
        notifyListeners(this)
    }

    /**
     * Deletes the current [Profile], if another one exists.
     * Cooses the next one that isn't deleted from profiles.xml
     */
    fun deleteProfile() {
        if (numberOfAvailableProfiles > 1) {
            val oldProfiles = profilesXml
            val profiles = XmlTree(oldProfiles.name)
            for (profile in oldProfiles) {
                if (profile.getAttributeValue(ID)!!.toInt() != currentProfileID) {
                    profiles.addChild(profile)
                }
            }
            saveProfilesFile(profiles)
            FileManager.deleteProfile(currentProfileID)
            setProfile(profiles.getChildren().next().getAttributeValue(ID)!!.toInt())
        }
    }

    /**
     * Gibt die Anzahl der nicht geloeschten Profile zurueck
     *
     * @return Anzahl nicht geloeschter Profile
     */
    val numberOfAvailableProfiles: Int
        get() = profilesXml.numberOfChildren

    /**
     * Diese Methode ändert die Einstellungen, die mit dieser Klasse abgerufen
     * werden können auf die spezifizierten SharedPreferences. Sind diese null,
     * so wird nichts geändert und es wird false zurückgegeben. War die Änderung
     * erfolgreich, so wird true zurückgegeben.
     *
     * @param profileID
     * Die ID des Profils, zu dem gewechselt werden soll.
     *
     * @return boolean true, falls die Änderung erfolgreich war, false
     * andernfalls
     */
    fun changeProfile(profileID: Int): Boolean {
        val oldProfileID = currentProfileID
        if (profileID == oldProfileID) return true
        xmlHandler.saveAsXml(this) // save current
        return setProfile(profileID)
    }

    /**
     * Setzt das neue Profil ohne das alte zu speichern (zB nach löschen)
     */
    private fun setProfile(profileID: Int): Boolean {
        currentProfileID = profileID
        xmlHandler.createObjectFromXml(this) // load new values

        // set current profile in profiles.xml
        val profiles = profilesXml
        profiles.updateAttribute(XmlAttribute(CURRENT, Integer.toString(profileID)))
        saveProfilesFile(profiles)
        FileManager.setCurrentProfile(profileID)
        notifyListeners(this)
        return false
    }

    /**
     * Wird von der PlayerPreference aufgerufen, falls sie verlassen wird und
     * speichert Aenderungen an der profile.xml fuer dieses Profil sowie an der
     * profiles.xml, welche Informationen ueber alle Profile enthaelt
     */
    fun saveChanges() {
        xmlHandler.saveAsXml(this)
        val profiles = profilesXml
        for (profile in profiles) {
            if (profile.getAttributeValue(ID)!!.toInt() == currentProfileID) {
                profile.updateAttribute(XmlAttribute(NAME, name!!))
            }
        }
        saveProfilesFile(profiles)
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    fun createProfile() {
        val newProfileID = newProfileID
        if (currentProfileID != -1) {
            xmlHandler.saveAsXml(this) // save current profile xml
        }
        currentProfileID = newProfileID
        setDefaultValues()
        FileManager.createProfileFiles(newProfileID)

        // add into profiles.xml and save it
        val profiles = profilesXml
        val profileTree = XmlTree("profile")
        profileTree.addAttribute(XmlAttribute(ID, Integer.toString(newProfileID)))
        profileTree.addAttribute(XmlAttribute(NAME, name!!))
        profiles.addChild(profileTree)
        profiles.updateAttribute(XmlAttribute(CURRENT, Integer.toString(newProfileID)))
        saveProfilesFile(profiles)

        // save new profile xml
        xmlHandler.saveAsXml(this)
        FileManager.setCurrentProfile(newProfileID)
        notifyListeners(this)
    }

    private fun setDefaultValues() {
        name = DEFAULT_PROFILE_NAME
        currentGame = -1
        assistances = GameSettings()
        assistances.setAssistance(Assistances.markRowColumn)
        //		this.gameSettings.setGestures(false);
        //this.appSettings.setDebug(false);
        statistics = IntArray(Statistics.values().size)
        statistics!![Statistics.fastestSolvingTime.ordinal] = INITIAL_TIME_RECORD
        notifyListeners(this)
    }

    /**
     * Gibt eine neue Profil ID zurück. Bestehende Profile dürfen nicht gelöscht
     * werden, sonder als solches markiert werden.
     *
     * @return the new Profile ID
     */
    val newProfileID: Int
        get() {
            val used = ArrayList<Int>()
            for (profile in profilesXml) {
                used.add(profile.getAttributeValue(ID)!!.toInt())
            }
            var i = 1
            while (used.contains(i)) {
                i++
            }
            return i
        }
    /**
     * Diese Methode gibt zurück, ob die Gesteneingabe im aktuellen
     * Spielerprofil aktiv ist oder nicht.
     *
     * @return true, falls die Gesteneingabe im aktuellen Profil aktiv ist,
     * false andernfalls
     */
    /**
     * Setzt die Verwendung von Gesten in den Preferences auf den übergebenen
     * Wert.
     *
     * @param value
     * true, falls Gesten gesetzt werden sollen, false falls nicht
     */
    var isGestureActive: Boolean
        get() = assistances.isGesturesSet
        set(value) {
            assistances.setGestures(value)
        }

    /*Advanced Settings*/
    fun setLefthandActive(value: Boolean) {
        assistances.setLefthandMode(value)
    }

    fun setHelperActive(value: Boolean) {
        assistances.setHelper(value)
    }

    fun setDebugActive(value: Boolean) {
        appSettings.setDebug(value)
    }

    /**
     * Gibt eine String Liste mit allen Profilnamen zurück.
     *
     * @return die Namensliste
     */
    val profilesNameList: ArrayList<String>
        get() {
            val profilesList = ArrayList<String>()
            val profiles = profilesXml
            for (profile in profiles) {
                profile.getAttributeValue(NAME)?.let { profilesList.add(it) }
            }
            return profilesList
        }

    /**
     * Gibt eine Integer Liste mit allen Profilids zurück.
     *
     * @return die Idliste
     */
    val profilesIdList: ArrayList<Int>
        get() {
            val profilesList = ArrayList<Int>()
            val profiles = profilesXml
            for (profile in profiles) {
                profilesList.add(Integer.valueOf(profile.getAttributeValue(ID)))
            }
            return profilesList
        }

    /**
     * Setzt eine Assistance in den Preferences auf true oder false.
     *
     * @param assistance
     * Assistance, welche gesetzt werden soll
     * @param value
     * true um die Assistance anzuschalten, sonst false
     */
    fun setAssistance(assistance: Assistances?, value: Boolean) {
        if (value) assistances.setAssistance(assistance!!) else assistances.clearAssistance(assistance!!)
        // notifyListeners(this);
    }

    /**
     * Diese Methode teilt mit, ob die spezifizierte Hilfestellung im aktuellen
     * Spielerprofil aktiviert ist. Ist dies der Fall, so wird true
     * zurückgegeben. Ist die Hilfestellung nicht aktiv oder ungültig, so wird
     * false zurückgegeben.
     *
     * @param asst
     * Die Hilfestellung von der überprüft werden soll, ob sie im
     * aktuellen Profil aktiviert ist
     * @return boolean true, falls die spezifizierte Hilfestellung im aktuellen
     * Profil aktiviert ist, false falls sie es nicht oder ungültig ist
     */
    fun getAssistance(asst: Assistances?): Boolean {
        return assistances.getAssistance(asst!!)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("profile")
        representation.addAttribute(XmlAttribute("id", currentProfileID.toString()))
        representation.addAttribute(XmlAttribute("currentGame", currentGame.toString()))
        representation.addAttribute(XmlAttribute("name", name!!))
        representation.addChild(assistances.toXmlTree())
        for (stat in Statistics.values()) {
            representation.addAttribute(XmlAttribute(stat.name, getStatistic(stat).toString() + ""))
        }
        representation.addChild(appSettings.toXmlTree())
        return representation
    }

    /**
     * {@inheritDoc}
     */
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        currentGame = xmlTreeRepresentation.getAttributeValue("currentGame")!!.toInt()
        name = xmlTreeRepresentation.getAttributeValue("name")
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "gameSettings") {
                assistances = GameSettings()
                assistances.fillFromXml(sub)
            }
            if (sub.name == "appSettings") {
                appSettings = AppSettings()
                appSettings.fillFromXml(sub)
            }
        }
        statistics = IntArray(Statistics.values().size)
        for (stat in Statistics.values()) {
            statistics!![stat.ordinal] = xmlTreeRepresentation.getAttributeValue(stat.name)!!.toInt()
        }
    }

    private fun saveProfilesFile(profiles: XmlTree) {
        try {
            XmlHelper().saveXml(profiles, FileManager.getProfilesFile())
        } catch (e: IOException) {
            throw IllegalStateException("Something went wrong writing profiles.xml", e)
        }
    }

    private val profilesXml: XmlTree
        get() = try {
            XmlHelper().loadXml(FileManager.getProfilesFile())
        } catch (e: IOException) {
            throw IllegalStateException("Something went wrong reading profiles.xml", e)
        }

    /**
     * Setzt den Wert der gegebenen Statistik für dieses Profil auf den
     * gegebenen Wert
     *
     * @param stat
     * die zu setzende Statistik
     * @param value
     * der einzutragende Wert
     */
    fun setStatistic(stat: Statistics?, value: Int) {
        if (stat == null) return
        statistics!!.set(stat.ordinal, value)
    }

    /**
     * Diese Methode gibt den Wert der spezifizierten Statistik im aktuellen
     * Spielerprofil zurück. Ist die spezifizierte Statistik ungültig, so wird
     * null zurückgegeben.
     *
     * @param stat
     * Die Statistik, dessen Wert abgerufen werden soll
     * @return Der Wert der spezifizierten Statistik als String, oder null falls
     * diese ungültig ist
     */
    fun getStatistic(stat: Statistics?): Int {
        return if (stat == null) -1 else statistics!!.get(stat.ordinal)
    }

    companion object {
        const val INITIAL_TIME_RECORD = 5999
        private const val ID = "id"
        private const val NAME = "name"
        private const val CURRENT = "current"

        /**
         * Konstante die signalisiert, dass es kein aktuelles Spiel gibt
         */
        const val NO_GAME = -1

        /**
         * Konstante die signalisiert, dass ein neues Profil noch keinen namen hat
         */
        const val DEFAULT_PROFILE_NAME = "unnamed"

        /**
         * Diese Methode gibt eine Instance dieser Klasse zurück, wird sie erneut
         * aufgerufen, so wird dieselbe Instanz zurückgegeben.
         *
         * @return Die Instanz dieses Profile Singletons
         */
        //@JvmStatic
		//@get:Synchronized
        var instance: Profile?
        init {
            instance = Profile()
            instance!!.loadCurrentProfile()
        }

        fun forceReinitialize() : Profile {
            instance = Profile()
            instance!!.loadCurrentProfile();
            return instance!!
        }


    }
}