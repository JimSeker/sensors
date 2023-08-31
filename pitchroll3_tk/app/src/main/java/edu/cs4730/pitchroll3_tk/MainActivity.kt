package edu.cs4730.pitchroll3_tk

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import edu.cs4730.pitchroll3_tk.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val TAG = "MainActivity"
    private lateinit var mgr: SensorManager
    private var accel: Sensor? = null
    private var compass: Sensor? = null

    private lateinit var binding: ActivityMainBinding
    private var ready = false
    private val accelValues = FloatArray(3)
    private val compassValues = FloatArray(3)
    private val inR = FloatArray(9)
    private val inclineMatrix = FloatArray(9)
    private val prefValues = FloatArray(3)
    private var mAzimuth = 0f
    private var mPitch = 0.0
    private var mInclination = 0.0
    private val counter = 0
    private var mRotation = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mgr = this.getSystemService(SENSOR_SERVICE) as SensorManager
        accel = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        compass = mgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val window = this.getSystemService(WINDOW_SERVICE) as WindowManager
        mRotation = window.defaultDisplay.rotation
    }

    override fun onResume() {
        mgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        mgr.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL)
        super.onResume()
    }

    override fun onPause() {
        mgr.unregisterListener(this, accel)
        mgr.unregisterListener(this, compass)
        super.onPause()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // ignore
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Need to get both accelerometer and compass
        // before we can determine our orientationValues
        // Log.wtf(TAG, "onSensorChanged.");
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                var i = 0
                while (i < 3) {
                    accelValues[i] = event.values[i]
                    i++
                }
                if (compassValues[0] != 0F) ready = true
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                var i = 0
                while (i < 3) {
                    compassValues[i] = event.values[i]
                    i++
                }
                if (accelValues[2] != 0F) ready = true
            }
        }
        if (!ready) {
            binding.preferred.text = "Not enough data yet."
            return
        }
        if (SensorManager.getRotationMatrix(
                inR, inclineMatrix, accelValues, compassValues
            )
        ) {
            // got a good rotation matrix
            SensorManager.getOrientation(inR, prefValues)
            mInclination = SensorManager.getInclination(inclineMatrix).toDouble()
            // Display every 10th value
            //if(counter++ % 10 == 0) {
            doUpdate()
            //				counter = 1;
            //}
        }
    }

    fun doUpdate() {
        if (!ready) {
            binding.preferred.text = "Not enough data yet2."
            return
        }
        // Log.wtf(TAG, "doUpdate.");
        mAzimuth = Math.toDegrees(prefValues[0].toDouble()).toFloat()
        if (mAzimuth < 0) {
            mAzimuth += 360.0f
        }
        //mPitch = 180.0  + Math.toDegrees(prefValues[1]); //so it goes from 0 to 360, instead of -180 to 180
        mPitch = Math.toDegrees(prefValues[1].toDouble())
        val msg = String.format(
            Locale.getDefault(),
            "Preferred:\nazimuth (Z): %7.3f \npitch (X): %7.3f\nroll (Y): %7.3f",
            mAzimuth,  //heading
            mPitch,
            Math.toDegrees(prefValues[2].toDouble())
        )
        binding.preferred.text = msg
    }


}