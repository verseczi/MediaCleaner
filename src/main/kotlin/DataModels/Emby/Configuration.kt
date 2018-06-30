package com.mediacleaner.DataModels.Emby

data class Configuration(
        val PlayDefaultAudioTrack: Boolean?,
        val DisplayMissingEpisodes: Boolean?,
        val DisplayUnairedEpisodes: Boolean?,
        val GroupedFolders: List<Any>?,
        val SubtitleMode: String?,
        val DisplayCollectionsView: Boolean?,
        val EnableLocalPassword: Boolean?,
        val OrderedViews: List<Any>?,
        val LatestItemsExcludes: List<Any>?,
        val HidePlayedInLatest: Boolean?,
        val RememberAudioSelections: Boolean?,
        val RememberSubtitleSelections: Boolean?,
        val EnableNextEpisodeAutoPlay: Boolean?
)