package de.sudoq.persistence.profile

import de.sudoq.model.profile.AppSettings

object AppSettingsMapper {

    fun toBE(appSettings: AppSettings): AppSettingsBE {
        return AppSettingsBE(
            appSettings.isDebugSet,
            appSettings.language)
    }

    fun fromBE(appSettingsBE: AppSettingsBE): AppSettings {
        val a = AppSettings()
        a.setDebug(appSettingsBE.isDebugSet)
        a.language = appSettingsBE.language
        return a
    }

}