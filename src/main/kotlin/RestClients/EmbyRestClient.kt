package com.mediacleaner.RestClients

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mediacleaner.DataModels.Emby.AuthenticateByName
import com.mediacleaner.DataModels.Emby.User
import com.mediacleaner.DataModels.Emby.UserItems
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.MediaServers.Emby
import com.mediacleaner.Utils.HashUtils
import com.mediacleaner.Utils.Logger
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.xml.ws.http.HTTPException

class EmbyRestClient (val settings: Settings, val settings_emby: Emby.embySettings) {
    private val logger = Logger(this.javaClass.name, settings)
    private val mapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    private val client = OkHttpClient()

    fun checkConnection(): Boolean {
        val url = "${settings_emby.Address}/System/Info"
        val request = Request.Builder()
                .header("X-MediaBrowser-Token", settings_emby.accessToken)
                .url(url)
                .build()

        try {
            val response = client.newCall(request).execute()
            if(response.message() == "OK")
                return true
            else
                throw HTTPException(401)
        }
        catch(e: HTTPException) {
            logger.error("HTTPException: ${e.statusCode}")
            throw e
        }
        catch(e: Exception) {
            logger.error("Exception: ${e.localizedMessage}")
            throw e
        }
    }

    fun getUserItems(): UserItems {
        val url = "${settings_emby.Address}/Users/${settings_emby.userId}/Items?recursive=true&IncludeItemTypes=Episode&Fields=MediaSources,DateCreated"
        val request = Request.Builder()
                .header("X-MediaBrowser-Token", settings_emby.accessToken)
                .url(url)
                .build()
        var content: String
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body()
            content = responseBody!!.string()
            response.body()?.close()
        } catch (e: Exception) {
            println(e)
            throw e
        }

        val result = mapper.readValue<UserItems>(content)

        return result
    }


    fun getAccessToken(username: String, password: String = ""): AuthenticateByName {
        val url = "${settings_emby.Address}/Users/AuthenticateByName"
        val json = """
            {
                "Username":"${username}",
                "password":"${HashUtils.sha1(password)}",
                "passwordMd5":"${HashUtils.md5(password)}",
            }
            """.trimIndent()

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)

        val request = Request.Builder()
                .header("Authorization", "MediaBrowser Client=\"MediaCleaner\", Device=\"Media Cleaner\", DeviceId=\"1\", Version=\"1.0.0\"")
                .post(body)
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        try {
            val user = mapper.readValue<AuthenticateByName>(response.body()?.string().toString())
            return user
        }
        catch (e: Exception) {
            when {
                response.code() == 500 -> throw HTTPException(500)
                response.code() == 401 -> throw HTTPException(401)
                else -> throw e
            }
        }
    }

    fun getPublicUsers(): List<User> {
        val url = "${settings_emby.Address}/Users/Public"
        val request = Request.Builder()
                .url(url)
                .build()
        val response = client.newCall(request).execute()
        val state = mapper.readValue<List<User>>(response.body()?.string().toString())
        return state
    }

}