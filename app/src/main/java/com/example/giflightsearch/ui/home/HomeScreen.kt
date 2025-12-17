package com.example.giflightsearch.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.giflightsearch.data.Airport

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    searchQuery: String,
    airportList: List<Airport>,
    onQueryChange: (String) -> Unit,
    onAirportClick: (Airport) -> Unit
) {
    Column(modifier = modifier) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(16.dp)
        )
        AirportList(
            airports = airportList,
            onAirportClick = onAirportClick
        )
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
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search for an airport") }
    )
}
