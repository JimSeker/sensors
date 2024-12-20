package edu.cs4730.a8ball;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.cs4730.a8ball.databinding.ActivityMainBinding;

/**
 * A simple "fun" example of how to use the sensors to detect a shake.
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int shake = 0;
    float threshold = 1.9f;  //hand set, based on the phone and log.v info.

    ActivityMainBinding binding;

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
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);  //exclude gravity in the
    }

    /**
     * When the screen is visible, register the sensor to see if the user has "shaken" the 8ball.
     */
    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    /**
     * When the screen is not visible, unregister listener.
     */
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this, mSensor);
        super.onPause();
    }

    /**
     * necessary methods to implement for the sensor listener.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //figuration the acceleration (without gravity) to determine is if the device is being "shaken".
        double totalForce = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
        //Log.v("TAG", "force is " + totalForce);
        if (totalForce > threshold) {
            //it's being shaken now.
            shake++;
            //Log.v("TAG", "Shake! " + shake);
        } else {
            //user stopped to read the result.
            if (shake > 1) {
                // Log.v("TAG", "new text! ");
                binding.ball.changeText();
                shake = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //don't care, but required method.
    }

}
