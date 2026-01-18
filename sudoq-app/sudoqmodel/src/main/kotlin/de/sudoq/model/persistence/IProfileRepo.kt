package de.sudoq.model.persistence

import de.sudoq.model.profile.Profile

interface IProfileRepo {//todo can it be generalized to game and sudoku?

    fun getFreeId(): Int

    fun create(t: Profile)

    fun read(id: Int): Profile


    fun update(t: Profile): Profile


    fun delete(id: Int)

    fun ids(): List<Int>
}