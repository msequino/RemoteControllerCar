package com.example.remotecontrollercar.engine.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import com.example.remotecontrollercar.MessagePayload
import com.example.remotecontrollercar.engine.IEngine
import com.example.remotecontrollercar.util.Constants.Companion.CLOSE_CONNECTION
import com.example.remotecontrollercar.util.Constants.Companion.STATE_CONNECTED
import com.example.remotecontrollercar.util.Constants.Companion.STATE_LISTEN
import java.io.IOException
import java.util.*

// FIXME use thread/coroutine apis
class BluetoothEngine(val device: BluetoothDevice) : IEngine, Thread() {
    val uuid : UUID = UUID.fromString("3f9af32c-548f-4152-87b9-46ea997b22b5")
    private val TAG = "BluetoothEngine"

    private var mSocket: BluetoothSocket? = null
    private var mState: Int =  STATE_LISTEN

    override fun turnOn() : Boolean {
        Log.d(TAG, "bluetooth engine start")
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
        synchronized(this) {
            mSocket = device.createRfcommSocketToServiceRecord(uuid)
            if(mState == STATE_LISTEN) {
                try {
                    mSocket?.connect()
                    mState = STATE_CONNECTED
                } catch (ex: IOException) {
                    mState = STATE_LISTEN
                }
            }
        }
        return mState == STATE_CONNECTED

    }

    override fun send(messagePayload: MessagePayload) {
        Log.d(TAG, "bluetooth engine send")
        // Synchronize a copy of the ConnectedThread
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
        }
        // Perform the write unsynchronized
        mSocket!!.outputStream.write(messagePayload.toString().toByteArray())
    }

    override fun close() {
        Log.d(TAG, "bluetooth engine close")
        synchronized(this) {
            if (mState != STATE_CONNECTED) return
        }
        mSocket!!.outputStream.write(CLOSE_CONNECTION.toByteArray())
        synchronized(this) {
            try {
                mSocket?.close()
                mState = STATE_LISTEN
            } catch (ex: IOException) {
                Log.e(TAG, "cannot close socket")
            }

        }
    }
}