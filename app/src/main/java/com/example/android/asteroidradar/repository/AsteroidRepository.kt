package com.example.android.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.asteroidradar.api.*
import com.example.android.asteroidradar.database.AsteroidDatabase
import com.example.android.asteroidradar.database.asDomainModel
import com.example.android.asteroidradar.model.Asteroid
import com.example.android.asteroidradar.model.PictureOfDay
import com.example.android.asteroidradar.model.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

enum class AsteroidApiStatus { LOADING, ERROR, DONE }

class AsteroidRepository(private val database: AsteroidDatabase) {

    private val today = getTodayFormattedDate()

    /**
     * Weekly list of [Asteroid]
     */
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeekAsteroids(today)) {
            it.asDomainModel()
        }

    /**
     * Today's list of [Asteroid]
     */
    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getTodayAsteroids(today)) {
            it.asDomainModel()
        }

    /**
     * All list of [Asteroids]
     */
    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    /**
     * Asteroids feed API status
     */
    private val _asteroidApiStatus = MutableLiveData(AsteroidApiStatus.DONE)
    val asteroidApiStatus: LiveData<AsteroidApiStatus>
        get() = _asteroidApiStatus

    /**
     * Downloaded [PictureOfDay] object
     */
    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay>
        get() = _pictureOfTheDay

    /**
     * Refresh the asteroids feed stored in database
     */
    suspend fun refreshAsteroidsFeed() {
        withContext(Dispatchers.IO) {

            val isDatabaseDataAvailable = allAsteroids.value?.count() ?: 0 > 0
            if (!isDatabaseDataAvailable){
                _asteroidApiStatus.postValue(AsteroidApiStatus.LOADING)
            }

            try {
                val response = NeoWs.asteroids.getAsteroidsFeed()
                val asteroids = parseAsteroidsJsonResult(JSONObject(response))
                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
                _asteroidApiStatus.postValue(AsteroidApiStatus.DONE)
            }
            catch (e:Exception) {
                if (!isDatabaseDataAvailable) {
                    _asteroidApiStatus.postValue(AsteroidApiStatus.ERROR)
                }
                Timber.e(e.message.toString())
            }
        }
    }

    /**
     * Delete asteroids feed from previous days
     */
    fun deletePreviousAsteroidsFeed() {
        database.asteroidDao.deletePreviousAsteroids(today)
    }

    /**
     * Get [PictureOfDay] from Nasa's web service
     */
    suspend fun getPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            try {
                val picture = Apod.pictureOfTheDay.getPictureOfTheDay()
                if (picture.mediaType == "image") {
                    _pictureOfTheDay.postValue(picture)
                } else {
                    _pictureOfTheDay.postValue(null)
                }
            }
            catch (e: Exception) {
                Timber.e(e.message.toString())
            }
        }
    }
}