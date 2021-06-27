package de.sudoq.controller.menus.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Locale;

import de.sudoq.model.profile.Profile;

public class LanguageUtility {

    private final static String SUDOQ_SHARED_PREFS_FILE = "SudoqSharedPrefs";

    /* save languagesetting to two values*/

    public static LanguageSetting loadLanguageFromSharedPreferences(Activity a){
        SharedPreferences sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE,Context.MODE_PRIVATE);
        String code = sp.getString("language", "system_en");

        return LanguageSetting.fromStorableString(code);
    }


    /* save language to enum */

    public static LanguageSetting loadLanguageFromSharedPreferences2(Activity a){


        SharedPreferences sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        String code = sp.getString("language", "system");
        LanguageSetting.LanguageCode langEnum = LanguageSetting.LanguageCode.valueOf(code);

        if (langEnum.equals(LanguageSetting.LanguageCode.system)){
            return new LanguageSetting(loadLanguageFromLocale(), true);
        } else {
            return new LanguageSetting(langEnum, false);
        }
    }

    /**
     * @param langSetting
     */
    public static void storeLanguageToMemory2(Activity a, LanguageSetting langSetting) {

        LanguageSetting.LanguageCode langEnum = langSetting.isSystemLanguage()
                ? LanguageSetting.LanguageCode.system
                : langSetting.language;

        SharedPreferences sp = a.getSharedPreferences(SUDOQ_SHARED_PREFS_FILE,Context.MODE_PRIVATE);
        sp.edit()
                .putString("language", langEnum.name())
                .apply();
    }

    /* save language to enum */


    public static LanguageSetting.LanguageCode loadLanguageFromLocale(){
        String code = Locale.getDefault().getLanguage();
        return LanguageSetting.LanguageCode.valueOf(code);
    }




    public static LanguageSetting.LanguageCode getConfLocale(Activity a){
        Resources res = a.getResources();
        Configuration conf = res.getConfiguration();
        String code = conf.locale.getLanguage();
        return LanguageSetting.LanguageCode.valueOf(code);

    }


    public static void setConfLocale(String lang, Activity a) {
        Log.i("lang","setLocale( "+lang+", " + a.getClass().getSimpleName() + ")");

        Locale myLocale = new Locale(lang);
        Resources res = a.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    public static LanguageSetting getLanguageFromItem(LanguageSetting.LanguageCode i){
        boolean system = i.equals(LanguageSetting.LanguageCode.system);
        LanguageSetting.LanguageCode language = system
                ? LanguageSetting.LanguageCode.valueOf(Locale.getDefault().getLanguage())
                : i;
        return new LanguageSetting(language, system);
    }


}
