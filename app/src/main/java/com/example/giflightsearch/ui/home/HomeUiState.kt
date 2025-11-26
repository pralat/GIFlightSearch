package com.example.giflightsearch.ui.home

import com.example.giflightsearch.data.Airport
import com.example.giflightsearch.data.Favorite

data class HomeUiState(
    val searchQuery: String = "",
    val selectedAirport: Airport? = null,
    val airportList: List<Airport> = emptyList(),
    val favoriteList: List<Favorite> = emptyList()
)