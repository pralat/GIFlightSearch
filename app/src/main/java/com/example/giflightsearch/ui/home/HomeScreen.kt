package com.example.giflightsearch.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.giflightsearch.data.Airport

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    searchQuery: String,
    airportList: List<Airport>,
    favoriteFlights: List<FavoriteFlight>,
    onQueryChange: (String) -> Unit,
    onAirportClick: (Airport) -> Unit,
    onFavoriteClick: (String, String) -> Unit
) {
    Column(modifier = modifier) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(16.dp)
        )
        if (searchQuery.isEmpty()) {
            FavoriteFlights(
                favoriteFlights = favoriteFlights,
                onFavoriteClick = onFavoriteClick
            )
        } else {
            AirportList(
                airports = airportList,
                onAirportClick = onAirportClick
            )
        }
    }
}

@Composable
fun AirportList(
    airports: List<Airport>,
    onAirportClick: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(airports) { airport ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAirportClick(airport) }
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "${airport.iataCode} ${airport.name}")
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search for an airport") }
    )
}

@Composable
fun FavoriteFlights(
    favoriteFlights: List<FavoriteFlight>,
    onFavoriteClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(favoriteFlights) { flight ->
            FlightRow(
                departure = flight.departureAirport,
                destination = flight.destinationAirport,
                isFavorite = true,
                onFavoriteClick = { onFavoriteClick(flight.departureAirport.iataCode, flight.destinationAirport.iataCode) }
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
        Column(modifier = Modifier.weight(1f)) {
            Text("DEPARTURE")
            Text("${departure.iataCode} ${departure.name}")
            Text("DESTINATION")
            Text("${destination.iataCode} ${destination.name}")
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) Color.Red else Color.Gray
            )
        }
    }
}
