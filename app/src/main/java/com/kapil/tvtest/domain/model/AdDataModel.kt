package com.kapil.tvtest.domain.model
data class AdDataModel (val ad_id:String,val ad_data:String,val user_id:Int,val isapproved:Boolean,val created_at:String,val meme_type:String,val device_id:String)
data class UpdateStats (var ad_id:String="",var user_id: Int=0, var time_at:String="",var device_id:String="", var end_time:String="")