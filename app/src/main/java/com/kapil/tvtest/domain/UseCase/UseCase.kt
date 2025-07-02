package com.kapil.tvtest.domain.UseCase
import com.kapil.tvtest.domain.UserRepository.Repository
import com.kapil.tvtest.domain.model.AdDataModel
import com.kapil.tvtest.domain.model.UpdateStats
import javax.inject.Inject
class UseCase @Inject constructor(private val repository: Repository) {
  suspend fun getAds(): List<AdDataModel> {
    return repository.getAds()
  }
  suspend fun updateStats(updateStats: UpdateStats) {
    repository.updateStats(updateStats)
  }
}