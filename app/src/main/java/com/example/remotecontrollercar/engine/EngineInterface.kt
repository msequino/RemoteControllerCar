package com.example.remotecontrollercar.engine

import com.example.remotecontrollercar.MessagePayload
import com.example.remotecontrollercar.Reverse
import com.example.remotecontrollercar.SlowDown
import com.example.remotecontrollercar.Throttle
import com.example.remotecontrollercar.engine.bluetooth.BluetoothService
import java.time.Instant
import java.time.format.DateTimeFormatter

interface IEngine {
    fun accelerate(x: Float, y: Float)
    fun reverse(x: Float, y: Float)
    fun slowDown()
}

class BluetoothEngine(val client: BluetoothService) : IEngine {
    override fun accelerate(x: Float, y: Float) {
        println("acc bt")
        client.write(MessagePayload(Throttle(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    override fun reverse(x: Float, y: Float) {
        println("rev bt")
        client.write(MessagePayload(Reverse(x, y), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }

    override fun slowDown() {
        println("slod bt")
        client.write(MessagePayload(SlowDown(), DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
    }
}
class RestEngine() : IEngine {
    override fun accelerate(x: Float, y: Float) {
    }

    override fun reverse(x: Float, y: Float) {
    }

    override fun slowDown() {
        println("slod rt")
    }
}