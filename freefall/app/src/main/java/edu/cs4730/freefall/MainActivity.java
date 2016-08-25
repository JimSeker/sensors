package edu.cs4730.freefall;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/*
 * This an example for a statement in class, where I said, "you can make a phone scream when it is falling"
 * 
 * so this is what this code attempts to do.  when the phone is falling, it will play a scream.
 * 
 * NOTE: This code is not perfect, nor indented to be.  It should only be used for fun with something very soft 
 * for the phone to land on.   It was only tested with a height of maybe foot and landed on very soft pillows.
 * 
 * USE AT YOUR OWN RISK OF BREAKING YOUR DEVICE!
 * 
 * some information was gotten here:
 * http://stackoverflow.com/questions/4848490/android-how-to-approach-fall-detection-algorithm
 * 
 * This may also be useful, but I didn't use any it's code.
 * https://github.com/BharadwajS/Fall-detection-in-Android
 */



public class MainActivity extends AppCompatActivity implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	Boolean Falling = false;
	MediaPlayer mediaPlayer;

	TextView ev0, freefall;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ev0 = (TextView) findViewById(R.id.tv_ev0);


		freefall = (TextView) findViewById(R.id.fall);		

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Falling = false;
	}


	@Override
	protected void onResume() {
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		super.onResume();
		Falling = false;
	}
	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(this, mSensor);
		KillMediaPlayer();
		super.onPause();
		Falling = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {


		//SQRT(x*x + y*y + z*z).
		double vector=Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
		//9.8 m/s is basically not moving
		//3.0 m/s or less is basically falling.
		//20 m/s is landing ish, based on what I read.
		

		ev0.setText(String.valueOf(vector));
		//logthis(ev0.getText().toString());

		if (vector <=3.0) { // 3 m/s should be falling, I think...
			Falling = true;
			playsnd();
		} else {
			Falling = false;
		}
		if (Falling) {
			freefall.setText("Falling!");
		} else {
			freefall.setText("not");
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// I don't care.

	}

	public void playsnd() {
		if (mediaPlayer == null) { //first time
			mediaPlayer =  MediaPlayer.create(getBaseContext(),R.raw.hmscream);
		} else if (mediaPlayer.isPlaying()) { //duh don't start it again.
			//Toast.makeText(getBaseContext(), "I'm playing already", Toast.LENGTH_SHORT).show();
			return;
		} else { //play it at least one, reset and play again.
			mediaPlayer.seekTo(0);
		}
		mediaPlayer.start();
	}


	void KillMediaPlayer() {
		if (mediaPlayer != null)
			mediaPlayer.release();
	}
	
	public void logthis(String text) {
		Log.v("freefall", text);
	}

}
