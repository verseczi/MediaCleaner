package com.mediacleaner

import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.MediaServers.Emby
import com.mediacleaner.Utils.Logger

class MediaServer (override var settings: Settings) : IMediaServer {
    val logger = Logger(this.javaClass.name, settings)
    var timestamp: Long = 0
    private var timestamp_last: Long = 0
    private lateinit var mServer: IMediaServer
    private var mServerT = settings.mediaServer

    init {
        initMediaServer()
    }


    fun getEpisodeListByOrder(episodeList_: List<Episode>): List<Episode> {
        val episodeList = episodeList_.sortedWith(compareBy<Episode> { it.SeriesName }.thenByDescending { it.SeasonNumber }.thenByDescending {it.EpisodeNumber})
        return episodeList
    }

    override fun getEpisodeList(): List<Episode> {
        checkMediaServer()

        return mServer.getEpisodeList()
    }

    override fun checkConnection(): Boolean {
        try {
            checkMediaServer()
        } catch (e: Exception) {
            throw e
        }

        return mServer.checkConnection()
    }

    override fun checkSettings(): Boolean {
        checkMediaServer()
        return mServer.checkSettings()
    }

    private fun checkMediaServer() {
        if(timestamp != timestamp_last || mServerT != settings.mediaServer)
            initMediaServer()
    }

    private fun initMediaServer() {
        when(settings.mediaServer) {
            0 -> mServer = Emby(settings)
            //1 -> mServer = Plex()
        }
        timestamp_last = timestamp
        mServerT = settings.mediaServer
        mServer
    }

}