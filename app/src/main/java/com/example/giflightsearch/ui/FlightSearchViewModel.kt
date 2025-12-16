package com.example.giflightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.giflightsearch.FlightSearchApplication
import com.example.giflightsearch.data.*
import com.example.giflightsearch.ui.home.HomeUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightRepository: FlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            userPreferencesRepository.searchQuery.first().let {
                _searchQuery.value = it
            }
        }
    }

    private val favoriteList: Flow<List<Favorite>> = flightRepository.getAllFavorites()

    val uiState: StateFlow<HomeUiState> = _searchQuery.flatMapLatest { query ->
        val airportsFlow = if (query.isBlank()) {
            flightRepository.getAllAirports()
        } else {
            flightRepository.searchAirports(query)
        }
        combine(airportsFlow, favoriteList) { airports, favorites ->
            HomeUiState(
                searchQuery = query,
                airportList = airports,
                favoriteList = favorites
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = HomeUiState()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onAirportSelected(airport: Airport) {
        _searchQuery.value = airport.iataCode
    }

    fun addFavorite(favorite: Favorite) {
        viewModelScope.launch {
            flightRepository.insertFavorite(favorite)
        }
    }

    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            flightRepository.deleteFavorite(favorite)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                FlightSearchViewModel(
                    application.container.flightRepository,
                    application.container.userPreferencesRepository
                )
            }
        }
    }
}