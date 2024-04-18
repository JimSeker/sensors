package edu.cs4730.pitchroll3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import edu.cs4730.pitchroll3.databinding.ActivityMainBinding;


/**
 * http://stackoverflow.com/questions/9454948/android-pitch-and-roll-issue
 * modified to remove the Orientation sensor that google dec in api 9.
 */

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private SensorManager mgr;
    private Sensor accel;
    private Sensor compass;

    private ActivityMainBinding binding;
    private boolean ready = false;
    private float[] accelValues = new float[3];
    private float[] compassValues = new float[3];
    private float[] inR = new float[9];
    private float[] inclineMatrix = new float[9];
    private float[] prefValues = new float[3];
    private float mAzimuth;
    private double mPitch;
    private double mInclination;
    private int counter;
    private int mRotation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        accel = mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        compass = mgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mRotation = rotationInfo();
    }

    public int rotationInfo() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return getDisplay().getRotation();
        } else {

            return getWindowManager().getDefaultDisplay().getRotation();
        }
    }

    @Override
    protected void onResume() {
        mgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
        mgr.registerListener(this, compass, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mgr.unregisterListener(this, accel);
        mgr.unregisterListener(this, compass);
        super.onPause();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    public void onSensorChanged(SensorEvent event) {
        // Need to get both accelerometer and compass
        // before we can determine our orientationValues
        // Log.wtf(TAG, "onSensorChanged.");
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < 3; i++) {
                    accelValues[i] = event.values[i];
                }
                if (compassValues[0] != 0)
                    ready = true;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < 3; i++) {
                    compassValues[i] = event.values[i];
                }
                if (accelValues[2] != 0)
                    ready = true;
                break;
        }

        if (!ready) {
            binding.preferred.setText("Not enough data yet.");
            return;
        }
        if (SensorManager.getRotationMatrix(
            inR, inclineMatrix, accelValues, compassValues)) {
            // got a good rotation matrix
            SensorManager.getOrientation(inR, prefValues);
            mInclination = SensorManager.getInclination(inclineMatrix);
            // Display every 10th value
            //if(counter++ % 10 == 0) {
            doUpdate();
//				counter = 1;
            //}

        }
    }

    public void doUpdate() {
        if (!ready) {
            binding.preferred.setText("Not enough data yet2.");
            return;
        }
        // Log.wtf(TAG, "doUpdate.");
        mAzimuth = (float) Math.toDegrees(prefValues[0]);
        if (mAzimuth < 0) {
            mAzimuth += 360.0f;
        }
        //mPitch = 180.0  + Math.toDegrees(prefValues[1]); //so it goes from 0 to 360, instead of -180 to 180
        mPitch = Math.toDegrees(prefValues[1]);
        String msg = String.format(Locale.getDefault(),
            "Preferred:\nazimuth (Z): %7.3f \npitch (X): %7.3f\nroll (Y): %7.3f",
            mAzimuth, //heading
            mPitch,
            Math.toDegrees(prefValues[2]));
        binding.preferred.setText(msg);
    }

}
