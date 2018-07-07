package com.mediacleaner

import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import java.util.*

interface IMediaServer {
    var properties: Properties
    var settings: Settings
    fun getEpisodeList(): List<Episode>                         // Get a list of <Episode>
    fun checkConnection(): Boolean                              // Check the connection to the media server
    fun checkSettings(): Boolean                                // Check the media server's settings if it has any
    fun readSettingsCLI(properties: Properties): Properties     // read media server settings from user
    fun removeSettings(properties: Properties): Properties      // remove media server specific settings from properties
}