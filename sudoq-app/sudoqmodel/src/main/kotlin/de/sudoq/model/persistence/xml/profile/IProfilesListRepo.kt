package de.sudoq.model.persistence.xml.profile

import de.sudoq.model.profile.Profile

interface IProfilesListRepo {

    fun profilesFileExists(): Boolean
    fun createProfilesFile()
    fun addProfile(newProfile: Profile)
    fun getProfileNamesList(): List<String>
    fun getCurrentProfileId(): Int
    fun deleteProfileFromList(id: Int)
    fun getNextProfile(): Int
    fun getProfilesCount(): Int
    fun setCurrentProfileId(id: Int)
    fun updateProfilesList(changedProfile: Profile)
    fun getProfileIdsList(): List<Int>
}