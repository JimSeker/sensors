package edu.cs4730.a8ball;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    EightBall myEightBall;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    int shake = 0;
    float threshold  = 1.9f;  //hand set, based on the phone and log.v info.

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myEightBall = (EightBall) findViewById(R.id.ball);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);  //exclude gravity in the
    }

    /*
     * When the screen is visible, register the sensor to see if the user has "shaken" the 8ball.
     */
    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    /*
     * When the screen is not visible, unregister listener.
     */

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this, mSensor);
        super.onPause();
    }

    /*
     * necessary methods to implement for the sensor listener.
     */

    @Override
    public void onSensorChanged(SensorEvent event) {
        //figuration the acceralation (without gravity) to determine is if the device is being "shaken".
        double totalForce =Math.sqrt(event.values[0]*event.values[0]+event.values[1]*event.values[1]+event.values[2]*event.values[2]);
        //Log.v("TAG", "force is " + totalForce);
        if (totalForce > threshold) {
            //it's being shaken now.
            shake++;
            //Log.v("TAG", "Shake! " + shake);
        } else {
            //user stopped to read the result.
            if (shake >1) {
               // Log.v("TAG", "new text! ");
                myEightBall.changeText();
                shake = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //don't care, but required method.
    }

}
