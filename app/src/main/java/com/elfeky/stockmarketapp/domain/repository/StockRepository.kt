package com.elfeky.stockmarketapp.domain.repository

import com.elfeky.stockmarketapp.domain.model.CompanyListing
import com.elfeky.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>>
}