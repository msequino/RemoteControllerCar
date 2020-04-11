package com.example.remotecontrollercar.engine.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.remotecontrollercar.MessagePayload
import com.example.remotecontrollercar.engine.IEngine
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.util.*

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

class BluetoothEngine(device: BluetoothDevice, private val handler: Handler) : Thread(), IEngine {
    val uuid : UUID = UUID.fromString("3f9af32c-548f-4152-87b9-46ea997b22b5")

    private val thread : ConnectThread = ConnectThread(device)

    init {
        try {
            thread.run()
        } catch (ex : Exception) {
            Log.d(TAG, "cannot connect to bluetooth")
            val writtenMsg = handler.obtainMessage(
                MESSAGE_TOAST, -1, -1, "cannot connect to bluetooth")
            writtenMsg.sendToTarget()

        }
    }
    override fun send(message: MessagePayload) {

        val _message : String = message.toString()
        thread.write(_message)
    }

    fun disconnect() {
        thread.cancel()
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(uuid)
        }

        private var connectedThread : ConnectedThread ?= null

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                this.connectedThread = ConnectedThread(socket)
            }
        }

        fun write(message: String) {

            try {
                mmSocket?.use { socket ->
                    if(socket.isConnected && connectedThread != null)
                        connectedThread?.write(message.toByteArray())
                }
           } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.use { socket ->
                    if(socket.isConnected)
                        socket.close()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer)
            writtenMsg.sendToTarget()
        }

    }

}