package de.sudoq.controller.menus.preferences

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import de.sudoq.controller.menus.preferences.LanguageCode.*

/**
 * This class provides several utility functions, for dealing with language management using AppCompat.
 */
object LanguageUtility {

    /**
     * Loads the [LanguageCode] currently selected by the user.
     *
     * @return the [LanguageCode] currently set, or system if no app-specific locale is set.
     */
    fun loadAppLanguage(): LanguageCode {
        val locales = AppCompatDelegate.getApplicationLocales()
        if (locales.isEmpty) {
            return SYSTEM
        }
        val firstLocale = locales.get(0) ?: return SYSTEM
        return LanguageCode.parseFromCode(firstLocale.language)
    }


    /**
     * Applies the given [LanguageCode].
     *
     * @param languageCode the [LanguageCode] to set
     */
    fun setAppLanguage(languageCode: LanguageCode) {
        val appLocale: LocaleListCompat = if (languageCode == SYSTEM) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageCode.value)
        }
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

}
