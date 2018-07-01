package com.mediacleaner.app

import com.mediacleaner.DataModels.Settings

class Settings(private val settings_og: Settings) {
    fun getSettings(): Settings {
        val settings = settings_og
        // TODO: get input for all settings
        settings.sonarrApiKey = getString("Sonarr API Key", settings.sonarrApiKey)

        println(settings.sonarrApiKey)


        // Would you like to save the new settings?
        // Y/n

        // yes
        return settings
        // no
        // return settings_og
    }

    private fun getString(msg: String, default: String): String {
        print("$msg [$default]: ")
        val input = readLine()
        if(input == "")
            return default
        return input!!
    }
}