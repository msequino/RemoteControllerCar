package com.example.remotecontrollercar.engine

import com.example.remotecontrollercar.MessagePayload
import kotlinx.coroutines.Deferred

interface IEngine {
    suspend fun turnOn(): Deferred<Boolean>
    suspend fun send(messagePayload: MessagePayload)
    suspend fun close()
}
