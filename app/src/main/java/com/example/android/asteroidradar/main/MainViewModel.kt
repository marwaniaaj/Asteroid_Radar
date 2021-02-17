package com.example.android.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.asteroidradar.database.getDatabase
import com.example.android.asteroidradar.repository.AsteroidRepository
import com.example.android.asteroidradar.model.Asteroid
import kotlinx.coroutines.launch

enum class AsteroidViewFilter { SHOW_WEEK, SHOW_TODAY, SHOW_ALL }

class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        viewModelScope.launch {
            asteroidsRepository.getPictureOfTheDay()
            asteroidsRepository.refreshAsteroidsFeed()
        }
    }

    private val _asteroidFilter = MutableLiveData(AsteroidViewFilter.SHOW_ALL)

    /**
     * A list of asteroids feed to be observed.
     * Using switchMap to filter asteroid list based on menu selected.
     * Changing the value of [_asteroidFilter] will trigger asteroids to filter.
     */
    var asteroids = Transformations.switchMap(_asteroidFilter) {
        when (it!!) {
            AsteroidViewFilter.SHOW_WEEK ->
                asteroidsRepository.asteroids
            AsteroidViewFilter.SHOW_TODAY ->
                asteroidsRepository.todayAsteroids
            else -> asteroidsRepository.allAsteroids
        }
    }

    val status = asteroidsRepository.asteroidApiStatus

    /**
     * Astronomy Picture of the Day
     */
    val pictureOfTheDay = asteroidsRepository.pictureOfTheDay

    /**
     * Called when navigation is finished
     */
    fun onSelectedAsteroidNavigated() {
        _navigateToSelectedAsteroid.value = null
    }

    /**
     * Called when asteroid is selected
     */
    fun onSelectedAsteroidClicked(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    /**
     * Update the value of [AsteroidViewFilter]
     */
    fun updateFilter(filter: AsteroidViewFilter) {
        _asteroidFilter.value = filter
    }

    /**
     * Factory for constructing MainViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}