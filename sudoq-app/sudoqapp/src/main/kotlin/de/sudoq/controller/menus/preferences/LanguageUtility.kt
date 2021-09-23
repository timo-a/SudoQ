package de.sudoq.controller.menus.preferences

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.util.Log
import de.sudoq.controller.menus.preferences.LanguageSetting.LanguageCode
import java.util.*

object LanguageUtility {

    val SUDOQ_SHARED_PREFS_FILE : String = "SudoqSharedPrefs";

    /* save language to enum */
    @JvmStatic
    fun loadLanguageFromSharedPreferences(a: Activity): LanguageSetting {
        val sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, MODE_PRIVATE)
        val code = sp.getString("language", "system")
        val langEnum = LanguageCode.valueOf(code!!)
        if (langEnum == LanguageCode.system) {
            return resolveSystemLocale()
        } else {
            return LanguageSetting(langEnum, false)
        }
    }

    /**
     * @param langSetting
     */
    @JvmStatic
    fun storeLanguageToSharedPreferences(a: Activity, langSetting: LanguageSetting) {
        val langEnum = if (langSetting.isSystemLanguage) LanguageCode.system else langSetting.language
        val sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, MODE_PRIVATE)
        sp.edit()
            .putString("language", langEnum.name)
            .apply()
    }

    //can be 'system'
    @JvmStatic
    fun getConfLocale(a: Activity): String {
        val res = a.resources
        val conf = res.configuration
        val code = conf.locale.language
        return code;
    }

    //can be 'system'
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
        return if (system) resolveSystemLocale() else LanguageSetting(i, false)
    }

    fun resolveSystemLocale(): LanguageSetting {
        val defaultCode = Locale.getDefault().language //TODO won't this always return en?
        LanguageCode.values().forEach { if (it.name == defaultCode) return LanguageSetting(it, true)}
        return LanguageSetting(LanguageCode.en, true)
    }
}