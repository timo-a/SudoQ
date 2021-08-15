package de.sudoq.persistence

import de.sudoq.model.xml.XmlAttribute
import de.sudoq.model.xml.XmlTree
import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class XmlTreeTests {

    @Test
    fun testGetName() {
        val tree = XmlTree("xyz")
        tree.name `should be equal to` "xyz"
    }

    @Test
    fun testGetContent() {
        val tree = XmlTree("xyz", "some content")
        tree.content `should be equal to` "some content"
    }

    @Test
    fun testGetNumberOfChildren() {
        val tree = XmlTree("root")
        tree.addChild(XmlTree("sub"))
        tree.addChild(XmlTree("sub"))
        tree.numberOfChildren `should be equal to` 2
    }

    @Test
    fun testGetAttributeValue() {
        val tree = XmlTree("root")
        val attribute = XmlAttribute("xyzName", "xyzValue")
        tree.addAttribute(attribute)
        tree.getAttributeValue("xyzName") `should be equal to` "xyzValue"
        tree.getAttributeValue("notExistingAttribute").`should not be null`()
    }

    @Test
    fun testGetAttributes() {
        val tree = XmlTree("root")
        val attribute1 = XmlAttribute("attribute1", "value1")
        val attribute2 = XmlAttribute("attribute2", "value2")
        tree.addAttribute(attribute1)
        tree.addAttribute(attribute2)
        tree.addAttribute(attribute2)
        var i = 0
        val iterator = tree.getAttributes()
        while (iterator.hasNext()) {
            val attribute = iterator.next()
            if (i == 0) {
                attribute.name `should be equal to` "attribute1"
                attribute.value `should be equal to` "value1"
            } else if (i == 1) {
                attribute.name `should be equal to` "attribute2"
                attribute.value `should be equal to` "value2"
            }
            i++
        }
        i `should be equal to` 2
    }

    @Test
    fun testGetChildren() {
        val tree = XmlTree("root")
        val subtree1 = XmlTree("sub")
        val subtree2 = XmlTree("sub")
        tree.addChild(subtree1)
        tree.addChild(subtree2)
        var i = 0
        val iterator: Iterator<XmlTree> = tree.getChildren()
        while (iterator.hasNext()) {
            iterator.next().name `should be equal to` "sub"
            i++
        }
        i `should be equal to` 2
    }

    @Test
    fun testUpdateAttribute1() {
        val tree = XmlTree("root")
        tree.addAttribute(XmlAttribute("attr", "first_value"))
        tree.updateAttribute(XmlAttribute("attr", "second_value"))
        tree.getAttributeValue("attr") `should be equal to` "second_value"
    }

    @Test
    fun testUpdateAttribute2() {
        val tree = XmlTree("root")
        tree.updateAttribute(XmlAttribute("attr", "first_value"))
        tree.getAttributeValue("attr") `should be equal to` "first_value"
    }
}