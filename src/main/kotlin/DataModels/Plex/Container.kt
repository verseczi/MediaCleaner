package DataModels.Plex

data class Container(
        val MediaContainer: MediaContainerX
) {
    data class MediaContainerX (
            val size: Int,
            val parentTitle: String? = "",
            val title1: String?,
            val Directory: List<Directory?> = listOf(),
            val Metadata: List<Metadata?> = listOf()
    )

    data class Directory (
            val key: String,
            val type: String?,
            val title: String,
            val uuid: String?,
            val Location: List<Location?> = listOf()
    )

    data class Location (
            val id: Int,
            val path: String
    )

    data class Metadata(
            val ratingKey: String,
            val key: String,
            val type: String,
            val title: String,
            val index: Int,
            val viewCount: Int = 0,
            val addedAt: Int,
            val Media: List<Media?> = listOf()
    )

    data class Media(
            val id: Int,
            val Part: List<Part> = listOf()
    )

    data class Part(
            val id: Int,
            val key: String,
            val file: String
    )
}