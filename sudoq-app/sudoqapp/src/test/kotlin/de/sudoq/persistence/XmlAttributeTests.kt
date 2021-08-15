package de.sudoq.persistence

import org.amshove.kluent.*
import org.junit.jupiter.api.Test

class XmlAttributeTests {

    @Test
    fun testConstructorStringStringIllegalArgumentException2() {
        invoking { XmlAttribute("", "value") }
            .`should throw`(IllegalArgumentException::class)
    }

    @Test
    fun testGetName() {
        val attribute = XmlAttribute("xyzName", "")
        attribute.name `should be equal to` "xyzName"
    }

    @Test
    fun testGetValue() {
        val attribute = XmlAttribute("xyzName", "xyzValue")
        attribute.value `should be equal to`  "xyzValue"
    }

    @Test
    fun testIsSameAttribute() {
        val a = XmlAttribute("xyzName", "value")
        val b = XmlAttribute("xyzName","differentvalue")
        a.isSameAttribute(b).`should be true`()
    }
}