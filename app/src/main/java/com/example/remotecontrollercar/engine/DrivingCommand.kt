package com.example.remotecontrollercar.engine

import android.util.Log
import com.example.remotecontrollercar.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class DrivingCommand(val client: IEngine) {
    private val TAG : String = "DrivingCommand"

    private var lastX : Float? = 0F
    private var lastY : Float? = 0F

    suspend fun start() {
        Log.d(TAG,"burning up the motor")
        return client.turnOn()
    }
    suspend fun accelerate(x: Float, y: Float) {
        Log.d(TAG,"accelerating $x $y")
        client.send(MessagePayload(Throttle(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    suspend fun reverse(x: Float, y: Float) {
        Log.d(TAG,"reversing $x $y")
        client.send(MessagePayload(Reverse(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    suspend fun slowDown() {
        Log.d(TAG,"slowing down")
        client.send(MessagePayload(SlowDown(), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    suspend fun steer(x: Float, y: Float) {
        if(Math.abs(lastX?.minus(x)!!) > 0.05F || Math.abs(lastY?.minus(y)!!) > 0.05F) {
            Log.d(TAG,"steering $x $y")
            client.send(MessagePayload(Steer(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
        }
    }
    suspend fun stop() {
        Log.d(TAG,"closingo into box")
        client.close()
    }
}
