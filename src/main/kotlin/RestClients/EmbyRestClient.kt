package com.mediacleaner.RestClients

import com.mediacleaner.DataModels.Emby.AuthUser
import com.mediacleaner.DataModels.Emby.User
import com.mediacleaner.DataModels.Emby.UserItems
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.MediaServers.Emby
import com.mediacleaner.Utils.HashUtils
import com.mediacleaner.Utils.Logger
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.xml.ws.http.HTTPException
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types

class EmbyRestClient (val settings: Settings, val settings_emby: Emby.embySettings) {
    private val logger = Logger(this.javaClass.name, settings)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
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
            throw e
        }
        catch(e: Exception) {
            throw e
        }
    }

    fun getUserItems(): UserItems {
        val url = "${settings_emby.Address}/Users/${settings_emby.userId}/Items?recursive=true&IncludeItemTypes=Episode&Fields=MediaSources,DateCreated"
        val request = Request.Builder()
                .header("X-MediaBrowser-Token", settings_emby.accessToken)
                .url(url)
                .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            throw e
        }

        val content = response.body()!!.string()
        try {
            return moshi.adapter(UserItems::class.java).fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            throw e
        }
    }


    fun getAccessToken(username: String, password: String = ""): AuthUser {
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

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            throw e
        }

        val content = response.body()!!.string()
        try {
            return moshi.adapter(AuthUser::class.java).fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            when {
                response.code() == 500 -> throw HTTPException(500)
                response.code() == 401 -> throw HTTPException(401)
                else -> {
                    throw e
                }
            }
        }
    }

    fun getPublicUsers(): List<User> {
        val url = "${settings_emby.Address}/Users/Public"
        val request = Request.Builder()
                .url(url)
                .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            throw e
        }

        val content = response.body()!!.string()
        try {
            val adapter: JsonAdapter<List<User>> = moshi.adapter(Types.newParameterizedType(List::class.java, User::class.java))
            return adapter.fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            throw e
        }
    }
}