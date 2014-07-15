package edu.cs4730.input2;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;


/*
 * example from http://developer.android.com/training/gestures/detector.html
 * 
 * sets a gesture detector on the "screen".     There is no real layout.
 *  needs testing with say a edittext field and button to see what if anything breaks.
 *  
 *  we are overriding the default ontouchlistener (onTouchEvent) to call the gesture detector.
 *  
 *  Also overriding the default onKeyDown()  for the activity, this maybe not be good idea
 *  if there are widgets that also need the key events such as a EditText.
 *  
 *  Overriding the backbuttonpressed as well.  it just calls finish().
 */

public class MainActivity extends FragmentActivity implements  OnGestureListener, OnDoubleTapListener {

	private static final String DEBUG_TAG = "Gestures";
	private GestureDetectorCompat mDetector; 
	char[] chars = {'a','b','c','d','e', 'f', 'g', 'h', 'i', 'j', 'k', 'l','m', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
 
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new MainFragment()).commit();
		}
		
		
		// Instantiate the gesture detector with the
		// application context and an implementation of
		// GestureDetector.OnGestureListener
		mDetector = new GestureDetectorCompat(this,this);
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
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onKeyDown(int, android.view.KeyEvent)
     * 
     * Keyevent that comes from the activity.
     */
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		char key = event.getMatch(chars);
		if (key != '\0') {
			Log.d(DEBUG_TAG,"onKeyDown: " + key); 
			Toast.makeText(getApplicationContext(), "onKeyDown: " + key, Toast.LENGTH_SHORT).show();
			return true;
		} 
		//allow the system to deal with the events we do not, like all the rest of the buttons.
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 * 
	 * 
	 * This is really not recommended by well anyone.  You are change the default function of the device.
	 * users really hate when you do that.  
	 */
			
	@Override
	public void onBackPressed() {
		// do something on back.
		Log.d(DEBUG_TAG,"onBackPressed: it was pushed."); 
		Toast.makeText(getApplicationContext(), "onBackPressed: it was pushed.", Toast.LENGTH_SHORT).show();
		
		finish();  //we have to manually end the program, since we captured the backup button.
	return;
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 * 
	 * touch event that comes from he activity.  
	 */
	
	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
		this.mDetector.onTouchEvent(event);
         //log it, but no toast here.
		Log.d(DEBUG_TAG,"onTouchEvent: " + event.toString()); 
		// Be sure to call the superclass implementation, since we are not handling anything here.
		return super.onTouchEvent(event);
	}

	
	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 * 
	 * overridden from the OnGuestureListener.
	 */
	@Override
	public boolean onDown(MotionEvent event) { 
		Log.d(DEBUG_TAG,"onDown: " + event.toString()); 
		Toast.makeText(getApplicationContext(), "onDown: " + event.toString(), Toast.LENGTH_SHORT).show();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 * 
	 * overridden from the OnGuestureListener.
	 */
	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, 
			float velocityX, float velocityY) {
		Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
		Toast.makeText(getApplicationContext(), "onFling: " + event1.toString()+event2.toString(), Toast.LENGTH_SHORT).show();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 * 
	 * overridden from the OnGuestureListener.
	 */
	@Override
	public void onLongPress(MotionEvent event) {
		Toast.makeText(getApplicationContext(), "onLongPress: " + event.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onLongPress: " + event.toString()); 
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 * 
	 * overridden from the OnGuestureListener.
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		Toast.makeText(getApplicationContext(), "onScroll: " + e1.toString()+e2.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 * 
	 * overridden from the OnGuestureListener.
	 */
	@Override
	public void onShowPress(MotionEvent event) {
		Toast.makeText(getApplicationContext(), "onShowPress: " + event.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 * 
	 * overridden from the OnGuestureListener.
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
		Toast.makeText(getApplicationContext(), "onSingleTapUp: " + event.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onDoubleTap(android.view.MotionEvent)
	 * 
	 * overridden from the OnDoubleTapListener
	 */
	@Override
	public boolean onDoubleTap(MotionEvent event) {
		Toast.makeText(getApplicationContext(), "onDoubleTap: " + event.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onDoubleTapEvent(android.view.MotionEvent)
	 * 
	 * overridden from the OnDoubleTapListener
	 */
	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		Toast.makeText(getApplicationContext(), "onDoubleTapEvent: " + event.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.GestureDetector.OnDoubleTapListener#onSingleTapConfirmed(android.view.MotionEvent)
	 * 
	 * overridden from the OnDoubleTapListener
	 */
	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		Toast.makeText(getApplicationContext(), "onSingleTapConfirmed: " + event.toString(), Toast.LENGTH_SHORT).show();
		Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
		return true;
	}



}
