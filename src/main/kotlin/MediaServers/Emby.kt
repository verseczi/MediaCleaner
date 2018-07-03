package com.mediacleaner.MediaServers

import com.mediacleaner.DataModels.Emby.UserItems
import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.IMediaServer
import com.mediacleaner.RestClients.EmbyRestClient
import java.time.ZonedDateTime
import java.util.*

class Emby(override var settings: Settings) : IMediaServer {
    private val embyRestClient = EmbyRestClient(settings)
    private lateinit var userItemList: UserItems

    override fun getEpisodeList(): List<Episode> {
        val embyUserItems = getUserItems()
        val userItems = mutableListOf<Episode>()

        for(userItem in embyUserItems.Items) {
            val mediaSource = userItem.MediaSources.first()!!
            if(mediaSource.Path != null && mediaSource.Protocol == "File") {
                val season = try {
                    Regex("[^0-9]").replace(userItem.SeasonName!!, "").toInt()
                } catch (e: Exception) {
                    0
                }

                userItems.add(Episode(
                        userItem.SeriesName,
                        season,
                        userItem.IndexNumber,
                        userItem.Name,
                        mediaSource.Path,
                        userItem.UserData.IsFavorite,
                        userItem.UserData.Played,
                        Date.from(ZonedDateTime.parse(userItem.DateCreated).toInstant()))
                )
            }
        }

        return userItems
    }

    private fun getUserItems(): UserItems {
        if (!::userItemList.isInitialized)
            userItemList = embyRestClient.getUserItems()

        return userItemList
    }

    override fun checkConnection(): Boolean {
        try {
            embyRestClient.checkConnection()
        } catch (e: Exception) {
            throw e
        }

        return embyRestClient.checkConnection()
    }

    override fun checkSettings(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}