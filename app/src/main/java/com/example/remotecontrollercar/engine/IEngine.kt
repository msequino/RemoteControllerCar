package com.example.remotecontrollercar.engine

import com.example.remotecontrollercar.MessagePayload

interface IEngine {
    suspend fun turnOn()
    suspend fun send(messagePayload: MessagePayload)
    suspend fun close()
}
