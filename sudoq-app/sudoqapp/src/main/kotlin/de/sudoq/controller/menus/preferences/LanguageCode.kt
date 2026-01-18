package de.sudoq.controller.menus.preferences

import android.util.Log

/**
 * The LanguageCode enum represents the languages which the user can choose.
 * The user may enforce one of these three or follow the system default.
 */
enum class LanguageCode(val value: String) {
    SYSTEM("system"),
    GERMAN("de"),
    ENGLISH("en"),
    FRENCH("fr");

    companion object {

        /**
         * Returns the LanguageCode for the given code, if the code is not supported.
         * This method is meant to parse the system language preferences.
         *
         * @param code the language setting code (system, de, en, fr)
         * @return the LanguageCode for that code, or system if the code is not supported
         */
        fun parseFromCode(code: String): LanguageCode {
            return entries.find { it.value == code } ?: SYSTEM.also {
                Log.d("SudoQLanguage",
                    "Invalid LanguageCode code, defaulting to system. Supplied code: '$code'")
            }
        }
    }
}