/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import java.io.File

/**
 * This static class is a wrapper for the currently loaded player profile
 * which is maintained by SharedPreferences of the Android-API.
 *
 */
class ProfileSingleton private constructor(f: File) : ProfileManager(f) {
//private constructor because class is static
//TODO split into profile handler and profile

    // Profiles todo move all to profileManager
    companion object {

        /**
         * Diese Methode gibt eine Instance dieser Klasse zurück, wird sie erneut
         * aufgerufen, so wird dieselbe Instanz zurückgegeben.
         *
         * @return Die Instanz dieses Profile Singletons
         */
        //@JvmStatic
        //@get:Synchronized
        fun getInstance(f: File): ProfileSingleton {
            if (instance == null || instance!!.profilesDir != f) {
                instance = ProfileSingleton(f)
                instance!!.loadCurrentProfile()
            }

            return instance!!
        }

        private var instance: ProfileSingleton? = null

    }
}