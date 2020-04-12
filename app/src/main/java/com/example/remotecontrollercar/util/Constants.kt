package com.example.remotecontrollercar.util

class Constants {
    companion object {
        // Socket commands
        val CLOSE_CONNECTION : String = "CLOSE_CONNECTION"

        // Constants that indicate the current connection state
        val STATE_LISTEN = 1 // now listening for incoming connections
        val STATE_CONNECTED = 3 // now connected to a remote device

        // Key names received from the BluetoothChatService Handler
        val TOAST = "toast"

        val DEVICE_NAME = "raspberrypi"

    }
    
}