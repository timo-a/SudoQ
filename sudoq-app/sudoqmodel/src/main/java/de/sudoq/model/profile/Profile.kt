/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.model.profile

import de.sudoq.model.ObservableModelImpl
import de.sudoq.model.files.FileManager
import de.sudoq.model.game.Assistances
import de.sudoq.model.game.GameSettings
import de.sudoq.model.persistence.xml.ProfileBE
import de.sudoq.model.persistence.xml.ProfileRepo
import de.sudoq.model.xml.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * This static class is a wrapper for the currently loaded player profile
 * which is maintained by SharedPreferences of the Android-API.
 *
 */
class Profile() : ProfileManager() {
//private constructor because class is static
//TODO split into profile handler and profile



    constructor(profileRepo: ProfileRepo) : this() {
        this.profileRepo = profileRepo
    }

    /**
     * Diese Methode erstellt ein neues Profil.
     */
    private fun createProfile() {    }


    companion object {
        const val INITIAL_TIME_RECORD = 5999

        /**
         * Konstante die signalisiert, dass es kein aktuelles Spiel gibt
         */
        const val NO_GAME = -1

        /**
         * Konstante die signalisiert, dass ein neues Profil noch keinen namen hat
         */
        const val DEFAULT_PROFILE_NAME = "unnamed"

        /**
         * Diese Methode gibt eine Instance dieser Klasse zurück, wird sie erneut
         * aufgerufen, so wird dieselbe Instanz zurückgegeben.
         *
         * @return Die Instanz dieses Profile Singletons
         */
        //@JvmStatic
		//@get:Synchronized
        var instance: Profile?
        init {
            instance = Profile()
            instance!!.loadCurrentProfile()
        }

        fun forceReinitialize() : Profile {
            instance = Profile()
            instance!!.loadCurrentProfile();
            return instance!!
        }


    }
}