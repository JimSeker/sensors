package edu.cs4730.input2_tk

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.OnGestureListener
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.cs4730.input2_tk.databinding.ActivityMainBinding

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

    private val TAG = "Gestures"
    private lateinit var binding: ActivityMainBinding
    private lateinit var mDetector: GestureDetector
    private val chars = charArrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', '0'
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        // Instantiate the gesture detector with the application context and an implementation of GestureDetector.OnGestureListener
        mDetector = GestureDetector(this, this)
        // Also Set the gesture detector as the double tap  listener.  See the overrides below for which events comes from which.
        mDetector.setOnDoubleTapListener(this)


//      This is really not recommended by well anyone.  You are change the default function of the device.
//      users really hate when you do that.  the override for onBackPressed() is deprecated.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d(TAG, "onBackPressed: it was pushed.")
                Toast.makeText(
                    applicationContext,
                    "onBackPressed: it was pushed.",
                    Toast.LENGTH_SHORT
                ).show()
                finish() //we have to manually end the program, since we captured the backup button.
            }
        })


    }

    /**
     * Keyevent that comes from the activity.
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val key = event.getMatch(chars)
        if (key != '\u0000') {
            logthis("onKeyDown: $key")
            return true
        }
        //allow the system to deal with the events we do not, like all the rest of the buttons.
        return super.onKeyDown(keyCode, event)
    }

    /**
     * touch event that comes from he activity.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        //log it, but no toast here.
        Log.d(TAG, "onTouchEvent: $event")
        // Be sure to call the superclass implementation, since we are not handling anything here.
        return super.onTouchEvent(event)
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onDown(event: MotionEvent): Boolean {
        logthis("onDown: $event")
        return true
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onFling(
        event1: MotionEvent?, event2: MotionEvent,
        velocityX: Float, velocityY: Float
    ): Boolean {
        val msg = "onFling: $event1$event2"
        logthis(msg)
        return true
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onLongPress(event: MotionEvent) {
        logthis("onLongPress: $event")
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onScroll(
        e1: MotionEvent?, e2: MotionEvent, distanceX: Float,
        distanceY: Float
    ): Boolean {
        val msg = "onScroll: $e1$e2"
        logthis(msg)
        return true
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onShowPress(event: MotionEvent) {
        logthis("onShowPress: $event")
    }

    /**
     * overridden from the OnGuestureListener.
     */
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        logthis("onSingleTapUp: $event")
        return true
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    override fun onDoubleTap(event: MotionEvent): Boolean {
        logthis("onDoubleTap: $event")
        return true
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        logthis("onDoubleTapEvent: $event")
        return true
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        logthis("onSingleTapConfirmed: $event")
        return true
    }

    private fun logthis(item: String) {
        Toast.makeText(applicationContext, "onSingleTapConfirmed: $item", Toast.LENGTH_SHORT).show()
        Log.d(TAG, item)
    }
}