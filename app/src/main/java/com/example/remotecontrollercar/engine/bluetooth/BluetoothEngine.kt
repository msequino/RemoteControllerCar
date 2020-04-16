package com.example.remotecontrollercar.engine.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.remotecontrollercar.MessagePayload
import com.example.remotecontrollercar.engine.IEngine
import com.example.remotecontrollercar.util.Constants.Companion.CLOSE_CONNECTION
import com.example.remotecontrollercar.util.Constants.Companion.STATE_CONNECTED
import com.example.remotecontrollercar.util.Constants.Companion.STATE_LISTEN
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class BluetoothEngine(val device: BluetoothDevice) : IEngine {
    val uuid : UUID = UUID.fromString("3f9af32c-548f-4152-87b9-46ea997b22b5")
    private val TAG = "BluetoothEngine"

    private var mSocket: BluetoothSocket? = null
    private var mState: AtomicInteger =  AtomicInteger(STATE_LISTEN)

    private suspend fun scope(f: () -> Unit): Job {
        return GlobalScope.launch { withContext(Dispatchers.IO) { f() } }
    }

    override suspend fun turnOn(): Deferred<Boolean> {
        Log.d(TAG, "bluetooth engine start")
        return GlobalScope.async {
            withContext(Dispatchers.IO) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                synchronized(this) {
                    if (mState.get() == STATE_LISTEN) {
                        mSocket = device.createRfcommSocketToServiceRecord(uuid)
                        try {
                            mSocket!!.connect()
                            mState.set(STATE_CONNECTED)
                            return@withContext true
                        } catch (ex: IOException) {
                            Log.e(TAG, "cannot turn on the engine")
                        }
                    }
                }
                false
            }
        }
    }

    override suspend fun send(messagePayload: MessagePayload) {
        Log.d(TAG, "bluetooth engine send")
        scope {
            // Get synchronized state
            if (mState.get() == STATE_CONNECTED) {
                // Perform the write unsynchronized
                mSocket!!.outputStream.write(messagePayload.toString().toByteArray())
            }
        }
    }

    override suspend fun close() {
        Log.d(TAG, "bluetooth engine close")
        scope {
            if (mState.get() == STATE_CONNECTED) {

                try {
                    mSocket!!.outputStream.write(CLOSE_CONNECTION.toByteArray())
                    mSocket!!.close()
                    mState.set(STATE_LISTEN)
                } catch (ex: IOException) {
                    Log.e(TAG, "cannot close socket")
                }

            }
        }
    }
}