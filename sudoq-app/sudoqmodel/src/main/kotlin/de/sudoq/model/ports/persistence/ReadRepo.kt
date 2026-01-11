package de.sudoq.model.ports.persistence

fun interface ReadRepo<T> {
    fun read(id: Int): T
}