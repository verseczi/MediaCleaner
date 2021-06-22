package com.mediacleaner.app

import com.mediacleaner.FileHandler
import com.mediacleaner.MediaServer
import com.mediacleaner.Utils.DateUtils
import com.mediacleaner.Utils.FileUtils
import com.mediacleaner.Utils.Logger
import com.mediacleaner.DataModels.Settings
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.util.Timer
import java.util.concurrent.TimeUnit
import java.io.File

class MediaCleaner (private val mServer: MediaServer,  val settings: Settings) {
    val logger = Logger(this.javaClass.name, settings)
    var cancelled: Boolean = false
    val fileHandler = FileHandler(mServer)
    var timer = Timer()
    var retry = 0

    fun startTimer(mCleaner: MediaCleaner, delay_: Long = 0, interval_: Long = settings.interval.toLong()) {
        var interval: Long = (interval_ * 60 * 1000)
        var delay = delay_
        if (cancelled && retry > 0) {
            delay = 5 * 1000 * 60       // 5 minutes
            interval = 5 * 1000 * 60    // 5 minutes
        }
        else if (cancelled && retry == 0) {
            delay = interval
        }

        cancelled = false

        timer = Timer()
        timer.schedule(MediaCleanerTimer(mCleaner), delay, interval)
    }

    fun stopTimer() {
        timer.cancel()
    }

    fun mediaCleaner() {
        var error = false
        if(!error) {
            try {
                if(!mServer.checkConnection() || !fileHandler.ready())
                    error = true
            } catch (e: SocketTimeoutException) {
                logger.error("Can't connect to the servers.")
                error = true
            } catch (e: Exception) {
                logger.error("Something went wrong. Error: ${e.message}")
                error = true
            }
        }

        if(error) {
            logger.info("Retrying in 5 minutes!")
            if(retry == 0) {
                cancelled = true
                timer.cancel()
            }

            retry++
        }

        if(!error) {
            var episodeList = mServer.getEpisodeList()
            if (!episodeList.isEmpty()) {
                episodeList = mServer.getEpisodeListByOrder(episodeList)
                var i = 0
                var episodeCounter = 0
                var lastWatchedSeason = -1
                var deletedFiles = 0
                var savedSpace : Long = 0
                while (i < episodeList.count()) {
                    val episode = episodeList[i]
                    var fileDeletable = false
                    var notDeletableBecause = ""

                    if(i != 0) {
                        if(episodeList[i].SeriesName != episodeList[i-1].SeriesName) {
                            episodeCounter = 0
                            lastWatchedSeason = -1
                        }
                    }

                    val timeDiff = TimeUnit.MILLISECONDS.toHours(DateUtils.asDate(LocalDate.now()).time  - episode.dateAdded.time)

                    if(episode.Played && lastWatchedSeason == -1)
                        lastWatchedSeason = episode.SeasonNumber

                    if(timeDiff > settings.hoursToKeep
                            && episode.Played
                            && episodeCounter >= settings.episodesToKeep)
                    {
                        if(settings.keepFavoriteEpisodes) {
                            if(!episode.IsFavorite)
                                fileDeletable = true
                        } else {
                            fileDeletable = true
                        }

                        if(settings.keepLastSeason) {
                            if (lastWatchedSeason == episode.SeasonNumber) {
                                fileDeletable = false
                            }
                        }

                        if(fileDeletable) {
                            val excludedPhrases: List<String> = settings.excludedPhrases.split(";")
                            
                            for(phrase in excludedPhrases) {
                                if(phrase != "") {
                                    if (episode.SeriesName.contains(phrase)) {
                                        fileDeletable = false
                                        notDeletableBecause += " excludedPhrases;"
                                    }
                                }
                            }
                        }
                    }

                    if(!episode.Played)
                        notDeletableBecause += " Played;"
                    if(settings.keepFavoriteEpisodes && episode.IsFavorite)
                        notDeletableBecause += " IsFavorite;"
                    if(episodeCounter < settings.episodesToKeep)
                        notDeletableBecause += " episodesToKeep;"
                    if(timeDiff < settings.hoursToKeep)
                        notDeletableBecause += " timeDiff;"
                    if(settings.keepLastSeason && lastWatchedSeason == episode.SeasonNumber)
                        notDeletableBecause += " keepLastWatchedSeason;"

                    logger.info("[${episode.SeriesName}] - Season: [${episode.SeasonNumber}] - Episode: ${episode.EpisodeNumber} - [${episode.EpisodeTitle}]: FilePath: ${episode.FilePath}; IsFavorite: ${episode.IsFavorite}; Played: ${episode.Played}; dateAdded: ${episode.dateAdded}; timeDiff: $timeDiff; deletable: $fileDeletable; Reason why its not deletable: $notDeletableBecause")
                    if(fileDeletable) {
                        logger.info("File deleted: ${episode.FilePath}")
                        fileHandler.deleteFile(episode.FilePath)
                        savedSpace += FileUtils.getFileSize(File(episode.FilePath))
                        deletedFiles++
                    }
                    if(episode.Played)
                        episodeCounter++
                    i++
                }

                logger.info("episodeList.count(): ${episodeList.count()}")
                logger.info("Deleted files: $deletedFiles")
                logger.info("Saved space: ${FileUtils.humanReadableByteCountSI(savedSpace)}")
            }

            if (retry > 0) {
                retry = 0
                cancelled = true
                timer.cancel()
            }
        }
    }
}