package de.sudoq.persistence.profile

import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.profile.ProfileBE
import de.sudoq.model.persistence.xml.profile.ProfileMapper
import de.sudoq.model.persistence.xml.profile.ProfilesListRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.profile.ProfileManager
import de.sudoq.model.profile.Statistics
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import java.io.File
import java.io.IOException


class ProfileRepo(private val profilesDir: File) : IRepo<Profile> {

    override fun create(): Profile {

        return create(newProfileID)
    }

    private fun create(id: Int): Profile {

        val newProfile = ProfileBE(id)
        newProfile.name = ProfileManager.DEFAULT_PROFILE_NAME
        newProfile.currentGame = -1
        newProfile.assistances = GameSettings()
        newProfile.assistances.setAssistance(Assistances.markRowColumn)
        //		this.gameSettings.setGestures(false);
        //this.appSettings.setDebug(false);
        newProfile.statistics = IntArray(Statistics.values().size)
        newProfile.statistics!![Statistics.fastestSolvingTime.ordinal] = ProfileManager.INITIAL_TIME_RECORD

        createProfileFiles(id)
        // save new profile xml
        val profileReloaded = updateBE(newProfile)
        return ProfileMapper.fromBE(profileReloaded)
    }


    /**
     * Erstellt die Ordnerstruktur und nötige Dateien für das Profil mit der
     * übergebenen ID
     *
     * @param id
     * ID des Profils
     */
    private fun createProfileFiles(id: Int) {
        val profileDir = getProfileDirFor(id)
        profileDir.mkdir()
        File(profileDir, "games").mkdir()
        val games = File(profileDir, "games.xml")
        try {
            XmlHelper().saveXml(XmlTree("games"), games)
        } catch (e: IOException) {
            throw IllegalStateException("Invalid Profile", e)
        }
    }


    override fun read(id: Int): Profile {
        val profileDir = File(profilesDir, "profile_$id")
        val file = File(profileDir, "profile.xml")
        val p = ProfileBE(id)
        val helper = XmlHelper()
        p.fillFromXml(helper.loadXml(file)!!)
        return ProfileMapper.fromBE(p)
    }

    private fun readBE(id: Int): ProfileBE {
        val profileDir = File(profilesDir, "profile_$id")
        val file = File(profileDir, "profile.xml")
        val p = ProfileBE(id)
        val helper = XmlHelper()
        p.fillFromXml(helper.loadXml(file)!!)
        return p
    }

    override fun update(t: Profile): Profile {
        val profileBEIn = ProfileMapper.toBE(t)
        val profileBEOut = updateBE(profileBEIn)
        val profileOut = ProfileMapper.fromBE(profileBEOut)
        return profileOut
    }

    private fun updateBE(profileBE: ProfileBE): ProfileBE {
        try {
            val file = getProfileXmlFor(profileBE.id)
            val tree = profileBE.toXmlTree()
            val helper = XmlHelper()
            helper.saveXml(tree, file)
            return readBE(profileBE.id) //return the object that is now saved under that id
        } catch (e: IOException) {
            throw IllegalArgumentException("Something went wrong when writing xml", e)
        }
    }

    /**
     * Gibt die XML-Datei das aktuellen Profils zurück
     *
     * @param id
     * die id des Profils dessen xml gesucht ist
     *
     * @return File, welcher auf die XML Datei des aktuellen Profils zeigt
     */
    private fun getProfileXmlFor(id: Int): File {
        return File(getProfileDirFor(id), "profile.xml")
    }


    @Throws(java.lang.IllegalArgumentException::class)
    override fun delete(id: Int) {
        val dir: File = getProfileDirFor(id)

        if (!dir.exists())
            return

        if (!dir.deleteRecursively())
            throw java.lang.IllegalArgumentException("Unable to delete given Profile")

    }


    /////// file operations
    /**
     * Gibt das Verzeichnis des Profils mit der gegebenen id zurueck
     *
     * @return File, welcher auf das Profilverzeichnis zeigt
     */
    private fun getProfileDirFor(id: Int): File {
        return File(profilesDir.absolutePath + File.separator + "profile_$id")
    }


    /**
     * Gibt eine neue Profil ID zurück. Bestehende Profile dürfen nicht gelöscht
     * werden, sonder als solches markiert werden.
     *
     * @return the new Profile ID
     */
    private val newProfileID: Int
        get() {
            val used = getProfileIdsList()

            var i = 1
            while (used.contains(i)) i++
            return i
        }


    private fun getProfileIdsList(): List<Int> {
        val profilesList = ProfilesListRepo(profilesDir)
        return profilesList.getProfileIdsList()
    }

}