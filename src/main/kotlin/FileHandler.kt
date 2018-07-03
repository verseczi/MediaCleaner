package com.mediacleaner

import java.nio.file.Files
import java.nio.file.Paths
import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Sonarr.Series
import com.mediacleaner.RestClients.SonarrRestClient
import com.mediacleaner.Utils.Logger
import com.mediacleaner.DataModels.Sonarr.Episode as SonarrEpisode

class FileHandler (private val mServer: MediaServer, private val sonarrRestClient: SonarrRestClient){
    private val settings = mServer.settings
    private val logger = Logger(this.javaClass.name, settings)

    fun deleteFile(filePath: String): Boolean {
        try {
            when(settings.deleteMethod) {
                0 -> {
                    val file = Paths.get(filePath)
                    return if(Files.exists(file) && !Files.isDirectory(file)) {
                        Files.delete(file)
                        true
                    } else {
                        logger.error("There was an error deleting $filePath.")
                        false
                    }
                }
                1 -> {
                    val seriesList = sonarrRestClient.getSeriesList()
                    if(seriesList != null) {
                        for (series in seriesList) {
                            val episodeList = sonarrRestClient.getEpisodebySeries(series.id.toString())

                            if (episodeList != null) {
                                for (episode in episodeList) {
                                    if (episode.hasFile) {
                                        if(episode.episodeFile?.path == filePath) {
                                            try {
                                                sonarrRestClient.deleteEpisodeFile(episode.episodeFileId)
                                                return true
                                            } catch (e: Exception) {
                                                logger.error("There was an error deleting $filePath.")
                                                logger.error(e.toString())
                                                return false
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }

        return false
    }
}