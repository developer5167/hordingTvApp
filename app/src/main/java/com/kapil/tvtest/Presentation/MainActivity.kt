package com.kapil.tvtest.Presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.kapil.tvtest.domain.model.AdDataModel
import com.kapil.tvtest.domain.model.Ads
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

var videoEndedCallback: (() -> Unit)? = null

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val viewModel: MainActivityViewModel by viewModels()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.getAds()
    viewModel.connectToSocket()
    setContent {
      MediaSequencer()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Composable
  fun MediaSequencer() {
    val mediaList by viewModel.user.observeAsState(AdDataModel())
    var currentItem by remember { mutableStateOf<Ads?>(null) }
    val isPaused by viewModel.pauseAllAds.collectAsState()
    val isClientAdsDisabled by viewModel.isClientAdsDisabled.collectAsState()
    var clientAdIndex by remember { mutableStateOf(0) }
    var companyAdIndex by remember { mutableStateOf(0) }


    if (mediaList.ads.isEmpty() && mediaList.companyAds.isEmpty()) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Something went wrong")
      }
      return
    }

    if (isPaused) {
      Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Text("Ads have been limited")
      }
      return
    }

    LaunchedEffect(mediaList, isClientAdsDisabled) {
      while (true) {
        val adList = if (isClientAdsDisabled) mediaList.companyAds else mediaList.ads
        val startIndex = if (isClientAdsDisabled) companyAdIndex else clientAdIndex

        if (adList.isEmpty()) {
          delay(2000L)
          continue
        }

        for (i in startIndex until adList.size) {
          val item = adList[i]
          Log.d("MediaSequencer", "Now playing: ${item.adId} - ${item.memeType}")

          viewModel.startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
          currentItem = item.copy(adId = "${item.adId}_${System.currentTimeMillis()}")


          if (item.memeType?.lowercase() == "image") {
            delay(3000L)
          } else {
            suspendCancellableCoroutine { cont ->
              videoEndedCallback = {
                Log.d("MediaSequencer", "Video completed callback triggered")
                if (cont.isActive) {
                  cont.resume(Unit,onCancellation = null)
                }
              }

              // Fallback: resume if something goes wrong
              CoroutineScope(Dispatchers.Main).launch {
                delay(60000L) // 60s fallback
                if (cont.isActive) cont.resume(Unit,onCancellation = null)
              }
            }
          }

//          if (!isClientAdsDisabled) {
//            viewModel.updateStats(item)
//          }

          if (isClientAdsDisabled) {
            companyAdIndex = i + 1
            if (companyAdIndex >= mediaList.companyAds.size) companyAdIndex = 0
          } else {
            clientAdIndex = i + 1
            if (clientAdIndex >= mediaList.ads.size) clientAdIndex = 0
          }
        }

        // After playing all items, fetch updated list
        Log.d("MediaSequencer", "Finished loop, fetching new ads...")
        viewModel.getAds()

        delay(1000L) // allow LiveData to emit and Compose to recompose
      }
    }

//    currentItem?.let {
//      MemeMediaView(media = it) {
//        Log.d("MediaSequencer", "MemeMediaView completed, invoking callback")
//        videoEndedCallback?.invoke()
//        videoEndedCallback = null
//      }
//    }
    currentItem?.let {
      key(it.adId) { // ✅ forces full recomposition when adId changes
        MemeMediaView(media = it) {
          Log.d("MediaSequencer", "MemeMediaView completed, invoking callback")
          videoEndedCallback?.invoke()
          videoEndedCallback = null
        }
      }
    }
  }


  //  @OptIn(ExperimentalCoroutinesApi::class)
//  @Composable
//  fun MediaSequencer() {
//    val mediaList by viewModel.user.observeAsState(AdDataModel())
//    var currentItem by remember { mutableStateOf<Ads?>(null) }
//    val isPaused by viewModel.pauseAllAds.collectAsState()
//    val isClientAdsDisabled by viewModel.isClientAdsDisabled.collectAsState()
//    val loopKey by viewModel.loopKey.collectAsState()
//
//    if (mediaList.ads.isEmpty() && mediaList.companyAds.isEmpty()) { // Case: No ads at all
//      Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text("Something went wrong")
//      }
//      return
//    }
//    if (isPaused) {
//      Box(modifier = Modifier
//        .fillMaxSize()
//        .background(Color.White), contentAlignment = Alignment.Center) {
//        Text("Ads have been limited")
//      }
//      return
//    }
//    LaunchedEffect(mediaList,isClientAdsDisabled,loopKey) {
//      while (true) {
//          val adList = when {
//            isClientAdsDisabled -> mediaList.companyAds
//            mediaList.ads.isNotEmpty() -> mediaList.ads
//            else -> mediaList.companyAds
//          }
//
//        for (item in adList) {
//          viewModel.startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
//          currentItem = item
//          if (item.memeType?.lowercase() == "image") {
//            delay(3000L)
//          } else {
//            suspendCancellableCoroutine { cont ->
//              videoEndedCallback = {
//                println("VIDEO PLAY FINISHED")
//                cont.resume(Unit, onCancellation = null)
//              }
//              CoroutineScope(Dispatchers.Main).launch {
//                delay(60000L)
//                if (cont.isActive) cont.resume(Unit,onCancellation = null)
//              }
//            }
//
//          }
//
//          // Only update stats if it's client ad
//          if (!isClientAdsDisabled && mediaList.ads.isNotEmpty()) {
//            viewModel.updateStats(adDataModel = item)
//          }
//        }
//        viewModel.getAds()
//        viewModel.restartLoop()
//      }
//    }
//
//    // Show media
//    currentItem?.let {
//      MemeMediaView(media = it) {
//        videoEndedCallback?.invoke()
//        videoEndedCallback = null
//      }
//    }
//
//
//  }


  @Composable
  fun MemeMediaView(media: Ads, onComplete: () -> Unit) {
    when (media.memeType?.lowercase()) {
      "image" -> {
        AsyncImage(model = media.adData, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) // No need for delay here — handled in forEach loop
      }
      "video" -> {
        media.adData?.let { ExoPlayerView(videoUrl = it, onVideoEnded = onComplete) }
      }
      else -> {
        Text("Unsupported media")
      }
    }
  }

  @Composable
  fun ExoPlayerView(videoUrl: String, onVideoEnded: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
      ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(videoUrl))
        prepare()
        playWhenReady = true
      }
    }

    DisposableEffect(Unit) {
      val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
          if (state == Player.STATE_ENDED) {
            onVideoEnded()
          }
        }
      }
      exoPlayer.addListener(listener)
      onDispose {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
      }
    }

    AndroidView(factory = {
      PlayerView(it).apply {
        player = exoPlayer
        useController = false
      }
    }, modifier = Modifier.fillMaxSize())
  }


}