package com.kapil.tvtest.data

import com.kapil.tvtest.domain.model.AdDataModel
data class AdDataDto( val ad_id: String, val ad_data: String, val user_id: Int, val isapproved: Boolean, val created_at: String, val meme_type: String,val device_id:String,) {
  fun toDomain() = AdDataModel( ad_id, ad_data, user_id, isapproved, created_at, meme_type, device_id)
}
fun List<AdDataDto>.toDomain(): List<AdDataModel> {
  return map { it.toDomain() }
}