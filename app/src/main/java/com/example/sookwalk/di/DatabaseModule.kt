package com.example.sookwalk.di

import com.example.sookwalk.data.local.AppDatabase
import com.example.sookwalk.data.local.dao.FavoritePlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideFavoritePlaceDao(
        db: AppDatabase //
    ): FavoritePlaceDao {
        return db.favoritePlaceDao()
    }
}