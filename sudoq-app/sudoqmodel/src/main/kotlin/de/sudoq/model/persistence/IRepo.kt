package de.sudoq.model.persistence

interface IRepo<T> {

    fun create(): T

    fun read(id: Int): T

    fun update(t: T): T

    fun delete(id: Int)

    fun ids(): List<Int>
}