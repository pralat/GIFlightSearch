package com.example.giflightsearch

import com.example.giflightsearch.data.Airport
import com.example.giflightsearch.data.Favorite
import com.example.giflightsearch.data.FlightRepository
import com.example.giflightsearch.ui.FlightSearchViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FlightSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `updateSearchQuery updates uiState`() = runTest {
        val viewModel = FlightSearchViewModel(FakeFlightRepository())
        viewModel.updateSearchQuery("LAX")
        assertEquals("LAX", viewModel.uiState.value.searchQuery)
    }
}

class FakeFlightRepository : FlightRepository {
    override fun getAllAirports(): Flow<List<Airport>> = flowOf(emptyList())

    override fun getAirportByCode(code: String): Flow<Airport> = flowOf(Airport(0, code, "", 0))

    override fun searchAirports(query: String): Flow<List<Airport>> = flowOf(listOf(Airport(0, query, "", 0)))

    override fun getAllFavorites(): Flow<List<Favorite>> = flowOf(emptyList())

    override suspend fun insertFavorite(favorite: Favorite) {}

    override suspend fun deleteFavorite(favorite: Favorite) {}

    override fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?> = flowOf(null)
}