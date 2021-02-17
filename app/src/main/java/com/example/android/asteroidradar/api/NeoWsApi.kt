package com.example.android.asteroidradar.api

import com.example.android.asteroidradar.Constants
import com.example.android.asteroidradar.model.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(Constants.BASE_URL)
    .build()

private val next7Days = getNextSevenDaysFormattedDates()
private val startDate = next7Days[0]
private val endDate = next7Days[next7Days.count() - 1]

/*
Asteroids - Near Earth Object Web Service API
 */
interface NeoWsApi {

    /**
     * Get Asteroids feed
     * @return parsable [String] object containing JSON result
     */
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsFeed(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("start_date") start_date: String = startDate,
        @Query("end_date") end_date: String = endDate): String
}

/**
 * Near Earth Object Web Service api access
 */
object NeoWs {
    val asteroids: NeoWsApi by lazy {
        retrofit.create(NeoWsApi::class.java)
    }
}