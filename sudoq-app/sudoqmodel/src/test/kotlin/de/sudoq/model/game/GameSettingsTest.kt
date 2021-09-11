package de.sudoq.model.game

import org.amshove.kluent.`should be false`
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GameSettingsTest {
    @Test
    fun `clear should clear`() {
        val a = GameSettings()
        a.setAssistance(Assistances.markWrongSymbol)

        //confirm state
        a.getAssistance(Assistances.markWrongSymbol).`should be true`()

        a.clearAssistance(Assistances.markWrongSymbol)

        a.getAssistance(Assistances.markWrongSymbol).`should be false`()
    }

}