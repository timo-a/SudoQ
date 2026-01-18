package de.sudoq.model.profile

import de.sudoq.model.game.GameSettings

class Profile(val id: Int, var name: String) {

    var currentGame: Int = ProfileManager.NO_GAME //todo this field should be nullable, to make "no game" explicit

    var assistances = GameSettings()

    //todo can it be val? do we need to reassign the array?
    //todo make private, or EnumMap?
    var statistics: IntArray = IntArray(Statistics.entries.size)//todo do not expose the ints, but the enums
    /**
     * Setzt den Wert der gegebenen Statistik für dieses Profil auf den
     * gegebenen Wert
     *
     * @param stat
     * die zu setzende Statistik
     * @param value
     * der einzutragende Wert
     */
    fun setStatistic(stat: Statistics, value: Int) {
        statistics[stat.ordinal] = value
    }
    /**
     * Diese Methode gibt den Wert der spezifizierten Statistik im aktuellen
     * Spielerprofil zurück. Ist die spezifizierte Statistik ungültig, so wird
     * null zurückgegeben.
     *
     * @param stat
     * Die Statistik, dessen Wert abgerufen werden soll
     * @return Der Wert der spezifizierten Statistik als String, oder null falls
     * diese ungültig ist
     */
    fun getStatistic(stat: Statistics): Int = statistics[stat.ordinal]

    var appSettings = AppSettings()
}
