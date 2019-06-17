package com.mediacleaner.app

import com.mediacleaner.DataModels.Settings
import com.mediacleaner.RestClients.EmbyRestClient
import com.mediacleaner.Utils.ConsoleRead
import javax.xml.ws.http.HTTPException
import java.util.*

class Settings(private val properties: Properties, private val settings: Settings) {
    fun getSettings(): Properties {
        properties.put("keepLastSeason", ConsoleRead.getBoolean("Should we keep the last watched season", settings.keepLastSeason).toString())
        properties.put("episodesToKeep", ConsoleRead.getInt("How many episodes should we keep", settings.episodesToKeep).toString())
        properties.put("hoursToKeep", ConsoleRead.getInt("How long should we keep them (hours)", settings.hoursToKeep).toString())
        properties.put("interval", ConsoleRead.getInt("How much time should be between two cleanings", settings.interval).toString())
        properties.put("keepFavoriteEpisodes", ConsoleRead.getBoolean("Should we keep your favorite episodes? (Note: Not all Media Servers support this.)", settings.keepFavoriteEpisodes).toString())
        properties.put("debug", ConsoleRead.getBoolean("Debug mode", false).toString())
        properties.put("trace", ConsoleRead.getBoolean("Trace mode", false).toString())
        properties.put("logFile", settings.logFile)
        properties.put("deleteMethod", ConsoleRead.getInt("Delete method (0=Default, 1=Sonarr. Note: If you have Sonarr, i would recommend using that option.)", settings.deleteMethod, listOf(0, 1)).toString())

        return properties
    }
}