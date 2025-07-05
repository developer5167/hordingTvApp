package com.kapil.tvtest.Presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapil.tvtest.AdControlStatus
import com.kapil.tvtest.SocketManager
import com.kapil.tvtest.domain.UseCase.UseCase
import com.kapil.tvtest.domain.model.AdDataModel
import com.kapil.tvtest.domain.model.Ads
import com.kapil.tvtest.domain.model.UpdateStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val userUseCase: UseCase) : ViewModel() {
  private val _user = MutableLiveData<AdDataModel>()
  private val _pauseAllAds = MutableStateFlow(false)
  private val _isClientAdsDisabled = MutableStateFlow(false)
   val isClientAdsDisabled:StateFlow<Boolean> = _isClientAdsDisabled
  val pauseAllAds: StateFlow<Boolean> = _pauseAllAds
  private val _loopKey = MutableStateFlow(0)
  val loopKey: StateFlow<Int> = _loopKey

  fun restartLoop() {
    _loopKey.value = _loopKey.value + 1
  }
  val socketManager = SocketManager()
  val user: LiveData<AdDataModel> get() = _user

  val updateStats = UpdateStats()

  var startTime: String = ""
  fun connectToSocket() {
    socketManager.connect { pauseValue ->
      println("PAUSE VALUE: $pauseValue")
     val adControlStatus=parseAdControlStatus(pauseValue)
      _pauseAllAds.value = adControlStatus.pause_all_ads
      _isClientAdsDisabled.value = adControlStatus.show_client_ads
    }
  }
  override fun onCleared() {
    super.onCleared()
    socketManager.disconnect()
  }

  fun getAds() {
    viewModelScope.launch(Dispatchers.IO) {
      val result = userUseCase.getAds()
      _user.postValue(result)
    }
  }


  fun updateStats(adDataModel: Ads) {
    updateStats.time_at = startTime
    val endTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    updateStats.end_time = endTime
    updateStats.device_id = adDataModel.deviceId.toString()
    updateStats.ad_id = adDataModel.adId.toString()
    updateStats.user_id = adDataModel.userId!!
    viewModelScope.launch(Dispatchers.IO) {
      userUseCase.updateStats(updateStats)
    }
  }
  private fun parseAdControlStatus(json: JSONObject): AdControlStatus {
    return AdControlStatus(
      device_id = json.getString("device_id"),
      show_client_ads = json.getBoolean("disable_client_ads"),
      pause_all_ads = json.getBoolean("pause_all_ads")
    )
  }

}