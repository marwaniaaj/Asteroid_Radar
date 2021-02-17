package com.example.android.asteroidradar.api

import com.example.android.asteroidradar.Constants
import com.example.android.asteroidradar.model.PictureOfDay
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

/*
Astronomy picture of the day API
 */
interface ApodApi {

    /**
     * Get Astronomy picture of the day
     * @return [PictureOfDay] object
     */
    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(
        @Query("api_key") apiKey: String = Constants.API_KEY): PictureOfDay
}

/**
 * Astronomy picture of the day api access
 */
object Apod {
    val pictureOfTheDay: ApodApi by lazy {
        retrofit.create(ApodApi::class.java)
    }
}