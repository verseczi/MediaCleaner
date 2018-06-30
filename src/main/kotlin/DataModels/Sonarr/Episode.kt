package com.mediacleaner.DataModels.Sonarr

data class Episode(
        val seriesId: Int,
        val episodeFileId: Int,
        val seasonNumber: Int,
        val episodeNumber: Int,
        val title: String,
        val hasFile: Boolean,
        val id: Int,
        val episodeFile: EpisodeFile? = null
)