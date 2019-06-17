package com.mediacleaner

import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.MediaServers.Emby
import com.mediacleaner.MediaServers.Plex
import com.mediacleaner.Utils.ConsoleRead
import com.mediacleaner.Utils.Logger
import java.util.*

class MediaServer (override var properties: Properties, override var settings: Settings) : IMediaServer {
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
        timestamp = System.currentTimeMillis() / 1000L
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

        return try {
            mServer.checkSettings()
        } catch (e: Exception) {
            logger.error(e.message.toString())
            throw e
        }
    }

    private fun checkMediaServer() {
        if(timestamp != timestamp_last || mServerT != settings.mediaServer)
            initMediaServer()
    }

    private fun initMediaServer() {
        try {
            when (settings.mediaServer) {
                0 -> mServer = Emby(settings, properties)
                1 -> mServer = Plex(settings, properties)
            }
            timestamp_last = timestamp
            mServerT = settings.mediaServer
        } catch (e: Exception) {
            throw e
        }
    }

    override fun readSettingsCLI(properties: Properties): Properties {
        settings.mediaServer = ConsoleRead.getInt("Which Media Server would you like to use (0=Emby; 1=Plex)", settings.mediaServer,
                listOf(0,1), "There is no Media Server with this ID!")
        properties.put("mediaServer", settings.mediaServer.toString())


        // If the media server is changed then this should clear the previously used media server's settings from the properties variable.
        if(mServerT != settings.mediaServer) {
            mServer.removeSettings(properties)
            checkMediaServer()
        }

        return mServer.readSettingsCLI(properties)
    }

    override fun removeSettings(properties: Properties): Properties {
        return mServer.removeSettings(properties)
    }
}