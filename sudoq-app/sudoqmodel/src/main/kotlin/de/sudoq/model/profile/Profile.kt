package de.sudoq.model.profile

import de.sudoq.model.game.GameSettings

class Profile(val id: Int, var name: String, val statistics: ProfileStatistics) {

    var currentGame: Int = ProfileManager.NO_GAME //todo this field should be nullable, to make "no game" explicit

    var assistances = GameSettings()

    var appSettings = AppSettings()

    constructor(id: Int, name: String) : this(id, name, ProfileStatistics(IntArray(Statistics.entries.size)))

    fun hasCurrentGame(): Boolean = currentGame != ProfileManager.NO_GAME
}
