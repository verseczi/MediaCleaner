package com.mediacleaner.MediaServers

import com.mediacleaner.DataModels.Emby.UserItems
import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.IMediaServer
import com.mediacleaner.RestClients.EmbyRestClient
import com.mediacleaner.Utils.ConsoleRead
import com.mediacleaner.Utils.Logger
import java.time.ZonedDateTime
import java.util.*
import javax.xml.ws.http.HTTPException

class Emby(override var settings: Settings, override var properties: Properties) : IMediaServer {
    private val logger = Logger(this.javaClass.name, settings)
    private lateinit var userItemList: UserItems
    private val settings_emby: embySettings = getSettings()
    private val embyRestClient = EmbyRestClient(settings, settings_emby)

    override fun getEpisodeList(): List<Episode> {
        val embyUserItems = getUserItems()
        val userItems = mutableListOf<Episode>()

        for (userItem in embyUserItems.Items) {
            val mediaSource = userItem.MediaSources.first()!!
            if (mediaSource.Path != null && mediaSource.Protocol == "File") {
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
        try {
            if (!::userItemList.isInitialized)
                userItemList = embyRestClient.getUserItems()
        } catch (e: Exception) {
            logger.error("Exception ${e.message}")
        }

        return userItemList
    }

    private fun getSettings(): embySettings {
        val settings = try {
            embySettings (
                    properties.getProperty("embyAddress"),
                    properties.getProperty("embyUsername"),
                    properties.getProperty("embyUserId"),
                    properties.getProperty("embyAccessToken")
            )
        } catch (e: Exception) {
            embySettings()
        }

        return settings
    }

    override fun readSettingsCLI(properties: Properties): Properties {
        settings_emby.Address = ConsoleRead.getString("Emby address (e.g.: http://127.0.0.1:8096)", settings_emby.Address)
        embyRestClient.settings_emby.Address = settings_emby.Address

        val username = ConsoleRead.getString("Emby username", settings_emby.userName)

        if(username == settings_emby.userName) {
            if (ConsoleRead.getBoolean("Would you like to re-login?", false, listOf("Y", "n"))) {
                embyLogin(username)
            }
        }
        else {
            embyLogin(username)
        }

        properties.put("embyAddress", settings_emby.Address)
        properties.put("embyUsername", settings_emby.userName)
        properties.put("embyUserId", settings_emby.userId)
        properties.put("embyAccessToken", settings_emby.accessToken)

        return properties
    }

    override fun removeSettings(properties: Properties): Properties {
        properties.remove("embyAddress")
        properties.remove("embyUsername")
        properties.remove("embyUserId")
        properties.remove("embyAccessToken")

        return properties
    }

    override fun checkConnection(): Boolean {
        return try {
            embyRestClient.checkConnection()
        } catch (e: HTTPException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    override fun checkSettings(): Boolean {
        try {
            properties.getProperty("embyAddress")
            properties.getProperty("embyUsername")
            properties.getProperty("embyUserId")
            properties.getProperty("embyAccessToken")
        } catch (e: Exception) {
            throw e
        }

        if(settings_emby.Address == "" || settings_emby.userName == "" || settings_emby.userId == "" || settings_emby.accessToken == "")
            return false

        return true
    }

    private fun embyLogin(username: String) {
        val publicUsers = embyRestClient.getPublicUsers()
        val publicUser = publicUsers.firstOrNull { it.Name == username}

        val password = if(publicUser != null) {
            if(!publicUser.HasPassword)
                ""
            else
                ConsoleRead.getString("Password", "")
        }
        else {
            ConsoleRead.getString("Password", "")
        }
        try {
            val loggedInUser = embyRestClient.getAccessToken(username, password)
            settings_emby.userName = loggedInUser.User?.Name!!
            settings_emby.userId = loggedInUser.User.Id
            settings_emby.accessToken = loggedInUser.AccessToken!!
        }
        catch (e: HTTPException){
            when(e.statusCode){
                401 -> {
                    println("Your password is incorrect!")
                    embyLogin(username)
                }
                500 -> {
                    println("There is no user with this username!")
                    embyLogin(ConsoleRead.getString("Emby username", settings_emby.userName))
                }
            }
        }
        catch (e: Exception) {
            println("Something went wrong!")
        }

    }

    data class embySettings (
            var Address: String = "http://127.0.0.1:8096",
            var userName: String = "",
            var userId: String = "",
            var accessToken: String = ""
    )
}