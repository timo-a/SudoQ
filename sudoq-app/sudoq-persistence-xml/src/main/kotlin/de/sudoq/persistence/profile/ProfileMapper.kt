package de.sudoq.persistence.profile

import de.sudoq.model.profile.Profile

object ProfileMapper {
    fun toBE(profile: Profile): ProfileBE {
        return ProfileBE(profile.id,
            profile.currentGame,
            profile.name!!,
            profile.assistances,
            profile.statistics!!,
            profile.appSettings
        )
    }

    fun fromBE(profileBE: ProfileBE): Profile {
        val profile = Profile(profileBE.id)
        profile.currentGame = profileBE.currentGame
        profile.name = profileBE.name
        profile.assistances = profileBE.assistances
        profile.statistics = profileBE.statistics
        profile.appSettings = profileBE.appSettings
        return profile
    }

}