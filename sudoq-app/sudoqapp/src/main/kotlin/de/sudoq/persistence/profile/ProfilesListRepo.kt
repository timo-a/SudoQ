package de.sudoq.persistence.profile

import de.sudoq.model.persistence.xml.profile.IProfilesListRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import java.io.File
import java.io.IOException

class ProfilesListRepo(private val profilesDir: File) : IProfilesListRepo {
//todo names and ids are redundant. just iterate through the profiles everytime?

    override fun addProfile(newProfile: Profile) {
        //todo move to repo
        val newProfileID = newProfile.id

        // add into profiles.xml and save it
        val profiles = profilesXml
        val profileTree = XmlTree("profile")
        profileTree.addAttribute(XmlAttribute(ID, newProfile.id.toString()))
        profileTree.addAttribute(XmlAttribute(NAME, newProfile.name!!))
        profiles.addChild(profileTree)
        profiles.updateAttribute(XmlAttribute(CURRENT, newProfile.id.toString()))
        saveProfilesFile(profiles)

    }

    /////// list of all profiles

    private val profilesXml: XmlTree
        get() = try {
            XmlHelper().loadXml(getProfilesFile())!!
        } catch (e: IOException) {
            throw IllegalStateException("Something went wrong reading profiles.xml", e)
        }

    /**
     * Erzeugt die profiles.xml Datei, wenn noch kein Profil vorhanden ist.
     */
    override fun createProfilesFile() {
        val profilesXML = File(profilesDir.absolutePath + File.separator + "profiles.xml")
        try {
            val xmlTree = XmlTree("profiles")
            xmlTree.addAttribute(XmlAttribute(CURRENT, "-1"))//empty list -> no current profile
            XmlHelper().saveXml(xmlTree, profilesXML)
        } catch (e: IOException) {
            throw java.lang.IllegalStateException("Couldnt create profiles.xml", e)
        }
    }


    /**
     * Gibt die Profilliste Datei zurueck
     *
     * @return das File welches auf die Profilliste zeigt
     */
    private fun getProfilesFile(): File {
        return File(profilesDir, "profiles.xml")
    }

    private fun saveProfilesFile(profiles: XmlTree) {
        try {
            XmlHelper().saveXml(profiles, getProfilesFile())
        } catch (e: IOException) {
            throw IllegalStateException("Something went wrong writing profiles.xml", e)
        }
    }


    override fun updateProfilesList(changedProfile: Profile) {
        val profiles = profilesXml
        for (profile in profiles) {
            if (profile.getAttributeValue(ID)!!.toInt() == changedProfile.id) {
                profile.updateAttribute(XmlAttribute(NAME, changedProfile.name!!))
            }
        }
        saveProfilesFile(profiles)
    }

    //todo make property
    override fun getCurrentProfileId(): Int {
        return profilesXml.getAttributeValue(name = CURRENT)!!.toInt()
    }

    override fun setCurrentProfileId(id: Int) {
        profilesXml.updateAttribute(XmlAttribute(CURRENT, id.toString()))
        saveProfilesFile(profilesXml)
    }

    override fun deleteProfileFromList(id: Int) {
        val oldProfiles = profilesXml
        val profiles = XmlTree(oldProfiles.name)
        oldProfiles
            .filter { it.getAttributeValue(ID)!!.toInt() != id }
            .forEach { profiles.addChild(it) }

        saveProfilesFile(profiles)
    }

    override fun getNextProfile(): Int {
        return profilesXml.getChildren().next().getAttributeValue(ID)!!.toInt()
    }

    override fun getProfilesCount(): Int = profilesXml.numberOfChildren

    override fun getProfileNamesList(): List<String> {
        return profilesXml.map { it.getAttributeValue(NAME)!! }
    }

    override fun getProfileIdsList(): List<Int> {
        return profilesXml.map { it.getAttributeValue(ID)!!.toInt() }
    }

    companion object {
        private const val ID = "id"
        private const val NAME = "name"
        private const val CURRENT = "current"

    }
}