package com.example.remotecontrollercar.engine

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.remotecontrollercar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

class DrivingCommand(val client: IEngine, val mEngineHandler: Handler) {
    private val TAG : String = "DrivingCommand"

    private var lastX : Float? = 0F
    private var lastY : Float? = 0F

    fun start() {
        Log.d(TAG,"burning up the motor")
        GlobalScope.launch {
            val turnOn = client.turnOn().await()
            val obtainMessage = mEngineHandler.obtainMessage()
            val bundle = Bundle()
            bundle.putBoolean("status engine", turnOn)
            obtainMessage.data = bundle
            mEngineHandler.sendMessage(obtainMessage)
        }
    }
    fun accelerate(x: Float, y: Float) {
        Log.d(TAG,"accelerating $x $y")
        GlobalScope.launch { client.send(MessagePayload(Throttle(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now()))) }
    }

    fun reverse(x: Float, y: Float) {
        Log.d(TAG,"reversing $x $y")
        GlobalScope.launch { client.send(MessagePayload(Reverse(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now()))) }
    }

    fun slowDown() {
        Log.d(TAG,"slowing down")
        GlobalScope.launch { client.send(MessagePayload(SlowDown(), DateTimeFormatter.ISO_INSTANT.format(Instant.now()))) }
    }

    fun steer(x: Float, y: Float) {
        if(Math.abs(lastX?.minus(x)!!) > 0.05F || Math.abs(lastY?.minus(y)!!) > 0.05F) {
            Log.d(TAG,"steering $x $y")
            GlobalScope.launch { client.send(MessagePayload(Steer(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now()))) }
        }
    }
    fun stop() {
        Log.d(TAG,"closingo into box")
        GlobalScope.launch { client.close() }
    }
}
