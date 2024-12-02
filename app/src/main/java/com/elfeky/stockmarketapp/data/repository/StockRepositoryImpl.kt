package com.elfeky.stockmarketapp.data.repository

import com.elfeky.stockmarketapp.data.csv.CSVParser
import com.elfeky.stockmarketapp.data.local.StockDatabase
import com.elfeky.stockmarketapp.data.mapper.toCompanyListing
import com.elfeky.stockmarketapp.data.mapper.toCompanyListingEntity
import com.elfeky.stockmarketapp.data.remote.StockApi
import com.elfeky.stockmarketapp.domain.model.CompanyListing
import com.elfeky.stockmarketapp.domain.repository.StockRepository
import com.elfeky.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean, query: String
    ): Flow<Resource<List<CompanyListing>>> = flow {

        emit(Resource.Loading(true))
        val localListings = dao.searchCompanyListing(query)
        emit(Resource.Success(data = localListings.map { it.toCompanyListing() }))

        val isDbEmpty = localListings.isEmpty() && query.isBlank()
        val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote

        if (shouldJustLoadFromCache) {
            emit(Resource.Loading(false))
            return@flow
        }

        val remoteListings = try {
            val response = api.getListings()
            companyListingsParser.parse(response.byteStream())
        } catch (e: IOException) {
            e.printStackTrace()
            emit(Resource.Error("couldn't load data"))
            null
        } catch (e: HttpException) {
            e.printStackTrace()
            emit(Resource.Error("couldn't load data"))
            null
        }

        remoteListings?.let { listings ->
            dao.clearCompanyListings()
            dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })
            emit(
                Resource.Success(
                    data = dao
                        .searchCompanyListing(query = "")
                        .map { it.toCompanyListing() }
                )
            )
            emit(Resource.Loading(isLoading = false))
        }
    }
}