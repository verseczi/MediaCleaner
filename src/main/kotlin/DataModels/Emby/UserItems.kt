package com.mediacleaner.DataModels.Emby

data class UserItems(
        val Items: List<UserItem>,
        val TotalRecordCount: Int = 0
) {
    data class UserItem(
            val Name: String,
            val Id: String?,
            val DateCreated: String,
            val MediaSources: List<MediaSource?> = emptyList(),
            val IndexNumber: Int,
            val UserData: UserData,
            val SeriesName: String,
            val SeasonName: String?
    )

    data class MediaSource(
            val Protocol: String?,
            val Id: String?,
            val Path: String?
    )

    data class UserData(
            val PlayCount: Int,
            val IsFavorite: Boolean,
            val Played: Boolean
    )
}