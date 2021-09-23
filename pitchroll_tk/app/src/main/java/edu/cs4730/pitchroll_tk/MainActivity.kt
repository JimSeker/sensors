package edu.cs4730.pitchroll_tk

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var myManager: SensorManager? = null
    private var accSensor: Sensor? = null
    private var sensors: List<Sensor>? = null
    var mySensorListener: SensorEventListener? = null
    lateinit var cx: TextView
    lateinit var cy: TextView
    lateinit var cz: TextView
    lateinit var sx: TextView
    lateinit var sy: TextView
    lateinit var sz: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cx = findViewById(R.id.txtcx)
        cy = findViewById(R.id.txtcy)
        cz = findViewById(R.id.txtcz)
        sx = findViewById(R.id.txtsx)
        sy = findViewById(R.id.txtsy)
        sz = findViewById(R.id.txtsz)
        findViewById<View>(R.id.Button01).setOnClickListener {
            sx.text = cx.text
            sy.text = cy.text
            sz.text = cz.text
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
                    cz.text =
                        "cz: " + event.values[0].toString() //azimuth, on z axis between -180 and 180
                    cy.text =
                        "cy: " + event.values[1].toString() //pitch rotation, on y axis between -180 and 180
                    cx.text =
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