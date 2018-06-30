package com.mediacleaner.DataModels.Emby

data class PlayState(
        val CanSeek: Boolean,
        val IsPaused: Boolean,
        val IsMuted: Boolean,
        val RepeatMode: String
)