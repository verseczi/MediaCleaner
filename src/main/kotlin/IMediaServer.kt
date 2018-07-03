package com.mediacleaner

import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings

interface IMediaServer {
    var settings: Settings
    fun getEpisodeList(): List<Episode>
    fun checkConnection(): Boolean
    fun checkSettings(): Boolean
}