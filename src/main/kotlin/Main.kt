package com.mediacleaner

import com.mediacleaner.Utils.ConsoleRead
import com.mediacleaner.app.MediaCleaner
import com.mediacleaner.app.Settings
import com.mediacleaner.DataModels.Settings as SettingsModel

fun main(args: Array<String>) {
    try {
        if (args.contains("--settings")) {
            val settingsCLI = Settings(Config().getConfig()).getSettings()
            if (ConsoleRead.getBoolean("Would you like to save these settings?", false, listOf("Y", "n")))
                Config().saveConfig(settingsCLI)
        } else if (args.contains("--reset-settings")) {
            Config().saveConfig(SettingsModel())
            println("Your Config file has been reset successfully.")
        } else {
            val mediaCleaner = MediaCleaner(Config().getConfig())
            mediaCleaner.startTimer(mediaCleaner)
        }
    } catch (e: Exception) {
        println("Run the software with the \"--reset-settings\" argument to reset settings.")
        throw e
    }
}