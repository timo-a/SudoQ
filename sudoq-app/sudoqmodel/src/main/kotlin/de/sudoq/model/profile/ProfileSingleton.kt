/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import de.sudoq.model.persistence.IRepo
import de.sudoq.model.persistence.xml.profile.IProfilesListRepo
import java.io.File

/**
 * This static class is a wrapper for the currently loaded player profile
 * which is maintained by SharedPreferences of the Android-API.
 * Some callers expect a singleton instance so for now we keep one around.
 * todo: replace all instances with manager
 */
class ProfileSingleton private constructor(f: File, pr: IRepo<Profile>, plr: IProfilesListRepo)
    : ProfileManager(f, pr, plr) {
//private constructor because class is static

    companion object {

        /**
         * Diese Methode gibt eine Instance dieser Klasse zurück, wird sie erneut
         * aufgerufen, so wird dieselbe Instanz zurückgegeben.
         *
         * @return Die Instanz dieses Profile Singletons
         */
        //@JvmStatic
        //@get:Synchronized
        fun getInstance(f: File, pr: IRepo<Profile>, plr: IProfilesListRepo): ProfileSingleton {
            if (instance == null || instance!!.profilesDir != f) {
                instance = ProfileSingleton(f, pr, plr)
                instance!!.loadCurrentProfile()
            }

            return instance!!
        }

        private var instance: ProfileSingleton? = null

    }
}