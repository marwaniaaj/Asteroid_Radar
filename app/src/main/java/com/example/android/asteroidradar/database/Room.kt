package com.example.android.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    
    @Query("SELECT * from databaseasteroid ORDER BY closeApproachDate")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * from databaseasteroid WHERE closeApproachDate = :startDate ORDER BY closeApproachDate")
    fun getTodayAsteroids(startDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * from databaseasteroid WHERE closeApproachDate >= :startDate ORDER BY closeApproachDate")
    fun getWeekAsteroids(startDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE from databaseasteroid WHERE closeApproachDate < :today")
    fun deletePreviousAsteroids(today: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroids")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}