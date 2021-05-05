package de.sudoq.model.persistence.xml

import de.sudoq.model.files.FileManager
import de.sudoq.model.persistence.IRepo
import de.sudoq.model.profile.Profile
import de.sudoq.model.xml.XmlHelper
import java.io.File

class ProfileRepo(private val profilesDir : File) : IRepo<Profile> {

    override fun save(t: Profile): Profile {
        TODO("Not yet implemented")
    }

    override fun load(id: Int): Profile {
        val profileDir = File(profilesDir, "profile_$id")
        val file = File(profileDir, "profile.xml")
        var p = Profile()
        val helper = XmlHelper()
        p.fillFromXml(helper.loadXml(file)!!)
        return p
    }
}