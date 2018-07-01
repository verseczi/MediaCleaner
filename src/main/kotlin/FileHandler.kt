package com.mediacleaner

import com.mediacleaner.DataModels.Episode
import com.mediacleaner.DataModels.Sonarr.Series
import com.mediacleaner.RestClients.SonarrRestClient
import com.mediacleaner.Utils.Logger
import com.mediacleaner.DataModels.Sonarr.Episode as SonarrEpisode

class FileHandler (private val mServer: MediaServer, private val sonarrRestClient: SonarrRestClient){
    private val logger = Logger(this.javaClass.name, mServer.settings)

    fun getEpisodeListByOrder(episodeList_: List<Episode>): List<Episode> {
        val episodeList = episodeList_.sortedWith(compareBy<Episode> { it.SeriesName }.thenByDescending { it.SeasonNumber }.thenByDescending {it.EpisodeNumber})
        return episodeList
    }

    fun getEpisodeList(): List<Episode>? {
        var fileList: List<String>?
        val episodeList = mutableListOf<Episode>()

        try {
            fileList = getFileList()
        } catch (e: Exception) {
            throw e
        }

        for(filePath in fileList)
        {
            var episode = mServer.getItem(filePath)

            if (episode == null)
            {
                println("Can't find this file in the media server database: $filePath")
                continue
            }
            episodeList.add(episode)
        }

        return if(!episodeList.isEmpty())
            episodeList
        else
            null
    }

    fun deleteFile(filePath: String): Boolean {
        var seriesList: List<Series>?

        try {
            seriesList = sonarrRestClient.getSeriesList()
        } catch (e: Exception) {
            throw e
        }

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

        return false
    }

    fun getFileList(): List<String> {
        var fileList: MutableList<String> = mutableListOf()
        var seriesList: List<Series>?

        try {
            seriesList = sonarrRestClient.getSeriesList()
        } catch (e: Exception) {
            throw e
        }

        if(seriesList != null) {
            for (series in seriesList) {
                val episodeList = sonarrRestClient.getEpisodebySeries(series.id.toString())

                if (episodeList != null) {
                    for (episode in episodeList) {
                        if (episode.hasFile)
                            fileList.add(episode.episodeFile!!.path)
                    }
                }
            }
        }

        return fileList
    }
}