package de.sudoq.model.ports.persistence

interface ReadRepo<T> {
    fun read(id: Int): T
}