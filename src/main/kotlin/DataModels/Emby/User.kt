package com.mediacleaner.DataModels.Emby

data class User (
        var Name: String = "",
        var ServerId: String?,
        var Id: String = "",
        var HasPassword: Boolean = false,
        var HasConfiguredPassword: Boolean?,
        var HasConfiguredEasyPassword: Boolean = false,
        var Configuration: Configuration?,
        var Policy: Policy?,
        var LastLoginDate: String?,
        var LastActivityDate: String?
)