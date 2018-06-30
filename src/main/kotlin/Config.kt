package com.mediacleaner

import com.mediacleaner.DataModels.Settings
import com.mediacleaner.Utils.Logger
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*
import java.io.File



class Config () {
    val logger = Logger(this.javaClass.name)

    val dir = File(System.getProperty("java.class.path")).absoluteFile.parentFile.toString()
    val propertiesFile =  dir + System.getProperty("file.separator") + "Config.properties"

    fun saveSettings(settings: Settings) {
        val properties = Properties()
        println("yo?")
        properties.put("interval", settings.interval.toString())
        properties.put("hoursToKeep", settings.hoursToKeep.toString())
        properties.put("episodesToKeep", settings.episodesToKeep.toString())
        properties.put("keepFavoriteEpisodes", settings.keepFavoriteEpisodes.toString())
        properties.put("mediaServer", settings.mediaServer.toString())
        properties.put("debug", settings.debug.toString())
        properties.put("trace", settings.trace.toString())

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
        var fileOutputStream = FileOutputStream(propertiesFile)
        properties.store(fileOutputStream, "I dont recommend changing this file if you dont know what you are doing-")
    }

    fun getSettings(): Settings {
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