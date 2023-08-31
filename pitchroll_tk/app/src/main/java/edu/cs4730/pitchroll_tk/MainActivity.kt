package edu.cs4730.pitchroll_tk

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.cs4730.pitchroll_tk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var myManager: SensorManager? = null
    private var accSensor: Sensor? = null
    private var sensors: List<Sensor>? = null
    var mySensorListener: SensorEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);

        findViewById<View>(R.id.Button01).setOnClickListener {
            binding.txtsx.text = binding.txtcx.text
            binding.txtsy.text = binding.txtcy.text
            binding.txtsz.text = binding.txtcz.text
        }
        registerAccelerometer()
    }

    override fun onResume() {
        super.onResume()
        registerAccelerometer()
    }

    override fun onPause() {
        super.onPause()
        unregisterAccelerometer()
    }

    override fun onStop() {
        super.onStop()
        unregisterAccelerometer()
    }

    fun registerAccelerometer() {
        myManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensors = myManager!!.getSensorList(Sensor.TYPE_ORIENTATION)
        if (sensors!!.isNotEmpty()) accSensor = sensors!![0]
        mySensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // I have no desire to deal with the accuracy events
            }
            
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    binding.txtcz.text =
                        "cz: " + event.values[0].toString() //azimuth, on z axis between -180 and 180
                    binding.txtcy.text =
                        "cy: " + event.values[1].toString() //pitch rotation, on y axis between -180 and 180
                    binding.txtcx.text =
                        "cx: " + event.values[2].toString() //roll rotation on the x axis  between -180 and 180
                }
            }
        }
        myManager!!.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun unregisterAccelerometer() {
        if (myManager != null && mySensorListener != null) {
            myManager!!.unregisterListener(mySensorListener)
        }
        myManager = null
        mySensorListener = null
    }
}