package com.kapil.tvtest.Presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.kapil.tvtest.domain.model.AdDataModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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

    setContent {
      MediaSequencer()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Composable
  fun MediaSequencer() {
    val mediaList = viewModel.user.observeAsState(emptyList()).value
    var currentItem by remember { mutableStateOf<AdDataModel?>(null) }
    var playSequenceKey by remember { mutableStateOf(0) }

    LaunchedEffect(mediaList) {
      mediaList.forEach { item ->
        viewModel.startTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        currentItem = item
        if (item.meme_type.lowercase() == "image") {
          delay(3000L) // Show image for 3 sec
        } else { // Wait until video ends — suspend until callback
          suspendCancellableCoroutine { cont ->
            videoEndedCallback = {
              cont.resume(Unit, onCancellation = null)
            }
          }
        }
        viewModel.updateStats(adDataModel =item)
      }
      viewModel.getAds()
//      playSequenceKey++
    }

    currentItem?.let { item ->
      MemeMediaView(media = item) {
        videoEndedCallback?.invoke() // Only called by video
        videoEndedCallback = null
      }
    }
  }



  @Composable
  fun MemeMediaView(media: AdDataModel, onComplete: () -> Unit) {
    when (media.meme_type.lowercase()) {
      "image" -> {
        AsyncImage(model = media.ad_data, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) // No need for delay here — handled in forEach loop
      }
      "video" -> {
        ExoPlayerView(videoUrl = media.ad_data, onVideoEnded = onComplete)
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

  override fun onStart() {
    super.onStart()
    println("LIFE-CYCLE: onStart")
  }

  override fun onResume() {
    super.onResume()
    println("LIFE-CYCLE: onResume")
  }

  override fun onDestroy() {
    super.onDestroy()
    println("LIFE-CYCLE: onDestroy")
  }

  override fun onPause() {
    super.onPause()
    println("LIFE-CYCLE: onPause")
  }

  override fun onStop() {
    super.onStop()
    println("LIFE-CYCLE: onStop")
  }

  override fun onRestart() {
    super.onRestart()
    println("LIFE-CYCLE: onRestart")
  }

  @Composable
  @Preview
  fun ShowPreview() {
  }
}