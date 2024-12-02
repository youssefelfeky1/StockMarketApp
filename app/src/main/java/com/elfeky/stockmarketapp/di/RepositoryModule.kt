package com.elfeky.stockmarketapp.di

import com.elfeky.stockmarketapp.data.csv.CSVParser
import com.elfeky.stockmarketapp.data.csv.CompanyListingsParser
import com.elfeky.stockmarketapp.data.repository.StockRepositoryImpl
import com.elfeky.stockmarketapp.domain.model.CompanyListing
import com.elfeky.stockmarketapp.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ):StockRepository
}