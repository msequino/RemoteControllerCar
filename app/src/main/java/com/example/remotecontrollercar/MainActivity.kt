package com.example.remotecontrollercar

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.remotecontrollercar.engine.DrivingCommand
import com.example.remotecontrollercar.engine.IEngine
import com.example.remotecontrollercar.engine.bluetooth.BluetoothEngine
import com.example.remotecontrollercar.util.RepeatListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() , SensorEventListener {

    private val TAG = "MainActivity"
    private val DEVICE_NAME = "X9 PLUS"

    private val REQUEST_ENABLE_BT: Int = 1
    private val DISCOVER_BLUETOOTH_DEVICE: Int = 2

    private var mSensorManager : SensorManager?= null
    private var mAccelerometer : Sensor?= null

    private var x : Float = 0F
    private var y : Float = 0F

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val engine : IEngine ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // get reference of the service
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // focus in accelerometer
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // setup the window
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "bluetooth not available", Toast.LENGTH_LONG).show();
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            tryToConnectToPairedDevices()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            y = event.values[1]
            x = event.values[0]
            //engine.steer(x, y)
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        // repeat that line for each sensor you want to monitor
    }

    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(this);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                    initializeBluetoothEngine(device)

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            unregisterReceiver(receiver)
        } catch (ex: Exception) {
            Log.e(TAG,"cannot deregister receiver")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                tryToConnectToPairedDevices()
                return
            }
        } else if (requestCode == DISCOVER_BLUETOOTH_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                return
            }
        }

        Toast.makeText(applicationContext, "cannot use application", Toast.LENGTH_LONG).show();
    }


    fun tryToConnectToPairedDevices() {
        // looking for already known devices
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val connected : Boolean = pairedDevices.orEmpty().map { device -> initializeBluetoothEngine(device) }.any { res -> res }
        if(!connected) {
            // Register for broadcasts when a device is discovered.
            registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        }

    }

    fun initializeBluetoothEngine(device: BluetoothDevice) : Boolean {
        Log.i(TAG,"try to connecting to ${device.name} ${device.address}")
        if(DEVICE_NAME.contentEquals(device.name)) {
            Log.d(TAG,"connected to ${device.name}")
            initializeView(DrivingCommand(BluetoothEngine(device, Handler() { it ->
                Log.d(TAG, "return $it")
                Toast.makeText(applicationContext, it.obj.toString(), Toast.LENGTH_SHORT).show()
                true
            })))
            return true
        }
        return false
    }


    fun initializeView(drivingCommand: DrivingCommand) : Unit {
        throttlePedal.setOnTouchListener(onThrottlePedalClicked(drivingCommand))
        reversePedal.setOnTouchListener(onReversePedalClicked(drivingCommand))
        breakPedal.setOnTouchListener(onBreakPedalClicked(drivingCommand))
    }

    fun onThrottlePedalClicked(engine: DrivingCommand): View.OnTouchListener? {
        return RepeatListener(0, 250,
            View.OnClickListener {
                engine.accelerate(x, y)
            })
    }
    fun onReversePedalClicked(engine: DrivingCommand): View.OnTouchListener? {
        return RepeatListener(0, 250,
            View.OnClickListener {
                engine.reverse(x, y)
            })
    }
    fun onBreakPedalClicked(engine: DrivingCommand): View.OnTouchListener? {
        return RepeatListener(0, 250,
            View.OnClickListener {
                engine.slowDown()
            })
    }
}
