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
import com.example.giflightsearch.data.UserPreferencesRepository
import com.example.giflightsearch.ui.home.FavoriteFlight
import com.example.giflightsearch.ui.home.HomeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightRepository: FlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedAirport = MutableStateFlow<Airport?>(null)

    init {
        viewModelScope.launch {
            _searchQuery.value = userPreferencesRepository.searchQuery.first()
        }
    }

    private val _favoriteList: StateFlow<List<Favorite>> = flightRepository.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _searchResultList: StateFlow<List<Airport>> =
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

    private val _favoriteFlights: Flow<List<FavoriteFlight>> =
        flightRepository.getAllFavorites().flatMapLatest { favorites ->
            val departureAirportFlows = favorites.map { flightRepository.getAirportByCode(it.departureCode) }
            val destinationAirportFlows = favorites.map { flightRepository.getAirportByCode(it.destinationCode) }

            if (departureAirportFlows.isEmpty() || destinationAirportFlows.isEmpty()) {
                flowOf(emptyList())
            } else {
                combine(departureAirportFlows) { departureAirports ->
                    combine(destinationAirportFlows) { destinationAirports ->
                        departureAirports.zip(destinationAirports).map { (departure, destination) ->
                            FavoriteFlight(
                                departureAirport = departure,
                                destinationAirport = destination
                            )
                        }
                    }
                }.flatMapLatest { it }
            }
        }

    val uiState: StateFlow<HomeUiState> = combine(
        _searchQuery,
        _selectedAirport,
        _searchResultList,
        _destinationList,
        _favoriteList
    ) { query, selected, searchResults, destinations, favorites ->
        HomeUiState(
            searchQuery = query,
            selectedAirport = selected,
            searchResultList = searchResults,
            destinationList = destinations,
            favoriteList = favorites
        )
    }.combine(_favoriteFlights.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )) { partialState, favoriteFlights ->
        partialState.copy(favoriteFlights = favoriteFlights)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(query)
        }
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
                FlightSearchViewModel(
                    application.container.flightRepository,
                    application.container.userPreferencesRepository
                )
            }
        }
    }
}
