package com.kapil.tvtest.domain.model

import com.google.gson.annotations.SerializedName
data class AdDataModel(
  @SerializedName("ads") var ads: ArrayList<Ads> = arrayListOf(),
  @SerializedName("isClientAdsDisabled") var isClientAdsDisabled: Boolean? = null,
  @SerializedName("pauseAllAds") var pauseAllAds: Boolean? = null,
  @SerializedName("companyAds") var companyAds: ArrayList<Ads> = arrayListOf(),
)
data class Ads(
  @SerializedName("ad_id") var adId: String? = null,
  @SerializedName("ad_data") var adData: String? = null,
  @SerializedName("user_id") var userId: Int? = null,
  @SerializedName("isapproved") var isapproved: Boolean? = null,
  @SerializedName("created_at") var createdAt: String? = null,
  @SerializedName("meme_type") var memeType: String? = null,
  @SerializedName("isactive") var isactive: Boolean? = null,
  @SerializedName("start_date") var startDate: String? = null,
  @SerializedName("end_date") var endDate: String? = null,
  @SerializedName("title") var title: String? = null,
  @SerializedName("description") var description: String? = null,
  @SerializedName("device_id") var deviceId: String? = null,
)
data class UpdateStats(var ad_id: String = "", var user_id: Int = 0, var time_at: String = "", var device_id: String = "", var end_time: String = "")
