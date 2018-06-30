package com.mediacleaner.DataModels.Sonarr

data class EpisodeFile(
        val seriesId: Int,
        val seasonNumber: Int,
        val relativePath: String,
        val path: String,
        val id: Int
)