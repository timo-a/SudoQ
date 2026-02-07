package de.sudoq.model.profile

import de.sudoq.model.sudoku.complexity.Complexity

class ProfileStatistics(val array: IntArray/* todo use EnumMap?*/) {

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
        array[stat.ordinal] = value
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
    fun getStatistic(stat: Statistics): Int = array[stat.ordinal]

    fun updateAfterWin(complexity: Complexity, time: Int, score: Int) {
        val statisticToIncrement = when (complexity) {
            Complexity.infernal -> Statistics.playedInfernalSudokus
            Complexity.difficult -> Statistics.playedDifficultSudokus
            Complexity.medium -> Statistics.playedMediumSudokus
            Complexity.easy -> Statistics.playedEasySudokus
            Complexity.arbitrary -> throw IllegalStateException("unexpected complexity value: 'arbitrary'")
        }
        incrementStatistic(statisticToIncrement)
        incrementStatistic(Statistics.playedSudokus)
        if (getStatistic(Statistics.fastestSolvingTime) > time) {
            setStatistic(Statistics.fastestSolvingTime, time)
        }
        if (getStatistic(Statistics.maximumPoints) < score) {
            setStatistic(Statistics.maximumPoints, score)
        }
    }
    private fun incrementStatistic(s: Statistics) = setStatistic(s, getStatistic(s) + 1)

    init {
        require(array.size == Statistics.entries.size)
    }
}