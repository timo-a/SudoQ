package de.sudoq.model.profile

import de.sudoq.model.game.GameSettings

class Profile(val id: Int, var name: String) {

    var currentGame: Int = ProfileManager.NO_GAME //todo this field should be nullable, to make "no game" explicit

    var assistances = GameSettings()

    var statistics: IntArray = IntArray(Statistics.entries.size)

    var appSettings = AppSettings()
}
