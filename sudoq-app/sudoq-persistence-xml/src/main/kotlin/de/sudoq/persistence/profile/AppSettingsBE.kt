package de.sudoq.persistence.profile

import de.sudoq.persistence.XmlAttribute
import de.sudoq.persistence.XmlTree
import de.sudoq.persistence.Xmlable

class AppSettingsBE(var isDebugSet: Boolean = false,
                    var language: String = "dummyval"): Xmlable {

    /* to and from string */
    override fun toXmlTree(): XmlTree {
        val representation = XmlTree("appSettings")
        representation.addAttribute(XmlAttribute("debug", isDebugSet))
        representation.addAttribute(XmlAttribute("language", language))
        return representation
    }

    @Throws(IllegalArgumentException::class)
    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        isDebugSet =
            java.lang.Boolean.parseBoolean(xmlTreeRepresentation.getAttributeValue("debug"))
        language = xmlTreeRepresentation.getAttributeValue("language") ?: "system"
        //if language hasn't been used before it will be null -> assume system
    }
}