package com.mediacleaner

import com.mediacleaner.Utils.ConsoleRead
import com.mediacleaner.app.MediaCleaner
import com.mediacleaner.app.Settings
import java.io.File
import java.io.FileReader
import java.util.*
import com.mediacleaner.DataModels.Settings as SettingsModel

fun main(args: Array<String>) {
    val config = Config()

    try {
        if (args.contains("--settings")) {
            var properties = config.getPropertiesFromFile()
            val settings_og = config.getSettings(properties)

            // Main Settings, including media server type
            properties = Settings(properties, settings_og).getSettings()
            config.getSettings(properties)
            val settings = config.getSettings(properties)

            // Sonarr
            if(settings.deleteMethod == 1) {
                val sonarr = Sonarr(properties, settings)
                properties = sonarr.getSettingsCLI(properties)
            }
            else {
                if(settings_og.deleteMethod != settings.deleteMethod) {
                    val sonarr = Sonarr(properties, settings)
                    properties = sonarr.removeSettings(properties)
                }
            }

            // Media Server settings
            val mServer = MediaServer(properties, settings)
            mServer.checkSettings()
            properties = mServer.readSettingsCLI(properties)


            println(properties)

            // Save settings
            if (ConsoleRead.getBoolean("Would you like to save these settings?", false, listOf("Y", "n")))
                config.saveConfig(properties)

        } else if (args.contains("--reset-settings")) {
            config.saveConfig(Properties())
            println("Your Config file has been reset successfully.")
        } else {
            val properties = config.getPropertiesFromFile()
            val settings = config.getSettings(properties)
            val mServer = MediaServer(properties, settings)
            if(mServer.checkSettings())
            {
                val mediaCleaner = MediaCleaner(mServer, settings)
                mediaCleaner.startTimer(mediaCleaner)
            }
            else
                println("Your settings are invalid. Run the software with the \"--settings\" argument.")
        }
    } catch (e: Exception) {
        println("Run the software with the \"--reset-settings\" argument to reset settings.")
        throw e
    }
}