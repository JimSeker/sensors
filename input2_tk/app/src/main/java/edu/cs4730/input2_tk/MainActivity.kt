package edu.cs4730.input2_tk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import android.view.GestureDetector.OnGestureListener
import android.view.GestureDetector.OnDoubleTapListener

import androidx.core.view.GestureDetectorCompat

/**
 * This is example is copied from the input2 java version.
 * It sets a gesture detector on the "screen".  There is no real layout.
 * needs testing with say a edittext field and button to see what if anything breaks.
 * <p>
 * we are overriding the default ontouchlistener (onTouchEvent) to call the gesture detector.
 * <p>
 * Also overriding the default onKeyDown()  for the activity, this maybe not be good idea
 * if there are widgets that also need the key events such as a EditText.
 * <p>
 * Overriding the backbuttonpressed as well.  it just calls finish().
 */


class MainActivity : AppCompatActivity(), OnGestureListener, OnDoubleTapListener {

    val TAG = "Gestures"
    var mDetector: GestureDetectorCompat? = null
    val chars = charArrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', '0'
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instantiate the gesture detector with the application context and an implementation of GestureDetector.OnGestureListener
        mDetector = GestureDetectorCompat(this, this)
        // Also Set the gesture detector as the double tap  listener.  See the overrides below for which events comes from which.
        mDetector!!.setOnDoubleTapListener(this)
    }

    /**
     * Keyevent that comes from the activity.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val key = event.getMatch(chars)
        if (key != '\u0000') {
            Log.d(TAG, "onKeyDown: $key")
            Toast.makeText(applicationContext, "onKeyDown: $key", Toast.LENGTH_SHORT).show()
            return true
        }
        //allow the system to deal with the events we do not, like all the rest of the buttons.
        return super.onKeyDown(keyCode, event)
    }

    /**
     * This is really not recommended by well anyone.  You are change the default function of the device.
     * users really hate when you do that.
     */
    override fun onBackPressed() {
        // do something on back.
        Log.d(TAG, "onBackPressed: it was pushed.")
        Toast.makeText(applicationContext, "onBackPressed: it was pushed.", Toast.LENGTH_SHORT)
            .show()
        finish() //we have to manually end the program, since we captured the backup button.
    }

    /**
     * touch event that comes from he activity.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector!!.onTouchEvent(event)
        //log it, but no toast here.
        Log.d(TAG, "onTouchEvent: $event")
        // Be sure to call the superclass implementation, since we are not handling anything here.
        return super.onTouchEvent(event)
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onDown(event: MotionEvent): Boolean {
        Log.d(TAG, "onDown: $event")
        Toast.makeText(applicationContext, "onDown: $event", Toast.LENGTH_SHORT).show()
        return true
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onFling(
        event1: MotionEvent, event2: MotionEvent,
        velocityX: Float, velocityY: Float
    ): Boolean {
        val msg = "onFling: $event1$event2"
        Log.d(TAG, msg)
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        return true
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onLongPress(event: MotionEvent) {
        Toast.makeText(applicationContext, "onLongPress: $event", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onLongPress: $event")
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onScroll(
        e1: MotionEvent, e2: MotionEvent, distanceX: Float,
        distanceY: Float
    ): Boolean {
        val msg = "onScroll: $e1$e2"
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
        Log.d(TAG, msg)
        return true
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onShowPress(event: MotionEvent) {
        Toast.makeText(applicationContext, "onShowPress: $event", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onShowPress: $event")
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        Toast.makeText(applicationContext, "onSingleTapUp: $event", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onSingleTapUp: $event")
        return true
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    override fun onDoubleTap(event: MotionEvent): Boolean {
        Toast.makeText(applicationContext, "onDoubleTap: $event", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onDoubleTap: $event")
        return true
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        Toast.makeText(applicationContext, "onDoubleTapEvent: $event", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onDoubleTapEvent: $event")
        return true
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        Toast.makeText(applicationContext, "onSingleTapConfirmed: $event", Toast.LENGTH_SHORT)
            .show()
        Log.d(TAG, "onSingleTapConfirmed: $event")
        return true
    }


}