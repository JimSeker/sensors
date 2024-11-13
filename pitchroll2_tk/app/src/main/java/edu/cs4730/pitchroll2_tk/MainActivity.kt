package edu.cs4730.pitchroll2_tk

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cs4730.pitchroll2_tk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var binding: ActivityMainBinding

    /* sensor data */
    lateinit var m_sensorManager: SensorManager
    var m_lastMagFields: FloatArray? = null
    var m_lastAccels: FloatArray? = null
    private val m_rotationMatrix = FloatArray(16)
    private val m_remappedR = FloatArray(16)
    private val m_orientation = FloatArray(4)

    /* fix random noise by averaging tilt values */
    val AVERAGE_BUFFER = 30
    var m_prevPitch = FloatArray(AVERAGE_BUFFER)
    var m_lastPitch = 0f
    var m_lastYaw = 0f

    /* current index int m_prevEasts */
    var m_pitchIndex = 0

    var m_prevRoll = FloatArray(AVERAGE_BUFFER)
    var m_lastRoll = 0f

    /* current index into m_prevTilts */
    var m_rollIndex = 0

    /* center of the rotation */
    private val m_tiltCentreX = 0f
    private val m_tiltCentreY = 0f
    private val m_tiltCentreZ = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        m_sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        registerListeners()
    }

    private fun registerListeners() {
        m_sensorManager.registerListener(
            this,
            m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
        m_sensorManager.registerListener(
            this,
            m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun unregisterListeners() {
        m_sensorManager.unregisterListener(this)
    }


    override fun onDestroy() {
        unregisterListeners()
        super.onDestroy()
    }

    override fun onPause() {
        unregisterListeners()
        super.onPause()
    }

    override fun onResume() {
        registerListeners()
        super.onResume()
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accel(event)
        }
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mag(event)
        }
    }

    private fun accel(event: SensorEvent) {
        if (m_lastAccels == null) {
            m_lastAccels = FloatArray(3)
        }
        System.arraycopy(event.values, 0, m_lastAccels, 0, 3)

        /*if (m_lastMagFields != null) {
            computeOrientation();
        }*/
    }

    private fun mag(event: SensorEvent) {
        if (m_lastMagFields == null) {
            m_lastMagFields = FloatArray(3)
        }
        System.arraycopy(event.values, 0, m_lastMagFields, 0, 3)
        if (m_lastAccels != null) {
            computeOrientation()
        }
    }

    var m_filters = arrayOf(Filter(), Filter(), Filter())

    class Filter {
        var m_arr = FloatArray(AVERAGE_BUFFER)
        var m_idx = 0
        fun append(`val`: Float): Float {
            m_arr[m_idx] = `val`
            m_idx++
            if (m_idx == AVERAGE_BUFFER) m_idx = 0
            return avg()
        }

        fun avg(): Float {
            var sum = 0f
            for (x in m_arr) sum += x
            return sum / AVERAGE_BUFFER
        }

        companion object {
            const val AVERAGE_BUFFER = 10
        }
    }

    private fun computeOrientation() {
        if (SensorManager.getRotationMatrix(
                m_rotationMatrix, null, m_lastAccels, m_lastMagFields
            )
        ) {
            SensorManager.getOrientation(m_rotationMatrix, m_orientation)

            /* 1 radian = 57.2957795 degrees *//* [0] : yaw, rotation around z axis
             * [1] : pitch, rotation around x axis
             * [2] : roll, rotation around y axis */
            val yaw = m_orientation[0] * 57.2957795f
            val pitch = m_orientation[1] * 57.2957795f
            val roll = m_orientation[2] * 57.2957795f
            m_lastYaw = m_filters[0].append(yaw)
            m_lastPitch = m_filters[1].append(pitch)
            m_lastRoll = m_filters[2].append(roll)
            binding.yaw.text = "azi z: $m_lastYaw"
            binding.pitch.text = "pitch x: $m_lastPitch"
            binding.roll.text = "roll y: $m_lastRoll"
        }
    }

}