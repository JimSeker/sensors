package edu.cs4730.input2;

import android.os.Bundle;

import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.GestureDetector.OnGestureListener;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;


/**
 * example from http://developer.android.com/training/gestures/detector.html
 * <p>
 * sets a gesture detector on the "screen".     There is no real layout.
 * needs testing with say a edittext field and button to see what if anything breaks.
 * <p>
 * we are overriding the default ontouchlistener (onTouchEvent) to call the gesture detector.
 * <p>
 * Also overriding the default onKeyDown()  for the activity, this maybe not be good idea
 * if there are widgets that also need the key events such as a EditText.
 * <p>
 * Overriding the backbuttonpressed as well.  it just calls finish().
 * <p>
 * As a note, I "broke" a nexus 5x with this example.  The toasts would not go away, even after 20 minutes
 * I had to reboot it.    The stop button may fix the problem as well (in studio).
 */

public class MainActivity extends AppCompatActivity implements OnGestureListener, OnDoubleTapListener {

    private static final String TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetectorCompat(this, this);
        // Also Set the gesture detector as the double tap
        // listener.  See the overrides below for which events comes from which.
        mDetector.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Keyevent that comes from the activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        char key = event.getMatch(chars);
        if (key != '\0') {
            Log.d(TAG, "onKeyDown: " + key);
            Toast.makeText(getApplicationContext(), "onKeyDown: " + key, Toast.LENGTH_SHORT).show();
            return true;
        }
        //allow the system to deal with the events we do not, like all the rest of the buttons.
        return super.onKeyDown(keyCode, event);
    }

    /**
     * This is really not recommended by well anyone.  You are change the default function of the device.
     * users really hate when you do that.
     */
    @Override
    public void onBackPressed() {
        // do something on back.
        Log.d(TAG, "onBackPressed: it was pushed.");
        Toast.makeText(getApplicationContext(), "onBackPressed: it was pushed.", Toast.LENGTH_SHORT).show();

        finish();  //we have to manually end the program, since we captured the backup button.

    }

    /**
     * touch event that comes from he activity.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        //log it, but no toast here.
        Log.d(TAG, "onTouchEvent: " + event.toString());
        // Be sure to call the superclass implementation, since we are not handling anything here.
        return super.onTouchEvent(event);
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(TAG, "onDown: " + event.toString());
        Toast.makeText(getApplicationContext(), "onDown: " + event.toString(), Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        String msg = "onFling: " + event1.toString() + event2.toString();
        Log.d(TAG, msg);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public void onLongPress(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "onLongPress: " + event.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLongPress: " + event.toString());
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        String msg = "onScroll: " + e1.toString() + e2.toString();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
        return true;
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public void onShowPress(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "onShowPress: " + event.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onShowPress: " + event.toString());
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "onSingleTapUp: " + event.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "onDoubleTap: " + event.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "onDoubleTapEvent: " + event.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Toast.makeText(getApplicationContext(), "onSingleTapConfirmed: " + event.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }


}
