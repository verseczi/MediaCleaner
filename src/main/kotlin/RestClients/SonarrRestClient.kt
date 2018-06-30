package com.mediacleaner.RestClients

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mediacleaner.Config
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.DataModels.Sonarr.Episode
import com.mediacleaner.DataModels.Sonarr.Series
import com.mediacleaner.Utils.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.xml.ws.http.HTTPException


class SonarrRestClient {
    val logger = Logger(this.javaClass.name)
    var settings: Settings = Config().getSettings()
    var url: String = settings.sonarrAddress
    val mapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    var client = OkHttpClient()

    init {
    }

    fun checkConnection(): Boolean {
        val url = "$url/system/status"
        val request = Request.Builder()
                .header("X-Api-Key", settings.sonarrApiKey)
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
            throw e
        }
    }

    fun checkAPIKey(): Boolean {
        val url = "$url/system/status"
        val request = Request.Builder()
                .header("X-Api-Key", settings.sonarrApiKey)
                .url(url)
                .build()
        try {
            val response = client.newCall(request).execute()
            return response.message() == "OK"
        }
        catch(e: Exception) {
            println("Exception: $e")
            return false
        }
    }

    fun getEpisodebySeries(seriesId: String): List<Episode>? {
        val url = "$url/Episode?SeriesId=$seriesId"
        val request = Request.Builder()
                .header("X-Api-Key", settings.sonarrApiKey)
                .url(url)
                .build()

        var content: String
        try {
            val response = client.newCall(request).execute()
            var content = response.body()!!.string()
            response.close()
            val episodeList = mapper.readValue<List<Episode>>(content)
            return episodeList
        } catch (e: Exception) {
            println(e)
            throw e
        }
    }

    fun getSeriesList(): List<Series>? {
        val url = "$url/Series"
        val request = Request.Builder()
                .header("X-Api-Key", settings.sonarrApiKey)
                .url(url)
                .build()

        var content: String
        try {
            val response = client.newCall(request).execute()
            val seriesList = mapper.readValue<List<Series>>(response.body()!!.string())
            return seriesList
        } catch (e: Exception) {
            println(e)
            throw e
        }
    }

    fun deleteEpisodeFile(episodeID: Int): Boolean {
        val url = "$url/EpisodeFile/$episodeID"

        val request = Request.Builder()
                .header("X-Api-Key", settings.sonarrApiKey)
                .url(url)
                .delete()
                .build()

        try {
            val response = client.newCall(request).execute()
            return response.message() == "OK"
        }
        catch(e: Exception) {
            println("Exception: $e")
            return false
        }
    }
}