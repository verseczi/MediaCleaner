package com.mediacleaner.DataModels.Sonarr

data class Episode(
        val episodeFileId: Int,
        val hasFile: Boolean,
        val episodeFile: EpisodeFile? = null
) {
    data class EpisodeFile(
            val path: String = ""
    )
}