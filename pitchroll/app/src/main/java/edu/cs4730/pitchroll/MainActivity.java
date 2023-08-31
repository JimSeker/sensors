package edu.cs4730.pitchroll;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import edu.cs4730.pitchroll.databinding.ActivityMainBinding;

/**
 * This example is using the deprecated API for the Orientation sensor.
 */

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private SensorManager myManager;
    private Sensor accSensor;
    private List<Sensor> sensors;
    SensorEventListener mySensorListener;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        findViewById(R.id.Button01).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.txtsx.setText(binding.txtcx.getText());
                binding.txtsy.setText(binding.txtcy.getText());
                binding.txtsz.setText(binding.txtcz.getText());
            }
        });
        registerAccelerometer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerAccelerometer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterAccelerometer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterAccelerometer();
    }

    void registerAccelerometer() {
        myManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors = myManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size() > 0)
            accSensor = sensors.get(0);

        mySensorListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // I have no desire to deal with the accuracy events

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                    binding.txtcz.setText("cz: " + String.valueOf(event.values[0]));  //azimuth, on z axis between -180 and 180
                    binding.txtcy.setText("cy: " + String.valueOf(event.values[1]));  //pitch rotation, on y axis between -180 and 180
                    binding.txtcx.setText("cx: " + String.valueOf(event.values[2]));  //roll rotation on the x axis  between -180 and 180
                }
            }
        };
        myManager.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    void unregisterAccelerometer() {
        if (myManager != null && mySensorListener != null) {
            myManager.unregisterListener(mySensorListener);
        }
        myManager = null;
        mySensorListener = null;
    }
}