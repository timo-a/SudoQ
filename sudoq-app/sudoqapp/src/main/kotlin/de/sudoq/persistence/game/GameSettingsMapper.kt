package de.sudoq.persistence.game

import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.persistence.sudoku.sudokuTypes.SudokuTypesListBE
import java.util.EnumMap

object GameSettingsMapper {

    fun toBE(gs: GameSettings): GameSettingsBE {
        val assistances = EnumMap<Assistances, Boolean>(Assistances::class.java)
        Assistances.entries
            .filter(gs::getAssistance)
            .forEach {
                assistances[it] = true
        }
        return GameSettingsBE(
            assistances,
            gs.isLeftHandModeSet,
            gs.isHelpersSet,
            gs.isGesturesSet,
            SudokuTypesListBE(gs.wantedTypesList)
        )
    }

    fun fromBE(gs: GameSettingsBE): GameSettings {
        val assistances = EnumMap<Assistances, Boolean>(Assistances::class.java)
        Assistances.entries
            .filter(gs::getAssistance)
            .forEach { assistances[it] = true }

        return GameSettings(
            assistances,
            gs.isLefthandModeSet,
            gs.isHelperSet,
            gs.isGesturesSet,
            gs.wantedTypesList)
    }
}