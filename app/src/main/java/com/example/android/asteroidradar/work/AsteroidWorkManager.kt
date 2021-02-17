package com.example.android.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.asteroidradar.database.getDatabase
import com.example.android.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class AsteroidWorkManager (appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object {
        /**
         * Unique [AsteroidWorkManager] work name
         */
        const val  WORK_NAME = "AsteroidWorkManager"
    }

    /**
     * A coroutine-friendly method to do the network work.
     */
    override suspend fun doWork(): Result {

        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)

        return try {
            repository.refreshAsteroidsFeed()
            repository.deletePreviousAsteroidsFeed()
            Result.success()
        }
        catch (e: HttpException) {
            Result.retry()
        }
    }
}