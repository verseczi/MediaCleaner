package com.mediacleaner.app

import com.mediacleaner.DataModels.Settings
import com.mediacleaner.RestClients.EmbyRestClient
import com.mediacleaner.Utils.ConsoleRead
import javax.xml.ws.http.HTTPException

class Settings(private val settings: Settings) {
    fun getSettings(): Settings {
        // MediaCleaner
        settings.episodesToKeep = ConsoleRead.getInt("How many episodes should we keep", settings.episodesToKeep)
        settings.hoursToKeep = ConsoleRead.getInt("How long should we keep them (hours)", settings.hoursToKeep)
        settings.interval = ConsoleRead.getInt("How much time should be between two cleanings", settings.interval)
        settings.keepFavoriteEpisodes = ConsoleRead.getBoolean("Should we keep your favorite episodes? (Note: Not all Media Servers support this.)", settings.keepFavoriteEpisodes)
        settings.debug = ConsoleRead.getBoolean("Debug mode", false)
        settings.trace = ConsoleRead.getBoolean("Trace mode", false)
        settings.deleteMethod = ConsoleRead.getInt("Delete method (0=Default, 1=Sonarr. Note: If you have Sonarr, i would recommend using that option.)", settings.deleteMethod, listOf(0, 1))

        // Sonarr
        if(settings.deleteMethod == 1) {
            settings.sonarrApiKey = ConsoleRead.getString("Sonarr API Key", settings.sonarrApiKey)
            settings.sonarrAddress = ConsoleRead.getString("Sonarr address (e.g.: http://127.0.0.1:8989)",settings.sonarrAddress)
        }

        // Media Server
        // TODO: Somehow move this whole mess with the media servers to a separate thing
        settings.mediaServer = ConsoleRead.getInt("Which Media Server would you like to use (0=Emby)", 0,
                listOf(0), "There is no Media Server with this ID!")

        when(settings.mediaServer)
        {
            0 -> {
                settings.embyAddress = ConsoleRead.getString("Emby address (e.g.: http://127.0.0.1:8096)", settings.embyAddress)

                val username = ConsoleRead.getString("Emby username", settings.embyUserName)

                if(username == settings.embyUserName){
                    if (ConsoleRead.getBoolean("Would you like to re-login?", false, listOf("Y", "n"))) {
                        embyLogin(username, EmbyRestClient(settings))
                    }
                }
                else {
                    embyLogin(username, EmbyRestClient(settings))
                }
            }
        }

        return settings
    }

    private fun embyLogin(username: String, embyRestClient: EmbyRestClient) {
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
            settings.embyUserName = loggedInUser.User?.Name!!
            settings.embyUserId = loggedInUser.User.Id
            settings.embyAccessToken = loggedInUser.AccessToken!!
        }
        catch (e: HTTPException){
            when(e.statusCode){
                401 -> {
                    println("Your password is incorrect!")
                    embyLogin(username, embyRestClient)
                }
                500 -> {
                    println("There is no user with this username!")
                    embyLogin(ConsoleRead.getString("Emby username", settings.embyUserName), embyRestClient)
                }
            }
        }
        catch (e: Exception) {
            println("Something went wrong!")
        }
    }
}