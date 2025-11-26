package com.example.giflightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightRepository {
    fun getAllAirports(): Flow<List<Airport>>

    fun getAirportByCode(code: String): Flow<Airport>

    fun searchAirports(query: String): Flow<List<Airport>>

    fun getAllFavorites(): Flow<List<Favorite>>

    suspend fun insertFavorite(favorite: Favorite)

    suspend fun deleteFavorite(favorite: Favorite)

    fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?>
}

class OfflineFlightRepository(private val airportDao: AirportDao, private val favoriteDao: FavoriteDao) : FlightRepository {
    override fun getAllAirports(): Flow<List<Airport>> = airportDao.getAll()

    override fun getAirportByCode(code: String): Flow<Airport> = airportDao.getByCode(code)

    override fun searchAirports(query: String): Flow<List<Airport>> = airportDao.search(query)

    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAll()

    override suspend fun insertFavorite(favorite: Favorite) = favoriteDao.insert(favorite)

    override suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.delete(favorite)

    override fun getFavorite(departureCode: String, destinationCode: String): Flow<Favorite?> =
        favoriteDao.getFavorite(departureCode, destinationCode)
}