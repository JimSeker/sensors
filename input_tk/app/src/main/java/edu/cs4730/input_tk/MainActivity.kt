package edu.cs4730.input_tk

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

/**
 * An example of how to access the touch, tap, double, and Gestures on the screen.
 * Also how to get keys (hardware or software keyboard)
 *
 * The orientation sensor is also used, and yea, it's is depreciated.
 *
 */

@SuppressLint("StaticFieldLeak")
class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var label1: TextView
        lateinit var label2: TextView
        lateinit var label3: TextView
        lateinit var label4: TextView
        lateinit var iv: ImageView

        lateinit var context: Context
        var myManager: SensorManager? = null
        lateinit var accSensor: Sensor
        lateinit var sensors: List<Sensor>
        var mySensorListener: SensorEventListener? = null

        lateinit var mGestureDetector: GestureDetector
        lateinit var mGestureListener: OnTouchListener
        val chars = charArrayOf('a', 'b', 'c', 'd', 'e')

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        label1 = findViewById(R.id.label1)
        label2 = findViewById(R.id.label2)
        label3 = findViewById(R.id.label3)
        label4 = findViewById(R.id.label4)
        iv = findViewById(R.id.ImageView01)


        iv.setOnKeyListener(myKeyListener())

        iv.setOnClickListener(myClickListener())
        iv.setOnLongClickListener(myLongClickListener())

        mGestureListener = myTouchListener()
        mGestureDetector = GestureDetector(context, MyGestureDetector())
        iv.setOnTouchListener(mGestureListener);
    }

    override fun onPause() {
        unregisterAccelerometer()
        super.onPause()
    }

    override fun onResume() {
        registerAccelerometer()
        super.onResume()
    }


    internal class myKeyListener : View.OnKeyListener {
        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            val key = event.getMatch(chars)
            if (key != '\u0000') {  //no idea why it won't let me use \0
                label1.text = "Key: $key"
                return true
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                label1.text = "Key: menu"
                return false //allow android to still use the key as well.
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                label1.text = "Key: back"
                return true //don't allow android, since it kills the program.
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                label2.text = "Navigation: DOWN"
                return true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                label2.text = "Navigation: UP"
                return true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                label2.text = "Navigation: LEFT"
                return true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                label2.text = "Navigation: RIGHT"
                return true
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                label2.text = "Navigation: CENTER"
                return true
            }
            label1.text = "Key: "
            return false
        }
    }

    internal class myClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            Toast.makeText(context, "Click!", Toast.LENGTH_SHORT).show()
        }
    }

    internal class myLongClickListener : OnLongClickListener {
        override fun onLongClick(v: View): Boolean {
            Toast.makeText(context, "Long Click!", Toast.LENGTH_SHORT).show()
            return true
        }
    }

    internal class myTouchListener : OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (mGestureDetector.onTouchEvent(event)) return true

            //Either not a gesture or not supposed to use them anyway.
            val action = event.action
            // Retrieve the new x and y touch positions
            val x = event.x.toInt()
            val y = event.y.toInt()
            label3.text = "Touch: x=$x y=$y"
            when (action) {
                MotionEvent.ACTION_MOVE -> {
                    label2.text = "Navigation: touch move"
                    return true
                }
            }
            return false
        }
    }

    internal class MyGestureDetector : SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val dX = e2.x - e1.x
            val dY = e1.y - e2.y
            if (Math.abs(dY) < SWIPE_MAX_OFF_PATH &&
                Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY &&
                Math.abs(dX) >= SWIPE_MIN_DISTANCE ) {
                if (dX > 0) {
                    Toast.makeText(context, "Right Swipe", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Left Swipe", Toast.LENGTH_SHORT).show()
                }
                return true
            } else if (Math.abs(dX) < SWIPE_MAX_OFF_PATH &&
                Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY
                && Math.abs(dY) >= SWIPE_MIN_DISTANCE ) {
                if (dY > 0) {
                    Toast.makeText(context, "Up Swipe", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Down Swipe", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            return false
        }

        companion object {
            private const val SWIPE_MIN_DISTANCE = 100 //150
            private const val SWIPE_MAX_OFF_PATH = 100
            private const val SWIPE_THRESHOLD_VELOCITY = 75 //100
        }
    }

    fun registerAccelerometer() {
        myManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        //        sensors = myManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
//        if(sensors.size() > 0)
//          accSensor = sensors.get(0);
        accSensor = myManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        mySensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // I have no desire to deal with the accuracy events
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ORIENTATION) {
                    val Sensorhold = 45 //about the angle the phone is held at.
                    val SensorThreshold =
                        15 // how far the phone needs be pitched/rolled for an effect.
                    val sy = event.values[1] //pitch rotation, on y axis between -180 and 180
                    val sx = event.values[2] //roll rotation on the x axis  between -180 and 180
                    //Toast.makeText(context, "Orientation: x:"+sx+" y:"+sy, Toast.LENGTH_SHORT).show();
                    if (sx >= SensorThreshold || sx <= -SensorThreshold || //x value is valid
                        sy >= SensorThreshold + Sensorhold || //y value is valid
                        sy <= -SensorThreshold + Sensorhold ) {
                        /*
        				 * valid value based on thresholding, now figure out
        				 * which direction to take is x ">" then y including negative direction
        				 * note -x is left is lifted, +x is right is lifted  (based on default orientation)
        				 *      +y is top is lifted and -y is bottom is lifted.
        				 *      But we need to reverse, because with want it goes "down", instead of lifted.
        				 */
                        if (Math.abs(sx) >= Math.abs(sy)) {  //sx value is the one to take
                            if (sx > 0) //go right lifted, so go left
                                label4.text = "Sensor: x:$sx y:$sy  Rightside up"
                            else
                                label4.text = "Sensor: x:$sx y:$sy  leftside up"
                        } else {  //up or down, sy value is bigger.
                            if (sy > 0)
                                label4.text = "Sensor: x:$sx y:$sy  Bottom  up"
                            else  //TOP is lifted, so go down
                                label4.text = "Sensor: x:$sx y:$sy top up"
                        }
                    }
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