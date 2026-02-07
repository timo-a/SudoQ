package de.sudoq.persistence.profile

import de.sudoq.model.profile.Profile
import de.sudoq.model.profile.ProfileStatistics
import de.sudoq.model.profile.Statistics

object ProfileMapper {
    fun toBE(profile: Profile): ProfileBE {
        val statistics = IntArray(Statistics.entries.size)
        for (i in Statistics.entries.indices) {
            statistics[i] = profile.statistics.getStatistic(Statistics.entries[i])
        }
        return ProfileBE(profile.id,
            profile.currentGame,
            profile.name,
            profile.assistances,
            statistics,
            profile.appSettings
        )
    }

    fun fromBE(profileBE: ProfileBE): Profile {
        val statistics = ProfileStatistics(profileBE.statistics!!)
        val profile = Profile(profileBE.id, profileBE.name!!, statistics)
        profile.currentGame = profileBE.currentGame
        profile.assistances = profileBE.assistances
        profile.appSettings = profileBE.appSettings
        return profile
    }

}