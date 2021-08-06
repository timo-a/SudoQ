package de.sudoq.persistence.game

import de.sudoq.model.game.Assistances
import de.sudoq.model.xml.XmlTree
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.ParameterizedTest


class GameSettingsBETests {

    enum class Letter {A,B}

    @ParameterizedTest
    @EnumSource(value=Letter::class)
    fun `should not be null`(assistance: Assistances) {
        Assertions.assertNotNull(assistance)
    }


    @Nested
    inner class Assistance {

        var a = GameSettingsBE()

        /*@ParameterizedTest
        @EnumSource(value=Assistances::class, names = ["autoAdjustNotes", "markWrongSymbol"])
        fun `should set assistance`(assistance: Assistances) {
            var a = GameSettingsBE()
            a.setAssistance(assistance)
            //a.getAssistance(assistance).`should be true`()

        }*/
    }
    @Test
    fun test() {
        var a = GameSettingsBE()
        a.setAssistance(Assistances.autoAdjustNotes)
        a.setAssistance(Assistances.markWrongSymbol)
        Assertions.assertTrue(
            a.getAssistance(Assistances.autoAdjustNotes),
            "autoAdjustNotes has wrong value"
        )
        Assertions.assertTrue(
            a.getAssistance(Assistances.markWrongSymbol),
            "markWrongSymbol has wrong value"
            )
        Assertions.assertFalse(
            a.getAssistance(Assistances.markRowColumn),
            "markRowColumn has wrong value"
        )
        Assertions.assertFalse(
            a.getAssistance(Assistances.restrictCandidates),
            "restrictCandidates has wrong value"
        )
        val t: XmlTree = a.toXmlTree()
        a = GameSettingsBE()
        a.fillFromXml(t)
        Assertions.assertTrue(
            a.getAssistance(Assistances.autoAdjustNotes),
            "autoAdjustNotes has wrong value"
        )
        Assertions.assertFalse(
            a.getAssistance(Assistances.markRowColumn),
            "markRowColumn has wrong value"
        )
        Assertions.assertTrue(
            a.getAssistance(Assistances.markWrongSymbol),
            "markWrongSymbol has wrong value"
        )
        Assertions.assertFalse(
            a.getAssistance(Assistances.restrictCandidates),
            "restrictCandidates has wrong value"
        )
        //TODO fix a.clearAssistance(Assistances.markWrongSymbol)
        Assertions.assertFalse(
            a.getAssistance(Assistances.markWrongSymbol),
            "markWrongSymbol has wrong value"
            )
    }

}