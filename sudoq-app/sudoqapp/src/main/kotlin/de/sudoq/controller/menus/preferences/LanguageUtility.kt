package de.sudoq.controller.menus.preferences

import android.app.Activity
import android.content.Context
import android.util.Log
import java.util.*

/**
 * This class provides several utility functions, for dealing with language management.
 */
object LanguageUtility {

    // ### Language preferences: ###

    /**
     * Name of the preferences.
     */
    private const val SUDOQ_SHARED_PREFS_FILE = "SudoqSharedPrefs"

    /**
     * The language key.
     */
    private const val LANGUAGE_KEY = "language"

    /**
     * Loads the [LanguageCode] from preferences. Defaults to system.
     *
     * @param context a [Context] of this application (any activity)
     * @return the [LanguageCode] stored in the settings, or system
     */
    @JvmStatic
    fun loadLanguageCodeFromPreferences(context: Context): LanguageCode {
        val sharedPreferences = context.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        val languageCodeOrSystem = sharedPreferences.getString(LANGUAGE_KEY, LanguageCode.system.name)
        return LanguageCode.getFromString(languageCodeOrSystem!!) // Null check pointless, but Kotlin does not like that we could potentially get null here.
    }

    /**
     * Stores a [LanguageCode] to preferences.
     *
     * @param context a [Context] of this application (any activity)
     * @param languageCode the [LanguageCode] to store in the preferences
     */
    @JvmStatic
    fun saveLanguageCodeToPreferences(context: Context, languageCode: LanguageCode) {
        val sp = context.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, Context.MODE_PRIVATE)
        sp.edit()
                .putString(LANGUAGE_KEY, languageCode.name)
                .apply()
    }

    // ### System language: ###

    /**
     * Finds the [LanguageCode] for the current system language. If the system language has no translation/is unknown English is chosen.
     *
     * @return the [LanguageCode] for the system language, or if unknown for english
     */
    @JvmStatic
    fun resolveSystemLanguage(): LanguageCode {
        val code = Locale.getDefault().language
        return LanguageCode.getFromLanguageCode(code)
    }

    // ### Resource language: ###

    /**
     * Gets the [LanguageCode] for the currently chosen resource language.
     * There are only 3 possible resource languages, if however the resource is something else, this returns English.
     *
     * @param context a [Context] of this application (any activity)
     * @return the [LanguageCode] which the resource is set to
     */
    @JvmStatic
    fun getResourceLanguageCode(context: Context): LanguageCode {
        val resources = context.resources
        val configuration = resources.configuration
        val languageCode = configuration.locale.language
        return LanguageCode.getFromLanguageCode(languageCode)
    }

    /**
     * Sets the locale of the resources to a provided language.
     *
     * @param context a [Context] of this application (any activity)
     * @param languageCode the [LanguageCode] which the resources should be set to
     * @throws IllegalArgumentException if the [LanguageCode] for system was supplied
     */
    @JvmStatic
    fun setResourceLocale(context: Context, languageCode: LanguageCode) {
        Log.d("SudoQLanguage", "Setting resource from context '" + context.javaClass.simpleName + "' to '" + languageCode + "'")
        require(languageCode != LanguageCode.system) { "The resource locale may never be set to system!" }
        val newLocale = Locale(languageCode.name)
        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = newLocale
        resources.updateConfiguration(configuration, displayMetrics)
    }

    // ### App language: ###

    /**
     * Returns the [LanguageCode] which the the app should currently be displaying.
     * If the setting points to system, the system language is resolved, fallback is English.
     *
     * @param context a [Context] of this application (any activity)
     * @return the [LanguageCode] representing the current language to use, never system
     */
    @JvmStatic
    fun getDesiredLanguage(context: Context): LanguageCode {
        val languageCode = loadLanguageCodeFromPreferences(context)
        return if (languageCode == LanguageCode.system) {
            //Load system language:
            resolveSystemLanguage()
        } else {
            languageCode
        }
    }
}