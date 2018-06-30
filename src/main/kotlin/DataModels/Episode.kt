package com.mediacleaner.DataModels

import java.util.*

data class Episode (
                        val SeriesName: String,
                        val SeasonNumber: Int,
                        val EpisodeNumber: Int,
                        val EpisodeTitle: String,
                        val FilePath: String,
                        val IsFavorite: Boolean,
                        val Played: Boolean,
                        val dateAdded: Date

)