package de.sudoq.controller.menus.preferences;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//at runtime we need both the concrete language to check if it changed
// but also whether we follow the system language, in case that changes
public class LanguageSetting {


    public enum LanguageCode {system, de, en, fr}

    //may never be system!
    public LanguageCode language;

    private boolean systemLanguage;

    public LanguageSetting(){
        this.language = LanguageCode.en;
        this.systemLanguage = true;
    }

    public LanguageSetting(LanguageCode language, boolean systemLanguage) {
        this.language = language;
        this.systemLanguage = systemLanguage;
    }

    public boolean isSystemLanguage() {
        return systemLanguage;
    }


    /**
     * parse a string loaded from shared prefs
     * */
    public static LanguageSetting fromStorableString(String s){
        //format: de or system_fr
        LanguageSetting ls;

        if (s.length()==2){ //de,en,fr or unsupported like it,pl,...
            ls = parseLanguageSettings(s, false);
        } else if (s.startsWith("system_")){
            String s2 = s.substring("system_".length());
            ls = parseLanguageSettings(s2, true);

        } else {
            ls = new LanguageSetting();
            Log.d("lang", "Value from SharedPreferences " + s + "has unsupported format, defaulting to system language.");
        }
        return ls;
    }

    private static LanguageSetting parseLanguageSettings(String languageString, boolean systemLanguage){
        LanguageCode lc = parseEnum(languageString);

        if (lc != null) {
            return new LanguageSetting(lc, systemLanguage);
        } else {
            Log.d("lang", "Value from SharedPreferences not recognized, defaulting to system language.");
            return new LanguageSetting();
        }
    }
    private static LanguageCode parseEnum(String s){
        for (LanguageCode lc: LanguageCode.values()) {
            if (lc.name().equals(s))
                return LanguageCode.valueOf(s);
        }
        Log.d("lang", "cannot parse " + s + " to language string.");
        return null;
    }

    //private static LanguageSetting makeDefault(){}

    public String toStorableString(){
        if (isSystemLanguage()){
            return "system_" + language.name();
        }else
            return language.name();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        throw new IllegalStateException("Method should not be used!");
    }

    @NonNull
    @Override
    public String toString() {
        return (isSystemLanguage() ? "system" : "fixed") + ", " + language.name();
    }
}
