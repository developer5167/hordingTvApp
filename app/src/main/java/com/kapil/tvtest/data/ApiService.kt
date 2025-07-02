package com.kapil.tvtest.data

import com.kapil.tvtest.domain.model.UpdateStats
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
  @GET("fetchAds/")
  suspend fun getAds(@Query("device_id") device_id: String): List<AdDataDto>

  @POST("addStats")
  suspend fun insertAds(@Body updateStats: UpdateStats)
}