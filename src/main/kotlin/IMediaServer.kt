package com.mediacleaner

import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings

interface IMediaServer {
    var settings: Settings
    fun getItem(EpisodePath: String): Episode?
    fun checkConnection(): Boolean
    fun checkSettings(): Boolean
}