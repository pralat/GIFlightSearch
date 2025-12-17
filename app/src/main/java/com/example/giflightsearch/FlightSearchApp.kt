package com.example.giflightsearch

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.giflightsearch.data.Favorite
import com.example.giflightsearch.ui.FlightSearchViewModel
import com.example.giflightsearch.ui.flights.FlightsScreen
import com.example.giflightsearch.ui.home.HomeScreen

@Composable
fun FlightSearchApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: "home"
    val viewModel: FlightSearchViewModel = viewModel(factory = FlightSearchViewModel.factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            FlightSearchTopAppBar(
                title = if (currentScreen == "home") "Flight Search" else "Flights from ${uiState.selectedAirport?.iataCode}",
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    searchQuery = uiState.searchQuery,
                    airportList = uiState.airportList,
                    onQueryChange = viewModel::updateSearchQuery,
                    onAirportClick = {
                        viewModel.onAirportSelected(it)
                        navController.navigate("flights")
                    }
                )
            }
            composable("flights") {
                uiState.selectedAirport?.let { departureAirport ->
                    FlightsScreen(
                        departureAirport = departureAirport,
                        destinationList = uiState.airportList,
                        favoriteList = uiState.favoriteList,
                        onFavoriteClick = { departureCode, destinationCode ->
                            val favorite = uiState.favoriteList.find {
                                it.departureCode == departureCode && it.destinationCode == destinationCode
                            }
                            if (favorite != null) {
                                viewModel.removeFavorite(favorite)
                            } else {
                                viewModel.addFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun FlightSearchTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}
