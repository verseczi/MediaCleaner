package com.mediacleaner

import com.mediacleaner.DataModels.Settings
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*
import java.io.File



class Config {
    private val dir = File(System.getProperty("java.class.path")).absoluteFile.parentFile.toString()
    private val propertiesFile =  dir + System.getProperty("file.separator") + "Config.properties"

    fun saveConfig(properties: Properties) {
        var prop = properties
        if (prop.isEmpty) {
            prop = getPropertiesFromSettings(Settings())
        }

        // Save to file.
        val fileOutputStream = FileOutputStream(propertiesFile)
        prop.store(fileOutputStream, "I dont recommend changing this file if you dont know what you are doing!")
    }

    fun getPropertiesFromFile(): Properties {
        val properties = Properties()
        val reader = FileReader(propertiesFile)
        properties.load(reader)

        return properties
    }

    fun getSettings(properties_: Properties): Settings {
        val properties = if(properties_.isEmpty)
            getPropertiesFromSettings(Settings())
        else
            properties_

        return Settings(
                properties.getProperty("interval").toInt(),
                properties.getProperty("hoursToKeep").toInt(),
                properties.getProperty("episodesToKeep").toInt(),
                properties.getProperty("keepFavoriteEpisodes").toBoolean(),
                properties.getProperty("mediaServer").toInt(),
                properties.getProperty("debug").toBoolean(),
                properties.getProperty("trace").toBoolean(),
                properties.getProperty("logFile"),
                properties.getProperty("deleteMethod").toInt(),
                properties.getProperty("excludedPhrases")
            )
    }

    fun getPropertiesFromSettings(settings: Settings): Properties {
        val properties = Properties()
        properties.put("interval", settings.interval.toString())
        properties.put("hoursToKeep", settings.hoursToKeep.toString())
        properties.put("episodesToKeep", settings.episodesToKeep.toString())
        properties.put("keepFavoriteEpisodes", settings.keepFavoriteEpisodes.toString())
        properties.put("mediaServer", settings.mediaServer.toString())
        properties.put("debug", settings.debug.toString())
        properties.put("trace", settings.trace.toString())
        properties.put("logFile", settings.logFile)
        properties.put("deleteMethod", settings.deleteMethod.toString())
        properties.put("excludedPhrases", settings.excludedPhrases)

        return properties
    }
}