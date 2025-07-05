package com.kapil.tvtest.data
import com.kapil.tvtest.Constants
import com.kapil.tvtest.domain.UserRepository.Repository
import com.kapil.tvtest.domain.model.AdDataModel
import com.kapil.tvtest.domain.model.UpdateStats
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val apiService: ApiService) : Repository {
  override suspend fun getAds(): AdDataModel {
    return apiService.getAds(Constants.deviceId)
  }

  override suspend fun updateStats(updateStats: UpdateStats) {
    return apiService.insertAds(updateStats)
  }
}