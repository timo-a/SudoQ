/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.persistence.xml.profile.ProfileBE
import de.sudoq.model.persistence.xml.profile.ProfileRepo
import de.sudoq.model.persistence.xml.profile.ProfilesListRepo
import de.sudoq.model.xml.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * This static class is a wrapper for the currently loaded player profile
 * which is maintained by SharedPreferences of the Android-API.
 *
 */
open class ProfileManager() : ObservableModelImpl<ProfileManager>() {
//private constructor because class is static
//TODO split into profile handler and profile

    lateinit var currentProfile: ProfileBE //initialized in loadCurrentProfile


    /**
     * Name of the current player profiles
     */
    var name: String?
        get() = currentProfile.name
        set(value) {
            currentProfile.name = value
        }

    /**
     * ID of the player profile
     */
    var currentProfileID = -1
        get() = currentProfile.id
        private set //todo should not be used, try val

    /**
     * ID of the current [Game]
     */
    var currentGame
        get() = currentProfile.currentGame
        set(value) {
            currentProfile.currentGame = value
            profileRepo!!.update(currentProfile)
        }

    /**
     * AssistanceSet representing the available Assistances
     */
    var assistances = GameSettings()
        get() = currentProfile.assistances
        private set

    /**
     * AppSettings object representing settings bound to the app in general
     */
    var appSettings = AppSettings() //todo read from currentProfile instead, same above
        get() = currentProfile.appSettings
        private set

    var statistics: IntArray?
        get() = currentProfile.statistics
        set(value) {
            currentProfile.statistics = value
        }

    var profileRepo: ProfileRepo? = null //TODO refactor initialization, set it right
    var profilesListRepo: ProfilesListRepo? = null //TODO refactor initialization, set it right

    var currentProfileDir: File? = null
        get() = profileRepo!!.getProfileDirFor(currentProfileID)
        private set

    var profilesDir: File? = null //todo remove noargs constructor, make non-nullable
        set(value) {
            if (value == null)
                throw IllegalArgumentException("profiles dir is null")
            if (!value.canWrite())
                throw IllegalArgumentException("profiles dir cannot write")

            field = value
        }

    constructor(profilesDir: File) : this() {
        this.profileRepo = ProfileRepo(profilesDir)
        this.profilesListRepo = ProfilesListRepo(profilesDir)

        if (!profilesDir.canWrite())
            throw IllegalArgumentException("profiles dir cannot write")

        this.profilesDir = profilesDir
    }

    constructor(
        profileRepo: ProfileRepo,
        profilesListRepo: ProfilesListRepo,
        profilesDir: File
    ) : this() {//todo pas just file and init here?
        this.profileRepo = profileRepo
        this.profilesListRepo = profilesListRepo

        if (!profilesDir.canWrite())
            throw IllegalArgumentException("profiles dir cannot write")

        this.profilesDir = profilesDir
    }

    /** assumes an empty directory */
    fun initialize() {
        if (!profilesDir!!.exists())
            profilesDir!!.mkdirs()

        //create a new profile
        //currentProfile = profileRepo!!.create()
        createInitialProfile()


    }

