package com.mediacleaner.DataModels

data class Settings (
                    var interval: Int = 60,
                    var hoursToKeep: Int = 168,
                    var episodesToKeep: Int = 5,
                    var keepFavoriteEpisodes: Boolean = true,
                    var mediaServer: Int = 0,
                    var debug: Boolean = false,
                    var trace: Boolean = false,
                    var logFile: String = "mediaCleaner.log",
                    var deleteMethod: Int = 0
                    )