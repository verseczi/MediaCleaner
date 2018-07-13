package com.mediacleaner.RestClients

import com.mediacleaner.DataModels.Settings
import com.mediacleaner.DataModels.Sonarr.Episode
import com.mediacleaner.DataModels.Sonarr.Series
import com.mediacleaner.Sonarr
import com.mediacleaner.Utils.Logger
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.xml.ws.http.HTTPException


class SonarrRestClient (val settings: Settings, val sonarr_settings: Sonarr.sonarrSettings) {
    private val logger = Logger(this.javaClass.name, settings)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private var client = OkHttpClient()

    fun checkConnection(): Boolean {
        val url = "${sonarr_settings.Address}/api/system/status"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .build()
        return try {
            val response = client.newCall(request).execute()
            if(response.message() == "OK")
                true
            else
                throw HTTPException(401)
        }
        catch(e: HTTPException) {
            throw e
        }
        catch(e: Exception) {
            false
        }
    }

    fun checkAPIKey(): Boolean {
        val url = "${sonarr_settings.Address}/api/system/status"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .build()
        return try {
            val response = client.newCall(request).execute()
            response.message() == "OK"
        } catch(e: Exception) {
            logger.error("Exception: $e")
            false
        }
    }

    fun getEpisodebySeries(seriesId: String): List<Episode>? {
        val url = "${sonarr_settings.Address}/api/Episode?SeriesId=$seriesId"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            throw e
        }

        val content = response.body()!!.string()
        try {
            val adapter: JsonAdapter<List<Episode>> = moshi.adapter(Types.newParameterizedType(List::class.java, Episode::class.java))
            return adapter.fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            throw e
        }
    }

    fun getSeriesList(): List<Series>? {
        val url = "${sonarr_settings.Address}/api/Series"
        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .build()

        val response = try {
            client.newCall(request).execute()
        } catch (e: Exception) {
            throw e
        }

        val content = response.body()!!.string()
        try {
            val adapter: JsonAdapter<List<Series>> = moshi.adapter(Types.newParameterizedType(List::class.java, Series::class.java))
            return adapter.fromJson(content)!!
        } catch (e: Exception) {
            logger.trace(content)
            throw e
        }
    }

    fun deleteEpisodeFile(episodeID: Int) {
        val url = "${sonarr_settings.Address}/api/EpisodeFile/$episodeID"

        val request = Request.Builder()
                .header("X-Api-Key", sonarr_settings.APIKey)
                .url(url)
                .delete()
                .build()

        try {
            val response = client.newCall(request).execute()
            if(response.message() != "OK")
                logger.trace(response.body()!!.string())
        }
        catch(e: Exception) {
            throw e
        }
    }
}