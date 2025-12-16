package com.example.giflightsearch

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.giflightsearch.data.Airport
import com.example.giflightsearch.data.Favorite
import com.example.giflightsearch.data.FlightRepository
import com.example.giflightsearch.data.UserPreferencesRepository
import com.example.giflightsearch.ui.FlightSearchViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class FlightSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `updateSearchQuery updates uiState`() = runTest {
        val viewModel = FlightSearchViewModel(FakeFlightRepository(), FakeUserPreferencesRepository())
        viewModel.updateSearchQuery("LAX")
        assertEquals("LAX", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `search returns valid airport`() = runTest {
        val viewModel = FlightSearchViewModel(FakeFlightRepository(), FakeUserPreferencesRepository())
        viewModel.updateSearchQuery("LAX")
        val airportList = viewModel.uiState.value.airportList
        assertEquals(1, airportList.size)
        assertEquals("LAX", airportList.first().iataCode)
    }

    @Test
    fun `search for non-existent airport returns empty list`() = runTest {
        val viewModel = FlightSearchViewModel(FakeFlightRepository(), FakeUserPreferencesRepository())
        viewModel.updateSearchQuery("L8R")
        val airportList = viewModel.uiState.value.airportList
        assertTrue(airportList.isEmpty())
    }

    @Test
    fun `updateSearchQuery saves to preferences`() = runTest {
        val fakeUserPreferencesRepository = FakeUserPreferencesRepository()
        val viewModel = FlightSearchViewModel(FakeFlightRepository(), fakeUserPreferencesRepository)
        viewModel.updateSearchQuery("LAX")
        assertEquals("LAX", fakeUserPreferencesRepository.savedSearchQuery)
    }
}

class FakeFlightRepository : FlightRepository {
    private val testAirports = listOf(
        Airport(id = 1, iataCode = "JFK", name = "John F. Kennedy International Airport", passengers = 1000),
        Airport(id = 2, iataCode = "LAX", name = "Los Angeles International Airport", passengers = 2000),
        Airport(id = 3, iataCode = "ORD", name = "O'Hare International Airport", passengers = 3000)
    )


    override fun getAllAirports(): Flow<List<Airport>> = flowOf(testAirports)

    override fun getAirportByCode(code: String): Flow<Airport> {
        return flowOf(testAirports.first { it.iataCode == code })
    }

    override fun searchAirports(query: String): Flow<List<Airport>> {
        return flowOf(
            testAirports.filter {
                it.iataCode.contains(query, ignoreCase = true) ||
                it.name.contains(query, ignoreCase = true)
            }
        )
    }

    override fun getAllFavorites(): Flow<List<Favorite>> = flowOf(emptyList())

    override suspend fun insertFavorite(favorite: Favorite) {}

    override suspend fun deleteFavorite(favorite: Favorite) {}

    override fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?> = flowOf(null)
}

open class FakeUserPreferencesRepository(
    private val initialSearchQuery: String = ""
) : UserPreferencesRepository(mock(DataStore::class.java) as DataStore<Preferences>) {
    var savedSearchQuery: String? = null
        private set

    override val searchQuery: Flow<String> = flowOf(initialSearchQuery)

    override suspend fun saveSearchQuery(searchQuery: String) {
        savedSearchQuery = searchQuery
    }
}
