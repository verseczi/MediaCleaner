package com.mediacleaner.RestClients

import DataModels.Plex.Container
import DataModels.Plex.User
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.MediaServers.Plex
import com.mediacleaner.Utils.Logger
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.xml.ws.http.HTTPException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class PlexRestClient (val settings: Settings, val settings_plex: Plex.plexSettings) {
    private val logger = Logger(this.javaClass.name, settings)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val client = OkHttpClient()

    fun checkConnection(): Boolean {
        val url = "${settings_plex.Address}/system"
        val request = addHeaders(Request.Builder())
                .addHeader("X-Plex-Token", settings_plex.accessToken)
                .addHeader("Accept", "application/json")
                .url(url)
                .get()
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

    fun getSections(): Container {
        val url = "${settings_plex.Address}/library/sections"
        val request = addHeaders(Request.Builder())

                .addHeader("X-Plex-Token", settings_plex.accessToken)
                .addHeader("Accept", "application/json")
                .url(url)
                .get()
                .build()
        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            println(e)
            throw e
        }

        val content = response.body()!!.string()
        try {
            return moshi.adapter(Container::class.java).fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            throw e
        }
    }

    fun getSection(section_id: String): Container {
        val url = "${settings_plex.Address}/library/sections/$section_id/all"
        val request = addHeaders(Request.Builder())
                .addHeader("X-Plex-Token", settings_plex.accessToken)
                .addHeader("Accept", "application/json").get()
                .url(url)
                .build()
        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            println(e)
            throw e
        }

        try {
            return moshi.adapter(Container::class.java).fromJson(response.body()!!.string())!!
        } catch (e: Exception) {
            logger.trace(response.body()!!.string())
            throw e
        }
    }

    fun getMetadataChildren(id: String): Container {
        val url = "${settings_plex.Address}/library/metadata/$id/children"
        val request = addHeaders(Request.Builder())
                .addHeader("X-Plex-Token", settings_plex.accessToken)
                .addHeader("Accept", "application/json").get()
                .url(url)
                .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            throw e
        }

        val content = response.body()!!.string()
        try {
            return moshi.adapter(Container::class.java).fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            throw e
        }
    }

    fun getAccessToken(username: String, password: String = ""): String {
        val url = "https://plex.tv/users/sign_in.json"
        val json = """
            {
                "user": {
                    "login": "$username",
                    "password": "$password"
                }
            }
            """.trimIndent()

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)

        val request = addHeaders(Request.Builder())
                .post(body)
                .url(url)
                .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            println(e)
            throw e
        }

        try {
            val result = moshi.adapter(User::class.java).fromJson(response.body()!!.string())
            return result!!.user.authToken
        } catch (e: Exception) {
            when {
                response.code() == 500 -> throw HTTPException(500)
                response.code() == 401 -> throw HTTPException(401)
                else -> throw e
            }
        }
    }

    fun addHeaders(req: Request.Builder): Request.Builder {
        req.addHeader("X-Plex-Device-Name", "Media Cleaner")
        req.addHeader("X-Plex-Product", "MediaCleaner")
        req.addHeader("X-Plex-Version", "1.0")
        req.addHeader("X-Plex-Client-Identifier", settings_plex.userId)

        return req
    }
}