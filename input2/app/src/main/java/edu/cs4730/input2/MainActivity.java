package edu.cs4730.input2;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import edu.cs4730.input2.databinding.ActivityMainBinding;


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

    ActivityMainBinding binding;

    private static final String TAG = "Gestures";
    private GestureDetector mDetector;
    char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        mDetector = new GestureDetector(this, this);
        // Also Set the gesture detector as the double tap
        // listener.  See the overrides below for which events comes from which.
        mDetector.setOnDoubleTapListener(this);


//      This is really not recommended by well anyone.  You are change the default function of the device.
//      users really hate when you do that.  the override for onBackPressed() is deprecated.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "onBackPressed: it was pushed.");
                Toast.makeText(getApplicationContext(), "onBackPressed: it was pushed.", Toast.LENGTH_SHORT).show();
                finish();  //we have to manually end the program, since we captured the backup button.
            }
        });


    }

    /**
     * Keyevent that comes from the activity.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        char key = event.getMatch(chars);
        if (key != '\0') {
            logthis("onKeyDown: " + key);
            return true;
        }
        //allow the system to deal with the events we do not, like all the rest of the buttons.
        return super.onKeyDown(keyCode, event);
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
        logthis("onDown: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        String msg = "onFling: " + event1.toString() + event2.toString();
        logthis(msg);
        return true;
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public void onLongPress(MotionEvent event) {

        logthis("onLongPress: " + event.toString());
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        String msg = "onScroll: " + e1.toString() + e2.toString();
        logthis(msg);
        return true;
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public void onShowPress(MotionEvent event) {
        logthis("onShowPress: " + event.toString());
    }

    /**
     * overridden from the OnGuestureListener.
     */
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        logthis("onSingleTapUp: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    @Override
    public boolean onDoubleTap(MotionEvent event) {

        logthis("onDoubleTap: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        logthis("onDoubleTapEvent: " + event.toString());
        return true;
    }

    /**
     * overridden from the OnDoubleTapListener
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        logthis("onSingleTapConfirmed: " + event.toString());
        return true;
    }

    void logthis(String item) {
        Toast.makeText(getApplicationContext(),  item, Toast.LENGTH_SHORT).show();
        Log.d(TAG,  item);
    }
}
