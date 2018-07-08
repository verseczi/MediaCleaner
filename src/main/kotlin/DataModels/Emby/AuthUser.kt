package com.mediacleaner.DataModels.Emby

data class AuthUser(
        val User: User?,
        val AccessToken: String?,
        val ServerId: String?
)