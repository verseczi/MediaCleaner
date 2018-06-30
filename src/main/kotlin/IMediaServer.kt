package com.mediacleaner

import com.mediacleaner.DataModels.Episode

interface IMediaServer {
    fun getItem(EpisodePath: String): Episode?
    fun checkConnection(): Boolean
    fun checkSettings(): Boolean
}