/*
 * SudoQ is a Sudoku-App for Android Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model

/**
 * This interface is implemented by all model classes, that can be observed.
 * In case of a change they will notify all registered listeners.
 *
 * @param T type of the object that is passed in case of a change
 */
interface ObservableModel<T> {

    /**
     * Notifies all listeners.
     *
     * @param obj The object that has changed.
     */
    fun notifyListeners(obj: T)

    /**
     * Registers a listeners to be notified about all future changes.
     *
     * @param listener the listener to register
     */
    fun registerListener(listener: ModelChangeListener<T>)

    /**
     * Removes an listner. If listener is not found nothing happens.
     *
     * @param listener the listener to remove
     */
    fun removeListener(listener: ModelChangeListener<T>)
}