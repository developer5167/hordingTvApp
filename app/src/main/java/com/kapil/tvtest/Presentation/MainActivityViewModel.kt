package com.kapil.tvtest.Presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapil.tvtest.domain.UseCase.UseCase
import com.kapil.tvtest.domain.model.AdDataModel
import com.kapil.tvtest.domain.model.UpdateStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val userUseCase: UseCase) : ViewModel() {
  private val _user = MutableLiveData<List<AdDataModel>>()
  val user: LiveData<List<AdDataModel>> get() = _user

  val updateStats = UpdateStats()

  var startTime: String = ""


  fun getAds() {
//    _user.postValue(emptyList())
    viewModelScope.launch(Dispatchers.IO) {
      val result = userUseCase.getAds()
      if (result.isEmpty()) {

      }
      _user.postValue(result)
    }
  }

  fun getCompanyAds() {

  }

  fun updateStats(adDataModel: AdDataModel) {
    updateStats.time_at = startTime
    val endTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    updateStats.end_time = endTime
    updateStats.device_id = adDataModel.device_id
    updateStats.ad_id = adDataModel.ad_id
    updateStats.user_id = adDataModel.user_id
    viewModelScope.launch(Dispatchers.IO) {
      userUseCase.updateStats(updateStats)
    }
  }


}