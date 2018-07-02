package com.mediacleaner

import com.mediacleaner.DataModels.Settings
import com.mediacleaner.Utils.Logger
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*
import java.io.File



class Config () {
    private val dir = File(System.getProperty("java.class.path")).absoluteFile.parentFile.toString()
    private val propertiesFile =  dir + System.getProperty("file.separator") + "Config.properties"

    fun saveConfig(settings: Settings) {
        val properties = Properties()
        properties.put("interval", settings.interval.toString())
        properties.put("hoursToKeep", settings.hoursToKeep.toString())
        properties.put("episodesToKeep", settings.episodesToKeep.toString())
        properties.put("keepFavoriteEpisodes", settings.keepFavoriteEpisodes.toString())
        properties.put("mediaServer", settings.mediaServer.toString())
        properties.put("debug", settings.debug.toString())
        properties.put("trace", settings.trace.toString())
        properties.put("logFile", settings.logFile)

        properties.put("sonarrApiKey", settings.sonarrApiKey)
        properties.put("sonarrAddress", settings.sonarrAddress)

        properties.put("embyAddress", settings.embyAddress)
        properties.put("embyUserName", settings.embyUserName)
        properties.put("embyUserId", settings.embyUserId)
        properties.put("embyAccessToken", settings.embyAccessToken)

        properties.put("plexAddress", settings.plexAddress)
        properties.put("plexUsername", settings.plexUsername)
        properties.put("plexUuid", settings.plexUuid)
        properties.put("plexAccessToken", settings.plexAccessToken)
        properties.put("plexClientToken", settings.plexClientToken)

        // Save to file.
        val fileOutputStream = FileOutputStream(propertiesFile)
        properties.store(fileOutputStream, "I dont recommend changing this file if you dont know what you are doing!")
    }

    fun getConfig(): Settings {
        val properties = Properties()
        val reader = FileReader(propertiesFile)
        properties.load(reader)

        return Settings(
                properties.getProperty("interval").toInt(),
                properties.getProperty("hoursToKeep").toInt(),
                properties.getProperty("episodesToKeep").toInt(),
                properties.getProperty("keepFavoriteEpisodes").toBoolean(),
                properties.getProperty("mediaServer").toInt(),
                properties.getProperty("debug").toBoolean(),
                properties.getProperty("trace").toBoolean(),
                properties.getProperty("logFile"),

                properties.getProperty("sonarrApiKey"),
                properties.getProperty("sonarrAddress"),

                properties.getProperty("embyAddress"),
                properties.getProperty("embyUserName"),
                properties.getProperty("embyUserId"),
                properties.getProperty("embyAccessToken"),

                properties.getProperty("plexAddress"),
                properties.getProperty("plexUsername"),
                properties.getProperty("plexUuid"),
                properties.getProperty("plexAccessToken"),
                properties.getProperty("plexClientToken")
            )
    }
}