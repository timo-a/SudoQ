/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model

import java.util.*

/**
 * Default Implementation for ObservableModel
 *
 * @param T type of the changed object
 */
abstract class ObservableModelImpl<T> : ObservableModel<T> {
    private val listeners: MutableList<ModelChangeListener<T>> = ArrayList()

    /**
     * {@inheritDoc}
     */
    override fun notifyListeners(obj: T) {
        for (l in listeners) l.onModelChanged(obj)
    }

    /**
     * {@inheritDoc}
     */
    override fun registerListener(listener: ModelChangeListener<T>) {
        listeners.add(listener)
    }

    /**
     * {@inheritDoc}
     */
    override fun removeListener(listener: ModelChangeListener<T>) {
        listeners.remove(listener)
    }
}