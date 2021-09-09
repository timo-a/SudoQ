package de.sudoq.model.profile

import de.sudoq.model.game.GameSettings

class Profile(val id: Int) {

    var currentGame: Int = 0

    var name: String? = null

    var assistances = GameSettings()

    var statistics: IntArray? = null

    var appSettings = AppSettings()

}
