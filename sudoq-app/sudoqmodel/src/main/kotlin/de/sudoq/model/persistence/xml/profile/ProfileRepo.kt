package de.sudoq.model.persistence.xml.profile

import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.profile.Statistics
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import java.io.File
import java.io.IOException


class ProfileRepo(private val profilesDir: File) : IRepo<ProfileBE> {

    override fun create(): ProfileBE {

        return create(newProfileID)
    }

    private fun create(id: Int): ProfileBE {

        val newProfile = ProfileBE(id)
        newProfile.name = Profile.DEFAULT_PROFILE_NAME
        newProfile.currentGame = -1
        newProfile.assistances = GameSettings()
        newProfile.assistances.setAssistance(Assistances.markRowColumn)
        //		this.gameSettings.setGestures(false);
        //this.appSettings.setDebug(false);
        newProfile.statistics = IntArray(Statistics.values().size)
        newProfile.statistics!![Statistics.fastestSolvingTime.ordinal] = Profile.INITIAL_TIME_RECORD

        createProfileFiles(id)
        // save new profile xml
        update(newProfile)

        return newProfile
    }


    fun createFirstProfile(): ProfileBE {
        return create(1)
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
            throw IllegalStateException("Invalid Profil", e)
        }
    }


    override fun read(id: Int): ProfileBE {
        val profileDir = File(profilesDir, "profile_$id")
        val file = File(profileDir, "profile.xml")
        val p = ProfileBE(id)
        val helper = XmlHelper()
        p.fillFromXml(helper.loadXml(file)!!)
        return p
    }

    override fun update(t: ProfileBE): ProfileBE {
        try {
            val file = getProfileXmlFor(t.id)
            val tree = t.toXmlTree()
            val helper = XmlHelper()
            helper.saveXml(tree, file)
            return read(t.id) //return the object that is now saved under that id
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
    fun getProfileDirFor(id: Int): File {
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