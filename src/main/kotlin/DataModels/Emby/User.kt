package com.mediacleaner.DataModels.Emby

data class User (
        var Name: String = "",
        var ServerId: String?,
        var Id: String = "",
        var HasPassword: Boolean = false
)