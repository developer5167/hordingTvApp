package com.kapil.tvtest.domain.UserRepository

import com.kapil.tvtest.domain.model.AdDataModel
import com.kapil.tvtest.domain.model.UpdateStats

interface Repository {
  suspend fun getAds(): List<AdDataModel>
  suspend fun updateStats(updateStats: UpdateStats)
}