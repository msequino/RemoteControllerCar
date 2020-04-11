package com.example.remotecontrollercar.engine

import android.util.Log
import com.example.remotecontrollercar.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class DrivingCommand(val client: IEngine) {
    private val TAG : String = "DrivingCommand"
    fun accelerate(x: Float, y: Float) {
        Log.d(TAG,"accelerating $x $y")
        client.send(MessagePayload(Throttle(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    fun reverse(x: Float, y: Float) {
        Log.d(TAG,"reversing $x $y")
        client.send(MessagePayload(Reverse(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    fun slowDown() {
        Log.d(TAG,"slowing down")
        client.send(MessagePayload(SlowDown(), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    fun steer(x: Float, y: Float) {
        Log.d(TAG,"steering $x $y")
        client.send(MessagePayload(Steer(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }
}
