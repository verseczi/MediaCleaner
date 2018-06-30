package com.mediacleaner.DataModels.Emby

data class UserItems(
        val Items: List<UserItem>,
        val TotalRecordCount: Int?
)