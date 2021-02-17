package com.example.android.asteroidradar

import android.app.Application
import androidx.work.*
import com.example.android.asteroidradar.work.AsteroidWorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Override application to setup background work via WorkManager
 */
class AsteroidRadarApplication: Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {

        // Constraints for the periodic work
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .build()

        // Periodic work
        val repeatingRequest = PeriodicWorkRequestBuilder<AsteroidWorkManager>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        // Enqueue work
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            AsteroidWorkManager.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }
}