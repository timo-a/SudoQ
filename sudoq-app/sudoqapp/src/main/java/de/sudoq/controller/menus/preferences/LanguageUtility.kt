package de.sudoq.controller.menus.preferences

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.util.Log
import de.sudoq.controller.menus.preferences.LanguageSetting.LanguageCode
import java.util.*

object LanguageUtility {

    val SUDOQ_SHARED_PREFS_FILE : String = "SudoqSharedPrefs";

    /* save languagesetting to two values*/
    fun loadLanguageFromSharedPreferences(a: Activity): LanguageSetting {
        val sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, MODE_PRIVATE)
        val code = sp.getString("language", "system_en")
        return LanguageSetting.fromStorableString(code)
    }

    /* save language to enum */
    @JvmStatic
    fun loadLanguageFromSharedPreferences2(a: Activity): LanguageSetting {
        val sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, MODE_PRIVATE)
        val code = sp.getString("language", "system")
        val langEnum = LanguageCode.valueOf(code!!)
        return if (langEnum == LanguageCode.system) {
            LanguageSetting(loadLanguageFromLocale(), true)
        } else {
            LanguageSetting(langEnum, false)
        }
    }

    /**
     * @param langSetting
     */
    @JvmStatic
    fun storeLanguageToMemory2(a: Activity, langSetting: LanguageSetting) {
        val langEnum = if (langSetting.isSystemLanguage) LanguageCode.system else langSetting.language
        val sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, MODE_PRIVATE)
        sp.edit()
            .putString("language", langEnum.name)
            .apply()
    }

    /* save language to enum */
    @JvmStatic
    fun loadLanguageFromLocale(): LanguageCode {
        val code = Locale.getDefault().language
        LanguageCode.values().forEach { if (it.name == code) return it}
        return LanguageCode.en
    }

    @JvmStatic
    fun getConfLocale(a: Activity): LanguageCode {
        val res = a.resources
        val conf = res.configuration
        val code = conf.locale.language
        return LanguageCode.valueOf(code)
    }

    @JvmStatic
    fun setConfLocale(lang: String, a: Activity) {
        Log.i("lang", "setLocale( " + lang + ", " + a.javaClass.simpleName + ")")
        val myLocale = Locale(lang)
        val res = a.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    fun getLanguageFromItem(i: LanguageCode): LanguageSetting {
        val system = i == LanguageCode.system
        val language = if (system) LanguageCode.valueOf(Locale.getDefault().language) else i
        return LanguageSetting(language, system)
    }
}