package com.mediacleaner.MediaServers

import com.mediacleaner.Config
import com.mediacleaner.DataModels.Emby.UserItems
import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.IMediaServer
import com.mediacleaner.RestClients.EmbyRestClient
import com.mediacleaner.Utils.Logger
import java.time.ZonedDateTime
import java.util.*


class Emby : IMediaServer {
    val embyRestClient = EmbyRestClient()
    lateinit var UserItemList: UserItems

    override fun getItem(EpisodePath: String): Episode? {
        if (!::UserItemList.isInitialized)
            UserItemList = embyRestClient.getUserItems()

        val result = UserItemList.Items
                .firstOrNull { it.MediaSources.all { it?.Path == EpisodePath}}

        if(result != null) {
            // Season
            var season: Int
            try {season = Regex("[^0-9]").replace(result.SeasonName!!, "").toInt()} catch(e: Exception) {season = 0}

            return Episode(
                    result.SeriesName,
                    season,
                    result.IndexNumber,
                    result.Name,
                    EpisodePath,
                    result.UserData.IsFavorite,
                    result.UserData.Played,
                    Date.from(ZonedDateTime.parse(result.DateCreated).toInstant())
            )
        }

        return null
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