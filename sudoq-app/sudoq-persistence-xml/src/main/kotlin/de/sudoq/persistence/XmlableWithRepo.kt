package de.sudoq.persistence

import de.sudoq.model.persistence.IRepo

/**
 * A class that can be (de-)serialised to/from XML.
 * This one includes a repo in the signature from which further data can be loaded.
 */
interface XmlableWithRepo<T> {

    /**
     * Creates an XmlTree Objekt, which contains all persist-worthy attributes.
     *
     * @return [XmlTree] representation of the implementing class
     */
    fun toXmlTree(): XmlTree?

    /**
     * Loads data from an xml representation into the implementing class
     *
     * @param xmlTreeRepresentation Representation of the implementing class.
     * @throws IllegalArgumentException if the XML Representation has an unsupported structure.
     */
    @Throws(IllegalArgumentException::class)
    fun fillFromXml(xmlTreeRepresentation: XmlTree, repo: IRepo<T>)
}