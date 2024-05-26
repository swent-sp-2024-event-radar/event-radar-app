package com.github.se.eventradar.model.di

import com.github.se.eventradar.model.repository.location.ILocationRepository
import com.github.se.eventradar.model.repository.location.NominatimLocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocationModule {
  @Provides
  @Singleton
  fun provideNominatimLocationRepository(): ILocationRepository {
    return NominatimLocationRepository()
  }
}
