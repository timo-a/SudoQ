package de.sudoq.model.persistence.xml.profile

import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import de.sudoq.model.xml.Xmlable

class ProfilesListBE : ArrayList<ProfilesListBE.P>(), Xmlable {

    private var currentProfileId: Int? = null

    data class P(var id: Int, var name: String)

    override fun toXmlTree(): XmlTree {
        val xmlTree = XmlTree("profiles")//as defined in FileManager
        xmlTree.addAttribute(XmlAttribute(CURRENT, currentProfileId.toString()))
        forEach {
            val profileTree = XmlTree("profile")
            profileTree.addAttribute(XmlAttribute(ID, it.id.toString()))
            profileTree.addAttribute(XmlAttribute(NAME, it.name))
            xmlTree.addChild(profileTree)
        }
        return xmlTree
    }

    override fun fillFromXml(xmlTreeRepresentation: XmlTree) {
        currentProfileId = xmlTreeRepresentation.getAttributeValue(CURRENT)!!.toInt()
        xmlTreeRepresentation.forEach {
            val id = it.getAttributeValue(ID)!!.toInt()
            val name = it.getAttributeValue(NAME)!!
            add(P(id, name))
        }
    }

    companion object {
        private const val ID = "id"
        private const val NAME = "name"
        private const val CURRENT = "current"

    }

}