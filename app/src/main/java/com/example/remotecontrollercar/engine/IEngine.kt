package com.example.remotecontrollercar.engine

import com.example.remotecontrollercar.MessagePayload
import com.example.remotecontrollercar.engine.bluetooth.BluetoothEngine

interface IEngine {
    fun send(messagePayload: MessagePayload)
}
