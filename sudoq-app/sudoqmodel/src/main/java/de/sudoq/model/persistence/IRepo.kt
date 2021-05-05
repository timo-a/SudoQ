package de.sudoq.model.persistence

interface IRepo<T> {

    fun save(t:T) : T

    fun load(id : Int) : T


}