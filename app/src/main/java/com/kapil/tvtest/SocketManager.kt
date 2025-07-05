package com.kapil.tvtest

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketManager {
  private lateinit var socket: Socket

  fun connect(onPauseValueReceived: (JSONObject) -> Unit) {
    try {
      socket = IO.socket("http://192.168.1.39:4000") // Your server IP

      socket.on(Socket.EVENT_CONNECT) {
        Log.d("SocketIO", "Connected")
      }

      socket.on("pauseAllAdsUpdate") { args ->
        Log.d("SocketIO", "pauseAllAdsUpdate: ${args[0]as JSONObject}")
        if (args.isNotEmpty()) {
          val json = args[0] as JSONObject
          onPauseValueReceived(json)
        }
      }

      socket.on(Socket.EVENT_DISCONNECT) {
        Log.d("SocketIO", "Disconnected")
      }

      socket.connect()
    } catch (e: Exception) {
      Log.e("SocketIO", "Connection error", e)
    }
  }

  fun disconnect() {
    if (this::socket.isInitialized) {
      socket.disconnect()
    }
  }
}