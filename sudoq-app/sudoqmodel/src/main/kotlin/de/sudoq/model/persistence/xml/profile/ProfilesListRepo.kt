package de.sudoq.model.persistence.xml.profile

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlHelper
import de.sudoq.model.xml.XmlTree
import java.io.File
import java.io.IOException

class ProfilesListRepo(private val profilesDir: File) : IRepo<ProfilesListBE> {
//todo names and ids are redundant. just iterate through the profiles everytime?

    override fun create(): ProfilesListBE {
        TODO("Not yet implemented")
    }


    override fun read(id: Int): ProfilesListBE {
        TODO("Not yet implemented")
    }

    override fun update(t: ProfilesListBE): ProfilesListBE {
        TODO("Not yet implemented")
    }

    override fun delete(id: Int) {
        TODO("Not yet implemented")
    }


    fun addProfile(newProfile: Profile) {
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
    fun createProfilesFile() {
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


    fun updateProfilesList(changedProfile: Profile) {
        val profiles = profilesXml
        for (profile in profiles) {
            if (profile.getAttributeValue(ID)!!.toInt() == changedProfile.id) {
                profile.updateAttribute(XmlAttribute(NAME, changedProfile.name!!))
            }
        }
        saveProfilesFile(profiles)
    }

    //todo make property
    fun getCurrentProfileId(): Int {
        return profilesXml.getAttributeValue(name = CURRENT)!!.toInt()
    }

    fun setCurrentProfileId(id: Int) {
        profilesXml.updateAttribute(XmlAttribute(CURRENT, id.toString()))
        saveProfilesFile(profilesXml)
    }

    fun deleteProfileFromList(id: Int) {
        val oldProfiles = profilesXml
        val profiles = XmlTree(oldProfiles.name)
        oldProfiles
            .filter { it.getAttributeValue(ID)!!.toInt() != id }
            .forEach { profiles.addChild(it) }

        saveProfilesFile(profiles)
    }

    fun getNextProfile(): Int {
        return profilesXml.getChildren().next().getAttributeValue(ID)!!.toInt()
    }

    fun getProfilesCount(): Int = profilesXml.numberOfChildren

    fun getProfileNamesList(): List<String> {
        return profilesXml.map { it.getAttributeValue(NAME)!! }
    }

    fun getProfileIdsList(): List<Int> {
        return profilesXml.map { it.getAttributeValue(ID)!!.toInt() }
    }

    companion object {
        private const val ID = "id"
        private const val NAME = "name"
        private const val CURRENT = "current"

    }
}