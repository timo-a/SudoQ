package de.sudoq.model.persistence.xml.profile

import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.profile.AppSettings
import de.sudoq.model.profile.Statistics
import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable

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
                assistances = GameSettings()
                assistances.fillFromXml(sub)
            }
            if (sub.name == "appSettings") {
                appSettings = AppSettings()
                appSettings.fillFromXml(sub)
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
        representation.addChild(assistances.toXmlTree())
        for (stat in Statistics.values()) {
            representation.addAttribute(
                XmlAttribute(
                    stat.name,
                    statistics!![stat.ordinal].toString() + ""
                )
            )
        }
        representation.addChild(appSettings.toXmlTree())
        return representation
    }


}
