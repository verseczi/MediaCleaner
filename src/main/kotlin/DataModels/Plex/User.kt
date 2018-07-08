package DataModels.Plex

data class User( val user: User)  {
    data class User(
            val id: Int,
            val uuid: String,
            val email: String,
            val joined_at: String,
            val username: String,
            val hasPassword: Boolean,
            val authToken: String,
            val authentication_token: String
    )
}