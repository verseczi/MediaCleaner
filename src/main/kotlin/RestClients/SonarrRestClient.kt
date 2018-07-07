package com.mediacleaner.RestClients

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mediacleaner.Config
import com.mediacleaner.DataModels.Settings
import com.mediacleaner.DataModels.Sonarr.Episode
import com.mediacleaner.DataModels.Sonarr.Series
import com.mediacleaner.Sonarr
import com.mediacleaner.Utils.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.xml.ws.http.HTTPException


class SonarrRestClient (val settings: Settings, val sonarr_settings: Sonarr.sonarrSettings) {
    private val logger = Logger(this.javaClass.name, settings)
    private val mapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    private var client = OkHttpClient()

    fun checkConnection(): Boolean {
        val url = "${sonarr_settings.Address}/system/status"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
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
        val url = "${sonarr_settings.Address}/system/status"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
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
        val url = "${sonarr_settings.Address}/Episode?SeriesId=$seriesId"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .build()

        try {
            val response = client.newCall(request).execute()
            val content = response.body()!!.string()
            val episodeList = mapper.readValue<List<Episode>>(content)
            return episodeList
        } catch (e: Exception) {
            println(e)
            throw e
        }
    }

    fun getSeriesList(): List<Series>? {
        val url = "${sonarr_settings.Address}/Series"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .build()

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
        val url = "${sonarr_settings.Address}/EpisodeFile/$episodeID"

        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
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