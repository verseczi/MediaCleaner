package com.mediacleaner

import java.nio.file.Files
import java.nio.file.Paths
import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Sonarr.Series
import com.mediacleaner.RestClients.SonarrRestClient
import com.mediacleaner.Utils.Logger
import javax.xml.ws.http.HTTPException
import com.mediacleaner.DataModels.Sonarr.Episode as SonarrEpisode

class FileHandler (mServer: MediaServer){
    private val settings = mServer.settings
    private val logger = Logger(this.javaClass.name, settings)
    private lateinit var sonarr: Sonarr

    init {
        when(settings.deleteMethod) {
            1-> sonarr = Sonarr(mServer.properties, mServer.settings)
        }
    }

    fun ready(): Boolean {
        when(settings.deleteMethod) {
            1 -> {
                return try {
                    sonarr.checkConnection()
                } catch (e: HTTPException) {
                    logger.error("HTTPException: ${e.statusCode}")

                    if(sonarr.checkAPIKey())
                        true
                    else {
                        logger.error("Sonarr API key is invalid.")
                        false
                    }
                } catch (e: Exception) {
                    logger.error("Exception: ${e.message}")
                    false
                }
            }
        }

        return true
    }

    fun deleteFile(filePath: String): Boolean {
        try {
            when(settings.deleteMethod) {
                0 -> {
                    val file = Paths.get(filePath)
                    return if(Files.exists(file) && !Files.isDirectory(file)) {
                        Files.delete(file)
                        true
                    } else {
                        throw Exception("File does not exist or it is a directory.")
                    }
                }
                1 -> {
                    sonarr.deleteEpisode(filePath)
                }
            }
        } catch (e: Exception) {
            logger.error("There was an error deleting $filePath.")
            logger.error("${e.message}")
        }

        return false
    }
}