package com.mediacleaner

import com.mediacleaner.DataModels.Settings
import com.mediacleaner.RestClients.SonarrRestClient
import com.mediacleaner.Utils.ConsoleRead
import java.util.*

class Sonarr(val properties: Properties, val settings: Settings) {
    val settings_sonarr = getSettings()
    val sonarrRestClient = SonarrRestClient(settings, settings_sonarr)

    fun deleteEpisode(filePath: String) {
        try {
            val seriesList = sonarrRestClient.getSeriesList()
            if (seriesList != null) {
                for (series in seriesList) {
                    val episodeList = sonarrRestClient.getEpisodebySeries(series.id.toString())

                    if (episodeList != null) {
                        val episode = episodeList.firstOrNull { it.episodeFile?.path == filePath }
                        if (episode != null) {
                            sonarrRestClient.deleteEpisodeFile(episode.episodeFileId)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun checkAPIKey(): Boolean {
        try {
            return sonarrRestClient.checkAPIKey()
        } catch (e: Exception) {
            throw e
        }
    }

    fun checkConnection(): Boolean {
        try {
            return sonarrRestClient.checkConnection()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getSettings(): sonarrSettings {
        return try {
            sonarrSettings (
                    properties.getProperty("sonarrAddress"),
                    properties.getProperty("sonarrAPIKey")
            )
        } catch (e: Exception) {
            sonarrSettings()
        }
    }

    fun removeSettings(properties: Properties): Properties {
        properties.remove("sonarrAddress")
        properties.remove("sonarrAPIKey")

        return properties
    }

    fun getSettingsCLI(properties: Properties): Properties {
        settings_sonarr.Address = ConsoleRead.getString("Sonarr Address", settings_sonarr.Address)
        settings_sonarr.APIKey = ConsoleRead.getString("Sonarr API Key", settings_sonarr.APIKey)

        properties.put("sonarrAddress", settings_sonarr.Address)
        properties.put("sonarrAPIKey", settings_sonarr.APIKey)

        return properties
    }

    fun checkSettings(): Boolean {
        try {
            properties.getProperty("sonarrAddress")
            properties.getProperty("sonarrAPIKey")
        } catch (e: Exception) {
            throw e
        }

        if(settings_sonarr.Address == "" || settings_sonarr.APIKey == "")
            return false

        return true
    }

    data class sonarrSettings (
        var Address: String = "http://127.0.0.1:8989",
        var APIKey: String = ""
    )
}