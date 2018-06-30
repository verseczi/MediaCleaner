package com.mediacleaner.DataModels

data class Settings (
                    var interval: Int = 60,
                    var hoursToKeep: Int = 168,
                    var episodesToKeep: Int = 5,
                    var keepFavoriteEpisodes: Boolean = false,
                    var mediaServer: Int = 0,
                    var debug: Boolean = false,
                    var trace: Boolean = false,

                    var sonarrApiKey: String = "",
                    var sonarrAddress: String = "http://127.0.0.1:8989",

                    var embyAddress: String = "http://127.0.0.1:8096",
                    var embyUserName: String = "",
                    var embyUserId: String = "",
                    var embyAccessToken: String = "",

                    var plexAddress: String = "http://127.0.0.1:32400",
                    var plexUsername: String = "",
                    var plexUuid: String = "",
                    var plexAccessToken: String = "",
                    var plexClientToken: String = ""
                    )