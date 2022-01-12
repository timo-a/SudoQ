package de.sudoq.controller.menus.preferences

import android.util.Log

/**
 * The LanguageCode class represents the language setting which the users chooses.
 * There are three translations of this apps strings: English, German and French.
 * The user may enforce one of these three or follow the system default.
 * If the system default is not supported, the chosen language will be english.
 */
enum class LanguageCode {
    system,
    de,
    en,
    fr;

    companion object {
        /**
         * Returns the LanguageCode for the given language code, or the language code for english, if the language code is unknown.
         *
         * @param code the language code (de, en, fr, ...)
         * @return the LanguageCode representing this language code, or english if not supported
         * @throws IllegalArgumentException if the string 'system' was supplied
         */
        @JvmStatic
        fun getFromLanguageCode(code: String): LanguageCode {
            require(system.name != code) { "Invalid language code '" + system.name + "'!" }
            for (languageCode in values()) {
                if (languageCode.name == code) {
                    return languageCode
                }
            }
            //Default to english, if the language is not supported.
            return en
        }

        /**
         * Returns the LanguageCode for the given code, if the code is not supported.
         * This method is meant to parse the system language preferences.
         *
         * @param code the language setting code (system, de, en, fr)
         * @return the LanguageCode for that code, or system if the code is not supported
         */
        @JvmStatic
        fun getFromString(code: String): LanguageCode {
            for (languageCode in values()) {
                if (languageCode.name == code) {
                    return languageCode
                }
            }
            //Default to system, if the code is not supported.
            Log.d("SudoQLanguage", "Invalid LanguageCode code, defaulting to system. Supplied code: '$code'")
            return system
        }
    }
}