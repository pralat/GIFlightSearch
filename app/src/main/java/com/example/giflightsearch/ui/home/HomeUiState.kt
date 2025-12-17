package com.example.giflightsearch.ui.home

import com.example.giflightsearch.data.Airport
import com.example.giflightsearch.data.Favorite

data class HomeUiState(
    val searchQuery: String = "",
    val selectedAirport: Airport? = null,
    val searchResultList: List<Airport> = emptyList(),
    val destinationList: List<Airport> = emptyList(),
    val favoriteList: List<Favorite> = emptyList()
)