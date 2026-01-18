/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.persistence.IProfileRepo
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.profile.IProfilesListRepo
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * This static class is a wrapper for the currently loaded player profile
 * which is maintained by SharedPreferences of the Android-API.
 *
 */
open class ProfileManager(
    val profilesDir: File,
    val profileRepo: IProfileRepo,
    val profilesListRepo: IProfilesListRepo
) : ObservableModelImpl<ProfileManager>() {
//TODO split into profile handler and profile
//todo we used to have several profiles and supported switching between them. At some point this was removed to cut down complexity. but at some point we might want to bring it back

    private lateinit var currentProfile: Profile //initialized in loadCurrentProfile

    //todo is it save to just give out the current profile instead of having all these delegating methods?

    /**
     * Name of the current player profiles
     */
    var name: String
        get() = currentProfile.name
        set(value) {
            currentProfile.name = value
        }

    /**
     * ID of the player profile
     */
    val currentProfileID: Int
        //get because dependent on lateinit currentProfile
        get() = currentProfile.id

    /**
     * ID of the current [Game]
     */
    var currentGame
        get() = currentProfile.currentGame
        set(value) {
            currentProfile.currentGame = value
            profileRepo.update(currentProfile)
        }

    /**
     * AssistanceSet representing the available Assistances
     */
    val assistances: GameSettings
        get() = currentProfile.assistances

    /**
     * AppSettings object representing settings bound to the app in general
     */
    val appSettings: AppSettings //todo read from currentProfile instead, same above
        get() = currentProfile.appSettings

    var statistics: IntArray
        get() = currentProfile.statistics
        set(value) {
            currentProfile.statistics = value
        }

    val currentProfileDir: File
        //this is a get because currentProfileID is depends on lateinit currentProfile
        get() = File(profilesDir.absolutePath, "profile_$currentProfileID")

    init {
        require(profilesDir.canWrite()) { "profiles dir cannot write" }
    }


    /** assumes an empty directory */
    fun initialize(name: String) {
        if (!profilesDir.exists())
            profilesDir.mkdirs()

        //create a new profile
        //currentProfile = profileRepo!!.create()
        createInitialProfile(name)


    }

    /**
     * Loads the current [ProfileManager] from profiles.xml
     *///todo shouldn't this method be called in the constructor?
    fun loadCurrentProfile() {//todo if all works bundle similarities
        //if the profiles (list) file doesn't exist
        check(profilesDir.exists())
        check(profilesDir.list()!!.isNotEmpty())
        check(File(profilesDir, "profiles.xml").exists())
        check(profilesListRepo.getProfileNamesList().isNotEmpty())
        //if there are no profiles

        val currentProfileID =
            profilesListRepo.getCurrentProfileId()//todo put directly into setter of currentProfileID???

        currentProfile = profileRepo.read(currentProfileID)

        notifyListeners(this)
    }

    /**
     * Deletes the current [ProfileManager], if another one exists.
     * Cooses the next one that isn't deleted from profiles.xml
     */
    fun deleteProfile() {
        if (numberOfAvailableProfiles > 1) {

            profilesListRepo.deleteProfileFromList(currentProfileID)

            profileRepo.delete(currentProfileID)
            setProfile(profilesListRepo.getNextProfile())
        }
    }

    /**
     * Gibt die Anzahl der nicht geloeschten Profile zurueck
     *
     * @return Anzahl nicht geloeschter Profile
     */
    val numberOfAvailableProfiles: Int
        get() = profilesListRepo.getProfilesCount()


    // Profiles todo move all to profileManager
    /**
     * Determines whether there are any profiles saved as files
     * todo merge with numberOfAvailableProfiles
     *
     * @return die Anzahl der Profile
     */
    fun noProfiles(): Boolean { //query profileRepo directly
        if (!profilesDir.exists()) return true

        // we expect
        // <ProfilesDir>
        // |- gestures
        // |- profiles.xml
        // '- profile_x
        //     |- profile.xml
        //     |- games.xml
        //     '- games        //may be empty
        val fileExists = profilesDir.listFiles()!!.any { it.name.startsWith("profile_")
                && it.isDirectory
                && it.listFiles()!!.any { it.name == "profile.xml" }
        }
        return !fileExists
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
        profileRepo.update(currentProfile)// save current
        return setProfile(profileID)
    }

    /**
     * Setzt das neue Profil ohne das alte zu speichern (zB nach löschen)
     */
    private fun setProfile(profileID: Int): Boolean {
        currentProfile = profileRepo.read(profileID) // load new values

        // set current profile in profiles.xml
        profilesListRepo.setCurrentProfileId(profileID)
        notifyListeners(this)
        return false
    }

    /**
     * Wird von der PlayerPreference aufgerufen, falls sie verlassen wird und
     * speichert Aenderungen an der profile.xml fuer dieses Profil sowie an der
     * profiles.xml, welche Informationen ueber alle Profile enthaelt
     */
    fun saveChanges() {
        profileRepo.update(currentProfile)

        profilesListRepo.updateProfilesList(currentProfile)
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    fun createAnotherProfile(name: String) {
        if (currentProfileID != -1) {
            profileRepo.update(currentProfile) // save current profile xml
        }

        createProfile(name)

        notifyListeners(this)
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    fun createInitialProfile(name: String) {

        if(!profilesListRepo.profilesFileExists()) {
            profilesListRepo.createProfilesFile()
        }
        createProfile(name)
    }

    fun createProfile(name: String) {
        check(profilesListRepo.profilesFileExists())

        val id = profileRepo.getFreeId()
        currentProfile = Profile(id, name)
        currentProfile.assistances.setAssistance(Assistances.markRowColumn)
        currentProfile.statistics[Statistics.fastestSolvingTime.ordinal] = INITIAL_TIME_RECORD
        profileRepo.create(currentProfile)
        profilesListRepo.addProfile(currentProfile)
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
            assistances.isGesturesSet = value
        }

    /**
     * Gibt die Datei zurück, in der die Gesten des Benutzers gespeichert werden
     *
     * @return File, welcher auf die Gesten-Datei des Benutzers zeigt
     */
    fun getCurrentGestureFile(): File = File(profilesDir, "gestures")


    /*Advanced Settings*/
    fun setLefthandActive(value: Boolean) {
        assistances.isLeftHandModeSet = value
    }

    fun setHelperActive(value: Boolean) {
        assistances.isHelpersSet = value
    }

    fun setDebugActive(value: Boolean) {
        appSettings.setDebug(value)
    }

    /**
     * Gibt eine String Liste mit allen Profilnamen zurück.
     *
     * @return die Namensliste
     */
    val profilesNameList: List<String>
        get() = profilesListRepo.getProfileNamesList()

    /**
     * Gibt eine Integer Liste mit allen Profilids zurück.
     *
     * @return die Idliste
     */
    val profilesIdList: List<Int>
        get() = profilesListRepo.getProfileIdsList()

    /**
     * Setzt eine Assistance in den Preferences auf true oder false.
     *
     * @param assistance
     * Assistance, welche gesetzt werden soll
     * @param value
     * true um die Assistance anzuschalten, sonst false
     */
    fun setAssistance(assistance: Assistances, value: Boolean) {
        if (value)
            assistances.setAssistance(assistance)
        else
            assistances.clearAssistance(assistance)
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
    fun getAssistance(asst: Assistances): Boolean = assistances.getAssistance(asst)

    /**
     * Setzt den Wert der gegebenen Statistik für dieses Profil auf den
     * gegebenen Wert
     *
     * @param stat
     * die zu setzende Statistik
     * @param value
     * der einzutragende Wert
     */
    fun setStatistic(stat: Statistics, value: Int) {
        statistics[stat.ordinal] = value
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
    fun getStatistic(stat: Statistics): Int = statistics[stat.ordinal]

    companion object {
        const val INITIAL_TIME_RECORD = 5999

        /**
         * Konstante die signalisiert, dass es kein aktuelles Spiel gibt
         */
        const val NO_GAME = -1

        /**
         * Konstante die signalisiert, dass ein neues Profil noch keinen namen hat
         */
        const val DEFAULT_PROFILE_NAME = "unnamed"
    }


}