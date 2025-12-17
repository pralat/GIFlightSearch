package com.example.giflightsearch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.giflightsearch.FlightSearchApplication
import com.example.giflightsearch.data.Airport
import com.example.giflightsearch.data.Favorite
import com.example.giflightsearch.data.FlightRepository
import com.example.giflightsearch.ui.home.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlightSearchViewModel(private val flightRepository: FlightRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedAirport = MutableStateFlow<Airport?>(null)

    private val _favoriteList: StateFlow<List<Favorite>> = flightRepository.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _airportList: StateFlow<List<Airport>> =
        _searchQuery.flatMapLatest { query ->
            if (query.isNotBlank()) {
                flightRepository.searchAirports(query)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _destinationList: StateFlow<List<Airport>> = 
        _selectedAirport.flatMapLatest { selected ->
            if (selected != null) {
                flightRepository.getAllAirports().map { airports ->
                    airports.filter { it.id != selected.id }
                }
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery,
        _selectedAirport,
        _airportList,
        _destinationList,
        _favoriteList
    ) { query, selected, airports, destinations, favorites ->
        HomeUiState(
            searchQuery = query,
            selectedAirport = selected,
            airportList = if (selected == null) airports else destinations,
            favoriteList = favorites
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _selectedAirport.value = null
    }

    fun onAirportSelected(airport: Airport) {
        _selectedAirport.value = airport
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
                FlightSearchViewModel(application.container.flightRepository)
            }
        }
    }
}