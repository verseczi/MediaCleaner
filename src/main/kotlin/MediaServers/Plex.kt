package com.mediacleaner.MediaServers

import DataModels.Plex.Container
import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.IMediaServer
import com.mediacleaner.RestClients.PlexRestClient
import com.mediacleaner.Utils.ConsoleRead
import java.time.ZonedDateTime
import java.util.*
import javax.xml.ws.http.HTTPException

class Plex(override var settings: Settings, override var properties: Properties) : IMediaServer {
    private val settings_plex: plexSettings = getSettings()
    private val plexRestClient = PlexRestClient(settings, settings_plex)

    private fun getSettings(): plexSettings {
        val settings = try {
            plexSettings (
                    properties.getProperty("plexAddress"),
                    properties.getProperty("plexUsername"),
                    properties.getProperty("plexUserId"),
                    properties.getProperty("plexAccessToken")
            )
        } catch (e: Exception) {
            plexSettings()
        }

        return settings
    }


    override fun getEpisodeList(): List<Episode> {
        val userItems: MutableList<Episode> = mutableListOf()
        try {
            val sections = plexRestClient.getSections().MediaContainer.Directory
            for (section in sections) {
                if (section?.type == "show") {
                    val series = plexRestClient.getSection(section.key).MediaContainer.Metadata
                    for (show in series) {
                        if(show != null) {
                            val seasons = plexRestClient.getMetadataChildren(show.ratingKey).MediaContainer.Metadata
                            for (season in seasons) {
                                if (season != null) {
                                    val episodes = plexRestClient.getMetadataChildren(season.ratingKey).MediaContainer.Metadata
                                    for (episode in episodes) {
                                        if(episode != null) {
                                            val seasonNumber = try {
                                                Regex("[^0-9]").replace(season.title, "").toInt()
                                            } catch (e: Exception) {
                                                0
                                            }

                                            userItems.add(
                                                    Episode(
                                                            show.title,
                                                            seasonNumber,
                                                            episode.index,
                                                            episode.title,
                                                            episode.Media.first()!!.Part.first().file,
                                                            false,
                                                            episode.viewCount > 0,
                                                            Date(episode.addedAt.toLong()*1000)
                                                    )
                                            )

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }

        return userItems
    }


    override fun readSettingsCLI(properties: Properties): Properties {
        settings_plex.Address = ConsoleRead.getString("Plex address (e.g.: http://127.0.0.1:32400)", settings_plex.Address)
        plexRestClient.settings_plex.Address = settings_plex.Address

        val username = ConsoleRead.getString("Plex e-mail / username", settings_plex.userName)

        if(username == settings_plex.userName) {
            if (ConsoleRead.getBoolean("Would you like to re-login?", false, listOf("Y", "n"))) {
                plexLogin(username)
            }
        }
        else {
            plexLogin(username)
        }

        properties.put("plexAddress", settings_plex.Address)
        properties.put("plexUsername", settings_plex.userName)
        properties.put("plexUserId", settings_plex.userId)
        properties.put("plexAccessToken", settings_plex.accessToken)

        return properties
    }

    private fun plexLogin(username: String) {
        val password = ConsoleRead.getString("Password", "")

        try {
            settings_plex.userName = username
            settings_plex.userId = UUID.randomUUID().toString()
            settings_plex.accessToken = plexRestClient.getAccessToken(username, password)
        }
        catch (e: HTTPException){
            when(e.statusCode){
                401 -> {
                    println("Your password is incorrect!")
                    plexLogin(username)
                }
                500 -> {
                    println("There is no user with this username!")
                    plexLogin(ConsoleRead.getString("Emby username", settings_plex.userName))
                }
            }
        }
        catch (e: Exception) {
            println(e)
            println("Something went wrong!")
        }

    }

    override fun removeSettings(properties: Properties): Properties {
        properties.remove("plexAddress")
        properties.remove("plexUsername")
        properties.remove("plexUserId")
        properties.remove("plexAccessToken")

        return properties
    }

    override fun checkConnection(): Boolean {
        return try {
            plexRestClient.checkConnection()
        } catch (e: HTTPException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    override fun checkSettings(): Boolean {
        try {
            properties.getProperty("plexAddress")
            properties.getProperty("plexUsername")
            properties.getProperty("plexUserId")
            properties.getProperty("plexAccessToken")
        } catch (e: Exception) {
            throw e
        }

        if(settings_plex.Address == "" || settings_plex.userName == "" || settings_plex.userId == "" || settings_plex.accessToken == "")
            return false

        return true
    }


    data class plexSettings (
            var Address: String = "http://127.0.0.1:32400",
            var userName: String = "",
            var userId: String = "",
            var accessToken: String = ""
    )
}