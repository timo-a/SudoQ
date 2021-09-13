package de.sudoq.controller.menus.preferences

import android.util.Log

//at runtime we need both the concrete language to check if it changed
// but also whether we follow the system language, in case that changes
class LanguageSetting {
    enum class LanguageCode {
        system, de, en, fr
    }

    //may never be system!
    var language: LanguageCode
       get() {
           if (language == LanguageCode.system)
               throw java.lang.IllegalStateException("LanguageSetting.language is 'system'. This is not allowed")
           else
               return language
       }
       set(value) {
           if (value == LanguageCode.system)
               throw java.lang.IllegalArgumentException("LanguageSetting.language may not be set to 'system'.")

           field = value
       }

    var isSystemLanguage: Boolean
        private set

    constructor() {
        language = LanguageCode.en
        isSystemLanguage = true
    }

    constructor(language: LanguageCode, systemLanguage: Boolean) {
        this.language = language
        isSystemLanguage = systemLanguage
    }

    //private static LanguageSetting makeDefault(){}
    fun toStorableString(): String {
        return if (isSystemLanguage) {
            "system_" + language.name
        } else language.name
    }

    override fun equals(obj: Any?): Boolean {
        throw IllegalStateException("Method should not be used!")
    }

    override fun toString(): String {
        return (if (isSystemLanguage) "system" else "fixed") + ", " + language.name
    }

    companion object {
            /**
             * parse a string loaded from shared prefs
             */
            fun fromStorableString(s: String?): LanguageSetting {
            //format: de or system_fr
            var s = s
            var ls = LanguageSetting()
            if (s!!.length == 2) {
                ls = parseLanguageSettings(s, false);

            } else if (s.startsWith("system_")) {
                val s2 = s.substring("system_".length)
                ls = parseLanguageSettings(s2, true);

            } else {
                ls = LanguageSetting()
                Log.d("lang", "Value from SharedPreferences $s has unsupported format, " +
                        "defaulting to system language.");
            }
            return ls
        }

        private fun parseLanguageSettings(
            languageString: String,
            systemLanguage: Boolean
        ): LanguageSetting {
            val lc = parseEnum(languageString)
            return if (lc != null) {
                LanguageSetting(lc, systemLanguage)
            } else {
                Log.d(
                    "lang",
                    "Value from SharedPreferences not recognized, defaulting to system language."
                )
                LanguageSetting()
            }
        }

        private fun parseEnum(s: String): LanguageCode? {
            for (lc in LanguageCode.values()) {
                if (lc.name == s)
                    return LanguageCode.valueOf(s)
            }
            Log.d("lang", "cannot parse $s to language string.")
            return null
        }

    }
}