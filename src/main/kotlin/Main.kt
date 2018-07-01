package com.mediacleaner

import com.mediacleaner.app.MediaCleaner
import com.mediacleaner.app.Settings

fun main(args: Array<String>) {
    val settings = Config().getConfig()
    if(args.contains("--settings"))
    {
        val settingsCLI = Settings(settings)
        settingsCLI.getSettings()
    }
    else
    {
        val mediaCleaner = MediaCleaner(settings)
        mediaCleaner.startTimer(mediaCleaner)
    }
}