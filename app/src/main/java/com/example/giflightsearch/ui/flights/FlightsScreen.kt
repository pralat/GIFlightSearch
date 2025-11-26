package com.example.giflightsearch.ui.flights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.giflightsearch.data.Airport
import com.example.giflightsearch.data.Favorite
import com.example.giflightsearch.ui.FlightSearchViewModel

@Composable
fun FlightsScreen(
    modifier: Modifier = Modifier,
    viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory),
    departureAirport: Airport
) {
    val uiState by viewModel.uiState.collectAsState()
    val destinationList = uiState.airportList.filter { it.iataCode != departureAirport.iataCode }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(destinationList) { destination ->
            val isFavorite = uiState.favoriteList.any {
                it.departureCode == departureAirport.iataCode && it.destinationCode == destination.iataCode
            }
            FlightRow(
                departure = departureAirport,
                destination = destination,
                isFavorite = isFavorite,
                onFavoriteClick = {
                    val favorite = Favorite(
                        departureCode = departureAirport.iataCode,
                        destinationCode = destination.iataCode
                    )
                    if (isFavorite) {
                        viewModel.removeFavorite(favorite)
                    } else {
                        viewModel.addFavorite(favorite)
                    }
                }
            )
        }
    }
}

@Composable
fun FlightRow(
    departure: Airport,
    destination: Airport,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("DEPARTURE")
            Text("${departure.iataCode} ${departure.name}")
            Text("DESTINATION")
            Text("${destination.iataCode} ${destination.name}")
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = Icons.Default.Star, 
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
            )
        }
    }
}
