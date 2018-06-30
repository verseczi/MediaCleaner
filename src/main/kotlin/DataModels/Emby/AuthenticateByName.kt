package com.mediacleaner.DataModels.Emby

data class AuthenticateByName(
        val User: User?,
        val SessionInfo: SessionInfo?,
        val AccessToken: String?,
        val ServerId: String?
)