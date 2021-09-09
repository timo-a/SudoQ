package de.sudoq.persistence.game

import de.sudoq.model.game.Assistances
import de.sudoq.model.game.Assistances.*
import de.sudoq.persistence.XmlTree
import org.amshove.kluent.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource


class GameSettingsBETests {

    @Nested
    inner class Assistance {

        var a = GameSettingsBE()

        @ParameterizedTest
        @EnumSource(value=Assistances::class, names = ["autoAdjustNotes", "markWrongSymbol"])
        fun `should set assistance`(assistance: Assistances) {
            var a = GameSettingsBE()
            a.setAssistance(assistance)
            a.getAssistance(assistance).`should be true`()
        }
    }

    @Test
    fun test() {
        var a = GameSettingsBE()
        a.setAssistance(autoAdjustNotes)
        a.setAssistance(markWrongSymbol)

        //confirm state
        a.getAssistance(autoAdjustNotes).`should be true`()
        a.getAssistance(markWrongSymbol).`should be true`()
        a.getAssistance(markRowColumn).`should be false`()
        a.getAssistance(restrictCandidates).`should be false`()

        //to xmlTree and back
        val t: XmlTree = a.toXmlTree()
        a = GameSettingsBE()
        a.fillFromXml(t)

        a.getAssistance(autoAdjustNotes).`should be true`()
        a.getAssistance(markRowColumn).`should be false`()
        a.getAssistance(markWrongSymbol).`should be true`()
        a.getAssistance(restrictCandidates).`should be false`()

    }

    @Test
    fun testFooString() {
        invoking { GameSettingsBE().fillFromXml(XmlTree("foo")) }
            .`should throw`(NullPointerException::class)
    }

}