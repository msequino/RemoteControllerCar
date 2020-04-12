package com.example.remotecontrollercar.engine

import com.example.remotecontrollercar.MessagePayload

interface IEngine {
    fun turnOn() : Boolean
    fun send(messagePayload: MessagePayload)
    fun close()
}
