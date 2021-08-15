package de.sudoq.persistence.profile

import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.AppSettings
import de.sudoq.model.profile.Statistics
import de.sudoq.persistence.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.persistence.Xmlable
import de.sudoq.persistence.game.GameSettingsBE
import de.sudoq.persistence.game.GameSettingsMapper

class ProfileBE(val id: Int) : Xmlable {

    var currentGame: Int = 0

    var name: String? = null

    var assistances = GameSettings()

    var statistics: IntArray? = null

    var appSettings = AppSettings()

    constructor(id: Int, currentGame: Int, name: String, assistances: GameSettings,
                statistics: IntArray, appSettings: AppSettings) : this(id) {
        this.currentGame = currentGame
        this.name = name
        this.assistances = assistances
        this.statistics = statistics
        this.appSettings = appSettings
    }

    /**
     * {@inheritDoc}
     */
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        currentGame = xmlTreeRepresentation.getAttributeValue("currentGame")!!.toInt()
        name = xmlTreeRepresentation.getAttributeValue("name")
        for (sub in xmlTreeRepresentation) {
            if (sub.name == "gameSettings") {
                val gameSettingsBE = GameSettingsBE()
                gameSettingsBE.fillFromXml(sub)
                assistances = GameSettingsMapper.fromBE(gameSettingsBE)
            }
            if (sub.name == "appSettings") {
                val appSettingsBE = AppSettingsBE()
                appSettingsBE.fillFromXml(sub)
                appSettings = AppSettingsMapper.fromBE(appSettingsBE)
            }
        }
        statistics = IntArray(Statistics.values().size)
        for (stat in Statistics.values()) {
            statistics!![stat.ordinal] =
                xmlTreeRepresentation.getAttributeValue(stat.name)!!.toInt()
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("profile")
        representation.addAttribute(XmlAttribute("id", id.toString()))
        representation.addAttribute(XmlAttribute("currentGame", currentGame.toString()))
        representation.addAttribute(XmlAttribute("name", name!!))
        representation.addChild(GameSettingsMapper.toBE(assistances).toXmlTree())
        for (stat in Statistics.values()) {
            representation.addAttribute(
                XmlAttribute(
                    stat.name,
                    statistics!![stat.ordinal].toString() + ""
                )
            )
        }
        representation.addChild(AppSettingsMapper.toBE(appSettings).toXmlTree())
        return representation
    }


}
