package com.example.remotecontrollercar.engine.wifi

import com.example.remotecontrollercar.MessagePayload
import com.example.remotecontrollercar.engine.IEngine
import kotlinx.coroutines.Deferred

class WifiEngine : IEngine {
    override suspend fun turnOn() : Deferred<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun send(messagePayload: MessagePayload) {
        TODO("Not yet implemented")
    }

    override suspend fun close() {
        TODO("Not yet implemented")
    }
}