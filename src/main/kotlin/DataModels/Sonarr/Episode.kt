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
) {
    data class EpisodeFile(
            val seriesId: Int,
            val seasonNumber: Int,
            val relativePath: String,
            val path: String,
            val id: Int
    )
}