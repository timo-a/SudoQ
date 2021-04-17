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


    public static LanguageSetting fromStorableString(String s){
        //format: de or system_fr
        LanguageSetting ls = new LanguageSetting();

        if (s.length()==2){
            try{
                ls = new LanguageSetting(LanguageCode.valueOf(s), false);
            } catch(IllegalArgumentException e) {
                Log.d("lang", "Value from SharedPreferences " + s + " not recognized, defaulting to system language.");

                ls = new LanguageSetting();
            }
        } else if (s.startsWith("system_")){
            s = s.substring("system_".length());
            try {
                ls = new LanguageSetting(LanguageCode.valueOf(s),true);
            } catch(IllegalArgumentException e){
                ls = new LanguageSetting();
            }
        } else {
            ls = new LanguageSetting();
        }
        return ls;
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