    /**
     * Loads the current [ProfileManager] from profiles.xml
     */
    fun loadCurrentProfile() {//todo if all works bundle similarities
        //if the profiles (list) file doesn't exist
        if (!profilesDir!!.exists()) {
            profilesListRepo!!.createProfilesFile()
            currentProfile = profileRepo!!.create()
            profilesListRepo!!.addProfile(currentProfile)
            notifyListeners(this)
            return
        }

        if (profilesDir!!.list().isEmpty() || !File(profilesDir, "profiles.xml").exists()) {
            profilesListRepo!!.createProfilesFile()
        }

        //if there are no profiles
        if (profilesListRepo!!.getProfileNamesList().isEmpty()) {
            currentProfile = profileRepo!!.create()
            profilesListRepo!!.addProfile(currentProfile)
            notifyListeners(this)
            return
        }


        val currentProfileID =
            profilesListRepo!!.getCurrentProfileId()//todo put directly into setter of currentProfileID???

        currentProfile = profileRepo!!.read(currentProfileID)

        notifyListeners(this)
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    private fun createProfile() {


    }


    /**
     * Deletes the current [ProfileManager], if another one exists.
     * Cooses the next one that isn't deleted from profiles.xml
     */
    fun deleteProfile() {
        if (numberOfAvailableProfiles > 1) {

            profilesListRepo!!.deleteProfileFromList(currentProfileID)

            profileRepo!!.delete(currentProfileID)
            setProfile(profilesListRepo!!.getNextProfile())
        }
    }

    /**
     * Gibt die Anzahl der nicht geloeschten Profile zurueck
     *
     * @return Anzahl nicht geloeschter Profile
     */
    val numberOfAvailableProfiles: Int
        get() = profilesListRepo!!.getProfilesCount()


    // Profiles todo move all to profileManager
    /**
     * Determines whether there are any profiles saved as files
     * todo merge with numberOfAvailableProfiles
     *
     * @return die Anzahl der Profile
     */
    fun noProfiles(): Boolean { //query profileRepo directly
        if (!profilesDir!!.exists()) return true

        /*System.out.println("getnrp");
		for(String s: profiles.list())
			System.out.println(profiles.list());
		System.out.println("getnrpEND");*/
        var count =
            profilesDir!!.list()!!.size //one folder for each profile + file listing all profiles
        if (File(
                profilesDir,
                "profiles.xml"
            ).exists()
        ) {  //if profiles.xml exists subtract it from count
            count--
        }
        return count == 0
    }


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
        profileRepo!!.update(currentProfile)// save current
        return setProfile(profileID)
    }

    /**
     * Setzt das neue Profil ohne das alte zu speichern (zB nach löschen)
     */
    private fun setProfile(profileID: Int): Boolean {
        currentProfile = profileRepo!!.read(profileID) // load new values

        // set current profile in profiles.xml
        profilesListRepo!!.setCurrentProfileId(profileID)
        notifyListeners(this)
        return false
    }

    /**
     * Wird von der PlayerPreference aufgerufen, falls sie verlassen wird und
     * speichert Aenderungen an der profile.xml fuer dieses Profil sowie an der
     * profiles.xml, welche Informationen ueber alle Profile enthaelt
     */
    fun saveChanges() {
        profileRepo!!.update(currentProfile)

        profilesListRepo!!.updateProfilesList(currentProfile)
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    fun createAnotherProfile() {
        if (currentProfileID != -1) {
            profileRepo!!.update(currentProfile) // save current profile xml
        }

        currentProfile = profileRepo!!.create()
        profilesListRepo!!.addProfile(currentProfile)

        notifyListeners(this)
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    fun createInitialProfile() {

        currentProfile = profileRepo!!.createFirstProfile()
        profilesListRepo!!.createProfilesFile()
        profilesListRepo!!.addProfile(currentProfile)

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

    /**
     * Gibt die Datei zurück, in der die Gesten des Benutzers gespeichert werden
     *
     * @return File, welcher auf die Gesten-Datei des Benutzers zeigt
     */
    fun getCurrentGestureFile(): File = File(profilesDir, "gestures")


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
    val profilesNameList: ArrayList<String> //todo change return type to just List<String>
        get() {
            return ArrayList(profilesListRepo!!.getProfileNamesList())
        }

    /**
     * Gibt eine Integer Liste mit allen Profilids zurück.
     *
     * @return die Idliste
     */
    val profilesIdList: ArrayList<Int> //todo change return type to just List<Int>
        get() {
            return ArrayList(profilesListRepo!!.getProfileIdsList())
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
        if (value) assistances.setAssistance(assistance!!) else assistances.clearAssistance(
            assistance!!
        )
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
        statistics!![stat.ordinal] = value
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
        return if (stat == null) -1 else statistics!![stat.ordinal]
    }

}