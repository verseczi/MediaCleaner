package com.mediacleaner.DataModels.Emby

data class UserData(
        val PlaybackPositionTicks: Long,
        val PlayCount: Int,
        val IsFavorite: Boolean,
        val LastPlayedDate: String?,
        val Played: Boolean,
        val Key: String,
        val PlayedPercentage: Double
)